package com.juansandoval.mychatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.juansandoval.mychatapp.R
import com.juansandoval.mychatapp.adapters.SectionPagerAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    var sectionPagerAdapter: SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar!!.title = getString(R.string.app_name)

        sectionPagerAdapter = SectionPagerAdapter(this)
        dashboardViewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(mainTabs, dashboardViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Users"
                    }
                    1 -> {
                        tab.text = "Chats"
                    }
                }
            }).attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                logoutDialog()
            }
            R.id.settings_action -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        return true
    }

    override fun onBackPressed() {
        logoutDialog()
    }

    private fun logoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        val dialogLayout =
            LayoutInflater.from(this).inflate(R.layout.activity_dialog_logout, null)
        val logoutBtn = dialogLayout.findViewById<Button>(R.id.logoutBtn)
        val cancelBtn = dialogLayout.findViewById<Button>(R.id.cancelBtn)
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setView(dialogLayout)
        dialog.setCancelable(true)
        dialog.show()
    }
}
