package sv.edu.udb.lissetdibellezza_bf180436

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import sv.edu.udb.lissetdibellezza_bf180436.adapters.TreatmentAdapter
import sv.edu.udb.lissetdibellezza_bf180436.models.Treatment

class TreatmentFragment : Fragment(), TreatmentAdapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var treatmentArrayList : ArrayList<Treatment>

    private lateinit var prgbar: ProgressBar
    private lateinit var txtsearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_treatment, container, false)
        prgbar = view.findViewById(R.id.pgrbTreatment)
        txtsearch = view.findViewById(R.id.txtSearchTreatment)

        treatmentRecyclerView = view.findViewById(R.id.items_treatment)
        treatmentRecyclerView.layoutManager = LinearLayoutManager(context)
        treatmentRecyclerView.setHasFixedSize(true)

        treatmentArrayList = arrayListOf<Treatment>()

        getTreatmentData("")

        //Buscamos a partir del EditText
        txtsearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getTreatmentData(txtsearch.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        return view
    }

    private fun getTreatmentData(search : String) {
        prgbar.visibility = View.VISIBLE
        val dbref = if (search.trim() == "") {
            FirebaseDatabase.getInstance().getReference("treatments")
        }
        else {
            FirebaseDatabase.getInstance().getReference("treatments").orderByChild("name").startAt(search).endAt(search + "\uf8ff")
        }
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentArrayList.clear()
                if (snapshot.exists()) {
                    for(treatmentSnapshot in snapshot.children) {
                        val id : String = treatmentSnapshot.key.toString()
                        val name : String = treatmentSnapshot.child("name").value.toString()
                        val description : String = treatmentSnapshot.child("description").value.toString()
                        val price : Double = treatmentSnapshot.child("price").value.toString().toDouble()
                        val img : String = treatmentSnapshot.child("img").value.toString()
                        treatmentArrayList.add(Treatment(id, name, description, price, img))
                    }
                    treatmentRecyclerView.adapter = TreatmentAdapter(treatmentArrayList, this@TreatmentFragment)
                }
                else Toast.makeText(requireContext(), "No existen tratamientos", Toast.LENGTH_SHORT).show()
                prgbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.message)
                prgbar.visibility = View.GONE
            }
        })
    }

    companion object {
        fun newInstance(): TreatmentFragment {
            return newInstance()
        }
    }

    override fun onItemClick(position: Int) {
        val bundle: Bundle = Bundle()
        bundle.putString("treatment_id", treatmentArrayList[position].id)
        bundle.putString("treatment_name", treatmentArrayList[position].name)
        bundle.putString("treatment_description", treatmentArrayList[position].description)
        bundle.putDouble("treatment_price", treatmentArrayList[position].price!!)
        bundle.putString("treatment_img", treatmentArrayList[position].img)
        val detail : Fragment = DetailTreatmentFragment()
        detail.arguments = bundle
        val fr : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fr.replace(R.id.frameLayout, detail).commit()
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