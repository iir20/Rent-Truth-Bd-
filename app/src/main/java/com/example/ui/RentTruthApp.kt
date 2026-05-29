package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = LightText),
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryEmerald,
            unfocusedBorderColor = GreyText.copy(0.3f),
            focusedLabelColor = PrimaryEmerald,
            unfocusedLabelColor = GreyText,
            cursorColor = PrimaryEmerald
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun RevenueBarMetric(label: String, amount: Int, pct: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 9.5.sp, color = LightText, fontWeight = FontWeight.Bold)
            Text("${amount} BDT", fontSize = 10.sp, color = SoftEmerald, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardSlate.copy(0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = pct.coerceIn(0.01f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryEmerald, AccentTeal)
                        )
                    )
            )
        }
        Spacer(Modifier.height(6.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentTruthApp(
    viewModel: RentViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val negotiations by viewModel.negotiations.collectAsState()
    val roommateRequests by viewModel.roommateRequests.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val escrowDeposits by viewModel.escrowDeposits.collectAsState()

    val areaFilter by viewModel.selectedAreaFilter.collectAsState()
    val budgetFilter by viewModel.maxBudgetFilter.collectAsState()
    val typeFilter by viewModel.selectedTypeFilter.collectAsState()
    val studentMode by viewModel.studentMode.collectAsState()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Phase 3 Flow Collections
    val notifications by viewModel.notifications.collectAsState()
    val agreements by viewModel.agreements.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val buildings by viewModel.buildings.collectAsState()
    val maintenanceTickets by viewModel.maintenanceTickets.collectAsState()
    val visitorLogs by viewModel.visitorLogs.collectAsState()
    val parkingSlots by viewModel.parkingSlots.collectAsState()
    val fraudReports by viewModel.fraudReports.collectAsState()

    val lowBandwidthMode by viewModel.lowBandwidthMode.collectAsState()
    val offlineDraftsSaved by viewModel.offlineDraftsSaved.collectAsState()
    val imageCompressionActive by viewModel.imageCompressionActive.collectAsState()
    val bilingualBanglaFirst by viewModel.bilingualBanglaFirst.collectAsState()
    val voiceGuidanceActive by viewModel.voiceGuidanceActive.collectAsState()

    var showNotificationDialog by remember { mutableStateOf(false) }

    var activeTab by remember { mutableStateOf("Listings") } // Listings, Map, AI Advisor, Roommates, Chat Hub, Account
    var selectedListingForDetail by remember { mutableStateOf<RentListing?>(null) }
    var showAddListingDialog by remember { mutableStateOf(false) }
    var showNegotiateDialog by remember { mutableStateOf<RentListing?>(null) }
    var showAddRoommateRequestDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf<RentListing?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (!isLoggedIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavyBg)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            var isRegisterState by remember { mutableStateOf(false) }
            var emailInput by remember { mutableStateOf("") }
            var usernameInput by remember { mutableStateOf("") }
            var phoneInput by remember { mutableStateOf("") }
            var passwordInput by remember { mutableStateOf("") }
            var selectedRoleByInput by remember { mutableStateOf(UserRole.TENANT) }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE1E2E9).copy(0.15f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterState) "Create Account 🛡️" else "Rent Truth BD Sign In Keys",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald
                        )
                    )
                    Text(
                        text = if (isRegisterState) "Join our verified BDT ecosystem" else "Input email or test credentials to login",
                        style = MaterialTheme.typography.bodySmall.copy(color = GreyText),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = GreyText.copy(0.4f),
                            focusedLabelColor = PrimaryEmerald
                        )
                    )

                    if (!isRegisterState) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val isAdminMode = emailInput.trim().lowercase() == "admin@renttruthbd.com"
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(if (isAdminMode) "Admin Security Password 🔑" else "Security Password (Optional)") },
                            modifier = Modifier.fillMaxWidth().testTag("password_input"),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isAdminMode) ErrorCrimson else PrimaryEmerald,
                                unfocusedBorderColor = if (isAdminMode) ErrorCrimson.copy(0.4f) else GreyText.copy(0.4f),
                                focusedLabelColor = if (isAdminMode) ErrorCrimson else PrimaryEmerald
                            )
                        )
                    }

                    if (isRegisterState) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Full Name (e.g. Ratul Ahmed)") },
                            modifier = Modifier.fillMaxWidth().testTag("name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Bangladeshi Phone (01*******)") },
                            modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Select Your Ecosystem Role:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = LightText),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            UserRole.values().forEach { r ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedRoleByInput == r) PrimaryEmerald.copy(0.15f) else CardSlate
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRoleByInput = r }
                                        .border(
                                            1.dp,
                                            if (selectedRoleByInput == r) PrimaryEmerald else GreyText.copy(0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = when(r) {
                                            UserRole.TENANT -> "Tenant"
                                            UserRole.OWNER -> "Owner"
                                            UserRole.OFFICE -> "Office"
                                            UserRole.BROKER -> "Broker"
                                            UserRole.SYSTEM -> "System"
                                        },
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedRoleByInput == r) PrimaryEmerald else LightText,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        val feeInfo = when (selectedRoleByInput) {
                            UserRole.OWNER -> "🏡 Owner Account Cost: 200 BDT (Registration Server Fee)"
                            UserRole.BROKER -> "⚠️ Broker Professional Licensing Charge: 500 BDT"
                            UserRole.OFFICE -> "🏢 Commercial / Corporate Member Registration Fee: 300 BDT"
                            UserRole.SYSTEM -> "🧠 Admin Setup (Unrestricted access to revenues)"
                            else -> "🎓 Students / Tenants registration is 100% FREE"
                        }
                        Text(
                            text = feeInfo,
                            fontSize = 10.sp,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (emailInput.isBlank()) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Email cannot be empty!") }
                                return@Button
                            }

                            if (isRegisterState) {
                                if (usernameInput.isBlank()) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Name cannot be empty!") }
                                    return@Button
                                }
                                val result = viewModel.register(usernameInput, emailInput, phoneInput, selectedRoleByInput)
                                coroutineScope.launch { snackbarHostState.showSnackbar(result) }
                            } else {
                                val success = viewModel.login(emailInput, passwordInput)
                                if (success) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Welcome back!") }
                                } else {
                                    val isAd = emailInput.trim().lowercase() == "admin@renttruthbd.com"
                                    val msg = if (isAd) "Access Denied: Incorrect Admin Security Password!" else "User not found! Toggle 'Create Account' below to join."
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        modifier = Modifier.fillMaxWidth().testTag("auth_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isRegisterState) "Create Account & Charge" else "Validate Account Keys")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { isRegisterState = !isRegisterState }) {
                        Text(
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            text = if (isRegisterState) "Already have credentials? Sign In" else "New? Register Account with BDT 800 Welcome Bonus",
                            color = AccentTeal
                        )
                    }

                    if (!RentViewModel.PRODUCTION_MODE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Demo Profiles: admin@renttruthbd.com | rahman@landlord.com | ratul@school.edu | salim@broker.com",
                            fontSize = 9.sp,
                            color = GreyText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    } else {
        // Navigation and Edge-to-Edge handling
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Rent Truth BD",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryEmerald
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E)) // bg-green-500
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified Ecosystem",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF44474E)
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        // Live Notification Bell with Glow Badge
                        val unreadCount = notifications.filter { !it.isRead }.size
                        IconButton(
                            onClick = { showNotificationDialog = true },
                            modifier = Modifier.padding(end = 4.dp).testTag("notif_bell_btn")
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (unreadCount > 0) PrimaryEmerald else Color.White
                                )
                                if (unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(ErrorCrimson),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$unreadCount",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Role Switcher Dropdown Pill
                        var expandedRoleMenu by remember { mutableStateOf(false) }
                        val isAdminOnAccount = currentUser?.isAdmin == true || currentUser?.role == UserRole.SYSTEM
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (currentRole) {
                                    UserRole.TENANT -> PrimaryEmerald.copy(0.15f)
                                    UserRole.OWNER -> InfoSky.copy(0.15f)
                                    UserRole.OFFICE -> AccentTeal.copy(0.15f)
                                    UserRole.BROKER -> WarningAmber.copy(0.15f)
                                    UserRole.SYSTEM -> ErrorCrimson.copy(0.15f)
                                }
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .then(
                                    if (isAdminOnAccount) {
                                        Modifier.clickable { expandedRoleMenu = true }
                                    } else {
                                        Modifier
                                    }
                                )
                                .border(
                                    1.dp,
                                    when (currentRole) {
                                        UserRole.TENANT -> PrimaryEmerald
                                        UserRole.OWNER -> InfoSky
                                        UserRole.OFFICE -> AccentTeal
                                        UserRole.BROKER -> WarningAmber
                                        UserRole.SYSTEM -> ErrorCrimson
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = when (currentRole) {
                                        UserRole.TENANT -> Icons.Default.Person
                                        UserRole.OWNER -> Icons.Default.Home
                                        UserRole.OFFICE -> Icons.Default.Build
                                        UserRole.BROKER -> Icons.Default.Warning
                                        UserRole.SYSTEM -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = "Role Mode",
                                    tint = when (currentRole) {
                                        UserRole.TENANT -> PrimaryEmerald
                                        UserRole.OWNER -> InfoSky
                                        UserRole.OFFICE -> AccentTeal
                                        UserRole.BROKER -> WarningAmber
                                        UserRole.SYSTEM -> ErrorCrimson
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentRole.name + if (!isAdminOnAccount) " 🔒" else "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (currentRole) {
                                        UserRole.TENANT -> PrimaryEmerald
                                        UserRole.OWNER -> InfoSky
                                        UserRole.OFFICE -> AccentTeal
                                        UserRole.BROKER -> WarningAmber
                                        UserRole.SYSTEM -> ErrorCrimson
                                    }
                                )
                            }
                        }

                        if (isAdminOnAccount) {
                            DropdownMenu(
                                expanded = expandedRoleMenu,
                                onDismissRequest = { expandedRoleMenu = false }
                            ) {
                                UserRole.values().forEach { role ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = when(role) {
                                                    UserRole.TENANT -> "🎓 Student / Tenant"
                                                    UserRole.OWNER -> "🏡 House Owner"
                                                    UserRole.OFFICE -> "🏢 Office / Corporate"
                                                    UserRole.BROKER -> "⚠ Broker Account"
                                                    UserRole.SYSTEM -> "🧠 AI Trust Moderator"
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        onClick = {
                                            viewModel.setRole(role)
                                            expandedRoleMenu = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Switched to ${role.name} Console"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepNavyBg
                    )
                )
            },
            bottomBar = {
            NavigationBar(
                containerColor = CardSlate,
                tonalElevation = 2.dp,
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = Color(0xFFE1E2E9),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            ) {
                NavigationBarItem(
                    selected = activeTab == "Listings",
                    onClick = {
                        activeTab = "Listings"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Listings", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "Map",
                    onClick = {
                        activeTab = "Map"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                    label = { Text("Map View", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "AI Advisor",
                    onClick = {
                        activeTab = "AI Advisor"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = "AI") },
                    label = { Text("AI Advisor", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "Roommates",
                    onClick = {
                        activeTab = "Roommates"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Roommates") },
                    label = { Text("Roommates", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "Chat Hub",
                    onClick = {
                        activeTab = "Chat Hub"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.Email, contentDescription = "Chats") },
                    label = { Text("Chat Hub", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "Account",
                    onClick = {
                        activeTab = "Account"
                        selectedListingForDetail = null
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                    label = { Text("Account", fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryEmerald,
                        selectedTextColor = PrimaryEmerald,
                        indicatorColor = PrimaryEmerald.copy(0.15f),
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(DeepNavyBg)
        ) {
            when (activeTab) {
                "Listings" -> {
                    var listingsTab by remember { mutableStateOf("Rooms") }
                    if (selectedListingForDetail != null) {
                        ListingDetailView(
                            listing = selectedListingForDetail!!,
                            allReviews = allReviews,
                            negotiations = negotiations,
                            currentRole = currentRole,
                            onBack = { selectedListingForDetail = null },
                            onNegotiate = { showNegotiateDialog = it },
                            onReport = {
                                viewModel.reportListing(it.id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Listing reported. AI Moderator notified.")
                                }
                            },
                            onAddReview = { showReviewDialog = it },
                            onAIScan = {
                                viewModel.runAIScamScanOnListing(it.id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("AI Scanner is matching prices & metadata...")
                                }
                            },
                            onStartEscrow = { l, amt ->
                                viewModel.startEscrow(l.id, l.title, amt)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Escrow Security Lock activated for ${l.area}")
                                }
                            },
                            viewModel = viewModel,
                            onChatClick = { activeTab = "Chat Hub" }
                        )
                    } else {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { listingsTab = "Rooms" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (listingsTab == "Rooms") PrimaryEmerald else CardSlate
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🏠 Rent Flats & Rooms", fontSize = 11.sp, color = Color.White)
                                }
                                Button(
                                    onClick = { listingsTab = "Escrows" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (listingsTab == "Escrows") PrimaryEmerald else CardSlate
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🔒 Escrow Lease Locks", fontSize = 11.sp, color = Color.White)
                                }
                            }

                            if (listingsTab == "Rooms") {
                                ListingsDashboard(
                                    listings = listings,
                                    currentRole = currentRole,
                                    areaFilter = areaFilter,
                                    budgetFilter = budgetFilter,
                                    typeFilter = typeFilter,
                                    studentMode = studentMode,
                                    onAreaChange = { viewModel.setAreaFilter(it) },
                                    onBudgetChange = { viewModel.setMaxBudget(it) },
                                    onTypeChange = { viewModel.setTypeFilter(it) },
                                    onToggleStudent = { viewModel.toggleStudentMode() },
                                    onSelectListing = { selectedListingForDetail = it },
                                    onAddListingClick = { showAddListingDialog = true },
                                    viewModel = viewModel,
                                    negotiations = negotiations,
                                    onRespondNegotiation = { id, stat -> viewModel.respondToNegotiation(id, stat) }
                                )
                            } else {
                                EscrowModuleView(
                                    deposits = escrowDeposits,
                                    onUpdateStatus = { id, stat -> viewModel.updateEscrowStatus(id, stat) },
                                    currentRole = currentRole
                                )
                            }
                        }
                    }
                }
                "Map" -> {
                    RealBangladeshMapView(
                        listings = listings,
                        onSelectListing = {
                            selectedListingForDetail = it
                            activeTab = "Listings"
                        }
                    )
                }
                "AI Advisor" -> {
                    AdvisorView(
                        conversation = viewModel.advisorConversation.collectAsState().value,
                        isLoading = viewModel.isAdvisorLoading.collectAsState().value,
                        onAsk = { viewModel.askAdvisor(it) }
                    )
                }
                "Roommates" -> {
                    RoommateFinderView(
                        requests = roommateRequests,
                        onAddRequest = { showAddRoommateRequestDialog = true }
                    )
                }
                "Chat Hub" -> {
                    ChatHubView(
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope
                    )
                }
                "Account" -> {
                    AccountCenterView(
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope
                    )
                }
            }
        }
    }

    // Role-Based Add Listing Input Dialog / Modal Sheet
    if (showAddListingDialog) {
        val areaList = listOf("Dhanmondi", "Mirpur", "Gulshan", "Uttara", "Bashundhara", "Banani")
        val typeList = listOf("Bachelor", "Family", "Sublet", "Office")

        val title by viewModel.inputTitle.collectAsState()
        val area by viewModel.inputArea.collectAsState()
        val address by viewModel.inputAddress.collectAsState()
        val rent by viewModel.inputRent.collectAsState()
        val type by viewModel.inputType.collectAsState()
        val electricity by viewModel.inputElectricity.collectAsState()
        val water by viewModel.inputWater.collectAsState()
        val deposit by viewModel.inputDeposit.collectAsState()
        val videoUrl by viewModel.inputVideoUrl.collectAsState()
        val hasVideo by viewModel.hasVideoWalkthrough.collectAsState()
        val isVerifiedOwnership by viewModel.isVerifiedOwner.collectAsState()

        AlertDialog(
            onDismissRequest = { showAddListingDialog = false },
            title = {
                Text(
                    text = "Post Room / Property",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        if (currentRole == UserRole.BROKER) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(10.dp)) {
                                    Icon(Icons.Default.Warning, contentDescription = "Broker Note", tint = WarningAmber)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Warning: You are in Broker Mode. 'I am Broker' tag will be permanently attached. Listing quote 1/3 used. Multi-brokers quota is regulated.",
                                        fontSize = 11.sp,
                                        color = WarningAmber
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { viewModel.inputTitle.value = it },
                            label = { Text("Title (e.g., Safe Sublet Dhanmondi)") },
                            modifier = Modifier.fillMaxWidth().testTag("listing_title_input")
                        )
                    }
                    item {
                        Text("Area Location", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            areaList.forEach { a ->
                                FilterChip(
                                    selected = area == a,
                                    onClick = { viewModel.inputArea.value = a },
                                    label = { Text(a, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { viewModel.inputAddress.value = it },
                            label = { Text("Exact Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = rent,
                            onValueChange = { viewModel.inputRent.value = it },
                            label = { Text("Rent Amount (BDT/month)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("listing_rent_input")
                        )
                    }
                    item {
                        Text("Property Type", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            typeList.forEach { t ->
                                FilterChip(
                                    selected = type == t,
                                    onClick = { viewModel.inputType.value = t },
                                    label = { Text(t, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = electricity,
                            onValueChange = { viewModel.inputElectricity.value = it },
                            label = { Text("Electricity Rules (e.g. Card, Sub-meter)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = water,
                            onValueChange = { viewModel.inputWater.value = it },
                            label = { Text("Water Rules (e.g. 500 fixed)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = deposit,
                            onValueChange = { viewModel.inputDeposit.value = it },
                            label = { Text("Security Deposit Amount (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text("Proof-Based Verifications", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryEmerald)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.hasVideoWalkthrough.value = !hasVideo }
                        ) {
                            Checkbox(checked = hasVideo, onCheckedChange = { viewModel.hasVideoWalkthrough.value = it })
                            Text("Upload walkthrough video (Mandatory for verified icon)", fontSize = 12.sp)
                        }
                        if (hasVideo) {
                            OutlinedTextField(
                                value = videoUrl,
                                onValueChange = { viewModel.inputVideoUrl.value = it },
                                label = { Text("Video Link (Verification Check)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.isVerifiedOwner.value = !isVerifiedOwnership }
                        ) {
                            Checkbox(checked = isVerifiedOwnership, onCheckedChange = { viewModel.isVerifiedOwner.value = it })
                            Text("I certify I am the legal owner (Verify NID / Land Deed)", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createListing { msg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                        showAddListingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("submit_listing_button")
                ) {
                    Text("Submit Real Listing")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddListingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Tenant Negotiation Dialog
    if (showNegotiateDialog != null) {
        val currentListing = showNegotiateDialog!!
        var proposedPrice by remember { mutableStateOf((currentListing.rentAmount - 1000).toString()) }
        var negotiationNotes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNegotiateDialog = null },
            title = {
                Text("Send Rent Offer to ${currentListing.ownerName}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Original Rent: ${currentListing.rentAmount} BDT", color = GreyText)
                    OutlinedTextField(
                        value = proposedPrice,
                        onValueChange = { proposedPrice = it },
                        label = { Text("My Proposal (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = negotiationNotes,
                        onValueChange = { negotiationNotes = it },
                        label = { Text("Message / Reason / Lifestyle info") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = InfoSky.copy(0.12f))
                    ) {
                        Text(
                            "AI Suggestion: For ${currentListing.area}, rents are usually stable. A negotiation of up to 10% is commonly accepted by Direct Owners.",
                            fontSize = 11.sp,
                            color = InfoSky,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = proposedPrice.toIntOrNull() ?: currentListing.rentAmount
                        viewModel.submitNegotiation(currentListing.id, currentListing.title, amt, negotiationNotes)
                        showNegotiateDialog = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Negotiation Offer dispatched securely!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Send Professional Offer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNegotiateDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Tenant Review Dialog
    if (showReviewDialog != null) {
        val targetListing = showReviewDialog!!
        var reviewText by remember { mutableStateOf("") }
        var cleanlinessRating by remember { mutableFloatStateOf(4.0f) }
        var behaviorRating by remember { mutableFloatStateOf(4.0f) }
        var safetyRating by remember { mutableFloatStateOf(4.0f) }
        var utilityRating by remember { mutableFloatStateOf(4.0f) }

        AlertDialog(
            onDismissRequest = { showReviewDialog = null },
            title = {
                Text("Post Truth Review", fontWeight = FontWeight.Bold, color = PrimaryEmerald)
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Add honest feedback on cleanliness, safety, landlord behavior, water & energy consistency. Verified stay checked via system history ledger.", fontSize = 11.sp, color = GreyText)
                    }
                    item {
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("My honest review...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text("Cleanliness rating (1 to 5)", fontSize = 11.sp)
                        Slider(value = cleanlinessRating, onValueChange = { cleanlinessRating = it }, valueRange = 1f..5f, steps = 3)
                    }
                    item {
                        Text("Landlord behavior rating (1 to 5)", fontSize = 11.sp)
                        Slider(value = behaviorRating, onValueChange = { behaviorRating = it }, valueRange = 1f..5f, steps = 3)
                    }
                    item {
                        Text("Safety rating (1 to 5)", fontSize = 11.sp)
                        Slider(value = safetyRating, onValueChange = { safetyRating = it }, valueRange = 1f..5f, steps = 3)
                    }
                    item {
                        Text("Electricity & Water reliability (1 to 5)", fontSize = 11.sp)
                        Slider(value = utilityRating, onValueChange = { utilityRating = it }, valueRange = 1f..5f, steps = 3)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview(
                            targetListing.id, reviewText,
                            cleanlinessRating, behaviorRating, safetyRating, utilityRating
                        )
                        showReviewDialog = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Trust review submitted. Thank you for truth verified contribution!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Roommate Dialog
    if (showAddRoommateRequestDialog) {
        val name by viewModel.rmName.collectAsState()
        val age by viewModel.rmAge.collectAsState()
        val inst by viewModel.rmInstitution.collectAsState()
        val budget by viewModel.rmBudget.collectAsState()
        val area by viewModel.rmArea.collectAsState()
        val lifestyle by viewModel.rmLifestyle.collectAsState()

        AlertDialog(
            onDismissRequest = { showAddRoommateRequestDialog = false },
            title = { Text("Post Roommate Match Alert", fontWeight = FontWeight.Bold, color = PrimaryEmerald) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { viewModel.rmName.value = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = age, onValueChange = { viewModel.rmAge.value = it }, label = { Text("Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inst, onValueChange = { viewModel.rmInstitution.value = it }, label = { Text("University / Office") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = budget, onValueChange = { viewModel.rmBudget.value = it }, label = { Text("Target split budget (BDT)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = lifestyle, onValueChange = { viewModel.rmLifestyle.value = it }, label = { Text("Lifestyle (e.g. non smoker, quiet)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitRoommateRequest()
                        showAddRoommateRequestDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Roommate post is live for matches!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRoommateRequestDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Live Notification Center Dialog
    if (showNotificationDialog) {
        var selectedNotifFilter by remember { mutableStateOf("All") }
        var smsChannelActive by remember { mutableStateOf(true) }
        var emailChannelActive by remember { mutableStateOf(true) }
        var pushChannelActive by remember { mutableStateOf(false) }

        val filteredNotifs = if (selectedNotifFilter == "All") {
            notifications
        } else {
            notifications.filter { it.type.lowercase() == selectedNotifFilter.lowercase() }
        }

        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Column {
                    Text(
                        "🔔 Rent Truth Alerts Tower",
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryEmerald,
                        fontSize = 18.sp
                    )
                    Text(
                        "Bilingual Smart Bangladesh Routing",
                        fontSize = 10.sp,
                        color = GreyText
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Category Chips Bar
                    Text("Filter alerts:", fontSize = 10.sp, color = GreyText, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Agreement", "Booking", "Verification", "Fraud", "RentDue").forEach { cat ->
                            val isSel = selectedNotifFilter == cat
                            Card(
                                modifier = Modifier.clickable { selectedNotifFilter = cat },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) PrimaryEmerald.copy(0.15f) else CardSlate
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isSel) PrimaryEmerald else GreyText.copy(0.2f))
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) PrimaryEmerald else LightText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Notifications Box Scroll
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        if (filteredNotifs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No alerts matched in this category. 🟢", fontSize = 11.sp, color = GreyText)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredNotifs) { item ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val emoji = when (item.type) {
                                                    "Booking" -> "👥"
                                                    "Verification" -> "🛡️"
                                                    "Agreement" -> "📜"
                                                    "RentDue" -> "📄"
                                                    "Fraud" -> "🚨"
                                                    else -> "🔔"
                                                }
                                                Text(
                                                    text = emoji,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        item.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = LightText
                                                    )
                                                    Text(
                                                        item.message,
                                                        fontSize = 10.sp,
                                                        color = GreyText
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { viewModel.dismissNotification(item.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Text("✕", fontSize = 11.sp, color = ErrorCrimson, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = GreyText.copy(0.15f))

                    // Delivery Preferences Routing Center
                    Text("📡 Multiplex Gateway Delivery Routing:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = smsChannelActive, 
                                onCheckedChange = { smsChannelActive = it; viewModel.triggerInAppNotification("Routing Configured", "Multiplex SMS alerts status: $it", "Booking") },
                                colors = CheckboxDefaults.colors(checkedColor = AccentTeal)
                            )
                            Text("SMS (Teletalk/GP)", fontSize = 11.sp, color = LightText)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = emailChannelActive, 
                                onCheckedChange = { emailChannelActive = it; viewModel.triggerInAppNotification("Routing Configured", "Multiplex Email reports status: $it", "Booking") },
                                colors = CheckboxDefaults.colors(checkedColor = AccentTeal)
                            )
                            Text("Email", fontSize = 11.sp, color = LightText)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = pushChannelActive, 
                                onCheckedChange = { pushChannelActive = it; viewModel.triggerInAppNotification("Routing Configured", "Push Service status: $it", "Booking") },
                                colors = CheckboxDefaults.colors(checkedColor = AccentTeal)
                            )
                            Text("Mobile Push Feed", fontSize = 11.sp, color = LightText)
                        }
                        Button(
                            onClick = {
                                viewModel.triggerInAppNotification(
                                    "Visual Diagnostics",
                                    "Ecosystem health is completely sound.",
                                    "Verification"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal.copy(0.2f)),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Ping Gateway 📡", fontSize = 9.sp, color = AccentTeal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Close Core Controller")
                }
            }
        )
    }
}
}

@Composable
fun ListingsDashboard(
    listings: List<RentListing>,
    currentRole: UserRole,
    areaFilter: String,
    budgetFilter: Int,
    typeFilter: String,
    studentMode: Boolean,
    onAreaChange: (String) -> Unit,
    onBudgetChange: (Int) -> Unit,
    onTypeChange: (String) -> Unit,
    onToggleStudent: () -> Unit,
    onSelectListing: (RentListing) -> Unit,
    onAddListingClick: () -> Unit,
    viewModel: RentViewModel,
    negotiations: List<Negotiation>,
    onRespondNegotiation: (Int, String) -> Unit
) {
    val areas = listOf("All", "Dhanmondi", "Mirpur", "Gulshan", "Uttara", "Bashundhara", "Banani")
    val types = listOf("All", "Bachelor", "Family", "Sublet", "Office")

    val selectedDivisionFilter by viewModel.selectedDivisionFilter.collectAsState()
    val selectedDistrictFilter by viewModel.selectedDistrictFilter.collectAsState()

    val filteredListings = listings.filter { listing ->
        val divisionMatch = selectedDivisionFilter == "All" || listing.division.equals(selectedDivisionFilter, ignoreCase = true)
        val districtMatch = selectedDistrictFilter == "All" || listing.district.equals(selectedDistrictFilter, ignoreCase = true)
        val areaMatch = areaFilter == "All" || listing.area.equals(areaFilter, ignoreCase = true)
        val typeMatch = typeFilter == "All" || listing.type.equals(typeFilter, ignoreCase = true)
        val budgetMatch = listing.rentAmount <= budgetFilter
        divisionMatch && districtMatch && areaMatch && typeMatch && budgetMatch
    }

    if (currentRole == UserRole.OWNER) {
        OwnerEcosystemWorkspace(
            listings = listings,
            onSelectListing = onSelectListing,
            onAddListingClick = onAddListingClick,
            viewModel = viewModel,
            negotiations = negotiations,
            onRespondNegotiation = onRespondNegotiation
        )
    } else if (currentRole == UserRole.BROKER) {
        BrokerComplianceWorkspace(
            listings = listings,
            onSelectListing = onSelectListing,
            onAddListingClick = onAddListingClick,
            viewModel = viewModel
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome Header & Profile Summary
            item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Salaam, Welcome back! 🇧🇩",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightText)
                            )
                            Text(
                                "Solving Bangladesh Broker Monopoly Since 2026",
                                style = MaterialTheme.typography.bodySmall.copy(color = GreyText)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Role Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (currentRole == UserRole.OWNER || currentRole == UserRole.BROKER) {
                            Button(
                                onClick = onAddListingClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_listing_trigger"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Post Listing", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = onToggleStudent,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (studentMode) SoftEmerald else CardSlate
                            ),
                            border = BorderStroke(1.dp, if (studentMode) SoftEmerald else GreyText),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Student Mode",
                                tint = if (studentMode) Color.White else GreyText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Student Mode",
                                color = if (studentMode) Color.White else GreyText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Region transparency analytics (Elimination index of brokers)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.7f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📉 Broker Influence & Location Transparency Index", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentTeal)
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = AccentTeal, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Dhanmondi", fontSize = 10.sp, color = GreyText)
                            Text("Direct: 82%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                            Text("Broker Cost: Low", fontSize = 8.sp, color = GreyText)
                        }
                        Column {
                            Text("Mirpur", fontSize = 10.sp, color = GreyText)
                            Text("Direct: 90%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                            Text("Broker Cost: Very Low", fontSize = 8.sp, color = GreyText)
                        }
                        Column {
                            Text("Banani/Gulshan", fontSize = 10.sp, color = GreyText)
                            Text("Direct: 42%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ErrorCrimson)
                            Text("Broker Cost: High", fontSize = 8.sp, color = GreyText)
                        }
                        Column {
                            Text("Bashundhara", fontSize = 10.sp, color = GreyText)
                            Text("Direct: 76%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                            Text("Broker Cost: Medium", fontSize = 8.sp, color = GreyText)
                        }
                    }
                }
            }
        }

        // Landlord-specific Negotiation Panel
        if (currentRole == UserRole.OWNER) {
            val ownerOffers = negotiations.filter { it.status == "Pending" }
            if (ownerOffers.isNotEmpty()) {
                item {
                    Text("📩 Active Tenant Negotiations", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SoftEmerald)
                }
                items(ownerOffers) { offer ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, SoftEmerald)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Offer for: ${offer.listingTitle}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Tenant: ${offer.tenantName}", fontSize = 11.sp, color = GreyText)
                            Text("Proposed Price: ${offer.proposedRent} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = SoftEmerald)
                            if (offer.notes.isNotBlank()) {
                                Text("\"${offer.notes}\"", fontSize = 11.sp, color = GreyText)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onRespondNegotiation(offer.id, "Accepted") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Accept BDT ${offer.proposedRent}", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { onRespondNegotiation(offer.id, "Rejected") },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Counter/Reject", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filters UI Title
        item {
            Column {
                Text(
                    text = "Verify & Discover Rooms",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightText)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Bangladesh Division selector (All of Bangladesh search)
                Text("Select Bangladesh Division (National)", fontSize = 11.sp, color = GreyText)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val divisions = listOf("All", "Dhaka", "Chittagong", "Sylhet", "Rajshahi", "Khulna", "Barisal", "Rangpur", "Mymensingh")
                    divisions.forEach { div ->
                        FilterChip(
                            selected = selectedDivisionFilter == div,
                            onClick = { viewModel.setDivisionFilter(div) },
                            label = { Text(div, fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Area Filter Scroller
                Text("Select Area", fontSize = 11.sp, color = GreyText)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    areas.take(4).forEach { area ->
                        FilterChip(
                            selected = areaFilter == area,
                            onClick = { onAreaChange(area) },
                            label = { Text(area, fontSize = 11.sp) }
                        )
                    }
                    Text("...", color = GreyText, modifier = Modifier.align(Alignment.CenterVertically))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    areas.drop(4).forEach { area ->
                        FilterChip(
                            selected = areaFilter == area,
                            onClick = { onAreaChange(area) },
                            label = { Text(area, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Budget Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Max Rent: $budgetFilter BDT", fontSize = 12.sp, color = LightText)
                    Text("BDT 40,000 max", fontSize = 10.sp, color = GreyText)
                }
                Slider(
                    value = budgetFilter.toFloat(),
                    onValueChange = { onBudgetChange(it.toInt()) },
                    valueRange = 3000f..40000f, // cap at 40k BDT
                    steps = 37
                )
            }
        }

        // Listings List
        if (filteredListings.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Empty", tint = GreyText, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No truth verified listings found.", color = GreyText, textAlign = TextAlign.Center)
                    Text("Try relaxing filters or changing areas.", color = GreyText, fontSize = 11.sp)
                }
            }
        } else {
            items(filteredListings) { listing ->
                ListingCardItem(
                    listing = listing,
                    onClick = { onSelectListing(listing) },
                    onAIScan = { viewModel.runAIScamScanOnListing(listing.id) }
                )
            }
        }
    }
}
}

@Composable
fun OwnerEcosystemWorkspace(
    listings: List<RentListing>,
    onSelectListing: (RentListing) -> Unit,
    onAddListingClick: () -> Unit,
    viewModel: RentViewModel,
    negotiations: List<Negotiation>,
    onRespondNegotiation: (Int, String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val landlordLogs by viewModel.landlordTenantLogs.collectAsState()
    val brokerRelationships by viewModel.brokerPartnerships.collectAsState()
    val socketLogs by viewModel.syncLogs.collectAsState()

    val user = currentUser ?: return
    val ownerListings = listings.filter { !it.isBrokerListing }
    val ownerOffers = negotiations.filter { it.status == "Pending" }

    var workspaceTab by remember { mutableStateOf("Bids") } // "Bids", "Registry", "Brokers", "SyncMonitor"
    var isRegisteringManualTenant by remember { mutableStateOf(false) }

    // Manual Tenant Registration states
    var manualName by remember { mutableStateOf("") }
    var manualPhone by remember { mutableStateOf("") }
    var manualEmail by remember { mutableStateOf("") }
    var manualProperty by remember { mutableStateOf("") }
    var manualRent by remember { mutableStateOf("15000") }
    var manualStart by remember { mutableStateOf("May 2026") }
    var manualDuration by remember { mutableStateOf("12") }

    var searchRenterQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("owner_workspace_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryEmerald.copy(0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "🏡 Landowner operating workspace",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                    )
                    Text(
                        "Track properties, preserve real-time tenant registers, and monitor broker relationships.",
                        color = LightText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Active Listings", fontSize = 8.5.sp, color = GreyText)
                                Text("${ownerListings.size} / 5", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightText)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Secured Deposits", fontSize = 8.5.sp, color = GreyText)
                                Text("${landlordLogs.filter { it.status == "Active Renter" }.sumOf { it.rentAmount }} BDT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Service Class", fontSize = 8.5.sp, color = GreyText)
                                Text(user.subscriptionType.ifBlank { "Free Basic" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }

        // Professional Dashboard sub-routing bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSlate, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "Bids" to "📨 Bids & Posts",
                    "Registry" to "👥 Tenant Register",
                    "Brokers" to "💼 Broker Monitor",
                    "SyncMonitor" to "⚡ Live Sync Logs"
                ).forEach { (key, label) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (workspaceTab == key) PrimaryEmerald else Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { workspaceTab = key }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (workspaceTab == key) Color.White else LightText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (workspaceTab) {
            "Bids" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📬 Live Tenant Bids & Negotiations", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                        Text("${ownerOffers.size} pending", fontSize = 10.sp, color = GreyText)
                    }
                }

                if (ownerOffers.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MailOutline, contentDescription = "Mail", tint = GreyText, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("No active bargaining bids currently.", fontSize = 10.sp, color = GreyText)
                            }
                        }
                    }
                } else {
                    items(ownerOffers) { offer ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, SoftEmerald),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(offer.listingTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                                    Text("Pending Approval", fontSize = 8.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(2.dp))
                                Text("Bidder: ${offer.tenantName} | BDT Proposed: ${offer.proposedRent}", fontSize = 10.sp, color = GreyText)
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Bargaining Rate: ${offer.proposedRent} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = VerifiedGreen)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Button(
                                            onClick = { onRespondNegotiation(offer.id, "Accepted") },
                                            colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Accept", fontSize = 9.sp)
                                        }
                                        Button(
                                            onClick = { onRespondNegotiation(offer.id, "Rejected") },
                                            colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Decline", fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        onClick = onAddListingClick,
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryEmerald, RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Post New Property Entry", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                                Text("Add verified flat, bachelor room, or sublet to ecosystem.", fontSize = 9.sp, color = GreyText)
                            }
                        }
                    }
                }

                item {
                    Text("🏠 My Listed Properties", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText, modifier = Modifier.padding(top = 4.dp))
                }

                if (ownerListings.isEmpty()) {
                    item {
                        Text("No properties posted yet.", fontSize = 10.sp, color = GreyText)
                    }
                } else {
                    items(ownerListings) { listing ->
                        ListingCardItem(
                            listing = listing,
                            onClick = { onSelectListing(listing) },
                            onAIScan = { viewModel.runAIScamScanOnListing(listing.id) }
                        )
                    }
                }
            }

            "Registry" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👥 Digital Property Operating Registry", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                        Button(
                            onClick = { isRegisteringManualTenant = !isRegisteringManualTenant },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(if (isRegisteringManualTenant) "Close Form" else "+ Manual Tenant", fontSize = 9.sp)
                        }
                    }
                }

                if (isRegisteringManualTenant) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AccentTeal.copy(0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Manual Tenant Record (Permanent Archival)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)

                                CustomOutlineTextField(
                                    value = manualName,
                                    onValueChange = { manualName = it },
                                    label = "Tenant Full Name (e.g. Riyad Azim)"
                                )
                                CustomOutlineTextField(
                                    value = manualPhone,
                                    onValueChange = { manualPhone = it },
                                    label = "Tenant WhatsApp Phone"
                                )
                                CustomOutlineTextField(
                                    value = manualEmail,
                                    onValueChange = { manualEmail = it },
                                    label = "Tenant Email Address"
                                )
                                CustomOutlineTextField(
                                    value = manualProperty,
                                    onValueChange = { manualProperty = it },
                                    label = "Ecosystem Property / Unit Name"
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        CustomOutlineTextField(
                                            value = manualRent,
                                            onValueChange = { manualRent = it },
                                            label = "Rent (BDT)"
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        CustomOutlineTextField(
                                            value = manualDuration,
                                            onValueChange = { manualDuration = it },
                                            label = "Duration (Months)"
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (manualName.isNotBlank() && manualPhone.isNotBlank()) {
                                            viewModel.addLandlordTenantLogManual(
                                                manualName,
                                                manualPhone,
                                                manualEmail.ifBlank { "${manualName.lowercase().replace(" ", "")}@domain.com" },
                                                manualProperty.ifBlank { "Dhanmondi Flat 4A" },
                                                manualRent.toIntOrNull() ?: 15000,
                                                manualStart,
                                                manualDuration.toIntOrNull() ?: 12
                                            )
                                            // Reset fields
                                            manualName = ""
                                            manualPhone = ""
                                            manualEmail = ""
                                            manualProperty = ""
                                            isRegisteringManualTenant = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Sign Lease & Archive Tenant Record", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Log Query Search Panel
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardSlate, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = GreyText, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        BasicTextField(
                            value = searchRenterQuery,
                            onValueChange = { searchRenterQuery = it },
                            textStyle = TextStyle(color = LightText, fontSize = 11.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            decorationBox = { innerTextField ->
                                if (searchRenterQuery.isEmpty()) {
                                    Text("Search archives by tenant name or flat...", color = GreyText, fontSize = 11.sp)
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                val filteredLogs = landlordLogs.filter {
                    searchRenterQuery.isEmpty() ||
                            it.tenantName.contains(searchRenterQuery, ignoreCase = true) ||
                            it.propertyTitle.contains(searchRenterQuery, ignoreCase = true)
                }

                if (filteredLogs.isEmpty()) {
                    item {
                        Text("No matching tenant records found in archives.", fontSize = 10.sp, color = GreyText)
                    }
                } else {
                    items(filteredLogs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (log.isArchived) CardSlate.copy(0.4f) else CardSlate
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(log.tenantName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (log.isArchived) GreyText else LightText)
                                            Spacer(Modifier.width(4.dp))
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (log.isArchived) Color.DarkGray else VerifiedGreen.copy(0.15f)
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = log.status,
                                                    fontSize = 7.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (log.isArchived) GreyText else VerifiedGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(log.propertyTitle, fontSize = 10.sp, color = GreyText)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${log.rentAmount} BDT/m", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                                        Text("Trust: ${log.trustScore}/100", fontSize = 8.5.sp, color = if (log.trustScore >= 90) VerifiedGreen else WarningAmber)
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Contact: ${log.tenantPhone} | ${log.tenantEmail}", fontSize = 9.sp, color = GreyText)
                                        Text("Start Date: ${log.startDate} (${log.durationMonths}m term)", fontSize = 9.sp, color = LightText)
                                    }

                                    if (!log.isArchived) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Button(
                                                onClick = { viewModel.updateLandlordTenantStatus(log.id, "Vacated") },
                                                colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                modifier = Modifier.height(20.dp)
                                            ) {
                                                Text("Evict/Vacated", fontSize = 7.5.sp)
                                            }
                                        }
                                    } else {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                                border = BorderStroke(0.5.dp, GreyText)
                                            ) {
                                                Text("🔒 Archived Ledger Permanent", fontSize = 7.5.sp, color = GreyText, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Brokers" -> {
                item {
                    Text("💼 Authorized Broker Assignments & Mappings", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                }

                if (brokerRelationships.isEmpty()) {
                    item {
                        Text("No active broker mappings linked to your properties.", fontSize = 10.sp, color = GreyText)
                    }
                } else {
                    items(brokerRelationships) { bp ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(bp.propertyTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(bp.status, color = VerifiedGreen, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("Broker: Salim (salim@broker.com)", fontSize = 10.sp, color = LightText)
                                Text("Owner Name: ${bp.ownerName} | Split Percent: ${bp.commissionSplitPercent}%", fontSize = 9.sp, color = GreyText)
                                Text("Broker Activity: 🟢 Active deal synchronization established. System tracking.", fontSize = 9.sp, color = AccentTeal)
                            }
                        }
                    }
                }
            }

            "SyncMonitor" -> {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡ Real-Time RentSync Websocket Heartbeat Monitor", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                            Button(
                                onClick = {
                                    viewModel.addSyncLog("[MANUAL TRIGGER] Client ping initialized. Latency request...")
                                    viewModel.addSyncLog("[OK] Packet acknowledged. Latency: 40ms. Client/Server state: Synced.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                modifier = Modifier.height(20.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("PING WEB_SOCKET", fontSize = 7.5.sp)
                            }
                        }
                        Text("Real-time telemetry and database optimistic write ledger ticker:", fontSize = 9.sp, color = GreyText, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }

                items(socketLogs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (log.contains("OK") || log.contains("SYSTEM")) CardSlate else CardSlate.copy(0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                    ) {
                        Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💻", fontSize = 10.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = log,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.contains("[OK]")) VerifiedGreen else if (log.contains("SYSTEM")) InfoSky else LightText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrokerComplianceWorkspace(
    listings: List<RentListing>,
    onSelectListing: (RentListing) -> Unit,
    onAddListingClick: () -> Unit,
    viewModel: RentViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val brokerDeals by viewModel.brokerDeals.collectAsState()
    val partnerships by viewModel.brokerPartnerships.collectAsState()
    val socketLogs by viewModel.syncLogs.collectAsState()

    val user = currentUser ?: return
    val brokerListings = listings.filter { it.isBrokerListing }

    var brokerTab by remember { mutableStateOf("Listings") } // "Listings", "Splits", "Deals", "Sync"
    var showAddPartnership by remember { mutableStateOf(false) }
    var showAddDeal by remember { mutableStateOf(false) }

    // Relationship states
    var ownerNameInput by remember { mutableStateOf("") }
    var ownerPhoneInput by remember { mutableStateOf("") }
    var relationshipProperty by remember { mutableStateOf("") }
    var commissionPct by remember { mutableStateOf("15") }

    // Deal states
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var dealProperty by remember { mutableStateOf("") }
    var dealAmount by remember { mutableStateOf("120000") }
    var coBrokeDetails by remember { mutableStateOf("No Split (Direct)") }
    var leaseType by remember { mutableStateOf("Commercial Office") } // Commercial Office, Residential

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("broker_workspace_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "⚠️ Professional Broker Workspace",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WarningAmber)
                    )
                    Text(
                        "All activities are tracked under Bangladesh Rental Registry Regulations 2026. Keep splits fully transparent.",
                        color = LightText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Portfolio Count", fontSize = 8.5.sp, color = GreyText)
                                Text("${brokerListings.size} Listings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Split Commission Earned", fontSize = 8.5.sp, color = GreyText)
                                Text("${brokerDeals.sumOf { it.commissionEarned }} BDT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VerifiedGreen)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SLA Compliance", fontSize = 8.5.sp, color = GreyText)
                                Text("98% Secure", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                            }
                        }
                    }
                }
            }
        }

        // Sub Tab Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSlate, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "Listings" to "📦 My Inventory",
                    "Splits" to "🤝 Relationship Maps",
                    "Deals" to "💼 Deal Ledger",
                    "Sync" to "⚡ Sync Terminal"
                ).forEach { (key, label) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (brokerTab == key) WarningAmber else Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { brokerTab = key }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (brokerTab == key) Color.Black else LightText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (brokerTab) {
            "Listings" -> {
                item {
                    Card(
                        onClick = onAddListingClick,
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, WarningAmber, RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = WarningAmber, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Publish Verified Rent Listing", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                                Text("Auto-routes verified details to avoid duplicate landlord postings.", fontSize = 9.sp, color = GreyText)
                            }
                        }
                    }
                }

                item {
                    Text("📦 Active Assigned Listings", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText, modifier = Modifier.padding(top = 4.dp))
                }

                if (brokerListings.isEmpty()) {
                    item {
                        Text("No properties posted under your portfolio. Use button above to begin.", fontSize = 10.sp, color = GreyText)
                    }
                } else {
                    items(brokerListings) { listing ->
                        ListingCardItem(
                            listing = listing,
                            onClick = { onSelectListing(listing) },
                            onAIScan = { viewModel.runAIScamScanOnListing(listing.id) }
                        )
                    }
                }
            }

            "Splits" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🤝 Broker-Owner Partnership Mapping (Anti-Scam)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                        Button(
                            onClick = { showAddPartnership = !showAddPartnership },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(if (showAddPartnership) "Close Form" else "+ Form Mappings", fontSize = 9.sp)
                        }
                    }
                }

                if (showAddPartnership) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AccentTeal.copy(0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Relationship Split Authorization", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)

                                CustomOutlineTextField(
                                    value = ownerNameInput,
                                    onValueChange = { ownerNameInput = it },
                                    label = "Landlord / Owner Name"
                                )
                                CustomOutlineTextField(
                                    value = ownerPhoneInput,
                                    onValueChange = { ownerPhoneInput = it },
                                    label = "Landlord Phone Number"
                                )
                                CustomOutlineTextField(
                                    value = relationshipProperty,
                                    onValueChange = { relationshipProperty = it },
                                    label = "Target Property Title"
                                )

                                CustomOutlineTextField(
                                    value = commissionPct,
                                    onValueChange = { commissionPct = it },
                                    label = "Commission Split Percentage (10% - 25% recommended)"
                                )

                                Button(
                                    onClick = {
                                        if (ownerNameInput.isNotBlank() && relationshipProperty.isNotBlank()) {
                                            viewModel.addBrokerPartnershipLog(
                                                user.email,
                                                ownerNameInput,
                                                ownerPhoneInput,
                                                relationshipProperty,
                                                commissionPct.toIntOrNull() ?: 15
                                            )
                                            ownerNameInput = ""
                                            ownerPhoneInput = ""
                                            relationshipProperty = ""
                                            showAddPartnership = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Verify & Map Relationship", fontSize = 10.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                if (partnerships.isEmpty()) {
                    item {
                        Text("No mapped owner relationships registered.", fontSize = 10.sp, color = GreyText)
                    }
                } else {
                    items(partnerships) { pb ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(pb.propertyTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text("Owner: ${pb.ownerName} (${pb.ownerPhone})", fontSize = 10.sp, color = GreyText)
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.15f)),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Split ${pb.commissionSplitPercent}%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp,
                                            color = WarningAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Status: ${pb.status}", color = VerifiedGreen, fontSize = 9.sp)
                                    Text("Credential ID: bp_ref_${pb.id.takeLast(4)}", fontSize = 9.sp, color = GreyText)
                                }
                            }
                        }
                    }
                }
            }

            "Deals" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💼 Corporate & Residential Deal Ledger", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                        Button(
                            onClick = { showAddDeal = !showAddDeal },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(if (showAddDeal) "Close Form" else "+ Live Deal Log", fontSize = 9.sp)
                        }
                    }
                }

                if (showAddDeal) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AccentTeal.copy(0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Submit Live Closed Deal Statement", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)

                                CustomOutlineTextField(
                                    value = clientName,
                                    onValueChange = { clientName = it },
                                    label = "Client Name"
                                )
                                CustomOutlineTextField(
                                    value = clientPhone,
                                    onValueChange = { clientPhone = it },
                                    label = "Client Phone"
                                )
                                CustomOutlineTextField(
                                    value = dealProperty,
                                    onValueChange = { dealProperty = it },
                                    label = "Property Title"
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        CustomOutlineTextField(
                                            value = dealAmount,
                                            onValueChange = { dealAmount = it },
                                            label = "Deal BDT Value"
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        CustomOutlineTextField(
                                            value = leaseType,
                                            onValueChange = { leaseType = it },
                                            label = "Type (Residential/Commercial)"
                                        )
                                    }
                                }
                                CustomOutlineTextField(
                                    value = coBrokeDetails,
                                    onValueChange = { coBrokeDetails = it },
                                    label = "Co-Broker Arrangement Split (Direct / Split 50/50)"
                                )

                                Button(
                                    onClick = {
                                        if (clientName.isNotBlank() && dealProperty.isNotBlank()) {
                                            val amt = dealAmount.toIntOrNull() ?: 50000
                                            val commissionPctRate = if (leaseType.contains("commercial", ignoreCase = true)) 10 else 15
                                            val commissionEarned = (amt * commissionPctRate) / 100
                                            viewModel.addBrokerDealLog(
                                                clientName,
                                                clientPhone,
                                                dealProperty,
                                                amt,
                                                commissionEarned,
                                                coBrokeDetails,
                                                leaseType
                                            )
                                            clientName = ""
                                            clientPhone = ""
                                            dealProperty = ""
                                            showAddDeal = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Submit Closed Deal Statement", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                items(brokerDeals) { deal ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(deal.propertyTitle, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (deal.dealType.contains("Commercial")) PrimaryEmerald.copy(0.15f) else WarningAmber.copy(0.15f)),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = deal.dealType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 7.5.sp,
                                        color = if (deal.dealType.contains("Commercial")) PrimaryEmerald else WarningAmber,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Client: ${deal.clientName} | contact: ${deal.clientPhone}", fontSize = 10.sp, color = GreyText)
                            Text("Co-Broker splitting: ${deal.coBrokeStatus}", fontSize = 9.sp, color = AccentTeal)
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Lease deal size BDT", fontSize = 8.sp, color = GreyText)
                                    Text("${deal.dealAmount} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = LightText)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("My Split Earnings", fontSize = 8.sp, color = GreyText)
                                    Text("+${deal.commissionEarned} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = VerifiedGreen)
                                }
                            }
                        }
                    }
                }
            }

            "Sync" -> {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡ Live RentSync Websocket Terminal Monitor", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                            Button(
                                onClick = {
                                    viewModel.addSyncLog("[MANUAL TRIGGER] Broker Syncing requests dispatched...")
                                    viewModel.addSyncLog("[OK] Packet acknowledged. State: Synced. Platform audit passed.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                modifier = Modifier.height(20.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("PING WEB_SOCKET", fontSize = 7.5.sp, color = Color.Black)
                            }
                        }
                        Text("Broker-owner ledger heartbeats and queue synchronization tracking:", fontSize = 9.sp, color = GreyText, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }

                items(socketLogs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (log.contains("OK") || log.contains("SYSTEM")) CardSlate else CardSlate.copy(0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                    ) {
                        Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💻", fontSize = 10.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = log,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.contains("[OK]")) VerifiedGreen else if (log.contains("SYSTEM")) InfoSky else LightText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCardItem(
    listing: RentListing,
    onClick: () -> Unit,
    onAIScan: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("listing_card_${listing.id}"),
        border = if (listing.isScamFlaggedByAI || listing.isScamFlaggedByUsers) {
            BorderStroke(1.5.dp, ErrorCrimson)
        } else {
            BorderStroke(1.dp, Color(0xFFE1E2E9))
        }
    ) {
        Column {
            // Visual Banner indicating Status of Room
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                when {
                                    listing.isScamFlaggedByAI -> ErrorCrimson.copy(0.3f)
                                    listing.isVerifiedOwner -> PrimaryEmerald.copy(0.4f)
                                    else -> WarningAmber.copy(0.3f)
                                },
                                DeepNavyBg
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                // Verified Badge at top right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Availability label with colorful pill
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (listing.availabilityState) {
                                "Vacant" -> VerifiedGreen
                                "Visiting Allowed" -> InfoSky
                                else -> ErrorCrimson
                            }
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            listing.availabilityState.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (listing.isVerifiedOwner) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = VerifiedGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Verified Owner", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (listing.hasVideoWalkthrough) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = InfoSky),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Video Badged", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Title overlay
                Text(
                    text = listing.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LightText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            // Body Info
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${listing.rentAmount} BDT/mo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (listing.isScamFlaggedByAI) ErrorCrimson else SoftEmerald
                    )
                    Text(
                        text = "📍 ${listing.area}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreyText
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listing.address,
                    fontSize = 11.sp,
                    color = GreyText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Tags section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(listing.type, fontSize = 9.sp) }
                        )
                        if (listing.isBrokerListing) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.12f)),
                                border = BorderStroke(0.5.dp, WarningAmber)
                            ) {
                                Text(
                                    "🚨 Brokered",
                                    fontSize = 8.sp,
                                    color = WarningAmber,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PrimaryEmerald.copy(0.12f)),
                                border = BorderStroke(0.5.dp, PrimaryEmerald)
                            ) {
                                Text(
                                    "🤝 Direct Owner",
                                    fontSize = 8.sp,
                                    color = PrimaryEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Scan triggers
                    TextButton(
                        onClick = onAIScan,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Scan", modifier = Modifier.size(12.dp), tint = AccentTeal)
                        Spacer(Modifier.width(2.dp))
                        Text("Verify AI Scan", fontSize = 10.sp, color = AccentTeal)
                    }
                }

                // Alert section for scam flags
                if (listing.isScamFlaggedByAI || listing.isScamFlaggedByUsers) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorCrimson.copy(0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = ErrorCrimson, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "AI Scammer Alert: Price irregularities or recycled images matched.",
                                fontSize = 9.sp,
                                color = ErrorCrimson,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingDetailView(
    listing: RentListing,
    allReviews: List<TenantReview>,
    negotiations: List<Negotiation>,
    currentRole: UserRole,
    onBack: () -> Unit,
    onNegotiate: (RentListing) -> Unit,
    onReport: (RentListing) -> Unit,
    onAddReview: (RentListing) -> Unit,
    onAIScan: (RentListing) -> Unit,
    onStartEscrow: (RentListing, Int) -> Unit,
    viewModel: RentViewModel,
    onChatClick: () -> Unit
) {
    val reviews = allReviews.filter { it.listingId == listing.id }
    val matchingNego = negotiations.find { it.listingId == listing.id }

    var maskedPhoneState by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LightText)
            }
        }

        item {
            // Large Display Card with Title & Status info
            Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        listing.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightText
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${listing.rentAmount} BDT/month",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftEmerald
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (listing.isVerifiedOwner) VerifiedGreen else WarningAmber
                            )
                        ) {
                            Text(
                                if (listing.isVerifiedOwner) "🛡 LANDLORD VERIFIED" else "⚠️ SELF-LOGGED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Live status controls for Landlord
        if (currentRole == UserRole.OWNER && listing.ownerName == "Rahman Saheb") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftEmerald.copy(0.1f)),
                    border = BorderStroke(1.dp, SoftEmerald)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📡 Landlord Controls (Live Status Hub)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Vacant", "Visiting Allowed", "Booked").forEach { state ->
                                Button(
                                    onClick = { viewModel.toggleAvailability(listing.id, state) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (listing.availabilityState == state) SoftEmerald else CardSlate
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(state, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Transparency Energy & Passport History (Past Tenants Logs)
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("📜 Rental History Passport", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentTeal)
                    Spacer(Modifier.height(6.dp))
                    Text("• May 2025 - Present: Rahman (Verified Student) Rent: 8500 BDT. No Complaint.", fontSize = 11.sp, color = GreyText)
                    Text("• Aug 2024 - April 2025: Sajjad Azad (NSU CSE) Rent: 8200 BDT. 0 Complaint.", fontSize = 11.sp, color = GreyText)
                    Text("• Rent Increase Frequency: Low (Stable for 24 months)", fontSize = 11.sp, color = PrimaryEmerald)
                    Spacer(Modifier.height(8.dp))

                    Text("⚡ Utilities Checklist Rules", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                    Text("• Electricity: ${listing.ruleElectricity}", fontSize = 11.sp, color = GreyText)
                    Text("• Water supply: ${listing.ruleWater}", fontSize = 11.sp, color = GreyText)
                    Text("• Security Guard Guarding: Yes. CC Security.", fontSize = 11.sp, color = GreyText)
                    Text("• Tenant Deposit Escrow: Support safe lock in app", fontSize = 11.sp, color = GreyText)
                }
            }
        }

        // Real Rent Transparency Canvas Chart
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("📊 Price Transparency Graph (${listing.area})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                    Text("Area average: ${if (listing.area == "Dhanmondi") "8,200 BDT" else "7,000 BDT"}", fontSize = 10.sp, color = GreyText)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Draw a custom canvas line graph (historical data)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        val width = size.width
                        val height = size.height

                        // Grid lines
                        drawLine(Color.Gray.copy(0.2f), Offset(0f, height*0.2f), Offset(width, height*0.2f), strokeWidth = 1f)
                        drawLine(Color.Gray.copy(0.2f), Offset(0f, height*0.8f), Offset(width, height*0.8f), strokeWidth = 1f)

                        // Draw area price plot Points (Historical line)
                        val p1 = Offset(width * 0.1f, height * 0.7f)
                        val p2 = Offset(width * 0.3f, height * 0.65f)
                        val p3 = Offset(width * 0.5f, height * 0.55f)
                        val p4 = Offset(width * 0.7f, height * 0.5f)
                        val p5 = Offset(width * 0.9f, height * 0.45f)

                        drawLine(PrimaryEmerald, p1, p2, strokeWidth = 3f)
                        drawLine(PrimaryEmerald, p2, p3, strokeWidth = 3f)
                        drawLine(PrimaryEmerald, p3, p4, strokeWidth = 3f)
                        drawLine(PrimaryEmerald, p4, p5, strokeWidth = 3f)

                        drawCircle(PrimaryEmerald, 6f, p1)
                        drawCircle(PrimaryEmerald, 6f, p2)
                        drawCircle(PrimaryEmerald, 6f, p3)
                        drawCircle(PrimaryEmerald, 6f, p4)
                        drawCircle(PrimaryEmerald, 6f, p5)

                        // Current listing point
                        val listingPriceRatio = when {
                            listing.rentAmount <= 5000 -> 0.8f
                            listing.rentAmount >= 20000 -> 0.2f
                            else -> 0.5f
                        }
                        val plist = Offset(width * 0.9f, height * listingPriceRatio)
                        drawCircle(ErrorCrimson, 8f, plist)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2024", fontSize = 9.sp, color = GreyText)
                        Text("2025", fontSize = 9.sp, color = GreyText)
                        Text("Current List (Red Dot)", fontSize = 9.sp, color = ErrorCrimson)
                    }
                }
            }
        }

        // Action Toolbar: Call, Negotiate, Escrow, AI Verify
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🛡 Anti-harassment & Privacy Protected Actions", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InfoSky)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Phone masking system
                        Button(
                            onClick = { maskedPhoneState = !maskedPhoneState },
                            colors = ButtonDefaults.buttonColors(containerColor = InfoSky),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Phone")
                            Spacer(Modifier.width(4.dp))
                            Text(if (maskedPhoneState) listing.ownerPhone else "Cal-Leak Protected", fontSize = 10.sp)
                        }

                        // AI Scam Scan
                        Button(
                            onClick = { onAIScan(listing) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "AI Scan")
                            Spacer(Modifier.width(4.dp))
                            Text("Diagnose Scan", fontSize = 11.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startBrokerNegotiationOrJoin(listing.ownerPhone, listing.title)
                                onChatClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("💬 Live Safe Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNegotiate(listing) },
                            colors = ButtonDefaults.buttonColors(containerColor = InfoSky),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Propose Offer", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onStartEscrow(listing, listing.securityDeposit) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Lock Deposit", fontSize = 11.sp)
                        }
                    }

                    // Report Scam
                    Button(
                        onClick = { onReport(listing) },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson.copy(0.12f)),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, ErrorCrimson),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Scam Report", tint = ErrorCrimson, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Flag Fake Ad or Scam Landlord", color = ErrorCrimson, fontSize = 11.sp)
                    }
                }
            }
        }

        // Truth Reviews
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐ Tenant Reviews passport", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Button(onClick = { onAddReview(listing) }, colors = ButtonDefaults.buttonColors(containerColor = CardSlate)) {
                    Text("Add Review", fontSize = 10.sp)
                }
            }
        }

        if (reviews.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No direct verification ratings yet. Live stays can record reviews anonymously.",
                        fontSize = 11.sp,
                        color = GreyText,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(reviews) { r ->
                Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(r.tenantName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryEmerald)
                            Text(r.dateString, fontSize = 10.sp, color = GreyText)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(r.reviewText, fontSize = 11.sp, color = LightText)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🧹 Clean: ${r.cleanlinessRating}/5", fontSize = 9.sp, color = GreyText)
                            Text("🤝 Owner: ${r.ownerBehaviorRating}/5", fontSize = 9.sp, color = GreyText)
                            Text("🔒 Safe: ${r.safetyRating}/5", fontSize = 9.sp, color = GreyText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapSimulationView(
    listings: List<RentListing>,
    onSelectListing: (RentListing) -> Unit
) {
    var selectedPointListing by remember { mutableStateOf<RentListing?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("🗺️ Color-Coded Room Map Index", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LightText)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(8.dp)) { drawCircle(VerifiedGreen) }
                        Spacer(Modifier.width(4.dp))
                        Text("Direct & Verified", fontSize = 10.sp, color = GreyText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(8.dp)) { drawCircle(WarningAmber) }
                        Spacer(Modifier.width(4.dp))
                        Text("New/Self-Logged", fontSize = 10.sp, color = GreyText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(Modifier.size(8.dp)) { drawCircle(ErrorCrimson) }
                        Spacer(Modifier.width(4.dp))
                        Text("Scam Warning Triggered", fontSize = 10.sp, color = GreyText)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Large simulated map canvas with plots!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GreyText.copy(0.4f), RoundedCornerShape(16.dp))
                .background(DeepNavyBg)
        ) {
            // Simulated grid background representing Mirpur/Dhanmondi maps
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStep = 40.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(Color.Gray.copy(0.08f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += gridStep
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(Color.Gray.copy(0.08f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += gridStep
                }
            }

            // Draw coordinate dots of listings in Dhaka context
            listings.forEach { l ->
                // Map latitude/longitude offsets into simulated local coordinates
                val mapX = (40 + (l.longitude - 90.35) * 5000).toFloat()
                val mapY = (120 + (23.85 - l.latitude) * 5000).toFloat()

                val dotColor = when {
                    l.isScamFlaggedByAI -> ErrorCrimson
                    l.isVerifiedOwner -> VerifiedGreen
                    else -> WarningAmber
                }

                Box(
                    modifier = Modifier
                        .offset(dpOuter(mapX).dp, dpOuter(mapY).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(0.25f))
                        .clickable { selectedPointListing = l }
                        .border(2.dp, dotColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (l.isVerifiedOwner) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                        contentDescription = l.title,
                        tint = dotColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Quick Map Helper Label Overlay
            Text(
                "Dhaka Map Coordinates (Click pin to diagnose truth indexes)",
                fontSize = 10.sp,
                color = GreyText.copy(0.8f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }

        // Point Preview footer Card
        AnimatedVisibility(
            visible = selectedPointListing != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            val point = selectedPointListing!!
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(point.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${point.rentAmount} BDT/month in ${point.area}", fontSize = 11.sp, color = SoftEmerald)
                        if (point.isScamFlaggedByAI) {
                            Text("📢 Scammer Alert Active", color = ErrorCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = { onSelectListing(point) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                    ) {
                        Text("View Truth", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// Convert absolute values safely into small offset scopes
private fun dpOuter(v: Float): Int {
    return (v.toInt() % 280).coerceIn(20, 260)
}

@Composable
fun AdvisorView(
    conversation: List<ChatMessage>,
    isLoading: Boolean,
    onAsk: (String) -> Unit
) {
    var queryInput by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "AI Key", tint = AccentTeal)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("🤖 AI Pro-Advisor & Scam Mod", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Bangladesh localized market rent models, safety checks & commute guides", fontSize = 10.sp, color = GreyText)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Chat logs
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(conversation) { chat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (chat.sender == "user") Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (chat.sender) {
                                "user" -> PrimaryEmerald
                                "system" -> CardSlate
                                else -> CardSlate.copy(0.7f)
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.widthIn(max = 280.dp),
                        border = if (chat.sender == "ai") BorderStroke(1.dp, AccentTeal) else null
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Text(
                                text = if (chat.sender == "user") "Me" else if (chat.sender == "system") "Rent Advisor Guide" else "Rent Truth BD AI",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                color = if (chat.sender == "user") LightText else AccentTeal
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(chat.message, fontSize = 12.sp, color = LightText)
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                        Card(colors = CardDefaults.cardColors(containerColor = CardSlate)) {
                            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.dp, color = AccentTeal)
                                Spacer(Modifier.width(8.dp))
                                Text("AI Room Expert is analyzing Dhanmondi route costs/rents...", fontSize = 10.sp, color = GreyText)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = queryInput,
                onValueChange = { queryInput = it },
                placeholder = { Text("Search advice: 'Mirpur sublet for student'...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentTeal,
                    unfocusedBorderColor = GreyText.copy(0.5f)
                )
            )
            Button(
                onClick = {
                    if (queryInput.isNotBlank()) {
                        onAsk(queryInput)
                        queryInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
            ) {
                Text("Ask")
            }
        }
    }
}

@Composable
fun RoommateFinderView(
    requests: List<RoommateRequest>,
    onAddRequest: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        // Welcome and add trigger
        Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("🤝 Hub for Campus Roommates", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Match lifestyle patterns, study times and splits", fontSize = 10.sp, color = GreyText)
                }
                Button(onClick = onAddRequest, colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)) {
                    Text("Post Request", fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            if (requests.isEmpty()) {
                item {
                    Text("No local roommates posts in this sector. Click 'Post Request' to start matching.", modifier = Modifier.padding(20.dp), color = GreyText)
                }
            } else {
                items(requests) { req ->
                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth(), border = BorderStroke(0.5.dp, PrimaryEmerald.copy(0.3f))) {
                        Column(Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(req.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LightText)
                                Text("Budget Limit: ${req.budget} BDT", fontWeight = FontWeight.Bold, color = SoftEmerald, fontSize = 11.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("🎓 ${req.institution} (${req.age}y)", fontSize = 11.sp, color = AccentTeal)
                            Text("📍 Target area: ${req.preferredArea}", fontSize = 11.sp, color = GreyText)
                            Spacer(Modifier.height(6.dp))
                            Text("\"${req.lifestyle}\"", fontSize = 11.sp, color = LightText, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                SuggestionChip(onClick = {}, label = { Text("Split: ${req.rentSplit}", fontSize = 9.sp) })
                                Text(req.contactDetails, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InfoSky)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EscrowModuleView(
    deposits: List<EscrowDeposit>,
    onUpdateStatus: (Int, String) -> Unit,
    currentRole: UserRole
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("💰 Escrow Security & Tenant Deposit Protection", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LightText)
                Spacer(Modifier.height(4.dp))
                Text("Protect deposits securely. Escrow holds BDT safe. Disbursed after verification check.", fontSize = 11.sp, color = GreyText)
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            if (deposits.isEmpty()) {
                item {
                    Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(36.dp), tint = GreyText)
                        Spacer(Modifier.height(8.dp))
                        Text("No active escrow secure deposits. Select a rental and press 'Lock Deposit' to initiate payment security protection.", color = GreyText, textAlign = TextAlign.Center, fontSize = 12.sp)
                    }
                }
            } else {
                items(deposits) { dep ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, when(dep.status) {
                            "Locked" -> WarningAmber
                            "Released" -> VerifiedGreen
                            else -> ErrorCrimson
                        })
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(dep.listingTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when(dep.status) {
                                            "Locked" -> WarningAmber
                                            "Released" -> VerifiedGreen
                                            else -> ErrorCrimson
                                        }
                                    )
                                ) {
                                    Text(
                                        dep.status.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Tenant payer: ${dep.tenantName}", fontSize = 11.sp, color = GreyText)
                            Text("Secured amount in hold: ${dep.amount} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = SoftEmerald)

                            if (dep.status == "Locked" && (currentRole == UserRole.SYSTEM || currentRole == UserRole.TENANT)) {
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onUpdateStatus(dep.id, "Released") },
                                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Release BDT to Owner", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { onUpdateStatus(dep.id, "Disputed") },
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("File Dispute / Raise", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCenterView(
    viewModel: RentViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val registeredUsers by viewModel.registeredUsers.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isRegisterState by remember { mutableStateOf(false) }
    var selectedRoleByInput by remember { mutableStateOf(UserRole.TENANT) }

    var showRechargeDialog by remember { mutableStateOf(false) }
    var rechargeAmountInput by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE1E2E9), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterState) "Create Account 🛡️" else "Rent Truth BD Sign In Keys",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald
                        )
                    )
                    Text(
                        text = if (isRegisterState) "Join our verified BDT ecosystem" else "Input email or test credentials to login",
                        style = MaterialTheme.typography.bodySmall.copy(color = GreyText),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = GreyText.copy(0.4f),
                            focusedLabelColor = PrimaryEmerald
                        )
                    )

                    if (!isRegisterState) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val isAdminMode = emailInput.trim().lowercase() == "admin@renttruthbd.com"
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(if (isAdminMode) "Admin Security Password 🔑" else "Security Password (Optional)") },
                            modifier = Modifier.fillMaxWidth().testTag("password_input"),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isAdminMode) ErrorCrimson else PrimaryEmerald,
                                unfocusedBorderColor = if (isAdminMode) ErrorCrimson.copy(0.4f) else GreyText.copy(0.4f),
                                focusedLabelColor = if (isAdminMode) ErrorCrimson else PrimaryEmerald
                            )
                        )
                    }

                    if (isRegisterState) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Full Name (e.g. Ratul Ahmed)") },
                            modifier = Modifier.fillMaxWidth().testTag("name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Bangladeshi Phone Number (01*******)") },
                            modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Select Your Ecosystem Role:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = LightText),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            UserRole.values().forEach { r ->
                                if (r != UserRole.OFFICE) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedRoleByInput == r) PrimaryEmerald.copy(0.15f) else CardSlate
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedRoleByInput = r }
                                            .border(
                                                1.dp,
                                                if (selectedRoleByInput == r) PrimaryEmerald else GreyText.copy(0.3f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Text(
                                            r.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedRoleByInput == r) PrimaryEmerald else LightText,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        val feeInfo = when (selectedRoleByInput) {
                            UserRole.OWNER -> "🏡 Owner Account Cost: 200 BDT (Registration Server Fee)"
                            UserRole.BROKER -> "⚠️ Broker Professional Licensing Charge: 500 BDT"
                            UserRole.SYSTEM -> "🧠 Admin Setup (Unrestricted access to revenues)"
                            else -> "🎓 Students / Tenants registration is 100% FREE"
                        }
                        Text(
                            text = feeInfo,
                            fontSize = 10.sp,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (emailInput.isBlank()) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Email cannot be empty!") }
                                return@Button
                            }

                            if (isRegisterState) {
                                if (usernameInput.isBlank()) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Name cannot be empty!") }
                                    return@Button
                                }
                                val result = viewModel.register(usernameInput, emailInput, phoneInput, selectedRoleByInput)
                                coroutineScope.launch { snackbarHostState.showSnackbar(result) }
                            } else {
                                val success = viewModel.login(emailInput, passwordInput)
                                if (success) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Welcome back!") }
                                } else {
                                    val isAd = emailInput.trim().lowercase() == "admin@renttruthbd.com"
                                    val msg = if (isAd) "Access Denied: Incorrect Admin Security Password!" else "User not found! Toggle 'Create Account' below to join."
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        modifier = Modifier.fillMaxWidth().testTag("auth_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isRegisterState) "Create Account & Charge" else "Validate Account Keys")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { isRegisterState = !isRegisterState }) {
                        Text(
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            text = if (isRegisterState) "Already have credentials? Sign In" else "New? Register Account with BDT 800 Welcome Bonus",
                            color = AccentTeal
                        )
                    }

                    if (!RentViewModel.PRODUCTION_MODE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Demo Profiles: admin@renttruthbd.com | rahman@landlord.com | ratul@school.edu",
                            fontSize = 9.sp,
                            color = GreyText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    } else {
        val user = currentUser ?: return
        var profileCategoryTab by remember { mutableStateOf("🏡 Workspace") }

        // Automated Rest Checkout States
        var showBkashCheckoutOverlay by remember { mutableStateOf(false) }
        var bkashCheckoutAmount by remember { mutableStateOf(0) }
        var bkashCheckoutPurpose by remember { mutableStateOf("") }
        var bkashOtpInput by remember { mutableStateOf("") }
        var bkashPinInput by remember { mutableStateOf("") }
        var checkoutStep by remember { mutableStateOf(1) } // 1: OTP, 2: PIN
        var checkoutSenderNo by remember { mutableStateOf("01711223344") }
        var checkoutGatewayType by remember { mutableStateOf("bKash API REST") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE1E2E9), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PrimaryEmerald.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.username.take(2).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                user.username,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = LightText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (user.isVerified || user.nidVerifiedBadge || user.propertyVerifiedBadge) {
                                Spacer(Modifier.width(4.dp))
                                Text("🛡️", fontSize = 14.sp)
                            }
                        }
                        Text(user.email, fontSize = 11.sp, color = GreyText)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = when (user.role) {
                                    UserRole.TENANT -> PrimaryEmerald.copy(0.2f)
                                    UserRole.OWNER -> InfoSky.copy(0.2f)
                                    UserRole.BROKER -> WarningAmber.copy(0.2f)
                                    else -> ErrorCrimson.copy(0.2f)
                                }),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    user.role.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = when (user.role) {
                                        UserRole.TENANT -> PrimaryEmerald
                                        UserRole.OWNER -> InfoSky
                                        UserRole.BROKER -> WarningAmber
                                        else -> ErrorCrimson
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(0.2f)),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "✨ Sub: ${user.subscriptionType}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = AccentTeal
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson.copy(0.15f)),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Logout", fontSize = 10.sp, color = ErrorCrimson, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (user.isLocked) {
                Spacer(Modifier.height(10.dp))
                Card(colors = CardDefaults.cardColors(containerColor = ErrorCrimson.copy(0.15f)), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Locked", tint = ErrorCrimson)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("🚨 Account Temporarily Suspended", fontWeight = FontWeight.Bold, color = ErrorCrimson, fontSize = 12.sp)
                            Text("Pending physical NID audit verification standard requirements.", color = ErrorCrimson.copy(0.8f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ScrollableTabRow(
                selectedTabIndex = when (profileCategoryTab) {
                    "🏡 Workspace" -> 0
                    "🛡️ Profile & Security" -> 1
                    "💳 Wallet & Subs" -> 2
                    "⚡ Pricing Rules" -> 3
                    "⚙️ Admin Dashboard" -> 4
                    else -> 0
                },
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                Tab(
                    selected = profileCategoryTab == "🏡 Workspace",
                    onClick = { profileCategoryTab = "🏡 Workspace" },
                    text = { Text("🏡 Workspace", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = PrimaryEmerald,
                    unselectedContentColor = GreyText
                )
                Tab(
                    selected = profileCategoryTab == "🛡️ Profile & Security",
                    onClick = { profileCategoryTab = "🛡️ Profile & Security" },
                    text = { Text("🛡️ Profile & Security", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = PrimaryEmerald,
                    unselectedContentColor = GreyText
                )
                Tab(
                    selected = profileCategoryTab == "💳 Wallet & Subs",
                    onClick = { profileCategoryTab = "💳 Wallet & Subs" },
                    text = { Text("💳 Wallet & Subs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = PrimaryEmerald,
                    unselectedContentColor = GreyText
                )
                Tab(
                    selected = profileCategoryTab == "⚡ Pricing Rules",
                    onClick = { profileCategoryTab = "⚡ Pricing Rules" },
                    text = { Text("⚡ Fees Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = PrimaryEmerald,
                    unselectedContentColor = GreyText
                )
                if (user.isAdmin || user.role == UserRole.SYSTEM) {
                    Tab(
                        selected = profileCategoryTab == "⚙️ Admin Dashboard",
                        onClick = { profileCategoryTab = "⚙️ Admin Dashboard" },
                        text = { Text("⚙️ Admin Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        selectedContentColor = ErrorCrimson,
                        unselectedContentColor = GreyText
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (profileCategoryTab) {
                "🏡 Workspace" -> {
                    when (user.role) {
                        UserRole.OWNER -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("🏡 Landlord Professional Stats", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = InfoSky)
                                            Spacer(Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(4.dp)) {
                                                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("Trust Score", fontSize = 10.sp, color = GreyText)
                                                        Text("${user.trustScore}/100", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VerifiedGreen)
                                                    }
                                                }
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(4.dp)) {
                                                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("Properties", fontSize = 10.sp, color = GreyText)
                                                        Text("${user.ownedPropertyCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InfoSky)
                                                    }
                                                }
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(4.dp)) {
                                                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("Complaints", fontSize = 10.sp, color = GreyText)
                                                        Text("${user.complaintCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (user.complaintCount > 0) ErrorCrimson else VerifiedGreen)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("👥 Current Registered Tenants", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                            Spacer(Modifier.height(8.dp))
                                            if (user.currentTenants.isEmpty()) {
                                                Text("No active tenants registered in system yet.", fontSize = 11.sp, color = GreyText)
                                            } else {
                                                user.currentTenants.forEach { t ->
                                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.Person, contentDescription = "tenant", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                                            Spacer(Modifier.width(6.dp))
                                                            Text(t, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LightText)
                                                        }
                                                        Text("Rent Status: Regular Paid ✅", fontSize = 10.sp, color = VerifiedGreen)
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(10.dp))
                                            HorizontalDivider(color = GreyText.copy(0.15f))
                                            Spacer(Modifier.height(10.dp))

                                            var newTenantNameInput by remember { mutableStateOf("") }
                                            Text("Add Existing Tenant Manually", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreyText)
                                            Spacer(Modifier.height(6.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                OutlinedTextField(
                                                    value = newTenantNameInput,
                                                    onValueChange = { newTenantNameInput = it },
                                                    placeholder = { Text("Tenant Name (e.g. Riyad Ahmed)") },
                                                    modifier = Modifier.weight(1f).height(48.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald),
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        if (newTenantNameInput.isNotBlank()) {
                                                            viewModel.addTenantManually(user.email, newTenantNameInput)
                                                            newTenantNameInput = ""
                                                            coroutineScope.launch { snackbarHostState.showSnackbar("Tenant manually added to registered lists!") }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                                    modifier = Modifier.height(36.dp)
                                                ) {
                                                    Text("Add", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("📬 Upcoming Tenant Booking Requests", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                            Spacer(Modifier.height(8.dp))
                                            if (user.upcomingTenantsRequests.isEmpty()) {
                                                Text("No pending future booking requests.", fontSize = 11.sp, color = GreyText)
                                            } else {
                                                user.upcomingTenantsRequests.forEach { reqName ->
                                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                                        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                            Column {
                                                                Text(reqName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                                Text("Wants lease for Dhanmondi Block 2", fontSize = 10.sp, color = GreyText)
                                                            }
                                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                Button(
                                                                    onClick = {
                                                                        viewModel.respondToTenantBookingRequest(user.email, reqName, true)
                                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Tenant Approved! Moved to current roster.") }
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                                                    modifier = Modifier.height(28.dp)
                                                                ) {
                                                                    Text("Approve", fontSize = 10.sp)
                                                                }
                                                                Button(
                                                                    onClick = {
                                                                        viewModel.respondToTenantBookingRequest(user.email, reqName, false)
                                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Booking rejected safely.") }
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                                                    modifier = Modifier.height(28.dp)
                                                                ) {
                                                                    Text("Decline", fontSize = 10.sp)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("📜 Rental History Logs", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                            Spacer(Modifier.height(8.dp))
                                            user.rentalAgreementHistory.forEach { log ->
                                                Text("• $log", fontSize = 11.sp, color = GreyText, modifier = Modifier.padding(vertical = 2.dp))
                                            }
                                            user.previousTenantHistory.forEach { former ->
                                                Text("• Former tenant: $former (Closed contract successfully)", fontSize = 11.sp, color = GreyText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        UserRole.TENANT -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("🛡️ Tenant Verification Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryEmerald)
                                            Spacer(Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f)) {
                                                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("Ecosystem Trust", fontSize = 9.sp, color = GreyText)
                                                        Text("${user.trustScore}/100", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VerifiedGreen)
                                                    }
                                                }
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f)) {
                                                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("Verification Status", fontSize = 9.sp, color = GreyText)
                                                        Text(if (user.isVerified) "Verified 🛡️" else "Pending Audit", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (user.isVerified) VerifiedGreen else WarningAmber)
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(12.dp))
                                            Text("• Occupation: ${user.occupationInfo}", fontSize = 11.sp, color = LightText)
                                            Text("• Current renting status: ${user.currentRentalStatus}", fontSize = 11.sp, color = LightText)
                                            Text("• Emergency Contact: ${user.emergencyContact}", fontSize = 11.sp, color = LightText)
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("📤 Upload Verification Scan Documents (KYC)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentTeal)
                                            Spacer(Modifier.height(8.dp))

                                            var selectedDocTypeForUpload by remember { mutableStateOf("NID Card") }
                                            var mockDocFileName by remember { mutableStateOf("") }

                                            Text("Select Document Category:", fontSize = 10.sp, color = GreyText)
                                            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                listOf("NID Card", "Smart NID", "Birth Certificate", "Passport", "Student ID", "Utility Bill").forEach { type ->
                                                    Card(
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = CardDefaults.cardColors(containerColor = if (selectedDocTypeForUpload == type) AccentTeal.copy(0.2f) else CardSlate.copy(0.4f)),
                                                        modifier = Modifier.clickable { selectedDocTypeForUpload = type }.border(1.dp, if (selectedDocTypeForUpload == type) AccentTeal else Color.Transparent, RoundedCornerShape(8.dp))
                                                    ) {
                                                        Text(type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selectedDocTypeForUpload == type) AccentTeal else LightText, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = mockDocFileName,
                                                onValueChange = { mockDocFileName = it },
                                                label = { Text("Document File Name (e.g. nid_scan_f.jpg)") },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentTeal),
                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                            )

                                            Spacer(Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    if (mockDocFileName.isBlank()) {
                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Please enter a file name to simulate scanner.") }
                                                    } else {
                                                        viewModel.uploadTenantDocument(selectedDocTypeForUpload, mockDocFileName)
                                                        mockDocFileName = ""
                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Document uploaded! Waiting for national server auditors.") }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Submit Verified Scan 📤", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("📋 Uploaded Document Status Logs", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                            Spacer(Modifier.height(8.dp))
                                            if (user.uploadedDocs.isEmpty()) {
                                                Text("No uploaded documents in repository history.", fontSize = 11.sp, color = GreyText)
                                            } else {
                                                user.uploadedDocs.forEach { doc ->
                                                    var isWatermarkedVisible by remember { mutableStateOf(true) }
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)),
                                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(0.5.dp, AccentTeal.copy(0.3f), RoundedCornerShape(10.dp))
                                                    ) {
                                                        Box(modifier = Modifier.fillMaxWidth()) {
                                                            Column(Modifier.padding(10.dp)) {
                                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                                    Text("• ${doc.docType}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentTeal)
                                                                    Card(
                                                                        colors = CardDefaults.cardColors(containerColor = when(doc.status) {
                                                                            "Approved" -> VerifiedGreen.copy(0.15f)
                                                                            "Pending" -> WarningAmber.copy(0.15f)
                                                                            "Rejected" -> ErrorCrimson.copy(0.15f)
                                                                            else -> InfoSky.copy(0.15f)
                                                                        })
                                                                    ) {
                                                                        Text(doc.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = when(doc.status) {
                                                                            "Approved" -> VerifiedGreen
                                                                            "Pending" -> WarningAmber
                                                                            "Rejected" -> ErrorCrimson
                                                                            else -> InfoSky
                                                                        }, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                                    }
                                                                }
                                                                Text("File: ${doc.fileName}", fontSize = 10.sp, color = GreyText)

                                                                // Simulated OCR Metadata Info
                                                                Spacer(Modifier.height(4.dp))
                                                                Card(
                                                                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.2f)),
                                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                                                ) {
                                                                    Column(Modifier.padding(6.dp)) {
                                                                        Text("🤖 Govt OCR AI Extraction Report", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                                                                        Text("• National Identity Match: SUCCESS (Verified Hash: rt_nid_984422A)", fontSize = 8.sp, color = LightText)
                                                                        Text("• Authenticity checks: Safe (Image Tampering Audit: 100% Tamper-Free)", fontSize = 8.sp, color = VerifiedGreen)
                                                                        Text("• Extracted Name Match: OK | DOB: Jan 12, 1994", fontSize = 8.sp, color = LightText)
                                                                    }
                                                                }

                                                                if (doc.comments.isNotBlank()) {
                                                                    Text("Audit: ${doc.comments}", fontSize = 10.sp, color = LightText.copy(0.8f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                                                }

                                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                                                    Text("Diagonal Watermark", fontSize = 9.sp, color = GreyText)
                                                                    Spacer(Modifier.width(6.dp))
                                                                    Switch(
                                                                        checked = isWatermarkedVisible,
                                                                        onCheckedChange = { isWatermarkedVisible = it },
                                                                        modifier = Modifier.scale(0.6f).height(24.dp)
                                                                    )
                                                                }
                                                            }

                                                            if (isWatermarkedVisible) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .align(Alignment.Center)
                                                                        .rotate(-15f)
                                                                        .background(ErrorCrimson.copy(0.12f), RoundedCornerShape(4.dp))
                                                                        .border(1.dp, ErrorCrimson.copy(0.40f), RoundedCornerShape(4.dp))
                                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                                ) {
                                                                    Text(
                                                                        "RENT TRUTH BD AUDIT ONLY",
                                                                        color = ErrorCrimson.copy(0.8f),
                                                                        fontSize = 8.sp,
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        fontFamily = FontFamily.Monospace,
                                                                        letterSpacing = 1.sp
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("🇧🇩 State Emergency Security Helpdesk", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ErrorCrimson)
                                            Spacer(Modifier.height(6.dp))
                                            Text("• National Emergency Service: 999 📞", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("• Bangladesh Police Headquarters: 100 📞", fontSize = 11.sp)
                                            Text("• Rapid Action Battalion: 101 📞", fontSize = 11.sp)
                                            Text("• Fire Service Civil Defense: 102 📞", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        UserRole.BROKER -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = CardSlate), modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.padding(14.dp)) {
                                            Text("💼 Professional Broker Directory Index", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WarningAmber)
                                            Spacer(Modifier.height(10.dp))
                                            Text("• Trust Score Rank: ${user.brokerTrustScore}/100 🏅", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("• Platform Commission Cap: ${user.commissionPercent}% (Strictly Audited)", fontSize = 11.sp)
                                            Text("• Handled Listings: ${user.propertyHandlingCount} Spaces", fontSize = 11.sp)
                                            Text("• Verified Partnership Portals: ${user.activePartnerships} Owners", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Text("Standard view for role ${user.role.name}", color = GreyText)
                        }
                    }
                }

                "🛡️ Profile & Security" -> {
                    var usernameEdit by remember(user) { mutableStateOf(user.username) }
                    var phoneEdit by remember(user) { mutableStateOf(user.phone) }
                    var emailEdit by remember(user) { mutableStateOf(user.email) }
                    var profilePhotoEdit by remember(user) { mutableStateOf(user.profilePhoto ?: "") }
                    var addressEdit by remember(user) { mutableStateOf(user.address ?: "") }
                    var profileBioEdit by remember(user) { mutableStateOf(user.profileBio ?: "") }
                    var rentalPreferencesEdit by remember(user) { mutableStateOf(user.rentalPreferences ?: "") }
                    var bankPaymentDetailsEdit by remember(user) { mutableStateOf(user.bankPaymentDetails ?: "") }
                    var propertyDescriptionsEdit by remember(user) { mutableStateOf(user.propertyDescriptions ?: "") }
                    var brokerIdentityDetailsEdit by remember(user) { mutableStateOf(user.brokerIdentityDetails ?: "") }
                    var occupationEdit by remember(user) { mutableStateOf(user.occupationInfo) }
                    var emergencyEdit by remember(user) { mutableStateOf(user.emergencyContact) }

                    val activeSessionsList by viewModel.activeSessions.collectAsState()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "🛡️ Rent Truth BD: Public Profile Credentials",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SoftEmerald
                                    )
                                    Text(
                                        "Manage your official registration credentials linked directly with the Bangladesh National database router.",
                                        fontSize = 10.sp,
                                        color = GreyText
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    CustomOutlineTextField(
                                        value = usernameEdit,
                                        onValueChange = { usernameEdit = it },
                                        label = "Ecosystem Full Name"
                                    )
                                    CustomOutlineTextField(
                                        value = phoneEdit,
                                        onValueChange = { phoneEdit = it },
                                        label = "Mobile Contact Number"
                                    )
                                    CustomOutlineTextField(
                                        value = emailEdit,
                                        onValueChange = { emailEdit = it },
                                        label = "Secure Email Identity"
                                    )
                                    CustomOutlineTextField(
                                        value = profilePhotoEdit,
                                        onValueChange = { profilePhotoEdit = it },
                                        label = "Profile Photo URL (Optional)"
                                    )
                                    CustomOutlineTextField(
                                        value = addressEdit,
                                        onValueChange = { addressEdit = it },
                                        label = "Current Residential Address"
                                    )
                                    CustomOutlineTextField(
                                        value = profileBioEdit,
                                        onValueChange = { profileBioEdit = it },
                                        label = "Personal biography / Overview"
                                    )
                                    CustomOutlineTextField(
                                        value = occupationEdit,
                                        onValueChange = { occupationEdit = it },
                                        label = "Current Occupation Info"
                                    )
                                    CustomOutlineTextField(
                                        value = emergencyEdit,
                                        onValueChange = { emergencyEdit = it },
                                        label = "Emergency Guardian Phone"
                                    )

                                    if (user.role == UserRole.TENANT) {
                                        CustomOutlineTextField(
                                            value = rentalPreferencesEdit,
                                            onValueChange = { rentalPreferencesEdit = it },
                                            label = "Rental Habit Preferences (e.g., Non-smoker, student roommates)"
                                        )
                                    }

                                    if (user.role == UserRole.OWNER) {
                                        CustomOutlineTextField(
                                            value = propertyDescriptionsEdit,
                                            onValueChange = { propertyDescriptionsEdit = it },
                                            label = "Detailed Property Outlines & Holding Tax Registration Keys"
                                        )
                                        CustomOutlineTextField(
                                            value = bankPaymentDetailsEdit,
                                            onValueChange = { bankPaymentDetailsEdit = it },
                                            label = "Direct Bank Payment Info (Routing number, bank account)"
                                        )
                                    }

                                    if (user.role == UserRole.BROKER) {
                                        CustomOutlineTextField(
                                            value = brokerIdentityDetailsEdit,
                                            onValueChange = { brokerIdentityDetailsEdit = it },
                                            label = "Broker Professional License Certification Records"
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.updateUserProfileDetails(
                                                username = usernameEdit,
                                                phone = phoneEdit,
                                                email = emailEdit,
                                                profilePhoto = profilePhotoEdit,
                                                address = addressEdit,
                                                profileBio = profileBioEdit,
                                                rentalPreferences = rentalPreferencesEdit,
                                                bankPaymentDetails = bankPaymentDetailsEdit,
                                                propertyDescriptions = propertyDescriptionsEdit,
                                                brokerIdentityDetails = brokerIdentityDetailsEdit,
                                                occupation = occupationEdit,
                                                emergencyContact = emergencyEdit
                                            )
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Ecosystem public profile changes flushed successfully.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Save Active Credentials", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(
                                        "📤 KYC Document Scan & Multi-Version Vault",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = AccentTeal
                                    )
                                    Spacer(Modifier.height(6.dp))

                                    var selectedDocTypeForUpload by remember { mutableStateOf("NID Card") }
                                    var mockDocFileName by remember { mutableStateOf("") }
                                    var expiryDateInput by remember { mutableStateOf("2035-12-31") }

                                    Text("Select Document Category:", fontSize = 10.sp, color = GreyText)
                                    Row(
                                        Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("NID Card", "Smart NID", "Birth Certificate", "Passport", "Student ID", "Utility Bill").forEach { type ->
                                            val isSel = selectedDocTypeForUpload == type
                                            Card(
                                                shape = RoundedCornerShape(8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSel) AccentTeal.copy(0.15f) else CardSlate.copy(0.4f)
                                                ),
                                                modifier = Modifier
                                                    .clickable { selectedDocTypeForUpload = type }
                                                    .border(
                                                        1.dp,
                                                        if (isSel) AccentTeal else Color.Transparent,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                            ) {
                                                Text(
                                                    type,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) AccentTeal else LightText,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))
                                    CustomOutlineTextField(
                                        value = mockDocFileName,
                                        onValueChange = { mockDocFileName = it },
                                        label = "Scan File Name (e.g. smart_nid_compressed.jpg)"
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    CustomOutlineTextField(
                                        value = expiryDateInput,
                                        onValueChange = { expiryDateInput = it },
                                        label = "Doc Expiry Date (YYYY-MM-DD)"
                                    )

                                    Spacer(Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (mockDocFileName.isBlank()) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Enter scan filename to simulate OCR scanner.")
                                                }
                                            } else {
                                                viewModel.uploadTenantDocumentWithMeta(
                                                    docType = selectedDocTypeForUpload,
                                                    fileName = mockDocFileName,
                                                    expiryDate = expiryDateInput
                                                )
                                                mockDocFileName = ""
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("New document version uploaded. Active audit processing.")
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Submit Verified Scan (Track Version) 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(
                                        "📋 Uploaded Document Status & Expiration logs",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = LightText
                                    )
                                    Spacer(Modifier.height(8.dp))

                                    if (user.uploadedDocs.isEmpty()) {
                                        Text(
                                            "No documents uploaded to this profile yet.",
                                            fontSize = 11.sp,
                                            color = GreyText
                                        )
                                    } else {
                                        user.uploadedDocs.forEach { doc ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)),
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                            ) {
                                                Column(Modifier.padding(10.dp)) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            "• ${doc.docType}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = AccentTeal
                                                        )
                                                        Card(
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = when (doc.status) {
                                                                    "Approved" -> VerifiedGreen.copy(0.15f)
                                                                    "Pending" -> WarningAmber.copy(0.15f)
                                                                    "Rejected" -> ErrorCrimson.copy(0.15f)
                                                                    else -> InfoSky.copy(0.15f)
                                                                }
                                                            )
                                                        ) {
                                                            Text(
                                                                doc.status,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = when (doc.status) {
                                                                    "Approved" -> VerifiedGreen
                                                                    "Pending" -> WarningAmber
                                                                    "Rejected" -> ErrorCrimson
                                                                    else -> InfoSky
                                                                },
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                    Text("File: ${doc.fileName}", fontSize = 10.sp, color = GreyText)
                                                    Text("Validity Expiry: ${doc.expiryDate} 🗓️", fontSize = 10.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                                                    if (doc.comments.isNotBlank()) {
                                                        Text(
                                                            "Audit Notes: ${doc.comments}",
                                                            fontSize = 10.sp,
                                                            color = LightText.copy(0.8f),
                                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(
                                        "⚡ Active Secured Device Sessions (RBAC)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ErrorCrimson
                                    )
                                    Text(
                                        "Manage and immediately revoke current login sessions, browser tokens, and connected hardware addresses.",
                                        fontSize = 10.sp,
                                        color = GreyText,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    if (activeSessionsList.isEmpty()) {
                                        Text(
                                            "No active sessions found.",
                                            fontSize = 11.sp,
                                            color = GreyText
                                        )
                                    } else {
                                        activeSessionsList.forEach { sess ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)),
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            sess.deviceName,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = LightText
                                                        )
                                                        Text(
                                                            "IP: ${sess.ipAddress} | Active: ${sess.lastActive}",
                                                            fontSize = 9.sp,
                                                            color = GreyText
                                                        )
                                                    }
                                                    Button(
                                                        onClick = {
                                                            viewModel.removeSecuritySession(sess.id)
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("Security Session token revoked successfully.")
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson.copy(0.15f)),
                                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Text(
                                                            "Revoke Token",
                                                            fontSize = 9.sp,
                                                            color = ErrorCrimson,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "💳 Wallet & Subs" -> {
                    val isAutomated by viewModel.isGatewayAutomated.collectAsState()
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Ecosystem Credit Wallet Balance", fontSize = 11.sp, color = GreyText)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${user.walletBalance} BDT", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = LightText))
                                        Button(
                                            onClick = { showRechargeDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                                        ) {
                                            Text("Bkash Recharge 🔄", fontSize = 11.sp)
                                        }
                                    }
                                    Text(
                                        "Deducts booking charges, escrow transfers, and registration maintainers.",
                                        fontSize = 10.sp,
                                        color = GreyText,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Text("Upgrade Platform Subscriptions", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LightText, modifier = Modifier.padding(top = 6.dp))
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "🎓 Student Priority Badge",
                                priceDesc = "199 BDT / mo",
                                desc = "Unlocks roommate prioritizing filters, 2/3 direct free Escrow releases, and campus verification tools.",
                                isCurrent = user.subscriptionType == "Student Premium",
                                onUpgrade = {
                                    if (isAutomated) {
                                        bkashCheckoutAmount = 199
                                        bkashCheckoutPurpose = "Upgrade to Student Priority Badge"
                                        checkoutStep = 1
                                        showBkashCheckoutOverlay = true
                                    } else {
                                        val success = viewModel.upgradeSubscription("Student Premium", 199)
                                        coroutineScope.launch {
                                            if (success) snackbarHostState.showSnackbar("Upgraded to Student Premium!")
                                            else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "🏡 Verified Landlord Platinum",
                                priceDesc = "499 BDT / mo",
                                desc = "Physical audit visits, continuous pricing watermark guarantee, direct priority landlord listings.",
                                isCurrent = user.subscriptionType == "Owner Platinum",
                                onUpgrade = {
                                    if (isAutomated) {
                                        bkashCheckoutAmount = 499
                                        bkashCheckoutPurpose = "Upgrade to Verified Landlord Platinum"
                                        checkoutStep = 1
                                        showBkashCheckoutOverlay = true
                                    } else {
                                        val success = viewModel.upgradeSubscription("Owner Platinum", 499)
                                        coroutineScope.launch {
                                            if (success) snackbarHostState.showSnackbar("Upgraded to Landlord Platinum!")
                                            else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "⚠️ Broker Pro license",
                                priceDesc = "999 BDT / mo",
                                desc = "Professional license. Mandatory to bypass direct warning limits on broker accounts.",
                                isCurrent = user.subscriptionType == "Broker Pro",
                                onUpgrade = {
                                    if (isAutomated) {
                                        bkashCheckoutAmount = 999
                                        bkashCheckoutPurpose = "Upgrade to Broker Pro license"
                                        checkoutStep = 1
                                        showBkashCheckoutOverlay = true
                                    } else {
                                        val success = viewModel.upgradeSubscription("Broker Pro", 999)
                                        coroutineScope.launch {
                                            if (success) snackbarHostState.showSnackbar("Upgraded to Broker Premium Active!")
                                            else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "👨‍🎓 Bachelor Elite Pass",
                                priceDesc = "250 BDT / mo",
                                desc = "Enables instant single roommate matching, zero hidden fees audit priority, and express tenant contract templates.",
                                isCurrent = user.subscriptionType == "Bachelor Elite",
                                onUpgrade = {
                                    val success = viewModel.upgradeSubscription("Bachelor Elite", 250)
                                    coroutineScope.launch {
                                        if (success) snackbarHostState.showSnackbar("Upgraded to Bachelor Elite Pass!")
                                        else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                    }
                                }
                            )
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "👨‍👩‍👧‍👦 Family Defense Shield",
                                priceDesc = "350 BDT / mo",
                                desc = "Legal document reviews, 100% genuine verified direct owners only, and free structural safety certification queries.",
                                isCurrent = user.subscriptionType == "Family Shield",
                                onUpgrade = {
                                    val success = viewModel.upgradeSubscription("Family Shield", 350)
                                    coroutineScope.launch {
                                        if (success) snackbarHostState.showSnackbar("Upgraded to Family Defense Shield!")
                                        else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                    }
                                }
                            )
                        }

                        item {
                            SubscriptionOptionCard(
                                title = "🏢 Commercial Office Premium",
                                priceDesc = "800 BDT / mo",
                                desc = "Trade license commercial validations, VAT/Tax invoice assist, corporate split-payment lease models, and multi-employee workspace matches.",
                                isCurrent = user.subscriptionType == "Office Premium",
                                onUpgrade = {
                                    val success = viewModel.upgradeSubscription("Office Premium", 800)
                                    coroutineScope.launch {
                                        if (success) snackbarHostState.showSnackbar("Upgraded to Commercial Office Premium!")
                                        else snackbarHostState.showSnackbar("Insufficient balance, please top up BDT first!")
                                    }
                                }
                            )
                        }
                    }
                }

                "⚡ Pricing Rules" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Pricing Rules & Server Cost Maintainers", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryEmerald)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    StaticBillingRow(label = "🏡 Landlord Account Creation Fee", value = "200 BDT")
                                    StaticBillingRow(label = "⚠️ Broker Account Activation Charge", value = "500 BDT")
                                    StaticBillingRow(label = "🏠 Base Listing Publication Fee", value = "50 BDT / post")
                                    StaticBillingRow(label = "🔓 Safe Guarded Escrow Commission", value = "2.5% of Deposit")
                                }
                            }
                        }
                    }
                }

                "⚙️ Admin Dashboard" -> {
                    val subRev by viewModel.adminRevenueSubscriptions.collectAsState()
                    val commRev by viewModel.adminRevenueCommissions.collectAsState()
                    val accRev by viewModel.adminRevenueAccountCharges.collectAsState()
                    val listRev by viewModel.adminRevenueListingFees.collectAsState()
                    val totalListingsCountByAdmin = viewModel.listings.collectAsState().value
                    val manualPaymentsList by viewModel.manualPayments.collectAsState(initial = emptyList())

                    var searchAdminUserQuery by remember { mutableStateOf("") }
                    var editingScoreUserEmail by remember { mutableStateOf<String?>(null) }
                    var trustScoreInputValue by remember { mutableStateOf("") }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val grandTotal = subRev + commRev + accRev + listRev
                                    Text("Grand Collected Treasury (BDT)", fontSize = 10.sp, color = GreyText)
                                    Text(
                                        "${grandTotal} BDT",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = VerifiedGreen)
                                    )
                                    HorizontalDivider(color = GreyText.copy(0.2f), thickness = 0.5.dp)
                                    AdminStatRow(label = "Subscription Upgrades", amount = subRev)
                                    AdminStatRow(label = "Escrow 2.5% Commission Audit", amount = commRev)
                                    AdminStatRow(label = "Account Registrations Charged", amount = accRev)
                                    AdminStatRow(label = "Property Standard Listing Fees", amount = listRev)

                                    Spacer(Modifier.height(8.dp))
                                    Text("📊 Visual Revenue Matrix Telemetry", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentTeal)

                                    val maxVal = maxOf(1, subRev, commRev, accRev, listRev).toFloat()
                                    RevenueBarMetric(label = "Premium Subscriptions", amount = subRev, pct = subRev / maxVal)
                                    RevenueBarMetric(label = "Escrow Commission Pools", amount = commRev, pct = commRev / maxVal)
                                    RevenueBarMetric(label = "Server Setup Signups", amount = accRev, pct = accRev / maxVal)
                                    RevenueBarMetric(label = "Listing Posting Charges", amount = listRev, pct = listRev / maxVal)
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth().border(1.dp, InfoSky.copy(0.2f), RoundedCornerShape(12.dp))
                            ) {
                                val wsStatus by viewModel.websocketStatus.collectAsState()
                                val offlineTasks by viewModel.offlineQueue.collectAsState()
                                val pingLatency by viewModel.websocketLatency.collectAsState()
                                val cpuLoad by viewModel.dashboardCpuPercent.collectAsState()
                                val heapUsed by viewModel.dashboardHeapMemory.collectAsState()
                                val isStressed by viewModel.isLoadTesting.collectAsState()
                                val stressProgress by viewModel.loadTestProgress.collectAsState()
                                val stressLogs by viewModel.loadTestingLogs.collectAsState()
                                val isAutomated by viewModel.isGatewayAutomated.collectAsState()

                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Text("🛰️ National Operations Command Center", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = if (wsStatus.contains("🟢")) VerifiedGreen.copy(0.15f) else ErrorCrimson.copy(0.15f)),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(wsStatus, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (wsStatus.contains("🟢")) VerifiedGreen else ErrorCrimson, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("Dhaka Cloud Clusters, WebSocket pools, thread locking states, and automated ledger gateways are online.", fontSize = 9.sp, color = GreyText)
                                    HorizontalDivider(color = GreyText.copy(0.15f), thickness = 0.5.dp)

                                    // Controls Bar
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { viewModel.toggleOfflineMode() },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (wsStatus.contains("🟢")) ErrorCrimson else VerifiedGreen),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(30.dp).weight(1f)
                                        ) {
                                            Text(if (wsStatus.contains("🟢")) "Force Offline Drop" else "Reconnect WebSocket", fontSize = 10.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.setGatewayAutomation(!isAutomated) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(30.dp).weight(1f)
                                        ) {
                                            Text(if (isAutomated) "REST Automated On" else "REST Automated Off", fontSize = 10.sp)
                                        }
                                    }

                                    if (offlineTasks.isNotEmpty()) {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.1f)),
                                            modifier = Modifier.fillMaxWidth().border(1.dp, WarningAmber.copy(0.3f), RoundedCornerShape(8.dp))
                                        ) {
                                            Column(Modifier.padding(8.dp)) {
                                                Text("⚠️ Locally Cached Sync Buffer (${offlineTasks.size} Events):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                                                offlineTasks.forEach { task ->
                                                    Text("• ${task.operationName}: ${task.description}", fontSize = 9.sp, color = LightText)
                                                }
                                            }
                                        }
                                    }

                                    // Hardware Stat Matrix
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                            Column(Modifier.padding(8.dp)) {
                                                Text("CPU TELEMETRY", fontSize = 8.sp, color = GreyText)
                                                Text("$cpuLoad% Peak Load", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (cpuLoad > 60) ErrorCrimson else VerifiedGreen)
                                                Text("Thread Locks: 0", fontSize = 8.sp, color = GreyText)
                                            }
                                        }
                                        Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                            Column(Modifier.padding(8.dp)) {
                                                Text("JVM HEAP SPACE", fontSize = 8.sp, color = GreyText)
                                                Text("${heapUsed}MB / 2048MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                                                Text("Self-Healing Active", fontSize = 8.sp, color = GreyText)
                                            }
                                        }
                                        Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                            Column(Modifier.padding(8.dp)) {
                                                Text("WS LATENCY", fontSize = 8.sp, color = GreyText)
                                                Text("${pingLatency}ms (99.9%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftEmerald)
                                                Text("Queue Backpressure: 0", fontSize = 8.sp, color = GreyText)
                                            }
                                        }
                                    }

                                    // Regional Dhaka Division Heatmaps
                                    Text("🗺️ Dhaka Division Occupancy & Active Concurrency", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GreyText)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(
                                            Triple("Dhanmondi / Banani / Gulshan", 88, "9.2k concurrent hits"),
                                            Triple("Mirpur / Pallabi", 96, "14.8k concurrent hits"),
                                            Triple("Uttara Sector 1-14", 82, "7.4k concurrent hits"),
                                            Triple("Bashundhara R/A Blocks", 91, "11.2k concurrent hits"),
                                            Triple("Chittagong GEC / Halishahar", 74, "5.1k concurrent hits")
                                        ).forEach { (region, fillPct, statusText) ->
                                            Column {
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                    Text(region, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Text("$fillPct% ($statusText)", fontSize = 8.sp, color = GreyText)
                                                }
                                                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(CardSlate.copy(0.6f), RoundedCornerShape(2.dp))) {
                                                    Box(modifier = Modifier.fillMaxWidth(fillPct / 100f).fillMaxHeight().background(
                                                        if (fillPct > 90) ErrorCrimson else if (fillPct > 80) AccentTeal else VerifiedGreen,
                                                        RoundedCornerShape(2.dp)
                                                    ))
                                                }
                                            }
                                        }
                                    }

                                    // Stress Testing Block
                                    HorizontalDivider(color = GreyText.copy(0.15f), thickness = 0.5.dp)
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Text("🧪 Bangladesh Concurrency Stress Tool", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = InfoSky)
                                        if (isStressed) {
                                            Text("TESTING ACTIVE CONCURRENTLY", fontSize = 8.sp, color = ErrorCrimson, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (!isStressed) {
                                        Button(
                                            onClick = { viewModel.runBangladeshScaleStressTest() },
                                            colors = ButtonDefaults.buttonColors(containerColor = InfoSky),
                                            modifier = Modifier.fillMaxWidth().height(36.dp)
                                        ) {
                                            Text("Run Bangladesh-Scale Concurrency Stress Test", fontSize = 11.sp)
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            LinearProgressIndicator(
                                                progress = { stressProgress },
                                                color = InfoSky,
                                                trackColor = GreyText.copy(0.2f),
                                                modifier = Modifier.fillMaxWidth().height(6.dp)
                                            )
                                            Text("Stress Progress: ${(stressProgress * 100).toInt()}% - Heavy concurrent operations on SQLite thread pools.", fontSize = 9.sp, color = LightText)
                                        }
                                    }

                                    if (stressLogs.isNotEmpty()) {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.Black),
                                            modifier = Modifier.fillMaxWidth().height(115.dp)
                                        ) {
                                            LazyColumn(
                                                modifier = Modifier.padding(6.dp).fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                items(stressLogs) { log ->
                                                    Text(log, fontSize = 8.5.sp, color = if (log.contains("SUCCESS") || log.contains("OK")) VerifiedGreen else if (log.contains("ALERT")) WarningAmber else LightText, fontFamily = FontFamily.Monospace)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("📩 Pending Manual Payment Audits", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText, modifier = Modifier.padding(top = 10.dp))
                        }

                        val pendingPayments = manualPaymentsList.filter { it.status == "Pending" }
                        if (pendingPayments.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("No pending payments for audit currently.", fontSize = 11.sp, color = GreyText, modifier = Modifier.padding(12.dp))
                                }
                            }
                        } else {
                            items(pendingPayments) { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, AccentTeal.copy(0.3f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("From: ${item.userName} (${item.userEmail})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = LightText)
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(0.1f)),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(item.paymentMethod, color = AccentTeal, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("TxnID: ${item.transactionId}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryEmerald)
                                        Text("Sender Number: ${item.senderNumber}", fontSize = 11.sp, color = LightText)
                                        Text("Amount: ${item.amount} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = VerifiedGreen)
                                        Text("Purpose: ${item.purpose}", fontSize = 10.sp, color = GreyText)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.respondToManualPayment(item.id, "Approved") { msg ->
                                                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Approve", fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.respondToManualPayment(item.id, "Rejected") { msg ->
                                                        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val auditLedger by viewModel.financialLedger.collectAsState()
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, PrimaryEmerald.copy(0.25f), RoundedCornerShape(12.dp))
                            ) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lock, contentDescription = "Security Audit", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("🔍 Secure Double-Entry Ledger Auditor", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                                        }
                                        Card(colors = CardDefaults.cardColors(containerColor = PrimaryEmerald.copy(0.15f))) {
                                            Text("AUDITED 🛡️", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("Automatic 2.5% developer splits, platform escrow ledgers, and transaction uniqueness audits are recorded below.", fontSize = 9.sp, color = GreyText)
                                    HorizontalDivider(color = GreyText.copy(0.15f), thickness = 0.5.dp)

                                    auditLedger.forEach { rec ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)),
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(0.5.dp, GreyText.copy(0.1f), RoundedCornerShape(8.dp))
                                        ) {
                                            Column(Modifier.padding(10.dp)) {
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                    Text(rec.type, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = LightText)
                                                    Text("${rec.totalAmount} BDT", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VerifiedGreen)
                                                }
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("ID: ${rec.transactionId} | via ${rec.gatewayMethod}", fontSize = 9.sp, color = GreyText)
                                                    Text("Fee (2.5%): ${rec.platformCut} BDT", fontSize = 8.5.sp, color = AccentTeal)
                                                }
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("From: ${rec.senderName} ➜ ${rec.receiverName}", fontSize = 8.5.sp, color = LightText.copy(0.7f))
                                                    Text(rec.status, fontSize = 8.5.sp, color = VerifiedGreen, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("👥 Global Ecosystem Registrants", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText, modifier = Modifier.padding(top = 10.dp))
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardSlate, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search Users", tint = GreyText, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                BasicTextField(
                                    value = searchAdminUserQuery,
                                    onValueChange = { searchAdminUserQuery = it },
                                    textStyle = TextStyle(color = LightText, fontSize = 12.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    decorationBox = { innerTextField ->
                                        if (searchAdminUserQuery.isEmpty()) {
                                            Text("Search users by name, role or email...", color = GreyText, fontSize = 12.sp)
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        val filteredUsers = registeredUsers.filter {
                            searchAdminUserQuery.isEmpty() ||
                            it.username.contains(searchAdminUserQuery, ignoreCase = true) ||
                            it.email.contains(searchAdminUserQuery, ignoreCase = true) ||
                            it.role.name.contains(searchAdminUserQuery, ignoreCase = true)
                        }

                        if (filteredUsers.isEmpty()) {
                            item {
                                Text("No registered users match your filter.", fontSize = 11.sp, color = GreyText, modifier = Modifier.padding(8.dp))
                            }
                        } else {
                            items(filteredUsers) { regUser ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(regUser.username, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    if (regUser.isVerified) {
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("🛡️", fontSize = 12.sp)
                                                    }
                                                }
                                                Text("${regUser.email} | ${regUser.phone}", fontSize = 10.sp, color = GreyText)
                                                Text("Role: ${regUser.role} | Wallet: ${regUser.walletBalance} BDT", fontSize = 10.sp, color = LightText)
                                                Text("National Trust Score: ${regUser.trustScore}/100 🏅", fontSize = 10.sp, color = SoftEmerald, fontWeight = FontWeight.Bold)
                                            }

                                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Button(
                                                        onClick = { viewModel.setUserLockStatus(regUser.email, !regUser.isLocked) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = if (regUser.isLocked) VerifiedGreen else ErrorCrimson),
                                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Text(if (regUser.isLocked) "Unlock" else "Lock Account", fontSize = 9.sp)
                                                    }
                                                    Button(
                                                        onClick = { viewModel.adminDirectVerifyUser(regUser.email) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                                        enabled = !regUser.isVerified,
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Text("Direct Verify", fontSize = 9.sp)
                                                    }
                                                }

                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Button(
                                                        onClick = {
                                                            val success = viewModel.deleteUserByAdmin(regUser.email)
                                                            coroutineScope.launch {
                                                                if (success) {
                                                                    snackbarHostState.showSnackbar("User ${regUser.username} purged from ecosystem.")
                                                                } else {
                                                                    snackbarHostState.showSnackbar("Cannot delete protected system admin!")
                                                                }
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson.copy(0.2f), contentColor = ErrorCrimson),
                                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Text("Purge Account", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            editingScoreUserEmail = regUser.email
                                                            trustScoreInputValue = regUser.trustScore.toString()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = InfoSky.copy(0.2f)),
                                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                                        modifier = Modifier.height(28.dp)
                                                    ) {
                                                        Text("Modify Trust", fontSize = 9.sp, color = InfoSky, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        if (editingScoreUserEmail == regUser.email) {
                                            Spacer(Modifier.height(8.dp))
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.1f)),
                                                modifier = Modifier.fillMaxWidth().border(1.dp, InfoSky.copy(0.4f), RoundedCornerShape(8.dp))
                                            ) {
                                                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    OutlinedTextField(
                                                        value = trustScoreInputValue,
                                                        onValueChange = { trustScoreInputValue = it },
                                                        placeholder = { Text("Trust (0-100)") },
                                                        modifier = Modifier.weight(1f).height(46.dp),
                                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = InfoSky),
                                                        textStyle = TextStyle(fontSize = 12.sp)
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Button(
                                                            onClick = {
                                                                val inputScore = trustScoreInputValue.toIntOrNull()
                                                                if (inputScore != null && inputScore in 0..100) {
                                                                    viewModel.adminOverrideTrustScore(regUser.email, inputScore)
                                                                    editingScoreUserEmail = null
                                                                    coroutineScope.launch { snackbarHostState.showSnackbar("Ecosystem Trust score overridden successfully.") }
                                                                } else {
                                                                    coroutineScope.launch { snackbarHostState.showSnackbar("Trust score must be 0 to 100.") }
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = InfoSky),
                                                            modifier = Modifier.height(32.dp)
                                                        ) {
                                                            Text("Apply", fontSize = 10.sp)
                                                        }
                                                        Button(
                                                            onClick = { editingScoreUserEmail = null },
                                                            colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                                            modifier = Modifier.height(32.dp)
                                                        ) {
                                                            Text("Cancel", fontSize = 10.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (regUser.uploadedDocs.isNotEmpty()) {
                                            Spacer(Modifier.height(6.dp))
                                            Text("Uploaded Documents Verification Sub-Panel:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GreyText)
                                            regUser.uploadedDocs.forEach { doc ->
                                                Card(colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.4f)), modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                                    Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text("${doc.docType} (${doc.fileName})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                            Text("Status: ${doc.status} | comments: ${doc.comments}", fontSize = 9.sp, color = GreyText)
                                                        }
                                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            Button(
                                                                onClick = { viewModel.updateTenantDocumentStatus(regUser.email, doc.id, "Approved", "Audited successfully via manual check") },
                                                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                                modifier = Modifier.height(24.dp)
                                                            ) {
                                                                Text("Approve", fontSize = 8.sp)
                                                            }
                                                            Button(
                                                                onClick = { viewModel.updateTenantDocumentStatus(regUser.email, doc.id, "Rejected", "Document credentials invalid") },
                                                                colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                                modifier = Modifier.height(24.dp)
                                                            ) {
                                                                Text("Reject", fontSize = 8.sp)
                                                            }
                                                            Button(
                                                                onClick = { viewModel.updateTenantDocumentStatus(regUser.email, doc.id, "Resubmit", "Document scan fuzzy. Please upload high contrast scan.") },
                                                                colors = ButtonDefaults.buttonColors(containerColor = InfoSky),
                                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                                modifier = Modifier.height(24.dp)
                                                            ) {
                                                                Text("Resubmit", fontSize = 8.sp)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("Verified Landlords Operations", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText, modifier = Modifier.padding(top = 10.dp))
                        }

                        items(totalListingsCountByAdmin) { l ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(l.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("${l.rentAmount} BDT | Owner: ${l.ownerName}", fontSize = 9.sp, color = GreyText)
                                        Row(modifier = Modifier.padding(top = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                if (l.isScamFlaggedByAI || l.isScamFlaggedByUsers) "🚨 Flagged Scam" else "🟢 Clean Score",
                                                color = if (l.isScamFlaggedByAI || l.isScamFlaggedByUsers) ErrorCrimson else VerifiedGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                if (l.isVerifiedOwner) "🛡️ Verified" else "⚠️ Self-logged",
                                                color = if (l.isVerifiedOwner) VerifiedGreen else WarningAmber,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Button(
                                            onClick = { viewModel.verifyListingByAdmin(l.id, !l.isVerifiedOwner) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (l.isVerifiedOwner) WarningAmber else VerifiedGreen),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(if (l.isVerifiedOwner) "Unverify" else "Verify", fontSize = 9.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.toggleScamFlag(l.id, !(l.isScamFlaggedByAI || l.isScamFlaggedByUsers)) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Flag", fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Automated REST Gateway Overlay Dialog simulation
                    if (showBkashCheckoutOverlay) {
                        AlertDialog(
                            onDismissRequest = { showBkashCheckoutOverlay = false },
                            confirmButton = {},
                            dismissButton = {
                                TextButton(onClick = { showBkashCheckoutOverlay = false }) {
                                    Text("Close Gateway Session", color = ErrorCrimson)
                                }
                            },
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(Color(0xFFE2125F)).padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🇧🇩 REST API CHECKOUT SECURE GATEWAY", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Text("Secure Merchant: Rent Truth BD Platform", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Charge Amount: ${bkashCheckoutAmount} BDT", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFFE2125F))
                                    Text("Invoice Reference: ${bkashCheckoutPurpose}", fontSize = 9.sp, color = GreyText)
                                    HorizontalDivider(color = GreyText.copy(0.15f))

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        listOf("bKash API REST", "Nagad REST Wallet-Secure", "Upay REST Sandbox").forEach { gw ->
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = if (checkoutGatewayType == gw) Color(0xFFE2125F).copy(0.1f) else CardSlate.copy(0.4f)),
                                                modifier = Modifier.weight(1f).clickable { checkoutGatewayType = gw }.border(1.dp, if (checkoutGatewayType == gw) Color(0xFFE2125F) else Color.Transparent, RoundedCornerShape(8.dp))
                                            ) {
                                                Text(gw.substringBefore(" "), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (checkoutGatewayType == gw) Color(0xFFE2125F) else LightText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp).align(Alignment.CenterHorizontally))
                                            }
                                        }
                                    }

                                    if (checkoutStep == 1) {
                                        Text("Verify Your Account Mobile Number:", fontSize = 9.sp, color = GreyText)
                                        OutlinedTextField(
                                            value = checkoutSenderNo,
                                            onValueChange = { checkoutSenderNo = it },
                                            placeholder = { Text("Wallet Phone Number") },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            textStyle = TextStyle(fontSize = 11.sp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Button(
                                            onClick = { checkoutStep = 2 },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2125F)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Send OTP Security Verification Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (checkoutStep == 2) {
                                        Text("Enter 6-Digit SMS Verification Pin Code (OTP):", fontSize = 9.sp, color = GreyText)
                                        OutlinedTextField(
                                            value = bkashOtpInput,
                                            onValueChange = { bkashOtpInput = it },
                                            placeholder = { Text("SMS OTP (e.g. 199284)") },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            textStyle = TextStyle(fontSize = 11.sp)
                                        )
                                        Text("Enter 5-Digit Mobile Money Secure Wallet PIN:", fontSize = 9.sp, color = GreyText)
                                        OutlinedTextField(
                                            value = bkashPinInput,
                                            onValueChange = { bkashPinInput = it },
                                            placeholder = { Text("Wallet PIN (•••••)") },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            textStyle = TextStyle(fontSize = 11.sp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Button(
                                            onClick = {
                                                viewModel.executeAutomatedGatewayCheckout(
                                                    senderNo = checkoutSenderNo,
                                                    amount = bkashCheckoutAmount,
                                                    purpose = bkashCheckoutPurpose,
                                                    gateway = checkoutGatewayType
                                                ) { result ->
                                                    if (result.contains("Success")) {
                                                        showBkashCheckoutOverlay = false
                                                        checkoutStep = 1
                                                        bkashOtpInput = ""
                                                        bkashPinInput = ""
                                                        coroutineScope.launch { snackbarHostState.showSnackbar("Payment processed through REST gateway successfully!") }
                                                    } else {
                                                        coroutineScope.launch { snackbarHostState.showSnackbar(result) }
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2125F)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Confirm REST Transaction Settlement", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    var senderNumberInput by remember { mutableStateOf("") }
    var transactionIdInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var paymentPurpose by remember { mutableStateOf("Recharge") }

    if (showRechargeDialog) {
        AlertDialog(
            onDismissRequest = { showRechargeDialog = false },
            title = {
                Text(
                    "🇧🇩 Manual Mobile Money System",
                    color = PrimaryEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Text("💸 Step 1: Send Money / Cash Out", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentTeal)
                                Text("Please send the exact BDT amount first:", fontSize = 10.sp, color = GreyText)
                                Spacer(Modifier.height(4.dp))
                                Text("• bKash Personal: 01700-112233", fontSize = 11.sp, color = LightText, fontWeight = FontWeight.Bold)
                                Text("• Nagad Personal: 01900-334455", fontSize = 11.sp, color = LightText, fontWeight = FontWeight.Bold)
                                Text("• Charge: No extra cash-out fees required.", fontSize = 9.sp, color = GreyText)
                            }
                        }
                    }

                    item {
                        Text("Select Platform Route:", fontSize = 10.sp, color = GreyText)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("bKash", "Nagad").forEach { m ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedMethod == m) PrimaryEmerald.copy(0.15f) else CardSlate
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMethod = m }
                                        .border(
                                            1.dp,
                                            if (selectedMethod == m) PrimaryEmerald else GreyText.copy(0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        m,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedMethod == m) PrimaryEmerald else LightText,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Select Your Intended Purpose:", fontSize = 10.sp, color = GreyText)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                "Recharge" to "Top UP",
                                "Upgrade_Student" to "Student",
                                "Upgrade_Owner" to "Landlord",
                                "Upgrade_Broker" to "Broker"
                            ).forEach { (p, label) ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (paymentPurpose == p) AccentTeal.copy(0.15f) else CardSlate
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { paymentPurpose = p }
                                        .border(
                                            1.dp,
                                            if (paymentPurpose == p) AccentTeal else GreyText.copy(0.2f),
                                            RoundedCornerShape(6.dp)
                                        )
                                ) {
                                    Text(
                                        label,
                                        fontSize = 8.2.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (paymentPurpose == p) AccentTeal else LightText,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = rechargeAmountInput,
                            onValueChange = { rechargeAmountInput = it },
                            label = { Text("Amount to Deposit (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("recharge_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = senderNumberInput,
                            onValueChange = { senderNumberInput = it },
                            label = { Text("Sender Mobile Number") },
                            placeholder = { Text("017********") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("recharge_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = transactionIdInput,
                            onValueChange = { transactionIdInput = it },
                            label = { Text("Transaction ID (TxnID)") },
                            placeholder = { Text("e.g. TRK92HSD72") },
                            modifier = Modifier.fillMaxWidth().testTag("recharge_tx_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                focusedLabelColor = PrimaryEmerald
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = rechargeAmountInput.toIntOrNull() ?: 0
                        if (amt > 0 && senderNumberInput.isNotBlank() && transactionIdInput.isNotBlank()) {
                            viewModel.submitManualPayment(
                                senderNumber = senderNumberInput,
                                transactionId = transactionIdInput,
                                amount = amt,
                                paymentMethod = selectedMethod,
                                purpose = paymentPurpose
                            ) { msg ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                            senderNumberInput = ""
                            transactionIdInput = ""
                            rechargeAmountInput = ""
                            showRechargeDialog = false
                        } else {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Please fill all payment fields correctly!") }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.testTag("submit_manual_recharge_button")
                ) {
                    Text("Submit for Verification")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRechargeDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SubscriptionOptionCard(
    title: String,
    priceDesc: String,
    desc: String,
    isCurrent: Boolean,
    onUpgrade: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isCurrent) PrimaryEmerald.copy(0.12f) else CardSlate),
        modifier = Modifier.fillMaxWidth().border(1.dp, if (isCurrent) PrimaryEmerald else Color.Transparent, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LightText)
                Text(priceDesc, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = SoftEmerald)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 10.sp, color = GreyText)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(containerColor = if (isCurrent) VerifiedGreen else PrimaryEmerald),
                modifier = Modifier.fillMaxWidth().height(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(if (isCurrent) "Current Active Badge 🟢" else "Purchase & Activate Platform Shield", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun RealBangladeshMapView(
    listings: List<RentListing>,
    onSelectListing: (RentListing) -> Unit
) {
    var selectedPointListing by remember { mutableStateOf<RentListing?>(null) }
    var focusedDivision by remember { mutableStateOf("All") }
    var activeLayer by remember { mutableStateOf("Standard") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "🗺️ Rent Truth BD: Free National Bangladesh Map",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LightText
                )
                Text(
                    "Interactive division nodes, visual pin overlays, and geographic checks (Padma-Meghna rivers and Bay of Bengal)",
                    fontSize = 10.sp,
                    color = GreyText
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("All", "Dhaka", "Chittagong", "Sylhet", "Rajshahi", "Khulna").forEach { div ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (focusedDivision == div) PrimaryEmerald.copy(0.15f) else CardSlate
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { focusedDivision = div }
                                .border(
                                    1.dp,
                                    if (focusedDivision == div) PrimaryEmerald else GreyText.copy(0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = div,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (focusedDivision == div) PrimaryEmerald else LightText,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    "🗺️ Intelligence Overlay Map Layer:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "Standard" to "📍 Standard Map",
                        "Family" to "👨‍👩‍👧‍👦 Family Zones",
                        "Student" to "🎓 Student Hubs",
                        "Office" to "💻 Office Sector",
                        "Premium" to "💎 Elite Spots",
                        "FloodScam" to "🚨 Flood & Scam Radar"
                    ).forEach { (layId, layName) ->
                        val isSel = activeLayer == layId
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) AccentTeal.copy(0.15f) else CardSlate.copy(0.7f)
                            ),
                            modifier = Modifier
                                .clickable { activeLayer = layId }
                                .border(
                                    1.dp,
                                    if (isSel) AccentTeal else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Text(
                                text = layName,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) AccentTeal else LightText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFE1E2E9).copy(0.15f), RoundedCornerShape(20.dp))
                .background(DeepNavyBg)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Bay of Bengal (Ocean water representation)
                    drawRect(
                        color = Color(0xFF1E3A8A).copy(0.25f),
                        size = androidx.compose.ui.geometry.Size(w, h * 0.18f),
                        topLeft = Offset(0f, h * 0.82f)
                    )

                    // Padma River
                    val padmaPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.1f, h * 0.45f)
                        quadraticTo(w * 0.35f, h * 0.52f, w * 0.55f, h * 0.62f)
                        quadraticTo(w * 0.65f, h * 0.68f, w * 0.68f, h * 0.82f)
                    }
                    drawPath(
                        path = padmaPath,
                        color = Color(0xFF3B82F6).copy(0.4f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )

                    // Jamuna River
                    val jamunaPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.44f, 0f)
                        quadraticTo(w * 0.42f, h * 0.28f, w * 0.44f, h * 0.48f)
                        quadraticTo(w * 0.45f, h * 0.51f, w * 0.55f, h * 0.62f)
                    }
                    drawPath(
                        path = jamunaPath,
                        color = Color(0xFF3B82F6).copy(0.4f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )

                    // Meghna River
                    val meghnaPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.78f, h * 0.2f)
                        quadraticTo(w * 0.72f, h * 0.42f, w * 0.58f, h * 0.62f)
                    }
                    drawPath(
                        path = meghnaPath,
                        color = Color(0xFF3B82F6).copy(0.4f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )

                    // Radar Circles for Major Divisional Centers
                    val divisionsCoords = listOf(
                        "Dhaka" to Offset(w * 0.5f, h * 0.45f),
                        "Chittagong" to Offset(w * 0.78f, h * 0.72f),
                        "Sylhet" to Offset(w * 0.82f, h * 0.25f),
                        "Rajshahi" to Offset(w * 0.22f, h * 0.32f),
                        "Khulna" to Offset(w * 0.26f, h * 0.68f)
                    )
                    divisionsCoords.forEach { (name, center) ->
                        drawCircle(
                            color = Color(0xFF10B981).copy(0.12f),
                            center = center,
                            radius = w * 0.08f
                        )
                        drawCircle(
                            color = Color(0xFF10B981).copy(0.35f),
                            center = center,
                            radius = 6f
                        )
                    }

                    // Intelligent Layer Highlights
                    when (activeLayer) {
                        "Family" -> {
                            drawCircle(
                                color = Color(0xFF10B981).copy(0.22f),
                                center = Offset(w * 0.51f, h * 0.45f),
                                radius = w * 0.10f
                            )
                        }
                        "Student" -> {
                            drawCircle(
                                color = Color(0xFF06B6D4).copy(0.22f),
                                center = Offset(w * 0.42f, h * 0.47f),
                                radius = w * 0.09f
                            )
                        }
                        "Office" -> {
                            drawCircle(
                                color = Color(0xFFF59E0B).copy(0.22f),
                                center = Offset(w * 0.52f, h * 0.44f),
                                radius = w * 0.09f
                            )
                        }
                        "Premium" -> {
                            drawCircle(
                                color = Color(0xFFEAB308).copy(0.27f),
                                center = Offset(w * 0.54f, h * 0.46f),
                                radius = w * 0.08f
                            )
                        }
                        "FloodScam" -> {
                            drawCircle(
                                color = Color(0xFFEF4444).copy(0.27f),
                                center = Offset(w * 0.44f, h * 0.52f),
                                radius = w * 0.05f
                            )
                            drawCircle(
                                color = Color(0xFFEF4444).copy(0.27f),
                                center = Offset(w * 0.40f, h * 0.42f),
                                radius = w * 0.06f
                            )
                        }
                    }
                }

                // Place labels at precise division center coordinates
                NeighborhoodText(name = "RAJSHAHI\nDivisional Region", x = 0.12f, y = 0.26f)
                NeighborhoodText(name = "DHAKA HQ\nCapital Center", x = 0.48f, y = 0.46f)
                NeighborhoodText(name = "SYLHET\nEastern Tea Zone", x = 0.76f, y = 0.20f)
                NeighborhoodText(name = "KHULNA\nMangrove Wetlands", x = 0.15f, y = 0.64f)
                NeighborhoodText(name = "CHITTAGONG\nMaritime Hub", x = 0.74f, y = 0.76f)
                NeighborhoodText(name = "BAY OF BENGAL\nDeep Blue Sea", x = 0.36f, y = 0.88f)

                // Loop through listings to plot pins dynamically
                listings.forEach { l ->
                    val showOnFilter = focusedDivision == "All" || l.division.equals(focusedDivision, ignoreCase = true)
                    if (showOnFilter) {
                        // Plot coordinates based on Division details
                        val calculatedX = when (l.division.lowercase().trim()) {
                            "dhaka" -> {
                                when (l.area) {
                                    "Dhanmondi" -> 0.44f
                                    "Mirpur" -> 0.40f
                                    "Gulshan" -> 0.54f
                                    "Banani" -> 0.48f
                                    "Bashundhara" -> 0.56f
                                    "Uttara" -> 0.50f
                                    else -> 0.48f
                                }
                            }
                            "chittagong", "chattogram" -> 0.76f + ((l.id * 13) % 10 - 5) * 0.012f
                            "sylhet" -> 0.80f + ((l.id * 17) % 10 - 5) * 0.012f
                            "rajshahi" -> 0.22f + ((l.id * 23) % 10 - 5) * 0.012f
                            "khulna" -> 0.26f + ((l.id * 29) % 10 - 5) * 0.012f
                            else -> 0.48f
                        }

                        val calculatedY = when (l.division.lowercase().trim()) {
                            "dhaka" -> {
                                when (l.area) {
                                    "Dhanmondi" -> 0.52f
                                    "Mirpur" -> 0.42f
                                    "Gulshan" -> 0.46f
                                    "Banani" -> 0.44f
                                    "Bashundhara" -> 0.40f
                                    "Uttara" -> 0.36f
                                    else -> 0.45f
                                }
                            }
                            "chittagong", "chattogram" -> 0.70f + ((l.id * 7) % 10 - 5) * 0.012f
                            "sylhet" -> 0.24f + ((l.id * 11) % 10 - 5) * 0.012f
                            "rajshahi" -> 0.30f + ((l.id * 19) % 10 - 5) * 0.012f
                            "khulna" -> 0.62f + ((l.id * 31) % 10 - 5) * 0.012f
                            else -> 0.45f
                        }

                        Box(
                            modifier = Modifier
                                .absoluteOffset(
                                    x = boxWidth * calculatedX - 41.dp,
                                    y = boxHeight * calculatedY - 30.dp
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedPointListing = l }
                                    .width(82.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (l.isScamFlaggedByAI || l.isScamFlaggedByUsers) ErrorCrimson else CardSlate
                                    ),
                                    modifier = Modifier.border(
                                        1.dp,
                                        if (l.isVerifiedOwner) VerifiedGreen else WarningAmber,
                                        RoundedCornerShape(8.dp)
                                    )
                                ) {
                                    Text(
                                        "${l.rentAmount} ৳",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                
                                Text(
                                    text = if (l.isScamFlaggedByAI || l.isScamFlaggedByUsers) "🚨" else "📌",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                
                                Text(
                                    text = "${l.division}:${l.area}",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.background(Color.Black.copy(0.4f), RoundedCornerShape(2.dp)).padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Text(
                "Click pin 📌 to start localized geo-diagnose",
                fontSize = 10.sp,
                color = GreyText.copy(0.8f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }

        AnimatedVisibility(
            visible = selectedPointListing != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            val point = selectedPointListing!!
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .border(1.dp, Color(0xFFE1E2E9), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            point.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${point.rentAmount} BDT/month",
                                fontSize = 11.sp,
                                color = SoftEmerald,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "in ${point.area}",
                                fontSize = 10.sp,
                                color = GreyText
                            )
                        }
                        if (point.isScamFlaggedByAI || point.isScamFlaggedByUsers) {
                            Text(
                                "🚨 Scammed Alert: Marked fake ad by mod",
                                color = ErrorCrimson,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (point.isVerifiedOwner) {
                            Text(
                                "🛡️ Verified safe landlord property",
                                color = VerifiedGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { onSelectListing(point) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text("Diagnose", fontSize = 11.sp)
                    }
                }
            }
        }

        // Intelligence Area Metrics Dashboard
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth().testTag("map_analytics_dashboard"),
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            border = BorderStroke(1.dp, AccentTeal.copy(0.25f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = when (activeLayer) {
                        "Standard" -> "📍 Standard Ecosystem Coverage"
                        "Family" -> "👨‍👩‍👧‍👦 Family Zones Intelligence Dashboard"
                        "Student" -> "🎓 Student Hubs Density Index"
                        "Office" -> "💻 Corporate Rental Analytics"
                        "Premium" -> "💎 Premium District Specifications"
                        "FloodScam" -> "🚨 National Flood & Scam Risk Warnings"
                        else -> "📍 General Analytics"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentTeal
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = when (activeLayer) {
                        "Standard" -> "Active Verified Listings: ${listings.size} properties in Bangladesh. National visual coverage: 100% principal divisions."
                        "Family" -> "👨‍👩‍👧‍‍👦 Gulshan & Banani score 98% for family suitability with 24/7 guarded security, high water pressure & gas supplies. Average Rent: 35k-80k BDT. Accessibility: Top schools & clinics within 1.5 km."
                        "Student" -> "🎓 Dhanmondi & Mirpur host the most student hostels/bachelors with monthly shared rents of 4.5k-12k BDT. Commute index: University shuttle route access and cheap rickshaw priorities."
                        "Office" -> "💻 Motijheel & Karwan Bazar host 80% of corporate commercial flats. High Metrorail route convenience. Legal checklist: 5% standard corporate tax certificate verification required."
                        "Premium" -> "💎 Gulshan 2 & Baridhara Diplomatic Zone. Mandatory requirements: Central CCTV surveillance, backup generator units. Safety metrics: 97% biometric verified security desk checkin."
                        "FloodScam" -> "🚨 Safety Warnings: Dhanmondi Sector 2-3 logs moderate road waterlogging during high seasonal monsoons. Mirpur 11 and Sector 10 have duplicate pricing attempts (5% average scam index)."
                        else -> ""
                    },
                    fontSize = 10.5.sp,
                    color = LightText
                )
            }
        }
    }
}

@Composable
fun BoxScope.NeighborhoodText(name: String, x: Float, y: Float) {
    Text(
        text = name,
        fontWeight = FontWeight.Black,
        fontSize = 8.sp,
        color = GreyText.copy(0.35f),
        lineHeight = 9.sp,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = (x * 320).dp, top = (y * 420).dp)
    )
}

@Composable
fun ChatHubView(
    viewModel: RentViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val chatChannels by viewModel.chatChannels.collectAsState()
    val selectedChannelId by viewModel.selectedChannelId.collectAsState()

    var chatMessageInput by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Email, contentDescription = "Mail", tint = GreyText, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "Access Secure Community Chats",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LightText
            )
            Text(
                "Please sign in first inside the 'Account' tab to negotiate leases and talk with owners/roommates.",
                fontSize = 11.sp,
                color = GreyText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 16.dp)
            )
        }
    } else {
        if (selectedChannelId == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = "Verified Channels", tint = PrimaryEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("🛡️ End-to-End Audited Communications", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("All communications are monitored to safeguard against scams and false pricing.", fontSize = 9.sp, color = GreyText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatChannels) { ch ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectChannel(ch.id) }
                                .border(0.5.dp, GreyText.copy(0.15f), RoundedCornerShape(12.dp))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryEmerald.copy(0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        ch.partnerName.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryEmerald
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(ch.partnerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = when (ch.partnerRole) {
                                                UserRole.OWNER -> InfoSky.copy(0.15f)
                                                UserRole.BROKER -> WarningAmber.copy(0.15f)
                                                else -> PrimaryEmerald.copy(0.15f)
                                            })
                                        ) {
                                            Text(
                                                ch.partnerRole.name,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                color = when (ch.partnerRole) {
                                                    UserRole.OWNER -> InfoSky
                                                    UserRole.BROKER -> WarningAmber
                                                    else -> PrimaryEmerald
                                                }
                                            )
                                        }
                                    }
                                    if (ch.listingTitle.isNotBlank()) {
                                        Text(ch.listingTitle, fontSize = 9.sp, color = AccentTeal, maxLines = 1)
                                    }
                                    Text(ch.lastMessage, fontSize = 11.sp, color = GreyText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val openCh = chatChannels.find { it.id == selectedChannelId }
            if (openCh == null) {
                viewModel.selectChannel(null)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.selectChannel(null) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LightText)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(openCh.partnerName, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                Text(
                                    if (openCh.listingTitle.isNotBlank()) "Regarding: ${openCh.listingTitle}" else openCh.partnerRole.name,
                                    fontSize = 9.sp,
                                    color = AccentTeal,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(openCh.messages) { m ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (m.sender == "me") Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (m.sender == "me") PrimaryEmerald else CardSlate
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = m.text,
                                            fontSize = 11.5.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = m.timestamp,
                                            fontSize = 8.sp,
                                            color = GreyText.copy(0.8f),
                                            modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatMessageInput,
                            onValueChange = { chatMessageInput = it },
                            placeholder = { Text("Ask verification, utilities, or timings...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = GreyText.copy(0.4f),
                                focusedLabelColor = PrimaryEmerald
                            )
                        )
                        Button(
                            onClick = {
                                if (chatMessageInput.isNotBlank()) {
                                    viewModel.sendMessage(chatMessageInput)
                                    chatMessageInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Send", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatRow(label: String, amount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 10.5.sp, color = LightText)
        Text("${amount} ৳", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftEmerald)
    }
}

@Composable
fun StaticBillingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = LightText)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftEmerald)
    }
}
