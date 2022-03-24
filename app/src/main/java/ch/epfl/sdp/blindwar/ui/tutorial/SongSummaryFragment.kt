package ch.epfl.sdp.blindwar.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.epfl.sdp.blindwar.R

//import com.squareup.picasso.Picasso

class SongSummaryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View =
            inflater.inflate(R.layout.fragment_song_summary, container, false)

        //val artistView = view.findViewById<ImageView>(R.id.artistImageView)
        val artistText = view.findViewById<TextView>(R.id.artistTextView)
        val trackText = view.findViewById<TextView>(R.id.trackTextView)

        artistText.text = arguments?.get("Artist").toString()
        trackText.text = arguments?.get("Title").toString()
        //Picasso.get().load(arguments?.get("Image")!!.toString()).into(artistView)

        return view
    }
}