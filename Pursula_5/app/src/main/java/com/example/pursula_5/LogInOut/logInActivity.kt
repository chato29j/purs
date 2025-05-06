package com.example.pursula_5.LogInOut

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pursula_5.LogInOut.App.HomeActivity
import com.example.pursula_5.LogInOut.register.RegisterActivity
import com.example.pursula_5.R

class logInActivity : AppCompatActivity() {

    private lateinit var viewLogIn:CardView
    private lateinit var viewRegister:CardView
    private lateinit var etUser:EditText
    private lateinit var etPassword:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initListeners() {
        viewRegister.setOnClickListener {
            navigateToRegister()
        }
        viewLogIn.setOnClickListener {
            navigateToHome()

        }

    }

    private fun initComponents() {
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        viewLogIn = findViewById(R.id.viewLogIn)
        viewRegister = findViewById(R.id.viewRegister)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        val user = etUser.text.toString()
        val password = etPassword.text.toString()

        if (user.isNotEmpty() && password.isNotEmpty()){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}