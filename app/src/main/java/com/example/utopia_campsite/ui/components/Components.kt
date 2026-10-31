package com.example.utopia_campsite.ui.components
import android.R
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Gray rounded text field with a placeholder.
 *
 * @param value: the current text
 * @param onChange: called with the new text when the user types
 * @param placeholder: hint shown when the field is empty
 * @param modifier: optional modifier from the caller
 * @param keyboard: keyboard type, e.g. KeyboardType.Number
  * @param singleLine: false for multi-line fields like description
 * @param isError: true to show the error state
 */
@Composable
fun InputField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String ?= null,
    keyboard: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    singleLine: Boolean = true,
){
    val bg = Color(0xFFF1F1F1)
    TextField(
        value =value,
        onValueChange = onChange,
        placeholder = placeholder?.let{{Text(it, color = Color(0xff888888))}},
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = bg,
            unfocusedContainerColor = bg,
            errorContainerColor = bg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )

}

/**
 * Black rounded button with small white text.
 *
 * @param text: the label shown on the button
 * @param onClick: called when the button is tapped
 * @param modifier: optional modifier from the caller
 */
@Composable
fun BlackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, maxLines = 1)
    }
}