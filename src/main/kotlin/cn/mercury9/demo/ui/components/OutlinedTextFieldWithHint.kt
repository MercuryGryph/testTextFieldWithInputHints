package cn.mercury9.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import cn.mercury9.demo.data.hintprovider.Hint
import cn.mercury9.demo.data.hintprovider.HintProvider
import cn.mercury9.demo.resources.Res
import cn.mercury9.demo.resources.keyboard_return_24px
import cn.mercury9.demo.resources.keyboard_tab_24px
import cn.mercury9.utils.compose.painter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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

    var hints = hintProvider.getHintFrom(value, textFieldValue.selection)

    val lineHeight = textStyle.lineHeight.value


    fun changeSelectedHintIndexTo(i: Int) {
        selectedHintIndex = i
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

    fun changeSelectedHint(delta: Int) {
        changeSelectedHintIndexTo(selectedHintIndex + delta)
    }

    SideEffect {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = value)
            selectedHint = Hint.Empty
            changeSelectedHintIndexTo(0)
            firstShowingHintIndex = 0
            hintEnabled = true
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
                        changeSelectedHint(-1)
                    } else {
                        changeSelectedHint(1)
                    }
                    return true
                }

                Key.DirectionUp -> {
                    if (
                        event.isAltPressed ||
                        event.isCtrlPressed ||
                        event.isMetaPressed ||
                        event.isShiftPressed
                    ) {
                        return false
                    }
                    changeSelectedHint(-1)
                    return true
                }

                Key.DirectionDown -> {
                    if (
                        event.isAltPressed ||
                        event.isCtrlPressed ||
                        event.isMetaPressed ||
                        event.isShiftPressed
                    ) {
                        return false
                    }
                    changeSelectedHint(1)
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
                hints = hintProvider.getHintFrom(it.text, it.selection)
                changeSelectedHintIndexTo(run {
                    val i = hints.indexOf(selectedHint)
                    if (i == -1) 0 else i
                })
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
                        .scrollable(
                            state = rememberScrollableState { delta ->
                                changeSelectedHint(-(delta / 15).roundToInt())
                                delta
                            },
                            orientation = Orientation.Vertical,
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (firstShowingHintIndex > 0) {
                                HintMoreToShow()
                            }

                            for (index in 0 until min(hintMaxShowingNumber, hints.size)) {
                                val selfIndex = firstShowingHintIndex + index
                                if (selfIndex > hints.size - 1) {
                                    changeSelectedHintIndexTo(0)
                                    break
                                }
                                val selected = selfIndex == selectedHintIndex
                                HintItem(
                                    hint = hints[selfIndex].hint,
                                    selected = selected,
                                ) {
                                    if (selected) {
                                        useHint()
                                    } else {
                                        changeSelectedHintIndexTo(selfIndex)
                                    }
                                }
                            }

                            if (firstShowingHintIndex + hintMaxShowingNumber < hints.size) {
                                HintMoreToShow()
                            }
                        }
                        if (hints.size > hintMaxShowingNumber) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(2.dp)
                                    .width(12.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            ) {
                                Spacer(Modifier
                                    .weight(max(firstShowingHintIndex.toFloat(), .01f))
                                )
                                Box(Modifier
                                    .weight(hintMaxShowingNumber + 1f)
                                    .fillMaxWidth()
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Spacer(Modifier
                                    .weight(max(hints.size - firstShowingHintIndex.toFloat() - hintMaxShowingNumber, .01f))
                                )
                            }
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
                .clickable { onClick() },
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
