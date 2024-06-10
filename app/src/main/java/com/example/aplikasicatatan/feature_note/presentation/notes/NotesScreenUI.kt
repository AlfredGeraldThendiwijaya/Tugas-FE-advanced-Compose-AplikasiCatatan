package com.example.aplikasicatatan.feature_note.presentation.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplikasicatatan.R
import com.example.aplikasicatatan.feature_note.navigation.NavScreen
import com.example.aplikasicatatan.feature_note.presentation.notes.components.NoteItemUI
import com.example.aplikasicatatan.feature_note.presentation.notes.components.OrderSection
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesScreen(
    navController: NavController,
    context: Context,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        floatingActionButton = { FloatingButtonScaffold(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            ContentPartScaffold(
                navController = navController,
                context = context,
                snackbarHostState = snackbarHostState
            )
        }
    )
}

@Composable
fun FloatingButtonScaffold(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(NavScreen.AddEditNoteScreen.route)
        },
        containerColor = Color.Gray
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
    }
}

@Composable
fun ContentPartScaffold(
    navController: NavController,
    context: Context,
    snackbarHostState: SnackbarHostState,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catatan Kamu",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            IconButton(
                onClick = { viewModel.onEvent(NotesEvent.ToggleOrderSection) }
            ) {
                Icon(Icons.Filled.Sort, contentDescription = "Sort")
            }
        }

        AnimatedVisibility(
            visible = state.isOrderSectionVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            OrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                noteOrder = state.noteOrder,
                onOrderChange = {
                    viewModel.onEvent(NotesEvent.Order(it))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Check if there are no notes
        if (state.notes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 200.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.notfound),
                    contentDescription = "No Notes Image",
                    modifier = Modifier.size(200.dp)
                )
                Text(text = "Tidak ada catatan" ,color=Color.Gray, modifier = Modifier.offset(y = (-15).dp))
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.notes) { note ->
                    NoteItemUI(
                        note = note,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    NavScreen.AddEditNoteScreen.route +
                                            "?noteId=${note.id}&noteColor=${note.color}"
                                )
                            },
                        onShareClicked = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, note.content)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)

                        },
                        onDeleteClicked = {
                            viewModel.onEvent(NotesEvent.DeleteNote(note))
                            // Show snackbar after deleting the note
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Catatan dihapus",
                                    actionLabel = "URUNG",
                                    duration = SnackbarDuration.Short
                                )

                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        viewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                    else -> {}
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

