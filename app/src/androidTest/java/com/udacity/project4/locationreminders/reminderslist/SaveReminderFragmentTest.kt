package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.runner.Description
import org.junit.runner.RunWith
import kotlin.test.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderFragmentTest {

    @Before
    fun init() {
        // Launch host activity or fragment
    }

    @Test
    fun saveReminder_withMissingTitle_showsSnackbar() {
        // GIVEN - SaveReminderFragment
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        // WHEN - User clicks save without title
        onView(withId(R.id.reminderDescription)).perform(typeText("Test Description"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Snackbar is displayed
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))
    }

    @Test
    fun saveReminder_successfully_navigatesBack() {
        // GIVEN
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        // WHEN
        onView(withId(R.id.reminderTitle)).perform(typeText("Test Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("Test Desc"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN
        // Toast message or navigation command
        // Option 1: Check Toast
        onView(withText(R.string.reminder_saved)).check(matches(isDisplayed()))

        // Option 2: Verify navigation
        // using mock NavController
    }
}


