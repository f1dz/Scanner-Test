package com.austrex.scannertest

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import com.austrex.scannertest.adapter.ScanAdapter
import com.austrex.scannertest.model.Tag
import com.austrex.scannertest.utils.hide
import com.austrex.scannertest.utils.show
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(),
    BluetoothSPP.OnDataReceivedListener,
    BluetoothSPP.BluetoothConnectionListener,
    BluetoothSPP.BluetoothStateListener{

    lateinit var adapter: ScanAdapter
    var bt = BluetoothSPP(this)
    var scannedTags: MutableList<Tag> = mutableListOf()
    var devices: MutableList<BluetoothDevice> = mutableListOf()
    var isConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt.setupService()
        bt.startService(BluetoothState.DEVICE_OTHER)

        if(!bt.isBluetoothAvailable) {
            Toast.makeText(this, "Bluetooth adapter not available on this phone", Toast.LENGTH_SHORT).show()
            finish()
        }

        adapter = ScanAdapter(this)

        rvScan.layoutManager = LinearLayoutManager(this)
        rvScan.adapter = adapter

        fabStart.setOnClickListener { fabClicked() }

        bt.setOnDataReceivedListener(this)
        bt.setBluetoothConnectionListener(this)
        bt.setBluetoothStateListener(this)
    }

    private fun fabClicked(){
        if(isConnected){
            bt.disconnect()
        } else {
            val intent = Intent(this, DeviceList::class.java)
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
        }
    }

    override fun onStart() {
        super.onStart()
        if(!bt.isBluetoothEnabled) bt.enable()
    }

    override fun onStop() {
        super.onStop()
        bt.stopService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK){
                data?.let {
                    val mac = it.getStringExtra("device_address")
                    mac?.let { bt.connect(it) }
                }
            }
        }
    }

    override fun onDataReceived(data: ByteArray, message: String) {
        scannedTags.add(Tag(message, DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
        adapter.setData(scannedTags)
        rvScan.scrollToPosition(scannedTags.size - 1)
    }

    override fun onDeviceConnected(name: String?, address: String?) {
        Toast.makeText(this, "Device connected", Toast.LENGTH_SHORT).show()
        fabStart.setImageResource(R.drawable.ic_baseline_stop)
        progress.show()
        progressConnect.hide()
    }

    override fun onDeviceDisconnected() {
        Toast.makeText(this, "Device disconnected", Toast.LENGTH_SHORT).show()
        fabStart.setImageResource(R.drawable.ic_baseline_play_arrow)
        progress.hide()
        progressConnect.hide()
    }

    override fun onDeviceConnectionFailed() {
        Toast.makeText(this, "Connection failed!", Toast.LENGTH_SHORT).show()
        fabStart.setImageResource(R.drawable.ic_baseline_play_arrow)
        progress.hide()
        progressConnect.hide()
    }

    override fun onServiceStateChanged(state: Int) {
        if(state == BluetoothState.STATE_CONNECTING){
            progressConnect.show()
        }
    }
}