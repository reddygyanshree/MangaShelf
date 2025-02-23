package com.example.mangashelff.ui.Screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mangashelff.MangaViewModel
import com.example.mangashelff.database.MangaDataLocal
import com.example.mangashelff.ui.BookReadStatus
import com.example.mangashelff.ui.HeartCanvas
import com.example.mangashelff.ui.common.Constants
import com.example.mangashelff.ui.shimmerAnimationEffect
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
@Composable
fun HomeScreen(navController: NavController, viewModel: MangaViewModel = hiltViewModel()) {
    val mangaData = viewModel.mangaData.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState(initial = true)
    val showBackOnlineMessage by viewModel.showBackOnlineMessage.collectAsState(initial = false)


    val sortedMangaData=if (viewModel.showYearBar.value) {mangaData.value.sortedWith(compareBy {
        timestampToYear(it.publishedChapterDate)
    })} else mangaData.value

    var distinctYears= remember { mutableStateOf(emptyList<Int>()) }
    var categoryList = remember { mutableStateOf(listOf("ALL")) }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    val yearRowState = rememberLazyListState()


    val selectedYear = remember { mutableStateOf(distinctYears.value.firstOrNull() ?: 0) }


    LaunchedEffect(sortedMangaData) {
        distinctYears.value = sortedMangaData
            .map { timestampToYear(it.publishedChapterDate) }
            .distinct()
            .sorted()
        categoryList.value += sortedMangaData
            .map { it.category}
            .distinct()

        if (distinctYears.value.isNotEmpty()) {
            selectedYear.value = distinctYears.value.first()
        }
    }


    // Track current visible year
    val firstVisibleItemIndex by remember {
        derivedStateOf { gridState.firstVisibleItemIndex }
    }
    LaunchedEffect(Unit){
        viewModel.loadData()
    }

    LaunchedEffect(firstVisibleItemIndex) {
        if (sortedMangaData.isNotEmpty()) {
            val visibleManga = sortedMangaData.getOrNull(firstVisibleItemIndex)
            visibleManga?.let {
                val year = timestampToYear(it.publishedChapterDate)
                if (year != selectedYear.value) {
                    selectedYear.value = year
                    // Scroll year selector
                    val yearIndex = distinctYears.value.indexOf(year)
                    if (yearIndex >= 0) {
                        yearRowState.scrollToItem(yearIndex)
                    }
                }
            }
        }
    }

    Column(Modifier.background(Color.White)) {
        Box(modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxWidth()
            .wrapContentHeight()) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Manga Shelf",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = Color.Black
            )
        }
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

        if (sortedMangaData.isEmpty() ) {
            ShowShimmer()
        }
        else {
            if (viewModel.showYearBar.value) {
                LazyRow(
                    state = yearRowState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(distinctYears.value.size) { index ->
                        YearTabItem(
                            year = distinctYears.value[index].toString(),
                            isSelected = selectedYear.value == distinctYears.value[index]
                        ) {
                            scope.launch {
                                val yearToScrollTo = distinctYears.value[index]
                                selectedYear.value = yearToScrollTo

                                // Find exact match for year
                                val targetIndex = sortedMangaData.indexOfFirst { manga ->
                                    timestampToYear(manga.publishedChapterDate) == yearToScrollTo
                                }

                                if (targetIndex != -1) {
                                    // Verify the year before scrolling
                                    val targetYear =
                                        timestampToYear(sortedMangaData[targetIndex].publishedChapterDate)
                                    if (targetYear == yearToScrollTo) {
                                        gridState.scrollToItem(
                                            index = targetIndex,
                                            scrollOffset = 0
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    items(
                        count = sortedMangaData.size,
                        key = { index -> sortedMangaData[index].id }
                    ) { index ->
                        MangaGridItem(
                            manga = sortedMangaData[index],
                            viewModel = viewModel,
                            onItemClick = {
                                viewModel.selectedManga.value = sortedMangaData[index]
                                navController.navigate("mangaDetail")
                            }
                        )
                    }
                }


                Column(modifier = Modifier
                    .height(60.dp)
                    .align(Alignment.BottomCenter)) {
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp))

                    SortAndFilterUI(
                        onSortSelected = { sortOption ->
                            println("Selected Sort: $sortOption")
                            viewModel.showYearBar.value = false
                            viewModel.setSelectedOption(sortOption)
                            scope.launch {
                                gridState.scrollToItem(0)
                            }

                        },
                        onFilterApplied = { filterOption ->
                            println("Selected Filter: $filterOption")
                            viewModel.showYearBar.value = false
                            viewModel.refreshAndFilter(filterOption)
                            scope.launch {
                                gridState.scrollToItem(0)
                            }
                        },
                        categoryList.value.distinct()
                    )
                }
            }


        }

    }
}

@Composable
private fun MangaGridItem(
    manga: MangaDataLocal,
    viewModel: MangaViewModel,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onItemClick() }
    ) {
        val imageUrl = manga.image
        val painter = rememberAsyncImagePainter(model = imageUrl,)
        val isSuccess = painter.state is AsyncImagePainter.State.Success
        Card(
            modifier = Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp))
                .let { if (!isSuccess ) it.shimmerAnimationEffect() else it },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = manga.id,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth()
                .height(25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeartCanvas(manga, viewModel)
            Text(
                text = "${manga.score} / 100",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            BookReadStatus(manga, viewModel)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = manga.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            color = Color.Black
        )
        Text(
            text = "Popularity: ${manga.popularity}" + " Category " + manga.category,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
        Text(
            text =  " Category " + manga.category,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
        Text(
            text = "Published year: ${timestampToYear(manga.publishedChapterDate)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
    }
}



fun timestampToYear(timestamp: Long): Int {
    val date = Date(timestamp * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.YEAR)
}

@Composable
fun YearTabItem(year: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier
        .size(80.dp, 35.dp)
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .background(Color.LightGray),
        contentAlignment = Alignment.Center){
        Text(
            text = year,
            color = Color.Black,
            fontSize = if (isSelected) 18.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortAndFilterUI(
    onSortSelected: (String) -> Unit,
    onFilterApplied: (List<String>) -> Unit,
    categoryList: List<String>
) {
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedOption by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .height(20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SORT BUTTON
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                        isSheetOpen = true
                        selectedOption = Constants.SORT
                    }
            ) {
                Icon(Icons.Default.Sort, contentDescription = "Sort")
                Spacer(modifier = Modifier.width(8.dp))
                Text(Constants.SORT,
                    color = Color.Black)
            }

            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
            )

            // FILTER BUTTON
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isSheetOpen = true
                        selectedOption = Constants.FILTER
                    }

            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                Spacer(modifier = Modifier.width(8.dp))
                Text("FILTER",color = Color.Black)
            }
        }

    }
    if (isSheetOpen) {
        // BOTTOM SHEET
        ModalBottomSheet(
            containerColor = Color.White,
            onDismissRequest = {
                isSheetOpen = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
//                    .navigationBarsPadding()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
                ,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedOption==Constants.SORT) {
                    Text(
                        "Sort By",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )

                    val sortOptions =
                        listOf(
                            Constants.POPULARITY_ASC,
                            Constants.POPULARITY_DESC,
                            Constants.SCORE_ASC,
                            Constants.SCORE_DESC
                        )
                    sortOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    selectedSort = option
                                    onSortSelected(option)
                                    isSheetOpen = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedSort == option, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option, color = Color.Black)
                        }
                    }
                }

                else if(selectedOption==Constants.FILTER) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Filter By Category", fontWeight = FontWeight.Bold, fontSize = 18.sp,color = Color.Black)
                        Text("CLEAR ALL", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                selectedFilter= emptyList()
                            },
                            color = Color.Black)
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier.padding(bottom = 50.dp)) {
                            items(categoryList) { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {

                                            if (selectedFilter.contains(option)) {
                                                selectedFilter =
                                                    selectedFilter.filterNot { it == option }
                                            } else {
                                                selectedFilter = selectedFilter + listOf(option)
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedFilter.contains(option),
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(option,color = Color.Black)
                                }

                            }
                        }
                        Column(modifier = Modifier
                            .height(45.dp)
                            .align(Alignment.BottomCenter) ) {

                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(Color.White),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text("APPLY", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                                    modifier = Modifier.clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        isSheetOpen = false
                                        onFilterApplied(selectedFilter)
                                    },
                                    color = Color.Black)

                                Divider(
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                )

                                Text("CANCEL", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                                    modifier = Modifier.clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { isSheetOpen = false },
                                    color = Color.Black)
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowShimmer()
{
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(5) { index ->
            Box(
                modifier = Modifier
                    .size(80.dp, 35.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.LightGray)
                    .shimmerAnimationEffect(),
            )
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            count = 10,
        ) { index ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerAnimationEffect()){
            }
        }
    }
}

