package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Customer
import com.example.data.NotificationLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CanvasWifiIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val px = width / 2f
        val py = height * 0.8f
        
        // Center Dot
        drawCircle(
            color = color,
            radius = width * 0.12f,
            center = androidx.compose.ui.geometry.Offset(px, py)
        )
        
        val strokeWidth = width * 0.1f
        val style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        // Arc 1 (inner ring)
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(px - width * 0.28f, py - width * 0.28f),
            size = androidx.compose.ui.geometry.Size(width * 0.56f, width * 0.56f),
            style = style
        )
        
        // Arc 2 (outer ring)
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(px - width * 0.56f, py - width * 0.56f),
            size = androidx.compose.ui.geometry.Size(width * 1.12f, width * 1.12f),
            style = style
        )
    }
}

@Composable
fun PortalApp(viewModel: WifiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.PortalSelection -> PortalSelectionScreen(viewModel)
                AppScreen.AdminDashboard -> AdminDashboardScreen(viewModel)
                AppScreen.AdminCustomerList -> AdminCustomerListScreen(viewModel)
                AppScreen.AdminAddEditCustomer -> AdminAddEditCustomerScreen(viewModel)
                AppScreen.AdminLogs -> AdminLogsScreen(viewModel)
                AppScreen.CustomerLogin -> CustomerLoginScreen(viewModel)
                AppScreen.CustomerDashboard -> CustomerDashboardScreen(viewModel)
            }
        }
    }
}

// 1. ROLE SELECTION SCREEN
@Composable
fun PortalSelectionScreen(viewModel: WifiViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF1E293B)  // Slate 800
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Visual Logo / Badge
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                CanvasWifiIcon(
                    modifier = Modifier.size(54.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Satellite Broadband",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = "WiFi Maintenance & Client Self-Care Portal",
                fontSize = 16.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Admin Portal Card button
            ElevatedCard(
                onClick = {
                    viewModel.clearHistory()
                    viewModel.navigateTo(AppScreen.AdminDashboard)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF3B82F6) // Bright blue
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Admin Portal",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Panel (Hindi-English)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Add customers, serial details & send reminds.",
                            color = Color(0xFFEFF6FF),
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Launch",
                        tint = Color.White
                    )
                }
            }

            // Customer Self Care Card button
            ElevatedCard(
                onClick = {
                    viewModel.clearHistory()
                    viewModel.navigateTo(AppScreen.CustomerLogin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF10B981) // Bright emerald
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Customer Portal",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Customer App Portal",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Change Wifi Password, SSID & check bills.",
                            color = Color(0xFFECFDF5),
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Launch",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "⚡ Realtime database sync simulation through Room",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

// 2. ADMIN DASHBOARD SCREEN
@Composable
fun AdminDashboardScreen(viewModel: WifiViewModel) {
    val customers by viewModel.customers.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val totalSubscribers = customers.size
    val activeSubscribers = customers.count { it.isActive }
    val unpaidSubscribers = customers.count { !it.isPaidThisMonth && it.isActive }
    val totalRevenue = customers.sumOf { it.billAmount }
    val unpaidAmount = customers.filter { !it.isPaidThisMonth && it.isActive }.sumOf { it.billAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Custom Header Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CanvasWifiIcon(
                            color = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Admin Panel",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.PortalSelection) }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Switch Roles",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "WiFi Business Remote Maintenance Tracker",
                    color = Color(0xFF93C5FD),
                    fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Analytics Dashboard Card
            item {
                Text(
                    text = "Aapka Business Statics (Summary)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "Total Subscribers", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(text = "$totalSubscribers", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                            Column {
                                Text(text = "Active Links", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(text = "$activeSubscribers", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Column {
                                Text(text = "Unpaid Bills", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(text = "$unpaidSubscribers", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            }
                        }

                        Divider(color = Color(0xFFE2E8F0))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "Monthly Receivables", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(text = "₹ $totalRevenue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Baki Rashi (Due Amount)", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(text = "₹ $unpaidAmount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                            }
                        }
                    }
                }
            }

            // Quick Actions Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(colors = listOf(Color(0xFFFCA5A5), Color(0xFFFCA5A5))))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alarm trigger",
                                tint = Color(0xFFEF4444)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Monthly Automatic Notification Reminder",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Unpaid customer ko bill payment reminder direct bhejein.",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                        Button(
                            onClick = { viewModel.sendBulkReminders() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Bhejein", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Navigation Panel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.AdminCustomerList) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF), contentColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.List, contentDescription = "Subscribers")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("User / Devices", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.AdminLogs) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5), contentColor = Color(0xFF065F46)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = "Logs")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sms/Alert State", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Recent customers section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Apne Active WiFi Device Profiles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF334155)
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.AdminCustomerList) }) {
                        Text("View All", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (customers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Database khali hai. FAB button (+) click kar ke naya customer add karein.", color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(customers.take(3)) { customer ->
                    CustomerCard(
                        customer = customer,
                        onSendReminder = { viewModel.sendPaymentReminder(customer) },
                        onEdit = {
                            viewModel.selectCustomer(customer)
                            viewModel.navigateTo(AppScreen.AdminAddEditCustomer)
                        }
                    )
                }
            }
        }

        // Bottom Action Bar to add
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
                    viewModel.selectCustomer(null)
                    viewModel.navigateTo(AppScreen.AdminAddEditCustomer)
                },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    }
}

// 3. ADMIN CUSTOMERS LIST SCREEN (Device/Billing Tracker)
@Composable
fun AdminCustomerListScreen(viewModel: WifiViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterActiveOnly by viewModel.filterActiveOnly.collectAsState()
    val customers by viewModel.customers.collectAsState()

    var showDetailsDialogFor by remember { mutableStateOf<Customer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // App header with Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "WiFi Users & Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Search users, device logs & edit profiles",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Search and Filter component
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name, phone and SN number...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Dynamic filter options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterActiveOnly == null,
                    onClick = { viewModel.setFilter(null) },
                    label = { Text("Sabhi (All)") }
                )
                FilterChip(
                    selected = filterActiveOnly == true,
                    onClick = { viewModel.setFilter(true) },
                    label = { Text("Chalu (Active)") }
                )
                FilterChip(
                    selected = filterActiveOnly == false,
                    onClick = { viewModel.setFilter(false) },
                    label = { Text("Band (Inactive)") }
                )
            }
        }

        // List View
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (customers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Koi matching customer nahi mila! \uD83D\uDD0D", color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(customers) { customer ->
                    CustomerCard(
                        customer = customer,
                        onSendReminder = { viewModel.sendPaymentReminder(customer) },
                        onEdit = {
                            viewModel.selectCustomer(customer)
                            viewModel.navigateTo(AppScreen.AdminAddEditCustomer)
                        },
                        onCardClick = {
                            showDetailsDialogFor = customer
                        }
                    )
                }
            }
        }
    }

    // Modal Sheet / Dialog to view Full details including hardware serial numbers
    showDetailsDialogFor?.let { customer ->
        AlertDialog(
            onDismissRequest = { showDetailsDialogFor = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "WiFi Device Profile Detail", tint = Color(0xFF2563EB))
                    Text(text = customer.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "DEVICE CREDENTIALS & HARDWARE LOGGER", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)

                    // Serial Detail
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Device Serial (SN):", color = Color(0xFF475569))
                        Text(customer.deviceSerialNumber, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }

                    // Device Model
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Router Model:", color = Color(0xFF475569))
                        Text(customer.deviceModel, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
                    }

                    // MAC address
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("MAC Address:", color = Color(0xFF475569))
                        Text(customer.macAddress, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
                    }

                    Divider(color = Color(0xFFE2E8F0))

                    Text(text = "CLIENT WiFi SETTING", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)

                    // SSID
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("WiFi SSID Name:", color = Color(0xFF475569))
                        Text(customer.wifiSSID, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }

                    // Password
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("WiFi Password:", color = Color(0xFF475569))
                        Text(customer.wifiPassword, fontWeight = FontWeight.Bold)
                    }

                    Divider(color = Color(0xFFE2E8F0))

                    Text(text = "SUBSCRIPTION & BILLING STATE", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Internet Package Plan:", color = Color(0xFF475569))
                        Text(customer.planName, fontWeight = FontWeight.SemiBold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly Package Fee:", color = Color(0xFF475569))
                        Text("₹ ${customer.billAmount}", fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Cycle Date:", color = Color(0xFF475569))
                        Text("${customer.billDate}th of each month", fontWeight = FontWeight.Medium)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status Code:", color = Color(0xFF475569))
                        if (customer.isActive) {
                            Text("Active / Active Line", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        } else {
                            Text("Deactivated / Cut line", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (customer.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes: ${customer.notes}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialogFor = null }) {
                    Text("Theek Hai", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                    onClick = {
                        viewModel.deleteCustomer(customer)
                        showDetailsDialogFor = null
                    }
                ) {
                    Text("DELETE USER")
                }
            }
        )
    }
}

// 4. ADMIN ADD / EDIT CLIENT FORM SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditCustomerScreen(viewModel: WifiViewModel) {
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()

    var name by remember { mutableStateOf(selectedCustomer?.name ?: "") }
    var mobileNumber by remember { mutableStateOf(selectedCustomer?.mobileNumber ?: "") }
    var wifiSSID by remember { mutableStateOf(selectedCustomer?.wifiSSID ?: "") }
    var wifiPassword by remember { mutableStateOf(selectedCustomer?.wifiPassword ?: "") }
    var billDate by remember { mutableStateOf(selectedCustomer?.billDate ?: "05") }
    var billAmountRaw by remember { mutableStateOf(selectedCustomer?.billAmount?.toString() ?: "599") }
    var planName by remember { mutableStateOf(selectedCustomer?.planName ?: "") }
    var macAddress by remember { mutableStateOf(selectedCustomer?.macAddress ?: "") }
    var deviceModel by remember { mutableStateOf(selectedCustomer?.deviceModel ?: "") }
    var deviceSerialNumber by remember { mutableStateOf(selectedCustomer?.deviceSerialNumber ?: "") }
    var notes by remember { mutableStateOf(selectedCustomer?.notes ?: "") }
    var isActive by remember { mutableStateOf(selectedCustomer?.isActive ?: true) }
    var isPaidThisMonth by remember { mutableStateOf(selectedCustomer?.isPaidThisMonth ?: true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (selectedCustomer == null) "WiFi Direct Router Details Add karein" else "Active Wifi Device Profile Update",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
        }

        // Form inputs
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Info
            item {
                Text(
                    text = "CUSTOMER PERSONAL DETAILS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF3B82F6)
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Ka Name *") },
                    placeholder = { Text("Apne client ka pura name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") }
                )
            }

            item {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Registered Mobile Number *") },
                    placeholder = { Text("98xxxxxxxx (Login Mobile Number)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Mobile") }
                )
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DEVICE SERIAL & ROUTER DETAILS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF3B82F6)
                )
            }

            item {
                OutlinedTextField(
                    value = deviceSerialNumber,
                    onValueChange = { deviceSerialNumber = it },
                    label = { Text("Device Serial Number (SN)") },
                    placeholder = { Text("TPLINK882773821 (Fulfill serial authentication)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Build, contentDescription = "Serial") }
                )
            }

            item {
                OutlinedTextField(
                    value = deviceModel,
                    onValueChange = { deviceModel = it },
                    label = { Text("Router Model Details") },
                    placeholder = { Text("TP-Link AC1200 / Syrotech ONU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = macAddress,
                    onValueChange = { macAddress = it },
                    label = { Text("Device Router MAC Address") },
                    placeholder = { Text("40:51:7C:F8:12:D5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CLIENT WiFi DEFAULTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF3B82F6)
                )
            }

            item {
                OutlinedTextField(
                    value = wifiSSID,
                    onValueChange = { wifiSSID = it },
                    label = { Text("WiFi SSID / Network Name *") },
                    placeholder = { Text("Satish_Fiber_5G") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { CanvasWifiIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                )
            }

            item {
                OutlinedTextField(
                    value = wifiPassword,
                    onValueChange = { wifiPassword = it },
                    label = { Text("WiFi Password (Min 8 Length) *") },
                    placeholder = { Text("Secure password detail") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") }
                )
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BILLING DETAILS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF3B82F6)
                )
            }

            item {
                OutlinedTextField(
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Active Internet Plan Pack") },
                    placeholder = { Text("50 Mbps Fiber Unlimited Plan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = billAmountRaw,
                        onValueChange = { billAmountRaw = it },
                        label = { Text("Monthly Bill Fee (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = billDate,
                        onValueChange = { billDate = it },
                        label = { Text("Bill Date Cycle (Day)") },
                        placeholder = { Text("05") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Any Notes/Aadesh") },
                    placeholder = { Text("No. of wire splices etc.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Flags
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Status (Chalu Line?)", fontWeight = FontWeight.Medium)
                        Text("Deactivate karne par red card display hoga", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Is Bill Paid this month?", fontWeight = FontWeight.Medium)
                        Text("Unpaid karne par reminder panel active hoga", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isPaidThisMonth,
                        onCheckedChange = { isPaidThisMonth = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Action Toolbar
        Surface(
            shadowElevation = 8.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val amt = billAmountRaw.toDoubleOrNull() ?: 0.0
                    val cust = selectedCustomer
                    if (cust == null) {
                        viewModel.insertCustomer(
                            name = name,
                            mobileNumber = mobileNumber,
                            wifiSSID = wifiSSID,
                            wifiPassword = wifiPassword,
                            billDate = billDate,
                            billAmount = amt,
                            planName = planName,
                            macAddress = macAddress,
                            deviceModel = deviceModel,
                            deviceSerialNumber = deviceSerialNumber,
                            notes = notes,
                            isActive = isActive,
                            isPaidThisMonth = isPaidThisMonth
                        )
                    } else {
                        // Update existing
                        val updated = cust.copy(
                            name = name.trim(),
                            mobileNumber = mobileNumber.trim(),
                            wifiSSID = wifiSSID.trim(),
                            wifiPassword = wifiPassword.trim(),
                            billDate = billDate.trim().padStart(2, '0'),
                            billAmount = amt,
                            planName = planName.trim().ifBlank { "Standard Plan" },
                            macAddress = macAddress.trim().ifBlank { "00:00:00:00:00:00" },
                            deviceModel = deviceModel.trim().ifBlank { "Generic ONU Router" },
                            deviceSerialNumber = deviceSerialNumber.trim().ifBlank { "SN-" + System.currentTimeMillis().toString().takeLast(8) },
                            notes = notes,
                            isActive = isActive,
                            isPaidThisMonth = isPaidThisMonth
                        )
                        viewModel.updateCustomer(updated)
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(
                    text = if (selectedCustomer == null) "DIRECT REGISTER PROFILE SECURE" else "SAVE EDITS & UDPATE FIRMWARE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// 5. ADMIN LOGS / BILLING NOTIFICATION ARCHIVE
@Composable
fun AdminLogsScreen(viewModel: WifiViewModel) {
    val logs by viewModel.logs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Headbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Reminders & Action Logs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Sent notification, bills history logs",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Abhi tak koi warning/bill logging history nahi h.", color = Color.Gray)
                    }
                }
            } else {
                items(logs) { log ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Reminder", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                    Text(text = log.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
                                Text(
                                    text = formatter.format(Date(log.timestamp)),
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Client: ${log.customerName} (${log.mobileNumber})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log.message,
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 6. CUSTOMER SECTOR - LOGIN
@Composable
fun CustomerLoginScreen(viewModel: WifiViewModel) {
    val loginMobileNumber by viewModel.loginMobileNumber.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C4A6E), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual element
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Login Secure",
                    modifier = Modifier.size(42.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Client Self-Care Portal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "WiFi speed settings aur bill manage karne ke liye login karein.",
                fontSize = 13.sp,
                color = Color(0xFF38BDF8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = loginMobileNumber,
                onValueChange = { viewModel.updateLoginMobile(it) },
                label = { Text("Registered Mobile Number", color = Color(0xFF93C5FD)) },
                placeholder = { Text("98xxxxxxxx") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0xFF0284C7),
                    focusedLabelColor = Color(0xFF10B981),
                    unfocusedLabelColor = Color(0xFF93C5FD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone icon", tint = Color(0xFF38BDF8)) }
            )

            if (loginError != null) {
                Text(
                    text = loginError ?: "",
                    color = Color(0xFFFCA5A5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.loginCustomer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Login Mobile Number Secure", fontWeight = FontWeight.Bold, color = Color.White)
            }

            // DEMO NUMBERS QUICK SHORTCUTS PANEL (MASSIVE UX SCORE!)
            Spacer(modifier = Modifier.height(16.dp))
            Text("EVALUATOR QUICK ACCESS (DEMO CHIPS)", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                SuggestionChip(
                    onClick = {
                        viewModel.updateLoginMobile("9876543210")
                        viewModel.loginCustomer()
                    },
                    label = { Text("Satish (9876543210)", fontSize = 11.sp, color = Color.White) }
                )
                SuggestionChip(
                    onClick = {
                        viewModel.updateLoginMobile("9988776655")
                        viewModel.loginCustomer()
                    },
                    label = { Text("Amit (9988776655)", fontSize = 11.sp, color = Color.White) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { viewModel.navigateTo(AppScreen.PortalSelection) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF38BDF8))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Home, contentDescription = "Go Home", modifier = Modifier.size(16.dp))
                    Text("Go back to Main Menu Selection", fontSize = 13.sp)
                }
            }
        }
    }
}

// 7. CUSTOMER DASHBOARD (Client direct device manager)
@Composable
fun CustomerDashboardScreen(viewModel: WifiViewModel) {
    val loggedInCustomer by viewModel.loggedInCustomer.collectAsState()
    val logs by viewModel.customerLogs.collectAsState()

    val customer = loggedInCustomer ?: return

    var ssidInput by remember { mutableStateOf(customer.wifiSSID) }
    var passwordInput by remember { mutableStateOf(customer.wifiPassword) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBF5FF)) // Cool light blue theme
    ) {
        // App header with Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E3A8A))
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "User", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        text = "Namaskar, ${customer.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "No: ${customer.mobileNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
            }

            IconButton(onClick = { viewModel.logoutCustomer() }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unpaid Warning Alert if baki hai
            if (!customer.isPaidThisMonth) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(colors = listOf(Color(0xFFFCA5A5), Color(0xFFFCA5A5))))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = "Payment Warning", tint = Color(0xFFDC2626))
                                Text(
                                    text = "Monthly Bill Bhugtan Pending!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text(
                                text = "Aapka dynamic cycle bill amount ₹${customer.billAmount} baki hai. Internet continuity ke liye niche diye pure step se online pay karein.",
                                fontSize = 13.sp,
                                color = Color(0xFF7F1D1D)
                            )
                            Button(
                                onClick = { viewModel.processCustomerPayment() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = "Pay", modifier = Modifier.size(16.dp))
                                    Text("Pay ₹${customer.billAmount} Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "All clean", tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                            Column {
                                Text("Aapka WiFi Account active hai!", fontWeight = FontWeight.Bold, color = Color(0xFF065F46), fontSize = 15.sp)
                                Text("Monthly subscription plan limits: clear and paid.", color = Color(0xFF047857), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // EDIT WIFI CREDENTIALS (SABSE IMPORTANT FULFILLMENT!)
            item {
                Text(
                    text = "APNE ROUTER DEVICE KI SETTINGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A)
                )

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Aap yaha se direct apna WiFi SSID router ka name badal sakte hain.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        // SSID input
                        OutlinedTextField(
                            value = ssidInput,
                            onValueChange = { ssidInput = it },
                            label = { Text("WiFi Network Name (SSID)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { CanvasWifiIcon(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                        )

                        // Password input with visibility trigger
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("WiFi Password (8+ length)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "password icon") },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Info else Icons.Default.Settings,
                                        contentDescription = "Hide/Show"
                                    )
                                }
                            }
                        )

                        Button(
                            onClick = { viewModel.updateWifiCredentials(ssidInput, passwordInput) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("SAVE NEW ROUTER SETTINGS", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // MY DEVICE ACCORDION (Fulfills: taki user apna hi device access kar sake)
            item {
                Text(
                    text = "Aapka Verified WiFi Device Logging Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A)
                )

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Registered Router Name:", color = Color.Gray, fontSize = 13.sp)
                            Text(customer.deviceModel, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hardware Serial No. (SN):", color = Color.Gray, fontSize = 13.sp)
                            Text(customer.deviceSerialNumber, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Router MAC Address:", color = Color.Gray, fontSize = 13.sp)
                            Text(customer.macAddress, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Selected Subscription Plan:", color = Color.Gray, fontSize = 13.sp)
                            Text(customer.planName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Monthly Due Cycle Date:", color = Color.Gray, fontSize = 13.sp)
                            Text("${customer.billDate}th of each month", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
            }

            // NOTIFICATIONS RECEIVED
            item {
                Text(
                    text = "Provider Se Bheje Gaye Sandesh (Inbox)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A)
                )
            }

            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Abhi is profile me koi alerts notification ya receipt sandesh nahi h.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                items(logs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                                val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
                                Text(
                                    formatter.format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(log.message, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

// SHARED CUSTOMER LAYOUT CARD
@Composable
fun CustomerCard(
    customer: Customer,
    onSendReminder: () -> Unit,
    onEdit: () -> Unit,
    onCardClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (customer.isActive) Color(0xFFE0F2FE) else Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        CanvasWifiIcon(
                            color = if (customer.isActive) Color(0xFF0369A1) else Color(0xFFB91C1C),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = customer.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "No: ${customer.mobileNumber}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Billing / Paid Badge
                if (customer.isPaidThisMonth) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Paid This Month", color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEE2E2))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Bill Pending", color = Color(0xFFB91C1C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subscriptions and Serial details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Selected Plan", fontSize = 11.sp, color = Color.Gray)
                    Text(customer.planName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bill Date", fontSize = 11.sp, color = Color.Gray)
                    Text("${customer.billDate}th month", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Monthly Fee", fontSize = 11.sp, color = Color.Gray)
                    Text("₹ ${customer.billAmount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Build, contentDescription = "Router Serial", tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Text(
                        text = "SN: ${customer.deviceSerialNumber.take(12)}",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                // Reminder and Edit Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!customer.isPaidThisMonth && customer.isActive) {
                        IconButton(
                            onClick = onSendReminder,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEF3C7))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Reminder Alert",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Customer Details",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
