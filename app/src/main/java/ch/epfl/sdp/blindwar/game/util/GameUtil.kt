package ch.epfl.sdp.blindwar.game.util

import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.data.music.metadata.MusicMetadata
import ch.epfl.sdp.blindwar.game.model.Difficulty
import ch.epfl.sdp.blindwar.game.model.Genre
import ch.epfl.sdp.blindwar.game.model.LocalPlaylist
import ch.epfl.sdp.blindwar.game.model.OnlinePlaylist
import ch.epfl.sdp.blindwar.game.model.config.*

object GameUtil {

    const val TIME_TO_FIND = 5000
    const val ROUND = 2

    private const val ACDC: String =
        "https://i.scdn.co/image/ab67616d0000b27351c02a77d09dfcd53c8676d0"
    private const val DAFT_PUNK: String =
        "https://i.scdn.co/image/ab67616d0000b273b33d46dfa2635a47eebf63b2"
    private const val GORILLAZ: String =
        "https://i.scdn.co/image/ab67616d0000b27319d85a472f328a6ed9b704cf"
    private const val LADY_GAGA: String =
        "https://i.scdn.co/image/ab67616d0000b273e691217483df8798445c82e2"
    private const val THE_NOTORIOUS: String =
        "https://i.scdn.co/image/ab67616d0000b273db09958534ac66f9a90d3cf7"
    private const val THE_CLASH: String =
        "https://i.scdn.co/image/ab67616d0000b273cd9d8bc9ef04014b6e90e182"
    private const val RENAUD: String =
        "https://i.scdn.co/image/ab67616d0000b273b9cb0f33dc7ae3344f69282a"
    private const val RHCP: String =
        "https://i.scdn.co/image/ab67616d0000b27394d08ab63e57b0cae74e8595"
    private const val SUM_41: String =
        "https://i.scdn.co/image/ab67616d0000b2739a482180e6a306229bff49dc"
    private const val MICHAEL_JACKSON: String =
        "https://i.scdn.co/image/ab6761610000e5eba2a0b9e3448c1e702de9dc90"
    private const val QUEEN: String =
        "https://i.scdn.co/image/ab67616d0000b273dada67d578bea0b446036e87"
    private const val THE_BEATLES: String =
        "https://i.scdn.co/image/ab6761610000e5ebe9348cc01ff5d55971b22433"
    private const val KLAUS_BADELT: String =
        "https://i.scdn.co/image/ab67616d0000b27338786c7492ac252797bb2648"
    private const val DEEP_PURPLE: String =
        "https://i.scdn.co/image/ab6761610000e5eb23a7b4c49d285729a974d6dd"
    private const val LED_ZEPPELIN: String =
        "https://i.scdn.co/image/207803ce008388d3427a685254f9de6a8f61dc2e"
    private const val THE_CHAMPS: String =
        "https://i.scdn.co/image/ab67616d00001e02621b1b8858b7bfe2706fb7bb"

    private val urls = listOf(
        LADY_GAGA, GORILLAZ, ACDC, THE_NOTORIOUS, THE_CLASH,
        RENAUD, RHCP, SUM_41, DAFT_PUNK,
        //REVERSED
        MICHAEL_JACKSON, QUEEN, THE_BEATLES, KLAUS_BADELT,
        DEEP_PURPLE, LED_ZEPPELIN, THE_CHAMPS
    )

    private val keys = listOf(
        Pair("Lady Gaga", "Poker Face"),
        Pair("Gorillaz", "Feel Good Inc"),
        Pair("ACDC", "Highway to Hell"),
        Pair("The Notorious BIG", "Respect"),
        Pair("The Clash", "London Calling"),
        Pair("Renaud", "Mistral Gagnant"),
        Pair("Red Hot Chili Peppers", "Californication"),
        Pair("Sum 41", "In Too Deep"),
        Pair("Daft Punk", "One More Time"),
        // REVERSED
        Pair("Michael Jackson", "Billie Jean"),
        Pair("Queen", "Bohemian Rhapsody"),
        Pair("The Beatles", "Hey Jude"),
        Pair("Klaus Badelt", "Pirates of the Caribbean"),
        Pair("Deep Purple", "Smoke on the water"),
        Pair("Led Zeppelin", "Stairway to Heaven"),
        Pair("The Champs", "Tequila")
    )

    private val ids = listOf(
        R.raw.lady_gaga_poker_face, R.raw.gorillaz_feel_good, R.raw.acdc_highway_to_hell,
        R.raw.the_notorious_big_respect, R.raw.the_clash_london_calling,
        R.raw.renaud_mistral_gagnant, R.raw.red_hot_chili_peppers_californication,
        R.raw.sum_41_in_too_deep, R.raw.daft_punk_one_more_time,
        //REVERSED
        R.raw.billy_reversed, R.raw.bohemian_reversed, R.raw.hey_jude_reversed,
        R.raw.pirates_reversed, R.raw.smoke_reversed, R.raw.stairway_reversed,
        R.raw.tequila_reversed
    )

    private val durations = listOf(
        214000, 254000, 207000, 3200000, 203000, 162000, 321000, 222000, 321000,
        //REVERSED
        30000, 30000, 30000, 30000, 30000, 30000, 30000
    )

    fun metadataTutorial(): MutableMap<String, MusicMetadata> {
        return mutableMapOf<String, MusicMetadata>().let {
            for ((index, key) in keys.withIndex()) {
                it[key.first] = MusicMetadata.createWithResourceId(
                    key.second,
                    key.first,
                    urls[index],
                    durations[index],
                    ids[index]
                )
            }
            it
        }
    }

    private const val tutorialPlaylistLength = 9
    private const val reversedPlaylistLength = 7

    private val PLAYLIST: List<MusicMetadata> = metadataTutorial().values.toList()

    private val REV_PLAYLIST: List<MusicMetadata> =
        metadataTutorial().values.toList().subList(
            tutorialPlaylistLength,
            tutorialPlaylistLength + reversedPlaylistLength
        )
    private val TUTORIAL_PLAYLIST: List<MusicMetadata> =
        metadataTutorial().values.toList().subList(
            0,
            tutorialPlaylistLength
        )

    private val gameParameter =
        GameParameter(
            round = ROUND,
            funny = false,
            timeToFind = TIME_TO_FIND,
            hint = true,
            lives = 5
        )

    private val gameConfig =
        GameConfig(
            GameMode.REGULAR,
            gameParameter
        )

    private const val URL_PREVIEW_FIFA =
        "https://p.scdn.co/mp3-preview/6cc1de8747a673edf568d78a37b03eab86a65c21?cid=774b29d4f13844c495f206cafdad9c86"

    const val URL_FIFA_SONG_2 =
        "https://p.scdn.co/mp3-preview/7c53994cfbd98e4fe15c319ed23862a3bf24ac81?cid=774b29d4f13844c495f206cafdad9c86"

    private const val URL_FIFA_SONG_3 =
        "https://p.scdn.co/mp3-preview/9cb161a377591d24bfae1763c9d53c22549bddc5?cid=774b29d4f13844c495f206cafdad9c86"

    private const val URL_PREVIEW_TUTORIAL =
    //    "https://p.scdn.co/mp3-preview/83a6425e6cf360bff80cb93fa3fbc8799cac3894?cid=774b29d4f13844c495f206cafdad9c86"
    //"https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview115/v4/26/70/c9/2670c997-2aa1-550c-477a-fec1708ed552/mzaf_14517577682338403358.plus.aac.p.m4a"
    // "https://p.scdn.co/mp3-preview/33c3aa0656120e384ffdd450a730d62e82869c65?cid=774b29d4f13844c495f206cafdad9c86"
        // "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/86/ff/67/86ff676b-de98-1f74-b90a-b882566ab4a0/mzaf_1515347027412876886.plus.aac.p.m4a"
        "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/ca/6f/88/ca6f884c-ae3a-8503-3f0e-27522dbea1aa/mzaf_3487386545306563563.plus.aac.p.m4a"


    private const val COVER_URL_SONG =
        "https://i.scdn.co/image/ab67616d0000b27348eb4d2efa517a920ff4e14e"


    private val searchin = (
            MusicMetadata.createWithURI(
                "Searchin",
                "Matisyahu",
                COVER_URL_SONG,
                30000,
                URL_PREVIEW_FIFA
            ))

    private val uatw = MusicMetadata.createWithURI(
        "Us Against the World",
        "Clement Marfo",
        "https://i.scdn.co/image/ab67616d0000b273b6e0b1707eea74cd006df458",
        30000,
        URL_FIFA_SONG_2
    )

    val fly = MusicMetadata.createWithURI(
        "Fly Or Die",
        "Rock Mafia",
        "https://i.scdn.co/image/ab67616d0000b273711f517eabfb36486a6d96f2",
        30000,
        URL_FIFA_SONG_3
    )

    val fifaPlaylist = OnlinePlaylist(
        "FIFA13",
        "FIFA 13 OST",
        "EA Sports",
        arrayListOf(Genre.POP),
        arrayListOf(
            searchin,
            uatw,
            fly
        ),
        "https://i.scdn.co/image/ab67706c0000bebba1371bd946a7bc3f61f83db4",
        URL_PREVIEW_FIFA,
        Difficulty.EASY
    )

    private val masterPlaylist = LocalPlaylist(
        "tutorial",
        "GameUtil",
        "BlindWar",
        arrayListOf(Genre.POP, Genre.RAP),
        PLAYLIST,
        "",
        URL_PREVIEW_TUTORIAL,
        Difficulty.EASY
    )

    private val reversePlaylist = LocalPlaylist(
        "tutorial",
        "Reverse",
        "BlindWar",
        arrayListOf(Genre.POP, Genre.RAP),
        REV_PLAYLIST,
        "",
        URL_PREVIEW_TUTORIAL,
        Difficulty.EXTREME
    )

    //to del
    private val hiphopPlaylist = LocalPlaylist(
        "tutorial",
        "Hip hop",
        "BlindWar",
        arrayListOf(Genre.RAP),
        REV_PLAYLIST,
        "https://i.scdn.co/image/ab67706f0000000317f5a91128f800d0223948cd",
        URL_PREVIEW_TUTORIAL,
        Difficulty.MEDIUM
    )

    //to del
    private val classicPlaylist = LocalPlaylist(
        "tutorial",
        "Classic Rock",
        "BlindWar",
        arrayListOf(Genre.ROCK),
        REV_PLAYLIST,
        "https://i.scdn.co/image/ab67616d0000b27311b8f61cc557b8f1a3ea1d6e",
        URL_PREVIEW_TUTORIAL,
        Difficulty.MEDIUM
    )

    //to del
    private val housePlaylist = LocalPlaylist(
        "tutorial",
        "House",
        "BlindWar",
        arrayListOf(Genre.HOUSE),
        REV_PLAYLIST,
        "https://i.scdn.co/image/ab67706c0000bebbb27759f3adcf3943b417d62c",
        URL_PREVIEW_TUTORIAL,
        Difficulty.DIFFICULT
    )

    //to del
    private val partyPlaylist = LocalPlaylist(
        "tutorial",
        "Party",
        "BlindWar",
        arrayListOf(Genre.DANCE),
        REV_PLAYLIST,
        "https://i.scdn.co/image/ab67706c0000bebb6c5db53da7585761f71553ae",
        URL_PREVIEW_TUTORIAL,
        Difficulty.EASY
    )

    private val tutorialPlaylist = LocalPlaylist(
        "tutorial",
        "Tutorial",
        "BlindWar",
        arrayListOf(Genre.POP, Genre.RAP),
        TUTORIAL_PLAYLIST,
        "",
        URL_PREVIEW_TUTORIAL,
        Difficulty.MEDIUM
    )

    private const val COVER_TESTING =
        "https://i.scdn.co/image/ab67616d0000b273df756f52b91b4dcd656760b0"

    private const val SONG_TESTING =
        "Silver for Monsters"

    private const val URL_PREVIEW_TESTING =
        "https://p.scdn.co/mp3-preview/ecfd294001cbeea0811b78b35b5a1da80bf3ef98?cid=774b29d4f13844c495f206cafdad9c86"

    private val testingPlaylist = OnlinePlaylist(
        "",
        "The Witcher 3 OST",
        "Marcin Przybyłowicz",
        arrayListOf(Genre.POP),
        arrayListOf(
            MusicMetadata.createWithURI(
                SONG_TESTING,
                "Marcin Przybyłowicz",
                COVER_TESTING,
                30000,
                URL_PREVIEW_TESTING
            )
        ),
        COVER_TESTING,
        "https://p.scdn.co/mp3-preview/b2e959350596fc2d3f9ca80e855c51db9a5c5453?cid=774b29d4f13844c495f206cafdad9c86",
        Difficulty.EASY
    )

    val BASE_PLAYLISTS = arrayListOf(tutorialPlaylist, testingPlaylist, hiphopPlaylist, classicPlaylist, housePlaylist, partyPlaylist, reversePlaylist)

    val gameInstanceSolo = GameInstance(gameConfig, masterPlaylist, GameFormat.SOLO)
    val gameInstanceMulti = GameInstance(gameConfig, masterPlaylist, GameFormat.MULTI)
}