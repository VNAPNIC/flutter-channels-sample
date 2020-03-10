package com.namit.flutter_channels_sample.wowza

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wowza.gocoder.sdk.api.WowzaGoCoder
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView
import com.wowza.gocoder.sdk.api.geometry.WOWZSize
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatus.BroadcastState
import com.wowza.gocoder.sdk.api.status.WOWZBroadcastStatusCallback
import com.wowza.gocoder.sdk.support.status.WOWZStatus
import com.wowza.gocoder.sdk.support.status.WOWZStatusCallback
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView


class FlutterWOWZCameraView internal

constructor(private val context: Context?,private val methodChannel: MethodChannel, id: Int?, params: Map<String, Any>?) :
        PlatformView, MethodChannel.MethodCallHandler, WOWZBroadcastStatusCallback, WOWZStatusCallback {

    private val goCoderCameraView: WOWZCameraView = WOWZCameraView(context)
    // The top-level GoCoder API interface
    private var goCoder: WowzaGoCoder? = null
    // The GoCoder SDK audio device
    private var goCoderAudioDevice: WOWZAudioDevice? = null
    // The GoCoder SDK broadcaster
    private var goCoderBroadcaster: WOWZBroadcast? = null
    // The broadcast configuration settings
    private var goCoderBroadcastConfig: WOWZBroadcastConfig? = null

    init {
        methodChannel.setMethodCallHandler(this)
        goCoder = WowzaGoCoder.init(context, "GOSK-9A47-010C-8DE7-381D-76DB")
        // Set the camera preview to 720p
        goCoderCameraView.frameSize = WOWZSize(1280, 720)
        // Set the cropping mode so that the full frame is displayed, potentially cropped
        goCoderCameraView.scaleMode = WOWZMediaConfig.FILL_VIEW
        // Set the active camera to the front camera if it's not active
        if (!goCoderCameraView.camera.isFront)
            goCoderCameraView.switchCamera()

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = WOWZAudioDevice()

        // Create a broadcaster instance
        goCoderBroadcaster = WOWZBroadcast()

        // Create an audio device instance for capturing and broadcasting audio
        goCoderAudioDevice = WOWZAudioDevice()

        // Create a broadcaster instance
        goCoderBroadcaster = WOWZBroadcast()

        // Create a configuration instance for the broadcaster
        goCoderBroadcastConfig = WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1920x1080)

        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Streaming Cloud live stream
        goCoderBroadcastConfig?.hostAddress = "20ae97.entrypoint.cloud.wowza.com"
        goCoderBroadcastConfig?.portNumber = 1935
        goCoderBroadcastConfig?.applicationName = "app-9887"
        goCoderBroadcastConfig?.streamName = "eea812c0"
        //authentication
        goCoderBroadcastConfig?.username = "client49777"
        goCoderBroadcastConfig?.password = "59afa4d1"

        // Designate the camera preview as the video broadcaster
        goCoderBroadcastConfig?.videoBroadcaster = goCoderCameraView

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig?.audioBroadcaster = goCoderAudioDevice

        goCoderCameraView.startPreview()
    }

    override fun getView(): View {
        return goCoderCameraView
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.i("FlutterWOWZCameraView", "MethodCallHandler: \\nMethod: ${call.method} \\nArguments: ${call.arguments}")
        val activeCamera = this.goCoderCameraView.camera

        when (call.method) {
            "start" -> {
                // Start streaming
                goCoderBroadcaster?.startBroadcast(goCoderBroadcastConfig, this)
            }
            "resume" -> {
                Log.i("FlutterWOWZCameraView", "isPreviewPaused: ${goCoderCameraView.isPreviewPaused}")
                activeCamera.continuePreview()
            }
            "end" -> {
                // Stop the broadcast that is currently broadcasting
                goCoderBroadcaster?.endBroadcast(this)
            }
            "switch_camera" -> {
                goCoderCameraView.switchCamera()
            }
            "pause" -> {
                if (activeCamera.isPreviewing)
                    activeCamera.pausePreview()
            }
            "flashlight " -> {
                activeCamera.isTorchOn = !activeCamera.isTorchOn
            }
            else -> result.notImplemented()
        }
    }

    override fun dispose() {
    }

    override fun onWZStatus(goCoderStatus: WOWZBroadcastStatus?) {
        // A successful status transition has been reported by the GoCoder SDK
        val statusMessage = StringBuffer("Broadcast status: ")

        when (goCoderStatus?.state) {
            BroadcastState.READY -> {
                statusMessage.append("Ready to begin broadcasting")
            }
            BroadcastState.BROADCASTING -> {
                statusMessage.append("Broadcast is active")
            }
            BroadcastState.IDLE -> {
                statusMessage.append("The broadcast is stopped")
            }
            else -> return
        }

        // Display the status message using the U/I thread
        Handler(Looper.getMainLooper()).post {
            methodChannel.invokeMethod("state", goCoderStatus.state?.name)
            Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onWZError(goCoderStatus: WOWZBroadcastStatus?) {
        methodChannel.invokeMethod("error", goCoderStatus?.lastError?.errorDescription)

        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context,
                    "Streaming error: " + goCoderStatus?.lastError?.errorDescription,
                    Toast.LENGTH_LONG).show()
        }
    }


    override fun onWZStatus(status: WOWZStatus?) {
        // containing the error details using the U/I thread
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context,
                    "Streaming WZStatus: " + status?.toString(),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onWZError(status: WOWZStatus?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context,
                    "Streaming nWZError: " + status?.toString(),
                    Toast.LENGTH_LONG).show()
        }
    }
}