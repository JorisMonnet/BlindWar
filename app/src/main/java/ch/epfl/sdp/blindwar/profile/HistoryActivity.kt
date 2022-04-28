package ch.epfl.sdp.blindwar.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.menu.PlayMenuFragment
import ch.epfl.sdp.blindwar.menu.SearchFragment
import ch.epfl.sdp.blindwar.profile.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {
    //private val database = UserDatabase

    /**
     * Generates the layout and sets up bottom navigation
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        showFragment(PlayMenuFragment())

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener {
            when(it.itemId){
                R.id.item_liked_musics-> showFragment(PlayMenuFragment())
                R.id.item_match_history -> showFragment(SearchFragment())
            }
            true
        }
    }

    /**
     * Shows the selected fragment
     *
     * @param fragment to show
     */
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_menu_container, fragment)
            commit()
        }
    }

    /**
     * Minimizes the app if the back button is pressed
     *
     */
    override fun onBackPressed() {
        this.moveTaskToBack(true);
    }
}