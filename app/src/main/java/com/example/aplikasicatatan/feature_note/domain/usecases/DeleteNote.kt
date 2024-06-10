package com.example.aplikasicatatan.feature_note.domain.usecases

import com.example.aplikasicatatan.feature_note.domain.model.Note
import com.example.aplikasicatatan.feature_note.domain.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}