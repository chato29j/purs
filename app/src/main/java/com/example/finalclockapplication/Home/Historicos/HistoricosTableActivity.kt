package com.example.finalclockapplication.Home.Historicos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.R
import kotlin.random.Random
import android.content.pm.PackageManager

class HistoricosTableActivity : AppCompatActivity() {

    private lateinit var btnBack_5: ImageButton
    private lateinit var btnSave_2: ImageButton
    private lateinit var btnHistoricos_3: ImageButton
    private lateinit var tableLayout_1: TableLayout

    private val CHANNEL_ID = "health_alert_channel"
    private val NOTIFICATION_ID = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historicos_table)

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
        btnBack_5 = findViewById(R.id.btnBack_5)
        btnSave_2 = findViewById(R.id.btnSave_2)
        btnHistoricos_3 = findViewById(R.id.btnHistoricos_3)
        tableLayout_1 = findViewById(R.id.tableLayout_1)
    }

    private fun initListeners() {
        btnBack_5.setOnClickListener {
            navigateToHistoricosGraph()
        }
        btnSave_2.setOnClickListener {
            tableLayout_1.removeAllViews()
            fillTableWithRandomValues()
        }
        btnHistoricos_3.setOnClickListener {
            navigateToHistoricos()
        }
    }

    private fun navigateToHistoricosGraph() {
        val intent = Intent(this, HistoricosGraphActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistoricos() {
        val intent = Intent(this, HistoricosActivity::class.java)
        startActivity(intent)
    }

    private fun fillTableWithRandomValues() {
        var greenCount = 0

        for (i in 0 until 3) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            for (j in 0 until 3) {
                val value = Random.nextInt(1, 101)
                val cell = TextView(this).apply {
                    text = value.toString()
                    textSize = 18f
                    setPadding(20, 20, 20, 20)
                    setTextColor(Color.WHITE)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.cell_border)
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                    when (value) {
                        in 1..30 -> setBackgroundResource(R.drawable.cell_red)
                        in 31..65 -> setBackgroundResource(R.drawable.cell_yellow)
                        in 66..100 -> {
                            setBackgroundResource(R.drawable.cell_green)
                            greenCount++
                        }
                    }
                }
                row.addView(cell)
            }
            tableLayout_1.addView(row)
        }

        // Notificación si hay menos de 6 verdes
        if (greenCount < 6) {
            showHealthDangerNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alerta de salud"
            val descriptionText = "Notificaciones cuando la salud esté en riesgo"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showHealthDangerNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("¡Salud en peligro!")
                .setContentText("Menos de 6 parámetros están en nivel óptimo.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            Toast.makeText(this, "Permiso de notificaciones no concedido", Toast.LENGTH_SHORT).show()
        }
    }
}
