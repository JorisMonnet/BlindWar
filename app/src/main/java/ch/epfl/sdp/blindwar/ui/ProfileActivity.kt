package ch.epfl.sdp.blindwar.ui
/*
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.database.ImageDatabase
import ch.epfl.sdp.blindwar.database.UserDatabase
import ch.epfl.sdp.blindwar.profile.fragments.DisplayHistoryActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ProfileActivity : AppCompatActivity() {
    private val database = UserDatabase
    private val imageDatabase = ImageDatabase

    private val userInfoListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get User info and use the values to update the UI
            val user: User? = try {
                dataSnapshot.getValue<User>()
            } catch (e: DatabaseException) {
                null
            }
            val nameView = findViewById<TextView>(R.id.nameView)
            val emailView = findViewById<TextView>(R.id.emailView)
            val eloView = findViewById<TextView>(R.id.eloDeclarationView)
            val profileImageView = findViewById<ImageView>(R.id.profileImageView)
            user?.let{
                nameView.text = it.firstName
                emailView.text = it.email
                eloView.text = it.userStatistics.elo.toString()
                if (it.profilePicture != "") {
                    imageDatabase.dowloadProfilePicture(
                        it.profilePicture,
                        profileImageView,
                        applicationContext
                    )
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // user id should be set according to authentication
        FirebaseAuth.getInstance().currentUser?.let{
            database.addUserListener(it.uid, userInfoListener)
        }
        setContentView(R.layout.activity_profile)

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun editProfile(view: View) {
        startActivity(Intent(this, UserNewInfoActivity::class.java))
    }

    fun logoutButton(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, SplashScreenActivity::class.java))
//        AuthUI.getInstance().signOut(this).addOnCompleteListener {
//            startActivity(Intent(this, SplashScreenActivity::class.java))
//        }
    }

    fun deleteProfile(view: View) {
        // Alert Dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            // new Alert Dialogue to ensure deletion
            val builderSecond: AlertDialog.Builder = AlertDialog.Builder(this)
            val secondPositiveButtonClick = { _: DialogInterface, _: Int ->

                FirebaseAuth.getInstance().currentUser?.let{UserDatabase.removeUser(it.uid)}
                AuthUI.getInstance().delete(this).addOnCompleteListener {
                    startActivity(Intent(this, SplashScreenActivity::class.java))
                }

                Toast.makeText(
                    this,
                    getString(R.string.deletion_success), Toast.LENGTH_SHORT
                ).show()
            }
            val secondNegativeButtonClick = { _: DialogInterface, _: Int ->
                Toast.makeText(
                    this,
                    getString(R.string.account_not_deleted_confirm_toast), Toast.LENGTH_SHORT
                ).show()
            }

            builderSecond.setTitle(getString(R.string.account_deletion_confirm_title))
                .setMessage(getString(R.string.account_deletion_confirm_text))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, secondPositiveButtonClick)
                .setNegativeButton(android.R.string.cancel, secondNegativeButtonClick)
            builderSecond.create().show()

        }
        val negativeButtonClick = { _: DialogInterface, _: Int ->
            Toast.makeText(
                this,
                getString(R.string.account_not_deleted_toast), Toast.LENGTH_SHORT
            ).show()
        }

        builder.setTitle(getString(R.string.account_deletion_title))
            .setMessage(getString(R.string.account_deletion_text))
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, positiveButtonClick)
            .setNegativeButton(android.R.string.cancel, negativeButtonClick)
        builder.create().show()
    }


    fun statisticsButton(view: View) {
        startActivity(Intent(this, StatisticsActivity::class.java))
    }

    fun historyButton(view: View) {
        startActivity(Intent(this, DisplayHistoryActivity::class.java))
    }
}*/