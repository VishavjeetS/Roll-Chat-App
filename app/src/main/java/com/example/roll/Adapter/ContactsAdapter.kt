package com.example.roll.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Model.Contact
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ContactsAdapter(private var context: Context, private var contactList: ArrayList<Contact>): RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {
    private lateinit var MDRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.contact_name)
        var addFriend = itemView.findViewById<Button>(R.id.contact_addFriend_btn)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentUser = contactList[position]
        mAuth = FirebaseAuth.getInstance()
        holder.name.text = currentUser.name
        FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser?.uid!!).equalTo(currentUser.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    if(!postSnapshot.exists()){
                        holder.addFriend.visibility = View.GONE
                    }
                    else{
                        holder.addFriend.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser?.uid!!).child("friends").addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(postSnapshot in snapshot.children){
//                    if(!postSnapshot.exists()){
//                        holder.addFriend.visibility = View.VISIBLE
//                    }
//                    else{
//                        holder.addFriend.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
        holder.addFriend.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}