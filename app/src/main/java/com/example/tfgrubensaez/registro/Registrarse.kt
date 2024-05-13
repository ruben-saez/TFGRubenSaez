package com.example.tfgrubensaez.registro

import Disenos.ImageListAdapter2
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.databinding.ActivityRegistrarseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.storage.FirebaseStorage

class Registrarse : AppCompatActivity() {
    lateinit var binding: ActivityRegistrarseBinding
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var isMenuVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registrarse)

        val correo = intent.getStringExtra("correo")

        if (correo != null) {
            cargarImagenesUsuario(correo)
            cargarFotoPerfilGoogle()
            verificarCorreoExistenteYAgregarUsuario(correo)
        } else {
            binding.textView2.text = "Correo no disponible"
        }
        binding.avatarUsuario.setOnClickListener(){
            val intent = Intent(this@Registrarse, Perfil.Profile::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
        binding.Crear.setOnClickListener(){
            val intent = Intent(this@Registrarse, Disenos.CrearDisenos::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
        binding.misDisenos.setOnClickListener(){
            val intent = Intent(this@Registrarse, Disenos.MisDisenos::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }

        binding.btnOpcion1.setOnClickListener(){
            val intent = Intent(this@Registrarse,Disenos.MisDisenos ::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
        binding.btnOpcion2.setOnClickListener(){
            val intent = Intent(this@Registrarse,Disenos.CrearDisenos::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
        binding.btnOpcion3.setOnClickListener(){
            val intent = Intent(this@Registrarse, ComprarVenta.Compra::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)

        }
        binding.btnOpcion4.setOnClickListener(){
            val intent = Intent(this@Registrarse, Soporte.SolicitudSoporte::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)

        }
        binding.btnOpcion5.setOnClickListener(){
            val intent = Intent(this@Registrarse, ComprarVenta.Pedidos::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)

        }
        binding.MenuDes.setOnClickListener {
            toggleMenu()
        }
    }

    private fun verificarCorreoExistenteYAgregarUsuario(correo: String) {
        db.collection("UsuariosTFG")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    agregarUsuarioAColeccion(correo)
                } else {
                    Toast.makeText(this, "BIENVENIDO", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar correo en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun toggleMenu() {
        isMenuVisible = !isMenuVisible
        if (isMenuVisible) {
            val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            binding.menuDesplegable.startAnimation(slideAnimation)
            binding.menuDesplegable.visibility = View.VISIBLE
            binding.menuDesplegable.bringToFront()
        } else {
            val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left)
            slideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.menuDesplegable.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding.menuDesplegable.startAnimation(slideAnimation)
        }
    }
    private fun agregarUsuarioAColeccion(correo: String) {

        auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser

        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            val nuevoUsuario = hashMapOf(
                "correo" to correo,
                "camisetas" to arrayListOf<String>()
            )

            db.collection("UsuariosTFG")
                .document(userId)
                .set(nuevoUsuario)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario agregado correctamente a la base de datos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al agregar usuario a la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show()
        }
    }
    private fun cargarFotoPerfilGoogle() {
        // Configuración de Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // Obtener la URL de la imagen de perfil del usuario de Google
            val photoUrl = account.photoUrl
            if (photoUrl != null) {
                  Glide.with(this)
                    .load(photoUrl)
                    .into(binding.avatarUsuario)
            }
        }
    }
    private fun cargarImagenesUsuario(correo: String) {

        db.collection("UsuariosTFG")

            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    val bitmaps = mutableListOf<Bitmap>()
                    val correos = mutableListOf<String>()
                    var totalDescargas = 0

                    documents.forEach { document ->
                        val correo = document.getString("correo")
                        val camisetas = document.get("camisetas") as? List<String>

                        camisetas?.forEach { imageUrl ->

                            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->

                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                bitmaps.add(bitmap)
                                correos.add(correo ?: "")
                                totalDescargas++
                                if (bitmaps.size == totalDescargas) {
                                    val adapter = ImageListAdapter2(this, bitmaps, correos)
                                    binding.gridView.adapter = adapter
                                }
                            }.addOnFailureListener { e ->

                                Toast.makeText(this, "Error al descargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {

                    Toast.makeText(this, "No se encontraron datos de usuarios en Firestore", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Error al obtener datos de usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
