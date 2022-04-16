package sv.edu.udb.lissetdibellezza_bf180436

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    companion object {
        var m_client = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val txtClient: EditText = findViewById(R.id.txtClientLogin)
        val prgbar: ProgressBar = findViewById(R.id.pgrbLogin)
        btnLogin.setOnClickListener {
            prgbar.visibility = View.VISIBLE
            try {
                if (txtClient.text.toString().trim() != "") {
                    m_client = txtClient.text.toString()
                    Toast.makeText(this, "Hola $m_client", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    prgbar.visibility = View.GONE
                    txtClient.setText("")
                }
                else {
                    prgbar.visibility = View.GONE
                    Toast.makeText(this, "Debe ingresar el nombre de quien utilizara la aplicación", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: Exception) {
                Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
                prgbar.visibility = View.GONE
            }
        }
    }
}