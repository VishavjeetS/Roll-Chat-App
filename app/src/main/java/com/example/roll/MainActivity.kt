package com.example.roll

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Adapter.ChatAdapter
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.Util.Contacts
import com.example.roll.Util.Login
import com.example.roll.Util.MessageActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService


class MainActivity : AppCompatActivity() {
    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var chatsAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<user>
    private lateinit var messageList: ArrayList<Message>
    private lateinit var MDRef: DatabaseReference
    private lateinit var toolbar: Toolbar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var showContacts: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar()

        initialization()

        getChatList()

        showContacts.setOnClickListener {
            startActivity(Intent(this, Contacts::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.log_out -> {
                finish()
                mAuth.signOut()
                startActivity(Intent(this, Login::class.java))
                Toast.makeText(this,"Log out", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initialization(){
        MDRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        showContacts = findViewById(R.id.btContacts)
        chatsRecyclerView = findViewById(R.id.chatRV)
        chatList = ArrayList()
        messageList = ArrayList()
        chatsAdapter = ChatAdapter(this, chatList)
        chatsRecyclerView.layoutManager = LinearLayoutManager(this)
        chatsRecyclerView.adapter = chatsAdapter
    }

    private fun toolbar(){
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Roll"
        setSupportActionBar(toolbar)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun getChatList(){
        MDRef.child("user").addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(user::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        chatList.add(currentUser!!)
                    }
                }
                chatsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}