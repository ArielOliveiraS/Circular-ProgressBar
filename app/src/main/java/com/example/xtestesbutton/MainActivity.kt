package com.example.xtestesbutton

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
     var clicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botao.setOnClickListener { botaoClick() }
    }

    fun botaoClick(){
        clicked = true

        if (clicked) {
            botao.showLoading()
        } else {
           // botao.hideLoading()
        }
    }
}
