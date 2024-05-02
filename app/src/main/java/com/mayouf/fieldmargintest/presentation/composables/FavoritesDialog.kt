package com.mayouf.fieldmargintest.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mayouf.fieldmargintest.presentation.viewmodel.CertificatesViewModel

@Composable
fun FavoritesDialog(showDialog: MutableState<Boolean>, viewModel: CertificatesViewModel) {
    val favorites by viewModel.favoritesList.collectAsState()

    if (showDialog.value && favorites.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Favorite Certificates") },
            text = {
                Column(modifier = Modifier.padding(all = 8.dp)) {
                    for (uiCertificate in favorites) {
                        Text(
                            "ID: ${uiCertificate.certificate.id} - Owner: ${uiCertificate.certificate.owner}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Close")
                }
            },
            modifier = Modifier
                .padding(all = 20.dp)
                .fillMaxWidth()
        )
    } else if (showDialog.value) {
        showDialog.value = false
    }
}
