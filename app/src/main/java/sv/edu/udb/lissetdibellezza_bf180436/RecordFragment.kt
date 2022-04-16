package sv.edu.udb.lissetdibellezza_bf180436

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import sv.edu.udb.lissetdibellezza_bf180436.adapters.RecordAdapter
import sv.edu.udb.lissetdibellezza_bf180436.models.Record
import sv.edu.udb.lissetdibellezza_bf180436.models.Schedule
import sv.edu.udb.lissetdibellezza_bf180436.models.Treatment

class RecordFragment : Fragment() {

    private lateinit var recordRecyclerView: RecyclerView
    private lateinit var recordArrayList : ArrayList<Record>
    private lateinit var treatmentArrayList : ArrayList<Treatment>
    private lateinit var scheduleArrayList : ArrayList<Schedule>

    private lateinit var prgbar: ProgressBar
    private lateinit var txtsearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_record, container, false)
        prgbar = view.findViewById(R.id.pgrbRecord)
        txtsearch = view.findViewById(R.id.txtSearchRecord)

        recordRecyclerView = view.findViewById(R.id.items_record)
        recordRecyclerView.layoutManager = LinearLayoutManager(context)
        recordRecyclerView.setHasFixedSize(true)

        recordArrayList = arrayListOf<Record>()
        treatmentArrayList = arrayListOf<Treatment>()
        scheduleArrayList = arrayListOf<Schedule>()

        txtsearch.setText(LoginActivity.m_client)

        getRecordData(LoginActivity.m_client)

        //Buscamos a partir del EditText
        txtsearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getRecordData(txtsearch.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        return view
    }

    private fun getRecordData(search : String) {
        prgbar.visibility = View.VISIBLE

        //Verificamos los tratamientos
        val dbreftreat = FirebaseDatabase.getInstance().getReference("treatments")
        dbreftreat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotrec: DataSnapshot) {
                treatmentArrayList.clear()
                if (snapshotrec.exists()) {
                    for(treatmentSnapshot in snapshotrec.children) {
                        val id : String = treatmentSnapshot.key.toString()
                        val name : String = treatmentSnapshot.child("name").value.toString()
                        val description : String = treatmentSnapshot.child("description").value.toString()
                        val price : Double = treatmentSnapshot.child("price").value.toString().toDouble()
                        val img : String = treatmentSnapshot.child("img").value.toString()
                        treatmentArrayList.add(Treatment(id, name, description, price, img))
                    }

                    //Verificamos los horarios
                    val dbrefsched = FirebaseDatabase.getInstance().getReference("schedule")
                    dbrefsched.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotsched: DataSnapshot) {
                            scheduleArrayList.clear()
                            if (snapshotsched.exists()) {
                                for(scheduleSnapshot in snapshotsched.children) {
                                    val id : String = scheduleSnapshot.key.toString()
                                    val day : String = scheduleSnapshot.child("day").value.toString()

                                    //Damos formato al dia
                                    var name_day : String = ""
                                    if (day.trim().equals("1")) name_day = "Lunes"
                                    else if (day.trim().equals("2")) name_day = "Martes"
                                    else if (day.trim().equals("3")) name_day = "Miercoles"
                                    else if (day.trim().equals("4")) name_day = "Jueves"
                                    else if (day.trim().equals("5")) name_day = "Viernes"
                                    else if (day.trim().equals("6")) name_day = "Sabado"
                                    else if (day.trim().equals("7")) name_day = "Domingo"

                                    val hour : String = scheduleSnapshot.child("hour").value.toString()
                                    val id_treatment : String = scheduleSnapshot.child("treatment").value.toString()
                                    var treatment : String = ""
                                    var i: Int = 0
                                    while (i < treatmentArrayList.size) {
                                        if (treatmentArrayList[i].id.equals(id_treatment)) {
                                            treatment = treatmentArrayList[i].name.toString()
                                        }
                                        i++
                                    }
                                    scheduleArrayList.add(Schedule(id, name_day, hour, treatment))
                                }

                                //Obtenemos el historial
                                val dbrefrec = if (search.trim() == "") {
                                    FirebaseDatabase.getInstance().getReference("record")
                                }
                                else {
                                    FirebaseDatabase.getInstance().getReference("record").orderByChild("name").startAt(search).endAt(search + "\uf8ff")
                                }
                                dbrefrec.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        recordArrayList.clear()
                                        if (snapshot.exists()) {
                                            for(recordSnapshot in snapshot.children) {
                                                val id : String = recordSnapshot.key.toString()
                                                val name : String = recordSnapshot.child("name").value.toString()
                                                val id_schedule : String = recordSnapshot.child("schedule").value.toString()
                                                var treatment : String = ""
                                                var date : String = ""
                                                var i: Int = 0
                                                while (i < scheduleArrayList.size) {
                                                    if (scheduleArrayList[i].id.equals(id_schedule)) {
                                                        treatment = scheduleArrayList[i].treatment.toString()
                                                        date = scheduleArrayList[i].day.toString() + " a las " + scheduleArrayList[i].hour.toString()
                                                    }
                                                    i++
                                                }
                                                recordArrayList.add(Record(id,treatment,name, date))
                                            }
                                            recordRecyclerView.adapter = RecordAdapter(recordArrayList)
                                        }
                                        else Toast.makeText(requireContext(), "No existe historial de este cliente", Toast.LENGTH_SHORT).show()
                                        prgbar.visibility = View.GONE
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d("Error", error.message)
                                        prgbar.visibility = View.GONE
                                    }
                                })
                            }
                            else {
                                prgbar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Fallo al generar el historial", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Error", error.message)
                            prgbar.visibility = View.GONE
                        }
                    })
                }
                else {
                    prgbar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Fallo al generar el historial", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.message)
                prgbar.visibility = View.GONE
            }
        })
    }

    companion object {
        fun newInstance(): RecordFragment {
            return newInstance()
        }
    }

    /*
    fun onClick(view: View?) {
        when (view?.id) {
            // R.id.btn -> { }
            // else -> { }
        }
    }
    */
}