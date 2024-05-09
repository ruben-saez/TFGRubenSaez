package ComprarVenta

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.databinding.ActivityCompraBinding
import kotlin.math.roundToInt

class Compra : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedColor: String
    private lateinit var selectedMaterial: String
    private lateinit var selectedTalla: String
    private lateinit var selectedImpresion: String

    lateinit var binding: ActivityCompraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_compra)

        val colors = arrayOf("Blanco", "Negro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = adapter

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
              //Poner algo por si no se selecciona
            }

        }
        val materials = arrayOf("Algodón", "Poliéster", "Lino","Seda") // Agrega los materiales que desees mostrar
        val materialAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMaterial.adapter = materialAdapter
        binding.spinnerMaterial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMaterial = parent.getItemAtPosition(position).toString()

                var precioTotal = 17.99

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

        binding.spinnerTallas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTalla = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val impresion = arrayOf( "Serigrafía", "Vinilo de transferencia térmica", "Impresión digital directa (DTG)", "Sublimación", "Transferencia de pantalla", "Bordado")
        val impresionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, impresion)
        impresionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImpresion.adapter = impresionAdapter
        binding.spinnerImpresion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedImpresion = parent.getItemAtPosition(position).toString()

                var precioTotal = 17.99

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
            val correo = intent.getStringExtra("correo") ?: ""
            val contenidoCorreo = "Detalles de la compra:\n" +
                    "Color: $selectedColor\n" +
                    "Material: $selectedMaterial\n" +
                    "Talla: $selectedTalla\n" +
                    "Impresión: $selectedImpresion"

            val intentCorreo = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$correo")
                putExtra(Intent.EXTRA_SUBJECT, "Detalles de la compra de tu camiseta")
                putExtra(Intent.EXTRA_TEXT, contenidoCorreo)
            }

            if (intentCorreo.resolveActivity(packageManager) != null) {
                startActivity(intentCorreo)
            } else {
                // Manejar caso en que no haya aplicaciones de correo electrónico disponibles
                Toast.makeText(this, "No se encontró una aplicación de correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            val imageUri = data.data
            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                binding.ImagenCompra.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}