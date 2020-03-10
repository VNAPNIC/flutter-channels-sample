package com.namit.flutter_channels_sample.wowza

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class WOWZCameraViewFactory (private val messenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE){
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val methodChannel = MethodChannel(messenger, "platform_wowz_camera_view_$viewId")
        val params = args?.let { args as? Map<String, Any> }
        return FlutterWOWZCameraView(context, methodChannel, viewId, params)
    }

}