package com.pulsefit.app.nd

object PdaLanguage {

    private val transformations = mapOf(
        "Start Workout" to "Ready when you are",
        "GO" to "Let's go?",
        "Begin" to "Would you like to begin?",
        "Start" to "Ready to start?",
        "Do it now" to "Whenever you're ready",
        "Push harder" to "You could try pushing a bit more",
        "Keep going" to "You're doing great if you want to continue",
        "Don't stop" to "It's okay to keep going or take a break",
        "You must" to "You might want to",
        "You need to" to "It could help to",
        "Complete your workout" to "Finish up when you're ready",
        "Hit your goal" to "Your goal is within reach",
        "You should" to "You could try",
        "Time to work out" to "Your workout is ready if you are",
        "Get moving" to "Movement is available whenever you'd like",
        "Stop being lazy" to "Rest is valid too",
        "No excuses" to "Whatever you choose is fine",
        "Burn more" to "There's more to explore if you want",
        "Earn your rest" to "Rest whenever you need it",
        "Work harder" to "You could increase intensity if it feels right",
        "Pick up the pace" to "Try a slightly faster pace if you'd like",
        "End Workout" to "Ready to wrap up?",
        "Quit" to "Step away?",
        "Cancel" to "Change your mind?",
        "Submit" to "Save this?",
        "Confirm" to "Does this look right?",
        "Delete" to "Remove this?"
    )

    fun transform(text: String, pdaEnabled: Boolean): String {
        if (!pdaEnabled) return text
        return transformations[text] ?: transformSentence(text)
    }

    private fun transformSentence(text: String): String {
        var result = text
        val sentenceSwaps = listOf(
            "You must" to "You might want to",
            "You need to" to "It could help to",
            "You should" to "You could try",
            "Make sure to" to "Consider",
            "Don't forget to" to "You might want to",
            "You have to" to "You could"
        )
        for ((imperative, gentle) in sentenceSwaps) {
            result = result.replace(imperative, gentle, ignoreCase = true)
        }
        return result
    }
}
