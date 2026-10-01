package com.example.ui.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.PidayabAccent
import com.example.ui.theme.PidayabPrimary
import java.util.Locale
import kotlin.random.Random

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    TRANSCRIBED,
    ERROR
}

@Composable
fun VoiceSearchDialog(
    onDismiss: () -> Unit,
    onVoiceTranscribed: (text: String) -> Unit
) {
    val context = LocalContext.current

    var voiceState by remember { mutableStateOf(VoiceState.IDLE) }
    var transcribedText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var soundLevel by remember { mutableFloatStateOf(0f) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            errorMessage = "برای جستجوی صوتی نیاز به دسترسی میکروفون است."
            voiceState = VoiceState.ERROR
        }
    }

    // Speech recognizer management
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val startListening = {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                try {
                    speechRecognizer?.destroy()
                    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                    speechRecognizer = recognizer

                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa-IR")
                        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "fa-IR")
                        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    }

                    recognizer.setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            voiceState = VoiceState.LISTENING
                            errorMessage = null
                        }

                        override fun onBeginningOfSpeech() {
                            voiceState = VoiceState.LISTENING
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            soundLevel = (rmsdB + 2f).coerceIn(0f, 10f)
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            voiceState = VoiceState.PROCESSING
                        }

                        override fun onError(error: Int) {
                            val msg = when (error) {
                                SpeechRecognizer.ERROR_NO_MATCH -> "صدایی تشخیص داده نشد. لطفاً دوباره امتحان کنید."
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "زمان صحبت به پایان رسید."
                                SpeechRecognizer.ERROR_NETWORK -> "خطای اتصال به اینترنت برای تبدیل صدا."
                                else -> "امکان پردازش صوتی در این لحظه میسر نشد."
                            }
                            errorMessage = msg
                            voiceState = VoiceState.ERROR
                        }

                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                transcribedText = matches[0]
                                voiceState = VoiceState.TRANSCRIBED
                            } else {
                                errorMessage = "صدایی دریافت نشد."
                                voiceState = VoiceState.ERROR
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!partial.isNullOrEmpty()) {
                                transcribedText = partial[0]
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })

                    voiceState = VoiceState.LISTENING
                    recognizer.startListening(intent)
                } catch (e: Exception) {
                    errorMessage = "سرویس گفتار در دسترس نیست؛ می‌توانید از نمونه‌های زیر استفاده کنید."
                    voiceState = VoiceState.ERROR
                }
            } else {
                // Speech recognition not installed on device
                errorMessage = "سرویس تشخیص گفتار بر روی این دستگاه فعال نیست؛ می‌توانید از نمونه‌های آماده یا تایپ صوتی استفاده نمایید."
                voiceState = VoiceState.IDLE
            }
        }
    }

    DisposableEffect(Unit) {
        if (hasPermission) {
            startListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (e: Exception) {}
        }
    }

    AlertDialog(
        onDismissRequest = {
            speechRecognizer?.stopListening()
            onDismiss()
        },
        modifier = Modifier.testTag("voice_search_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = PidayabPrimary
                    )
                    Text(
                        text = "جستجوی صوتی پیدایاب",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "بستن", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (voiceState) {
                        VoiceState.LISTENING -> "در حال شنیدن... کالا یا خودروی مورد نظرتان را توصیف کنید."
                        VoiceState.PROCESSING -> "در حال تبدیل گفتار به متن و استخراج معیارها..."
                        VoiceState.TRANSCRIBED -> "متن دریافت شد؛ برای شروع جستجو تأیید کنید:"
                        VoiceState.ERROR -> errorMessage ?: "خطایی رخ داد."
                        VoiceState.IDLE -> "دکمه میکروفون را لمس کنید یا از نمونه‌های زیر انتخاب فرمایید."
                    },
                    fontSize = 13.sp,
                    color = when (voiceState) {
                        VoiceState.ERROR -> MaterialTheme.colorScheme.error
                        VoiceState.LISTENING -> PidayabPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Audio Waveform & Mic Button
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            if (voiceState == VoiceState.LISTENING)
                                PidayabPrimary.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            if (voiceState == VoiceState.LISTENING) {
                                speechRecognizer?.stopListening()
                                voiceState = VoiceState.PROCESSING
                            } else {
                                startListening()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (voiceState == VoiceState.LISTENING) {
                        PulsingAudioWaves(level = soundLevel)
                    }

                    Icon(
                        imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "میکروفون",
                        tint = if (voiceState == VoiceState.LISTENING) PidayabPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Transcribed Text Display or Editable Text
                if (transcribedText.isNotBlank()) {
                    OutlinedTextField(
                        value = transcribedText,
                        onValueChange = { transcribedText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_transcribed_input"),
                        label = { Text("متن تشخیص داده شده") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick vehicle and item examples for instant voice simulation/querying
                Text(
                    text = "یا انتخاب سریع عبارات گفتاری نمونه:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(6.dp))

                val samplePrompts = listOf(
                    "یه کرولا کراس هیبرید ۲۰۲۵ زیر ۷ میلیارد، ترجیحاً تبریز" to "خودرو",
                    "گوشی سامسونگ اس ۲۴ اولترا ۵۱۲ گیگ مشکی" to "کالا",
                    "پژو ۲۰۷ دنده‌ای صفر کیلومتر سفید تهران" to "خودرو",
                    "کفش ورزشی نایک اورجینال زوم سایز ۴۲" to "کالا"
                )

                samplePrompts.forEach { (prompt, cat) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                transcribedText = prompt
                                voiceState = VoiceState.TRANSCRIBED
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (cat == "خودرو") PidayabPrimary.copy(alpha = 0.2f) else PidayabAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    color = if (cat == "خودرو") PidayabPrimary else PidayabAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (transcribedText.isNotBlank()) {
                        speechRecognizer?.stopListening()
                        onVoiceTranscribed(transcribedText)
                    } else if (voiceState == VoiceState.IDLE) {
                        startListening()
                    }
                },
                enabled = transcribedText.isNotBlank() || voiceState == VoiceState.IDLE,
                colors = ButtonDefaults.buttonColors(containerColor = PidayabPrimary),
                modifier = Modifier.testTag("confirm_voice_search_btn")
            ) {
                Text(
                    text = if (transcribedText.isNotBlank()) "جستجوی عبارت صوتی" else "شروع ضبط",
                    color = Color(0xFF00382F),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun PulsingAudioWaves(level: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barCount = 11
        val barWidth = 4.dp.toPx()
        val spacing = 5.dp.toPx()
        val totalWidth = barCount * barWidth + (barCount - 1) * spacing
        val startX = (size.width - totalWidth) / 2f
        val centerY = size.height / 2f

        for (i in 0 until barCount) {
            val factor = 1f - kotlin.math.abs(i - barCount / 2f) / (barCount / 2f)
            val barHeight = ((20.dp.toPx() + (level * 5.dp.toPx())) * factor * animProgress).coerceAtLeast(8.dp.toPx())
            val x = startX + i * (barWidth + spacing)
            val y = centerY - barHeight / 2f

            drawRoundRect(
                color = Color(0xFF00BFA5).copy(alpha = 0.8f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
