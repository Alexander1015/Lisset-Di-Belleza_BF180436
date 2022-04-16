package sv.edu.udb.lissetdibellezza_bf180436

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

private lateinit var drawer: DrawerLayout
private lateinit var toggle: ActionBarDrawerToggle

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this,drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when (item.itemId) {
            R.id.nav_treatment -> replacefragment(TreatmentFragment(), item.title.toString())
            R.id.nav_record -> replacefragment(RecordFragment(), item.title.toString())
            R.id.nav_logout -> {
                LoginActivity.m_client = ""
                finish()
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replacefragment(fragment: Fragment, title: String) {
        val frangmentManager = supportFragmentManager
        val frangmentTransaction = frangmentManager.beginTransaction()
        frangmentTransaction.replace(R.id.frameLayout,fragment)
        frangmentTransaction.commit()
        drawer.closeDrawers()
        setTitle(title)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}