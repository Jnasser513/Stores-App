package com.nasser.storesapp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
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
    //Variable para saber si estamos en modo edicion
    private var mIsEditMode: Boolean = false
    //Recibe los parametros de la tienda que se esta consultando
    private var mStore: Store? = null

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
            mIsEditMode = true
            getStore(id)
        } else{
            mIsEditMode = false
            mStore = Store(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()
        setupTextFields()
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        //Flecha de retroceso en la Action bar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Cambio de titulo de la action bar dependiendo si esta creando tienda o actualizando
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_tittle)
        else getString(R.string.create_store_tittle_add)

        //Toma el control del menu
        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        mBinding.photoUrlEditText.addTextChangedListener {
        }

        with(mBinding){
            photoUrlEditText.addTextChangedListener {
                validateFields(photoUrlTextInputLayout)
                loadImage(it.toString().trim())
            }

            phoneEditTextInput.addTextChangedListener {
                validateFields(phoneTextInputLayout)
            }

            nameEditTextInput.addTextChangedListener {
                validateFields(nameTextInputLayout)
            }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun getStore(id: Long) {
        doAsync {
            mStore = StoreApplication.database.storeDao().getStoreById(id)
            uiThread {
                if(mStore != null) setUiStore(mStore!!)
            }
        }
    }

    private fun setUiStore(store: Store) {
        with(mBinding){
            //2 formas para llamar al argumento, con setText o a traves de la funciona editable
            nameEditTextInput.setText(store.name)
            phoneEditTextInput.text = store.phone.editable()
            websiteEditTextInput.text = store.website.editable()
            photoUrlEditText.text = store.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

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
                if(mStore != null &&
                    validateFields(mBinding.photoUrlTextInputLayout, mBinding.phoneTextInputLayout, mBinding.nameTextInputLayout)) {
                    /*val store = Store(name = mBinding.nameEditTextInput.text.toString().trim(),
                    phone = mBinding.phoneEditTextInput.text.toString().trim(),
                    website = mBinding.websiteEditTextInput.text.toString().trim(),
                    photoUrl = mBinding.photoUrlEditText.text.toString().trim())*/
                    with(mStore!!){
                        name = mBinding.nameEditTextInput.text.toString().trim()
                        phone = mBinding.phoneEditTextInput.text.toString().trim()
                        website = mBinding.websiteEditTextInput.text.toString().trim()
                        photoUrl = mBinding.photoUrlEditText.text.toString().trim()
                    }

                    doAsync {
                        if(mIsEditMode){
                            StoreApplication.database.storeDao().insertOrUpdateStore(mStore!!)
                        } else {
                            //Inserta el elemento a la base de datos y nos devuelve su id
                            mStore!!.id = StoreApplication.database.storeDao().insertOrUpdateStore(mStore!!)
                        }
                        uiThread {

                            hideKeyboard()

                            if(mIsEditMode){
                                mActivity?.updateStore(mStore!!)
                                Snackbar.make(mBinding.root,
                                    R.string.edit_store_message_update_succes,
                                    Snackbar.LENGTH_SHORT).show()
                            } else {

                                mActivity?.addStore(mStore!!)

                                //Oculta el teclado cuando se presiona el boton de action bar para agregar tienda
                                Toast.makeText(mActivity,
                                    R.string.edit_store_message_save_succes,
                                    Toast.LENGTH_SHORT).show()
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields) {
            //Si esta vacio marca error
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_require)
                textField.editText?.requestFocus()
                isValid = false
            }
            else {
                textField.error = null
            }
        }

        if(!isValid) Toast.makeText(requireContext(), R.string.missing_data, Toast.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if(mBinding.photoUrlEditText.text.toString().trim().isEmpty()){
            mBinding.photoUrlTextInputLayout.error = getString(R.string.helper_require)
            mBinding.photoUrlEditText.requestFocus()
            isValid = false
        }

        if(mBinding.phoneEditTextInput.text.toString().trim().isEmpty()){
            mBinding.phoneTextInputLayout.error = getString(R.string.helper_require)
            mBinding.phoneEditTextInput.requestFocus()
            isValid = false
        }

        if(mBinding.nameEditTextInput.text.toString().trim().isEmpty()){
            mBinding.nameTextInputLayout.error = getString(R.string.helper_require)
            mBinding.nameEditTextInput.requestFocus()
            isValid = false
        }

        return isValid
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