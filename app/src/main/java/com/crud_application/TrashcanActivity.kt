package com.crud_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.crud_application.databinding.ActivityTrashcanBinding

// Activity responsible for displaying deleted notes
class TrashcanActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTrashcanBinding
    private lateinit var db : DataBaseHelper
    private lateinit var notesAdapter : NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityTrashcanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper and notes adapter
        db = DataBaseHelper(this, TableType.DELETED)
        notesAdapter = NotesAdapter(db.getNotes(), this)

        // Set up RecyclerView with LinearLayoutManager and adapter
        binding.noteHolder.layoutManager = LinearLayoutManager(this)
        binding.noteHolder.adapter = notesAdapter

        // Set click listener for back button to return to MainActivity
        binding.backToMenu.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}