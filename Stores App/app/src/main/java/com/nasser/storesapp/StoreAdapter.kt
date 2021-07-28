package com.nasser.storesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasser.storesapp.data.entity.Store
import com.nasser.storesapp.databinding.ItemStoreBinding

class StoreAdapter(private var storeList: MutableList<Store>, private var listener: OnClickListener):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    //Creacion de la vista para mostrar el item_store
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

    //Visualizacion de las tiendas existentes en la base de datos
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = storeList.get(position)

        with(holder) {
            setListener(store)

            binding.textViewTittle.text = store.name
            binding.checkboxFavorite.isChecked = store.isFavorite
        }
    }

    override fun getItemCount(): Int = storeList.size

    fun setStores(stores: MutableList<Store>) {
        this.storeList = stores
        notifyDataSetChanged()
    }

    fun add(store: Store) {
        storeList.add(store)
        notifyDataSetChanged()
    }

    fun update(store: Store) {
        val index = storeList.indexOf(store)
        if(index != -1){
            storeList.set(index, store)
            notifyItemChanged(index)
        }
    }

    fun delete(store: Store) {
        val index = storeList.indexOf(store)
        if(index != -1) {
            storeList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)

        fun setListener(store: Store){
            with(binding.root){
                setOnClickListener {
                    listener.onClick(store)
                }

                setOnLongClickListener {
                    listener.deleteStore(store)
                    true
                }
            }

            binding.checkboxFavorite.setOnClickListener {
                listener.favoriteStore(store)
            }
        }
    }

}