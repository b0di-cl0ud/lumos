package com.example.lumos.ui.screens.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lumos.R
import com.example.lumos.ui.components.ElementList
import com.example.lumos.ui.screens.LampSettingsScreen
import com.example.lumos.ui.theme.LumosTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Scan(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    val toCheckPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        listOf(android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN)
    }
    val permissionState = rememberMultiplePermissionsState(toCheckPermissions)
    val context = LocalContext.current
    var showScreenScan by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Black),
                    startY = 1000f,
                    endY = 2500f
                )
            )
    ) {
        // Vérifier si la permission est accordée
        if (permissionState.allPermissionsGranted) {
            // La permission est accordée
            // Nous sommes prêt à scanner
            // Vérification si le Bluetooth est activé
            checkBluetoothEnabled(context, navController, bluetoothViewModel)
            LaunchedEffect(Unit) {
                //delay(2000L) // Ajoute un délai de 2 secondes
                showScreenScan = true
            }
            if (showScreenScan) {
                ScreenScan(navController)
            }
        } else {
            // La permission n'est pas accordée
            // Nous devons demander la permission
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                AlertDialogExample(
                    onDismissRequest = { showDialog = false },
                    onConfirmation = {
                        showDialog = false
                        permissionState.launchMultiplePermissionRequest()
                    },
                    dialogTitle = stringResource(R.string.permission_required),
                    dialogText = stringResource(R.string.this_feature_requires_permission_to_access_bluetooth_please_grant_the_permission),
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_bluetooth_24)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.lumos),
                        contentDescription = "Logo lamp",
                    )
                    Spacer(modifier = Modifier.padding(15.dp))
                    Text(
                        text = stringResource(R.string.this_feature_requires_permission_to_access_bluetooth_please_grant_the_permission),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.padding(15.dp))
                    OutlinedButton(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = stringResource(R.string.request_permission))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScan(
    navController: NavController,
    viewModel: ScanViewModel = viewModel(),
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    val list by viewModel.scanItemsFlow.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanningFlow.collectAsStateWithLifecycle()
    val isConnecting by viewModel.isConnectingFlow.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnectedToDeviceFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        // Crée un observateur du cycle de vie
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {

                }
                Lifecycle.Event.ON_RESUME -> {
                    if (bluetoothViewModel.isBluetoothEnabled.value == true) {
                        viewModel.startScan(context)
                        Toast.makeText(context,
                            context.getString(R.string.scan_begins), Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }

        // Ajoute l'observateur au cycle de vie
        lifecycleOwner.lifecycle.addObserver(observer)

        // Nettoie l'observateur lorsque le composable est détruit
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.LightGray, Color.White)
                        )
                    )
                    .padding(horizontal = 10.dp)
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.connected_device)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black,
                    ),
                    navigationIcon = {
                        if(!isConnected) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                        } else {
                            IconButton(onClick = { navController.navigate("scan") }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    actions = {
                        if(!isConnected) {
                            IconButton(
                                onClick = { viewModel.clearList() },
                                enabled = list.isNotEmpty(),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Clear",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isConnected) {
                FloatingActionButton(
                    onClick = {
                        viewModel.startScan(context)
                        Toast.makeText(context, R.string.scan_begins, Toast.LENGTH_SHORT).show()
                    },
                    containerColor = Color.LightGray,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Floating action button",
                        tint = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color.LightGray),
                        startY = 10f,
                        endY = 1000f
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when {
                    isScanning -> {
                        IndeterminateCircularIndicator()
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(stringResource(R.string.scanning), color = Color.Black)
                    }
                    isConnecting -> {
                        IndeterminateCircularIndicator()
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(stringResource(R.string.connecting), color = Color.Black)
                    }
                    //isConnected -> Button(onClick = { viewModel.toggleLed() }) { }
                    isConnected -> {
                        LampSettingsScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 5.dp)
                        ) {
                            items(list) { result ->
                                ElementList(
                                    title = result.device.name ?: context.getString(R.string.unknown_device),
                                    content = result.device.address,
                                    onClick = { viewModel.connect(context, result.device) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun checkBluetoothEnabled(
    context: Context,
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel,
    notAvailable: () -> Unit = {}
) {
    val bluetoothManager: BluetoothManager? = remember {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
    }
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    val enableBluetoothLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {}
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bluetoothAdapter) {
        when {
            bluetoothAdapter == null -> {
                notAvailable()
            }
            !bluetoothAdapter.isEnabled -> {
                Toast.makeText(context,
                    context.getString(R.string.bluetooth_is_disabled), Toast.LENGTH_SHORT).show()
                showDialog = true
                bluetoothViewModel.isBluetoothEnabled.value = false
            }
            else -> {
                bluetoothViewModel.isBluetoothEnabled.value = true
            }
        }
    }
    if (showDialog) {
        AlertDialogExample(
            onDismissRequest = {
                showDialog = false
                navController.popBackStack() // Retourne à l'écran précédent
            },
            onConfirmation = {
                showDialog = false
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            },
            dialogTitle = stringResource(R.string.enable_bluetooth),
            dialogText = stringResource(R.string.bluetooth_is_required_for_this_feature_please_enable_bluetooth),
            icon = ImageVector.vectorResource(id = R.drawable.baseline_bluetooth_24)
        )
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(40.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun ElementList(
    title: String,
    content: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Column {
            Text(text = title)
            Text(text = content)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanPreview() {
    LumosTheme {
        Scan(
            navController = NavController(LocalContext.current)
        )
    }
}