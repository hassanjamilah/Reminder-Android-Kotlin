package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(RemindersActivity::class.java)

    @Before
    fun setup() {
        // You can setup a fake repo here if needed
    }

    @After
    fun tearDown() {
        // Clean up any states or fake repos
    }

    @Test
    fun clickAddReminderButton_navigatesToSaveReminderFragment() {
        // GIVEN - ReminderListFragment is displayed
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // WHEN - FAB is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify navigation to SaveReminderFragment
        onView(withId(R.id.saveReminder)).check(matches(isDisplayed()))
    }

    @Test
    fun reminderList_ShowsSnackbarOnError() {
        // GIVEN - Launch activity with fake data source returning error
        val fakeDataSource = FakeDataSource()
        fakeDataSource.setReturnError(true)

        val viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        val fragment = launchFragmentInContainer<ReminderListFragment>()

        // WHEN - Load reminders

        // THEN - Check for snackbar
        onView(withText("Test exception")).check(matches(isDisplayed()))
    }

}