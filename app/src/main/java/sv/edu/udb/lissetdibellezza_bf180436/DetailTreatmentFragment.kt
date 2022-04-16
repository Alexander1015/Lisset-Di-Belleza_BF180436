package sv.edu.udb.lissetdibellezza_bf180436

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import sv.edu.udb.lissetdibellezza_bf180436.models.Schedule

class DetailTreatmentFragment : Fragment(), View.OnClickListener {
    private var treatment_id: String? = ""
    private var treatment_name: String? = ""
    private var treatment_description: String? = ""
    private var treatment_price: Double? = 0.00
    private var treatment_img: String? = ""

    private lateinit var prgbar: ProgressBar
    private lateinit var imgView: ImageView
    private lateinit var txtname: TextView
    private lateinit var txtprice: TextView
    private lateinit var txtdescription: TextView

    private lateinit var btnback: Button
    private lateinit var btnnew: Button

    private lateinit var scheduleArrayList : ArrayList<Schedule>
    private lateinit var autocompleteTxt: AutoCompleteTextView
    private lateinit var adapterSchedule : ArrayAdapter<String>
    private lateinit var items : ArrayList<String>

    private var select : Int = -1
    private var last : Int = -2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            treatment_id = it.getString("treatment_id")
            treatment_name = it.getString("treatment_name")
            treatment_description = it.getString("treatment_description")
            treatment_price = it.getDouble("treatment_price")
            treatment_img = it.getString("treatment_img")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_detail_treatment, container, false)
        prgbar = view.findViewById(R.id.pgrbDetail)

        btnback = view.findViewById(R.id.btnBack)
        btnnew = view.findViewById(R.id.btnNew)

        txtname = view.findViewById(R.id.dt_name)
        txtprice = view.findViewById(R.id.dt_price)
        txtdescription = view.findViewById(R.id.dt_description)
        imgView = view.findViewById(R.id.dt_img)
        prgbar.visibility = View.VISIBLE

        scheduleArrayList = arrayListOf<Schedule>()
        items = arrayListOf<String>()
        autocompleteTxt = view.findViewById(R.id.auto_complete_txt)

        if (treatment_id != "" || treatment_id != null) {
            txtname.text = treatment_name
            txtprice.text = "$ " + treatment_price.toString()
            txtdescription.text = treatment_description
            Picasso.get()
                .load(treatment_img) //Cargamos recurso
                .error(R.drawable.logo_large) //Si da error mostramos otra imagen
                .into(imgView) //En donde mostraremos la img

            treatment_id?.let { getScheduleData(it) }
        }
        btnback.setOnClickListener(this)
        btnnew.setOnClickListener(this)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(treatment_id: String) =
            DetailTreatmentFragment().apply {
                arguments = Bundle().apply {
                    putString("treatment_id", treatment_id)
                    putString("treatment_name", treatment_name)
                    putString("treatment_description", treatment_description)
                    putDouble("treatment_price", treatment_price!!)
                    putString("treatment_img", treatment_img)
                }
            }
    }

    private fun getScheduleData(id: String) {
        val dbref = FirebaseDatabase.getInstance().getReference("schedule")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scheduleArrayList.clear()
                items.clear()
                if (snapshot.exists()) {
                    for(scheduleSnapshot in snapshot.children) {
                        val id_treatment = scheduleSnapshot.child("treatment").value.toString()
                        if (id_treatment == id) {
                            val id_schedule : String = scheduleSnapshot.key.toString()
                            val day : String = scheduleSnapshot.child("day").value.toString()
                            //Damos formato al dia
                            var name_day : String = ""
                            when {
                                day.trim() == "1" -> name_day = "Lunes"
                                day.trim() == "2" -> name_day = "Martes"
                                day.trim() == "3" -> name_day = "Miercoles"
                                day.trim() == "4" -> name_day = "Jueves"
                                day.trim() == "5" -> name_day = "Viernes"
                                day.trim() == "6" -> name_day = "Sabado"
                                day.trim() == "7" -> name_day = "Domingo"
                            }
                            val hour : String = scheduleSnapshot.child("hour").value.toString()
                            scheduleArrayList.add(Schedule(id_schedule, day, hour, treatment_id))
                            items.add("$name_day a las $hour")
                            Log.d("Item", "$name_day a las $hour")
                        }
                    }
                    adapterSchedule = ArrayAdapter(requireContext(), R.layout.list_detail, items)
                    autocompleteTxt.setAdapter(adapterSchedule)
                    prgbar.visibility = View.GONE


                    autocompleteTxt.setOnItemClickListener { parent, view, position, id ->
                        val item : String = parent.getItemAtPosition(position).toString()
                        select = position

                        val dbrefrec = FirebaseDatabase.getInstance().getReference("record")
                        dbrefrec.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (recordnapshot in snapshot.children) {
                                        last = recordnapshot.key!!.toInt()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("Error", error.message)
                                prgbar.visibility = View.GONE
                            }
                        })
                        //Toast.makeText(requireContext(), "Cita ($item) seleccionada, pero no creada", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    prgbar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.message)
                prgbar.visibility = View.GONE
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBack -> {
                try {
                    val fr : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fr.replace(R.id.frameLayout, TreatmentFragment()).commit()
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }
            R.id.btnNew -> {
                try {
                    prgbar.visibility = View.VISIBLE
                    if (select >= 0) {
                        if (last == null) last = -2
                        last++
                        if (last >= 0) {
                            val name : String = LoginActivity.m_client
                            val schedule : String = scheduleArrayList[select].id.toString()
                            val dbrefsche = FirebaseDatabase.getInstance().reference
                            dbrefsche.child("record/$last/name").setValue(name)
                            dbrefsche.child("record/$last/schedule").setValue(schedule)

                            prgbar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Cita creada con exito", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            prgbar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Fallo al generar la cita", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        prgbar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Fallo al generar la cita", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception) {
                    Log.d("Error", e.toString())
                    prgbar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Fallo al generar la cita", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }
}