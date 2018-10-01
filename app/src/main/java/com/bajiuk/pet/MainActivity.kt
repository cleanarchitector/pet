package com.bajiuk.pet

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bajiuk.pet.bash.view.BashActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, BashActivity::class.java))
        finish()
    }

}
