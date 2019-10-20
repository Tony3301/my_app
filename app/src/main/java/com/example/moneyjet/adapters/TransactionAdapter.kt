package com.example.moneyjet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import arrayCategsExpenses
import com.example.moneyjet.R
import com.example.moneyjet.room.transactions.ItemInfo
import kotlinx.android.synthetic.main.item_by_date.view.*

class TransactionAdapter(context: Context,
                         private val dataSource: List<ItemInfo>): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(p0: Int): ItemInfo {
            return dataSource[p0]
        }

    override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

    override fun getCount(): Int {
            return dataSource.size
        }


    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view = inflater.inflate(R.layout.item_by_date, p2, false)

        val item:ItemInfo = getItem(p0)

        //fill the views
        view.tvTime.text = item.date
        view.tvDescription.text = item.desc

        if(arrayCategsExpenses.contains(item.type)) {
            view.imvColor.setImageResource(R.drawable.item_date_negative)
            view.tvValTransaction.text = "-"+item.pom.toString()+" Br"
        }else {
            view.imvColor.setImageResource(R.drawable.item_date_plus)
            view.tvValTransaction.text = "+"+item.pom.toString()+" Br"
        }

        return view
    }

}

