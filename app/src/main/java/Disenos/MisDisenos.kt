package Disenos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.databinding.ActivityMisDisenosBinding
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MisDisenos : AppCompatActivity() {

    lateinit var binding: ActivityMisDisenosBinding
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_disenos)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mis_disenos)
        auth = FirebaseAuth.getInstance()
        mostrarImagenesUsuario()

    }
    private fun mostrarImagenesUsuario() {
        val usuarioActual = auth.currentUser

        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            db.collection("UsuariosTFG")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {

                        val camisetas = document.get("camisetas") as? List<String>

                        if (camisetas != null && camisetas.isNotEmpty()) {

                            binding.cantidad.text = "Números de diseños: ${camisetas.size}"

                            val bitmaps = mutableListOf<Bitmap>()
                            val storageReference = FirebaseStorage.getInstance().reference

                            camisetas.forEach { imageUrl ->

                                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                                storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->

                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    bitmaps.add(bitmap)

                                    if (bitmaps.size == camisetas.size) {
                                        val adapter = ImageListAdapter(this, bitmaps)

                                        binding.gridView.adapter = adapter
                                    }
                                }.addOnFailureListener { e ->

                                    Toast.makeText(this, "Error al descargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {

                            Toast.makeText(this, "No hay diseños que mostrar", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        Toast.makeText(this, "No se encontraron datos de usuario en Firestore", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener datos de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show()
        }
    }

}