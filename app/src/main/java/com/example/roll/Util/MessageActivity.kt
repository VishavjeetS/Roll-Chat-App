package com.example.roll.Util

import android.app.*
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.roll.Adapter.MessageAdapter
import com.example.roll.Model.Message
import com.example.roll.Model.user
import com.example.roll.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


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
    private var receiverUid: String? = null
    private var senderUid: String? = null
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private final var NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send"
    private final var SERVER_KEY = "AAAAR3ka--c:APA91bEyV4EQYG661qfY5XdJ7_VSoGioywSfAqKQTzSx-bPld0erOb5m51VZIZ8gTZ7S-u1eEMFWZr-N1qSvMZJ06l-Bxg8vbxkHpcmpOkrm_7Yl1td7dnidXgjVwdPw7VKwj__QCaNu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_layout)
        toolbar()

        initialization()

        loadMessages()

        sendMessage()
    }

    private fun toolbar(){
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        messageToolbar = findViewById(R.id.message_toolbar)
        friendsName = findViewById(R.id.friendsName)
        setSupportActionBar(messageToolbar)
    }

    private fun initialization(){
        MDRef = FirebaseDatabase.getInstance().reference
        val name = intent.getStringExtra("name")
        friendsName.text = name
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        Message().receiverID = receiverUid
        Message().senderID = senderUid
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
    }

    private fun sendMessage(){
        sendMessage.setOnClickListener {
            if(messageBox.text.isEmpty()){
                messageBox.error = "Type a message"
            }
            else{
                val message = messageBox.text.toString()
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val timeStamp = "$hour:$minute"
                val messageObject = Message(message, senderUid, receiverUid, timeStamp)
                MDRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        MDRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                getToken(message, senderUid, receiverUid)
                messageBox.setText("")
            }
        }
    }

    private fun getToken(message: String, senderUid: String?, receiverUid: String?) {
        val dbref = FirebaseDatabase.getInstance().getReference("user").child(senderUid.toString())
        dbref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val token = snapshot.child("token").value.toString()


                val to = JSONObject()
                val data = JSONObject()
                try {
                    data.put("title", friendsName)
                    data.put("message", message)
                    data.put("hisID", receiverUid)
                    data.put("chatID", senderUid)
                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendNotification(to: JSONObject) {
        val request: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, NOTIFICATION_URL, to,
                Response.Listener { response: JSONObject ->
                    Log.d("notification",
                        "sendNotification: $response")
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.d("notification",
                        "sendNotification: $error")
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map: MutableMap<String, String> = HashMap()
                    map["Authorization"] = "key=$SERVER_KEY"
                    map["Content-Type"] = "application/json"
                    return map
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(request)
    }

    private fun loadMessages(){
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
    }
}