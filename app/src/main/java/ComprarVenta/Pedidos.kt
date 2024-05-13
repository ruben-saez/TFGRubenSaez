package ComprarVenta

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tfgrubensaez.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.GridView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.tfgrubensaez.databinding.ActivityMisDisenosBinding
import com.google.firebase.auth.FirebaseAuth

class Pedidos : AppCompatActivity() {
    lateinit var binding: ActivityMisDisenosBinding
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mis_disenos)
        auth = FirebaseAuth.getInstance()
        val correo = auth.currentUser?.email

        if (correo != null) {
            cargarPedidosUsuario(correo)
        }

    }
    private fun cargarPedidosUsuario(correo: String) {
        db.collection("Pedidos")
            .whereEqualTo("Correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val bitmaps = mutableListOf<Bitmap>()
                    val contenidos = mutableListOf<String>()
                    documents.forEach { document ->
                        val imageUrl = document.getString("Imagen")
                        val contenido = document.getString("Contenido")
                        if (imageUrl != null && contenido != null) {
                            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                bitmaps.add(bitmap)
                                contenidos.add(contenido) // Agrega el contenido a la lista

                                // Si ya has obtenido todos los datos, configura el adaptador
                                if (bitmaps.size == documents.size()) {
                                    val adapter = ImageListAdapterPedido(this, bitmaps, contenidos)
                                    binding.gridView.adapter = adapter
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Error al descargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontraron pedidos para este usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener pedidos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


