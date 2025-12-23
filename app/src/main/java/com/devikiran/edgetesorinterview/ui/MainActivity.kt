package com.devikiran.edgetesorinterview.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devikiran.edgetesorinterview.service.HealthMonitorService
import com.devikiran.edgetesorinterview.ui.theme.EdgeTesorInterviewTheme
import com.devikiran.edgetesorinterview.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher:
            ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            if (results.values.all { it }) {
                startServiceAndExit()
            } else {
                showPermissionDeniedUi()
            }
        }

        if (PermissionManager.allPermissionsGranted(this)) {
            startServiceAndExit()
        } else {
            permissionLauncher.launch(PermissionManager.requiredPermissions())
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionManager.allPermissionsGranted(this)) {
            startServiceAndExit()
        }
    }

    private fun showPermissionDeniedUi() {
        setContent {
            EdgeTesorInterviewTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Permissions Required",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Location, Camera, and Notification permissions are required for the dashcam service to function properly.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { openAppSettings() }
                        ) {
                            Text("Open App Settings")
                        }
                    }
                }
            }
        }
    }

    private fun startServiceAndExit() {
        startForegroundService(
            Intent(this, HealthMonitorService::class.java)
        )
        finish()
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
