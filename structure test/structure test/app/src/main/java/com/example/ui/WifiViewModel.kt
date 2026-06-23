package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Customer
import com.example.data.NotificationLog
import com.example.data.WifiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    PortalSelection,
    AdminDashboard,
    AdminCustomerList,
    AdminAddEditCustomer,
    AdminLogs,
    CustomerLogin,
    CustomerDashboard
}

class WifiViewModel(
    private val application: Application,
    private val repository: WifiRepository
) : AndroidViewModel(application) {

    // Screen State
    private val _currentScreen = MutableStateFlow(AppScreen.PortalSelection)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Backstack for simple nested screens in Admin
    private val screenHistory = mutableListOf<AppScreen>()

    fun navigateTo(screen: AppScreen) {
        screenHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
        } else {
            _currentScreen.value = AppScreen.PortalSelection
        }
    }

    fun clearHistory() {
        screenHistory.clear()
    }

    // Admin state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterActiveOnly = MutableStateFlow<Boolean?>(null) // null = all, true = active, false = inactive
    val filterActiveOnly = _filterActiveOnly.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    // Login state
    private val _loginMobileNumber = MutableStateFlow("")
    val loginMobileNumber = _loginMobileNumber.asStateFlow()

    private val _loggedInCustomer = MutableStateFlow<Customer?>(null)
    val loggedInCustomer = _loggedInCustomer.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    // Toast/Snack message status
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage = _uiMessage.asStateFlow()

    fun showMessage(msg: String) {
        _uiMessage.value = msg
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    // Observe customers with flow transformations (search and filter)
    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list else {
                list.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.mobileNumber.contains(query) ||
                    it.deviceSerialNumber.contains(query, ignoreCase = true) ||
                    it.wifiSSID.contains(query, ignoreCase = true)
                }
            }
        }
        .combine(_filterActiveOnly) { list, activeOnly ->
            when (activeOnly) {
                null -> list
                true -> list.filter { it.isActive }
                false -> list.filter { !it.isActive }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val logs: StateFlow<List<NotificationLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Customer specific notifications
    private val _customerLogs = MutableStateFlow<List<NotificationLog>>(emptyList())
    val customerLogs = _customerLogs.asStateFlow()

    // Search and filters
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(active: Boolean?) {
        _filterActiveOnly.value = active
    }

    fun selectCustomer(customer: Customer?) {
        _selectedCustomer.value = customer
    }

    fun updateLoginMobile(num: String) {
        _loginMobileNumber.value = num
        _loginError.value = null
    }

    // Customer login action
    fun loginCustomer() {
        val num = _loginMobileNumber.value.trim()
        if (num.length < 10) {
            _loginError.value = "Krpya sahi mobile number enter karein (kam se kam 10 anko ka)."
            return
        }

        viewModelScope.launch {
            val customer = repository.getCustomerByMobileNumber(num)
            if (customer != null) {
                _loggedInCustomer.value = customer
                // Retrieve notifications for this customer
                repository.getLogsForCustomer(customer.id).collect {
                    _customerLogs.value = it
                }
                navigateTo(AppScreen.CustomerDashboard)
                showMessage("Swagat hai, ${customer.name}!")
            } else {
                _loginError.value = "Maafi chahenge! Ye number registered nahi hai. Krpya support/admin se sampark karein."
            }
        }
    }

    fun logoutCustomer() {
        _loggedInCustomer.value = null
        _customerLogs.value = emptyList()
        _loginMobileNumber.value = ""
        navigateTo(AppScreen.CustomerLogin)
    }

    // Admin Actions
    fun insertCustomer(
        name: String,
        mobileNumber: String,
        wifiSSID: String,
        wifiPassword: String,
        billDate: String,
        billAmount: Double,
        planName: String,
        macAddress: String,
        deviceModel: String,
        deviceSerialNumber: String,
        notes: String = "",
        isActive: Boolean = true,
        isPaidThisMonth: Boolean = true
    ) {
        viewModelScope.launch {
            if (name.isBlank() || mobileNumber.isBlank() || wifiSSID.isBlank() || wifiPassword.isBlank()) {
                showMessage("Zaroori details (Name, Mobile, Wi-Fi SSID aur Password) fill karna compulsory hai.")
                return@launch
            }

            val newCustomer = Customer(
                name = name.trim(),
                mobileNumber = mobileNumber.trim(),
                wifiSSID = wifiSSID.trim(),
                wifiPassword = wifiPassword.trim(),
                billDate = billDate.trim().padStart(2, '0'),
                billAmount = billAmount,
                planName = planName.trim().ifBlank { "Basic Plan" },
                macAddress = macAddress.trim().ifBlank { "00:00:00:00:00:00" },
                deviceModel = deviceModel.trim().ifBlank { "Generic ONU Router" },
                deviceSerialNumber = deviceSerialNumber.trim().ifBlank { "SN-" + System.currentTimeMillis().toString().takeLast(8) },
                notes = notes,
                isActive = isActive,
                isPaidThisMonth = isPaidThisMonth
            )

            val id = repository.insertCustomer(newCustomer)
            showMessage("Customer '${name}' successfully add ho gaya!")
            navigateBack()
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            // If the updated customer is the logged-in customer, update their state too
            if (_loggedInCustomer.value?.id == customer.id) {
                _loggedInCustomer.value = customer
            }
            showMessage("Customer verification/updates complete!")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            showMessage("Customer account profile permanently delete ho gaya.")
            if (_selectedCustomer.value?.id == customer.id) {
                _selectedCustomer.value = null
            }
        }
    }

    // SSID & Password updates directly from Customer dashboard
    fun updateWifiCredentials(cssid: String, cpass: String) {
        val currentCust = _loggedInCustomer.value ?: return
        if (cssid.isBlank() || cpass.length < 8) {
            showMessage("Wi-Fi SSID khali nahi ho sakta aur Password kam se kam 8 characters ka hona chahiye!")
            return
        }

        val updated = currentCust.copy(
            wifiSSID = cssid,
            wifiPassword = cpass
        )

        viewModelScope.launch {
            repository.updateCustomer(updated)
            _loggedInCustomer.value = updated
            showMessage("Aapke router ke Net SSID aur Password details update kar diye gaye hain!")

            // Add notification log about wifi change
            val log = NotificationLog(
                customerId = updated.id,
                customerName = updated.name,
                mobileNumber = updated.mobileNumber,
                title = "Wi-Fi Settings Changed",
                message = "Aapne safaltapurvak apna Wi-Fi Name badal kar '$cssid' aur Password '$cpass' set kar diya hai."
            )
            repository.insertLog(log)
        }
    }

    // Bill payment trigger by customer
    fun processCustomerPayment() {
        val currentCust = _loggedInCustomer.value ?: return
        val updated = currentCust.copy(isPaidThisMonth = true)
        viewModelScope.launch {
            repository.updateCustomer(updated)
            _loggedInCustomer.value = updated
            showMessage("Payment of ₹${currentCust.billAmount} successfully complete!")

            val log = NotificationLog(
                customerId = updated.id,
                customerName = updated.name,
                mobileNumber = updated.mobileNumber,
                title = "Payment Successful",
                message = "Dhanyavad! Aapka ₹${updated.billAmount} ka payment safaltapurvak receive ho gaya hai. Agli bill date: ${updated.billDate}th hai."
            )
            repository.insertLog(log)
        }
    }

    // Send single manual/automatic notification to a specific customer (Payment Reminder)
    fun sendPaymentReminder(customer: Customer) {
        viewModelScope.launch {
            val title = "Payment Reminder \uD83D\uDCE2"
            val message = "Pranam ${customer.name}, aapka Wi-Fi bill ₹${customer.billAmount} baki hai. Wi-Fi band hone se bachane ke liye jaldi bhugtan karein."

            val logged = NotificationLog(
                customerId = customer.id,
                customerName = customer.name,
                mobileNumber = customer.mobileNumber,
                title = title,
                message = message,
                timestamp = System.currentTimeMillis()
            )

            // Update customer reminder timestamp
            val updatedCustomer = customer.copy(
                lastReminderSent = System.currentTimeMillis()
            )
            repository.updateCustomer(updatedCustomer)

            repository.insertLog(logged)

            // Trigger system tray notification
            triggerSystemNotification(customer.name, "Bill due: ₹${customer.billAmount}. Play store status remind.")
            showMessage("${customer.name} ko payment reminder notification bhej di gai!")
        }
    }

    // Bulks reminder task simulation
    fun sendBulkReminders() {
        viewModelScope.launch {
            val allCustFlow = repository.allCustomers
            var count = 0
            allCustFlow.collect { list ->
                val unpaid = list.filter { !it.isPaidThisMonth && it.isActive }
                for (cust in unpaid) {
                    val title = "Monthly Payment Reminder ⚡"
                    val message = "Namaskar ${cust.name}, aapka ₹${cust.billAmount} ka subscription due hai. Zero discontinuity ke liye krpya onetime payment clear karein."
                    repository.insertLog(
                        NotificationLog(
                            customerId = cust.id,
                            customerName = cust.name,
                            mobileNumber = cust.mobileNumber,
                            title = title,
                            message = message
                        )
                    )
                    // Trigger Android system notification
                    triggerSystemNotification(cust.name, "WiFi Payment due amount of ₹${cust.billAmount} is pending.")
                    count++
                }
                showMessage("${count} active unpaid customers ko payment alarm reminders bhej diye gaye!")
                // Break infinite collect loop
                return@collect
            }
        }
    }

    // Android System Notification builder
    private fun triggerSystemNotification(customerName: String, summaryText: String) {
        val channelId = "wifi_manager_reminders"
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Wifi Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Customer Wifi Subscription Reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(application, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Satellite Broadband Reminder Alert")
            .setContentText("Dear $customerName, $summaryText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

class WifiViewModelFactory(
    private val application: Application,
    private val repository: WifiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
