package com.example.roll.Util

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roll.Adapter.MessageAdapter
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageActivity : AppCompatActivity() {
    private lateinit var messageRV: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendMessage: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var chatMessage: user
    private lateinit var MDRef: DatabaseReference
    private lateinit var messageToolbar: Toolbar
    private lateinit var friendsName: TextView

    var receiverRoom: String? = null
    var senderRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_layout)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        messageToolbar = findViewById(R.id.message_toolbar)
        friendsName = findViewById(R.id.friendsName)
        setSupportActionBar(messageToolbar)

        val name = intent.getStringExtra("name")
        friendsName.text = name

        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        Message().receiverID = receiverUid
        Message().senderID = senderUid
        Log.d("MessageRID", Message().receiverID.toString())
        Log.d("MessageSID", Message().senderID.toString())

        MDRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        messageRV = findViewById(R.id.message_rv)
        messageBox = findViewById(R.id.messageboxEditText)
        sendMessage = findViewById(R.id.sendMessage)
        messageList = ArrayList()
        chatMessage = user()
        messageAdapter = MessageAdapter(this, messageList)
        messageRV.layoutManager = LinearLayoutManager(this)
        messageRV.adapter = messageAdapter

        MDRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                    messageRV.scrollToPosition(messageList.size-1)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        sendMessage.setOnClickListener {
            if(messageBox.text.isEmpty()){
                messageBox.error = "Type a message"
            }
            else{
                val message = messageBox.text.toString()
                val messageObject = Message(message, senderUid)
                MDRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        MDRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")
            }
        }

    }
}