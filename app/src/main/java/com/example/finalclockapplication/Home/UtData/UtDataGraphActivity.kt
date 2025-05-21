package com.example.finalclockapplication.Home.UtData

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class UtDataGraphActivity : AppCompatActivity() {

    private lateinit var btnBack_2: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var btnUtTable: ImageButton
    private lateinit var lineChart: LineChart

    private val CHANNEL_ID = "data_alerts_channel"
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 100

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ut_data_graph)

        requestNotificationPermission()
        createNotificationChannel()

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponents() {
        btnBack_2 = findViewById(R.id.btnBack_2)
        btnSave = findViewById(R.id.btnSave)
        btnUtTable = findViewById(R.id.btnUtTable)
        lineChart = findViewById(R.id.lineChart)
    }

    private fun initListeners() {
        btnBack_2.setOnClickListener { navigateToUtData() }
        btnSave.setOnClickListener { fetchAndDrawDataFromFirestore() }
        btnUtTable.setOnClickListener { navigateToUtTable() }
    }

    private fun navigateToUtData() {
        startActivity(Intent(this, UtDataActivity::class.java))
    }

    private fun navigateToUtTable() {
        startActivity(Intent(this, UtDataTableActivity::class.java))
    }

    private fun fetchAndDrawDataFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val usuariosRef = FirebaseFirestore.getInstance().collection("usuarios")
        usuariosRef.document(currentUser.uid).get().addOnSuccessListener { document ->
            val userName = document.getString("name") ?: return@addOnSuccessListener

            firestore.collection(userName)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { result ->
                    val entries = ArrayList<Entry>()
                    var lowValueCount = 0

                    val sortedDocs = result.documents.reversed()
                    for ((index, doc) in sortedDocs.withIndex()) {
                        val value = doc.getDouble("valor")?.toFloat() ?: continue
                        entries.add(Entry(index.toFloat(), value))
                        if (value < 50) lowValueCount++
                    }

                    val dataSet = LineDataSet(entries, "Valores del sensor")
                    dataSet.color = getColor(R.color.white)
                    dataSet.valueTextColor = getColor(R.color.white)
                    dataSet.lineWidth = 2f
                    dataSet.circleRadius = 4f
                    dataSet.setCircleColor(getColor(R.color.white))
                    dataSet.setDrawFilled(true)
                    dataSet.fillColor = getColor(R.color.teal_200)

                    val lineData = LineData(dataSet)
                    lineChart.data = lineData

                    val description = Description()
                    description.text = "Datos desde Firestore"
                    description.textColor = getColor(R.color.white)
                    lineChart.description = description

                    lineChart.setBackgroundColor(getColor(R.color.background_component))
                    lineChart.animateY(1000)
                    lineChart.invalidate()

                    if (lowValueCount >= 5) {
                        sendNotification()
                    }
                }
        }
    }

    private fun sendNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Valores críticos detectados")
                .setContentText("5 o más valores están por debajo de 50.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(this)) {
                notify(1001, builder.build())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alertas de datos"
            val descriptionText = "Notificaciones cuando hay muchos valores bajos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Opcional: manejar si quieres mostrar algo
        }
    }
}
