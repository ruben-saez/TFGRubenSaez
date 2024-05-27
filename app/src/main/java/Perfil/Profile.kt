package Perfil

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        val correo = intent.getStringExtra("correo")

        binding.textView2.text = correo
        cargarFotoPerfilGoogle()


        if (correo != null) {
            mostrarBeneficioUsuario(correo)
        }

    }
    private fun cargarFotoPerfilGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {

            val photoUrl = account.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .into(binding.avatarUsu)
            }
        }
    }
    private fun mostrarBeneficioUsuario(correo: String) {
        val usuariosRef = db.collection("UsuariosTFG")
        usuariosRef
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val beneficio = document.getDouble("beneficio") ?: 0.0
                    val beneficio10Porciento = beneficio * 0.10
                    val beneficioFormateado = String.format("%.2f", beneficio10Porciento)
                    binding.beneficio.text = "Beneficio: $beneficioFormateado$"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el beneficio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

