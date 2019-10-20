package com.example.moneyjet.frags


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import colorSource
import com.example.moneyjet.R
import com.example.moneyjet.adapters.NewCategColorsAdapter
import com.example.moneyjet.room.categs.CategDao
import com.example.moneyjet.room.categs.CategInfo
import com.example.moneyjet.room.categs.CategoriesDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_cat.*
import rbutton_id


/**
 * A simple [Fragment] subclass.
 */
class NewCatFragment : Fragment() {

    private lateinit var categDao: CategDao
    private lateinit var db: CategoriesDatabase

    private var colorID = 0

    private lateinit var radioGroup: RadioGroup
    private lateinit var btnDialog:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO вот эта штука спасает фрагмент, при открытии киборда
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        db = activity?.let { CategoriesDatabase.getAppDataBase(it) }!!
        categDao = db.CategDao()
        val view:View = inflater.inflate(R.layout.fragment_new_cat, container, false)
        btnDialog = view.findViewById(R.id.btnColorPick)
        btnDialog.setOnClickListener {
            activity?.let{showDialog(it)}
        }

        radioGroup = view.findViewById(R.id.rad_group)
        radioGroup.setOnCheckedChangeListener{_, isChecked ->
            when(isChecked){
                R.id.rbExpenses -> rbutton_id = 0
                R.id.rbIncome -> rbutton_id = 1
            }
        }

        val btnAddCat:Button = view.findViewById(R.id.btnAddCat)
        btnAddCat.setOnClickListener {
            addCategory()
        }
        return view
    }



    //CREATE CATEGDAO AND ADD TO DB
    private fun addCategory(){
        val textName = etNameCat
        if (db.CategDao().catExist(textName.text.toString()) == 0) {
            when {
                db.CategDao().catExist(textName.text.toString()) == 1 -> view?.let {
                    Snackbar.make(
                        it,
                        "Such category is already exist O_o",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                textName.text.isEmpty() -> view?.let {
                    Snackbar.make(
                        etNameCat,
                        "Name is empty =(",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val catDao = CategInfo(
                        categDao.getLastID() + 1,
                        etNameCat.text.toString().trim(),
                        colorSource[colorID],
                        rbutton_id
                    )
                    Log.d("ADD COLOR", colorID.toString())
                    categDao.insertCateg(catDao)
                    activity?.onBackPressed()
                }
            }
        }
    }


    private fun showDialog(activity: Activity){
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_list_colors)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val gvColors = dialog.findViewById(R.id.gvDialogColors) as GridView
        gvColors.adapter = context?.let { NewCategColorsAdapter(it) }
        gvColors.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            colorID = position
            btnDialog.setBackgroundResource(colorSource[colorID])
            dialog.dismiss()
            //Toast.makeText(this@MainActivity, " Clicked Position: " + (position + 1),
            //    Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }


}