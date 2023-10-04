package com.aquafusion.crudtemplate_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.aquafusion.crudtemplate_v2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@MainActivity, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val emailAddress: String = binding.emailAddressText.text.toString().trim()
            val password: String = binding.passwordText.text.toString().trim()

            if (emailAddress.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                if (password.isNotEmpty()) {
                    // Sign in the user using Firebase Authentication
                    auth.signInWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(this) { authTask ->
                            if (authTask.isSuccessful) {
                                // User successfully logged in
                                Toast.makeText(this@MainActivity, "Login Successful.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@MainActivity, Dashboard::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign-in fails, display a message to the user.
                                Toast.makeText(this@MainActivity, "Login Failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    binding.passwordText.error = "Please enter your password"
                }
            } else if (emailAddress.isEmpty()) {
                binding.emailAddressText.error = "Please enter your email address."
            } else {
                binding.emailAddressText.error = "Please enter a valid email address."
            }
        }

        // Register button click event
        binding.createAccountButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}