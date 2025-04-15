package com.udacity.project4.locationreminders.reminderslist


import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario // Use ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity // Target the Activity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.FakeDataSource // Use your FakeDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity // Use monitorActivity
// Remove TestNavHostController imports if not used elsewhere
// import androidx.navigation.testing.TestNavHostController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking // Use runBlocking for repo cleanup
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get // Use KoinTest get

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderFragmentTest: KoinTest {

    // Use Koin to get dependencies, including the repository
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<RemindersActivity>

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun init() {
        stopKoin() // Stop existing Koin instance
        appContext = ApplicationProvider.getApplicationContext()
        val testModule = module {
            // Provide Application Context if needed
            // single { appContext } // Might not be needed if VM gets it via AndroidViewModel

            // Provide ViewModel using Koin
            viewModel { RemindersListViewModel(appContext, get() as ReminderDataSource) } // For list fragment
            single { SaveReminderViewModel(appContext, get() as ReminderDataSource) }    // For save fragment

            // Use FakeDataSource for testing Repository
            single<ReminderDataSource> { FakeDataSource() }

            // Provide Dao for FakeDataSource if it needs it, otherwise FakeDataSource is self-contained
            single { FakeDataSource() as ReminderDataSource } // Replace RemindersLocalRepository

            // If FakeDataSource directly uses DAO:
            // single { FakeRemindersDao() } // You might need a fake DAO too
            // single<ReminderDataSource> { RemindersLocalRepository(get()) } // Keep if testing LocalRepo with fake DAO
        }
        startKoin {
            modules(listOf(testModule))
        }

        // Get repository instance
        repository = get()

        // Clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }

        // Launch the activity scenario
        activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // Monitor the activity
    }

    @After
    fun tearDown() {
        runBlocking {
            repository.deleteAllReminders() // Clean up fake data
        }
        activityScenario.close() // Close the activity scenario
        stopKoin()
    }

    // --- Test Cases Focused on SaveReminderFragment ---

    @Test
    fun navigateToSaveReminder_enterNoTitle_showsErrorSnackbar() {
        // GIVEN - On the Reminders List screen
        // WHEN - Navigate to SaveReminderFragment
        onView(withId(R.id.addReminderFAB)).perform(click())

        // WHEN - Enter description, simulate location selection (optional), click save
        onView(withId(R.id.reminderDescription)).perform(typeText("No Title Test Desc"))
        closeSoftKeyboard()
        // Simulate selecting location by navigating and returning (more E2E) OR assume selected for validation test
        // For this validation, let's assume location was conceptually selected or skip that step if validation happens before it
        // Click select location -> Simulate interaction -> Click save location button
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick()) // Simulate selecting a point
        onView(withId(R.id.saveLocationButton)).perform(click()) // Save and return

        // Click final save
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Snackbar with the title error message is displayed
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_enter_title)))
    }

    @Test
    fun navigateToSaveReminder_enterNoLocation_showsErrorSnackbar() {
        // GIVEN - On the Reminders List screen
        // WHEN - Navigate to SaveReminderFragment
        onView(withId(R.id.addReminderFAB)).perform(click())

        // WHEN - Enter title and description, but DO NOT select location, click save
        onView(withId(R.id.reminderTitle)).perform(typeText("No Location Test Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("No Location Test Desc"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Snackbar with the location error message is displayed
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_select_location)))
    }

}


