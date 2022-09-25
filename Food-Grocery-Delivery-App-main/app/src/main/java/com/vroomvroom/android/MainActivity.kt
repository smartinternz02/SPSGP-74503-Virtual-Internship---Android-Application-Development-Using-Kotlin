package com.himanshu.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.himanshu.android.view.ui.home.HomeActivity
import com.himanshu.android.utils.Utils.startNewActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.Theme_GroceriesApp)
        startNewActivity(HomeActivity::class.java)

    }
}