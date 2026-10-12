package com.ejemplo.gemmamotog24

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File

/**
 * Gestor del motor de inferencia local para Motorola Moto G24.
 * Optimizado para ejecutar en CPU de 8 núcleos evitando saturar la RAM.
 */
class GemmaEngine(private val context: Context) {

    private var inferenceEngine: LlmInference? = null

    private fun findModelFile(): File? {
        val candidates = listOf(
            File("/data/data/com.termux/files/home/GemmaMotoG24/gemma-2b-it-cpu-int4.bin"),
            File(context.getExternalFilesDir(null), "gemma-2b-it-cpu-int4.bin"),
            File("/storage/emulated/0/Download/gemma-2b-it-cpu-int4.bin"),
            File("/storage/emulated/0/Android/data/com.ejemplo.gemmamotog24/files/gemma-2b-it-cpu-int4.bin"),
            File(context.filesDir, "gemma-2b-it-cpu-int4.bin")
        )
        return candidates.firstOrNull { it.exists() && it.canRead() }
    }

    fun getExpectedModelPath(): String {
        return findModelFile()?.absolutePath ?: File(context.getExternalFilesDir(null), "gemma-2b-it-cpu-int4.bin").absolutePath
    }

    fun isModelPresent(): Boolean {
        return findModelFile() != null
    }

    fun loadModel(): Boolean {
        val file = findModelFile() ?: return false

        // Configuración específica para el procesador MediaTek Helio G85
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(file.absolutePath)
            .setMaxTokens(256)      // 256 tokens es seguro para el límite de RAM disponible
            .setTemperature(0.7f)
            .setTopK(40)
            .build()

        inferenceEngine = LlmInference.createFromOptions(context, options)
        return true
    }

    fun generate(prompt: String): String {
        val engine = inferenceEngine ?: throw IllegalStateException("Modelo no inicializado")
        return engine.generateResponse(prompt)
    }

    fun generateStream(prompt: String, onToken: (String, Boolean) -> Unit) {
        val response = generate(prompt)
        onToken(response, true)
    }

    fun close() {
        inferenceEngine?.close()
        inferenceEngine = null
    }
}
