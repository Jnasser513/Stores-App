package com.nasser.storesapp.data.dao

import androidx.room.*
import com.nasser.storesapp.data.entities.Store

@Dao
interface StoreDao {

    //Obtener el listado de tiendas existentes
    @Query("SELECT * FROM store_table")
    fun getAllStores(): MutableList<Store>

    //Insertar o actualizar tienda
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateStore(store: Store)

    //Borrar una tienda
    @Delete
    fun deleteStore(store: Store)

    //Obtener una tienda
    @Query("SELECT * FROM store_table WHERE id = :query")
    suspend fun searchUser(query: Long): Store
}