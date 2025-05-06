package com.example.pursula_5.LogInOut.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pursula_5.LogInOut.logInActivity
import com.example.pursula_5.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName:EditText
    private lateinit var etPassword_1:EditText
    private lateinit var etPassword_2:EditText
    private lateinit var etPhone:EditText
    private lateinit var etMail:EditText
    private lateinit var btnRegister:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        initComponents()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    private fun initComponents() {
        etName = findViewById(R.id.etName)
        etPassword_1 = findViewById(R.id.etPassword_1)
        etPassword_2 = findViewById(R.id.etPassword_2)
        etPhone = findViewById(R.id.etPhone)
        etMail = findViewById(R.id.etMail)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun initListeners() {
        btnRegister.setOnClickListener {
            navigateToLog()
        }
    }

    private fun navigateToLog() {
        val name = etName.text.toString()
        val password_1 = etPassword_1.text.toString()
        val password_2 = etPassword_2.text.toString()
        val phone = etPhone.text.toString()
        val mail = etMail.text.toString()

        if(name.isNotEmpty() && password_1.isNotEmpty() && password_2.isNotEmpty()
            && phone.isNotEmpty() && mail.isNotEmpty()){
            val intent = Intent(this, logInActivity::class.java)
            startActivity(intent)
        }

    }

}