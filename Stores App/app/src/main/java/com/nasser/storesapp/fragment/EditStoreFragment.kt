package com.nasser.storesapp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nasser.storesapp.MainActivity
import com.nasser.storesapp.R
import com.nasser.storesapp.databinding.FragmentEditStoreBinding

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

        mActivity = activity as? MainActivity
        //Flecha de retroceso en la Action bar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Cambio de titulo de la action bar
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_tittle_add)

        //Toma el control del menu
        setHasOptionsMenu(true)
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
                //regresa atras al presionar el boton
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                Snackbar.make(mBinding.root, getString(R.string.edit_store_message_save_succes),
                    Snackbar.LENGTH_SHORT)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}