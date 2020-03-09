package com.namit.flutter_channels_sample

import androidx.annotation.NonNull;
import com.namit.flutter_channels_sample.textview.TextViewPlugin
import com.namit.flutter_channels_sample.wowza.WOWZCameraViewPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        val shimPluginRegistry = ShimPluginRegistry(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        TextViewPlugin.registerWith(shimPluginRegistry.registrarFor("platform_text_view"))
        WOWZCameraViewPlugin.registerWith(shimPluginRegistry.registrarFor("platform_wowz_camera_view"))
    }
}
