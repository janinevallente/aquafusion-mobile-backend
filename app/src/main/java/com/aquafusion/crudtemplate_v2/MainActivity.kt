package com.aquafusion.crudtemplate_v2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aquafusion.crudtemplate_v2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

// KTor backend HTTP client

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    // Start KTor
    @Serializable
    private data class Farmer(
        val accountId: String,
        val emailAddress: String,
        val password: String,
        val fullName: String,
        val workgroupId: String
    );

    @Serializable
    private data class FarmerLoginCredentials(
        val emailAddress: String,
        val password: String
    );

    // client object
    private val client = HttpClient(CIO) {
        install(UserAgent) {
            agent = "Ktor client"
        }
    }

    // call firebase login
    private suspend fun callFirebaseLogin(emailAddress: String, password: String): Boolean {
        try {
            val json = Gson().toJson(FarmerLoginCredentials(emailAddress, password))

            val response: HttpResponse = client.post("https://us-central1-aquafusion-b8744.cloudfunctions.net/api/farmer/login") {
                contentType(ContentType.Application.Json)
                setBody(json);
            }
            // Handle the response accordingly
            Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_LONG).show();
            val responseData = response.bodyAsText();
            Log.d("API Response", responseData);
            Log.d("client:", response.isActive.toString());

            return true;
        } catch (e: Exception) {
            // Handle the error
            Log.e("API Error", "Error: ${e.localizedMessage}")
        } finally {
            client.close()
        }
        return false
    }

    // End KTor

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

    private fun login() {
        val emailAddress: String = binding.emailAddressText.text.toString().trim()
        val password: String = binding.passwordText.text.toString().trim()

        if (emailAddress.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            if (password.isNotEmpty()) {
                // Call the login function with provided email and password
                lifecycleScope.launch(Dispatchers.Main) {
                    callFirebaseLogin(emailAddress, password);
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            // Launch a coroutine to call login()
            login()
        }

        // Register button click event
        binding.createAccountButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}