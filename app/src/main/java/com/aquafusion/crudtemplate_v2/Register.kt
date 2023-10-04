package com.aquafusion.crudtemplate_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aquafusion.crudtemplate_v2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            val workGroupId: String = binding.workgroupIdText.text.toString().trim()
            val fullName: String = binding.fullNameText.text.toString().trim()
            val emailAddress: String = binding.EmailAddressText.text.toString().trim()
            val password: String = binding.PasswordText.text.toString().trim()
            val confirmPassword: String = binding.confirmPasswordText.text.toString().trim()

            if (workGroupId.isEmpty()) {
                Toast.makeText(this@Register, "Please enter your workgroup ID.", Toast.LENGTH_SHORT).show()
            } else if (fullName.isEmpty()) {
                Toast.makeText(this@Register, "Please enter your full name.", Toast.LENGTH_SHORT).show()
            } else if (emailAddress.isEmpty()) {
                Toast.makeText(this@Register, "Please enter your email.", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this@Register, "Please enter your password.", Toast.LENGTH_SHORT).show()
            } else if (confirmPassword.isEmpty()) {
                Toast.makeText(this@Register, "Please confirm your password", Toast.LENGTH_SHORT).show()
            } else if (confirmPassword != password) {
                Toast.makeText(this@Register, "Password doesn't match", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = auth.currentUser
                            val uid = user?.uid

                            // Create a user document in Firestore
                            val farmerUserMap = hashMapOf(
                                "workgroupId" to workGroupId,
                                "fullName" to fullName,
                                "email" to emailAddress,
                                "password" to password
                            )

                            if (uid != null) {
                                firestore.collection("farmerUsers")
                                    .document(uid)
                                    .set(farmerUserMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@Register, "Farmer registered successfully.", Toast.LENGTH_SHORT).show()
                                        // Clear input fields
                                        binding.workgroupIdText.text.clear()
                                        binding.fullNameText.text.clear()
                                        binding.EmailAddressText.text.clear()
                                        binding.PasswordText.text.clear()
                                        binding.confirmPasswordText.text.clear()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@Register, "Farmer registration failed.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            // Registration failed
                            Toast.makeText(this@Register, "Registration Failed: ${task.exception?.message ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }

            }

            binding.backToLoginButton.setOnClickListener {
                val intent = Intent(this@Register, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}