package com.example.rbclabs

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * Base class for all activities in the application.
 * Provides utility methods shared across activities, such as displaying a Snackbar.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Displays a Snackbar with a specified message.
     *
     * @param message The message to display in the Snackbar.
     * @param errorMessage True if this is an error message, false for success.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (errorMessage) R.color.colorSnackBarError else R.color.colorSnackBarSuccess
            )
        )
        snackbar.show()
    }
}
