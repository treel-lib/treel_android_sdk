package com.treel.androidsdk.ble

sealed class AlertData

class HighPressureAlert: AlertData()
class LowPressureAlert: AlertData()
class HighTemperatureAlert: AlertData()
class TireRotationAlert: AlertData()