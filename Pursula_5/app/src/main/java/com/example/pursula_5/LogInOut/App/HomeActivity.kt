package com.example.pursula_5.LogInOut.App

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pursula_5.LogInOut.logInActivity
import com.example.pursula_5.LogInOut.register.RegisterActivity
import com.example.pursula_5.R

class HomeActivity : AppCompatActivity() {

    private lateinit var viewUtData:CardView
    private lateinit var viewHistoricos:CardView
    private lateinit var viewAlertas:CardView
    private lateinit var viewLogOut:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        initComponents()
        initListeners()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun initComponents() {
        viewUtData = findViewById(R.id.viewUtData)
        viewHistoricos = findViewById(R.id.viewHistoricos)
        viewAlertas = findViewById(R.id.viewAlertas)
        viewLogOut = findViewById(R.id.viewLogOut)
    }

    private fun initListeners() {
        viewUtData.setOnClickListener {
            navigateToData()
        }
        viewHistoricos.setOnClickListener {
            navigateToHistoricos()
        }
        viewAlertas.setOnClickListener {
            navigateToAlertas()
        }
        viewLogOut.setOnClickListener {
            navigateToLogIn()
        }
    }

    private fun navigateToData() {
        val intent = Intent(this, UtDataActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistoricos() {
        val intent = Intent(this, HistoricosActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAlertas() {
        val intent = Intent(this, AlertasActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLogIn() {
        val intent = Intent(this, logInActivity::class.java)
        startActivity(intent)
    }
}