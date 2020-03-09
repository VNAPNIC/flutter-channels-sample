package com.namit.flutter_channels_sample.textview

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class FlutterTextView internal constructor(context: Context?, messenger: BinaryMessenger?, id: Int?,
                                           params: Map<String, Any>?) : PlatformView, MethodChannel.MethodCallHandler {
    private val textView: TextView = TextView(context)
    private val methodChannel: MethodChannel = MethodChannel(messenger, "platform_text_view_$id")

    init {
        methodChannel.setMethodCallHandler(this)
        textView.textSize = 30f
        textView.setTextColor(Color.parseColor("#000000"))
        textView.gravity = Gravity.CENTER
        textView.viewTreeObserver.addOnGlobalLayoutListener{
            Log.e("xx","parent:"+textView.parent.javaClass.name)

        }
    }
    override fun getView(): View {
        return textView
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "setText" -> setText(methodCall, result)
            else -> result.notImplemented()
        }
    }

    private fun setText(methodCall: MethodCall, result: MethodChannel.Result) {
        val text = methodCall.arguments as String
        textView.text = text
        result.success(null)
    }

    override fun dispose() {

    }
}