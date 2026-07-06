package com.demo.customrating

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.custom.ratingdialog.CustomRatingDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.show_rating_button).setOnClickListener {
            CustomRatingDialog.Builder(this)
                .title("Enjoying the app?")
                .message("Tap a star to rate your experience")
                .onRatingSubmitted { rating ->
                    Toast.makeText(this, "You rated: $rating", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }
}