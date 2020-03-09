package com.namit.flutter_channels_sample.wowza

import io.flutter.plugin.common.PluginRegistry

object WOWZCameraViewPlugin {
    fun registerWith(registrar: PluginRegistry.Registrar) {
        registrar.platformViewRegistry().registerViewFactory(
                "platform_wowz_camera_view", WOWZCameraViewFactory(registrar.messenger()))
    }
}