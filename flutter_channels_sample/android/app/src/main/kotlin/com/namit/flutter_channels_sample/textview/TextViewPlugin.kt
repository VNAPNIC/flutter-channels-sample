package com.namit.flutter_channels_sample.textview

import com.namit.flutter_channels_sample.textview.TextViewFactory
import io.flutter.plugin.common.PluginRegistry.Registrar


object TextViewPlugin {
    fun registerWith(registrar: Registrar) {
        registrar.platformViewRegistry().registerViewFactory(
                        "platform_text_view", TextViewFactory(registrar.messenger()))
    }
}