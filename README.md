# Gemma Moto G24 - App On-Device (100% Offline)

Proyecto nativo de Android configurado para ejecutar **Gemma** en el **Motorola Moto G24** sin servidores externos, sin internet y sin dependencias de terceros.

---

## Estructura del Proyecto Creado
* `app/src/main/java/com/ejemplo/gemmamotog24/GemmaEngine.kt`: Motor de inferencia MediaPipe optimizado para CPU.
* `app/src/main/java/com/ejemplo/gemmamotog24/MainActivity.kt`: Interfaz de chat en tiempo real (token streaming).
* `app/src/main/res/layout/activity_main.xml`: Interfaz visual.
* `app/build.gradle.kts`: Dependencia oficial de `com.google.mediapipe:tasks-genai:0.10.14`.

---

## Pasos para Compilar y Probar

### 1. Abrir en Android Studio
Copia o transfiere la carpeta `GemmaMotoG24` a tu computadora y abre el proyecto en Android Studio.

### 2. Descargar el Modelo Gemma
Descarga la variante INT4 para CPU desde Kaggle de Google:
* Archivo: `gemma-2b-it-cpu-int4.bin`
* Enlace oficial: [Kaggle - Google Gemma](https://www.kaggle.com/models/google/gemma-2) (sección TFLite/MediaPipe).

### 3. Instalar y Colocar el Modelo en tu Moto G24
1. Compila e instala la app en tu Moto G24 (`Run 'app'`).
2. Una vez instalada, transfiere el archivo `gemma-2b-it-cpu-int4.bin` a la siguiente ruta de tu Moto G24:
   ```
   /storage/emulated/0/Android/data/com.ejemplo.gemmamotog24/files/gemma-2b-it-cpu-int4.bin
   ```
3. Abre la app: detectará el archivo, cargará el modelo en la RAM del Moto G24 y podrás conversar completamente offline.
