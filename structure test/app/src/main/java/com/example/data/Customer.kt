package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobileNumber: String,
    val wifiSSID: String,
    val wifiPassword: String,
    val billDate: String, // e.g. "05" indicating 5th of every month
    val billAmount: Double,
    val planName: String,
    val macAddress: String,
    val deviceModel: String,
    val deviceSerialNumber: String,
    val notes: String = "",
    val isActive: Boolean = true,
    val isPaidThisMonth: Boolean = true,
    val lastReminderSent: Long = 0L
)
