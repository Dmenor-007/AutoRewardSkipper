package com.example.autorewards

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {

    private lateinit var editKeywords: EditText
    private val defaultKeywords = listOf("ganhou", "moedas", "recompensa")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        editKeywords = findViewById(R.id.editKeywords)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val resetBtn = findViewById<Button>(R.id.resetBtn)

        loadKeywords()

        saveBtn.setOnClickListener {
            val keywords = editKeywords.text.toString()
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            getSharedPreferences("config", Context.MODE_PRIVATE)
                .edit()
                .putStringSet("keywords", keywords.toSet())
                .apply()

            Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show()
        }

        resetBtn.setOnClickListener {
            editKeywords.setText(defaultKeywords.joinToString("
"))
        }
    }

    private fun loadKeywords() {
        val prefs = getSharedPreferences("config", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("keywords", defaultKeywords.toSet())
        editKeywords.setText(saved?.joinToString("\n"))
    }
}