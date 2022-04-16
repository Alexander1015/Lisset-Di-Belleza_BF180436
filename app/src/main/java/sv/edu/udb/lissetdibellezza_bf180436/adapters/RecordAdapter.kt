package sv.edu.udb.lissetdibellezza_bf180436.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sv.edu.udb.lissetdibellezza_bf180436.R
import sv.edu.udb.lissetdibellezza_bf180436.models.Record

class RecordAdapter(private val recordList : ArrayList<Record>) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_record, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = recordList[position]

        holder.name.text = currentitem.name
        holder.treatment.text = currentitem.treatment
        holder.date.text = currentitem.date
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.rc_name)
        val treatment : TextView = itemView.findViewById(R.id.rc_treatment)
        val date : TextView = itemView.findViewById(R.id.rc_date)
    }
}