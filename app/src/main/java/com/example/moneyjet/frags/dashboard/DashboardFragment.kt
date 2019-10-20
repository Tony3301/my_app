package com.example.moneyjet.frags.dashboard

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.moneyjet.R
import com.example.moneyjet.adapters.CategAdapters
import com.example.moneyjet.adapters.TransactionAdapter
import com.example.moneyjet.room.transactions.ItemDao
import com.example.moneyjet.room.transactions.ItemInfo
import com.example.moneyjet.room.transactions.TransactionsDatabase
import currentDate
import dateDB
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

@Suppress("NAME_SHADOWING")
class DashboardFragment : Fragment(), View.OnClickListener {

    private lateinit var dashboardViewModel: DashboardViewModel

    private var array:MutableList<ItemInfo> = mutableListOf()
    private lateinit var itemDao: ItemDao

    private lateinit var dbTrans: TransactionsDatabase
    private lateinit var adapter: TransactionAdapter
    //views
    private lateinit var listTrans:ListView
    private lateinit var btnDialogDate: Button

    //for date
    private val date = Date()
    @RequiresApi(Build.VERSION_CODES.O)
    private val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    private lateinit var clickedDate: String

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        listTrans = root.findViewById(R.id.lvTransactions)
        //Найти кнопку и засетить дату за сегодня
        dateDB = currentDate

        //здесь дата типа 8/ a11 для кнопки
        val dateEnd = ""+localDate.dayOfMonth+"/"+localDate.monthValue

        btnDialogDate = root.findViewById(R.id.btnDateDialog)
        btnDialogDate.text = dateEnd

        updateListTrans(true)
        btnDialogDate.setOnClickListener(this)
        dashboardViewModel.text.observe(this, Observer {
        })
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDateDialog -> {
                clickDataPicker()
            }
            else -> {
            }
        }
    }

    @SuppressLint("SetTextI18n", "NewApi")
    fun clickDataPicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener
            {
                    _, year, monthOfYear, dayOfMonth ->

                clickedDate = "$dayOfMonth/${monthOfYear + 1}/$year"

                val checkDay = dateDB == clickedDate
                updateListTrans(checkDay)

                btnDialogDate.text = "$dayOfMonth/${monthOfYear + 1}"

            }, year, month, day)
        dpd.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateListTrans(thisDay: Boolean){

        dateDB = if(thisDay)
            currentDate
        else
            clickedDate

        Log.d("cddd", currentDate)

        dbTrans = context?.let { TransactionsDatabase.getAppDataBase(it)}!!
        itemDao = dbTrans.ItemDao()
        array = itemDao.getItemsByDate(dateDB) as MutableList<ItemInfo>

        adapter = context?.let {
            TransactionAdapter(
                it, array)
        }!!
        listTrans.adapter = adapter

        dateDB = currentDate
    }

}