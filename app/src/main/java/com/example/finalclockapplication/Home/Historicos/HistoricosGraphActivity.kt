package com.example.finalclockapplication.Home.Historicos

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
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

class HistoricosGraphActivity : AppCompatActivity() {

    private lateinit var btnBack_4: ImageButton
    private lateinit var btnSave_1: ImageButton
    private lateinit var btnHistoricosGraph: ImageButton
    private lateinit var spinnerOptions: Spinner
    private lateinit var lineChart: LineChart

    private val options = listOf("Pasado", "Recientes", "Promedio")
    private var selectedOption: String = options[0]
    private val CHANNEL_ID = "historicos_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historicos_graph)

        initComponents()
        initListeners()
        setupSpinner()
        createNotificationChannel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponents() {
        btnBack_4 = findViewById(R.id.btnBack_4)
        btnSave_1 = findViewById(R.id.btnSave_1)
        btnHistoricosGraph = findViewById(R.id.btnHistoricosGraph)
        spinnerOptions = findViewById(R.id.spinnerOptions)
        lineChart = findViewById(R.id.lineChart)
    }

    private fun initListeners() {
        btnBack_4.setOnClickListener { navigateToHistoricosTable() }
        btnSave_1.setOnClickListener { plotFirestoreDataBasedOnOption() }
        btnHistoricosGraph.setOnClickListener { navigateToHistoricos() }

        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedOption = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinnerOptions.adapter = adapter
    }

    private fun plotFirestoreDataBasedOnOption() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(uid).get().addOnSuccessListener { document ->
            val userName = document.getString("name") ?: return@addOnSuccessListener
            val userDataRef = db.collection(userName)

            val query = when (selectedOption) {
                "Pasado" -> userDataRef.orderBy("timestamp", Query.Direction.ASCENDING).limit(10)
                "Recientes" -> userDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(10)
                "Promedio" -> userDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(10)
                else -> userDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(10)
            }

            query.get().addOnSuccessListener { snapshot ->
                val entries = ArrayList<Entry>()
                var belowThresholdCount = 0

                val documents = snapshot.documents.let {
                    if (selectedOption == "Pasado") it else it.reversed()
                }

                for ((index, doc) in documents.withIndex()) {
                    val value = doc.getDouble("valor")?.toFloat() ?: continue
                    if (value < 50) belowThresholdCount++
                    entries.add(Entry(index.toFloat(), value))
                }

                val dataSet = LineDataSet(entries, "Datos: $selectedOption").apply {
                    color = getColor(R.color.white)
                    valueTextColor = getColor(R.color.white)
                    lineWidth = 2f
                    circleRadius = 4f
                    setCircleColor(getColor(R.color.white))
                    setDrawFilled(true)
                    fillColor = getColor(R.color.teal_200)
                }

                lineChart.data = LineData(dataSet)
                lineChart.description = Description().apply {
                    text = "Gráfica: $selectedOption"
                    textColor = getColor(R.color.white)
                }
                lineChart.setBackgroundColor(getColor(R.color.background_component))
                lineChart.animateY(1000)
                lineChart.invalidate()

                if (belowThresholdCount >= 5) {
                    sendNotification("Alerta: muchos valores bajos", "Más de 5 datos están por debajo de 50 en '$selectedOption'.")
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener el nombre del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHistoricosTable() {
        startActivity(Intent(this, HistoricosTableActivity::class.java))
    }

    private fun navigateToHistoricos() {
        startActivity(Intent(this, HistoricosActivity::class.java))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones Históricos"
            val descriptionText = "Canal para valores críticos en históricos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            NotificationManagerCompat.from(this).notify(2001, builder.build())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }
}
