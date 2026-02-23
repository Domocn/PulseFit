package com.pulsefit.app.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class DiscountCode(
    val id: String = "",
    val brand: String = "",
    val description: String = "",
    val percentOff: Int = 0,
    val code: String = "",
    val category: String = "",
    val expiryDate: String = "",
    val coinCost: Int = 50,
    val active: Boolean = true,
    val maxRedemptions: Int = 100,
    val redeemedCount: Int = 0
)

data class RedeemedCode(
    val id: String = "",
    val discountCodeId: String = "",
    val userId: String = "",
    val brand: String = "",
    val code: String = "",
    val percentOff: Int = 0,
    val description: String = "",
    val redeemedAt: Long = 0,
    val expiryDate: String = ""
)

@Singleton
class DiscountCodeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "DiscountCodeRepo"
    }

    suspend fun getAvailableCodes(): List<DiscountCode> {
        return try {
            val today = LocalDate.now().toString()
            val snapshot = firestore.collection("discount_codes")
                .whereEqualTo("active", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val code = DiscountCode(
                        id = doc.id,
                        brand = doc.getString("brand") ?: "",
                        description = doc.getString("description") ?: "",
                        percentOff = (doc.getLong("percentOff") ?: 0).toInt(),
                        code = doc.getString("code") ?: "",
                        category = doc.getString("category") ?: "",
                        expiryDate = doc.getString("expiryDate") ?: "",
                        coinCost = (doc.getLong("coinCost") ?: 50).toInt(),
                        active = doc.getBoolean("active") ?: true,
                        maxRedemptions = (doc.getLong("maxRedemptions") ?: 100).toInt(),
                        redeemedCount = (doc.getLong("redeemedCount") ?: 0).toInt()
                    )
                    if (code.expiryDate >= today && code.redeemedCount < code.maxRedemptions) code else null
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse discount code ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch discount codes", e)
            emptyList()
        }
    }

    suspend fun redeemCode(discountCode: DiscountCode): Result<RedeemedCode> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not signed in"))

        return try {
            // Check if already redeemed by this user
            val existing = firestore.collection("code_redemptions")
                .whereEqualTo("userId", uid)
                .whereEqualTo("discountCodeId", discountCode.id)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("You have already redeemed this code"))
            }

            val redemption = RedeemedCode(
                discountCodeId = discountCode.id,
                userId = uid,
                brand = discountCode.brand,
                code = discountCode.code,
                percentOff = discountCode.percentOff,
                description = discountCode.description,
                redeemedAt = System.currentTimeMillis(),
                expiryDate = discountCode.expiryDate
            )

            val docRef = firestore.collection("code_redemptions")
                .add(mapOf(
                    "discountCodeId" to redemption.discountCodeId,
                    "userId" to redemption.userId,
                    "brand" to redemption.brand,
                    "code" to redemption.code,
                    "percentOff" to redemption.percentOff,
                    "description" to redemption.description,
                    "redeemedAt" to redemption.redeemedAt,
                    "expiryDate" to redemption.expiryDate
                ))
                .await()

            Result.success(redemption.copy(id = docRef.id))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to redeem code", e)
            Result.failure(e)
        }
    }

    suspend fun getMyRedeemedCodes(): List<RedeemedCode> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("code_redemptions")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    RedeemedCode(
                        id = doc.id,
                        discountCodeId = doc.getString("discountCodeId") ?: "",
                        userId = doc.getString("userId") ?: "",
                        brand = doc.getString("brand") ?: "",
                        code = doc.getString("code") ?: "",
                        percentOff = (doc.getLong("percentOff") ?: 0).toInt(),
                        description = doc.getString("description") ?: "",
                        redeemedAt = doc.getLong("redeemedAt") ?: 0,
                        expiryDate = doc.getString("expiryDate") ?: ""
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse redeemed code ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch redeemed codes", e)
            emptyList()
        }
    }
}
