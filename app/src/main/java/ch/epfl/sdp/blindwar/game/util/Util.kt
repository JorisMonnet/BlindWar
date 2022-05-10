package ch.epfl.sdp.blindwar.game.util

import android.os.CountDownTimer
import android.widget.Filter
import ch.epfl.sdp.blindwar.game.model.Displayable
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.*

object Util {
    /**
     * Create a countdown timer
     * @param duration timer's duration
     * @param progress visual indicator reference
     */
    fun createCountDown(duration: Long, progress: CircularProgressIndicator): CountDownTimer {
        return object : CountDownTimer(duration, 10) {

            override fun onTick(millisUntilFinished: Long) {
                progress.progress =
                    (((duration.toDouble() - millisUntilFinished.toDouble()) / duration.toDouble()) * 100).toInt()
            }

            override fun onFinish() {}
        }
    }

    /**
     * Creates a Filter based on the name for the playlistSelectionFragment
     * @param initialOnlinePlaylists base list of playlists
     * @param onlinePlaylistSet list of available playlists
     * @param displayableItemAdapter playlistRecyclerView adapter
     */
    fun playlistFilterQuery(
        initialOnlinePlaylists: List<Displayable>,
        onlinePlaylistSet: ArrayList<Displayable>,
        displayableItemAdapter: DisplayableItemAdapter
    ): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<Displayable> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialOnlinePlaylists.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().lowercase(Locale.getDefault())
                initialOnlinePlaylists.forEach {
                    if (it.getName().lowercase(Locale.getDefault()).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                onlinePlaylistSet.clear()
                onlinePlaylistSet.addAll(results.values as ArrayList<Displayable>)
                displayableItemAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Check if internet is working via one ping
     *
     * @return
     */
    fun isOnline(): Boolean {
        return try {
            Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8").waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }
}