package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import com.example.util.ProvideSafeTextToolbar
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.supabase.SupabaseSchema
import com.example.data.remote.supabase.SyncResult
import com.example.data.remote.supabase.SyncUiState
import com.example.ui.theme.BuListioCoral
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupabaseSyncSection(
    syncState: SyncUiState,
    supabaseUrl: String,
    supabaseMaskedKey: String,
    isConfigured: Boolean,
    onSyncNow: ((SyncResult) -> Unit) -> Unit,
    onTestConnection: ((Boolean, String) -> Unit) -> Unit,
    onUpdateConfig: (String, String) -> Unit,
    onResetConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResultDialogText by remember { mutableStateOf<String?>(null) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showSqlSchemaDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isConfigured) BuListioCoral.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("supabase_sync_section_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isConfigured) Color(0xFF22C55E).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isConfigured) Icons.Filled.CloudDone else Icons.Filled.Cloud,
                                contentDescription = null,
                                tint = if (isConfigured) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Supabase Cloud Database",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "PostgreSQL cloud sync & storage",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isConfigured) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isConfigured) Color(0xFF86EFAC) else Color(0xFFE5E7EB)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isConfigured) Color(0xFF16A34A) else Color(0xFF9CA3AF))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isConfigured) "Connected" else "Offline / Local",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isConfigured) Color(0xFF15803D) else Color(0xFF4B5563)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connection Details Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Link, contentDescription = null, tint = BuListioCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "URL: ${if (supabaseUrl.isNotBlank()) supabaseUrl else "Not Configured"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "API Key: $supabaseMaskedKey",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sync Status Indicator
            if (syncState.lastSyncTime != null || syncState.isSyncing) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = BuListioCoral
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Syncing with Supabase...",
                                style = MaterialTheme.typography.labelSmall,
                                color = BuListioCoral
                            )
                        } else {
                            val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(syncState.lastSyncTime ?: 0L))
                            Icon(
                                imageVector = if (syncState.isSuccess == true) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = if (syncState.isSuccess == true) SuccessGreen else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Last synced at $timeStr (${syncState.businessesCount} spots)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sync Now Button
                Button(
                    onClick = {
                        onSyncNow { result ->
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !syncState.isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("supabase_sync_now_btn")
                ) {
                    if (syncState.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (syncState.isSyncing) "Syncing..." else "Sync Now", fontWeight = FontWeight.Bold)
                }

                // Test Connection Button
                OutlinedButton(
                    onClick = {
                        isTestingConnection = true
                        onTestConnection { success, message ->
                            isTestingConnection = false
                            testResultDialogText = message
                        }
                    },
                    enabled = !isTestingConnection,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("supabase_test_btn")
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTestingConnection) "Testing..." else "Test Link")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-actions: Configure Credentials & SQL Schema
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showConfigDialog = true },
                    modifier = Modifier.testTag("supabase_config_dialog_btn")
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Configure URL / Key", fontSize = 12.sp)
                }

                TextButton(
                    onClick = { showSqlSchemaDialog = true },
                    modifier = Modifier.testTag("supabase_view_sql_btn")
                ) {
                    Icon(Icons.Filled.Storage, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SQL Table Script", fontSize = 12.sp)
                }
            }
        }
    }

    // --- Test Result Dialog ---
    testResultDialogText?.let { resultMsg ->
        AlertDialog(
            onDismissRequest = { testResultDialogText = null },
            icon = {
                Icon(
                    imageVector = if (resultMsg.contains("successful", ignoreCase = true)) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = if (resultMsg.contains("successful", ignoreCase = true)) SuccessGreen else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = if (resultMsg.contains("successful", ignoreCase = true)) "Connection Verified" else "Connection Notice",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(text = resultMsg, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TownSpot automatically persists data locally in Room database and continuously syncs with your Supabase cloud tables.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { testResultDialogText = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // --- Configure Credentials Dialog ---
    if (showConfigDialog) {
        var inputUrl by remember { mutableStateOf(supabaseUrl) }
        var inputKey by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val dismissSafely = {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            showConfigDialog = false
        }

        ProvideSafeTextToolbar {
            AlertDialog(
                onDismissRequest = dismissSafely,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Cloud, contentDescription = null, tint = BuListioCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supabase Credentials", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Enter your live Supabase project details. Alternatively, you can configure SUPABASE_URL and SUPABASE_ANON_KEY in the AI Studio Secrets panel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            label = { Text("Supabase Project URL") },
                            placeholder = { Text("https://xyz.supabase.co") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("supabase_input_url")
                        )

                        OutlinedTextField(
                            value = inputKey,
                            onValueChange = { inputKey = it },
                            label = { Text("Supabase Anon Key") },
                            placeholder = { Text("eyJhbGciOiJIUzI1Ni...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("supabase_input_key")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputUrl.isNotBlank() && inputKey.isNotBlank()) {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onUpdateConfig(inputUrl, inputKey)
                                showConfigDialog = false
                                Toast.makeText(context, "Supabase credentials updated!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please provide both URL and Key", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral)
                    ) {
                        Text("Save & Connect")
                    }
                },
                dismissButton = {
                    Row {
                        TextButton(onClick = {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                            onResetConfig()
                            showConfigDialog = false
                            Toast.makeText(context, "Reset to defaults", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Reset Defaults")
                        }
                        TextButton(onClick = dismissSafely) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }

    // --- SQL Setup Script Dialog ---
    if (showSqlSchemaDialog) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        ProvideSafeTextToolbar {
            AlertDialog(
                onDismissRequest = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    showSqlSchemaDialog = false
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Storage, contentDescription = null, tint = BuListioCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supabase SQL Schema", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Copy and execute this SQL script in your Supabase Project -> SQL Editor to initialize the tables (`businesses`, `reviews`, `promotions`, `users`) with public RLS policies:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = SupabaseSchema.SQL_SETUP_SCRIPT.trim(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("TownSpot Supabase SQL", SupabaseSchema.SQL_SETUP_SCRIPT.trim())
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "SQL schema copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showSqlSchemaDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy SQL Script")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        showSqlSchemaDialog = false
                    }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
