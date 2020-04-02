package com.juansandoval.mychatapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.juansandoval.mychatapp.R
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    private var mStorageRef: StorageReference? = null
    private var userStatus: Any? = null
    private var GALLERY_ID: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        var userId = mCurrentUser!!.uid

        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var displayName = dataSnapshot.child("display_name").value
                var imageProfile = dataSnapshot.child("image").value.toString()
                userStatus = dataSnapshot.child("status").value
                var userThumbNail = dataSnapshot.child("thumb_image").value

                settingsStatusText.text = userStatus.toString()
                settingsDisplayName.text = displayName.toString()
                if (!imageProfile!!.equals("default")) {
                    Picasso
                        .get()
                        .load(imageProfile)
                        .placeholder(R.drawable.profile_img)
                        .into(settingsProfileId)
                }
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

        settingsChangeStatusBtn.setOnClickListener {
            dialogStatusChange()
        }

        settingsImgBtn.setOnClickListener {
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    galleryIntent,
                    getString(R.string.select_image_title)
                ), GALLERY_ID
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateDBReferences()
    }

    private fun updateDBReferences() {
        var userId = mCurrentUser!!.uid
        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var displayName = dataSnapshot.child("display_name").value
                var imageProfile = dataSnapshot.child("image").value.toString()
                userStatus = dataSnapshot.child("status").value
                var userThumbNail = dataSnapshot.child("thumb_image").value

                settingsStatusText.text = userStatus.toString()
                settingsDisplayName.text = displayName.toString()
                if (!imageProfile!!.equals("default")) {
                    Picasso
                        .get()
                        .load(imageProfile)
                        .placeholder(R.drawable.profile_img)
                        .into(settingsProfileId)
                }
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

    private fun dialogStatusChange() {
        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.create()
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_dialog_status, null)
        val statusUpdateEt = dialogLayout.findViewById<EditText>(R.id.statusUpdateEt)
        val statusUpdateBtn = dialogLayout.findViewById<Button>(R.id.statusUpdateBtn)
        if (userStatus != null) {
            statusUpdateEt.setText(userStatus.toString())
        } else if (userStatus == null) {
            statusUpdateEt.setText(getString(R.string.enter_your_status_title))
        }
        statusUpdateBtn.setOnClickListener {
            var status = statusUpdateEt.text.toString().trim()
            mDataBase!!.child("status").setValue(status)
                .addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                }
        }
        dialog.setView(dialogLayout)
        dialog.setCancelable(true)
        dialog.show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {
            var image: Uri? = dataIntent!!.data
            CropImage.activity(image)
                .setAspectRatio(1, 1)
                .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(dataIntent)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var userId = mCurrentUser!!.uid
                var thumbFile = File(resultUri.path)

                var thumbBitMap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(65)
                    .compressToBitmap(thumbFile)

                var byteArray = ByteArrayOutputStream()
                thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                var thumbByteArray: ByteArray = byteArray.toByteArray()

                // Images directory
                var filePath = mStorageRef!!
                    .child("chat_profile_images")
                    .child("$userId.jpg")

                // Compressed images - thumbnail
                var thumbFilePath = mStorageRef!!
                    .child("chat_profile_images")
                    .child("thumbs").child("$userId.jpg")

                filePath.putFile(resultUri)
                    .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                        if (task.isSuccessful) {
                            var downloadUrl = ""
                            var downloadThumbUrl = ""
                            var downloadUri: Task<Uri> = task.result!!.storage.downloadUrl
                            downloadUri.addOnSuccessListener { uri: Uri? ->
                                downloadUrl = uri.toString()
                                downloadThumbUrl = uri.toString()
                            }
                            var uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)
                            uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                if (task.isSuccessful) {
                                    var updateObj = HashMap<String, Any>()
                                    updateObj["image"] = downloadUrl
                                    updateObj["thumb_image"] = downloadThumbUrl

                                    mDataBase!!.updateChildren(updateObj)
                                        .addOnCompleteListener { task: Task<Void> ->
                                            if (task.isSuccessful) {
                                                // Should show a Dialog confirmed
                                                Toast.makeText(
                                                    this,
                                                    "Profile Image Saved",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    getString(R.string.something_went_wrong),
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.something_went_wrong),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
            }
        }
    }
}
