package com.nasser.storesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.nasser.storesapp.data.entity.Store
import com.nasser.storesapp.databinding.ActivityMainBinding
import com.nasser.storesapp.fragment.EditStoreFragment
import org.jetbrains.anko.doAsync
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
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    override fun favoriteStore(store: Store) {
        store.isFavorite = !store.isFavorite
        doAsync {
            StoreApplication.database.storeDao().insertOrUpdateStore(store)
            uiThread {
                mAdapter.update(store)
            }
        }
    }

    override fun deleteStore(store: Store) {
        doAsync {
            StoreApplication.database.storeDao().deleteStore(store)
            uiThread {
                mAdapter.delete(store)
            }
        }
    }

    /*
    * MainAux
    * */

    override fun hideFab(isVisible: Boolean) {
        if(isVisible){
            mBinding.fav.show()
        } else{
            mBinding.fav.hide()
        }
    }

    override fun addStore(store: Store) {
        mAdapter.add(store)
    }

    override fun updateStore(store: Store) {

    }
}