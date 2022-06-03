package com.example.roll.Util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.roll.MainActivity
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var signup: Button
    private lateinit var login: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var DBRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.emailEditTextSignup)
        password = findViewById(R.id.passwordEditTextSignUp)
        name = findViewById(R.id.nameEditTextSignUp)
        signup = findViewById(R.id.signupSignUp)
        login = findViewById(R.id.loginSignUp)

        login.setOnClickListener {
            finish()
            startActivity(Intent(this, Login::class.java))
        }

        signup.setOnClickListener {
            signupUser(name.text.toString(), email.text.toString(), password.text.toString())
        }
    }

    private fun signupUser(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name, email, mAuth.currentUser?.uid)
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, task.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String?) {
        DBRef = FirebaseDatabase.getInstance().reference
        DBRef.child("user").child(mAuth.currentUser?.uid!!).setValue(user(name, email, uid))
    }

}