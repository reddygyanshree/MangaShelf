package com.example.mangashelff.ui.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mangashelff.MangaViewModel
import com.example.mangashelff.ui.BookReadStatus
import com.example.mangashelff.ui.HeartCanvas
import com.example.mangashelff.ui.shimmerAnimationEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MangaDetailScreen(navController: NavHostController,viewModel:MangaViewModel= hiltViewModel()) {

    Log.d("heyy","viewModel.selectedManga.value--> ${viewModel.selectedManga.value}")
    val isConnected by viewModel.isConnected.collectAsState(initial = true)
    val showBackOnlineMessage by viewModel.showBackOnlineMessage.collectAsState(initial = false)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)

    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                tint = Color.Black,
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.popBackStack()
                    }
            )
            Text(modifier = Modifier.padding(start = 7.dp, end = 12.dp) ,
                text = viewModel.selectedManga.value!!.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = Color.Black)
        }
        Column (Modifier.padding(24.dp)){
            if (!isConnected) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color.Red),
                    contentAlignment = Alignment.Center){
                    Text(
                        text = "No Internet Connection",
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            if (showBackOnlineMessage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(Color.Green),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Back Online", color = Color.White,
                        fontWeight = FontWeight.Medium,)
                }
            }


            val imageUrl = viewModel.selectedManga.value!!.image
            val painter = rememberAsyncImagePainter(model = imageUrl,)
            val isSuccess = painter.state is AsyncImagePainter.State.Success
            Card(
                modifier = Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .let { if (!isSuccess ) it.shimmerAnimationEffect() else it },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {

                //img
                Image(
                    painter = painter,
                    contentDescription = viewModel.selectedManga.value?.id,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            //fav
            //read
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeartCanvas(viewModel.selectedManga.value!!, viewModel)

                BookReadStatus(viewModel.selectedManga.value!!, viewModel)
            }
            Spacer(modifier = Modifier.height(15.dp))

            //title
            Text(
                text = viewModel.selectedManga.value!!.title,
                fontSize = 20.sp,
//                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            //score
            Text(
                text = "Score: ${viewModel.selectedManga.value?.score ?: "N/A"} / 100",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            //popularity
            Text(
                text = "Popularity: ${viewModel.selectedManga.value?.popularity ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            //category
            Text(
                text = "Category: ${viewModel.selectedManga.value?.category ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            //date
            Text(
                text = "Published year: " + if (viewModel.selectedManga.value == null) "N/A" else timestampToReadableDate(
                    viewModel.selectedManga.value!!.publishedChapterDate
                ), style = MaterialTheme.typography.bodySmall, color = Color.Black, fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
    }

}

fun timestampToReadableDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return format.format(date)
}