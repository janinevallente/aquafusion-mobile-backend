package com.aquafusion.crudtemplate_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aquafusion.crudtemplate_v2.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_dashboard)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch and display the user's name
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("farmerUsers")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val fullName = documentSnapshot.getString("fullName")
                        if (!fullName.isNullOrEmpty()) {
                            binding.fullNameTextView.text = "$fullName"
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors while fetching the data from Firestore
                    Toast.makeText(this@Dashboard, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        //logout
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
    }
    private fun showLogoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Session")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@Dashboard, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}