package com.juansandoval.mychatapp.adapters

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.juansandoval.mychatapp.R
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
        viewHolder.itemView.setOnClickListener {
            // TODO: Create a pop up dialog where user can chose to send message or see profile
            Toast.makeText(context, "User Id: $userId", Toast.LENGTH_LONG).show()
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