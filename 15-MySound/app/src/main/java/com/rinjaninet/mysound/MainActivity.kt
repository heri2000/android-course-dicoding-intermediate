package com.rinjaninet.mysound

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sp: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvSoundPool = findViewById<TextView>(R.id.tv_soundpool)
        val btnSound = findViewById<Button>(R.id.btn_sound_pool)

        tvSoundPool.text = "Test Soundpool: loading..."

        sp = SoundPool.Builder()
            .setMaxStreams(10)
            .build()

        sp.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                spLoaded = true
                tvSoundPool.text = "Test Soundpool: loaded"
            } else {
                Toast.makeText(this@MainActivity, "Gagal load", Toast.LENGTH_SHORT).show()
                tvSoundPool.text = "Test Soundpool: loading failed"
            }
        }

        soundId = sp.load(this, R.raw.clinking_glasses, 1)

        btnSound.setOnClickListener {
            if (spLoaded) {
                sp.play(soundId, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}