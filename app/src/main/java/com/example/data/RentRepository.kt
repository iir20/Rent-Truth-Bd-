package com.example.data

import kotlinx.coroutines.flow.Flow

class RentRepository(private val rentDao: RentDao) {
    val allListings: Flow<List<RentListing>> = rentDao.getAllListings()
    val allNegotiations: Flow<List<Negotiation>> = rentDao.getAllNegotiations()
    val allRoommateRequests: Flow<List<RoommateRequest>> = rentDao.getAllRoommateRequests()
    val allReviews: Flow<List<TenantReview>> = rentDao.getAllReviews()
    val allEscrowDeposits: Flow<List<EscrowDeposit>> = rentDao.getAllEscrowDeposits()
    val allManualPayments: Flow<List<ManualPaymentSubmission>> = rentDao.getAllManualPayments()
    val allUserAccounts: Flow<List<UserAccount>> = rentDao.getAllUserAccounts()
    val allRentalAgreements: Flow<List<SmartRentalAgreement>> = rentDao.getAllRentalAgreements()
    val allAuthorizedRatings: Flow<List<AuthorizedRating>> = rentDao.getAllAuthorizedRatings()

    suspend fun insertUserAccount(user: UserAccount) {
        rentDao.insertUserAccount(user)
    }

    suspend fun insertRentalAgreement(agreement: SmartRentalAgreement) {
        rentDao.insertRentalAgreement(agreement)
    }

    suspend fun deleteRentalAgreement(id: String) {
        rentDao.deleteRentalAgreement(id)
    }

    suspend fun insertAuthorizedRating(rating: AuthorizedRating) {
        rentDao.insertAuthorizedRating(rating)
    }

    suspend fun insertListing(listing: RentListing) {
        rentDao.insertListing(listing)
    }

    suspend fun deleteListing(id: Int) {
        rentDao.deleteListing(id)
    }

    suspend fun updateAvailability(id: Int, state: String) {
        rentDao.updateAvailability(id, state)
    }

    suspend fun reportComplaint(id: Int) {
        rentDao.incrementComplaint(id)
    }

    suspend fun updateScamFlag(id: Int, isScam: Boolean) {
        rentDao.updateScamFlag(id, isScam)
    }

    suspend fun updateListingVerification(id: Int, isVerified: Boolean) {
        rentDao.updateListingVerification(id, isVerified)
    }

    // Negotiations
    suspend fun insertNegotiation(negotiation: Negotiation) {
        rentDao.insertNegotiation(negotiation)
    }

    suspend fun updateNegotiationStatus(id: Int, status: String) {
        rentDao.updateNegotiationStatus(id, status)
    }

    // Roommate matching
    suspend fun insertRoommateRequest(request: RoommateRequest) {
        rentDao.insertRoommateRequest(request)
    }

    // Reviews
    fun getReviewsForListing(listingId: Int): Flow<List<TenantReview>> {
        return rentDao.getReviewsForListing(listingId)
    }

    suspend fun insertReview(review: TenantReview) {
        rentDao.insertReview(review)
    }

    // Escrow Deposits
    suspend fun insertEscrowDeposit(deposit: EscrowDeposit) {
        rentDao.insertEscrowDeposit(deposit)
    }

    suspend fun updateEscrowStatus(id: Int, status: String) {
        rentDao.updateEscrowStatus(id, status)
    }

    // Manual Payments (bKash & Nagad Auditing)
    suspend fun insertManualPayment(payment: ManualPaymentSubmission) {
        rentDao.insertManualPayment(payment)
    }

    suspend fun updateManualPaymentStatus(id: Int, status: String) {
        rentDao.updateManualPaymentStatus(id, status)
    }
}
