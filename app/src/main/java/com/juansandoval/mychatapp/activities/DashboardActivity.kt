package com.juansandoval.mychatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
        supportActionBar!!.title = "Dashboard"

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

        if (intent.extras != null) {
            var username = intent!!.extras!!.get("name")
            Toast.makeText(this, username.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.settings_action -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        return true
    }
}
