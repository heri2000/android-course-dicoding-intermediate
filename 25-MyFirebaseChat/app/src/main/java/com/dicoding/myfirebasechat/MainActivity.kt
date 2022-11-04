package com.dicoding.myfirebasechat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myfirebasechat.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: FirebaseMessageAdapter

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        binding.sendButton.setOnClickListener {
            val friendlyMessage = Message(
                binding.messageEditText.text.toString(),
                firebaseUser.displayName.toString(),
                firebaseUser.photoUrl.toString(),
                Date().time
            )
            messagesRef.push().setValue(friendlyMessage) { error, _ ->
                if (error != null) {
                    Toast.makeText(this, getString(R.string.send_error) + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
                }
            }
            binding.messageEditText.setText("")
        }

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()
        adapter = FirebaseMessageAdapter(options, firebaseUser.displayName)
        binding.messageRecyclerView.adapter = adapter

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this,
            OnCompleteListener<Void?> { updateUI(null) })
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    companion object {
        const val MESSAGES_CHILD = "messages"
    }
}