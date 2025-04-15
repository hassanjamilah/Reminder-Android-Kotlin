package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.hamcrest.CoreMatchers.not

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: KoinTest {
    // Inject FakeDataSource using Koin
    private val repository: ReminderDataSource by inject()
    private val appContext: Application by inject()

    // Use Koin to inject the ViewModel (it will use the FakeDataSource)
    // private val viewModel: RemindersListViewModel by inject() // Can inject if needed, but often testing via UI interaction is enough

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun setupKoin() {
        stopKoin() // Stop any existing instance
        startKoin {
            // Declare a module with a FakeDataSource
            val testModule = module {
                single<ReminderDataSource> { FakeDataSource() } // Use FakeDataSource
                single { getApplicationContext<Application>() } // Provide Application context if needed by viewmodel directly
                viewModel { RemindersListViewModel(get(), get()) } // Provide ViewModel using Koin
            }
            modules(listOf(testModule))
        }
    }

    @After
    fun tearDownKoin() = runTest {
        (repository as FakeDataSource).deleteAllReminders() // Clean up fake data
        stopKoin()
    }

    @Test
    fun displayReminders_whenDataAvailable_showsList() = runTest {
        // GIVEN - Reminders are added to the fake repository
        val reminder1 = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 1.0)
        val reminder2 = ReminderDTO("Title2", "Desc2", "Loc2", 2.0, 2.0)
        (repository as FakeDataSource).saveReminder(reminder1)
        (repository as FakeDataSource).saveReminder(reminder2)

        // WHEN - ReminderListFragment is launched
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario) // Monitor this fragment

        // THEN - The reminders are displayed on the screen
        onView(withText(reminder1.title)).check(matches(isDisplayed()))
        onView(withText(reminder1.description)).check(matches(isDisplayed()))
        onView(withText(reminder2.title)).check(matches(isDisplayed()))
        onView(withText(reminder2.description)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed()))) // Ensure "no data" is hidden
    }

    @Test
    fun displayNoData_whenRepositoryIsEmpty_showsNoDataView() {
        // GIVEN - The repository is empty (default state after setup/cleanup)

        // WHEN - ReminderListFragment is launched
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)

        // THEN - The "No Data" TextView is displayed
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        // Check that a known item from the list isn't displayed (optional safety check)
        // onView(withText("Some known reminder title")).check(doesNotExist())
    }

    @Test
    fun clickAddReminderFAB_navigatesToSaveReminderFragment() {
        // GIVEN - ReminderListFragment is displayed
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        val mockNavController = mock(NavController::class.java)
        scenario.onFragment { fragment ->
            // Set the NavController property on the fragment
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // WHEN - The Floating Action Button (FAB) is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Navigation is triggered to the SaveReminderFragment direction
        verify(mockNavController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }



}