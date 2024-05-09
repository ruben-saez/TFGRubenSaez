package Disenos

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.tfgrubensaez.R

class ImageListAdapter(context: Context, private val images: List<Bitmap>) : ArrayAdapter<Bitmap>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_imagen2, parent, false)
        }

        val imageView: ImageView = itemView!!.findViewById(R.id.imageView)
        imageView.setImageBitmap(images[position])

        return itemView
    }
}
