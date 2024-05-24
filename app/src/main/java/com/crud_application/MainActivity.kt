package com.crud_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.crud_application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DataBaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DataBaseHelper(this, TableType.SAVED)
        notesAdapter = NotesAdapter(db.getNotes(), this)

        binding.noteHolder.layoutManager = LinearLayoutManager(this)
        binding.noteHolder.adapter = notesAdapter

        binding.addNote.setOnClickListener {
            startActivity(
                Intent(this, AddNoteActivity::class.java).apply {
                    putExtra("edit_mode", NoteEditMode.Add)
                }
            )
        }

        binding.toTrashcan.setOnClickListener {
            startActivity(
                Intent(this, TrashcanActivity::class.java)
            )
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (searchText.isEmpty() && before > 0) {
                    notesAdapter.filterByTitle("")
                } else {
                    notesAdapter.filterByTitle(searchText)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.clearSearchBar.setOnClickListener {
            binding.searchBar.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()

        notesAdapter.refreshData(db.getNotes())
    }
}