package com.reemsib.mosta3ml.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.SubCategory
import kotlinx.android.synthetic.main.sub_categ_item.view.*

class SubCategAdapter(var activity: Activity, var data: MutableList<SubCategory>) :
    RecyclerView.Adapter<SubCategAdapter.MyViewHolder>() {

    var mListener: OnItemClickListener?=null

    interface OnItemClickListener {
        fun onClicked(clickedItemPosition: Int, category: String)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subCat = itemView.tv_sub_categ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(activity).inflate(R.layout.sub_categ_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       holder.subCat.text = data[position].name

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClicked(position, data[position].name)

            }
        }
    }
}
//    fun filterList(filteredList: ArrayList<Drug>) {
//        data = filteredList
//        notifyDataSetChanged()
//    }


