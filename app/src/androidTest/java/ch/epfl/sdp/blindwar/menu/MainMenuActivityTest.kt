package ch.epfl.sdp.blindwar.menu

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.game.solo.util.typeSearchViewText
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuActivityTest : TestCase() {
    @get:Rule
    var testRule = ActivityScenarioRule(
        MainMenuActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
        Espresso.closeSoftKeyboard()
    }

    @After
    fun cleanup() {
        Intents.release()
    }

    // INTERFACE

    @Test
    fun testPlayMenuButton() {
        onView(withId(R.id.item_play)).perform(click())
        onView(withId(R.id.play_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testSoloButton() {
        onView(withId(R.id.item_play)).perform(click())
        onView(withId(R.id.soloBtn)).perform(click())
        //intended(hasComponent(SoloActivity::class.java.name))
    }

    @Test
    fun testMultiButton() {
        onView(withId(R.id.item_play)).perform(click())
        onView(withId(R.id.multiBtn)).perform(click())
        //intended(hasComponent(MultiPlayerMenuActivity::class.java.name))
    }

    @Test
    fun testProfileButton() {
        onView(withId(R.id.item_profile)).perform(click())
        //onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testBackButton() {
        val device = UiDevice.getInstance(getInstrumentation())
        assertTrue("Back button can't be pressed", device.pressBack())
    }

    @Test
    fun testViewModels() {
        testRule.scenario.onActivity {
        }
    }

    // SEARCH FRAGMENT

    private fun launchSearchFragment() {
        onView(withId(R.id.item_search)).perform(click())
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
    }

    @Test
    fun testBasicSearch() {
        launchSearchFragment()
        onView(withId(R.id.searchBar)).perform(typeSearchViewText("cirrus"))
        // TODO: Assert that the correct music is displayed
    }

    @Test
    fun testPlaylistCreationButton() {
        launchSearchFragment()
        onView(withId(R.id.addButton)).check(matches(isClickable())).perform(click())
        // TODO: Assert that the playlist creation activity is launcheds
        //onView(withId())
    }
}