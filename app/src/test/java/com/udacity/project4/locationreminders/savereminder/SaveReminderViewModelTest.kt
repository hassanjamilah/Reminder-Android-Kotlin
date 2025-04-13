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

        viewModel.validateAndSaveReminder(reminder)

        // Initially loading should be true
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Advance coroutine execution
        testDispatcher.scheduler.advanceUntilIdle()

        // Then loading should be false
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun validateReminder_shouldReturnErrorIfTitleMissing() = runTest(testDispatcher) {
        val reminder = ReminderDataItem(null, "Description", "Location", 1.0, 1.0)

        viewModel.validateAndSaveReminder(reminder)

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isNotNull()
    }
}
