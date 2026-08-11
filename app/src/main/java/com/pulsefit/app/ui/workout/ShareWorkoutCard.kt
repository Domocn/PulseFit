package com.pulsefit.app.ui.workout

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.pulsefit.app.domain.model.ExerciseLog
import com.pulsefit.app.domain.model.PersonalRecord
import com.pulsefit.app.domain.model.Workout
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Renders a stylised workout summary card to a Bitmap for sharing.
 * Uses Android Canvas API for precise control over layout and typography.
 */
object WorkoutCardRenderer {

    private const val CARD_WIDTH = 1080
    private const val CARD_PADDING = 48f
    private const val CORNER_RADIUS = 24f

    // PulseFit brand colours
    private const val BG_DARK = 0xFF1A1A2E.toInt()
    private const val BG_CARD = 0xFF16213E.toInt()
    private const val ACCENT = 0xFFE94560.toInt()
    private const val ACCENT_SECONDARY = 0xFF0F3460.toInt()
    private const val TEXT_PRIMARY = 0xFFFFFFFF.toInt()
    private const val TEXT_SECONDARY = 0xFFB0B0C0.toInt()
    private const val TEXT_MUTED = 0xFF707088.toInt()
    private const val PR_GOLD = 0xFFFFD700.toInt()
    private const val DIVIDER = 0xFF2A2A4A.toInt()

    fun renderToBitmap(
        workout: Workout,
        exerciseLogs: List<ExerciseLog>,
        personalRecords: List<PersonalRecord>,
        context: Context
    ): Bitmap {
        val cardHeight = calculateCardHeight(exerciseLogs, personalRecords)
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, cardHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BG_DARK }
        canvas.drawRoundRect(
            RectF(0f, 0f, CARD_WIDTH.toFloat(), cardHeight.toFloat()),
            CORNER_RADIUS, CORNER_RADIUS, bgPaint
        )

        var y = CARD_PADDING

        // Header: date
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'at' HH:mm")
            .withZone(ZoneId.systemDefault())
        val dateText = workout.startTime.let { dateFormatter.format(it) }

        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_MUTED
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        canvas.drawText(dateText, CARD_PADDING, y + 28f, datePaint)
        y += 52f

        // Title
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_PRIMARY
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val title = if (workout.workoutMode == "STRENGTH") "Strength Workout" else "PulseFit Workout"
        canvas.drawText(title, CARD_PADDING, y + 52f, titlePaint)
        y += 80f

        // Stats row
        y = drawStatsRow(canvas, workout, exerciseLogs, y)
        y += 24f

        // Divider
        canvas.drawLine(
            CARD_PADDING, y, CARD_WIDTH - CARD_PADDING, y,
            Paint().apply { color = DIVIDER; strokeWidth = 2f }
        )
        y += 32f

        // Exercise list
        if (exerciseLogs.isNotEmpty()) {
            y = drawExerciseList(canvas, exerciseLogs, y)
            y += 16f
        }

        // Personal Records
        if (personalRecords.isNotEmpty()) {
            y = drawPersonalRecords(canvas, personalRecords, y)
            y += 16f
        }

        // Footer
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_MUTED
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        val footerText = "Tracked with PulseFit • pulsefit.app"
        val footerWidth = footerPaint.measureText(footerText)
        canvas.drawText(
            footerText,
            (CARD_WIDTH - footerWidth) / 2f,
            cardHeight - CARD_PADDING + 12f,
            footerPaint
        )

        return bitmap
    }

    private fun calculateCardHeight(
        exerciseLogs: List<ExerciseLog>,
        personalRecords: List<PersonalRecord>
    ): Int {
        var height = 280f // header + stats + padding
        if (exerciseLogs.isNotEmpty()) {
            height += 48f // section title
            height += exerciseLogs.size * 72f // each exercise row
        }
        if (personalRecords.isNotEmpty()) {
            height += 48f // section title
            height += personalRecords.size * 56f // each PR row
        }
        height += 80f // footer
        return height.toInt().coerceAtLeast(600)
    }

    private fun drawStatsRow(
        canvas: Canvas,
        workout: Workout,
        exerciseLogs: List<ExerciseLog>,
        startY: Float
    ): Float {
        val stats = mutableListOf<Pair<String, String>>()

        // Duration
        val mins = workout.durationSeconds / 60
        val secs = workout.durationSeconds % 60
        stats.add("Duration" to "${mins}:${secs.toString().padStart(2, '0')}")

        // Exercises
        if (exerciseLogs.isNotEmpty()) {
            stats.add("Exercises" to "${exerciseLogs.size}")
        }

        // Sets
        val totalSets = exerciseLogs.sumOf { it.setsCompleted }
        if (totalSets > 0) {
            stats.add("Sets" to "$totalSets")
        }

        // Volume
        val totalVolume = exerciseLogs.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
        if (totalVolume > 0) {
            stats.add("Volume" to "${totalVolume.toInt()}kg")
        }

        // Burn Points (always show if present)
        if (workout.burnPoints > 0 && workout.workoutMode != "STRENGTH") {
            stats.add("Burn Pts" to "${workout.burnPoints}")
        }

        val statWidth = (CARD_WIDTH - CARD_PADDING * 2) / stats.size.coerceAtLeast(1)
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ACCENT
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_SECONDARY
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Draw stat card background
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BG_CARD }
        canvas.drawRoundRect(
            RectF(CARD_PADDING, startY, CARD_WIDTH - CARD_PADDING, startY + 100f),
            16f, 16f, cardPaint
        )

        stats.forEachIndexed { index, (label, value) ->
            val cx = CARD_PADDING + statWidth * index + statWidth / 2f
            val valueWidth = valuePaint.measureText(value)
            val labelWidth = labelPaint.measureText(label)

            canvas.drawText(value, cx - valueWidth / 2f, startY + 42f, valuePaint)
            canvas.drawText(label, cx - labelWidth / 2f, startY + 76f, labelPaint)
        }

        return startY + 100f
    }

    private fun drawExerciseList(
        canvas: Canvas,
        exerciseLogs: List<ExerciseLog>,
        startY: Float
    ): Float {
        var y = startY

        // Section title
        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_SECONDARY
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("EXERCISES", CARD_PADDING, y + 28f, sectionPaint)
        y += 48f

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_PRIMARY
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_SECONDARY
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val weightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ACCENT
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val rowBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BG_CARD }

        exerciseLogs.forEach { log ->
            canvas.drawRoundRect(
                RectF(CARD_PADDING, y, CARD_WIDTH - CARD_PADDING, y + 64f),
                12f, 12f, rowBgPaint
            )

            // Exercise name
            canvas.drawText(log.exerciseName, CARD_PADDING + 16f, y + 28f, namePaint)

            // Sets x reps detail
            val detailText = buildString {
                append("${log.setsCompleted}/${log.setsPlanned} sets")
                log.bestSetReps?.let { append(" • up to ${it} reps") }
            }
            canvas.drawText(detailText, CARD_PADDING + 16f, y + 54f, detailPaint)

            // Weight / volume on the right
            val volumeText = if (log.maxWeightKg != null && log.maxWeightKg > 0) {
                "${log.maxWeightKg.toInt()}kg max"
            } else {
                "${log.totalVolumeKg.toInt()}kg total"
            }
            val volWidth = weightPaint.measureText(volumeText)
            canvas.drawText(
                volumeText,
                CARD_WIDTH - CARD_PADDING - 16f - volWidth,
                y + 38f,
                weightPaint
            )

            y += 76f
        }

        return y
    }

    private fun drawPersonalRecords(
        canvas: Canvas,
        personalRecords: List<PersonalRecord>,
        startY: Float
    ): Float {
        var y = startY

        // Section title
        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = PR_GOLD
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("★ PERSONAL RECORDS", CARD_PADDING, y + 28f, sectionPaint)
        y += 48f

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_PRIMARY
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val prPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = PR_GOLD
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = TEXT_SECONDARY
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        personalRecords.forEach { pr ->
            // PR badge dot
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = PR_GOLD }
            canvas.drawCircle(CARD_PADDING + 16f, y + 20f, 6f, dotPaint)

            canvas.drawText(pr.exerciseName, CARD_PADDING + 36f, y + 26f, namePaint)

            val prText = "1RM: ${pr.estimatedOneRmKg.toInt()}kg"
            val prWidth = prPaint.measureText(prText)
            canvas.drawText(
                prText,
                CARD_WIDTH - CARD_PADDING - 16f - prWidth,
                y + 26f,
                prPaint
            )

            val basedOnText = "(${pr.basedOnWeightKg.toInt()}kg × ${pr.basedOnReps} reps)"
            canvas.drawText(basedOnText, CARD_PADDING + 36f, y + 48f, detailPaint)

            y += 60f
        }

        return y
    }

    /**
     * Save bitmap to cache directory and return the file URI for sharing.
     */
    fun saveToCache(context: Context, bitmap: Bitmap): Uri {
        val cacheDir = File(context.cacheDir, "share_images")
        cacheDir.mkdirs()
        val file = File(cacheDir, "pulsefit_workout_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Create a share intent for the workout card image.
     */
    fun createShareIntent(context: Context, bitmap: Bitmap): Intent {
        val uri = saveToCache(context, bitmap)
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
