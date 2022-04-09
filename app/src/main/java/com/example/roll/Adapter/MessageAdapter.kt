package com.example.roll.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(private val context: Context, private val messageList:ArrayList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var RECEIVED = 2
    private var SENT = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1){
            //inflate sent
            val view = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            SentViewHolder(view)
        } else{
            //inflate receive
            val view = LayoutInflater.from(context).inflate(R.layout.recieved, parent, false)
            ReceiveViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timeStamp = "$hour:$minute"
        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.sentMessageTimeStamp.text = timeStamp
        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            holder.receiveMessageTimeStamp.text = timeStamp
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.receivedMessage)
        val receiveMessageTimeStamp: TextView = itemView.findViewById(R.id.receiveMessageTimeStamp)
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.sentMessage)
        val sentMessageTimeStamp: TextView = itemView.findViewById(R.id.sentMessageTimeStamp)
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderID){
            SENT
        }else{
            RECEIVED
        }
    }
}