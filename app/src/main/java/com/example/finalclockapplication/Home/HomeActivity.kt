package com.example.finalclockapplication.Home

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finalclockapplication.LogInActivity
import com.example.finalclockapplication.R
import com.example.finalclockapplication.Home.Alertas.AlertasActivity
import com.example.finalclockapplication.Home.Historicos.HistoricosActivity
import com.example.finalclockapplication.Home.UtData.UtDataActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var viewUtData: CardView
    private lateinit var viewHistoricos: CardView
    private lateinit var viewAlertas: CardView
    private lateinit var viewLogOut: CardView
    private lateinit var text: TextView
    private lateinit var statusIndicator: TextView
    private lateinit var spinnerDevices: Spinner

    private val PERMISSION_REQUEST_CODE = 1001
    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bleManager: BleManager

    private val devicesMap = mutableMapOf<String, BluetoothDevice>()
    private lateinit var deviceNamesAdapter: ArrayAdapter<String>
    private var userCollectionName: String = "usuarios"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
            return
        }

        initComponents()
        mostrarNombreUsuario()
        initListeners()
    }

    private fun initComponents() {
        viewUtData = findViewById(R.id.viewUtData)
        viewHistoricos = findViewById(R.id.viewHistoricos)
        viewAlertas = findViewById(R.id.viewAlertas)
        viewLogOut = findViewById(R.id.viewLogOut)
        text = findViewById(R.id.tvText_T)
        statusIndicator = findViewById(R.id.statusIndicator)
        spinnerDevices = findViewById(R.id.spinnerDevices)

        deviceNamesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        deviceNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDevices.adapter = deviceNamesAdapter
    }

    private fun initListeners() {
        viewUtData.setOnClickListener { navigateToData() }
        viewHistoricos.setOnClickListener { navigateToHistoricos() }
        viewAlertas.setOnClickListener { navigateToAlertas() }
        viewLogOut.setOnClickListener { cerrarSesion() }

        spinnerDevices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                val deviceName = parent?.getItemAtPosition(position) as String
                val device = devicesMap[deviceName]
                device?.let {
                    bleManager.connectToDevice(it) { conectado ->
                        runOnUiThread {
                            if (conectado) {
                                statusIndicator.text = "Conectado"
                                statusIndicator.setBackgroundColor(Color.GREEN)
                                Toast.makeText(applicationContext, "Conexión exitosa", Toast.LENGTH_SHORT).show()
                            } else {
                                statusIndicator.text = "Desconectado"
                                statusIndicator.setBackgroundColor(Color.RED)
                                Toast.makeText(applicationContext, "Desconectado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mostrarNombreUsuario() {
        val user = auth.currentUser ?: return
        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener {
                val nombre = it.getString("name")
                userCollectionName = nombre ?: "usuario_desconocido"
                text.text = "Hola: $userCollectionName"

                // Instanciar BleManager con el nombre del usuario
                bleManager = BleManager(this, userCollectionName)

                // Si ya hay permisos, iniciar escaneo
                if (hasBluetoothPermissions()) {
                    iniciarEscaneoBLE()
                } else {
                    requestBluetoothPermissions()
                }
            }
            .addOnFailureListener {
                text.text = "Hola usuario"
                Toast.makeText(this, "No se pudo obtener el nombre", Toast.LENGTH_SHORT).show()
            }
    }

    private fun iniciarEscaneoBLE() {
        bleManager.startScan { device ->
            runOnUiThread {
                val deviceName = getSafeDeviceName(device)
                if (!devicesMap.containsKey(deviceName)) {
                    devicesMap[deviceName] = device
                    deviceNamesAdapter.add(deviceName)
                    deviceNamesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getSafeDeviceName(device: BluetoothDevice): String {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            device.name ?: "Dispositivo desconocido (${device.address})"
        } else {
            "Permiso no concedido"
        }
    }

    private fun cerrarSesion() {
        auth.signOut()
        startActivity(Intent(this, LogInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun navigateToData() {
        startActivity(Intent(this, UtDataActivity::class.java))
    }

    private fun navigateToHistoricos() {
        startActivity(Intent(this, HistoricosActivity::class.java))
    }

    private fun navigateToAlertas() {
        startActivity(Intent(this, AlertasActivity::class.java))
    }

    private fun hasBluetoothPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasBluetoothPermissions()) {
                iniciarEscaneoBLE()
            } else {
                Toast.makeText(this, "Se requieren permisos Bluetooth para continuar", Toast.LENGTH_LONG).show()
            }
        }
    }
}
