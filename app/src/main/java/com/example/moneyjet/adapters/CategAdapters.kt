package com.example.moneyjet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import colorSource
import com.example.moneyjet.R
import com.example.moneyjet.room.categs.CategInfo
import kotlinx.android.synthetic.main.categ_item.view.*

class CategAdapters(context: Context,
    private val dataSource: List<CategInfo>): BaseAdapter(){


    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(p0: Int): CategInfo {
        return dataSource[p0]
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        // Get view for row item
        val view = inflater.inflate(R.layout.categ_item, p2, false)

        val category = getItem(p0)
        view.tvCateg.text = category.name

        view.categItemBackground.setBackgroundResource(category.colorId)

        return view
    }

}