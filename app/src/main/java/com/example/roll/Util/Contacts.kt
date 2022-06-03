package com.example.roll.Util

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Adapter.ChatAdapter
import com.example.roll.Adapter.ContactsAdapter
import com.example.roll.Model.Contact
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Contacts: AppCompatActivity() {
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsLayoutManager: RecyclerView.LayoutManager
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var dbref: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var contactsList : ArrayList<Contact>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts)
        contactsRecyclerView = findViewById(R.id.contacts_rv)
        contactsLayoutManager = LinearLayoutManager(this as Activity)
        dbref = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        contactsList = ArrayList()
        contactsAdapter = ContactsAdapter(this, contactsList)
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsRecyclerView.adapter = contactsAdapter
        dbref.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                contactsList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(Contact::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        contactsList.add(currentUser!!)
                    }
                }
                contactsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}