package com.juansandoval.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.juansandoval.mychatapp.R
import com.juansandoval.mychatapp.models.FriendlyMessage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    var userId: String? = null
    var chatName: String? = null
    var mFirebaseDatabaseRef: DatabaseReference? = null
    var mFirebaseUser: FirebaseUser? = null

    var mLinearLayoutManager: LinearLayoutManager? = null
    var mFirebaseAdapter: FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mFirebaseUser = FirebaseAuth.getInstance().currentUser

        userId = intent.extras!!.getString("userId")
        chatName = intent.extras!!.getString("userName")

        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager!!.stackFromEnd = true

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.title = chatName


        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference

        mFirebaseAdapter = object : FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
            FriendlyMessage::class.java,
            R.layout.chat_item_message,
            MessageViewHolder::class.java,
            mFirebaseDatabaseRef!!.child("messages")
        ) {

            override fun populateViewHolder(
                viewHolder: MessageViewHolder?,
                friendlyMessage: FriendlyMessage?,
                position: Int
            ) {

                if (friendlyMessage!!.text != null) {
                    viewHolder!!.bindView(friendlyMessage)

                    var currentUserId = mFirebaseUser!!.uid

                    var isMe: Boolean = friendlyMessage!!.id!!.equals(currentUserId)

                    if (isMe) {
                        //Me to the right side
                        viewHolder.profileImageViewRight!!.visibility = View.VISIBLE
                        viewHolder.profileImageView!!.visibility = View.GONE
                        viewHolder.messageTextView!!.gravity =
                            (Gravity.CENTER_VERTICAL or Gravity.RIGHT)
                        viewHolder.messengerTextView!!.gravity =
                            (Gravity.CENTER_VERTICAL or Gravity.RIGHT)


                        //Get imageUrl for me!
                        mFirebaseDatabaseRef!!.child("Users")
                            .child(currentUserId)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(dbError: DatabaseError) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.something_went_wrong),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    var imageUrl = data!!.child("thumb_image").value.toString()
                                    var displayName = data!!.child("display_name").value

                                    viewHolder.messengerTextView!!.text =
                                        getString(R.string.i_wrote_hint)

                                    Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile_img)
                                        .into(viewHolder.profileImageViewRight)
                                }

                            })

                    } else {
                        //the other person show imageview to the left side

                        viewHolder.profileImageViewRight!!.visibility = View.GONE
                        viewHolder.profileImageView!!.visibility = View.VISIBLE
                        viewHolder.messageTextView!!.gravity =
                            (Gravity.CENTER_VERTICAL or Gravity.LEFT)
                        viewHolder.messengerTextView!!.gravity =
                            (Gravity.CENTER_VERTICAL or Gravity.LEFT)


                        //Get imageUrl for me!
                        mFirebaseDatabaseRef!!.child("Users")
                            .child(userId!!)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(dbError: DatabaseError) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.something_went_wrong),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                                override fun onDataChange(data: DataSnapshot) {
                                    var imageUrl = data!!.child("thumb_image").value.toString()
                                    var displayName = data!!.child("display_name").value.toString()

                                    viewHolder.messengerTextView!!.text =
                                        "$displayName ${getString(R.string.other_wrote_hint)}"

                                    Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile_img)
                                        .into(viewHolder.profileImageView)
                                }

                            })


                    }

                }
            }

        }

        // Set the RecyclerView
        messageRecyclerView.layoutManager = mLinearLayoutManager
        messageRecyclerView.adapter = mFirebaseAdapter


        sendButton.setOnClickListener {
            if (!messageEdt.text.isNullOrEmpty()) {
                if (intent!!.extras!!.get("userName").toString() != "") {
                    var currentUsername = intent!!.extras!!.get("userName")
                    var mCurrentUserId = mFirebaseUser!!.uid


                    var friendlyMessage = FriendlyMessage(
                        mCurrentUserId,
                        messageEdt.text.toString().trim(),
                        currentUsername.toString().trim()
                    )

                    mFirebaseDatabaseRef!!.child("messages")
                        .push().setValue(friendlyMessage)

                    messageEdt.setText("")


                }
            } else {
                Toast.makeText(this, getString(R.string.message_text_empty), Toast.LENGTH_LONG)
                    .show()
            }

        }


    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTextView: TextView? = null
        var messengerTextView: TextView? = null
        var profileImageView: CircleImageView? = null
        var profileImageViewRight: CircleImageView? = null

        fun bindView(friendlyMessage: FriendlyMessage) {

            messageTextView = itemView.findViewById(R.id.messageTextview)
            messengerTextView = itemView.findViewById(R.id.messengerTextview)
            profileImageView = itemView.findViewById(R.id.messengerImageView)
            profileImageViewRight = itemView.findViewById(R.id.messengerImageViewRight)

            messengerTextView!!.text = friendlyMessage.name
            messageTextView!!.text = friendlyMessage.text

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
