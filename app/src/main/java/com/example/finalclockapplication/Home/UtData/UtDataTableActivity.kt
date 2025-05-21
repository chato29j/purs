package com.example.finalclockapplication.Home.UtData

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.min
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class UtDataTableActivity : AppCompatActivity() {

    private lateinit var btnBack_3: ImageButton
    private lateinit var btnSave_3: ImageButton
    private lateinit var btnUtData_3: ImageButton
    private lateinit var tableLayout: TableLayout

    private val CHANNEL_ID = "health_alert_channel"
    private val NOTIFICATION_ID = 1

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ut_data_table)

        initComponents()
        initListeners()
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponents() {
        btnBack_3 = findViewById(R.id.btnBack_3)
        btnSave_3 = findViewById(R.id.btnSave_3)
        btnUtData_3 = findViewById(R.id.btnUtData_3)
        tableLayout = findViewById(R.id.tableLayout)
    }

    private fun initListeners() {
        btnBack_3.setOnClickListener { navigateToUtGraph() }
        btnUtData_3.setOnClickListener { navigateToUtData() }

        btnSave_3.setOnClickListener {
            Toast.makeText(this, "Cargando tabla...", Toast.LENGTH_SHORT).show()
            tableLayout.removeAllViews()
            fetchAndFillTableFromFirestore()
        }
    }

    private fun navigateToUtData() {
        startActivity(Intent(this, UtDataActivity::class.java))
    }

    private fun navigateToUtGraph() {
        startActivity(Intent(this, UtDataGraphActivity::class.java))
    }

    private fun fetchAndFillTableFromFirestore() {
        try {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val userName = user.displayName ?: user.email?.substringBefore("@")
            ?: throw Exception("Nombre de usuario no disponible")

            db.collection(userName)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(9)
                .get()
                .addOnSuccessListener { documents ->
                    val values = documents.mapNotNull {
                        val value = it.data["value"]
                        android.util.Log.d("FIRESTORE_DATA", "Documento: ${it.id}, value: $value (${value?.javaClass?.simpleName})")
                        when (value) {
                            is Number -> value.toInt()
                            is String -> value.toIntOrNull()
                            else -> null
                        }
                    }.reversed()

                    fillTableWithValues(values)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al obtener datos: ${exception.message}", Toast.LENGTH_LONG).show()
                    exception.printStackTrace()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun fillTableWithValues(values: List<Int>) {
        var greenCount = 0
        var index = 0

        for (i in 0 until 3) {
            val row = TableRow(this).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
            }

            for (j in 0 until 3) {
                val value = if (index < values.size) values[index++] else 0
                val cell = TextView(this).apply {
                    text = value.toString()
                    textSize = 18f
                    setPadding(20, 20, 20, 20)
                    setTextColor(Color.BLACK)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                    when (value) {
                        in 1..30 -> setBackgroundResource(R.drawable.cell_red)
                        in 31..65 -> setBackgroundResource(R.drawable.cell_yellow)
                        in 66..100 -> {
                            setBackgroundResource(R.drawable.cell_green)
                            greenCount++
                        }
                        else -> setBackgroundResource(R.drawable.cell_border)
                    }
                }
                row.addView(cell)
            }
            tableLayout.addView(row)
        }

        if (greenCount < 6) {
            showHealthDangerNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alerta de salud",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando la salud esté en riesgo"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showHealthDangerNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("¡Salud en peligro!")
                .setContentText("Menos de 6 parámetros están en nivel óptimo.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
        } else {
            Toast.makeText(this, "Permiso de notificaciones no concedido", Toast.LENGTH_SHORT).show()
        }
    }
}

