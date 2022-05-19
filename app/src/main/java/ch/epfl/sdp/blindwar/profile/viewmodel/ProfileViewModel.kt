package ch.epfl.sdp.blindwar.profile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.epfl.sdp.blindwar.data.music.metadata.URIMusicMetadata
import ch.epfl.sdp.blindwar.database.ImageDatabase
import ch.epfl.sdp.blindwar.database.UserDatabase
import ch.epfl.sdp.blindwar.game.model.GameResult
import ch.epfl.sdp.blindwar.profile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class ProfileViewModel : ViewModel() {
    // DATABASE
    private val database = UserDatabase
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    //private val userRepository = UserRepository
    private val imageDatabase = ImageDatabase

    // OBSERVABLES
    val imageRef = MutableLiveData<StorageReference?>()
    val user = MutableLiveData<User>()

    private val userInfoListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get User info and use the values to update the UI
            val userDB: User? = try {
                dataSnapshot.getValue<User>()
            } catch (e: DatabaseException) {
                null
            }

            userDB?.let {
                if (it.profilePicture.isNotEmpty()) {
                    imageRef.postValue(imageDatabase.getImageReference(it.profilePicture))
                } else {
                    imageRef.postValue(null)
                }
                user.postValue(it)

            }?: run {
                user.postValue(User()) // TODO : Wrong -> if not found should watch local data, not an empty user
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    init {
        FirebaseAuth.getInstance().currentUser?.let {
            UserDatabase.addUserListener(it.uid, userInfoListener)
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }

    fun updateStats(score: Int, fails: Int, gameResult: GameResult) {
        FirebaseAuth.getInstance().currentUser?.let {
            UserDatabase.updateSoloUserStatistics(it.uid, score, fails)
            UserDatabase.addGameResult(it.uid, gameResult)
        }
    }

    fun likeMusic(music: URIMusicMetadata) {
        FirebaseAuth.getInstance().currentUser?.let {
            UserDatabase.addLikedMusic(it.uid, music)
        }
    }
}