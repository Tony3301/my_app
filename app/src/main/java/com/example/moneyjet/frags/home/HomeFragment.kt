package com.example.moneyjet.frags.home

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import arrayCategsExpenses
import arrayForSlices
import balance
import com.example.moneyjet.adapters.CategAdapters
import com.example.moneyjet.room.categs.CategDao
import com.example.moneyjet.room.categs.CategInfo
import com.example.moneyjet.room.categs.CategoriesDatabase
import com.example.moneyjet.room.transactions.ItemInfo
import com.example.moneyjet.room.transactions.TransactionsDatabase
import com.google.android.material.snackbar.Snackbar
import forContextMenu
import itemName
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import switchVar
import java.time.ZoneId
import java.util.*
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import com.example.moneyjet.R
import com.example.moneyjet.adapters.DialogCategReviewAdapter
import dayliDiagramCheck
import expenseShare
import incomeShare
import kotlinx.android.synthetic.main.fragment_home.*
import languageVar


@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var categDao: CategDao
    private var dbCategs: CategoriesDatabase? = null
    private lateinit var dbTrans: TransactionsDatabase

    private var array: MutableList<CategInfo> = mutableListOf()

    private lateinit var adapter: CategAdapters
    private lateinit var lvCat: ListView
    private lateinit var pieChartView: PieChartView
    private lateinit var pieChartData: PieChartData

    private lateinit var totalExp: TextView
    private lateinit var totalIncome: TextView
    private lateinit var totalSums: TextView
    private lateinit var endDate: String
    private lateinit var ivSwitchDiagram: ImageView

    //FOR CONTEXT MENU!
    private val menuItemDel = "Delete"
    private val menuItemDelPlus = "U can't delete it"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        /*val textView: TextView = root.findViewById(R.id.tvToday)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        val date = Date()
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        endDate = ""+localDate.dayOfMonth+"/"+localDate.monthValue+"/"+localDate.year

        totalExp = view.findViewById(R.id.tvTotalExp)
        totalIncome = view.findViewById(R.id.tvTotalIncome)
        totalSums = view.findViewById(R.id.tvTotalSum)

        ivSwitchDiagram = view.findViewById(R.id.ivSwitchDiagram)
        ivSwitchDiagram.setOnClickListener {
            if(dayliDiagramCheck && languageVar == "en")
                tvDailyDiagram.text = "Dayli diagram"
            if(!dayliDiagramCheck && languageVar == "en")
                tvDailyDiagram.text = "Total diagram"


            if(dayliDiagramCheck && languageVar == "ru")
                tvDailyDiagram.text = "Диаграмма за сегодня"
            if(!dayliDiagramCheck && languageVar == "ru")
                tvDailyDiagram.text = "Общая диаграмма"

            dayliDiagramCheck = !dayliDiagramCheck
            updateDiagram()
        }

        pieChartView = view.findViewById(R.id.charterHome) as PieChartView
        pieChartView.onValueTouchListener = activity?.let { MyValueTouchListener(it, endDate) }

        val btnSwitchDiag = view.findViewById(R.id.imgbtnSwitchDiagramm) as ImageButton
        btnSwitchDiag.setOnClickListener {
            updateCategsList()
            updateDiagram()
        }

        /** WORK DATABASE*/
        dbCategs = context?.let { CategoriesDatabase.getAppDataBase(it) }!!
        dbTrans = context?.let { TransactionsDatabase.getAppDataBase(it) }!!
        categDao = dbCategs!!.CategDao()
        lvCat = view.findViewById(R.id.lvCategories)
        //метод для обновления диаграммы и категорий
        updateCategsList()
        updateDiagram()
        registerForContextMenu(lvCat)
        val navController = activity?.findNavController(R.id.nav_host_fragment)

        lvCat.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                lvCat.getItemAtPosition(position)

                if (position == array.size - 1)
                    navController?.navigate(R.id.newCatFragment)
                else{
                    //тут из листа надо достать имя, потом по этому имени засетим транзакцию
                    showDialog(array[position])
                }
            }
        //обновляем тотал
        countTotal()

        return view
    }

    private fun updateCategsList() {
        arrayForSlices.clear()
        array = if(switchVar) {
            categDao.getCategsExpense() as MutableList<CategInfo>
        }else {
            categDao.getCategsIncome() as MutableList<CategInfo>
        }
        categDao.getCategsExpense().forEach {
            arrayCategsExpenses.add(it.name)
        }

        array.forEach {
            arrayForSlices.add(it.name)
        }

        //добавляем плюс, якобы плюшка для добавления категории
        array.add(CategInfo(array.size, "+", R.drawable.list_item_plus, 1))
        /**ТУТ ЛИСТ ВЬЮ С КАТЕГОРИЯМИ*/
        adapter = context?.let {
            CategAdapters(
                it, array)
        }!!
        lvCat.adapter = adapter

    }

    //THIS IS CUSTOM DIALOG FOR CREATING TRANSACTION AND ADDING TO THE CATEGORY
    @TargetApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun showDialog(catItem: CategInfo) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.dialog_add_transaction)

        val title = dialog?.findViewById(R.id.dtvCatName) as TextView
        title.text = catItem.name

        val constrLay = dialog.findViewById(R.id.dialog_add_constraint) as ConstraintLayout
        constrLay.setBackgroundResource(catItem.colorId)

        val tvDescription = dialog.findViewById(R.id.detDescription) as EditText
        val tvPom = dialog.findViewById(R.id.detPom) as EditText
        /**
         * Маску использовал,
         * с гитхаба достал
         * Просто потом взяла логика, что начерта вводить дату,
         * если ее можно из системки брать
         * */
        //val tvDate = dialog.findViewById(R.id.detDate) as MaskEditText

        val yesBtn = dialog.findViewById(R.id.btnYes) as Button
        val noBtn = dialog.findViewById(R.id.btnCancel) as TextView
        //FOR POSITIVE BUTTON
        yesBtn.setOnClickListener {
            //ТУТ ДОБАВЛЕНИЕ айтема из диалога в DB
            dbTrans = context?.let { TransactionsDatabase.getAppDataBase(context!!) }!!
            val itemsDao = dbTrans.ItemDao()
            if(tvPom.text.isNotEmpty()){
                val transaction = ItemInfo(
                    (itemsDao.getMaxID().plus(1)),
                    catItem.name,
                    tvDescription.text.toString(),
                    tvPom.text.toString().toFloat(),
                    endDate
                )
                itemsDao.insertItem(transaction)
                dialog .dismiss()
                if(switchVar)
                    balance += tvPom.text.toString().toFloat()
                else
                    balance -= tvPom.text.toString().toFloat()
                activity?.title ?: balance
                switchVar = !switchVar
                activity?.recreate()
            }
            else{
                Snackbar.make(yesBtn, "Fill all fields!", Snackbar.LENGTH_SHORT)
            }
        }
        noBtn.setOnClickListener { dialog .dismiss() }
        dialog.show()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    )
    { if (v.id == R.id.lvCategories) {
        val acmi = menuInfo as AdapterContextMenuInfo
        val itemCat = lvCat.getItemAtPosition(acmi.position) as CategInfo

        itemName = itemCat.name
        if(itemName == "+") {
            menu.add(menuItemDelPlus)
            forContextMenu = true
        }else
            menu.add(menuItemDel)

        super.onCreateContextMenu(menu, v, menuInfo)
    }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> {
                if(!forContextMenu) {
                    categDao.deleteCategByName(itemName)
                    updateDiagram()
                    activity?.recreate()
                }else
                    forContextMenu = false
            }
        }
        return super.onContextItemSelected(item)
    }

    //hear i find results for diagram
    //from ItemDao
    //I use categories to get names of categs
    //then we count all transactions
    private fun getDiagrammValues():MutableList<SliceValue> {
        val sliceValues: MutableList<SliceValue> = mutableListOf()
        val allSums: MutableList<Float> = mutableListOf()
        val allColors: MutableList<Int> = mutableListOf()

        var itemSum = 0.0

        val itemsList = if (switchVar)
            categDao.getCategsExpense() as MutableList<CategInfo>
        else
            categDao.getCategsIncome() as MutableList<CategInfo>

        var secItem: List<ItemInfo>

        itemsList.forEach{
            //тут получаем все транзакции конкретной категории
            secItem = if(dayliDiagramCheck)
                dbTrans.ItemDao().getTodaysItemsByName(it.name, endDate)
            else
                dbTrans.ItemDao().getItemsByName(it.name)
            //потом считаем сумму всей категории и вносим ее в список
            for (categ in secItem) {
                Log.d("ITEMS LIST1", categ.pom.toString() + " " + categ.type)
                itemSum += categ.pom
            }

            allSums.add(itemSum.toFloat())
            itemSum = 0.0

            when (it.colorId) {
                R.drawable.list_item_red -> allColors.add(Color.parseColor("#B6D50000"))
                R.drawable.list_item_orange_red -> allColors.add(Color.parseColor("#FF4500"))
                R.drawable.list_item_orange -> allColors.add(Color.parseColor("#FFA500"))
                R.drawable.list_item_yellow -> allColors.add(Color.parseColor("#DCFFD600"))
                R.drawable.list_item_khaki -> allColors.add(Color.parseColor("#BDB76B"))
                R.drawable.list_item_olive -> allColors.add(Color.parseColor("#808000"))
                R.drawable.list_item_green -> allColors.add(Color.parseColor("#B900C853"))
                R.drawable.list_item_blue -> allColors.add(Color.parseColor("#D12979FF"))
                R.drawable.list_item_navy -> allColors.add(Color.parseColor("#000080"))
                R.drawable.list_item_indigo -> allColors.add(Color.parseColor("#4B0082"))
                R.drawable.list_item_magneta -> allColors.add(Color.parseColor("#CD9C27B0"))
                R.drawable.list_item_pink -> allColors.add(Color.parseColor("#DBE64A7A"))
                else -> android.R.color.black
            }
        }
        //короче, заваливаем тут проверку(switchVar из файла котлин CommonVars) на расходы\доходы
        //мол, если расходы, то считаем отриц сумму, а полож не трогаем
        //сумма всех категорий
        var sumCategs = 0.0
        if (switchVar) {
            allSums.forEach {
                sumCategs += it
            }
        }
        //for(i in allSums)
        if(!switchVar)
            allSums.forEach{
                sumCategs -= it
            }
        //типа по модулю
        if(sumCategs < 0)
            sumCategs -= 2*sumCategs

        if(sumCategs == 0.0)
            sumCategs = 1.0
        //создаем лист, в котором будут посчитаны в процентах
        val percentList: MutableList<Double> = mutableListOf()
        allSums.forEach {
            //считаем проценты и в лист добавляем
            percentList.add(it / sumCategs * 100)
        }
        if(percentList.size == 0)
            sliceValues.add(SliceValue(0F, R.color.colorPrimaryDark))
            for(i in 0 until percentList.size){
                val slice = SliceValue(percentList[i].toFloat(), allColors[i])
                sliceValues.add(slice)
        }
        return sliceValues
    }

    /**ТУТ КРУГОВАЯ ДИАГРАММА
     * */
    private fun updateDiagram(){
        val lvCateg:MutableList<SliceValue> = mutableListOf()
        getDiagrammValues().forEach{
            lvCateg.add(it)
        }
        pieChartData = PieChartData(lvCateg)
        val fromAlpha = AnimationUtils.loadAnimation(context, R.anim.from_alpha)
        val toAlpha = AnimationUtils.loadAnimation(context, R.anim.to_alpha)

        pieChartView.startAnimation(toAlpha)
        pieChartData.setHasLabels(true).valueLabelTextSize = 14
        //тут я возможно что-то упустил по поводу локализации. пока не нашел еще
        if (switchVar) {
            if(languageVar == "en")
                pieChartData.setHasCenterCircle(true).centerText1 = "Expenses"
            if(languageVar == "ru")
                pieChartData.setHasCenterCircle(true).centerText1 = "Расходы"
        }else {
            if(languageVar == "en")
                pieChartData.setHasCenterCircle(true).centerText1 = "Income"
            if(languageVar == "ru")
                pieChartData.setHasCenterCircle(true).centerText1 = "Доход"
        }
        switchVar = !switchVar
        pieChartData.setHasCenterCircle(true).centerText1FontSize = 20
        pieChartView.startAnimation(fromAlpha)
        pieChartView.pieChartData = pieChartData

        //обновляем сумму расходов и доходов
        countTotal()
    }

    @SuppressLint("SetTextI18n")
    fun countTotal() {
        var negativeSum = 0.0F
        var positiveSum = 0.0F
        val totalSum:Float

        if(dayliDiagramCheck){
            categDao.getCategsExpense().forEach{ it ->
                dbTrans.ItemDao().getTodaysItemsByName(it.name, endDate).forEach {
                    negativeSum += it.pom
                }
            }
            categDao.getCategsIncome().forEach{ it ->
                dbTrans.ItemDao().getTodaysItemsByName(it.name, endDate).forEach{
                    positiveSum += it.pom
                }
            }
        }else{
            categDao.getCategsExpense().forEach{ it ->
                dbTrans.ItemDao().getItemsByName(it.name).forEach {
                    negativeSum += it.pom
                }
            }
            categDao.getCategsIncome().forEach{ it ->
                dbTrans.ItemDao().getItemsByName(it.name).forEach{
                    positiveSum += it.pom
                }
            }
        }
        //считаем общую сумму всего
        //ушли в плюс или в минус
        totalSum = positiveSum - negativeSum
        totalExp.text = "-"+String.format("%.2f", negativeSum)
        totalIncome.text = "+"+String.format("%.2f", positiveSum)
        totalSums.text = String.format("%.2f", totalSum)

        incomeShare = totalIncome.text.toString()
        expenseShare = totalExp.text.toString()

        if(totalSum > 0)
            totalSums.text = "+"+String.format("%.2f", totalSum)
        else
            totalSums.text = String.format("%.2f", totalSum)
    }

    class MyValueTouchListener constructor(actvty: Activity, date: String) : PieChartOnValueSelectListener {

        private val activity: Activity = actvty
        private var color: Int = 0
        private val nowDate = date
        //этот коммент я удлаять конечно же не буду
        //я гуглил и даже бывал на таких сайтах китайскоязычных=)
        override//如果設置爲能選中，選中時走此方法，如果設置爲不能選中，點擊時走此方法
        fun onValueSelected(i: Int, value: SliceValue) {
            color = value.color
            Log.d("TOUCH", i.toString())
            showDialog(activity, i)
        }

        @SuppressLint("SetTextI18n")
        private fun showDialog(activity: Activity, index: Int) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_transaction_info)

            val dbTrans = activity.let { TransactionsDatabase.getAppDataBase(it) }!!
            val lvInfo = dialog.findViewById(R.id.lvDialogShowTrans) as ListView
            val dialogCategName = dialog.findViewById(R.id.tvDialogReviewTransCategName) as TextView
            if(arrayForSlices.size == 0)
                return
            dialogCategName.text = arrayForSlices[index]
            val dialogCategTotal =
                dialog.findViewById(R.id.tvDialogReviewTransCategTotal) as TextView
            var countVal = 0.0
            val database = dbTrans.ItemDao()
            val array: List<ItemInfo>

            if (dayliDiagramCheck){
                database.getTodaysItemsByName(arrayForSlices[index], nowDate).forEach {
                    countVal += it.pom
                }
                array = database.getTodaysItemsByName(arrayForSlices[index], nowDate)
            }else{
                database.getItemsByName(arrayForSlices[index]).forEach {
                    countVal += it.pom
                }
                array = database.getItemsByName(arrayForSlices[index])
            }
            val totalMultiLang = if(languageVar == "en") "Total:" else  "В сумме:"

            if(switchVar)
                dialogCategTotal.text = totalMultiLang+" +" + String.format("%.2f", countVal) + " Br"
            else
                dialogCategTotal.text = totalMultiLang + " -" + String.format("%.2f", countVal) + " Br"


            val adapter = DialogCategReviewAdapter(
                activity, array)
            lvInfo.adapter = adapter
            val lvItemTrans = dialog.findViewById(R.id.lvDialogShowTrans) as ListView
            lvItemTrans.setBackgroundColor(color)

            val yesBtn = dialog.findViewById(R.id.btnDialogTransYes) as Button
            //FOR POSITIVE BUTTON
            yesBtn.setOnClickListener {
                dialog .dismiss()
            }
            dialog .show()
        }
        override fun onValueDeselected() {
        }
    }

    //TODO корутины и датабейз!
}