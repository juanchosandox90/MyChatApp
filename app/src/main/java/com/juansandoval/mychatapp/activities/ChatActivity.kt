package com.juansandoval.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.juansandoval.mychatapp.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.title = "Chat"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
