package ch.epfl.sdp.blindwar.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.data.music.metadata.MusicMetadata
import ch.epfl.sdp.blindwar.database.MatchDatabase
import ch.epfl.sdp.blindwar.database.UserDatabase
import ch.epfl.sdp.blindwar.game.model.config.GameFormat
import ch.epfl.sdp.blindwar.game.model.config.GameInstance
import ch.epfl.sdp.blindwar.game.model.config.GameMode
import ch.epfl.sdp.blindwar.game.multi.model.Match
import ch.epfl.sdp.blindwar.game.solo.fragments.GameSummaryFragment
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment.Companion.ARTIST_KEY
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment.Companion.COVER_KEY
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment.Companion.IS_MULTI
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment.Companion.SUCCESS_KEY
import ch.epfl.sdp.blindwar.game.solo.fragments.SongSummaryFragment.Companion.TITLE_KEY
import ch.epfl.sdp.blindwar.game.util.ScoreboardAdapter
import ch.epfl.sdp.blindwar.game.util.VoiceRecognizer
import ch.epfl.sdp.blindwar.game.viewmodels.GameViewModel
import ch.epfl.sdp.blindwar.profile.model.User
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


/**
 * Fragment containing the UI logic of a solo game
 *
 * @constructor creates a GameActivity
 */
class GameActivity : AppCompatActivity() {

    // Game instance and match
    private lateinit var gameViewModel: GameViewModel
    private lateinit var match: Match
    private lateinit var gameInstance: GameInstance

    // Adapter
    private lateinit var scoreboardAdapter: ScoreboardAdapter

    // METADATA
    private lateinit var musicMetadata: MusicMetadata
    private var playing = true

    // INTERFACE
    private lateinit var gameSummary: GameSummaryFragment
    private lateinit var scoreTextView: TextView
    private lateinit var guessButton: ImageButton
    private lateinit var countDown: TextView
    private lateinit var timer: CountDownTimer
    private lateinit var scoreboard: RecyclerView

    // Animations / Buttons
    private lateinit var crossAnim: LottieAnimationView
    private lateinit var startButton: LottieAnimationView
    private lateinit var audioVisualizer: LottieAnimationView
    private lateinit var microphoneButton: ImageButton

    // Round duration
    private var duration: Int = 0

    // INPUT
    private lateinit var voiceRecognizer: VoiceRecognizer
    private var isVocal = false
    private lateinit var guessEditText: EditText

    // MODES
    // Race mode
    private lateinit var chronometer: Chronometer
    private var elapsed: Long = -1000L

    // Survival mode
    private lateinit var heartImage: ImageView
    private lateinit var heartNumber: TextView

    // Multiplayer
    private var matchId: String? = null
    private var playerIndex = -1
    private var playerList: MutableList<String>? = null

    // Database listener
    @SuppressLint("NotifyDataSetChanged")
    private val databaseListener =
        EventListener<DocumentSnapshot> { value, _ ->
            // Set the match object
            this.match = value?.toObject(Match::class.java)!!

            // Set the score board
            scoreboardAdapter.updateScoreboardFromList(match.listResult)
            scoreboardAdapter.notifyDataSetChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animated_demo)

        // if multi mode, get matchId from the user database
        matchId = UserDatabase.getCurrentUser()?.getValue(User::class.java)!!.matchId


        // Get the scoreboard
        scoreboard = findViewById(R.id.scoreboard)

        // Set match and store locally the index of the player and retrieve the list of players
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null && matchId != null) {
            MatchDatabase.getMatchSnapshot(matchId!!, Firebase.firestore)?.let {
                this.match = it.toObject(Match::class.java)!!
                val userList = match?.listPlayers
                playerIndex = userList?.indexOf(currentUser.uid)!!
                playerList = match.listPseudo
            }
        }
        scoreboardAdapter = if (playerList != null) {
            ScoreboardAdapter(playerList!!)
        } else {
            ScoreboardAdapter(listOf(""))
        }
        scoreboard.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        scoreboard.layoutManager = layoutManager
        scoreboard.adapter = scoreboardAdapter
        scoreboardAdapter.notifyDataSetChanged()

        // Set gameInstance
        setGameInstance()

        // Set the gameViewModel
        setGameViewModel()

        gameViewModel.createMusicViewModel(this)

        // Retrieve the game duration from the GameInstance object
        duration = gameInstance.gameConfig?.parameter?.timeToFind!!

        // Create and start countdown
        timer = createCountDown()
        countDown = this.findViewById(R.id.countdown)

        // Mode specific interface
        val mode = gameInstance.gameConfig!!.mode

        chronometer = findViewById(R.id.simpleChronometer)
        heartImage = findViewById(R.id.heartImage)
        heartNumber = findViewById(R.id.heartNumber)

        when (mode) {
            GameMode.TIMED -> initRaceMode()
            GameMode.SURVIVAL -> initSurvivalMode()
            else -> {
            }
        }

        // Get the widgets
        guessEditText = findViewById(R.id.guessEditText)
        scoreTextView = findViewById(R.id.scoreTextView)
        guessButton = findViewById<ImageButton>(R.id.guessButton).also {
            it.setOnClickListener {
                guessEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                guess(isVocal, isAuto = false)
                // Delete the text of the guess
                guessEditText.setText("")
            }
        }

        startButton = findViewById<LottieAnimationView>(R.id.startButton).also {
            it.setOnClickListener {
                playAndPause()
            }
        }

        crossAnim = findViewById(R.id.cross)
        crossAnim.repeatCount = 1

        findViewById<ConstraintLayout>(R.id.fragment_container).setOnClickListener {
            guessEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        voiceRecognizer = VoiceRecognizer()
        audioVisualizer = findViewById(R.id.audioVisualizer)
        startButton.setMinAndMaxFrame(30, 50)

        microphoneButton = findViewById(R.id.microphone)
        this.let { voiceRecognizer.init(it, Locale.ENGLISH.toLanguageTag()) }
        gameSummary = GameSummaryFragment()

        // Listen for fragment result
        this.supportFragmentManager.setFragmentResultListener("SongSummaryExit", this
        ) { _, bundle ->
            fragmentResultListener(bundle)
        }

        // Prepare the voice recognizer
        prepareVoiceRecognizer()

        // Start the game
        startGame()
    }

    private fun setGameViewModel() {
        when (gameInstance.gameFormat) {
            GameFormat.SOLO -> {
                gameViewModel = GameViewModel(
                    gameInstance,
                    resources
                )

                // Hide the scoreboard
                scoreboard.visibility = View.INVISIBLE
            }
            GameFormat.MULTI -> {
                gameViewModel = GameViewModel(
                    gameInstance,
                    resources,
                    scoreboardAdapter
                )
            }
            else -> {
            }
        }

    }

    private fun setGameInstance() {
        // If currently in a match, get the gameSettingsViewModel from the server
        if (matchId != null) {
            MatchDatabase.addListener(matchId!!, Firebase.firestore, databaseListener)
            MatchDatabase.getMatchSnapshot(matchId!!, Firebase.firestore)?.let {
                val match = it.toObject(Match::class.java)
                val gameInstanceShared = match?.game
                this.gameInstance = gameInstanceShared!!
            }
        }
        else {
            // Get the game instance from the arguments
            this.gameInstance = (intent.getSerializableExtra("game_instance") as? GameInstance)!!
        }

    }

    private fun fragmentResultListener(bundle: Bundle) {

        // Does the user liked the music ?
        val liked = bundle.getBoolean("liked")

        // Does the user succeed ?
        val succeed = bundle.getBoolean("succeed")

        // Start the next round
        nextRound(liked, succeed)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun prepareVoiceRecognizer() {
        // TODO Check this == viewLifecycleOwner
        voiceRecognizer.resultString.observe(this) {
            guessEditText.setText(it)
            guess(isVocal, isAuto = false)
            isVocal = false
        }

        //warning seems ok, no need to override performClick
        microphoneButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    gameViewModel.pause()
                    voiceRecognizer.start()
                    isVocal = true
                }

                MotionEvent.ACTION_UP -> {
                    gameViewModel.play()
                    voiceRecognizer.stop()
                    v.performClick()
                }
            }
            true
        }
    }

    /**
     * Start Game
     */
    private fun startGame() {
        scoreboardAdapter.notifyDataSetChanged()

        // Start the game
        gameViewModel.nextRound()
        gameViewModel.play()
        musicMetadata = gameViewModel.currentMetadata()!!
        guessEditText.hint = musicMetadata.author
        timer.start()
    }

    /**
     * Creates the countdown timer
     *
     * @return creates countdown timer with the duration of the round
     */
    private fun createCountDown(): CountDownTimer {
        return object : CountDownTimer(duration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                duration = millisUntilFinished.toInt()
                elapsed += 1000
                //Log.d("DURATION", duration.toString())
                countDown.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                gameViewModel.timeout()
                this.cancel()
                launchSongSummary(success = false)
            }
        }
    }

    /**
     * Set the demo fragment visibility
     *
     * @param code visibility code : either VISIBLE or GONE
     */
    private fun setVisibilityLayout(code: Int) {
        guessButton.visibility = code
        scoreTextView.visibility = code
        guessEditText.visibility = code

        // Set animation visibility
        crossAnim.visibility = code
        countDown.visibility = code
        audioVisualizer.visibility = code
        startButton.visibility = code
        microphoneButton.visibility = code
        findViewById<ImageButton>(R.id.guessButton)?.visibility = code
    }

    /**
     * Handle pause and resume game logic
     */
    private fun playAndPause() {
        playing = if (playing) {
            gameViewModel.pause()
            // Pause Animation
            audioVisualizer.pauseAnimation()
            startButton.setMinAndMaxFrame(30, 55)
            startButton.repeatCount = 0
            startButton.playAnimation()
            // Timer cancel
            timer.cancel()
            timer = createCountDown()
            chronometer.stop()
            false

        } else {
            gameViewModel.play()

            // Resume Animation
            audioVisualizer.resumeAnimation()
            startButton.setMinAndMaxFrame(10, 25)
            startButton.playAnimation()

            restartChronometer()
            timer.start()
            true
        }
    }

    // RACE MODE
    /**
     * Restart chronometer
     *
     */
    private fun restartChronometer() {
        chronometer.base = SystemClock.elapsedRealtime() - elapsed
        chronometer.start()
    }

    /**
     * Start chronometer
     */
    private fun initRaceMode() {
        chronometer.visibility = View.VISIBLE
        chronometer.start()
    }

    // SURVIVAL MODE
    /**
     * Add the number of hearts on screen
     */
    private fun initSurvivalMode() {
        heartImage.visibility = View.VISIBLE
        heartNumber.visibility = View.VISIBLE
        gameViewModel.lives.observe(this) {
            heartNumber.text = getString(R.string.heart_number, it)
        }
    }

    /**
     * Verify the guess of the player
     *
     * @param isVocal true is player used its microphone to guess
     * @param isAuto true if autoGuessing is activated
     */
    private fun guess(isVocal: Boolean, isAuto: Boolean) {
        if (gameViewModel.guess(guessEditText.text.toString(), isVocal)) {
            // Update the number of point view
            increaseScore()
            scoreTextView.text = gameViewModel.score.toString()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            launchSongSummary(success = true)
        } else if (!isAuto) {
            /** Resets the base frame value of the animation and keep the reversing mode **/
            crossAnim.repeatMode = LottieDrawable.RESTART
            crossAnim.repeatMode = LottieDrawable.REVERSE
            crossAnim.playAnimation()
            increaseScore()
        }
    }

    /**
     * Increases the score of a User(designated by uid) after a good guess.
     */
    private fun increaseScore() {
        when (gameInstance.gameFormat) {
            GameFormat.MULTI -> {
                MatchDatabase.incrementScore(matchId!!, playerIndex, Firebase.firestore)
            }
            GameFormat.SOLO -> {
                //TODO
            }
            else -> {
            }
        }
    }

    /** Launches the Game Over fragment after a song
     *
     * @param success indicates if the user found the sound or not
     */
    private fun launchSongSummary(success: Boolean) {
        setVisibilityLayout(View.GONE)
        timer.cancel()
        chronometer.stop()

        this.supportFragmentManager.beginTransaction()
            .addToBackStack("DEMO")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .add<SongSummaryFragment>(R.id.fragment_container, "Song Summary", args = createBundleSongSummary(success))
            .commit()
    }

    /**
     * Creates the bundle providing metadata arguments to a Song Summary fragment
     */
    private fun createBundleSongSummary(success: Boolean): Bundle {
        val bundle = Bundle()
        bundle.putString(ARTIST_KEY, musicMetadata.author)
        bundle.putString(TITLE_KEY, musicMetadata.name)
        bundle.putString(COVER_KEY, musicMetadata.cover)
        bundle.putBoolean(SUCCESS_KEY, success)
        bundle.putBoolean(
            IS_MULTI,
            gameInstance.gameFormat == GameFormat.MULTI
        )
        return bundle
    }

    /**
     * Launches the Game Over fragment after a game
     */
    private fun gameOver() {
        // If we are in multiplayer, wait for the others
        if (gameInstance.gameFormat == GameFormat.MULTI) {
            MatchDatabase.playerFinish(matchId!!, playerIndex, Firebase.firestore)
        }
        launchGameSummary()
    }

    /**
     * Launch the game over summary
     *
     */
    private fun launchGameSummary() {
        if (gameInstance.gameFormat == GameFormat.SOLO) {
            // Get the match id and give the bundle to the fragment
            gameSummary.arguments = bundleOf("matchId" to matchId)

            supportFragmentManager.beginTransaction()
                .addToBackStack("SUMMARY")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(R.id.fragment_container, gameSummary)
                .commit()
        }
    }


    private fun nextRound(isLastMusicLiked: Boolean, isLastMusicSuccess: Boolean) {

        val bundle = createBundleSongSummary(isLastMusicSuccess)

        duration = gameInstance.gameConfig
            ?.parameter
            ?.timeToFind!!
        restartChronometer()

        bundle.putBoolean("liked", isLastMusicLiked)

        // Add a new song record to the final game summary fragment
        val songRecord = SongSummaryFragment()
        songRecord.arguments = bundle
        gameSummary.addNewSongFragment(songRecord)

        if (!gameViewModel.nextRound()) {
            setVisibilityLayout(View.VISIBLE)
            // Pass to the next music
            musicMetadata = gameViewModel.currentMetadata()!!
            guessEditText.hint = musicMetadata.author
            guessEditText.setText("")
            timer = createCountDown()
            timer.start()
        } else {
            gameOver()
        }
    }

    override fun onPause() {
        if (playing)
            playAndPause()
        super.onPause()
    }

    override fun onDestroy() {
        timer.cancel()
        voiceRecognizer.destroy()
        super.onDestroy()
    }
}