package com.nasser.storesapp

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nasser.storesapp.data.entity.Store
import com.nasser.storesapp.databinding.ActivityMainBinding
import com.nasser.storesapp.fragment.EditStoreFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.actionSave.setOnClickListener {
            val store = Store(name = mBinding.textInputName.text.toString().trim())

            Thread{
                StoreApplication.database.storeDao().insertOrUpdateStore(store)
            }.start()
            mAdapter.add(store)
        }*/

        mBinding.fav.setOnClickListener { launchEditFragment() }

        setupRecyclerView()
    }

    //Lanzando el fragmento de crear tienda
    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if(args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.container_main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
        //mBinding.fav.hide()
    }

    //Configuracion del recyclerview
    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStores()

        mBinding.recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores() {
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    /*
    * OnClickListener
    * */

    //Metodo que envia a editar tienda
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    //Metodo para poner o eliminar el favorito de una tienda
    override fun favoriteStore(store: Store) {
        store.isFavorite = !store.isFavorite
        doAsync {
            StoreApplication.database.storeDao().insertOrUpdateStore(store)
            uiThread {
                updateStore(store)
            }
        }
    }

    //Metodo que muestra el menu de la tienda
    override fun deleteStore(store: Store) {
        val item = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_tittle)
            .setItems(item) { dialog, which ->
                when(which){
                    0 -> confirmDelete(store)
                    1 -> goPhone(store.phone)
                    2 -> goWebsite(store.website)

            }}
            .show()
    }

    private fun confirmDelete(store: Store){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_tittle)
            .setPositiveButton(R.string.dialog_delete_confirm) { dialog, which ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(store)
                    uiThread {
                        mAdapter.delete(store)
                    }
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    //Metodo para lanzar el telefono
    private fun goPhone(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel: $phone")
        }
        startIntent(callIntent)
    }

    //Metodo para lanzar el sitio web de la tienda
    private fun goWebsite(website: String) {
        if(website.isEmpty()) {
            Toast.makeText(this, R.string.no_website_message, Toast.LENGTH_SHORT).show()
        } else if (URLUtil.isValidUrl(website)){
            val websiteIntent = Intent(). apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        } else {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
        }
    }

    //Metodo para lanzar el intent
    private fun startIntent(intent: Intent) {
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
        }
    }

    /*
    * MainAux
    * */

    //Metodo para mostrar si es favorito o no
    override fun hideFab(isVisible: Boolean) {
        if(isVisible){
            mBinding.fav.show()
        } else{
            mBinding.fav.hide()
        }
    }

    //Metodo para agregar tienda al recyclerview
    override fun addStore(store: Store) {
        mAdapter.add(store)
    }

    //Metodo para actualizar los datos del recyclerview
    override fun updateStore(store: Store) {
        mAdapter.update(store)
    }
}