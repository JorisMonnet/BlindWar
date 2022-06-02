package ch.epfl.sdp.blindwar.data.spotify

import android.util.Base64

object SpotifyApiConstants {
    /** Credentials constants **/
    private const val VALUE = "66f4e3c14d6e425"
    private const val VALUE_2 = "19a77b5d1e0a4142"
    private const val VALUE_3 = "caed973ba62bb2077"
    private const val VALUE_4 = "ab2ec16ecea8bb28"
    const val AUTH_TYPE = "client_credentials"

    fun credentialsEncoding(): String {
        return "Basic ${
            Base64.encodeToString(
                "${VALUE + VALUE_3}:${VALUE_2 + VALUE_4}".toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )
        }"
    }

    fun tokenParameter(responseToken: SpotifyToken): String {
        return "Bearer ${responseToken.access_token}"
    }

    /** Headers constants **/
    const val AUTH = "Authorization"
    const val ACCEPT = "Accept: application/json"
    const val CONTENT_TYPE = "Content-Type: application/json"
    const val URL_ENCODED_FORM = "Content-Type: application/x-www-form-urlencoded"

    /** URL path constants **/
    const val SPOTIFY_META_END_POINT = "https://api.spotify.com/v1/"
    const val SPOTIFY_AUTH_END_POINT = "https://accounts.spotify.com/"
    const val API_PATH = "api/token/"
    const val ARTIST_PATH = "artists/{artist_id}"
    const val TRACK_PATH = "tracks/{track_id}"
    const val SEARCH_PATH = "search"

    /** Query and field constants **/
    const val ARTIST_ID = "artist_id"
    const val GRANT_TYPE = "grant_type"
    const val TRACK_ID = "track_id"
    const val TYPE = "type"
    const val QUERY = "q"
    const val LIMIT = "limit"
}