package com.example.cellnet.core.common

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.example.cellnet.core.common.constants.ApiKeys
import com.example.cellnet.core.common.model.NetworkOperatorCodes
import com.example.cellnet.core.common.model.cellTowerData
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object LocationUtil {
    private const val apiKey = ApiKeys.apiKey // Replace with your actual API Key

    fun fetchLocationDetails(cellTowerData: cellTowerData, context: Context, networkOperatorCodes: NetworkOperatorCodes ): Location {
        val urlString = "https://www.googleapis.com/geolocation/v1/geolocate?key=$apiKey"
        // Create a new thread for network operations
//        Thread {
        val towerLocation = Location("")
        try {
            val url = URL(urlString)
            val radioType = NetworkUtil.getRadioType(context)
            val carrier = NetworkUtil.getNetworkOperator(context)

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")

                // Construct request body
                val jsonInputString = JSONObject().apply {
                    put("homeMobileCountryCode", networkOperatorCodes.mcc) // Example MCC
                    put("homeMobileNetworkCode", networkOperatorCodes.mnc) // Example MNC
                    put("radioType", radioType.lowercase())
                    put("carrier", carrier)
                    put("cellTowers", JSONArray().put(JSONObject().apply {
                        put("cellId", cellTowerData.cid)
                        put("locationAreaCode", cellTowerData.lac)
                        put("mobileCountryCode", networkOperatorCodes.mcc)
                        put("mobileNetworkCode", networkOperatorCodes.mnc)
                    }))
                }

                outputStream.use { os ->
                    os.write(jsonInputString.toString().toByteArray())
                    os.flush()
                }

                val response = inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                val location = jsonResponse.getJSONObject("location")

                towerLocation.latitude = location.getDouble("lat")
                towerLocation.longitude = location.getDouble("lng")
                towerLocation.accuracy = jsonResponse.getString("accuracy").toFloat()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return towerLocation
    }

    fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
//                val locationName = address.getAddressLine(0) // Full address
                val locality = address.locality // City name or locality
                val adminArea = address.adminArea // State or administrative area
                val countryName = address.countryName // Country name

                // Combine the names based on your requirement
                return locality ?: adminArea ?: countryName ?: "Location name not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "Location not found"
    }
}