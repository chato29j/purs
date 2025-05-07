package com.example.finalclockapplication.Home.UtData

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.Home.Alertas.AlertasActivity
import com.example.finalclockapplication.Home.Historicos.HistoricosActivity
import com.example.finalclockapplication.Home.HomeActivity
import com.example.finalclockapplication.R

class UtDataActivity : AppCompatActivity() {


    private lateinit var btnHome: ImageButton
    private lateinit var btnHistorical: ImageButton
    private lateinit var btnAlert: ImageButton
    private lateinit var btnNext: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ut_data)

        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initComponents() {
        btnHome = findViewById(R.id.btnHome)
        btnHistorical = findViewById(R.id.btnHistorical)
        btnAlert = findViewById(R.id.btnAlert)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun initListeners() {
        btnHome.setOnClickListener {
            navigateToHome()
        }
        btnHistorical.setOnClickListener {
            navigateToHistorical()
        }
        btnAlert.setOnClickListener {
            navigateToAlert()
        }
        btnNext.setOnClickListener {
            navigateToHistorical()
        }
    }

    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistorical(){
        val intent = Intent(this, HistoricosActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToAlert(){
        val intent = Intent(this, AlertasActivity::class.java)
        startActivity(intent)
    }

}