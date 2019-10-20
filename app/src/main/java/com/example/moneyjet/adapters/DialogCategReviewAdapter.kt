package com.example.moneyjet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.moneyjet.R
import com.example.moneyjet.room.transactions.ItemInfo

class DialogCategReviewAdapter(context: Context,
                                private val dataSource: List<ItemInfo>): BaseAdapter(){


    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(p0: Int): ItemInfo {
        return dataSource[p0]
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        // Get view for row item
        val view = inflater.inflate(R.layout.dialog_review_categs_item, p2, false)

        val categValue = view.findViewById(R.id.tvDialogCategValue) as  TextView
        val categDescription = view.findViewById(R.id.tvDialogCategDesc) as  TextView

        categValue.text = getItem(p0).pom.toString()+" Br: "
        categDescription.text = getItem(p0).desc

        return view
    }

}