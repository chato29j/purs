package com.example.finalclockapplication.Home.Alertas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.Home.Historicos.HistoricosActivity
import com.example.finalclockapplication.Home.HomeActivity
import com.example.finalclockapplication.Home.UtData.UtDataActivity
import com.example.finalclockapplication.R

class AlertasActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnUtData: ImageButton
    private lateinit var btnHistoricos: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alertas)
        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initComponents() {
        btnBack = findViewById(R.id.btnBack_1)
        btnHome = findViewById(R.id.btnHome_2)
        btnUtData = findViewById(R.id.btnUtData_1)
        btnHistoricos = findViewById(R.id.btnHistoricos_2)
    }

    private fun initListeners() {
        btnBack.setOnClickListener {
            navigateToHistoricos()
        }
        btnHome.setOnClickListener {
            navigateToHome()
        }
        btnUtData.setOnClickListener {
            navigateToData()
        }
        btnHistoricos.setOnClickListener {
            navigateToHistoricos()
        }
    }

    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToHistoricos(){
        val intent = Intent(this, HistoricosActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToData(){
        val intent = Intent(this, UtDataActivity::class.java)
        startActivity(intent)
    }
}