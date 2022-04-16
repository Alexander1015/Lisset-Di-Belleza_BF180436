package sv.edu.udb.lissetdibellezza_bf180436.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import sv.edu.udb.lissetdibellezza_bf180436.R
import sv.edu.udb.lissetdibellezza_bf180436.models.Treatment

class TreatmentAdapter(
    private val treatmentList: ArrayList<Treatment>,
    private val listener: OnItemClickListener
    ) :
    RecyclerView.Adapter<TreatmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_treatment, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = treatmentList[position]

        holder.name.text = currentitem.name
        holder.price.text = "$ " + currentitem.price.toString()
        Picasso.get()
            .load(currentitem.img) //Cargamos recurso
            .error(R.drawable.logo_large) //Si da error mostramos otra imagen
            .into(holder.img) //En donde mostraremos la img
    }

    override fun getItemCount(): Int {
        return treatmentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name : TextView = itemView.findViewById(R.id.tr_name)
        val price : TextView = itemView.findViewById(R.id.tr_price)
        val img : ImageView = itemView.findViewById(R.id.tr_img)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}