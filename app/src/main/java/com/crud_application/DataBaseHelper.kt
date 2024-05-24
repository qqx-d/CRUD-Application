package com.crud_application

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context : Context, tableType : TableType) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        private const val DATABASE_NAME = "notes_data.db"
        private const val TABLE_NAME_SAVED = "saved_notes"
        private const val TABLE_NAME_DELETED = "deleted_notes"
        private const val COLUMN_ID =  "id"
        private const val COLUMN_TITLE =  "title"
        private const val COLUMN_CONTENT =  "content"
        private const val COLUMN_CREATION_DATE =  "date"
    }

    public val MAX_ITEM_COUNT = 6

    private val TABLE_NAME : String = if(tableType == TableType.SAVED) TABLE_NAME_SAVED else TABLE_NAME_DELETED

    override fun onCreate(db: SQLiteDatabase?) {
        // Create tables for SAVED and DELETED notes
        val createTableQueryForSAVED = "CREATE TABLE ${TABLE_NAME_SAVED} ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_CREATION_DATE TEXT)"
        val createTableQueryForDELETED = "CREATE TABLE ${TABLE_NAME_DELETED} ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_CREATION_DATE TEXT)"
        db?.execSQL(createTableQueryForSAVED)
        db?.execSQL(createTableQueryForDELETED)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop existing tables if they exist and recreate them
        val dropTableQuery = "DROP TABLE IF EXISTS ${TABLE_NAME}"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun getNotes() : List<NoteData> {
        // Retrieve notes from the database
        val db = readableDatabase
        val query = "SELECT * FROM ${TABLE_NAME}"
        val cursor = db.rawQuery(query, null )

        val notesList = mutableListOf<NoteData>()
        while(cursor.moveToNext()) {
            // Iterate through cursor and add note data to the list
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE))

            val noteData = NoteData(id, title, content, creationDate)
            notesList.add(noteData)
        }
        cursor.close()
        db.close()

        return notesList
    }

    fun clearTable() {
        // Clear the entire table
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    fun getNoteDataByID(noteID : Int) : NoteData {
        // Retrieve note data by its ID
        val db = readableDatabase
        val query = "SELECT * FROM ${TABLE_NAME} WHERE $COLUMN_ID = $noteID"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE))

        cursor.close()
        db.close()

        return NoteData(id, title, content, creationDate)
    }

    fun addNoteData(noteData : NoteData) {
        // Add a new note to the database
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, noteData.title)
            put(COLUMN_CONTENT, noteData.content)
            put(COLUMN_CREATION_DATE, noteData.creationDate)
        }
        db?.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateNote(noteData : NoteData) {
        // Update an existing note in the database
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, noteData.title)
            put(COLUMN_CONTENT, noteData.content)
            put(COLUMN_CREATION_DATE, noteData.creationDate)
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteData.id.toString())

        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteNote(noteID : Int) {
        // Delete a note from the database by its ID
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteID.toString())

        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
