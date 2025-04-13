package com.udacity.project4.locationreminders.data.local


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RemindersDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        dao = database.reminderDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertReminder_getById_returnsCorrectReminder() = runBlocking {
        // GIVEN - A reminder
        val reminder = ReminderDTO("Title", "Description", "Location", 1.0, 1.0)

        // WHEN - Insert and retrieve by ID
        dao.saveReminder(reminder)
        val loaded = dao.getReminderById(reminder.id)

        // THEN - Check all fields
        assertThat(loaded != null, `is`(true))
        assertThat(loaded?.id, `is`(reminder.id))
        assertThat(loaded?.title, `is`("Title"))
        assertThat(loaded?.description, `is`("Description"))
        assertThat(loaded?.location, `is`("Location"))
        assertThat(loaded?.latitude, `is`(1.0))
        assertThat(loaded?.longitude, `is`(1.0))
    }

    @Test
    fun getReminderById_notFound_returnsNull() = runBlocking {
        // WHEN - Querying for non-existent ID
        val result = dao.getReminderById("non-existent-id")

        // THEN - Should return null
        assertThat(result == null, `is`(true))
    }
}