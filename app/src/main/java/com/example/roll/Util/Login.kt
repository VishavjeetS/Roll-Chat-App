package com.example.roll.Util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.roll.MainActivity
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var login:Button
    private lateinit var signup:TextView
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        login = findViewById(R.id.login)
        signup = findViewById(R.id.signup)

        login.setOnClickListener {
            loginUser(email.text.toString(), password.text.toString())
        }
        signup.setOnClickListener {
            finish()
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if(p0.currentUser != null){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onStart() {
        if(mAuth.currentUser != null){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
        super.onStart()
    }
}