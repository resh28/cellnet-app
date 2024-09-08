package com.example.cellnet.core.data.repository

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
            val deviceInfoMap = KotlinSerializationMapHelper.toMap(deviceInfo)
            db.collection("devices").document(deviceInfo.androidId).set(deviceInfoMap).await()
            Result.success("Device info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCellTowerInfo(cellTowerInfo: CellTowerInfo): Result<String> {
        return try {
            val cellTowerInfoMap = KotlinSerializationMapHelper.toMap(cellTowerInfo)
            db.collection("cellTowers").document(cellTowerInfo.uId).set(cellTowerInfoMap).await()
            Result.success("Cell tower info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveNetworkInfo(networkInfo: NetworkInfo): Result<String> {
        return try {
            val networkInfoMap = KotlinSerializationMapHelper.toMap(networkInfo)
            db.collection("networkData").document().set(networkInfoMap).await()
            Result.success("Network info saved successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}