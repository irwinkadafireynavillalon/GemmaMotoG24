package com.ejemplo.gemmamotog24

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var gemmaEngine: GemmaEngine
    private lateinit var tvStatus: TextView
    private lateinit var tvOutput: TextView
    private lateinit var etPrompt: EditText
    private lateinit var btnSend: Button
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvOutput = findViewById(R.id.tvOutput)
        etPrompt = findViewById(R.id.etPrompt)
        btnSend = findViewById(R.id.btnSend)
        scrollView = findViewById(R.id.scrollView)

        gemmaEngine = GemmaEngine(this)

        startModelLoading()

        btnSend.setOnClickListener {
            val prompt = etPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                sendUserPrompt(prompt)
            }
        }
    }

    private fun startModelLoading() {
        tvStatus.text = "Comprobando archivo del modelo..."

        CoroutineScope(Dispatchers.IO).launch {
            if (!gemmaEngine.isModelPresent()) {
                val expectedPath = gemmaEngine.getExpectedModelPath()
                withContext(Dispatchers.Main) {
                    tvStatus.text = "Falta el modelo. Cópialo a:\n$expectedPath"
                    btnSend.isEnabled = false
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                tvStatus.text = "Cargando Gemma en memoria (esto toma ~5-10s)..."
            }

            try {
                val loaded = gemmaEngine.loadModel()
                withContext(Dispatchers.Main) {
                    if (loaded) {
                        tvStatus.text = "● Gemma listo (Modo Offline / Local)"
                        tvStatus.setBackgroundColor(0xFFE8F5E9.toInt())
                        tvStatus.setTextColor(0xFF2E7D32.toInt())
                        btnSend.isEnabled = true
                    } else {
                        tvStatus.text = "Error al inicializar el modelo."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvStatus.text = "Error de carga: ${e.localizedMessage}"
                }
            }
        }
    }

    private fun sendUserPrompt(prompt: String) {
        btnSend.isEnabled = false
        etPrompt.text.clear()

        tvOutput.append("\n\nTú: $prompt\n\nGemma: ")
        scrollToBottom()

        try {
            gemmaEngine.generateStream(prompt) { token, done ->
                runOnUiThread {
                    tvOutput.append(token)
                    scrollToBottom()
                    if (done) {
                        btnSend.isEnabled = true
                    }
                }
            }
        } catch (e: Exception) {
            tvOutput.append("\n[Error de inferencia: ${e.localizedMessage}]")
            btnSend.isEnabled = true
        }
    }

    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gemmaEngine.close()
    }
}
