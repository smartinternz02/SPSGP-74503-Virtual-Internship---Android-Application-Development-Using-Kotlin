package com.himanshu.android.view.ui.home

import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.himanshu.android.R
import com.himanshu.android.databinding.ActivityHomeBinding
import com.himanshu.android.view.ui.auth.viewmodel.AuthViewModel
import com.himanshu.android.view.ui.base.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val mainActivityViewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    private var isHomeSelected = true

    override fun onStart() {
        super.onStart()
        authViewModel.saveIdToken()
        authViewModel.getLocalIdToken()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(authViewModel.broadcastReceiver, intentFilter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.itemIconTintList = null
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.findNavController()
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener {
            if (it.itemId == R.id.homeFragment) {
                if (isHomeSelected && mainActivityViewModel.isHomeScrolled.value == true) {
                    mainActivityViewModel.shouldBackToTop.postValue(true)
                }
            }
        }

        destinationListener()
        observeIsHomeScrolled()

    }

    private fun destinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                isHomeSelected = true
            }
            when (destination.id) {
                R.id.homeFragment, R.id.browseFragment, R.id.ordersFragment, R.id.accountFragment -> {
                    mainActivityViewModel.isBottomBarVisible = true
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    isHomeSelected = false
                    mainActivityViewModel.isBottomBarVisible = false
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun observeIsHomeScrolled() {
        mainActivityViewModel.isHomeScrolled.observe(this) {
            if (!isHomeSelected) {
                return@observe
            }

            val homeItem = bottomNavigationView.menu.getItem(0)
            homeItem.icon =
                ContextCompat.getDrawable(
                    this,
                    if (it) R.drawable.home_scrolled_selector else R.drawable.home_selector
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(authViewModel.broadcastReceiver)
    }
}