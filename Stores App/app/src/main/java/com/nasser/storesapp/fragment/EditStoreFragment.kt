package com.nasser.storesapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.nasser.storesapp.MainActivity
import com.nasser.storesapp.R
import com.nasser.storesapp.StoreApplication
import com.nasser.storesapp.data.entity.Store
import com.nasser.storesapp.databinding.FragmentEditStoreBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if(id != null && id != 0L){
            Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
        }

        mActivity = activity as? MainActivity
        //Flecha de retroceso en la Action bar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Cambio de titulo de la action bar
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_tittle_add)

        //Toma el control del menu
        setHasOptionsMenu(true)

        mBinding.photoUrlEditText.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.photoUrlEditText.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Inflamos la vista del menu
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //Cuando se presione el boton
            android.R.id.home -> {
                //regresa atras al presionar el boton y oculta el teclado
                hideKeyboard()
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                val store = Store(name = mBinding.nameEditTextInput.text.toString().trim(),
                    phone = mBinding.phoneEditTextInput.text.toString().trim(),
                    website = mBinding.websiteEditTextInput.text.toString().trim(),
                    photoUrl = mBinding.photoUrlEditText.text.toString().trim())

                doAsync {
                    //Inserta el elemento a la base de datos y nos devuelve su id
                    store.id = StoreApplication.database.storeDao().insertOrUpdateStore(store)
                    uiThread {
                        mActivity?.addStore(store)

                        //Oculta el teclado cuando se presiona el boton de action bar para agregar tienda
                        Toast.makeText(mActivity, R.string.edit_store_message_save_succes, Toast.LENGTH_SHORT)
                            .show()
                        mActivity?.onBackPressed()
                        hideKeyboard()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Funcion para ocultar el teclado
    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null) {
            imm.hideSoftInputFromWindow(requireView().windowToken,0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}