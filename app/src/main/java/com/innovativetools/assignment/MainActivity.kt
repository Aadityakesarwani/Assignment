package com.innovativetools.assignment

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.innovativetools.assignment.databinding.ActivityMainBinding
import com.innovativetools.assignment.view.CampaignFragment
import com.innovativetools.assignment.view.CourseFragment
import com.innovativetools.assignment.view.DashboardFragment
import com.innovativetools.assignment.view.ProfileFragment
import com.innovativetools.assignment.viewmodel.CampaignViewModel
import com.innovativetools.assignment.viewmodel.CourseViewModel
import com.innovativetools.assignment.viewmodel.DashboardViewModel
import com.innovativetools.assignment.viewmodel.ProfileViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottom_nav: BottomNavigationView
    private lateinit var frag_title : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        frag_title = findViewById(R.id.tv_frag_title)

        setSupportActionBar(toolbar)
        bottom_nav = binding.bottomNav
        bottom_nav.background = null
        bottom_nav.menu.getItem(2).isEnabled = false
        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))

        val fab: FloatingActionButton = findViewById(R.id.floating_action)
        val tintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        fab.imageTintList = tintList


        val sharedPref = getSharedPreferences("KeyAccessToken", Context.MODE_PRIVATE)
        sharedPref?.edit()?.apply {
            putString("accessToken", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI")
            apply()
        }


        replaceFragment(DashboardFragment(),"Dashboard")
        bottom_nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_my_link -> {
                    replaceFragment(DashboardFragment(),"Dashboard")
                    true
                }
                R.id.navigation_courses -> {
                    replaceFragment(CourseFragment(),"Courses")
                    true
                }
                R.id.navigation_campaign -> {
                    replaceFragment(CampaignFragment(),"Campaigns")
                    true
                }
                R.id.navigation_my_profile -> {
                    replaceFragment(ProfileFragment(),"Profile")
                    true
                }
                else -> false
            }
        }

    }


    private fun setFragmentTitle(title: String) {
        frag_title.text = title
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_container, fragment)
            .commit()
        setFragmentTitle(title)
    }
}









