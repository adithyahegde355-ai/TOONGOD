package com.mangaverse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF11111A)
private val Panel = Color(0xFF1D1D2B)
private val Violet = Color(0xFFB79CFF)
private val Soft = Color(0xFFE9E6F4)

data class Series(val id: Int, val title: String, val type: String, val genre: String, val description: String, val chapters: Int)

private val library = listOf(
    Series(1, "Moonlit Promise", "Manhwa", "Romance • Fantasy", "A palace healer discovers that moonlight reveals memories hidden in royal blood.", 12),
    Series(2, "Neon Ronin", "Manga", "Action • Sci-Fi", "A wandering swordsman protects a city where memories can be traded.", 8),
    Series(3, "The Last Sky Garden", "Manhwa", "Adventure • Fantasy", "Two friends climb floating ruins to find the last seed on Earth.", 10),
    Series(4, "Paper Stars Club", "Manga", "Slice of Life • Drama", "Students revive a forgotten astronomy club and find a place to belong.", 6),
    Series(5, "Dungeon Delivery", "Manhwa", "Comedy • Adventure", "A courier's most dangerous route is also the most profitable.", 15),
    Series(6, "Clockwork Orchard", "Manga", "Mystery • Fantasy", "An apprentice repairs clocks that predict impossible events.", 7),
    Series(7, "Red Comet Academy", "Manhwa", "Action • School", "A new student enters an academy that trains guardians of falling stars.", 9),
    Series(8, "Tiny Worlds", "Manga", "Comedy • Fantasy", "A miniature explorer maps the secret kingdoms in an old apartment.", 5)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MangaVerseApp() }
    }
}

@Composable
fun MangaVerseApp() {
    var tab by remember { mutableStateOf("Home") }
    var selected by remember { mutableStateOf<Series?>(null) }
    var readerSeries by remember { mutableStateOf<Series?>(null) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val saved = remember { mutableStateListOf<Int>() }
    val progress = remember { mutableStateMapOf<Int, Int>() }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Panel, primary = Violet, onBackground = Soft,
        onSurface = Soft, onPrimary = Ink
    )) {
        Surface(Modifier.fillMaxSize(), color = Ink) {
            when {
                readerSeries != null -> ReaderScreen(readerSeries!!, progress[readerSeries!!.id] ?: 1,
                    onBack = { readerSeries = null },
                    onProgress = { progress[readerSeries!!.id] = it })
                selected != null -> DetailScreen(
                    series = selected!!,
                    saved = saved.contains(selected!!.id),
                    onBack = { selected = null },
                    onToggleSave = {
                        if (saved.contains(selected!!.id)) saved.remove(selected!!.id)
                        else saved.add(selected!!.id)
                    },
                    onRead = { readerSeries = selected; selected = null }
                )
                else -> Column(Modifier.fillMaxSize()) {
                    Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                        Text("MangaVerse", color = Violet, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Your next world is one chapter away.", color = Soft.copy(alpha = .7f), fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = query, onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            placeholder = { Text("Search stories, genres…") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Violet, unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Soft, unfocusedTextColor = Soft
                            )
                        )
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Manga", "Manhwa").forEach { item ->
                            FilterChip(selected = filter == item, onClick = { filter = item }, label = { Text(item) })
                        }
                    }
                    val shown = library.filter {
                        (filter == "All" || it.type == filter) &&
                        (it.title.contains(query, true) || it.genre.contains(query, true) || it.type.contains(query, true))
                    }.let { if (tab == "Saved") it.filter { s -> saved.contains(s.id) } else it }
                    if (tab == "Saved") Text("YOUR BOOKMARKS", color = Violet, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 14.dp))
                    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 90.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Text(if (tab == "Saved") "Saved stories" else "Featured stories", color = Soft, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
                        }
                        items(shown) { series ->
                            SeriesCard(series, progress[series.id], saved.contains(series.id)) { selected = series }
                        }
                        if (shown.isEmpty()) item {
                            Text("Nothing here yet. Try another search or save a story.", color = Soft.copy(alpha = .65f), modifier = Modifier.padding(20.dp))
                        }
                    }
                    NavigationBar(containerColor = Panel) {
                        NavigationBarItem(selected = tab == "Home", onClick = { tab = "Home" }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Discover") })
                        NavigationBarItem(selected = tab == "Saved", onClick = { tab = "Saved" }, icon = { Icon(Icons.Default.Bookmark, null) }, label = { Text("Bookmarks") })
                    }
                }
            }
        }
    }
}

@Composable
fun SeriesCard(series: Series, progress: Int?, saved: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Panel), shape = MaterialTheme.shapes.large) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 86.dp, height = 112.dp).background(
                Brush.linearGradient(listOf(Violet.copy(alpha = .9f), Color(0xFF34345A))),
                shape = MaterialTheme.shapes.medium
            ), contentAlignment = Alignment.Center) {
                Text(if (series.type == "Manga") "漫\n画" else "웹\n툰", fontSize = 23.sp, fontWeight = FontWeight.Black, color = Ink, lineHeight = 27.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(series.type.uppercase(), color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(series.title, color = Soft, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(series.genre, color = Soft.copy(alpha = .65f), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("${series.chapters} sample chapters", color = Soft.copy(alpha = .8f), fontSize = 12.sp)
                if (progress != null) Text("Last read: Chapter $progress", color = Violet, fontSize = 11.sp)
                if (saved) Text("♥ Bookmarked", color = Violet, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun DetailScreen(series: Series, saved: Boolean, onBack: () -> Unit, onToggleSave: () -> Unit, onRead: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth().height(210.dp).background(Violet.copy(alpha = .2f), MaterialTheme.shapes.extraLarge), contentAlignment = Alignment.Center) {
            Text(if (series.type == "Manga") "漫 画" else "웹 툰", fontSize = 52.sp, color = Violet, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(20.dp))
        Text(series.type.uppercase(), color = Violet, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(series.title, color = Soft, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp)
        Text(series.genre, color = Soft.copy(alpha = .65f))
        Spacer(Modifier.height(18.dp))
        Text(series.description, color = Soft, fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(16.dp))
        Text("${series.chapters} demo chapters", color = Violet)
        Spacer(Modifier.height(22.dp))
        Button(onClick = onRead, modifier = Modifier.fillMaxWidth()) { Text("Start reading") }
        OutlinedButton(onClick = onToggleSave, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Bookmark, null); Spacer(Modifier.width(8.dp)); Text(if (saved) "Remove bookmark" else "Add to bookmarks")
        }
        Text("Demo content: replace with chapters you own or have permission to distribute.", color = Soft.copy(alpha = .5f), fontSize = 12.sp, modifier = Modifier.padding(top = 18.dp))
    }
}

@Composable
fun ReaderScreen(series: Series, currentChapter: Int, onBack: () -> Unit, onProgress: (Int) -> Unit) {
    var chapter by remember(series.id) { mutableIntStateOf(currentChapter.coerceIn(1, series.chapters)) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Column(Modifier.weight(1f)) {
                Text(series.title, color = Soft, fontWeight = FontWeight.Bold)
                Text("Chapter $chapter • ${series.type} reader", color = Violet, fontSize = 12.sp)
            }
        }
        if (series.type == "Manhwa") {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items((1..7).toList()) { panel ->
                    Box(Modifier.fillMaxWidth().height(if (panel % 2 == 0) 250.dp else 190.dp).background(
                        Brush.verticalGradient(listOf(Color(0xFF34345A), Color(0xFF202033))),
                        MaterialTheme.shapes.medium
                    ), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                            Text("SAMPLE PANEL $panel", color = Violet, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("Your authorized chapter artwork goes here.", color = Soft.copy(alpha = .8f))
                        }
                    }
                }
            }
        } else {
            Box(Modifier.weight(1f).fillMaxWidth().padding(18.dp).background(Color(0xFFF2EEE8), MaterialTheme.shapes.medium), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(26.dp)) {
                    Text("MANGA • CHAPTER $chapter", color = Color(0xFF302A36), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Spacer(Modifier.height(18.dp))
                    Text("Sample manga page", color = Color(0xFF302A36), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Replace this demo page with artwork you own or have permission to publish.", color = Color(0xFF514A56))
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { chapter = (chapter - 1).coerceAtLeast(1); onProgress(chapter) }, enabled = chapter > 1) { Text("Previous") }
            Text("$chapter / ${series.chapters}", color = Soft)
            Button(onClick = { chapter = (chapter + 1).coerceAtMost(series.chapters); onProgress(chapter) }, enabled = chapter < series.chapters) { Text("Next") }
        }
    }
}
