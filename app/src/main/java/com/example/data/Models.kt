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
