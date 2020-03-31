package com.juansandoval.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.juansandoval.mychatapp.R

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        if (intent.extras != null) {
            var username = intent!!.extras!!.get("name")
            Toast.makeText(this, username.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
