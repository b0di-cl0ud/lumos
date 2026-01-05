package com.example.lumos.ui.screens.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lumos.data.ble.BluetoothLEManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

import androidx.compose.ui.graphics.Color
import com.example.lumos.data.LightBulb

class ScanViewModel : ViewModel() {
    // Le processus de scan
    private var scanJob: Job? = null

    // Durée du scan
    private val scanDuration = 10000L

    // Le scanner bluetooth
    private val scanFilters: List<ScanFilter> = listOf(
        // Pour filtrer les périphériques
        ScanFilter.Builder().setServiceUuid(ParcelUuid(BluetoothLEManager.DEVICE_UUID)).build()
    )
    // Les options de scan (mode faible latence)
    private val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    // Liste des résultats du scan, Le Set sera utilisé pour éviter les doublons
    private val scanResultsSet = mutableMapOf<String, ScanResult>()

    // Référence au BluetoothGatt en cours (device sur lequel nous sommes connectés)
    private var currentBluetoothGatt: BluetoothGatt? = null

    // La liste des appareils scannés autour
    val scanItemsFlow = MutableStateFlow<List<ScanResult>>(emptyList())

    // Boolean permettant de savoir si nous sommes en train de scanner
    val isScanningFlow = MutableStateFlow(false)

    @SuppressLint("MissingPermission")
    fun startScan(context: Context) {
        // Récupération du scanner BLE
        val bluetoothLeScanner = (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter.bluetoothLeScanner

        // Si nous sommes déjà en train de scanner, on ne fait rien
        if (isScanningFlow.value) return

        // Définition du processus de scan (Coroutine)
        scanJob = CoroutineScope(Dispatchers.IO).launch {
            // On indique que nous sommes en train de scanner
            isScanningFlow.value = true

            // Objet qui sera appelé à chaque résultat de scan
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    super.onScanResult(callbackType, result)
                    // On ajoute le résultat dans le set, si il n'y est pas déjà
                    if (scanResultsSet.put(result.device.address, result) == null) {
                        // On envoie la nouvelle liste des appareils scannés
                        scanItemsFlow.value = scanResultsSet.values.toList()
                    }
                }
            }

            // On lance le scan BLE
            bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback)

            // On attend la durée du scan (10 secondes)
            delay(scanDuration)

            // On stop le scan BLE
            bluetoothLeScanner.stopScan(scanCallback)

            // On indique que nous ne sommes plus en train de scanner
            isScanningFlow.value = false
        }
    }

    private fun stopScan() {
        scanJob?.cancel()
        isScanningFlow.value = false
    }

    fun clearList() {
        scanResultsSet.clear()
        scanItemsFlow.value = emptyList()
    }

    //PARTIE CONNEXION

    // Flow permettant de savoir si nous sommes en train de nous connecter
    val isConnectingFlow = MutableStateFlow(false)

    // Flow permettant de savoir si un appareil est connecté
    val isConnectedToDeviceFlow = MutableStateFlow(false)

    @SuppressLint("MissingPermission")
    fun connect(context: Context, bluetoothDevice: BluetoothDevice) {
        // On arrête le scan si il est en cours
        stopScan()

        // On indique que nous sommes en train de nous connecter (pour afficher un loader par exemple)
        isConnectingFlow.value = true

        // On tente de se connecter à l'appareil
        // On utilise le GattCallback pour gérer les événements BLE (connexion, déconnexion, notifications).
        currentBluetoothGatt = bluetoothDevice.connectGatt(
            context,
            false,
            BluetoothLEManager.GattCallback(
                // La connexion a réussi (onServicesDiscovered)
                onConnect = {
                    isConnectedToDeviceFlow.value = true
                    isConnectingFlow.value = false
                    // On active les notifications pour recevoir les événements de la LED et du compteur
                    // enableNotify()
                },


                // Nouvelle valeur reçue sur une caractéristique de type notification
                onNotify = { characteristic, value ->
                    /*when (characteristic.uuid) {
                        BluetoothLEManager.CHARACTERISTIC_NOTIFY_STATE -> connectedDeviceLedStateFlow.value = value == "1"
                        // Implémenter les autres caractéristiques ici (count, wifi)
                    }*/
                },

                // L'ESP32 s'est déconnecté (BluetoothGatt.STATE_DISCONNECTED)
                onDisconnect = {
                    isConnectedToDeviceFlow.value = false
                }
            ))
    }

    fun toggleLed() {
        writeCharacteristic(BluetoothLEManager.CHARACTERISTIC_TOGGLE_LED_UUID, "1")
    }

    @SuppressLint("MissingPermission")
    private fun writeCharacteristic(uuid: UUID, value: String) {
        // Récupération du service principal (celui de l'ESP32)
        getMainService()?.let { service ->
            // Récupération de la caractéristique
            val characteristic = service.getCharacteristic(uuid)

            if (characteristic == null) {
                Log.e("BluetoothLEManager", "La caractéristique $uuid n'a pas été trouvée")
                return
            }

            Log.i("BluetoothLEManager", "Ecriture de la valeur $value dans la caractéristique $uuid")

            // En fonction de la version de l'OS, on utilise la méthode adaptée
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // On écrit la valeur dans la caractéristique
                currentBluetoothGatt?.writeCharacteristic(characteristic, value.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            } else {
                // On écrit la valeur dans la caractéristique
                characteristic.setValue(value)
                currentBluetoothGatt?.writeCharacteristic(characteristic)

            }
        }
    }

    private fun getMainService(): BluetoothGattService? = currentBluetoothGatt?.getService(BluetoothLEManager.DEVICE_UUID)

    // MAJ DE L'ÉTAT DE LA LED (tentative pour faire en sorte que l'état de la LED soit sauvegardé mais ne fonctionne pas)
    var lightBulb = LightBulb(id = "1", name = "Ampoule Salon", color = Color.White, sliderPosition = 50f, isLedOn = false)

    fun updateSliderPosition(position: Float) {
        lightBulb.sliderPosition = position
    }

    fun updateSelectedColor(color: Color) {
        lightBulb.color = color
    }

    fun updateLedState(isOn: Boolean) {
        lightBulb.isLedOn = isOn
    }
}