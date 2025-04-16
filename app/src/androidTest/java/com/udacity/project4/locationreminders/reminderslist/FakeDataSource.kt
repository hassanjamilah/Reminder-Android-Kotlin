package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource : ReminderDataSource {

    private var reminders = mutableListOf<ReminderDTO>()
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setReminders(newReminders: List<ReminderDTO>) {
        reminders.clear()
        reminders.addAll(newReminders)
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error("Test exception: failed to retrieve reminders")
        } else {
            Result.Success(ArrayList(reminders)) // Return copy for safety
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error("Test exception: failed to retrieve reminder")
        } else {
            val reminder = reminders.find { it.id == id }
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found with id: $id")
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}