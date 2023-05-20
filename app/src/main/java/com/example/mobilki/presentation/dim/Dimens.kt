package com.example.mobilki.presentation.dim

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier



object Dimens {



    object Modifiers {
        val commonModifier = Modifier.clip(RoundedCornerShape(30))
                                     .border(
                                        width = 2.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(30),
                                    )
    }
}
