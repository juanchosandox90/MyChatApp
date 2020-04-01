package com.juansandoval.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
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
}
