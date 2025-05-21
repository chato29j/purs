package com.example.finalclockapplication.Register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalclockapplication.LogInActivity
import com.example.finalclockapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName:EditText
    private lateinit var etPassword_1:EditText
    private lateinit var etPassword_2:EditText
    private lateinit var etPhone:EditText
    private lateinit var etMail:EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnBackToLogin: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        initComponents()
        initListeners()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun initListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }

        btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun registerUser() {
        val name = etName.text.toString().trim()
        val password_1 = etPassword_1.text.toString().trim()
        val password_2 = etPassword_2.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val mail = etMail.text.toString().trim()


        if (name.isEmpty() || password_1.isEmpty() || password_2.isEmpty() || phone.isEmpty() || mail.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,}$"))) {
            Toast.makeText(this, "Nombre inválido. Usa solo letras y mínimo 3 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.length < 10) {
            Toast.makeText(this, "El número debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password_1 != password_2) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (password_1.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // MOSTRAR PROGRESSBAR
        progressBar.visibility = View.VISIBLE

        // CREAR USUARIO
        auth.createUserWithEmailAndPassword(mail, password_1)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val user = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to mail
                    )

                    uid?.let {
                        db.collection("usuarios").document(uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LogInActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    val error = task.exception?.message
                    if (error?.contains("email address is already in use") == true) {
                        Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error al registrar: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }
}