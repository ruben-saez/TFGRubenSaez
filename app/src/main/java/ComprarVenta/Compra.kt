package ComprarVenta

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.databinding.ActivityCompraBinding
import com.example.tfgrubensaez.registro.Registrarse
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class Compra : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedColor: String
    private lateinit var selectedMaterial: String
    private lateinit var selectedTalla: String
    private lateinit var selectedImpresion: String

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityCompraBinding
    lateinit var imagenSeleccionadaBitmap: Bitmap
    private lateinit var imagenSeleccionada: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_compra)

        val colors = arrayOf("Blanco", "Negro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = adapter

// Establecer color blanco para el texto del spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colors) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)
                textView.setTypeface(null, Typeface.BOLD)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                return view
            }
        }

// Manejar la selección del spinner
        binding.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedColor = parent.getItemAtPosition(position).toString()
                when (selectedColor) {
                    "Blanco" -> {
                        val drawable: Drawable? = ContextCompat.getDrawable(this@Compra, R.drawable.blanca)
                        binding.camiseta.setImageDrawable(drawable)
                    }
                    "Negro" -> {
                        val drawable: Drawable? = ContextCompat.getDrawable(this@Compra, R.drawable.negra)
                        binding.camiseta.setImageDrawable(drawable)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aquí no necesitas poner nada, ya que no necesitas manejar el caso en que no se seleccione nada
            }
        }

        val materials = arrayOf("Algodón", "Poliéster", "Lino", "Seda")
        val materialAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMaterial.adapter = materialAdapter

        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMaterial.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, materials) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)
                textView.setTypeface(null, Typeface.BOLD)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                return view
            }
        }
        binding.spinnerMaterial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMaterial = parent.getItemAtPosition(position).toString()

                var precioTotal = 18.99

                // Ajustar el precio según el material seleccionado
                when (selectedMaterial) {

                    "Seda" -> {
                        precioTotal += 5.0
                    }
                    "Lino" -> {
                        precioTotal += 2.0
                    }
                }


                val selectedImpresion = binding.spinnerImpresion.selectedItem.toString()
                when (selectedImpresion) {
                    "Serigrafía" -> {
                        precioTotal += 0
                    }
                    "Impresión digital directa (DTG)" -> {
                        precioTotal += 1.0
                    }
                    "Vinilo de transferencia térmica" -> {
                        precioTotal += 0
                    }
                    "Sublimación" -> {
                        precioTotal += 4.0
                    }
                    "Transferencia de pantalla" -> {
                        precioTotal += 3.0
                    }
                    "Bordado" -> {
                        precioTotal += 9.99
                    }
                }
                precioTotal = (precioTotal * 100).roundToInt() / 100.0

                binding.precioCamiseta.text = "$precioTotal$"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val byteArray = intent.getByteArrayExtra("imagen_bitmap")
        if (byteArray != null) {
            val ImagenCompraa = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            binding.ImagenCompra.setImageBitmap(ImagenCompraa)
        } else {
            // Manejar el caso donde el extra del Intent es nulo

        }
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.negra)
        binding.camiseta.setImageDrawable(drawable)

        binding.ImagenCompra.bringToFront()
        binding.ImportarIma.setOnClickListener {
            openGallery()
        }
        val tallas = arrayOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")

        val tallasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tallas)
        tallasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTallas.adapter = tallasAdapter

// Establecer color blanco para el texto del spinner
        tallasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTallas.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tallas) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)
                textView.setTypeface(null, Typeface.BOLD)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                return view
            }
        }
        binding.spinnerTallas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTalla = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aquí no necesitas poner nada, ya que no necesitas manejar el caso en que no se seleccione nada
            }
        }
        val impresion = arrayOf("Serigrafía", "Vinilo de transferencia térmica", "Impresión digital directa (DTG)", "Sublimación", "Transferencia de pantalla", "Bordado")
        val impresionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, impresion)
        impresionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImpresion.adapter = impresionAdapter

// Establecer color blanco para el texto del spinner
        impresionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImpresion.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, impresion) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)
                textView.setTypeface(null, Typeface.BOLD)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                return view
            }
        }
        binding.spinnerImpresion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedImpresion = parent.getItemAtPosition(position).toString()

                var precioTotal = 18.99

                // Obtener el precio adicional del material seleccionado
                val selectedMaterial = binding.spinnerMaterial.selectedItem.toString()
                when (selectedMaterial) {
                    "Seda" -> {
                        precioTotal += 4.99
                    }
                    "Lino" -> {
                        precioTotal += 1.99
                    }
                }

                // Ajustar el precio según el tipo de impresión seleccionado
                when (selectedImpresion) {
                    "Serigrafía" -> {
                        precioTotal += 0
                    }
                    "Impresión digital directa (DTG)" -> {
                        precioTotal += 1.0
                    }
                    "Vinilo de transferencia térmica" -> {
                        precioTotal += 0
                    }
                    "Sublimación" -> {
                        precioTotal += 4.0
                    }
                    "Transferencia de pantalla" -> {
                        precioTotal += 3.0
                    }
                    "Bordado" -> {
                        precioTotal += 9.99
                    }
                }
                precioTotal = (precioTotal * 100).roundToInt() / 100.0

                binding.precioCamiseta.text = "$precioTotal$"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso de no selección
            }
        }


        binding.compra.setOnClickListener {
            val correoVendedor  = intent.getStringExtra("correo_imagen")

            val auth = FirebaseAuth.getInstance()
            val usuarioActual = auth.currentUser
            if (usuarioActual != null) {
                val correo = usuarioActual.email // Obtener el correo electrónico del usuario
                val precioTotal = binding.precioCamiseta.text.toString() // Obtener el precio total del TextView
                val contenidoCorreo = "Detalles de la compra:\n" +
                        "Correo: $correo\n" + // Agregar el correo electrónico al correo
                        "Color: $selectedColor\n" +
                        "Material: $selectedMaterial\n" +
                        "Talla: $selectedTalla\n" +
                        "Impresión: $selectedImpresion\n" +
                        "Precio total: $precioTotal"

                guardarImagenCompraEnFirebase(correo!!, contenidoCorreo, precioTotal,correoVendedor)
                if (correoVendedor != null) {
                    actualizarBeneficioUsuario(correoVendedor, precioTotal)
                }
                val intent = Intent(this@Compra, Vendida::class.java)
                intent.putExtra("correo", correo)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        }







    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("ActivityResult", "requestCode: $requestCode, resultCode: $resultCode")

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                binding.ImagenCompra.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Mostrar el mensaje de error solo si no se seleccionó ninguna imagen
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }



    private fun guardarImagenCompraEnFirebase(correo: String, contenidoCorreo: String, precioTotal: String, correoVendedor: String?) {
        val auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser

        if (usuarioActual != null) {
            val userId = usuarioActual.uid

            val imagenBitmap = (binding.ImagenCompra.drawable as BitmapDrawable).bitmap

            val bytesImagen = bitmapToByteArray(imagenBitmap)

            val storageReference = FirebaseStorage.getInstance().reference

            val imageRef = storageReference.child("images/$userId/${System.currentTimeMillis()}.jpg")

            val uploadTask = imageRef.putBytes(bytesImagen)
            uploadTask.addOnSuccessListener { taskSnapshot ->

                // Obtener la URL de la imagen en Firebase Storage
                imageRef.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()

                    // Actualizar Firestore con la URL de la imagen y otros datos
                    val datosPedido = hashMapOf(
                        "Correo" to correo,
                        "Contenido" to contenidoCorreo,
                        "Imagen" to imageUrl,
                        "Precio" to precioTotal,
                        "CorreoVendedor" to correoVendedor
                    )

                    db.collection("Pedidos")
                        .add(datosPedido)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Pedido realizado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al realizar el pedido: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                }
            }
        }
    }


    private fun actualizarBeneficioUsuario(correoVendedor: String, precioTotal: String) {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("UsuariosTFG")

        usuariosRef
            .whereEqualTo("correo", correoVendedor) // Buscar al usuario con el correo del vendedor
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val beneficioActual = document.getDouble("beneficio") ?: 0.0
                    val precioTotalSinDolar = precioTotal.replace("$", "")
                    val nuevoBeneficio = beneficioActual + precioTotalSinDolar.toDouble()


                    // Actualizar el campo "beneficio" del documento con el nuevo valor
                    document.reference.update("beneficio", nuevoBeneficio)
                        .addOnSuccessListener {
                            }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al actualizar el beneficio para el usuario con correo: $correoVendedor", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el documento del usuario con correo: $correoVendedor", Toast.LENGTH_SHORT).show()
            }
    }




    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}