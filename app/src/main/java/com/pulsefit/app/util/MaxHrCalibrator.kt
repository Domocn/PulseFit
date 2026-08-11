package com.pulsefit.app.util

object MaxHrCalibrator {

    private const val EWMA_ALPHA = 0.3f
    private const val MIN_WORKOUTS_FOR_CALIBRATION = 3

    /**
     * Calibrate max HR using exponential weighted moving average.
     * Only updates upward (never reduces calibrated max).
     * Requires at least [MIN_WORKOUTS_FOR_CALIBRATION] workouts before overriding age formula.
     *
     * @param observedMaxHr The highest HR observed in the latest workout
     * @param currentCalibratedMax The current calibrated max HR (null if never calibrated)
     * @param ageFormulaMax The age-based formula max HR (220 - age)
     * @param workoutCount Total number of completed workouts
     * @return The new calibrated max HR, or null if not enough data yet
     */
    fun calibrate(
        observedMaxHr: Int,
        currentCalibratedMax: Int?,
        ageFormulaMax: Int,
        workoutCount: Int
    ): Int? {
        if (workoutCount < MIN_WORKOUTS_FOR_CALIBRATION) return currentCalibratedMax

        val baseline = currentCalibratedMax ?: ageFormulaMax

        // EWMA smoothing
        val ewma = (EWMA_ALPHA * observedMaxHr + (1 - EWMA_ALPHA) * baseline).toInt()

        // Only update upward
        return maxOf(baseline, ewma)
    }

    /**
     * Get the effective max HR for zone calculation.
     * Returns calibrated max if available, otherwise falls back to age formula.
     */
    fun getEffectiveMaxHr(calibratedMaxHr: Int?, age: Int): Int {
        return calibratedMaxHr ?: (220 - age)
    }
}
