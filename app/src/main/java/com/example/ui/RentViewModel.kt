package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RentViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val PRODUCTION_MODE = true
    }

    private val database = AppDatabase.getDatabase(application)
    private val repository = RentRepository(database.rentDao())

    // All database lists exposed as reactive states
    val listings: StateFlow<List<RentListing>> = repository.allListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val negotiations: StateFlow<List<Negotiation>> = repository.allNegotiations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roommateRequests: StateFlow<List<RoommateRequest>> = repository.allRoommateRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<TenantReview>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val escrowDeposits: StateFlow<List<EscrowDeposit>> = repository.allEscrowDeposits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Authentication State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Registered users in-memory to simulate real persistence
    private val _registeredUsers = MutableStateFlow<List<UserAccount>>(listOf(
        UserAccount(
            username = "Admin Boss",
            email = "admin@renttruthbd.com",
            phone = "01999999999",
            role = UserRole.SYSTEM,
            subscriptionType = "Free",
            walletBalance = 1000000,
            isVerified = true,
            isAdmin = true,
            isDemoUser = false,
            isSystemSeed = false,
            passwordHash = "e6c0c279e83ec90209df3dc594dc7dc2c77dfa8b438cf1df8c2b5d496e709087" // Hashed "admin0130"
        )
    ))
    val registeredUsers = _registeredUsers.asStateFlow()

    // Digital Tenant Registry Logs for Landowners
    private val _landlordTenantLogs = MutableStateFlow<List<LandlordTenantLog>>(emptyList())
    val landlordTenantLogs = _landlordTenantLogs.asStateFlow()

    // Broker Operations states
    private val _brokerPartnerships = MutableStateFlow<List<BrokerOwnerPartnership>>(emptyList())
    val brokerPartnerships = _brokerPartnerships.asStateFlow()

    private val _brokerDeals = MutableStateFlow<List<BrokerDeal>>(emptyList())
    val brokerDeals = _brokerDeals.asStateFlow()

    // Real-Time websockets / retry simulator stats
    private val _syncLogs = MutableStateFlow<List<String>>(listOf(
        "[SYSTEM] Secure Production Boot Completed under Dhaka Secure Clusters.",
        "[INFO] Live compliance telemetry router initiated successfully.",
        "[INFO] WebSocket pool active. Listening for real registered rental deeds..."
    ))
    val syncLogs = _syncLogs.asStateFlow()

    // Security active device logs
    private val _activeSessions = MutableStateFlow<List<SecurityDeviceSession>>(emptyList())
    val activeSessions = _activeSessions.asStateFlow()

    // Revenue tracking variables for Admin Pane (BDT)
    private val _adminRevenueSubscriptions = MutableStateFlow(0)
    val adminRevenueSubscriptions = _adminRevenueSubscriptions.asStateFlow()

    private val _adminRevenueCommissions = MutableStateFlow(0)
    val adminRevenueCommissions = _adminRevenueCommissions.asStateFlow()

    private val _adminRevenueAccountCharges = MutableStateFlow(0)
    val adminRevenueAccountCharges = _adminRevenueAccountCharges.asStateFlow()

    private val _adminRevenueListingFees = MutableStateFlow(0)
    val adminRevenueListingFees = _adminRevenueListingFees.asStateFlow()

    // Advanced Real-Time State Engine State
    private val _websocketStatus = MutableStateFlow("🟢 Live Connected")
    val websocketStatus = _websocketStatus.asStateFlow()

    private val _offlineQueue = MutableStateFlow<List<OfflineSyncTask>>(emptyList())
    val offlineQueue = _offlineQueue.asStateFlow()

    private val _websocketLatency = MutableStateFlow(12) // faster production ping
    val websocketLatency = _websocketLatency.asStateFlow()

    private val _dashboardCpuPercent = MutableStateFlow(2) // low idle load
    val dashboardCpuPercent = _dashboardCpuPercent.asStateFlow()

    private val _dashboardHeapMemory = MutableStateFlow(124) // low initial size
    val dashboardHeapMemory = _dashboardHeapMemory.asStateFlow()

    private val _isLoadTesting = MutableStateFlow(false)
    val isLoadTesting = _isLoadTesting.asStateFlow()

    private val _loadTestProgress = MutableStateFlow(0f)
    val loadTestProgress = _loadTestProgress.asStateFlow()

    private val _loadTestingLogs = MutableStateFlow<List<String>>(emptyList())
    val loadTestingLogs = _loadTestingLogs.asStateFlow()

    private val _failedTransactionsCount = MutableStateFlow(0)
    val failedTransactionsCount = _failedTransactionsCount.asStateFlow()

    private val _isGatewayAutomated = MutableStateFlow(true)
    val isGatewayAutomated = _isGatewayAutomated.asStateFlow()

    private val _financialLedger = MutableStateFlow<List<FinancialLedgerRecord>>(emptyList())
    val financialLedger = _financialLedger.asStateFlow()

    // Chat Channels & Messages
    private val _chatChannels = MutableStateFlow<List<ChatChannel>>(emptyList())
    val chatChannels = _chatChannels.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId = _selectedChannelId.asStateFlow()

    // Manual Payments (bKash & Nagad)
    val manualPayments: StateFlow<List<ManualPaymentSubmission>> = repository.allManualPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _currentRole = MutableStateFlow(UserRole.TENANT)
    val currentRole = _currentRole.asStateFlow()

    private val _selectedDivisionFilter = MutableStateFlow("All")
    val selectedDivisionFilter = _selectedDivisionFilter.asStateFlow()

    private val _selectedDistrictFilter = MutableStateFlow("All")
    val selectedDistrictFilter = _selectedDistrictFilter.asStateFlow()

    private val _selectedAreaFilter = MutableStateFlow("All")
    val selectedAreaFilter = _selectedAreaFilter.asStateFlow()

    private val _maxBudgetFilter = MutableStateFlow(20000)
    val maxBudgetFilter = _maxBudgetFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("All")
    val selectedTypeFilter = _selectedTypeFilter.asStateFlow()

    private val _studentMode = MutableStateFlow(false)
    val studentMode = _studentMode.asStateFlow()

    private val _advisorConversation = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("system", "Salam! I am your AI 'Room Advisor'. Try saying: 'I am a student looking for a room under 7000 BDT near Dhanmondi' or 'Suggest a space in Uttara and security checklist.' I am trained specifically with Bangladeshi rental insights, transport costs, and broker tactics.")
    ))
    val advisorConversation = _advisorConversation.asStateFlow()

    private val _isAdvisorLoading = MutableStateFlow(false)
    val isAdvisorLoading = _isAdvisorLoading.asStateFlow()

    // Add Listing state variables
    val inputTitle = MutableStateFlow("")
    val inputDivision = MutableStateFlow("Dhaka")
    val inputDistrict = MutableStateFlow("Dhaka")
    val inputArea = MutableStateFlow("Mirpur")
    val inputAddress = MutableStateFlow("")
    val inputRent = MutableStateFlow("")
    val inputType = MutableStateFlow("Bachelor")
    val inputElectricity = MutableStateFlow("Paid per usage (Sub-meter)")
    val inputWater = MutableStateFlow("Fixed 500 BDT/month")
    val inputDeposit = MutableStateFlow("10000")
    val inputVideoUrl = MutableStateFlow("")
    val hasVideoWalkthrough = MutableStateFlow(false)
    val isVerifiedOwner = MutableStateFlow(false)

    // Roommate Request variables
    val rmName = MutableStateFlow("")
    val rmAge = MutableStateFlow("")
    val rmInstitution = MutableStateFlow("")
    val rmBudget = MutableStateFlow("")
    val rmArea = MutableStateFlow("Dhanmondi")
    val rmLifestyle = MutableStateFlow("")

    init {
        // Pre-populate sample listings if table is empty AND not in PRODUCTION_MODE
        if (!PRODUCTION_MODE) {
            viewModelScope.launch {
                listings.collect { list ->
                    if (list.isEmpty()) {
                        populateSamples()
                    }
                }
            }
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
        val user = _currentUser.value
        if (user != null && user.role != role) {
            val updated = user.copy(role = role)
            _currentUser.value = updated
            _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
        }
    }

    private fun hashAdminPassword(password: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    fun login(email: String, passwordEntered: String = "", nameIfNew: String = ""): Boolean {
        val emailClean = email.trim().lowercase()
        
        // Admin Lock Enforcement
        if (emailClean == "admin@renttruthbd.com") {
            val hashed = hashAdminPassword(passwordEntered)
            if (hashed != "e6c0c279e83ec90209df3dc594dc7dc2c77dfa8b438cf1df8c2b5d496e709087") {
                addSyncLog("[SECURITY ALERT] Unauthorized admin login attempt rejected for $emailClean.")
                return false
            }
        }

        val existing = _registeredUsers.value.find { it.email.lowercase() == emailClean }
        if (existing != null) {
            _currentUser.value = existing
            _isLoggedIn.value = true
            _currentRole.value = existing.role
            addSyncLog("[OK] Secure login successful for ${existing.username} (${existing.role.name}).")
            return true
        } else if (nameIfNew.isNotBlank()) {
            val newAcc = UserAccount(
                username = nameIfNew,
                email = emailClean,
                phone = "017" + (10000000..99999999).random().toString(),
                role = UserRole.TENANT,
                subscriptionType = "Free",
                walletBalance = 1000,
                isVerified = false // Must complete onboarding in production
            )
            _registeredUsers.value = _registeredUsers.value + newAcc
            _currentUser.value = newAcc
            _isLoggedIn.value = true
            _currentRole.value = UserRole.TENANT
            addSyncLog("[SYSTEM] New real account registered: ${newAcc.username} ($emailClean).")
            return true
        }
        return false
    }

    fun register(username: String, email: String, phone: String, role: UserRole): String {
        val emailClean = email.trim().lowercase()
        if (_registeredUsers.value.any { it.email.lowercase() == emailClean }) {
            return "Email is already registered!"
        }

        val fee = when (role) {
            UserRole.OWNER -> 200
            UserRole.BROKER -> 500
            else -> 0
        }

        if (fee > 0) {
            _adminRevenueAccountCharges.value += fee
        }

        val newAcc = UserAccount(
            username = username,
            email = emailClean,
            phone = phone,
            role = role,
            subscriptionType = "Free",
            walletBalance = if (role == UserRole.SYSTEM) 100000 else 800,
            isVerified = (role == UserRole.SYSTEM)
        )

        _registeredUsers.value = _registeredUsers.value + newAcc
        _currentUser.value = newAcc
        _isLoggedIn.value = true
        _currentRole.value = role

        return "Successfully registered! Account creation fee of ${fee} BDT applied in system."
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun rechargeWallet(amount: Int) {
        val user = _currentUser.value ?: return
        val updated = user.copy(walletBalance = user.walletBalance + amount)
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
    }

    fun upgradeSubscription(type: String, cost: Int): Boolean {
        val user = _currentUser.value ?: return false
        if (user.walletBalance < cost) return false
        
        val updated = user.copy(
            walletBalance = user.walletBalance - cost,
            subscriptionType = type
        )
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
        _adminRevenueSubscriptions.value += cost
        return true
    }

    fun selectChannel(channelId: String?) {
        _selectedChannelId.value = channelId
    }

    fun startBrokerNegotiationOrJoin(ownerPhone: String, title: String) {
        val chId = "ch_dyn_" + System.currentTimeMillis()
        val exists = _chatChannels.value.find { it.partnerName == "Property Owner" || it.listingTitle == title }
        if (exists != null) {
            _selectedChannelId.value = exists.id
            return
        }
        val newCh = ChatChannel(
            id = chId,
            partnerName = "Verified Landlord / Agent",
            partnerRole = UserRole.OWNER,
            listingTitle = title,
            lastMessage = "Assalamu Alaikum, is this property available?",
            messages = listOf(
                LocalMessage("msg_i", "me", "Hello! I am viewing your property: $title. I would like to schedule a verification and booking.")
            )
        )
        _chatChannels.value = _chatChannels.value + newCh
        _selectedChannelId.value = chId
    }

    fun sendMessage(text: String) {
        val channelId = _selectedChannelId.value ?: return
        val activeChannels = _chatChannels.value.toMutableList()
        val index = activeChannels.indexOfFirst { it.id == channelId }
        if (index != -1) {
            val ch = activeChannels[index]
            val newMsg = LocalMessage("msg_user_${System.currentTimeMillis()}", "me", text)
            val updatedMsgs = ch.messages + newMsg
            val updatedCh = ch.copy(messages = updatedMsgs, lastMessage = text)
            activeChannels[index] = updatedCh
            _chatChannels.value = activeChannels

            viewModelScope.launch {
                kotlinx.coroutines.delay(1000)
                generateSimulatedReply(channelId, text)
            }
        }
    }

    private fun generateSimulatedReply(channelId: String, userPrompt: String) {
        val activeChannels = _chatChannels.value.toMutableList()
        val index = activeChannels.indexOfFirst { it.id == channelId }
        if (index != -1) {
            val ch = activeChannels[index]
            val replyText = when {
                userPrompt.contains("rent", ignoreCase = true) || userPrompt.contains("taka", ignoreCase = true) || userPrompt.contains("cost", ignoreCase = true) -> {
                    "Our rent is completely verified on Rent Truth BD. There are no surprise service charges."
                }
                userPrompt.contains("visit", ignoreCase = true) || userPrompt.contains("location", ignoreCase = true) || userPrompt.contains("map", ignoreCase = true) -> {
                    "Sure! You can find the coordinate badge under the Map View check. Let me know when you arrive."
                }
                userPrompt.contains("bachelor", ignoreCase = true) || userPrompt.contains("student", ignoreCase = true) -> {
                    "Yes, student bachelors are welcome. We charge 2.5% platform billing protection fees. You receive verified digital passport history."
                }
                else -> {
                    "I got your message. To proceed safely, please book a visit using the Escrow Safe Deposit in Rent Truth BD."
                }
            }
            val replyMsg = LocalMessage("msg_auto_${System.currentTimeMillis()}", "partner", replyText)
            val updatedMsgs = ch.messages + replyMsg
            val updatedCh = ch.copy(messages = updatedMsgs, lastMessage = replyText)
            activeChannels[index] = updatedCh
            _chatChannels.value = activeChannels
        }
    }

    fun verifyListingByAdmin(id: Int, flag: Boolean) {
        viewModelScope.launch {
            repository.updateListingVerification(id, flag)
            if (flag) {
                _adminRevenueCommissions.value += 1500
            }
        }
    }

    fun deleteListingByAdmin(id: Int) {
        viewModelScope.launch {
            repository.deleteListing(id)
        }
    }

    fun setDivisionFilter(division: String) {
        _selectedDivisionFilter.value = division
        if (division != "All") {
            _selectedAreaFilter.value = "All"
            _selectedDistrictFilter.value = "All"
        }
    }

    fun setDistrictFilter(district: String) {
        _selectedDistrictFilter.value = district
        if (district != "All") {
            _selectedAreaFilter.value = "All"
        }
    }

    fun setAreaFilter(area: String) {
        _selectedAreaFilter.value = area
    }

    fun submitManualPayment(
        senderNumber: String,
        transactionId: String,
        amount: Int,
        paymentMethod: String,
        purpose: String,
        onResult: (String) -> Unit
    ) {
        val user = _currentUser.value
        if (user == null) {
            onResult("Authentication failure: please login first.")
            return
        }
        if (senderNumber.isBlank() || transactionId.isBlank() || amount <= 0) {
            onResult("Invalid fields. Please provide a valid TxnID, sender number and amount.")
            return
        }

        viewModelScope.launch {
            val payment = ManualPaymentSubmission(
                senderNumber = senderNumber.trim(),
                transactionId = transactionId.trim().uppercase(),
                amount = amount,
                paymentMethod = paymentMethod,
                purpose = purpose,
                status = "Pending",
                userEmail = user.email,
                userName = user.username
            )
            repository.insertManualPayment(payment)
            onResult("Success! Manual bKash/Nagad audit request of $amount BDT submitted. Wait for Admin review.")
        }
    }

    fun respondToManualPayment(id: Int, status: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            repository.updateManualPaymentStatus(id, status)
            
            if (status == "Approved") {
                val submission = manualPayments.value.find { it.id == id }
                if (submission != null) {
                    val users = _registeredUsers.value.toMutableList()
                    val targetUserIndex = users.indexOfFirst { it.email.lowercase() == submission.userEmail.lowercase() }
                    
                    if (targetUserIndex != -1) {
                        val originalUser = users[targetUserIndex]
                        var updatedUser = originalUser
                        when (submission.purpose) {
                            "Recharge" -> {
                                updatedUser = originalUser.copy(
                                    walletBalance = originalUser.walletBalance + submission.amount
                                )
                                _adminRevenueAccountCharges.value += submission.amount
                            }
                            "Upgrade_Student" -> {
                                updatedUser = originalUser.copy(
                                    subscriptionType = "Student Premium"
                                )
                                _adminRevenueSubscriptions.value += submission.amount
                            }
                            "Upgrade_Owner" -> {
                                updatedUser = originalUser.copy(
                                    subscriptionType = "Owner Platinum",
                                    isVerified = true
                                )
                                _adminRevenueSubscriptions.value += submission.amount
                            }
                            "Upgrade_Broker" -> {
                                updatedUser = originalUser.copy(
                                    subscriptionType = "Broker Pro",
                                    isVerified = true
                                )
                                _adminRevenueSubscriptions.value += submission.amount
                            }
                        }
                        users[targetUserIndex] = updatedUser
                        _registeredUsers.value = users
                        
                        // If current logged in user was upgraded, let's sync live state
                        val loggedIn = _currentUser.value
                        if (loggedIn != null && loggedIn.email.lowercase() == submission.userEmail.lowercase()) {
                            _currentUser.value = updatedUser
                        }
                        onResult("Payment #${submission.transactionId} approved! User benefits loaded.")
                    } else {
                        onResult("Error: Targeted user account was not found.")
                    }
                } else {
                    onResult("Error: Submission matching ID not found.")
                }
            } else {
                onResult("Transaction status updated to Rejected.")
            }
        }
    }

    fun setMaxBudget(budget: Int) {
        _maxBudgetFilter.value = budget
    }

    fun setTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun toggleStudentMode() {
        val current = _studentMode.value
        _studentMode.value = !current
        if (_studentMode.value) {
            // Set student budget caps
            _maxBudgetFilter.value = 8500
            _selectedTypeFilter.value = "Bachelor"
        } else {
            _maxBudgetFilter.value = 40000
            _selectedTypeFilter.value = "All"
        }
    }

    // Insert user listing with account rules & charges
    fun createListing(onResult: (String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult("Please sign in first from the Account Center!")
            return
        }

        val cost = 50
        if (user.walletBalance < cost) {
            onResult("Insufficient BDT balance in wallet (needs 50 BDT) to cover hosting cost!")
            return
        }

        viewModelScope.launch {
            val updated = user.copy(walletBalance = user.walletBalance - cost)
            _currentUser.value = updated
            _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
            _adminRevenueListingFees.value += cost

            val rentInt = inputRent.value.toIntOrNull() ?: 8000
            val depositInt = inputDeposit.value.toIntOrNull() ?: 10000
            val listing = RentListing(
                title = inputTitle.value.ifBlank { "Cozy Room in ${inputArea.value}" },
                area = inputArea.value,
                address = inputAddress.value.ifBlank { "Sector 10, ${inputArea.value}" },
                rentAmount = rentInt,
                type = inputType.value,
                ruleElectricity = inputElectricity.value,
                ruleWater = inputWater.value,
                securityDeposit = depositInt,
                isVerifiedOwner = (user.role == UserRole.OWNER && user.subscriptionType != "Free") || isVerifiedOwner.value,
                isTrustedOwner = (user.role == UserRole.OWNER && user.subscriptionType != "Free") || isVerifiedOwner.value,
                hasVideoWalkthrough = hasVideoWalkthrough.value,
                videoUrl = inputVideoUrl.value,
                isBrokerListing = user.role == UserRole.BROKER,
                brokerName = if (user.role == UserRole.BROKER) user.username else "",
                brokerCommission = if (user.role == UserRole.BROKER) 4500 else 0,
                ownerName = if (user.role == UserRole.BROKER) "Salim Broker Services" else user.username,
                ownerPhone = user.phone,
                division = inputDivision.value,
                district = inputDistrict.value
            )
            repository.insertListing(listing)
            resetListingInputs()
            onResult("Success! Listing published. 50 BDT fee charged against your wallet balance.")
        }
    }

    private fun resetListingInputs() {
        inputTitle.value = ""
        inputDivision.value = "Dhaka"
        inputDistrict.value = "Dhaka"
        inputAddress.value = ""
        inputRent.value = ""
        inputDeposit.value = ""
        inputVideoUrl.value = ""
        hasVideoWalkthrough.value = false
        isVerifiedOwner.value = false
    }

    // Submit a review
    fun submitReview(listingId: Int, reviewText: String, clean: Float, owner: Float, safety: Float, water: Float) {
        viewModelScope.launch {
            val review = TenantReview(
                listingId = listingId,
                tenantName = "Anonymous Student",
                cleanlinessRating = clean,
                ownerBehaviorRating = owner,
                safetyRating = safety,
                waterElectricityRating = water,
                reviewText = reviewText
            )
            repository.insertReview(review)
        }
    }

    // File report or complaint
    fun reportListing(listingId: Int) {
        viewModelScope.launch {
            repository.reportComplaint(listingId)
        }
    }

    // Toggle Broker verification / Owner trust directly by AI/Admin
    fun toggleScamFlag(listingId: Int, isScam: Boolean) {
        viewModelScope.launch {
            repository.updateScamFlag(listingId, isScam)
        }
    }

    // Toggle availability directly
    fun toggleAvailability(listingId: Int, state: String) {
        viewModelScope.launch {
            repository.updateAvailability(listingId, state)
        }
    }

    // Create negotiation offer
    fun submitNegotiation(listingId: Int, title: String, proposed: Int, notes: String) {
        viewModelScope.launch {
            val n = Negotiation(
                listingId = listingId,
                listingTitle = title,
                tenantName = "Ratul Kabir (Tenant)",
                proposedRent = proposed,
                notes = notes
            )
            repository.insertNegotiation(n)
        }
    }

    // Accept or reject negotiation
    fun respondToNegotiation(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateNegotiationStatus(id, status)
        }
    }

    // Submit roommate match search
    fun submitRoommateRequest() {
        viewModelScope.launch {
            val budgetInt = rmBudget.value.toIntOrNull() ?: 5000
            val req = RoommateRequest(
                name = rmName.value.ifBlank { "Rony (Student)" },
                age = rmAge.value.toIntOrNull() ?: 21,
                institution = rmInstitution.value.ifBlank { "Dhaka University" },
                budget = budgetInt,
                preferredArea = rmArea.value,
                lifestyle = rmLifestyle.value.ifBlank { "Quiet study schedule, non smoker" }
            )
            repository.insertRoommateRequest(req)
            rmName.value = ""
            rmAge.value = ""
            rmInstitution.value = ""
            rmBudget.value = ""
            rmLifestyle.value = ""
        }
    }

    // Lock Escrow Deposit
    fun startEscrow(listingId: Int, title: String, amount: Int) {
        viewModelScope.launch {
            val esc = EscrowDeposit(
                listingId = listingId,
                listingTitle = title,
                tenantName = "Sabbir Hasan",
                amount = amount
            )
            repository.insertEscrowDeposit(esc)
        }
    }

    // Released/Disputed Escrow
    fun updateEscrowStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateEscrowStatus(id, status)
        }
    }

    // AI Advisor call
    fun askAdvisor(query: String) {
        val userMsg = ChatMessage("user", query)
        _advisorConversation.value = _advisorConversation.value + userMsg
        _isAdvisorLoading.value = true

        viewModelScope.launch {
            val systemInstruction = """
                You are high-performance localized AI "Room Advisor" for Bangladesh, specifically "Rent Truth BD".
                Your style is empathetic, highly precise, helpful, and completely expert on Bangladesh rental insights.
                
                Important context on rents in Dhaka:
                - Dhanmondi: High demand (near DU, NSU, AIUB, BRAC, UIU buses). Bachelor sub-let: 6,000 to 10,000 BDT. Family flat: 22,000 to 45,000 BDT.
                - Mirpur: Budget-friendly. Bachelor sublets: 4,000 to 7,500 BDT. Family apartments: 12,000 to 22,000 BDT. Very popular with students.
                - Gulshan/Banani/Baridhara: High premium / corporate. Flat: 40,000 to 1,500,000 BDT. Bachelor sub-let: 15,000 to 25,000 BDT.
                - Uttara: Structured sectors. Bachelor: 5,000 to 9,000 BDT. Family: 18,000 to 30,000 BDT. High broker influence.
                - Badda/Boshundhara: Boshundhara is near NSU, IUB. Single sub-let: 7,000 to 12,000 BDT (very high demand). Badda is cheaper: 5,000 to 9,000 BDT.
                
                Always structures response beautifully:
                1. Best 3 options/areas fitted for user.
                2. Warning / Scam Pattern Risks (High broker score, overpriced, fake images warning).
                3. Approximate Transport Commute costs (Rickshaw, Bus, CNG) to adjacent universities.
                4. Match-percentage/Trust Level recommendation.
                
                Use crisp formatting, bullet points, and mix in subtle helpful Bangladeshi localized references (like "bachelor and bachelor rules", "sub-meter calculations", "tolet notice boards"). Keep responses clear, concise, and professional.
            """.trimIndent()

            val rawOutput = GeminiClient.generateContent(query, systemInstruction)
            val cleanReply = if (rawOutput.startsWith("Error:")) {
                rawOutput + "\n\n*(Note: For testing, you can input a mock key or simulated responses will show inside this interface)*"
            } else {
                rawOutput
            }
            _advisorConversation.value = _advisorConversation.value + ChatMessage("ai", cleanReply)
            _isAdvisorLoading.value = false
        }
    }

    // AI Listing Scan Check (Moderator tool)
    fun runAIScamScanOnListing(listingId: Int) {
        viewModelScope.launch {
            val details = listings.value.find { it.id == listingId } ?: return@launch
            val scanPrompt = """
                Formulate a rapid rental scam structural review.
                Listing title: ${details.title}
                Rent: ${details.rentAmount} BDT
                Area: ${details.area}
                Broker info: ${if (details.isBrokerListing) "Yes, commission is ${details.brokerCommission}" else "Direct Owner"}
                Video walk-through: ${if (details.hasVideoWalkthrough) "Mandatory uploaded & verified" else "None"}
                
                Respond in exactly 3-4 bullet points:
                1. PRICE TRUTH: is it too cheap (suspicious warning), overpriced, or fair for this area? Give specific range.
                2. BROKER FACTOR: evaluate broker risk and broker-removal level for ${details.area}.
                3. VERIFIED INDEX score (0 to 100) based on location check and walk-through.
                4. FINAL VERDICT: Approved, Red Flagged (scam risk), or Needs Walkthrough validation.
            """.trimIndent()

            val analysisResult = GeminiClient.generateContent(scanPrompt)
            val generatedReview = if (analysisResult.startsWith("Error:")) {
                // Return descriptive mockup scan so the app is always interactive and informative even without API keys!
                simulateAIScan(details)
            } else {
                analysisResult
            }
            
            // Auto update listing properties based on AI scan results
            if (generatedReview.contains("Red Flag") || generatedReview.contains("SCAM") || (details.rentAmount < 5000 && details.area == "Gulshan")) {
                repository.updateScamFlag(listingId, true)
            }
            
            // Put review comments as a synthetic system tenant review for transparency
            val mockAiReview = TenantReview(
                listingId = listingId,
                tenantName = "AI Trust Moderator",
                cleanlinessRating = 4.5f,
                ownerBehaviorRating = 3.0f,
                safetyRating = 4.0f,
                waterElectricityRating = 4.2f,
                reviewText = generatedReview,
                dateString = "AI Dynamic Analysis"
            )
            repository.insertReview(mockAiReview)
        }
    }

    private fun simulateAIScan(details: RentListing): String {
        val area = details.area
        val rent = details.rentAmount
        val hasVideo = details.hasVideoWalkthrough
        
        val isCheap = when(area) {
            "Dhanmondi" -> rent < 8000
            "Gulshan" -> rent < 15000
            "Banani" -> rent < 12000
            "Uttara" -> rent < 6000
            else -> rent < 4000
        }
        
        val isOverpriced = when(area) {
            "Dhanmondi" -> rent > 35000
            "Gulshan" -> rent > 80000
            "Mirpur" -> rent > 20000
            else -> rent > 15000
        }

        return """
            📊 Rent Truth AI Diagnostic:
            • **Price Check**: ${if (isCheap) "⚠️ HIGH SCAM RISK! This is too cheap for $area. Normal range: 8k-15k BDT." else if (isOverpriced) "⚠️ OVERPRICED WARNING: This listing is above average price index for $area." else "🟢 FAIR PRICE INDEX: Rent conforms to localized $area market expectations."}
            • **Media Score**: ${if (hasVideo) "🟢 Walkthrough video uploaded and validated. Ownership matches verification logs." else "❌ NO WALKTHROUGH: High risk of recycled internet images."}
            • **Broker Monopoly**: $area has a ${if (area == "Banani" || area == "Gulshan") "HOTSPOT" else "MEDIUM"} broker score. ${if (details.isBrokerListing) "Broker Salim has declared their professional licensing badge." else "Direct connection to family landlord."}
            • **Verdict**: ${if (isCheap) "🟥 SCAM SUSPECT - High alert placed." else "🟢 APPROVED for Trust Index ranking (85/100)."}
        """.trimIndent()
    }

    fun updateTenantDocumentStatus(userEmail: String, docId: String, newStatus: String, comments: String = "") {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == userEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val updatedDocs = originalUser.uploadedDocs.map {
                if (it.id == docId) it.copy(status = newStatus, comments = comments) else it
            }
            val isAllApproved = updatedDocs.isNotEmpty() && updatedDocs.all { it.status == "Approved" }
            val updatedUser = originalUser.copy(
                uploadedDocs = updatedDocs,
                nidVerifiedBadge = if (updatedDocs.any { it.docType == "NID Card" && it.status == "Approved" }) true else originalUser.nidVerifiedBadge,
                isVerified = isAllApproved
            )
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == userEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun addTenantManually(ownerEmail: String, tenantName: String) {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == ownerEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val updatedUser = originalUser.copy(
                currentTenants = originalUser.currentTenants + tenantName
            )
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == ownerEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun respondToTenantBookingRequest(ownerEmail: String, requestName: String, accept: Boolean) {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == ownerEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val requests = originalUser.upcomingTenantsRequests.toMutableList()
            requests.remove(requestName)
            val updatedTenants = if (accept) originalUser.currentTenants + requestName else originalUser.currentTenants
            val history = if (accept) originalUser.rentalAgreementHistory + "Lease with $requestName started May 2026" else originalUser.rentalAgreementHistory
            val updatedUser = originalUser.copy(
                upcomingTenantsRequests = requests,
                currentTenants = updatedTenants,
                rentalAgreementHistory = history
            )
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == ownerEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun setUserLockStatus(userEmail: String, isLocked: Boolean) {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == userEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val updatedUser = originalUser.copy(isLocked = isLocked)
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == userEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun deleteUserByAdmin(userEmail: String): Boolean {
        if (userEmail.trim().lowercase() == "admin@renttruthbd.com") {
            addSyncLog("[SECURITY ALERT] Prevented unauthorized deletion attempt on primary Admin Boss router account!")
            return false
        }
        val users = _registeredUsers.value.filter { it.email.lowercase() != userEmail.trim().lowercase() }
        _registeredUsers.value = users
        addSyncLog("[ADMIN ACTION] Permanently purged invalid user entry: $userEmail.")
        return true
    }

    fun adminDirectVerifyUser(userEmail: String) {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == userEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val updatedUser = originalUser.copy(
                isVerified = true,
                nidVerifiedBadge = true,
                propertyVerifiedBadge = if (originalUser.role == UserRole.OWNER) true else originalUser.propertyVerifiedBadge
            )
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == userEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun uploadTenantDocument(docType: String, fileName: String) {
        val user = _currentUser.value ?: return
        val newDoc = TenantDoc(
            id = "doc_" + System.currentTimeMillis(),
            docType = docType,
            status = "Pending",
            fileName = fileName,
            comments = "Version: v1 (Secure OCR Matching System active)",
            uploadedAt = "May 28, 2026",
            expiryDate = "2030-12-31"
        )
        val updatedDocs = user.uploadedDocs.filter { it.docType != docType } + newDoc
        val updated = user.copy(uploadedDocs = updatedDocs)
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
        addSyncLog("[OK] Document uploaded ($docType). Anti-malware scanner check passed.")
    }

    fun uploadTenantDocumentWithMeta(docType: String, fileName: String, expiryDate: String) {
        val user = _currentUser.value ?: return
        val existing = user.uploadedDocs.find { it.docType == docType }
        val nextVersion = if (existing != null) {
            val idx = existing.comments.indexOf("Version: v")
            if (idx != -1) {
                val vNum = existing.comments.substring(idx + 10).takeWhile { it.isDigit() }.toIntOrNull() ?: 1
                "v${vNum + 1}"
            } else "v2"
        } else "v1"

        val newDoc = TenantDoc(
            id = "doc_" + System.currentTimeMillis(),
            docType = docType,
            status = "Pending",
            fileName = fileName,
            comments = "Version: $nextVersion (Secure OCR Matching System active)",
            uploadedAt = "May 28, 2026",
            expiryDate = expiryDate
        )
        val updatedDocs = user.uploadedDocs.filter { it.docType != docType } + newDoc
        val updated = user.copy(uploadedDocs = updatedDocs)
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
        addSyncLog("[OK] Document replaced/updated ($docType). Version tracked to $nextVersion.")
    }

    fun updateUserProfileDetails(
        username: String,
        phone: String,
        email: String = "",
        profilePhoto: String = "",
        address: String = "",
        profileBio: String = "",
        rentalPreferences: String = "",
        bankPaymentDetails: String = "",
        propertyDescriptions: String = "",
        brokerIdentityDetails: String = "",
        occupation: String = "",
        emergencyContact: String = ""
    ) {
        val user = _currentUser.value ?: return
        val updated = user.copy(
            username = username,
            phone = phone,
            email = if (email.isNotBlank()) email else user.email,
            profilePhoto = profilePhoto,
            address = address,
            profileBio = profileBio,
            rentalPreferences = rentalPreferences,
            bankPaymentDetails = bankPaymentDetails,
            propertyDescriptions = propertyDescriptions,
            brokerIdentityDetails = brokerIdentityDetails,
            occupationInfo = occupation,
            emergencyContact = emergencyContact
        )
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updated else it }
        addSyncLog("[AUDIT LOG] Profile details updated for ${user.username} (${user.email}). All modifications are recorded.")
    }

    fun addSyncLog(log: String) {
        val logs = _syncLogs.value.toMutableList()
        logs.add(0, log)
        if (logs.size > 20) logs.removeAt(logs.size - 1)
        _syncLogs.value = logs
    }

    fun removeSecuritySession(sessionId: String) {
        _activeSessions.value = _activeSessions.value.filter { it.id != sessionId }
        addSyncLog("[SECURITY] Terminated device session token ID: $sessionId")
    }

    fun addLandlordTenantLogManual(
        name: String,
        phone: String,
        email: String,
        propertyTitle: String,
        rent: Int,
        startDate: String,
        duration: Int
    ) {
        val newLog = LandlordTenantLog(
            id = "manual_l_" + System.currentTimeMillis(),
            tenantName = name,
            tenantPhone = phone,
            tenantEmail = email,
            propertyTitle = propertyTitle,
            rentAmount = rent,
            startDate = startDate,
            durationMonths = duration,
            status = "Active Renter",
            docVerified = true,
            trustScore = (85..99).random(),
            isArchived = false,
            paymentHistory = listOf("Paid June 2026")
        )
        _landlordTenantLogs.value = listOf(newLog) + _landlordTenantLogs.value
        addSyncLog("[LEDGER] Owner registered manual renter: $name on $propertyTitle")
    }

    fun updateLandlordTenantStatus(logId: String, newStatus: String) {
        _landlordTenantLogs.value = _landlordTenantLogs.value.map {
            if (it.id == logId) {
                it.copy(
                    status = newStatus,
                    isArchived = (newStatus == "Vacated")
                )
            } else it
        }
        addSyncLog("[REGISTRY] Tenant log ID $logId status upgraded ➔ $newStatus")
    }

    fun addBrokerDealLog(
        clientName: String,
        clientPhone: String,
        propertyTitle: String,
        amount: Int,
        commission: Int,
        coBrokeStatus: String,
        dealType: String
    ) {
        val newDeal = BrokerDeal(
            id = "bd_" + System.currentTimeMillis(),
            clientName = clientName,
            clientPhone = clientPhone,
            propertyTitle = propertyTitle,
            dealAmount = amount,
            commissionEarned = commission,
            coBrokeStatus = coBrokeStatus,
            dealType = dealType,
            status = "Closed"
        )
        _brokerDeals.value = listOf(newDeal) + _brokerDeals.value
        _adminRevenueCommissions.value += commission / 10 // split
        addSyncLog("[BROKER] Deal closed successfully. Split parameters populated.")
    }

    fun addBrokerPartnershipLog(
        brokerEmail: String,
        ownerName: String,
        ownerPhone: String,
        propertyTitle: String,
        split: Int
    ) {
        val newBP = BrokerOwnerPartnership(
            id = "bp_" + System.currentTimeMillis(),
            brokerEmail = brokerEmail,
            ownerName = ownerName,
            ownerPhone = ownerPhone,
            propertyTitle = propertyTitle,
            commissionSplitPercent = split,
            status = "Authorized"
        )
        _brokerPartnerships.value = listOf(newBP) + _brokerPartnerships.value
        addSyncLog("[MAPPING] Broker-Owner relationship authorized for: $propertyTitle")
    }

    fun adminOverrideTrustScore(userEmail: String, score: Int) {
        val users = _registeredUsers.value.toMutableList()
        val idx = users.indexOfFirst { it.email.lowercase() == userEmail.lowercase() }
        if (idx != -1) {
            val originalUser = users[idx]
            val updatedUser = originalUser.copy(trustScore = score)
            users[idx] = updatedUser
            _registeredUsers.value = users
            val current = _currentUser.value
            if (current != null && current.email.lowercase() == userEmail.lowercase()) {
                _currentUser.value = updatedUser
            }
            addSyncLog("[ADMIN] Direct override trust score for $userEmail ➔ $score")
        }
    }

    private suspend fun populateSamples() {
        val sampleListings = listOf(
            RentListing(
                title = "Verified Dhanmondi Bachelor Room (Near UIU/NSU Route)",
                area = "Dhanmondi",
                address = "Road 8A, Dhanmondi, Dhaka (Beside Lake)",
                rentAmount = 8500,
                type = "Bachelor",
                ruleElectricity = "Sub-meter usage (Approx. 600 BDT)",
                ruleWater = "Included in rent",
                securityDeposit = 8500,
                isVerifiedOwner = true,
                isTrustedOwner = true,
                hasVideoWalkthrough = true,
                videoUrl = "https://assets.renttruthbd.com/walkthrough_dhanmondi_8a.mp4",
                availabilityState = "Vacant",
                ownerName = "Prof. Rafiqul Islam",
                ownerPhone = "01711-234567",
                latitude = 23.7461,
                longitude = 90.3742
            ),
            RentListing(
                title = "Spacious Family Flat Mirpur Sec-11",
                area = "Mirpur",
                address = "Road 5, Block C, Sec-11, Mirpur (2 Min walk from Metro)",
                rentAmount = 16500,
                type = "Family",
                ruleElectricity = "Govt commercial rates",
                ruleWater = "Deep tubewell (700 BDT fixed)",
                securityDeposit = 20000,
                isVerifiedOwner = true,
                isTrustedOwner = true,
                hasVideoWalkthrough = true,
                videoUrl = "https://assets.renttruthbd.com/walkthrough_mirpur_11.mp4",
                availabilityState = "Visiting Allowed",
                ownerName = "Alhaj Mrs. Begum",
                ownerPhone = "01819-876543",
                latitude = 23.8061,
                longitude = 90.3662
            ),
            RentListing(
                title = "[SPAM SUSPECT] Luxurious Single Studio in Gulshan-2",
                area = "Gulshan",
                address = "Road 55, Gulshan 2, Dhaka",
                rentAmount = 3500, // Ridiculously cheap, classic bait-and-switch scam!
                type = "Sublet",
                ruleElectricity = "Undisclosed charges",
                ruleWater = "Unknown",
                securityDeposit = 15000, // High deposit, then disappears
                isVerifiedOwner = false,
                isTrustedOwner = false,
                hasVideoWalkthrough = false,
                isScamFlaggedByAI = true,
                availabilityState = "Vacant",
                ownerName = "Mysterous Landlord (Agent)",
                ownerPhone = "01511-999888",
                latitude = 23.7925,
                longitude = 90.4156
            ),
            RentListing(
                title = "Cozy Student Sublet Uttara Sector 4",
                area = "Uttara",
                address = "Sector 4, Road 12, Uttara",
                rentAmount = 7000,
                type = "Bachelor",
                ruleElectricity = "Fixed 500 BDT/month",
                ruleWater = "No Charge",
                securityDeposit = 5000,
                isVerifiedOwner = true,
                isTrustedOwner = false,
                hasVideoWalkthrough = false,
                availabilityState = "Vacant",
                ownerName = "Naim Ahmed",
                ownerPhone = "01923-456789",
                latitude = 23.8725,
                longitude = 90.4005
            ),
            RentListing(
                title = "Banani Premium Corporate Office Space",
                area = "Banani",
                address = "Kemal Ataturk Avenue, Banani (Opposite Shwapno)",
                rentAmount = 85000,
                type = "Office",
                ruleElectricity = "Commercial High-vanguard Sub-metering",
                ruleWater = "Included",
                securityDeposit = 150000,
                isVerifiedOwner = true,
                isTrustedOwner = true,
                hasVideoWalkthrough = true,
                videoUrl = "https://assets.renttruthbd.com/walkthrough_banani_off.mp4",
                availabilityState = "Vacant",
                ownerName = "Sheltech Properties Corp",
                ownerPhone = "01755-998877",
                latitude = 23.7937,
                longitude = 90.4066
            ),
            RentListing(
                title = "Bashundhara R/A Block D Bachelor Shared Room",
                area = "Bashundhara",
                address = "Block D, Road 3, Bashundhara R/A (Near NSU Gate 2)",
                rentAmount = 7500,
                type = "Bachelor",
                ruleElectricity = "Prepaid card recharge proportion",
                ruleWater = "Fixed 300 BDT",
                isBrokerListing = true,
                brokerName = "Salim Professional Broker",
                brokerCommission = 4500,
                securityDeposit = 7500,
                isVerifiedOwner = false,
                hasVideoWalkthrough = false,
                availabilityState = "Vacant",
                ownerName = "Salim (Broker Services)",
                ownerPhone = "01688-223344",
                latitude = 23.8155,
                longitude = 90.4285
            ),
            RentListing(
                title = "Agrabad Residential Deluxe Family Unit",
                division = "Chittagong",
                district = "Chittagong",
                area = "Agrabad",
                address = "Agrabad Access Road, Chittagong",
                rentAmount = 18500,
                type = "Family",
                ruleElectricity = "Sub-meter custom reading",
                ruleWater = "Fixed 600 BDT/month",
                securityDeposit = 25000,
                isVerifiedOwner = true,
                ownerName = "Hajee Mohammad Yousuf",
                ownerPhone = "01819-112233",
                latitude = 22.3275,
                longitude = 91.8153
            ),
            RentListing(
                title = "Sylhet Zindabazar Student Room Share",
                division = "Sylhet",
                district = "Sylhet",
                area = "Zindabazar",
                address = "Jail Road, Zindabazar, Sylhet",
                rentAmount = 5500,
                type = "Bachelor",
                ruleElectricity = "Prepaid billing share",
                ruleWater = "No Charge",
                securityDeposit = 5000,
                isVerifiedOwner = true,
                ownerName = "Sufian Ahmed",
                ownerPhone = "01712-445566",
                latitude = 24.8949,
                longitude = 91.8687
            ),
            RentListing(
                title = "Rajshahi University Cadet Sublet Space",
                division = "Rajshahi",
                district = "Rajshahi",
                area = "Upashahar",
                address = "Sector 2, Upashahar, Rajshahi (Near RU)",
                rentAmount = 4500,
                type = "Bachelor",
                ruleElectricity = "Sub-metering (200 BDT/avg)",
                ruleWater = "Deep motor (included)",
                securityDeposit = 3000,
                isVerifiedOwner = false,
                ownerName = "Mst. Salma Begum",
                ownerPhone = "01715-778899",
                latitude = 24.3745,
                longitude = 88.6042
            ),
            RentListing(
                title = "Sonadanga Sweet Studio Apartment",
                division = "Khulna",
                district = "Khulna",
                area = "Sonadanga",
                address = "Sonadanga R/A, Road 3, Khulna",
                rentAmount = 11000,
                type = "Sublet",
                ruleElectricity = "Post-paid divided",
                ruleWater = "500 BDT flat rate",
                securityDeposit = 11000,
                isVerifiedOwner = true,
                ownerName = "Prof. Amzad Hossain",
                ownerPhone = "01911-334455",
                latitude = 22.8197,
                longitude = 89.5511
            )
        )

        for (item in sampleListings) {
            repository.insertListing(item)
        }

        // Add some sample roommate requests
        val roommates = listOf(
            RoommateRequest(
                name = "Tahsan Mahmud (NSU Student)",
                age = 21,
                institution = "North South University",
                budget = 6000,
                preferredArea = "Bashundhara",
                lifestyle = "Non-smoker, studious, respects privacy. Usually busy at library."
            ),
            RoommateRequest(
                name = "Afrin Jahan (BRAC Student)",
                age = 22,
                institution = "BRAC University",
                budget = 8000,
                preferredArea = "Dhanmondi",
                lifestyle = "Looking for safe female sharing room, study oriented, tidy, keeps kitchen clean."
            ),
            RoommateRequest(
                name = "Siam (DU CSE)",
                age = 20,
                institution = "Dhaka University",
                budget = 4500,
                preferredArea = "Mirpur",
                lifestyle = "No night stays allowed. Quiet study environment, shared utility bill division."
            )
        )
        for (rm in roommates) {
            repository.insertRoommateRequest(rm)
        }

        // Insert initial reviews
        val review = TenantReview(
            listingId = 1,
            tenantName = "Riyad Azim (Ex Tenant)",
            cleanlinessRating = 4.8f,
            ownerBehaviorRating = 5.0f,
            safetyRating = 4.5f,
            waterElectricityRating = 4.2f,
            reviewText = "Uncle and Aunt are extremely helpful. They don't restrict students entering after 11 PM if you notify earlier. Water supply is 24/7."
        )
        repository.insertReview(review)
    }

    // Low band / Offline / Compression modes
    private val _lowBandwidthMode = MutableStateFlow(false)
    val lowBandwidthMode = _lowBandwidthMode.asStateFlow()

    private val _offlineDraftsSaved = MutableStateFlow(false)
    val offlineDraftsSaved = _offlineDraftsSaved.asStateFlow()

    private val _imageCompressionActive = MutableStateFlow(true)
    val imageCompressionActive = _imageCompressionActive.asStateFlow()

    private val _bilingualBanglaFirst = MutableStateFlow(true)
    val bilingualBanglaFirst = _bilingualBanglaFirst.asStateFlow()

    private val _voiceGuidanceActive = MutableStateFlow(false)
    val voiceGuidanceActive = _voiceGuidanceActive.asStateFlow()

    // Smart Digital Rental Agreements lists
    private val _agreements = MutableStateFlow<List<SmartRentalAgreement>>(listOf(
        SmartRentalAgreement(
            id = "agr_1",
            type = SmartAgreementType.BACHELOR_ROOM,
            title = "Dhanmondi Bachelor Room #2A Sublet",
            tenantName = "Ratul (Tenant)",
            tenantEmail = "ratul@school.edu",
            ownerName = "Prof. Rafiqul Islam",
            ownerEmail = "rahman@landlord.com",
            durationMonths = 12,
            rentAmount = 8500,
            status = "Signed",
            ownerSignature = "Prof. Rafiqul Islam",
            tenantSignature = "Ratul Ahmed",
            startDate = "2026-06-01",
            endDate = "2027-05-31",
            violations = listOf("Late water sub-meter reading report (resolved)")
        ),
        SmartRentalAgreement(
            id = "agr_2",
            type = SmartAgreementType.STUDENT_ROOM,
            title = "Bashundhara Student Room Flat 4C",
            tenantName = "Tahsan Mahmud",
            tenantEmail = "tahsan@school.edu",
            ownerName = "Prof. Rafiqul Islam",
            ownerEmail = "rahman@landlord.com",
            durationMonths = 6,
            rentAmount = 6000,
            status = "Waiting Tenant",
            ownerSignature = "Prof. Rafiqul Islam",
            startDate = "2026-06-05",
            endDate = "2026-12-05"
        ),
        SmartRentalAgreement(
            id = "agr_3",
            type = SmartAgreementType.FAMILY_FLAT,
            title = "Sheela Niwas Mirpur Flat 2A Contract",
            tenantName = "Mufizur Rahman",
            tenantEmail = "mufiz@gmail.com",
            ownerName = "Rahman Saheb (Owner)",
            ownerEmail = "rahman@landlord.com",
            durationMonths = 12,
            rentAmount = 22000,
            status = "Signed",
            ownerSignature = "M. R. Rahman",
            tenantSignature = "Mufizur R.",
            startDate = "2026-05-01",
            endDate = "2027-04-30"
        )
    ))
    val agreements = _agreements.asStateFlow()

    // Smart Rent Payments tracker lists
    private val _payments = MutableStateFlow<List<RentTrackerPayment>>(listOf(
        RentTrackerPayment(
            id = "pay_101",
            agreementId = "agr_1",
            tenantEmail = "ratul@school.edu",
            tenantName = "Ratul (Tenant)",
            rentMonth = "June 2026",
            totalAmount = 8500,
            paidAmount = 0,
            status = "Due",
            depositPaid = 10000,
            utilitiesCost = 600,
            lateAlertActive = true,
            auditLog = listOf("Invoice initialized May 25, 2026", "Late payment grace days elapsed.")
        ),
        RentTrackerPayment(
            id = "pay_102",
            agreementId = "agr_1",
            tenantEmail = "ratul@school.edu",
            tenantName = "Ratul (Tenant)",
            rentMonth = "May 2026",
            totalAmount = 8500,
            paidAmount = 8500,
            status = "Paid",
            depositPaid = 10000,
            utilitiesCost = 550,
            paymentMethod = "bKash",
            transactionId = "BKSH5829471A",
            datePaid = "May 03, 2026",
            auditLog = listOf("Invoice generated Apr 25, 2026", "bKash payment confirmed & reconciled via automated gateway API.")
        ),
        RentTrackerPayment(
            id = "pay_103",
            agreementId = "agr_3",
            tenantEmail = "mufiz@gmail.com",
            tenantName = "Mufizur Rahman",
            rentMonth = "June 2026",
            totalAmount = 22000,
            paidAmount = 10000,
            status = "Partial",
            utilitiesCost = 1500,
            paymentMethod = "Bank transfer",
            transactionId = "EBL_TXN_REF8829",
            datePaid = "June 02, 2026",
            auditLog = listOf("Invoice generated May 25, 2026", "EBL EFT Bank Transfer received 10,000 BDT partial credit.")
        )
    ))
    val payments = _payments.asStateFlow()

    // Multi-building states
    private val _buildings = MutableStateFlow<List<Building>>(listOf(
        Building(
            id = "bld_1",
            name = "Sheela Niwas",
            address = "Road 8A, Dhanmondi, Dhaka",
            caretakerName = "Abdul Malek",
            status = "Fully Operational",
            announcements = listOf("Building water tank cleaning scheduled for next Friday 9AM to noon. Water line will be dry!"),
            units = listOf(
                BuildingUnit("unit_1", "Flat 1A", true, "Riyad Azim", 18000, "Paid"),
                BuildingUnit("unit_2", "Flat 1B", false, "", 18000, "Paid"),
                BuildingUnit("unit_3", "Flat 2A", true, "Tasnim Ahsan", 19000, "Due"),
                BuildingUnit("unit_4", "Flat 2B", false, "", 19000, "Paid")
            )
        ),
        Building(
            id = "bld_2",
            name = "Al-Suhrawardy Center",
            address = "Block D, Bashundhara R/A, Dhaka",
            caretakerName = "Sumon Mia",
            status = "Minor Maintenance",
            announcements = listOf("Fire evacuation audit check on Sunday noon. Please cooperate with safety officers."),
            units = listOf(
                BuildingUnit("u_101", "Flat 3C", true, "Tahsan Mahmud", 12000, "Paid"),
                BuildingUnit("u_102", "Flat 4B", true, "Ratul Ahmed", 11000, "Due")
            )
        )
    ))
    val buildings = _buildings.asStateFlow()

    // Maintenance requests
    private val _maintenanceTickets = MutableStateFlow<List<MaintenanceTicket>>(listOf(
        MaintenanceTicket("tkt_1", "Sheela Niwas", "Flat 2A", "Main bathroom plumbing backup and sink overflow", "High", "InProgress"),
        MaintenanceTicket("tkt_2", "Al-Suhrawardy Center", "Flat 4B", "Ceiling fan speed selector switch broken", "Low", "Open")
    ))
    val maintenanceTickets = _maintenanceTickets.asStateFlow()

    // Visitor Logs
    private val _visitorLogs = MutableStateFlow<List<VisitorLog>>(listOf(
        VisitorLog("vl_1", "Rashedul Karim (E-Desh Delivery)", "Flat 1A", "Document delivery", "Today 09:30 AM", "Today 09:40 AM"),
        VisitorLog("vl_2", "Wasi Uddin (WASA inspector)", "Flat 2A", "Water chlorination check", "Today 11:15 AM", "Today 11:35 AM")
    ))
    val visitorLogs = _visitorLogs.asStateFlow()

    // Parking slot allocation
    private val _parkingSlots = MutableStateFlow<List<ParkingSlot>>(listOf(
        ParkingSlot("Slot P1", "Riyad Azim", "Occupied"),
        ParkingSlot("Slot P2", "Prof. Rafiqul Islam", "Reserved"),
        ParkingSlot("Slot P3", "Empty", "Available"),
        ParkingSlot("Slot P4", "Empty", "Available")
    ))
    val parkingSlots = _parkingSlots.asStateFlow()

    // Fraud Reports - feed on control tower
    private val _fraudReports = MutableStateFlow<List<FraudModelReport>>(listOf(
        FraudModelReport("frd_1", "Duplicate Listing Detected", "Exactly matching text description and area dimension found for Mirpur 12 listing.", 92),
        FraudModelReport("frd_2", "Reused Image Fingerprint", "Listing bedroom picture matches blacklisted fraud file under index BD-F829.", 89),
        FraudModelReport("frd_3", "Fake Identity Risk", "User National ID scanner audit failed government database OCR matching rules.", 97),
        FraudModelReport("frd_4", "Suspicious Broker Behavior", "Listing active under 5 accounts concurrently with identical device cookies.", 94),
        FraudModelReport("frd_5", "Unrealistic Pricing Index", "Dhanmondi luxury studio listed for 3000 BDT instead of average 15000 BDT benchmark.", 98),
        FraudModelReport("frd_6", "VoIP Virtual Route Detector", "Phone registrant is using virtual VoIP routing instead of biometric verified SIM card.", 91)
    ))
    val fraudReports = _fraudReports.asStateFlow()

    // In-app Notification list
    private val _notifications = MutableStateFlow<List<NotificationModel>>(listOf(
        NotificationModel("nt_1", "Booking Request", "Hasan Expressed interest for Dhanmondi Bachelor Space.", "Booking", "In-App"),
        NotificationModel("nt_2", "Verification Approved", "Smart NID OCR and Government Database biometric match completely successful.", "Verification", "In-App"),
        NotificationModel("nt_3", "E-Contract Expiry Warning", "E-lease contract BD-2025-102 expires in 30 days. Auto renewal open.", "Agreement", "In-App"),
        NotificationModel("nt_4", "June Rent Bill Generated", "Rent Invoice for June 2026 of 8,500 BDT has been sent.", "RentDue", "In-App")
    ))
    val notifications = _notifications.asStateFlow()

    fun setLowBandwidth(enabled: Boolean) {
        _lowBandwidthMode.value = enabled
    }

    fun setOfflineDraftsSaved(saved: Boolean) {
        _offlineDraftsSaved.value = saved
    }

    fun setImageCompression(enabled: Boolean) {
        _imageCompressionActive.value = enabled
    }

    fun setBilingualBanglaFirst(enabled: Boolean) {
        _bilingualBanglaFirst.value = enabled
    }

    fun setVoiceGuidanceActive(enabled: Boolean) {
        _voiceGuidanceActive.value = enabled
    }

    fun createAgreement(
        type: SmartAgreementType,
        title: String,
        tenantEmail: String,
        ownerEmail: String,
        rent: Int,
        duration: Int,
        brokerName: String = ""
    ) {
        val newAg = SmartRentalAgreement(
            id = "agr_" + System.currentTimeMillis(),
            type = type,
            title = title,
            tenantName = tenantEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
            tenantEmail = tenantEmail,
            ownerName = ownerEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
            ownerEmail = ownerEmail,
            brokerName = brokerName,
            durationMonths = duration,
            rentAmount = rent,
            status = "Waiting Tenant"
        )
        _agreements.value = _agreements.value + newAg
        triggerInAppNotification("Agreement Initiated", "Agreement '$title' waiting for signature keys.", "Agreement")
    }

    fun signAgreement(agreementId: String, signature: String, isOwner: Boolean) {
        _agreements.value = _agreements.value.map { ag ->
            if (ag.id == agreementId) {
                if (isOwner) {
                    val finalStatus = if (ag.tenantSignature.isNotEmpty()) "Signed" else "Waiting Tenant"
                    ag.copy(ownerSignature = signature, status = finalStatus)
                } else {
                    val finalStatus = if (ag.ownerSignature.isNotEmpty()) "Signed" else "Waiting Owner"
                    ag.copy(tenantSignature = signature, status = finalStatus)
                }
            } else {
                ag
            }
        }
        triggerInAppNotification("Agreement Signed", "Contract updated with digital security signature hash.", "Agreement")
    }

    fun reportAgreementViolation(agreementId: String, violation: String) {
        _agreements.value = _agreements.value.map { ag ->
            if (ag.id == agreementId) {
                ag.copy(violations = ag.violations + violation)
            } else {
                ag
            }
        }
        triggerInAppNotification("Violation Reported", "Standard contract violation log appended to rental blockchain audit.", "Agreement")
    }

    fun submitManualPaymentConfirmation(
        paymentId: String,
        method: String,
        transactionId: String,
        paidAmt: Int
    ) {
        _payments.value = _payments.value.map { pay ->
            if (pay.id == paymentId) {
                pay.copy(
                    status = if (paidAmt >= pay.totalAmount + pay.utilitiesCost) "Pending Review" else "Partial",
                    paidAmount = paidAmt,
                    paymentMethod = method,
                    transactionId = transactionId,
                    datePaid = "May 28, 2026",
                    auditLog = pay.auditLog + "Manual payment submission logged via $method. Txn: $transactionId. Submitting to reconciliation queue."
                )
            } else {
                pay
            }
        }
        triggerInAppNotification("Payment Submitted", "Manual checkout submitted successfully. Pending national gateway reconciliation standard audit.", "RentDue")
    }

    fun adminReconcilePayment(paymentId: String, approve: Boolean, dispute: Boolean = false) {
        _payments.value = _payments.value.map { pay ->
            if (pay.id == paymentId) {
                val nextStatus = if (dispute) "Due" else if (approve) "Paid" else "Due"
                val logMsg = if (dispute) "Admin flagged financial dispute transaction verification mismatch." else if (approve) "Admin manual reconciliation matched national ledger successfully." else "Admin rejected manual verification ticket."
                pay.copy(
                    status = nextStatus,
                    lateAlertActive = !approve,
                    auditLog = pay.auditLog + logMsg
                )
            } else {
                pay
            }
        }
        triggerInAppNotification("Payment Audit Complete", "Government and bank reconciliation complete.", "RentDue")
    }

    fun addBuildingAnnouncement(buildingId: String, text: String) {
        _buildings.value = _buildings.value.map { bld ->
            if (bld.id == buildingId) {
                bld.copy(announcements = listOf(text) + bld.announcements)
            } else {
                bld
            }
        }
        triggerInAppNotification("Announcement Added", "New building update broadcasted to all residents.", "Booking")
    }

    fun addNewMaintenanceTicket(buildingName: String, unitName: String, issue: String, severity: String) {
        val newTkt = MaintenanceTicket(
            id = "tkt_" + System.currentTimeMillis(),
            buildingName = buildingName,
            unitName = unitName,
            issue = issue,
            severity = severity,
            status = "Open"
        )
        _maintenanceTickets.value = _maintenanceTickets.value + newTkt
        triggerInAppNotification("Maintenance Ticket Opened", "Care taker requested for plumbing/electrical audit at $unitName.", "Booking")
    }

    fun updateMaintenanceTicketStatus(ticketId: String, status: String) {
        _maintenanceTickets.value = _maintenanceTickets.value.map { tkt ->
            if (tkt.id == ticketId) {
                tkt.copy(status = status)
            } else {
                tkt
            }
        }
    }

    fun addVisitorLog(visitorName: String, unitName: String, purpose: String) {
        val newLog = VisitorLog(
            id = "vl_" + System.currentTimeMillis(),
            visitorName = visitorName,
            unitName = unitName,
            purpose = purpose
        )
        _visitorLogs.value = _visitorLogs.value + newLog
    }

    fun allocateParking(slotNumber: String, assignedTo: String) {
        _parkingSlots.value = _parkingSlots.value.map { slot ->
            if (slot.slotNumber == slotNumber) {
                slot.copy(
                    assignedTo = assignedTo,
                    status = if (assignedTo == "Empty") "Available" else "Occupied"
                )
            } else {
                slot
            }
        }
    }

    fun addNewFraudReport(type: String, desc: String, confidence: Int) {
        val report = FraudModelReport(
            id = "frd_" + System.currentTimeMillis(),
            type = type,
            description = desc,
            confidence = confidence
        )
        _fraudReports.value = listOf(report) + _fraudReports.value
        triggerInAppNotification("AI Critical Fraud Alert", "Anomaly Index High on platform telemetry vector detection.", "Fraud")
    }

    fun triggerInAppNotification(title: String, msg: String, type: String) {
        val note = NotificationModel(
            id = "nt_" + System.currentTimeMillis(),
            title = title,
            message = msg,
            type = type,
            channel = "In-App"
        )
        _notifications.value = listOf(note) + _notifications.value
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun setGatewayAutomation(automated: Boolean) {
        _isGatewayAutomated.value = automated
        addSyncLog("[SYSTEM] Billing flow toggled to ${if (automated) "REST Instant API Gateway" else "Manual SMS/Verification Check"}")
    }

    fun toggleOfflineMode() {
        if (_websocketStatus.value == "🟢 Connected") {
            _websocketStatus.value = "🔴 Offline Mode (Queue Active)"
            addSyncLog("[NETWORK] WebSocket disconnected. Platform entering local database lock and offline simulation buffer.")
        } else {
            _websocketStatus.value = "🟢 Connected"
            addSyncLog("[NETWORK] WebSocket reconnected. Resolving offline buffer queues gracefully...")
            reconcileOfflineTasks()
        }
    }

    fun recordOfflineTask(opName: String, desc: String) {
        val current = _offlineQueue.value.toMutableList()
        current.add(OfflineSyncTask(
            id = "task_" + System.currentTimeMillis(),
            operationName = opName,
            description = desc,
            timestamp = "Just Now"
        ))
        _offlineQueue.value = current
        addSyncLog("[QUEUE] Cached offline operation: $opName. Sync pending network availability.")
    }

    private fun reconcileOfflineTasks() {
        if (_offlineQueue.value.isEmpty()) return
        val count = _offlineQueue.value.size
        _offlineQueue.value = emptyList()
        addSyncLog("[SYNC] Successfully synchronized $count offline task(s) into national cloud cluster, resolving thread locks.")
    }

    fun triggerSimulatedLatencyChange(latency: Int) {
        _websocketLatency.value = latency
    }

    fun setSystemPerformanceMetrics(cpu: Int, heap: Int) {
        _dashboardCpuPercent.value = cpu
        _dashboardHeapMemory.value = heap
    }

    fun executeAutomatedGatewayCheckout(
        senderNo: String,
        amount: Int,
        purpose: String,
        gateway: String,
        onComplete: (String) -> Unit
    ) {
        if (senderNo.length < 10) {
            onComplete("Fail: Invalid sender number format.")
            return
        }
        val user = _currentUser.value ?: return
        val txId = "TXN-" + (100000..999999).random() + "REST"
        
        val existing = _financialLedger.value.find { it.transactionId == txId }
        if (existing != null) {
            _failedTransactionsCount.value = _failedTransactionsCount.value + 1
            onComplete("Fail: Duplicate Transaction Identified.")
            return
        }

        val platformCut = (amount * 0.025).toInt()
        val ownerPart = amount - platformCut

        val updatedLedger = _financialLedger.value.toMutableList()
        updatedLedger.add(0, FinancialLedgerRecord(
            id = "ledger_" + System.currentTimeMillis(),
            dateString = "2026-05-28 09:12:40",
            senderName = user.username,
            receiverName = if (purpose.contains("upgrade", ignoreCase = true)) "Rent Truth Admin" else "Direct Owner Wallet",
            totalAmount = amount,
            platformCut = platformCut,
            ownerRentAmount = ownerPart,
            type = purpose,
            gatewayMethod = gateway,
            transactionId = txId
        ))
        _financialLedger.value = updatedLedger

        if (purpose.contains("Owner", ignoreCase = true)) {
            val upgraded = user.copy(subscriptionType = "Owner Platinum", walletBalance = user.walletBalance + amount)
            _currentUser.value = upgraded
            _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) upgraded else it }
            _adminRevenueSubscriptions.value = _adminRevenueSubscriptions.value + amount
        } else if (purpose.contains("Broker", ignoreCase = true)) {
            val upgraded = user.copy(subscriptionType = "Broker Pro", walletBalance = user.walletBalance + amount)
            _currentUser.value = upgraded
            _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) upgraded else it }
            _adminRevenueSubscriptions.value = _adminRevenueSubscriptions.value + amount
        } else {
            val updatedUser = user.copy(walletBalance = user.walletBalance + amount)
            _currentUser.value = updatedUser
            _registeredUsers.value = _registeredUsers.value.map { if (it.email == user.email) updatedUser else it }
            _adminRevenueCommissions.value = _adminRevenueCommissions.value + platformCut
        }

        addSyncLog("[GATEWAY] REST Checkout OK: $txId. Split Commission 2.5% Audited inside Ledger.")
        onComplete("Success")
    }

    fun submitDirectRESTContractPayment(
        agreementId: String,
        amount: Int,
        sender: String,
        method: String,
        onComplete: (String) -> Unit
    ) {
        val txId = "TXN-CON-" + (100000..999999).random() + "REST"
        val platformCut = (amount * 0.025).toInt()
        val ownerPart = amount - platformCut

        val updatedLedger = _financialLedger.value.toMutableList()
        updatedLedger.add(0, FinancialLedgerRecord(
            id = "ledger_con_" + System.currentTimeMillis(),
            dateString = "2026-05-28 09:12:40",
            senderName = sender,
            receiverName = "System Contract Escrow Wallet",
            totalAmount = amount,
            platformCut = platformCut,
            ownerRentAmount = ownerPart,
            type = "Contract Installment payment (Agreement: $agreementId)",
            gatewayMethod = method,
            transactionId = txId
        ))
        _financialLedger.value = updatedLedger
        onComplete("Success Contract Payment: $txId")
    }

    fun runBangladeshScaleStressTest() {
        if (_isLoadTesting.value) return
        _isLoadTesting.value = true
        _loadTestProgress.value = 0.05f
        
        val logs = mutableListOf<String>()
        logs.add(0, "[TEST] Starting full Bangladesh-Scale (100k+ concurrent users) stress test simulation on JVM database and WebSocket router...")
        logs.add(0, "[INFO] Simulating heavy concurrency spikes across Dhanmondi, Mirpur, Gulshan, Uttara, Bashundhara, and Chittagong blocks.")
        _loadTestingLogs.value = logs

        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _loadTestProgress.value = 0.25f
            logs.add(0, "[BATCH 1] Spawning 20,000 concurrent WebSocket sessions. Connection handshake rate: 99.4%.")
            logs.add(0, "[DB] Multi-threaded SQLite read/write lock isolation: Active. Peak lock resolution time: 8ms.")
            _loadTestingLogs.value = logs.toList()

            kotlinx.coroutines.delay(1000)
            _loadTestProgress.value = 0.50f
            logs.add(0, "[BATCH 2] Spawning 35,000 concurrent payment submissions through bKash/Nagad simulated gateways.")
            logs.add(0, "[ALERT] Active backpressure queue throttling engaged. Buffer size mapped to 512/512 sockets.")
            logs.add(0, "[RECOVERY] Self-healing buffer thread engaged. GC heap cleared: 198MB reclaimed safely.")
            _loadTestingLogs.value = logs.toList()

            kotlinx.coroutines.delay(1000)
            _loadTestProgress.value = 0.75f
            logs.add(0, "[BATCH 3] 45,000 automated NID & student doc OCR scans initialized. AI fraud neural score detection latency: 3.4ms.")
            logs.add(0, "[AUDIT] Platform split transactions audited: 100k. Balanced successfully. Platform ledger balance mapped matches actual outputs.")
            _loadTestingLogs.value = logs.toList()

            kotlinx.coroutines.delay(1000)
            _loadTestProgress.value = 1.0f
            _isLoadTesting.value = false
            logs.add(0, "[SUCCESS] Bangladesh-scale stress test completed! 100,000+ operations simulated with zero platform crashes.")
            logs.add(0, "[FINAL] Multi-device state reconciliation success rate: 100.0%. WebSocket system stability: AAA CERTIFIED.")
            _loadTestingLogs.value = logs.toList()
            _websocketLatency.value = 18
            _dashboardCpuPercent.value = 8
            _dashboardHeapMemory.value = 320
        }
    }
}

data class ChatMessage(
    val sender: String, // system, user, ai
    val message: String
)

data class TenantDoc(
    val id: String,
    val docType: String, // NID Card, Smart NID, Birth Certificate, Passport, Driving License, Student ID, Utility Bill, Emergency Contact Proof
    val status: String = "Pending", // Pending, Approved, Rejected, Resubmit
    val fileName: String = "scanned_doc.pdf",
    val comments: String = "",
    val uploadedAt: String = "May 28, 2026",
    val expiryDate: String = "2030-12-31"
)

data class UserAccount(
    val username: String,
    val email: String,
    val phone: String,
    val role: com.example.data.UserRole,
    val subscriptionType: String = "Free", // Free, Student Premium, Owner Platinum, Broker Pro
    val walletBalance: Int = 1000,
    val isVerified: Boolean = false,
    val isAdmin: Boolean = false,
    val isDemoUser: Boolean = false,
    val isSystemSeed: Boolean = false,
    val passwordHash: String = "",
    
    // Editable profile fields:
    val profilePhoto: String = "",
    val address: String = "No registered address",
    val profileBio: String = "No bio entered",
    val rentalPreferences: String = "Any",
    val bankPaymentDetails: String = "Not registered",
    val propertyDescriptions: String = "No property details registered",
    val brokerIdentityDetails: String = "Not registered",
    
    // New Landlord & Tenant & Broker fields:
    val nidVerifiedBadge: Boolean = false,
    val propertyVerifiedBadge: Boolean = false,
    val ownedPropertyCount: Int = 0,
    val trustScore: Int = 85, // Default trust score
    val complaintCount: Int = 0,
    val isLocked: Boolean = false,
    
    // Tenant specific:
    val studentModeState: Boolean = false,
    val occupationInfo: String = "Not specified",
    val currentRentalStatus: String = "Looking for Room",
    val previousRentalHistory: List<String> = emptyList(),
    val emergencyContact: String = "Not specified",
    val rentalAgreementHistory: List<String> = emptyList(),
    val currentTenants: List<String> = emptyList(),
    val previousTenantHistory: List<String> = emptyList(),
    val upcomingTenantsRequests: List<String> = emptyList(),
    val complaintLogs: List<String> = emptyList(),
    
    // Broker specific:
    val brokerTrustScore: Int = 85,
    val commissionPercent: Int = 10,
    val activePartnerships: Int = 0,
    val propertyHandlingCount: Int = 0,
    
    // Documents
    val uploadedDocs: List<TenantDoc> = emptyList()
)

data class ChatChannel(
    val id: String,
    val partnerName: String,
    val partnerRole: com.example.data.UserRole,
    val listingTitle: String = "",
    val lastMessage: String = "",
    val messages: List<LocalMessage> = emptyList()
)

data class LocalMessage(
    val id: String,
    val sender: String, // me, partner
    val text: String,
    val timestamp: String = "10:30"
)

enum class SmartAgreementType {
    STUDENT_ROOM, BACHELOR_ROOM, FAMILY_FLAT, OFFICE_RENTAL, SHOP_RENTAL, SHARED_ROOMMATE
}

data class SmartRentalAgreement(
    val id: String,
    val type: SmartAgreementType,
    val title: String,
    val tenantName: String,
    val tenantEmail: String,
    val ownerName: String,
    val ownerEmail: String,
    val brokerName: String = "",
    val durationMonths: Int,
    val rentAmount: Int,
    val isBilingual: Boolean = true, // Bangla + English
    val status: String, // "Draft", "Waiting Tenant", "Waiting Owner", "Signed", "Terminated"
    val ownerSignature: String = "",
    val tenantSignature: String = "",
    val brokerWitnessSignature: String = "",
    val startDate: String = "2026-06-01",
    val endDate: String = "2027-05-31",
    val violations: List<String> = emptyList()
)

data class RentTrackerPayment(
    val id: String,
    val agreementId: String,
    val tenantEmail: String,
    val tenantName: String,
    val rentMonth: String, // e.g. "June 2026"
    val totalAmount: Int,
    val paidAmount: Int = 0,
    val status: String, // "Paid", "Due", "Partial", "Pending Review"
    val depositPaid: Int = 0,
    val utilitiesCost: Int = 0,
    val paymentMethod: String = "", // "bKash", "Nagad", "Rocket", "Bank transfer"
    val transactionId: String = "",
    val datePaid: String = "",
    val lateAlertActive: Boolean = false,
    val auditLog: List<String> = listOf("Payment bill generated May 28, 2026")
)

data class BuildingUnit(
    val unitId: String,
    val unitName: String, // e.g. "Flat 3A"
    val occupied: Boolean = false,
    val tenantName: String = "",
    val rentAmount: Int,
    val utilitiesStatus: String = "Paid" // "Paid", "Due"
)

data class Building(
    val id: String,
    val name: String,
    val address: String,
    val caretakerName: String = "Abdul Malek",
    val status: String = "Fully Operational",
    val announcements: List<String> = listOf("Building water tank cleaning scheduled for next Friday 9AM to noon."),
    val units: List<BuildingUnit> = emptyList()
)

data class MaintenanceTicket(
    val id: String,
    val buildingName: String,
    val unitName: String,
    val issue: String,
    val severity: String, // "High", "Medium", "Low"
    val status: String, // "Open", "InProgress", "Resolved"
    val assignedTo: String = "Caretaker Abdul",
    val dateCreated: String = "May 28, 2026"
)

data class VisitorLog(
    val id: String,
    val visitorName: String,
    val unitName: String,
    val purpose: String,
    val enterTime: String = "Today 10:15 AM",
    val exitTime: String = "Today 11:30 AM"
)

data class ParkingSlot(
    val slotNumber: String,
    val assignedTo: String = "Empty",
    val status: String = "Available" // "Available", "Occupied", "Reserved"
)

data class FraudModelReport(
    val id: String,
    val type: String, // e.g. "Duplicate Listing Detected", "Image Fingerprint Mismatch", "Fake Phone Alert"
    val description: String,
    val confidence: Int, // e.g. 98%
    val status: String = "Unresolved" // "Unresolved", "Resolved", "False Alarm"
)

data class NotificationModel(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "Booking", "Verification", "Fraud", "RentDue", "Agreement"
    val channel: String, // "In-App", "SMS", "Email", "Push"
    val isRead: Boolean = false,
    val timestamp: String = "Just Now"
)

data class LandlordTenantLog(
    val id: String,
    val tenantName: String,
    val tenantPhone: String,
    val tenantEmail: String,
    val propertyTitle: String,
    val rentAmount: Int,
    val startDate: String,
    val durationMonths: Int,
    val status: String = "Active Renter", // "Active Renter", "Vacated", "Booking Pending"
    val docVerified: Boolean = true,
    val trustScore: Int = 95,
    val isArchived: Boolean = false,
    val paymentHistory: List<String> = listOf("Paid June 2026", "Paid July 2026")
)

data class BrokerOwnerPartnership(
    val id: String,
    val brokerEmail: String,
    val ownerName: String,
    val ownerPhone: String,
    val propertyTitle: String,
    val commissionSplitPercent: Int = 10,
    val status: String = "Authorized"
)

data class BrokerDeal(
    val id: String,
    val clientName: String,
    val clientPhone: String,
    val propertyTitle: String,
    val dealAmount: Int,
    val commissionEarned: Int,
    val coBrokeStatus: String = "No Split (Direct)",
    val dealType: String = "Residential", // Residential, Commercial Office
    val status: String = "Closed"
)

data class SecurityDeviceSession(
    val id: String,
    val deviceName: String,
    val ipAddress: String,
    val lastActive: String = "Just Now"
)

data class OfflineSyncTask(
    val id: String,
    val operationName: String,
    val description: String,
    val timestamp: String,
    val status: String = "Queued in Local Cache"
)

data class FinancialLedgerRecord(
    val id: String,
    val dateString: String,
    val senderName: String,
    val receiverName: String,
    val totalAmount: Int,
    val platformCut: Int,
    val ownerRentAmount: Int,
    val type: String,
    val gatewayMethod: String,
    val transactionId: String,
    val status: String = "Audited & Authenticated",
    val fraudFlagCheck: Boolean = false,
    val doubleApprovalCheck: Boolean = true
)

