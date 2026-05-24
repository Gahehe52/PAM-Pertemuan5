package com.example.notesghama

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

// Inisialisasi Context Global untuk Android
object AppContext {
    private var contextRef: WeakReference<Context>? = null
    fun init(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }
    fun get(): Context = contextRef?.get()
        ?: throw IllegalStateException("AppContext must be initialized in MainActivity")
}

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// Implementasi Device Info
actual class DeviceInfo actual constructor() {
    actual fun getOsName(): String = "Android ${Build.VERSION.RELEASE}"
    actual fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"
}

// Implementasi Network Monitor
actual class NetworkMonitor actual constructor() {
    private val connectivityManager = AppContext.get()
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(checkCurrentStatus())
    actual val isConnected: StateFlow<Boolean> = _isConnected

    init {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
            }
            override fun onLost(network: Network) {
                _isConnected.value = false
            }
        })
    }

    private fun checkCurrentStatus(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}

// Implementasi Battery Info (Bonus)
actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int {
        val intent = AppContext.get().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level != -1 && scale != -1) (level * 100 / scale) else -1
    }
}