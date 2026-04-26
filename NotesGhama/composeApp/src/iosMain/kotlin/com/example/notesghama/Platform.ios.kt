package com.example.notesghama

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual class DeviceInfo actual constructor() {
    actual fun getOsName(): String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    actual fun getDeviceModel(): String = UIDevice.currentDevice.model
}

actual class NetworkMonitor actual constructor() {
    // Implementasi sederhana untuk iOS (memerlukan library tambahan untuk deteksi real-time yang kompleks)
    actual val isConnected: StateFlow<Boolean> = MutableStateFlow(true)
}

actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int {
        UIDevice.currentDevice.batteryMonitoringEnabled = true
        val level = UIDevice.currentDevice.batteryLevel
        return if (level >= 0) (level * 100).toInt() else -1
    }
}