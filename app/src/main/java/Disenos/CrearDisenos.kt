package Disenos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tfgrubensaez.R
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Environment
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.tfgrubensaez.databinding.ActivityCrearDisenosBinding
import com.example.tfgrubensaez.registro.Registrarse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CrearDisenos : AppCompatActivity() {

    lateinit var binding: ActivityCrearDisenosBinding
    private lateinit var customView: CustomView
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val INCREMENTO_GROSOR = 10f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_disenos)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_crear_disenos)

        customView = binding.customView

        binding.guardarBtn.setOnClickListener {
            guardarDibujo()
        }
        val backgroundColorSpinner = binding.backgroundColorSpinner
        val backgroundColors = listOf(
            "Blanco",
            "Gris",
            "Azul",
            "Verde",
            "Amarillo"
        )
        val backgroundColorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, backgroundColors)
        backgroundColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        backgroundColorSpinner.adapter = backgroundColorAdapter
        backgroundColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedColor = when (backgroundColors[position]) {
                    "Blanco" -> Color.WHITE
                    "Gris" -> Color.GRAY
                    "Azul" -> Color.BLUE
                    "Verde" -> Color.GREEN
                    "Amarillo" -> Color.YELLOW
                    else -> Color.WHITE
                }
                customView.setBackgroundColor(selectedColor)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.grosorBtn.setOnClickListener {

            customView.incrementStrokeWidth(INCREMENTO_GROSOR)
        }
        binding.grosorBtn2.setOnClickListener {

            customView.decrementStrokeWidth(INCREMENTO_GROSOR)
        }
        binding.lineaBtn.setOnClickListener {
            customView.setShapeType(CustomView.ShapeType.LINE)
        }
        binding.rectanguloBtn.setOnClickListener {
            customView.setShapeType(CustomView.ShapeType.RECTANGLE)
        }
        binding.circuloBtn.setOnClickListener {
            customView.setShapeType(CustomView.ShapeType.CIRCLE)
        }
        val paintColorSpinner = binding.paintColorSpinner
        val paintColors = listOf(
            "Negro",
            "Rojo",
            "Verde",
            "Azul",
            "Amarillo"
        )
        val paintColorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paintColors)
        paintColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paintColorSpinner.adapter = paintColorAdapter
        paintColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedColor = when (paintColors[position]) {
                    "Negro" -> Color.BLACK
                    "Rojo" -> Color.RED
                    "Verde" -> Color.GREEN
                    "Azul" -> Color.BLUE
                    "Amarillo" -> Color.YELLOW
                    else -> Color.BLACK
                }
                customView.setPaintColor(selectedColor)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.borrarBtn.setOnClickListener {
            customView.clearCanvas()
        }

    }
    private fun guardarDibujo() {
        val correo = intent.getStringExtra("correo")
        val auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser

        if (usuarioActual != null && correo != null) {

            val userId = usuarioActual.uid

            val bytesImagen = bitmapToByteArray(customView.getBitmap())

            val storageReference = FirebaseStorage.getInstance().reference

            val imageRef = storageReference.child("images/$userId/${System.currentTimeMillis()}.jpg")


            val uploadTask = imageRef.putBytes(bytesImagen)
            uploadTask.addOnSuccessListener { taskSnapshot ->

                imageRef.downloadUrl.addOnSuccessListener { uri ->

                    val imageUrl = uri.toString()
                    db.collection("UsuariosTFG")
                        .document(userId)
                        .update("camisetas", FieldValue.arrayUnion(imageUrl))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Imagen guardada correctamente en la base de datos", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CrearDisenos, Registrarse::class.java)
                            intent.putExtra("correo", correo)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar imagen en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir imagen a Firebase Storage: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario actual o el correo", Toast.LENGTH_SHORT).show()
        }
    }



    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
