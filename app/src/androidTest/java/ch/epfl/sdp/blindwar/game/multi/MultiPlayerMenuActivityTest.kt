package ch.epfl.sdp.blindwar.game.multi

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sdp.blindwar.R
import ch.epfl.sdp.blindwar.game.util.GameActivity
import ch.epfl.sdp.blindwar.menu.MainMenuActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MultiPlayerMenuActivityTest {
    @get:Rule
    var testRule = ActivityScenarioRule(
        MultiPlayerMenuActivity::class.java
    )

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testCancelButton() {
        onView(withId(R.id.cancel_multi_menu)).perform(scrollTo(), ViewActions.click())
        intended(hasComponent(MainMenuActivity::class.java.name))
    }

    @Test
    fun testDisplayFriendButtonAndClose() {
        onView(withId(R.id.imageFriendsButton)).perform(scrollTo(), ViewActions.click())
        Thread.sleep(1000)

        // Click on close
        onView(isRoot()).perform(ViewActions.pressBack());
        Thread.sleep(1000)

        onView(withId(R.id.imageFriendsButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testDisplayRandomButton() {
        onView(withId(R.id.imageRandomButton)).perform(scrollTo(), ViewActions.click())
        onView(withId(R.id.imageRandomButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testCreateButton() {
        onView(withId(R.id.imageCreateButton)).perform(scrollTo(), ViewActions.click())
        intended(hasComponent(ChoseNumberOfPlayerActivity::class.java.name))
    }
}