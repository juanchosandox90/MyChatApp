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
import com.google.firebase.database.FirebaseDatabase
import com.juansandoval.mychatapp.R
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDataBase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        mAuth = FirebaseAuth.getInstance()
        createActButtonId.setOnClickListener {
            validateFields()
        }
    }

    private fun createAccount(email: String, password: String, displayName: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    var currentUserId = mAuth!!.currentUser
                    var userId = currentUserId!!.uid

                    mDataBase = FirebaseDatabase.getInstance().reference
                        .child("Users").child(userId)

                    // Object User to put in the DB
                    var userObject = HashMap<String, String>()
                    userObject.put("display_name", displayName)
                    userObject.put("status", "Hello There")
                    userObject.put("image", "default")
                    userObject.put("thumb_image", "default")

                    mDataBase!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            var dashboardIntent = Intent(this, DashboardActivity::class.java)
                            dashboardIntent.putExtra("name", displayName)
                            startActivity(dashboardIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "User Not Created \uD83D\uDE2D", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Something Went Wrong! \ud83d\ude2d", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun validateFields() {
        var email = createActEmailEt.text.toString().trim()
        var password = createActPasswordEt.text.toString().trim()
        var displayName = createActNameEt.text.toString().trim()
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(
                displayName
            )
        ) {
            createAccount(email, password, displayName)
        } else {
            Toast.makeText(this, "Please Enter All Fields!", Toast.LENGTH_LONG).show()
        }
    }
}
