package com.example.roll.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.R
import com.example.roll.Util.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val context: Context, private val chats:ArrayList<user>):RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private lateinit var MDRef: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentUser = chats[position]
        holder.name.text = currentUser.name
        MDRef = FirebaseDatabase.getInstance().getReference("chats").child(currentUser.uid.toString() + FirebaseAuth.getInstance().currentUser?.uid.toString()).child("messages")
        MDRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    val user = postSnapshot.getValue(Message::class.java)
                    holder.lastMessage.text = user?.message.toString()
                    holder.timeStampChat.text = user?.timeStamp.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameRV)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
        val timeStampChat: TextView = itemView.findViewById(R.id.timeStampChat)
    }
}