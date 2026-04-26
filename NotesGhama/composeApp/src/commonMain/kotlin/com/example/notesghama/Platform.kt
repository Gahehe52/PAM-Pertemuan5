package com.example.notesghama

import kotlinx.coroutines.flow.StateFlow

// Antarmuka standar untuk mendapatkan nama platform (bawaan project)
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// Fitur Platform untuk Tugas Minggu 8
expect class DeviceInfo() {
    fun getOsName(): String
    fun getDeviceModel(): String
}

expect class NetworkMonitor() {
    val isConnected: StateFlow<Boolean>
}

expect class BatteryInfo() {
    fun getBatteryLevel(): Int
}