package Disenos
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.tfgrubensaez.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageListAdapter2(private val context: Context, private val bitmaps: List<Bitmap>, private val correos: List<String>?) : BaseAdapter() {

    override fun getCount(): Int {
        return bitmaps.size
    }

    override fun getItem(position: Int): Any {
        return bitmaps[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
        val holder: ViewHolder
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_imagen, parent, false)
            holder = ViewHolder()
            holder.imageView = itemView.findViewById(R.id.imageView)
            holder.correoTextView = itemView.findViewById(R.id.correoTextView)
            holder.precioTextView = itemView.findViewById(R.id.PrecioTextView)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        val bitmap = bitmaps[position]
        val correos = correos?.getOrNull(position)




        holder.imageView.setImageBitmap(bitmap)
        val correoSubstring = correos?.substring(0, minOf(correos.length, 7))
        holder.correoTextView.text = correoSubstring ?: "Nombre no disponible"
        holder.precioTextView.text = "18,99$"
        itemView?.setOnClickListener {

            val bitmap = bitmaps[position]

            val file = File(context.cacheDir, "imagen_temporal.jpg")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

            val intent = Intent(context, ComprarVenta.Compra::class.java)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            intent.putExtra("imagen_bitmap", byteArray)

            intent.putExtra("correo_imagen", correos)

            context.startActivity(intent)
        }

        return itemView!!
    }

    internal class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var correoTextView: TextView
        lateinit var precioTextView: TextView
    }
}
