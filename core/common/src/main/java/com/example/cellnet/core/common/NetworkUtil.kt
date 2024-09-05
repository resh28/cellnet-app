package com.example.cellnet.core.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.cellnet.core.common.model.NetworkOperatorCodes
import com.example.cellnet.core.common.model.cellTowerData

object NetworkUtil {
    private const val PERMISSION_REQUEST_CODE = 1

    fun getNetworkDownSpeed(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        val downSpeed = (nc?.linkDownstreamBandwidthKbps)?.div(1000)
        return downSpeed ?: 0
    }

    fun getNetworkUpSpeed(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        val upSpeed = (nc?.linkUpstreamBandwidthKbps)?.div(1000)
        return upSpeed ?: 0
    }

    // returns 2G,3G,4G,WIFI
    fun getNetworkClass(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnected) return "-" // not connected
        if (info.type == ConnectivityManager.TYPE_WIFI) return "WIFI"
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            return when (info.subtype) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN,
                TelephonyManager.NETWORK_TYPE_GSM -> "2G"

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "3G"

                TelephonyManager.NETWORK_TYPE_LTE,
                TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> "4G"

                TelephonyManager.NETWORK_TYPE_NR -> "5G"

                else -> "?"
            }
        }
        return "?"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getSignalStrength(context: Context): Int {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val signalStrength = telephonyManager.signalStrength
        val signalStrengthDbm = signalStrength?.cellSignalStrengths?.firstOrNull()?.dbm
        return signalStrengthDbm ?: 0
    }

    fun getNetworkOperator(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val carrierName = telephonyManager.networkOperatorName
        return carrierName
    }

    fun getPhoneType(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.phoneType) {
            TelephonyManager.PHONE_TYPE_NONE -> "None"
            TelephonyManager.PHONE_TYPE_GSM -> "GSM"
            TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
            TelephonyManager.PHONE_TYPE_SIP -> "SIP"
            else -> "Unknown"
        }
    }

    fun getWifiSSID(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid?.removePrefix("\"")?.removeSuffix("\"")
    }

    fun getCellTowerData(context: Context): cellTowerData {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cellInfoList = telephonyManager.allCellInfo
            if (cellInfoList != null) {
                for (cellInfo in cellInfoList) {
                    when (cellInfo) {
                        is CellInfoGsm -> {
                            val cellIdentityGsm = cellInfo.cellIdentity
                            val lac = cellIdentityGsm.lac
                            val cid = cellIdentityGsm.cid
                            Log.d("CellTowerLocation", "GSM Cell Tower: LAC=$lac, CID=$cid")
                            return cellTowerData(cid = cid, lac = lac)
                        }

                        is CellInfoLte -> {
                            val cellIdentityLte = cellInfo.cellIdentity
                            val tac = cellIdentityLte.tac
                            val ci = cellIdentityLte.ci
                            Log.d("CellTowerLocation", "LTE Cell Tower: TAC=$tac, CI=$ci")
                            return cellTowerData(cid = ci, lac = tac)
                        }

                        is CellInfoWcdma -> {
                            val cellIdentityWcdma = cellInfo.cellIdentity
                            val lac = cellIdentityWcdma.lac
                            val cid = cellIdentityWcdma.cid
                            Log.d("CellTowerLocation", "WCDMA Cell Tower: LAC=$lac, CID=$cid")
                            return cellTowerData(cid = cid, lac = lac)
                        }

                        // Handle other network types if needed
                        else -> Log.d(
                            "CellTowerLocation",
                            "Unknown Cell Info Type: ${cellInfo.javaClass.simpleName}"
                        )
                    }
                }
            } else {
                Log.d("CellTowerLocation", "No Cell Info available")
            }
        }
        return cellTowerData(null, null)
    }


    fun getNetworkOperatorCodes(context: Context): NetworkOperatorCodes {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Check for permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            Log.e("READ_PHONE_STATE", "Permission not granted")
            return NetworkOperatorCodes("", "")
        }

        val networkOperator = telephonyManager.networkOperator
        if (networkOperator.isNotEmpty()) {
            val mcc = networkOperator.substring(0, 3) // First 3 digits
            val mnc = networkOperator.substring(3) // Remaining digits
            return NetworkOperatorCodes(mcc = mcc, mnc = mnc)
        }

        return NetworkOperatorCodes("", "")
    }


    fun getRadioType(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            return ""
        }

        val cellInfoList = telephonyManager.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                when (cellInfo) {
                    is CellInfoGsm -> return "GSM"
                    is CellInfoLte -> return "LTE"
                    is CellInfoWcdma -> return "WCDMA"
                    is CellInfoCdma -> return "CDMA"
                    // Add more types if needed
                }
            }
        }
        return "Unknown"
    }
}