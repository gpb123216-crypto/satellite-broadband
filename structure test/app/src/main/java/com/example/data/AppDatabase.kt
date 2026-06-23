package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Customer::class, NotificationLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun notificationLogDao(): NotificationLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wifi_manager_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.customerDao(), database.notificationLogDao())
                }
            }
        }

        suspend fun populateDatabase(customerDao: CustomerDao, notificationLogDao: NotificationLogDao) {
            val demoCustomers = listOf(
                Customer(
                    name = "Satish Kumar",
                    mobileNumber = "9876543210",
                    wifiSSID = "Satish_HighSpeed_5G",
                    wifiPassword = "satishpass987",
                    billDate = "05",
                    billAmount = 599.00,
                    planName = "50 Mbps Fiber Unlimited",
                    macAddress = "A4:2B:B0:11:5C:9E",
                    deviceModel = "TP-Link Archer C6",
                    deviceSerialNumber = "TPLINK9938827110",
                    notes = "Premium customer. Router installed near window.",
                    isActive = true,
                    isPaidThisMonth = false
                ),
                Customer(
                    name = "Amit Sharma",
                    mobileNumber = "9988776655",
                    wifiSSID = "Amit_Home_Fiber",
                    wifiPassword = "amitpassword123",
                    billDate = "10",
                    billAmount = 799.00,
                    planName = "100 Mbps Pro",
                    macAddress = "C8:3A:35:E1:F2:3A",
                    deviceModel = "D-Link DIR-825",
                    deviceSerialNumber = "DLINK87654321",
                    notes = "Prefers reminders via app notifications.",
                    isActive = true,
                    isPaidThisMonth = true
                ),
                Customer(
                    name = "Priya Patel",
                    mobileNumber = "9123456789",
                    wifiSSID = "Priya_VIP_Net",
                    wifiPassword = "priyafiber789",
                    billDate = "15",
                    billAmount = 999.00,
                    planName = "200 Mbps Premium",
                    macAddress = "40:51:7C:F8:12:D5",
                    deviceModel = "Netgear Nighthawk Pro",
                    deviceSerialNumber = "NETGEAR4455883",
                    notes = "Dual band router active.",
                    isActive = true,
                    isPaidThisMonth = false
                ),
                Customer(
                    name = "Rajesh Gupta",
                    mobileNumber = "8888888888",
                    wifiSSID = "Gupta_WiFI",
                    wifiPassword = "guptafamily@123",
                    billDate = "20",
                    billAmount = 499.00,
                    planName = "40 Mbps Starter",
                    macAddress = "F4:F2:6D:E8:19:AA",
                    deviceModel = "Mercusys AC1200",
                    deviceSerialNumber = "MERCUSYS889211",
                    notes = "Needs replacement of adapters in next run.",
                    isActive = false,
                    isPaidThisMonth = true
                )
            )

            for (customer in demoCustomers) {
                val customerId = customerDao.insertCustomer(customer)
                if (!customer.isPaidThisMonth) {
                    notificationLogDao.insertLog(
                        NotificationLog(
                            customerId = customerId,
                            customerName = customer.name,
                            mobileNumber = customer.mobileNumber,
                            title = "Welcome & Pending Bill Alert",
                            message = "Aadarniya ${customer.name}, your monthly payment of ₹${customer.billAmount} for Wi-Fi service (${customer.planName}) is pending. Due date is the ${customer.billDate}th. Please pay to ensure zero interruptions.",
                            timestamp = System.currentTimeMillis() - 7200000L
                        )
                    )
                }
            }
        }
    }
}
