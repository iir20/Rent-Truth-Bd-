package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    OWNER, TENANT, OFFICE, BROKER, SYSTEM
}

@Entity(tableName = "listings")
data class RentListing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val area: String, // e.g., Dhanmondi, Mirpur, Gulshan, Uttara, Bashundhara
    val address: String,
    val rentAmount: Int,
    val type: String, // e.g., Bachelor, Family, Sublet, Office
    val ruleElectricity: String = "Paid per usage (Sub-meter)",
    val ruleWater: String = "Fixed 500 BDT/month",
    val securityDeposit: Int = 10000,
    val isVerifiedOwner: Boolean = false,
    val isTrustedOwner: Boolean = false,
    val hasVideoWalkthrough: Boolean = false,
    val videoUrl: String = "",
    val complaintCount: Int = 0,
    val isScamFlaggedByAI: Boolean = false,
    val isScamFlaggedByUsers: Boolean = false,
    val isBrokerListing: Boolean = false,
    val brokerName: String = "",
    val brokerCommission: Int = 0,
    val availabilityState: String = "Vacant", // Vacant, Booked, Maintenance
    val ownerName: String = "Direct Owner",
    val ownerPhone: String = "01712******",
    val latitude: Double = 23.7561, // Sample coords for Dhaka Map
    val longitude: Double = 90.3762,
    val division: String = "Dhaka",
    val district: String = "Dhaka"
)

@Entity(tableName = "manual_payments")
data class ManualPaymentSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderNumber: String,
    val transactionId: String,
    val amount: Int,
    val paymentMethod: String, // bKash, Nagad
    val purpose: String, // Recharge, Upgrade_Student, Upgrade_Owner, Upgrade_Broker
    val status: String = "Pending", // Pending, Approved, Rejected
    val submissionDate: String = "May 2026",
    val userEmail: String,
    val userName: String
)

@Entity(tableName = "negotiations")
data class Negotiation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val tenantName: String,
    val proposedRent: Int,
    val notes: String,
    val status: String = "Pending" // Pending, Accepted, Rejected
)

@Entity(tableName = "roommate_requests")
data class RoommateRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val institution: String, // e.g., NSU, DU, AIUB, BRAC
    val budget: Int,
    val preferredArea: String,
    val lifestyle: String, // e.g., "Studious, Quiet, Non-smoker"
    val rentSplit: String = "50-50",
    val contactDetails: String = "019********"
)

@Entity(tableName = "tenant_reviews")
data class TenantReview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val tenantName: String,
    val cleanlinessRating: Float,
    val ownerBehaviorRating: Float,
    val safetyRating: Float,
    val waterElectricityRating: Float,
    val reviewText: String,
    val dateString: String = "May 2026"
)

@Entity(tableName = "escrow_deposits")
data class EscrowDeposit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val tenantName: String,
    val amount: Int,
    val status: String = "Locked", // Locked, Released, Disputed
    val creationDate: String = "2026-05-27"
)

data class TenantDoc(
    val id: String,
    val docType: String, // NID Card, Smart NID, Birth Certificate, Passport, Driving License, Student ID, Utility Bill, Emergency Contact Proof
    val status: String = "Pending", // Pending, Approved, Rejected, Resubmit
    val fileName: String = "scanned_doc.pdf",
    val comments: String = "",
    val uploadedAt: String = "May 28, 2026",
    val expiryDate: String = "2030-12-31",
    val documentFilePath: String = "",
    val mimeType: String = "",
    val uploadTimestamp: Long = 0L,
    val verificationStatus: String = "Pending Review" // Pending Review, Verified, Rejected, Needs Resubmission
)

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val email: String, // unique email identification key
    val username: String,
    val phone: String,
    val role: UserRole,
    val subscriptionType: String = "Free", // Free, Student Premium, Owner Platinum, Broker Pro
    val walletBalance: Int = 0,
    val escrowBalance: Int = 0,
    val rewards: Int = 0,
    val subscriptionCredits: Int = 0,
    val isVerified: Boolean = false,
    val isAdmin: Boolean = false,
    val isDemoUser: Boolean = false,
    val isSystemSeed: Boolean = false,
    val passwordHash: String = "",
    val passwordSalt: String = "",
    
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
    val nidNumber: String? = "5432167890",
    val uploadedDocs: List<TenantDoc> = emptyList()
)

enum class SmartAgreementType {
    STUDENT_ROOM, BACHELOR_ROOM, FAMILY_FLAT, OFFICE_RENTAL, SHOP_RENTAL, SHARED_ROOMMATE
}

@Entity(tableName = "rental_agreements")
data class SmartRentalAgreement(
    @PrimaryKey val id: String,
    val type: SmartAgreementType,
    val title: String,
    val tenantName: String,
    val tenantEmail: String,
    val ownerName: String,
    val ownerEmail: String,
    val brokerName: String = "",
    val durationMonths: Int,
    val rentAmount: Int,
    val advancePaymentBDT: Int = 5000,          // Custom input: advance amount BDT
    val noticePeriodMonths: Int = 2,           // Custom input: months before vacating
    val customTerms: String = "",               // Special instructions drafted by Owner
    val isBilingual: Boolean = true, // Bangla + English
    val status: String, // "Draft", "Waiting Tenant", "Waiting Owner", "Signed", "Terminated"
    val ownerSignature: String = "",
    val tenantSignature: String = "",
    val brokerWitnessSignature: String = "",
    val startDate: String = "2026-06-01",
    val endDate: String = "2027-05-31",
    val violations: List<String> = emptyList()
)

@Entity(tableName = "authorized_ratings")
data class AuthorizedRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val raterEmail: String,
    val rateeEmail: String,
    val ratingValue: Float, // Overall
    val behaviorRating: Float,
    val cleanlinessRating: Float,
    val onTimePaymentRating: Float,
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relationshipType: String // "OWNER_RATES_TENANT" or "TENANT_RATES_OWNER"
)

