package com.nasser.storesapp.data.dao

import androidx.room.*
import com.nasser.storesapp.data.entity.Store

@Dao
interface StoreDao {

    //Obtener el listado de tiendas existentes
    @Query("SELECT * FROM store_table")
    fun getAllStores(): MutableList<Store>

    //Obetener una tienda por id
    @Query("SELECT * FROM store_table WHERE id = :id")
    fun getStoreById(id: Long): Store

    //Insertar o actualizar tienda
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateStore(store: Store): Long

    //Borrar una tienda
    @Delete
    fun deleteStore(store: Store)
}