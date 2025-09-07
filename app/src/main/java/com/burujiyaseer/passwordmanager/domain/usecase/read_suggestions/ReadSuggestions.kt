package com.burujiyaseer.passwordmanager.domain.usecase.read_suggestions

import kotlinx.coroutines.flow.Flow

interface ReadSuggestions {
    operator fun invoke(): Flow<Set<String>>
}