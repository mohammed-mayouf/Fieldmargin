package com.mayouf.fieldmargintest.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mayouf.fieldmargintest.data.model.UiCertificate
import com.mayouf.fieldmargintest.presentation.viewmodel.CertificatesViewModel
import com.mayouf.fieldmargintest.utils.DataState

@Composable
fun CertificatesScreen(viewModel: CertificatesViewModel) {
    val certificatesState by viewModel.certificatesList.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog.value = !showDialog.value }) {
                Icon(Icons.Filled.Favorite, contentDescription = "Show Favorites")
            }
        }
    ) { innerPadding ->
        when (val state = certificatesState) {
            is DataState.Loading -> LoadingView()
            is DataState.Success -> CertificatesList(
                certificates = state.data ?: emptyList(),
                viewModel,
                Modifier.padding(innerPadding)
            )

            is DataState.Error -> Text("Error: ${state.message}", Modifier.padding(innerPadding))
            else -> Text("Idle", Modifier.padding(innerPadding))
        }

        if (showDialog.value) {
            FavoritesDialog(showDialog, viewModel)
        }
    }
}


@Composable
fun CertificatesList(
    certificates: List<UiCertificate>,
    viewModel: CertificatesViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(certificates) { uiCertificate ->
            CertificateItem(uiCertificate = uiCertificate, viewModel = viewModel)
        }
    }
}

@Composable
fun CertificateItem(uiCertificate: UiCertificate, viewModel: CertificatesViewModel) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "ID: ${uiCertificate.certificate.id}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Owner: ${uiCertificate.certificate.owner}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { viewModel.toggleFavorite(uiCertificate.certificate.id.orEmpty()) }) {
                Icon(
                    imageVector = if (uiCertificate.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Toggle Favorite"
                )
            }
        }
    }
}