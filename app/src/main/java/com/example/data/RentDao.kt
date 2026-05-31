package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RentDao {
    // Listings
    @Query("SELECT * FROM listings")
    fun getAllListings(): Flow<List<RentListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: RentListing)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: Int)

    @Query("UPDATE listings SET availabilityState = :state WHERE id = :id")
    suspend fun updateAvailability(id: Int, state: String)

    @Query("UPDATE listings SET complaintCount = complaintCount + 1 WHERE id = :id")
    suspend fun incrementComplaint(id: Int)

    @Query("UPDATE listings SET isScamFlaggedByUsers = :isScam WHERE id = :id")
    suspend fun updateScamFlag(id: Int, isScam: Boolean)

    @Query("UPDATE listings SET isVerifiedOwner = :isVerified, isTrustedOwner = :isVerified WHERE id = :id")
    suspend fun updateListingVerification(id: Int, isVerified: Boolean)

    // Negotiations
    @Query("SELECT * FROM negotiations")
    fun getAllNegotiations(): Flow<List<Negotiation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegotiation(negotiation: Negotiation)

    @Query("UPDATE negotiations SET status = :status WHERE id = :id")
    suspend fun updateNegotiationStatus(id: Int, status: String)

    // Roommates
    @Query("SELECT * FROM roommate_requests")
    fun getAllRoommateRequests(): Flow<List<RoommateRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoommateRequest(request: RoommateRequest)

    // Reviews
    @Query("SELECT * FROM tenant_reviews WHERE listingId = :listingId")
    fun getReviewsForListing(listingId: Int): Flow<List<TenantReview>>

    @Query("SELECT * FROM tenant_reviews")
    fun getAllReviews(): Flow<List<TenantReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: TenantReview)

    // Escrow Deposits
    @Query("SELECT * FROM escrow_deposits")
    fun getAllEscrowDeposits(): Flow<List<EscrowDeposit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrowDeposit(deposit: EscrowDeposit)

    @Query("UPDATE escrow_deposits SET status = :status WHERE id = :id")
    suspend fun updateEscrowStatus(id: Int, status: String)

    // Manual Payments (bKash & Nagad Auditing)
    @Query("SELECT * FROM manual_payments")
    fun getAllManualPayments(): Flow<List<ManualPaymentSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManualPayment(payment: ManualPaymentSubmission)

    @Query("UPDATE manual_payments SET status = :status WHERE id = :id")
    suspend fun updateManualPaymentStatus(id: Int, status: String)

    // User Accounts Persistence
    @Query("SELECT * FROM user_accounts")
    fun getAllUserAccounts(): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccount)

    // Custom Stamps & Legal Agreements
    @Query("SELECT * FROM rental_agreements")
    fun getAllRentalAgreements(): Flow<List<SmartRentalAgreement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentalAgreement(agreement: SmartRentalAgreement)

    @Query("DELETE FROM rental_agreements WHERE id = :id")
    suspend fun deleteRentalAgreement(id: String)

    // Authorized Behavioral Ratings
    @Query("SELECT * FROM authorized_ratings")
    fun getAllAuthorizedRatings(): Flow<List<AuthorizedRating>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthorizedRating(rating: AuthorizedRating)
}
