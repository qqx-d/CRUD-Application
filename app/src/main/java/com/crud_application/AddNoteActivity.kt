package com.crud_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.crud_application.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddNoteBinding
    private lateinit var dataBaseHelper : DataBaseHelper

    private var mode : NoteEditMode = NoteEditMode.None
    private var noteID : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper
        dataBaseHelper = DataBaseHelper(this, TableType.SAVED)

        // Get the mode (Add or Update) from the intent
        mode = intent.getSerializableExtra("edit_mode") as? NoteEditMode ?: NoteEditMode.None

        // If the mode is Update, retrieve the note ID and populate the UI with existing note data
        if (mode == NoteEditMode.Update) {
            noteID = intent.getIntExtra("note_id", -1)

            // If note ID is invalid, finish the activity
            if (noteID == -1) {
                finish()
                return
            }

            // Fetch note data by ID and populate UI fields
            val note = dataBaseHelper.getNoteDataByID(noteID)
            binding.tittleText.setText(note.title)
            binding.contentText.setText(note.content)
        }

        // Bind click listeners to buttons
        bindButtons()
    }

    // Function to bind click listeners to buttons
    private fun bindButtons() {
        // Save button click listener
        binding.saveNote.setOnClickListener {
            // Get text from input fields
            val tittle = binding.tittleText.text.toString()
            val content = binding.contentText.text.toString()
            val creationDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time ).toString()

            // Create a NoteData object with input values
            val noteData = NoteData(
                if(noteID == -1) 0 else noteID, // If noteID is invalid (-1), set it to new
                tittle,
                content,
                creationDate
            )

            // Perform action based on mode (Add or Update)
            when(mode) {
                NoteEditMode.Add -> {
                    // Add new note to database
                    dataBaseHelper.addNoteData(noteData)
                    Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show()
                }

                NoteEditMode.Update -> {
                    // Update existing note in the database
                    dataBaseHelper.updateNote(noteData)
                    Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Handle other error cases
                    Toast.makeText(this, "There's some error issue", Toast.LENGTH_SHORT).show()
                }
            }

            // Finish the activity
            finish()
        }

        // Exit button click listener
        binding.exitNote.setOnClickListener{
            // Navigate back to MainActivity
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}