package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveReminder_shouldShowLoading() = runTest(testDispatcher) {
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.0, 1.0)

        // Run the block that triggers the coroutine and synchronous update


        advanceUntilIdle()
        // Since showLoading.value = true is synchronous, this should now capture it
        viewModel.saveReminder(reminder)
        val loadingBefore = viewModel.showLoading.getOrAwaitValue()


        assertThat(loadingBefore).isTrue() // Assert immediately after it's set
        // Now let the coroutine run to completion
        testDispatcher.scheduler.advanceUntilIdle()

        // This should now capture the 'false' value set inside the coroutine
        // Use getOrAwaitValue again to ensure we wait for the update from the coroutine
        val loadingAfter = viewModel.showLoading.getOrAwaitValue()
        assertThat(loadingAfter).isFalse()


    }

    @Test
    fun validateReminder_shouldReturnErrorIfTitleMissing() = runTest(testDispatcher) {
        val reminder = ReminderDataItem(null, "Description", "Location", 1.0, 1.0)

        viewModel.validateAndSaveReminder(reminder)

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isNotNull()
    }
}
