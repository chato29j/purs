package com.example.finalclockapplication.Home

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BleManager(private val context: Context, private val userCollection: String) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val scanResults = mutableMapOf<String, BluetoothDevice>()
    private var bluetoothGatt: BluetoothGatt? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val handler = Handler(Looper.getMainLooper())

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (!scanResults.containsKey(device.address)) {
                    scanResults[device.address] = device
                    onDeviceFound?.invoke(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleManager", "Error al escanear BLE: $errorCode")
        }
    }

    private var onDeviceFound: ((BluetoothDevice) -> Unit)? = null

    @SuppressLint("MissingPermission")
    fun startScan(onDeviceFoundCallback: (BluetoothDevice) -> Unit) {
        onDeviceFound = onDeviceFoundCallback
        val scanFilters = listOf<ScanFilter>()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner?.startScan(scanFilters, scanSettings, scanCallback)

        handler.postDelayed({ stopScan() }, 10000)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice, onConnected: (Boolean) -> Unit) {
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt?.discoverServices()
                    handler.post {
                        onConnected(true)
                        Toast.makeText(context, "Conectado a ${device.name}", Toast.LENGTH_SHORT).show()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    handler.post {
                        onConnected(false)
                        Toast.makeText(context, "Desconectado de ${device.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                gatt?.services?.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                            gatt.setCharacteristicNotification(characteristic, true)

                            val descriptor = characteristic.getDescriptor(
                                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                            )
                            descriptor?.let {
                                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                gatt.writeDescriptor(it)
                            }

                        } else if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                            gatt.readCharacteristic(characteristic)
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                characteristic?.value?.let { value ->
                    val dataStr = value.toString(Charsets.UTF_8).trim()
                    val intValue = dataStr.toIntOrNull()
                    if (intValue != null) {
                        guardarDatoEnFirestore(intValue)
                    } else {
                        Log.w("BleManager", "Dato recibido no es entero válido: '$dataStr'")
                    }
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS && characteristic != null) {
                    val dataStr = characteristic.value?.toString(Charsets.UTF_8)?.trim()
                    val intValue = dataStr?.toIntOrNull()
                    if (intValue != null) {
                        guardarDatoEnFirestore(intValue)
                    } else {
                        Log.w("BleManager", "Lectura inicial no es entero válido: '$dataStr'")
                    }
                }
            }
        })
    }

    private fun guardarDatoEnFirestore(valor: Int) {
        val dataMap = hashMapOf(
            "valor" to valor,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection(userCollection)
            .add(dataMap)
            .addOnSuccessListener {
                Log.d("BleManager", "Dato guardado: $valor")
            }
            .addOnFailureListener {
                Log.e("BleManager", "Error al guardar: ${it.message}")
            }
    }
}
