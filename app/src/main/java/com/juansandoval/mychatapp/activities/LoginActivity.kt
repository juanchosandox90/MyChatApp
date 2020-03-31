package com.juansandoval.mychatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.juansandoval.mychatapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDataBase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        loginButtonId.setOnClickListener {
            validateFields()
        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    var username = email.split("@")[0]
                    var dashboardIntent = Intent(this, DashboardActivity::class.java)
                    dashboardIntent.putExtra("name", username)
                    startActivity(dashboardIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Something Went Wrong! \ud83d\ude2d", Toast.LENGTH_LONG)
                        .show()
                }

            }
    }

    private fun validateFields() {
        var email = loginEmailEt.text.toString().trim()
        var password = loginPswdEt.text.toString().trim()
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
            loginUser(email, password)
        } else {
            Toast.makeText(this, "Please Enter All Fields!", Toast.LENGTH_LONG).show()
        }
    }
}
