package com.davcres.rickandmorty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davcres.rickandmorty.data.local.model.CharacterEntity
import com.davcres.rickandmorty.data.local.model.SearchPageCharacterCrossRef
import com.davcres.rickandmorty.data.local.model.SearchPageEntity

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_pages WHERE `query` = :query AND page = :page")
    suspend fun getSearchPage(query: String, page: Int): SearchPageEntity?

    @Query(
        """
        SELECT c.* FROM characters c
        INNER JOIN search_page_character_cross_ref ref ON c.id = ref.characterId
        WHERE ref.`query` = :query AND ref.page = :page
        ORDER BY ref.position ASC
        """,
    )
    suspend fun getCharactersForPage(query: String, page: Int): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchPage(searchPage: SearchPageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<SearchPageCharacterCrossRef>)

    @Query("DELETE FROM search_page_character_cross_ref WHERE `query` = :query AND page = :page")
    suspend fun deleteCrossRefsForPage(query: String, page: Int)

    @Query("DELETE FROM search_pages WHERE `query` = :query AND page = :page")
    suspend fun deleteSearchPage(query: String, page: Int)

    @Query("DELETE FROM search_pages")
    suspend fun clearAllSearchPages()
}
