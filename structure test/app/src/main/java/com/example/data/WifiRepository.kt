package com.example.data

import kotlinx.coroutines.flow.Flow

class WifiRepository(
    private val customerDao: CustomerDao,
    private val notificationLogDao: NotificationLogDao
) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allLogs: Flow<List<NotificationLog>> = notificationLogDao.getAllLogs()

    suspend fun getCustomerById(id: Long): Customer? {
        return customerDao.getCustomerById(id)
    }

    suspend fun getCustomerByMobileNumber(mobileNumber: String): Customer? {
        return customerDao.getCustomerByMobileNumber(mobileNumber)
    }

    fun getLogsForCustomer(customerId: Long): Flow<List<NotificationLog>> {
        return notificationLogDao.getLogsForCustomer(customerId)
    }

    suspend fun insertCustomer(customer: Customer): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }

    suspend fun insertLog(log: NotificationLog) {
        notificationLogDao.insertLog(log)
    }
}
