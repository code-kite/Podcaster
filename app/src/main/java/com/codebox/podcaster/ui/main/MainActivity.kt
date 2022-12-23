package com.codebox.podcaster.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.codebox.podcaster.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)



        setupGlobalObservers()

        val a = null
    }

    private fun setupGlobalObservers() {
        setupBottomNavObserver()
        setupToastObserver()
        setupSnackbarObserver()
    }

    private fun setupBottomNavObserver() {
        mainViewModel.bottomNavVisibilityData.observe(this, {
            when (it) {
                View.VISIBLE -> {
                    showBottomNav()
                }
                View.GONE, View.INVISIBLE -> {
                    hideBottomNav()
                }
            }
        })
    }

    private fun setupSnackbarObserver() {
        mainViewModel.snackbarMsgData.observe(this, {
            Snackbar.make(root, it.msg, Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun setupToastObserver() {
        mainViewModel.toastMessageData.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

}