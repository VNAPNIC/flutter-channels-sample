package com.namit.flutter_channels_sample.wowza

import android.content.Context
import android.view.View
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView
import com.wowza.gocoder.sdk.api.geometry.WOWZSize
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView


class FlutterWOWZCameraView internal constructor(context: Context?, messenger: BinaryMessenger?, id: Int?, params: Map<String, Any>?) : PlatformView, MethodChannel.MethodCallHandler {

    private val goCoderCameraView: WOWZCameraView = WOWZCameraView(context)
    private val methodChannel: MethodChannel = MethodChannel(messenger, "platform_wowz_camera_view_$id")

    init {
        methodChannel.setMethodCallHandler(this)
        // Set the camera preview to 720p
        goCoderCameraView.frameSize = WOWZSize(1280, 720)
        // Set the cropping mode so that the full frame is displayed, potentially cropped
        goCoderCameraView.scaleMode = WOWZMediaConfig.FILL_VIEW
        // Set the active camera to the front camera if it's not active
        if (!goCoderCameraView.camera.isFront)
            goCoderCameraView.switchCamera()
    }

    override fun getView(): View {
        return goCoderCameraView
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "start" -> goCoderCameraView.startPreview()
            else -> result.notImplemented()
        }
    }

    override fun dispose() {
    }
}