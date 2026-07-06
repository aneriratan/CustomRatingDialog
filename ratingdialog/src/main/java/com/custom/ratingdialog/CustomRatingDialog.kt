package com.custom.ratingdialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView

/**
 * A lightweight, dependency-free custom rating dialog.
 *
 * Build and show it with the [Builder]:
 *
 * ```
 * CustomRatingDialog.Builder(context)
 *     .title("Enjoying the app?")
 *     .message("Let us know how we're doing")
 *     .onRatingSubmitted { rating -> /* handle rating */ }
 *     .show()
 * ```
 */
class CustomRatingDialog private constructor(
    context: Context,
    private val config: Config,
) : Dialog(context) {

    /** Immutable configuration for a single dialog instance. */
    class Config(
        val title: String,
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String,
        val initialRating: Float,
        val onRatingSubmitted: (Float) -> Unit,
        val onDismissed: () -> Unit,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_custom_rating)

        val titleView = findViewById<TextView>(R.id.rating_title)
        val messageView = findViewById<TextView>(R.id.rating_message)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val positiveButton = findViewById<Button>(R.id.rating_positive)
        val negativeButton = findViewById<Button>(R.id.rating_negative)

        titleView.text = config.title
        messageView.text = config.message
        positiveButton.text = config.positiveButtonText
        negativeButton.text = config.negativeButtonText
        ratingBar.rating = config.initialRating

        // The positive button is only meaningful once at least one star is set.
        positiveButton.isEnabled = ratingBar.rating > 0f
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            positiveButton.isEnabled = rating > 0f
        }

        positiveButton.setOnClickListener {
            config.onRatingSubmitted(ratingBar.rating)
            dismiss()
        }
        negativeButton.setOnClickListener { dismiss() }

        setOnDismissListener { config.onDismissed() }
    }

    /** Fluent builder for [CustomRatingDialog]. */
    class Builder(private val context: Context) {
        private var title: String = "Rate this app"
        private var message: String = "Tap a star to rate your experience"
        private var positiveButtonText: String = "Submit"
        private var negativeButtonText: String = "Not now"
        private var initialRating: Float = 0f
        private var cancelable: Boolean = true
        private var onRatingSubmitted: (Float) -> Unit = {}
        private var onDismissed: () -> Unit = {}

        fun title(title: String) = apply { this.title = title }

        fun message(message: String) = apply { this.message = message }

        fun positiveButtonText(text: String) = apply { this.positiveButtonText = text }

        fun negativeButtonText(text: String) = apply { this.negativeButtonText = text }

        fun initialRating(rating: Float) = apply { this.initialRating = rating }

        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

        fun onRatingSubmitted(listener: (Float) -> Unit) = apply {
            this.onRatingSubmitted = listener
        }

        fun onDismissed(listener: () -> Unit) = apply { this.onDismissed = listener }

        fun build(): CustomRatingDialog {
            val config = Config(
                title = title,
                message = message,
                positiveButtonText = positiveButtonText,
                negativeButtonText = negativeButtonText,
                initialRating = initialRating,
                onRatingSubmitted = onRatingSubmitted,
                onDismissed = onDismissed,
            )
            return CustomRatingDialog(context, config).apply {
                setCancelable(cancelable)
            }
        }

        /** Builds the dialog and immediately shows it, returning the instance. */
        fun show(): CustomRatingDialog = build().also { it.show() }
    }
}
