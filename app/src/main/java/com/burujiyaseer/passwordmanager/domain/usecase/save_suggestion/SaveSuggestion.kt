package com.burujiyaseer.passwordmanager.domain.usecase.save_suggestion

interface SaveSuggestion {
    suspend operator fun invoke(text: String)
    suspend fun deleteSuggestion(text: String)
}