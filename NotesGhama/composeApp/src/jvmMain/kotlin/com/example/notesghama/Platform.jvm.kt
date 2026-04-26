package com.example.notesghama

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual class DeviceInfo actual constructor() {
    actual fun getOsName(): String = System.getProperty("os.name") ?: "Unknown OS"
    actual fun getDeviceModel(): String = "Desktop (JVM)"
}

actual class NetworkMonitor actual constructor() {
    actual val isConnected: StateFlow<Boolean> = MutableStateFlow(true)
}

actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int = 100 // Default untuk desktop tanpa baterai terdeteksi
}