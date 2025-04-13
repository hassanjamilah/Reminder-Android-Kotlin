package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val reminders = mutableListOf(
            ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 1.0),
            ReminderDTO("Title2", "Desc2", "Loc2", 2.0, 2.0)
        )
        fakeDataSource = FakeDataSource(reminders)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadReminders_shouldShowLoading() = runTest(testDispatcher) {
        viewModel.loadReminders()

        // Initially loading should be true
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Advance dispatcher to complete coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then loading should be false
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun loadReminders_shouldReturnError() = runTest(testDispatcher) {
        fakeDataSource.setReturnError(true)

        viewModel.loadReminders()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Test exception")
    }
}
