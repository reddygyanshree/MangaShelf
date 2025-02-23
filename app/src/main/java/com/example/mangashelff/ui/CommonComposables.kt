package com.example.mangashelff.ui

import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.mangashelff.MangaViewModel
import com.example.mangashelff.database.MangaDataLocal


@Composable
fun HeartCanvas(manga: MangaDataLocal, viewModel: MangaViewModel,) {
    val isfav = remember { mutableStateOf( manga.fav ) }

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                manga.fav = !manga.fav
                isfav.value = manga.fav
                viewModel.updateManga(manga)
            }
    ) {
        val heartSize = size.height

        val path = Path().apply {
            moveTo(heartSize / 2, heartSize / 4)
            cubicTo(
                heartSize * 0.8f, -heartSize / 5,
                heartSize * 1.4f, heartSize / 3,
                heartSize / 2, heartSize
            )
            cubicTo(
                -heartSize * 0.4f, heartSize / 3,
                heartSize * 0.2f, -heartSize / 5,
                heartSize / 2, heartSize / 4
            )
            close()
        }

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = if (isfav.value) Color.Red.hashCode() else Color.Black.hashCode()
                style = if (isfav.value) Paint.Style.FILL else Paint.Style.STROKE
                strokeWidth = 8f // Increase this value for a bolder stroke
                isAntiAlias = true // Smooth edges
            }
            canvas.nativeCanvas.drawPath(path, paint)
        }
    }
}

@Composable
fun BookReadStatus(manga: MangaDataLocal, viewModel: MangaViewModel) {

    val isRead = remember { mutableStateOf( manga.read ) }

    androidx.compose.material.Icon(
        imageVector = if (isRead.value) Icons.Filled.AutoStories else Icons.Filled.MenuBook,
        contentDescription = if (isRead.value) "Book Read" else "Book Unread",
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Log.d("heyy read", "clickable" + manga.read)
                manga.read = !manga.read
                isRead.value = manga.read
                Log.d("heyy read after", "clickable" + manga.read)
                viewModel.updateManga(manga)
            },
        tint = if (isRead.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    )
}

fun Modifier.shimmerAnimationEffect(): Modifier = composed {
    val size = remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.value.width.toFloat(),
        targetValue = 2 * size.value.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE4E2E2),
                Color(0xFFBEBDBD),
                Color(0xFFE4E2E2),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.value.width.toFloat(), size.value.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size.value = it.size
        }
}