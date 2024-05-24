package com.crud_application

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

// Adapter class responsible for managing notes in a RecyclerView
class NotesAdapter(private var notes: List<NoteData>, context: Context) :
    RecyclerView.Adapter<NotesAdapter.NoteDataHolder>() {

    private val thisContext : Context = context

    // Database helpers for managing note data
    private val dataBaseSaveHelper : DataBaseHelper = DataBaseHelper(context, TableType.SAVED)
    private val dataBaseDeleteHelper : DataBaseHelper = DataBaseHelper(context, TableType.DELETED)

    // ViewHolder class for individual note items
    class NoteDataHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tittleText : TextView = itemView.findViewById(R.id.tittle_note_view)
        val content : TextView = itemView.findViewById(R.id.content_note_view)
        val creationDate : TextView = itemView.findViewById(R.id.creation_date_text)
        val noteField : CardView = itemView.findViewById(R.id.card_view)
        val deleteView : ImageView = itemView.findViewById(R.id.delete_button)
    }

    // Inflates the layout for a note item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteDataHolder {
        return NoteDataHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_view, parent, false)
        )
    }

    // Returns the total number of notes
    override fun getItemCount(): Int = notes.size

    // Binds data to the views in the ViewHolder
    override fun onBindViewHolder(holder: NoteDataHolder, position: Int) {
        val note = notes[position]

        holder.tittleText.text = note.title
        holder.content.text = note.content
        holder.creationDate.text = note.creationDate

        // Click listener for editing a note
        holder.noteField.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddNoteActivity::class.java).apply {
                putExtra("edit_mode", NoteEditMode.Update)
                putExtra("note_id", note.id)
            }

            holder.itemView.context.startActivity(intent)
        }

        // Click listener for deleting a note
        holder.deleteView.setOnClickListener {
            when(thisContext) {
                is MainActivity -> {
                    addAsLastElement(note)
                    dataBaseSaveHelper.deleteNote(note.id)

                    refreshData(dataBaseSaveHelper.getNotes())
                    Toast.makeText(holder.itemView.context, "Note moved to trashcan", Toast.LENGTH_SHORT).show()
                }

                is TrashcanActivity -> {
                    dataBaseDeleteHelper.deleteNote(note.id)

                    refreshData(dataBaseDeleteHelper.getNotes())
                    Toast.makeText(holder.itemView.context, "Note permanently deleted", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    // Filters notes by title and updates the RecyclerView
    fun filterByTitle(title: String) {
        notes = dataBaseSaveHelper.getNotes()

        val filteredNotes = if (title.isEmpty()) {
            notes
        } else {
            notes.filter { it.title.contains(title, ignoreCase = true) }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            refreshData(filteredNotes)
        }
    }

    // Adds a note to the deleted notes list
    private fun addAsLastElement(noteData : NoteData) {
        val notes = dataBaseDeleteHelper.getNotes().toMutableList()

        if(notes.size == dataBaseDeleteHelper.MAX_ITEM_COUNT) {
            dataBaseDeleteHelper.clearTable()
            notes.removeAt(0)

            for (item in notes) dataBaseDeleteHelper.addNoteData(item)
        }

        dataBaseDeleteHelper.addNoteData(noteData)
    }

    // Refreshes the list of notes and notifies the adapter
    fun refreshData(newNotes : List<NoteData>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}