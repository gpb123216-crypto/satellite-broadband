package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_logs")
data class NotificationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val mobileNumber: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
