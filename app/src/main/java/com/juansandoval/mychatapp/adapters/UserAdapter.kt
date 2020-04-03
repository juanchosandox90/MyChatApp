package com.juansandoval.mychatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.juansandoval.mychatapp.R
import com.juansandoval.mychatapp.activities.ChatActivity
import com.juansandoval.mychatapp.activities.ProfileActivity
import com.juansandoval.mychatapp.models.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_row_item.view.*

class UserAdapter(databaseQuery: DatabaseReference, var context: Context?) :
    FirebaseRecyclerAdapter<Users, UserAdapter.ViewHolder>(
        Users::class.java,
        R.layout.user_row_item,
        ViewHolder::class.java,
        databaseQuery
    ) {
    override fun populateViewHolder(viewHolder: ViewHolder?, users: Users?, position: Int) {
        var userId = getRef(position).key
        viewHolder!!.bindView(users!!, context!!)
        var userName = viewHolder.userNameText
        var status = viewHolder.userStatusText
        var profilePic = viewHolder.userProfileImageTxt
        viewHolder.itemView.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
            val dialog: androidx.appcompat.app.AlertDialog = builder.create()
            val dialogLayout =
                LayoutInflater.from(context).inflate(R.layout.activity_dialog_tap_user, null)
            val openProfile = dialogLayout.findViewById<Button>(R.id.openProfileId)
            val sendMessage = dialogLayout.findViewById<Button>(R.id.sendMessageId)
            openProfile.setOnClickListener {
                var profileIntent = Intent(context, ProfileActivity::class.java)
                profileIntent.putExtra("userId", userId)
                context!!.startActivity(profileIntent)
                dialog.dismiss()
            }
            sendMessage.setOnClickListener {
                var chatIntent = Intent(context, ChatActivity::class.java)
                chatIntent.putExtra("userId", userId)
                chatIntent.putExtra("userName", userName)
                chatIntent.putExtra("userStatus", status)
                chatIntent.putExtra("profilePic", profilePic)
                context!!.startActivity(chatIntent)
                dialog.dismiss()
            }
            dialog.setView(dialogLayout)
            dialog.setCancelable(true)
            dialog.show()
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameText: String? = null
        var userStatusText: String? = null
        var userProfileImageTxt: String? = null

        fun bindView(users: Users, context: Context) {

            var userName = itemView.profileDisplaName
            var userStatus = itemView.profileStatus
            var userProfileImage = itemView.userProfileImg

            userNameText = users.display_name
            userStatusText = users.status
            userProfileImageTxt = users.thumb_image

            userName.text = users.display_name
            userStatus.text = users.status

            Picasso.get()
                .load(userProfileImageTxt)
                .placeholder(R.drawable.profile_img)
                .into(userProfileImage)
        }
    }
}