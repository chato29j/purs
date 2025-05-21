package com.example.finalclockapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.Home.HomeActivity
import com.example.finalclockapplication.Register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var viewLogIn: CardView
    private lateinit var viewRegister: CardView
    internal lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    internal lateinit var auth: FirebaseAuth
    private lateinit var btnForgotPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        initComponents()
        initListeners()

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            navigateToHome()
        }

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
            loginUser()
        }
        btnForgotPassword.setOnClickListener {
            forgotPassword()
        }
    }



    private fun initComponents() {
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        viewLogIn = findViewById(R.id.viewLogIn)
        viewRegister = findViewById(R.id.viewRegister)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginUser() {
        val user = etUser.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (user.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun LogInActivity.forgotPassword() {
        val email = etUser.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo para recuperar la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de recuperación enviado a $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}


