package com.pulsefit.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
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
 * Generates PDF workout reports using Android's built-in PdfDocument API.
 * No external dependencies required.
 *
 * Report structure:
 *   Page 1: Cover page — workout summary, stats, date
 *   Page 2+: Exercise log — per-exercise breakdown with sets, reps, weight, volume
 *   Final pages: Personal records, body measurement trends (if data provided)
 */
class PdfReportGenerator(private val context: Context) {

    // A4 at 72 DPI
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 48f
    private val contentWidth = pageWidth - margin * 2

    // Colours
    private val colorPrimary = 0xFFE94560.toInt()
    private val colorDark = 0xFF1A1A2E.toInt()
    private val colorText = 0xFF1A1A2E.toInt()
    private val colorTextSecondary = 0xFF666680.toInt()
    private val colorDivider = 0xFFE0E0E8.toInt()
    private val colorAccent = 0xFF0F3460.toInt()
    private val colorGold = 0xFFD4A017.toInt()

    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        .withZone(ZoneId.systemDefault())
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    data class ReportData(
        val workout: Workout,
        val exerciseLogs: List<ExerciseLog> = emptyList(),
        val personalRecords: List<PersonalRecord> = emptyList(),
        val bodyWeightHistory: List<Pair<Long, Float>> = emptyList(), // timestamp to weightKg
        val bodyFatHistory: List<Pair<Long, Float>> = emptyList()     // timestamp to bodyFat%
    )

    fun generate(data: ReportData): File {
        val document = PdfDocument()
        var currentPage = 0

        // Page 1: Cover
        val coverPage = document.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++currentPage).create()
        )
        drawCoverPage(coverPage.canvas, data)
        document.finishPage(coverPage)

        // Page 2+: Exercise logs
        if (data.exerciseLogs.isNotEmpty()) {
            val exercisePage = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++currentPage).create()
            )
            drawExerciseLogPage(exercisePage.canvas, data)
            document.finishPage(exercisePage)
        }

        // Personal Records page
        if (data.personalRecords.isNotEmpty()) {
            val prPage = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++currentPage).create()
            )
            drawPersonalRecordsPage(prPage.canvas, data)
            document.finishPage(prPage)
        }

        // Body measurement trends page
        if (data.bodyWeightHistory.isNotEmpty() || data.bodyFatHistory.isNotEmpty()) {
            val bodyPage = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++currentPage).create()
            )
            drawBodyMeasurementsPage(bodyPage.canvas, data)
            document.finishPage(bodyPage)
        }

        // Save to file
        val dir = File(context.cacheDir, "pdf_reports")
        dir.mkdirs()
        val file = File(dir, "pulsefit_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        return file
    }

    fun getShareUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // ─── Cover Page ───────────────────────────────────────────────

    private fun drawCoverPage(canvas: Canvas, data: ReportData) {
        val w = data.workout
        var y = margin

        // Header bar
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorDark }
        canvas.drawRoundRect(
            RectF(margin, y, pageWidth - margin, y + 120f), 16f, 16f, headerPaint
        )

        // Title
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val title = if (w.workoutMode == "STRENGTH") "Strength Workout Report" else "Workout Report"
        canvas.drawText(title, margin + 24f, y + 50f, titlePaint)

        // Subtitle
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFB0B0C0.toInt()
            textSize = 18f
        }
        val dateStr = "${dateFormatter.format(w.startTime)} at ${timeFormatter.format(w.startTime)}"
        canvas.drawText(dateStr, margin + 24f, y + 80f, subtitlePaint)

        // PulseFit branding
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorPrimary
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("PulseFit", pageWidth - margin - 24f - brandPaint.measureText("PulseFit"), y + 50f, brandPaint)

        y += 160f

        // Stats grid
        val stats = buildList {
            add("Duration" to formatDuration(w.durationSeconds))
            if (w.workoutMode == "STRENGTH") {
                w.totalVolumeKg?.let { add("Total Volume" to "${it.toInt()} kg") }
                w.totalSets?.let { add("Total Sets" to "$it") }
                if (data.exerciseLogs.isNotEmpty()) {
                    add("Exercises" to "${data.exerciseLogs.size}")
                }
            } else {
                add("Burn Points" to "${w.burnPoints}")
                if (w.averageHeartRate > 0) add("Avg HR" to "${w.averageHeartRate} bpm")
                if (w.maxHeartRate > 0) add("Max HR" to "${w.maxHeartRate} bpm")
                w.estimatedCalories?.let { add("Calories" to "$it kcal") }
            }
            if (w.xpEarned > 0) add("XP Earned" to "+${w.xpEarned}")
        }

        val cols = 2
        val rows = (stats.size + cols - 1) / cols
        val cellWidth = contentWidth / cols
        val cellHeight = 80f

        val statValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorPrimary
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val statLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 14f
        }
        val cellBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFF5F5FA.toInt() }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val idx = row * cols + col
                if (idx >= stats.size) break

                val cx = margin + col * cellWidth
                val cy = y + row * (cellHeight + 8f)

                canvas.drawRoundRect(
                    RectF(cx, cy, cx + cellWidth - 8f, cy + cellHeight),
                    12f, 12f, cellBgPaint
                )

                val (label, value) = stats[idx]
                val valWidth = statValuePaint.measureText(value)
                val labelWidth = statLabelPaint.measureText(label)

                canvas.drawText(
                    value,
                    cx + (cellWidth - 8f) / 2f - valWidth / 2f,
                    cy + 36f,
                    statValuePaint
                )
                canvas.drawText(
                    label,
                    cx + (cellWidth - 8f) / 2f - labelWidth / 2f,
                    cy + 60f,
                    statLabelPaint
                )
            }
        }

        y += rows * (cellHeight + 8f) + 32f

        // Workout mode badge
        val modePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorAccent
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val modeText = "Mode: ${w.workoutMode}"
        canvas.drawText(modeText, margin, y, modePaint)

        // Notes
        w.notes?.let { notes ->
            y += 32f
            val notesTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorText
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Notes", margin, y, notesTitlePaint)
            y += 24f
            val notesPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorTextSecondary
                textSize = 14f
            }
            // Simple text wrapping
            val words = notes.split(" ")
            var line = ""
            var lineY = y
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (notesPaint.measureText(testLine) > contentWidth) {
                    canvas.drawText(line, margin, lineY, notesPaint)
                    lineY += 20f
                    line = word
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, margin, lineY, notesPaint)
            }
        }

        // Footer
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        val footer = "Generated by PulseFit • pulsefit.app"
        canvas.drawText(
            footer,
            (pageWidth - footerPaint.measureText(footer)) / 2f,
            pageHeight - margin,
            footerPaint
        )

        // Page number
        drawPageNumber(canvas, 1)
    }

    // ─── Exercise Log Page ────────────────────────────────────────

    private fun drawExerciseLogPage(canvas: Canvas, data: ReportData) {
        var y = margin

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDark
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Exercise Log", margin, y + 24f, titlePaint)
        y += 48f

        // Column headers
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val colExercise = margin
        val colSets = margin + contentWidth * 0.35f
        val colWeight = margin + contentWidth * 0.55f
        val colVolume = margin + contentWidth * 0.75f

        canvas.drawText("EXERCISE", colExercise, y, headerPaint)
        canvas.drawText("SETS", colSets, y, headerPaint)
        canvas.drawText("MAX WT", colWeight, y, headerPaint)
        canvas.drawText("VOLUME", colVolume, y, headerPaint)

        y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, Paint().apply { color = colorDivider; strokeWidth = 1f })
        y += 16f

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorText
            textSize = 14f
        }
        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 12f
        }
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorPrimary
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val rowBgEven = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFF8F8FC.toInt() }

        data.exerciseLogs.forEachIndexed { index, log ->
            // Row background
            if (index % 2 == 0) {
                canvas.drawRect(
                    RectF(margin - 8f, y - 8f, pageWidth - margin + 8f, y + 40f),
                    rowBgEven
                )
            }

            // Muscle group tag
            if (log.primaryMuscleGroup.isNotEmpty()) {
                val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = colorAccent
                    textSize = 10f
                }
                canvas.drawText(log.primaryMuscleGroup, colExercise, y, tagPaint)
                canvas.drawText(log.exerciseName, colExercise, y + 18f, namePaint)
            } else {
                canvas.drawText(log.exerciseName, colExercise, y + 14f, namePaint)
            }

            // Sets
            val setsText = "${log.setsCompleted}/${log.setsPlanned}"
            canvas.drawText(setsText, colSets, y + 14f, detailPaint)

            // Max weight
            val weightText = if (log.maxWeightKg != null && log.maxWeightKg > 0) {
                "${log.maxWeightKg.toInt()} kg"
            } else "—"
            canvas.drawText(weightText, colWeight, y + 14f, valuePaint)

            // Volume
            canvas.drawText("${log.totalVolumeKg.toInt()} kg", colVolume, y + 14f, valuePaint)

            // Best set detail
            val bestDetail = buildString {
                log.bestSetReps?.let { append("Best: ${it} reps") }
                log.bestSetWeightKg?.let { if (it > 0) append(" @ ${it.toInt()}kg") }
                log.averageRpe?.let { append(" • RPE ${"%.1f".format(it)}") }
            }
            if (bestDetail.isNotEmpty()) {
                canvas.drawText(bestDetail, colExercise, y + 34f, detailPaint)
            }

            y += 52f

            // Page break if near bottom
            if (y > pageHeight - 100f && index < data.exerciseLogs.size - 1) {
                drawPageNumber(canvas, 2)
                // Note: PdfDocument doesn't support multi-page from a single canvas easily
                // In practice, we'd need to track remaining items and start a new page
                // For simplicity, we'll just stop drawing if we run out of space
                return@forEachIndexed
            }
        }

        drawPageNumber(canvas, 2)
    }

    // ─── Personal Records Page ────────────────────────────────────

    private fun drawPersonalRecordsPage(canvas: Canvas, data: ReportData) {
        var y = margin

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDark
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Personal Records", margin, y + 24f, titlePaint)
        y += 48f

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorText
            textSize = 16f
        }
        val prPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorGold
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 13f
        }
        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 12f
        }

        data.personalRecords.forEachIndexed { index, pr ->
            // Card background
            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFAFAFE.toInt() }
            canvas.drawRoundRect(
                RectF(margin, y, pageWidth - margin, y + 72f),
                12f, 12f, cardPaint
            )

            // Gold accent bar
            val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorGold }
            canvas.drawRoundRect(
                RectF(margin, y, margin + 6f, y + 72f),
                3f, 3f, accentPaint
            )

            canvas.drawText(pr.exerciseName, margin + 20f, y + 28f, namePaint)
            canvas.drawText(
                "Based on ${pr.basedOnWeightKg.toInt()}kg × ${pr.basedOnReps} reps (${pr.formula})",
                margin + 20f,
                y + 50f,
                detailPaint
            )

            val prText = "1RM: ${pr.estimatedOneRmKg.toInt()}kg"
            val prWidth = prPaint.measureText(prText)
            canvas.drawText(prText, pageWidth - margin - 20f - prWidth, y + 32f, prPaint)

            val dateText = dateFormatter.format(pr.timestamp)
            canvas.drawText(dateText, pageWidth - margin - 20f - datePaint.measureText(dateText), y + 54f, datePaint)

            y += 88f
        }

        drawPageNumber(canvas, 3)
    }

    // ─── Body Measurements Page ───────────────────────────────────

    private fun drawBodyMeasurementsPage(canvas: Canvas, data: ReportData) {
        var y = margin

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDark
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Body Measurements", margin, y + 24f, titlePaint)
        y += 48f

        if (data.bodyWeightHistory.isNotEmpty()) {
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorText
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Weight Trend", margin, y, subtitlePaint)
            y += 24f

            drawTrendChart(
                canvas = canvas,
                data = data.bodyWeightHistory,
                label = "kg",
                chartTop = y,
                chartHeight = 160f
            )
            y += 180f
        }

        if (data.bodyFatHistory.isNotEmpty()) {
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorText
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Body Fat % Trend", margin, y, subtitlePaint)
            y += 24f

            drawTrendChart(
                canvas = canvas,
                data = data.bodyFatHistory,
                label = "%",
                chartTop = y,
                chartHeight = 160f
            )
        }

        drawPageNumber(canvas, 4)
    }

    private fun drawTrendChart(
        canvas: Canvas,
        data: List<Pair<Long, Float>>,
        label: String,
        chartTop: Float,
        chartHeight: Float
    ) {
        if (data.size < 2) return

        val chartLeft = margin + 40f
        val chartRight = pageWidth - margin
        val chartBottom = chartTop + chartHeight

        val values = data.map { it.second }
        val minVal = values.min() * 0.95f
        val maxVal = values.max() * 1.05f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        // Axes
        val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDivider
            strokeWidth = 1f
        }
        canvas.drawLine(chartLeft, chartTop, chartLeft, chartBottom, axisPaint)
        canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, axisPaint)

        // Y-axis labels
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 10f
        }
        for (i in 0..4) {
            val valY = minVal + range * i / 4f
            val y = chartBottom - (valY - minVal) / range * chartHeight
            canvas.drawText("${"%.1f".format(valY)}$label", margin, y + 4f, labelPaint)
            // Grid line
            canvas.drawLine(chartLeft, y, chartRight, y, Paint().apply {
                color = 0xFFF0F0F4.toInt()
                strokeWidth = 0.5f
            })
        }

        // Data line
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorPrimary
            strokeWidth = 3f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorPrimary
            style = Paint.Style.FILL
        }

        val chartWidth = chartRight - chartLeft
        val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)

        data.forEachIndexed { index, (_, value) ->
            val x = chartLeft + index * stepX
            val y = chartBottom - (value - minVal) / range * chartHeight

            if (index > 0) {
                val prevValue = data[index - 1].second
                val prevX = chartLeft + (index - 1) * stepX
                val prevY = chartBottom - (prevValue - minVal) / range * chartHeight
                canvas.drawLine(prevX, prevY, x, y, linePaint)
            }
            canvas.drawCircle(x, y, 4f, dotPaint)
        }

        // X-axis date labels (first and last)
        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 10f
        }
        val firstDate = java.text.SimpleDateFormat("d/M", java.util.Locale.getDefault())
            .format(java.util.Date(data.first().first))
        val lastDate = java.text.SimpleDateFormat("d/M", java.util.Locale.getDefault())
            .format(java.util.Date(data.last().first))
        canvas.drawText(firstDate, chartLeft, chartBottom + 16f, datePaint)
        val lastWidth = datePaint.measureText(lastDate)
        canvas.drawText(lastDate, chartRight - lastWidth, chartBottom + 16f, datePaint)
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private fun drawPageNumber(canvas: Canvas, page: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorTextSecondary
            textSize = 11f
        }
        val text = "Page $page"
        canvas.drawText(
            text,
            (pageWidth - paint.measureText(text)) / 2f,
            pageHeight - margin,
            paint
        )
    }

    private fun formatDuration(totalSeconds: Int): String {
        val mins = totalSeconds / 60
        val secs = totalSeconds % 60
        return "${mins}:${secs.toString().padStart(2, '0')}"
    }
}
