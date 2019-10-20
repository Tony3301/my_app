package com.example.moneyjet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import colorSource
import com.example.moneyjet.R

class NewCategColorsAdapter(context: Context): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return colorSource.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(p0: Int): Int {
        return colorSource[p0]
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        // Get view for row item
        val view = inflater.inflate(R.layout.item_dialog_select_categ_color, p2, false)

        val colorValue = view.findViewById(R.id.tvDialogListSelectColor) as TextView
        colorValue.setBackgroundResource(colorSource[p0])

        return view
    }
}