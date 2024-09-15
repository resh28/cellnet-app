package com.example.cellnet.core.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.cellnet.core.common.KotlinSerializationMapHelper
import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.NetworkInfo
import com.example.cellnet.core.common.model.User
import com.example.cellnet.core.data.iRepository.FirebaseRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class DefaultFirebaseRepository @Inject constructor(

): FirebaseRepository {
    private var auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    override suspend fun signOutUser() {
        auth.signOut()
    }

    override suspend fun signUpUser(email: String, password: String, userData: User): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                saveUser(user.uid, userData)
                Result.success(it.uid)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                Result.success(it.uid)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return try {
            val user = auth.currentUser
            val email = user?.email
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await() // Re-authenticate first
                user.updatePassword(newPassword).await() // Then update the password
                Result.success("Password updated successfully")
            } else {
                Result.failure(Exception("User is not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<DocumentSnapshot> {
        return try {
            val docRef = db.collection("users").document(userId)
            val document = docRef.get().await()
            if (document.exists()) {
                Result.success(document)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     override suspend fun saveUser(userId: String, userData: User): Result<String> {
         return try {
            val user = KotlinSerializationMapHelper.toMap(userData)
            db.collection("users").document(userId).set(user).await()
            Result.success("User Created")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveDeviceInfo(deviceInfo: DeviceInfo): Result<String> {
        return try {
            db.collection("devices").document(deviceInfo.androidId).set(deviceInfo).await()
            Result.success("Device info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCellTowerInfo(cellTowerInfo: CellTowerInfo): Result<String> {
        return try {
            db.collection("cellTowers").document(cellTowerInfo.uid).set(cellTowerInfo).await()
            Result.success("Cell tower info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveNetworkInfo(networkInfo: NetworkInfo): Result<String> {
        return try {
            db.collection("networkData").document().set(networkInfo).await()
            Result.success("Network info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLastNetworkInfo(duration: Long): Result<List<NetworkInfo>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, -duration.toInt())
            val timeStamp = calendar.time

            val documents = db.collection("networkData")
                .whereEqualTo("userId", auth.currentUser?.uid)
                .whereGreaterThanOrEqualTo("timeStamp", timeStamp)
                .orderBy("timeStamp")
                .get()
                .await()

            if (!documents.isEmpty) {
                val networkInfoList = documents.map { document ->
                    document.toObject(NetworkInfo::class.java)
                }
                Result.success(networkInfoList)
            } else {
                Result.failure(Exception("No network data found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCellTowerData(cellTowerIds: List<String>): Result<List<CellTowerInfo>> {
        return try {
            val documents = db.collection("cellTowers")
                .whereIn("uid", cellTowerIds)
                .get()
                .await()

            if (!documents.isEmpty) {
                val cellTowerInfoList = documents.map { document ->
                    document.toObject(CellTowerInfo::class.java)
                }
                Result.success(cellTowerInfoList)
            } else {
                Result.failure(Exception("No cell tower data found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}