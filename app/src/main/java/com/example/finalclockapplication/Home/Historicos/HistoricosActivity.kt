package com.example.finalclockapplication.Home.Historicos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.Home.Alertas.AlertasActivity
import com.example.finalclockapplication.Home.HomeActivity
import com.example.finalclockapplication.Home.UtData.UtDataActivity
import com.example.finalclockapplication.R

class HistoricosActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnUtData: ImageButton
    private lateinit var btnHome: ImageButton
    private lateinit var btnAlertas: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var viewTable_1: CardView
    private lateinit var viewGraph_1: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historicos)

        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initComponents() {
        btnBack = findViewById(R.id.btnBack)
        btnUtData = findViewById(R.id.btnUtData)
        btnHome = findViewById(R.id.btnHome_1)
        btnAlertas = findViewById(R.id.btnAlertas_1)
        btnNext = findViewById(R.id.btnNext_1)
        viewTable_1 = findViewById(R.id.viewTable_1)
        viewGraph_1 = findViewById(R.id.viewGraph_1)
    }

    private fun initListeners() {
        btnBack.setOnClickListener {
            navigateToData()
        }
        btnUtData.setOnClickListener {
            navigateToData()
        }
        btnHome.setOnClickListener {
            navigateToHome()
        }
        btnAlertas.setOnClickListener {
            navigateToAlertas()
        }
        btnNext.setOnClickListener {
            navigateToAlertas()
        }
        viewTable_1.setOnClickListener {
            navigateToHistoricosTable()
        }
        viewGraph_1.setOnClickListener {
            navigateToHistoricosGraph()
        }
    }

    private fun navigateToHistoricosGraph() {
        val intent = Intent(this, HistoricosGraphActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistoricosTable() {
        val intent = Intent(this, HistoricosTableActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToData(){
        val intent = Intent(this, UtDataActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToAlertas(){
        val intent = Intent(this, AlertasActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToNext(){
        val intent = Intent(this, HistoricosActivity::class.java)
        startActivity(intent)
    }
}