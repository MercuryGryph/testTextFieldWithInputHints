package cn.mercury9.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import cn.mercury9.decoratedText.DecoratedText
import cn.mercury9.decoratedText.InCardDecoratedText
import cn.mercury9.decoratedText.UndecoratedText
import cn.mercury9.demo.data.hintprovider.Hint
import cn.mercury9.demo.data.hintprovider.HintProvider
import cn.mercury9.demo.resources.Res
import cn.mercury9.demo.resources.keyboard_return_24px
import cn.mercury9.demo.resources.keyboard_tab_24px
import cn.mercury9.utils.compose.painter
import kotlin.math.min

@ExperimentalLayoutApi
@Composable
fun OutlinedTextFieldWithHint(
    value: String,
    onValueChange: (String) -> Unit,
    hintProvider: HintProvider,
    hintMaxShowingNumber: Int = 6,
    //region unchanged OutlinedTextField params
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
    //endregion unchanged OutlinedTextField params
) {
    var hintEnabled by remember { mutableStateOf(true) }

    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = value)) }

    fun isHintShouldEffect(): Boolean {
        return hintEnabled &&
                enabled
    }

    var selectedHint by remember { mutableStateOf(Hint.Empty) }
    var selectedHintIndex by remember { mutableStateOf(0) }
    var firstShowingHintIndex by remember { mutableStateOf(0) }

    val hints = hintProvider.getHintFrom(value, textFieldValue.selection)

    val lineHeight = textStyle.lineHeight.value

    SideEffect {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = value)
            selectedHint = Hint.Empty
            selectedHintIndex = 0
            firstShowingHintIndex = 0
            hintEnabled = true
        }
    }

    LaunchedEffect(selectedHintIndex) {
        if (selectedHintIndex < 0) selectedHintIndex = 0
        else if (selectedHintIndex > hints.size - 1) selectedHintIndex = hints.size - 1
        else {
            selectedHint = hints[selectedHintIndex]
            if (firstShowingHintIndex > selectedHintIndex) {
                firstShowingHintIndex = selectedHintIndex
            } else if (firstShowingHintIndex + hintMaxShowingNumber - 1 < selectedHintIndex) {
                firstShowingHintIndex = selectedHintIndex - hintMaxShowingNumber + 1
            }
        }
    }

    fun useHint() {
        if (hints.isEmpty()) return

        val hint = hints[selectedHintIndex]

        val res = textFieldValue.text
            .replaceRange(
                hint.textRange.start,
                hint.textRange.end,
                hint.hint
            )

        onValueChange(res)

        textFieldValue = textFieldValue.copy(
            selection = TextRange(hint.textRange.start + hint.hint.length),
            text = res
        )

        hintEnabled = false
    }

    fun handelKeyboard(event: KeyEvent): Boolean {
        if (!isHintShouldEffect() || hints.isEmpty()) return false
        if (event.type == KeyEventType.KeyDown) {
            when (event.key) {
                Key.Escape -> {
                    hintEnabled = false
                    return true
                }

                Key.Enter, Key.NumPadEnter -> {
                    if (
                        event.isAltPressed ||
                        event.isCtrlPressed ||
                        event.isMetaPressed ||
                        event.isShiftPressed
                    ) {
                        return false
                    }

                    useHint()
                    return true
                }

                Key.Tab -> {
                    if (
                        event.isAltPressed ||
                        event.isCtrlPressed ||
                        event.isMetaPressed
                    ) {
                        return false
                    } else if (event.isShiftPressed) {
                        selectedHintIndex -= 1
                    } else {
                        selectedHintIndex += 1
                    }
                    return true
                }
            }
        }
        return false
    }

    Box(
        Modifier
            .onPreviewKeyEvent {
                handelKeyboard(it)
            }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                hintEnabled = true
                selectedHintIndex = run {
                    val i = hints.indexOf(selectedHint)
                    if (i == -1) 0 else i
                }
                onValueChange(it.text)
            },
            //region unchanged OutlinedTextField params
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
            //endregion unchanged OutlinedTextField params
        )

        if (isHintShouldEffect() && hints.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, (lineHeight + 40).toInt()),
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(250.dp)
                ) {
                    Column {
                        Column(
                            modifier = Modifier
                                .padding(4.dp)
                        ) {
                            if (firstShowingHintIndex > 0) {
                                HintMoreToShow()
                            }

                            for (index in 0 until min(hintMaxShowingNumber, hints.size)) {
                                val selfIndex = firstShowingHintIndex + index
                                val selected = selfIndex == selectedHintIndex
                                HintItem(
                                    hint = hints[selfIndex].hint,
                                    selected = selected,
                                ) {
                                    if (selected) {
                                        useHint()
                                    } else {
                                        selectedHintIndex = selfIndex
                                    }
                                }
                            }

                            if (firstShowingHintIndex + hintMaxShowingNumber < hints.size) {
                                HintMoreToShow()
                            }
                        }
                        HorizontalDivider()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            HintOperations()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HintItem(
    hint: String,
    selected: Boolean,
    onClick: (() -> Unit) = {},
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(1.dp)
            .height(IntrinsicSize.Min)
            .clickable { onClick() },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    }
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(4.dp)
            ) {
                Icon(
                    Res.drawable.keyboard_tab_24px.painter,
                    contentDescription = null,
                    tint = if (selected) { MaterialTheme.colorScheme.outline } else { Color.Transparent }
                )
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                )
                Icon(
                    Res.drawable.keyboard_return_24px.painter,
                    contentDescription = null,
                    tint = if (selected) { MaterialTheme.colorScheme.outline } else { Color.Transparent }
                )
            }
        }
    }
}

@Composable
private fun HintMoreToShow() {
    Text(
        text = "...",
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(horizontal = 4.dp)
    )
}

private val hintOperations: List<DecoratedText> = listOf(
    InCardDecoratedText("Tab")
) + UndecoratedText.splitWhitespace(" to select UP, ") +
    InCardDecoratedText("Shift") +
    UndecoratedText.splitWhitespace(" + ") +
    InCardDecoratedText("Tab") +
    UndecoratedText.splitWhitespace(" to select Down, ") +
    InCardDecoratedText("Enter") +
    UndecoratedText.splitWhitespace(" to Input.")

@ExperimentalLayoutApi
@Composable
private fun HintOperations(
    style: TextStyle = MaterialTheme.typography.labelSmall,
    color: Color = MaterialTheme.colorScheme.outline,
    modifier: Modifier = Modifier.padding(8.dp)
) {
    FlowRow(
        modifier = modifier,
    ) {
        DecoratedText.composeAll(
            hintOperations,
            style = style,
            color = color,
        )
    }
}
