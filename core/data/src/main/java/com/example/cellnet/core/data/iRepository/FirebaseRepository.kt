package com.example.cellnet.core.data.iRepository

import com.example.cellnet.core.common.model.CellTowerInfo
import com.example.cellnet.core.common.model.DeviceInfo
import com.example.cellnet.core.common.model.NetworkInfo
import com.example.cellnet.core.common.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseRepository {
    suspend fun signUpUser(email: String, password: String, userData: User) : Result<String>
    suspend fun signInUser(email: String, password: String) : Result<String>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String>
    suspend fun getUser(userId: String): Result<DocumentSnapshot>
    suspend fun saveUser(userId: String, userData: User): Result<String>
    suspend fun saveDeviceInfo(deviceInfo: DeviceInfo): Result<String>
    suspend fun saveCellTowerInfo(cellTowerInfo: CellTowerInfo): Result<String>
    suspend fun saveNetworkInfo(networkInfo: NetworkInfo): Result<String>
    suspend fun signOutUser()
}