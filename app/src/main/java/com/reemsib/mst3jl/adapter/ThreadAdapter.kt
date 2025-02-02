package com.reemsib.mst3jl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reemsib.mst3jl.R
import com.reemsib.mst3jl.model.ChatInfo
import com.reemsib.mst3jl.model.Message
import java.text.SimpleDateFormat
import java.util.*


class ThreadAdapter(var activity: Activity, var infos: ArrayList<Message>, val userId: Int):
    RecyclerView.Adapter<ThreadAdapter.ViewHolder>() {

    //Tag for tracking self message
    private val SELF = 786

    //IN this method we are tracking the self message
    override fun getItemViewType(position: Int): Int {
        //getting message object of current position
        val info: Message = infos[position]

        //If its owner  id is  equals to the logged in user id
         if (info.user_id == userId) {
            //Returning self
           return SELF
        } else{
            return position
        }
        //else returning position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Creating view
        val itemView: View
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_sent_message,
                parent,
                false
            )
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_recieved_message,
                parent,
                false
            )
        }
        //returing the view
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Adding messages to the views
        val info: Message = infos[position]
        holder.textViewMessage.setText(info.message)

        val sDate1 =info.created_at
        val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(sDate1)
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        val s= dateFormat.format(date1!!)
        println(sDate1 + "\t" + date1)
        holder.textViewTime.text = s

    }

    override fun getItemCount(): Int {
        return infos.size
    }

    //Initializing views
     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMessage: TextView
        var textViewTime: TextView

        init {
            textViewMessage = itemView.findViewById(R.id.tv_message)
            textViewTime = itemView.findViewById(R.id.tv_time)
        }
    }


}