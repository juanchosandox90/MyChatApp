package com.juansandoval.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.juansandoval.mychatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var mCurrentUser: FirebaseUser? = null
    private var mDataBase: DatabaseReference? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.title = getString(R.string.profile_title_activity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.extras != null) {
            userId = intent!!.extras!!.get("userId").toString()
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
            setupProfile()
        }
    }

    private fun setupProfile() {
        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var displayName = dataSnapshot.child("display_name").value.toString()
                var status = dataSnapshot.child("status").value.toString()
                var image = dataSnapshot.child("image").value.toString()

                profileDisplayNameTxt.text = displayName
                profileStatusTxt.text = status

                Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.profile_img)
                    .into(profileImageIv)
            }

            override fun onCancelled(dbError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
