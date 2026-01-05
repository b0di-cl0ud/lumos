package com.example.lumos.ui.screens.scan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothViewModel : ViewModel() {
    val isBluetoothEnabled = MutableLiveData<Boolean>()
}