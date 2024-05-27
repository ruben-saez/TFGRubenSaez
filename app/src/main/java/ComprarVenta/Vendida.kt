package ComprarVenta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.tfgrubensaez.R
import com.example.tfgrubensaez.registro.Registrarse

class Vendida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendida)

        val volverButton = findViewById<Button>(R.id.volver)
        val correo = intent.getStringExtra("correo")
        // Agrega un OnClickListener al botón "VOLVER"
        volverButton.setOnClickListener {
            // Crea un Intent para iniciar la actividad "Registrarse"
            val intent = Intent(this, Registrarse::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }
    }
}