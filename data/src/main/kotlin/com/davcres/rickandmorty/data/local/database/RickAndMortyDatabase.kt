package com.davcres.rickandmorty.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davcres.rickandmorty.data.local.dao.CharacterDao
import com.davcres.rickandmorty.data.local.dao.SearchDao
import com.davcres.rickandmorty.data.local.model.CharacterEntity
import com.davcres.rickandmorty.data.local.model.SearchPageCharacterCrossRef
import com.davcres.rickandmorty.data.local.model.SearchPageEntity

@Database(
    entities = [
        CharacterEntity::class,
        SearchPageEntity::class,
        SearchPageCharacterCrossRef::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun searchDao(): SearchDao
}
