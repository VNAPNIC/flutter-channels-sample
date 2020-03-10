import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
      child: WOWZCameraView(),
    ));
  }

  void _onTextViewCreated(TextViewController controller) {
    controller.setText('Con me may channels nhu con cac');
  }
}

/// WOWZCameraView

class WOWZCameraView extends StatefulWidget {
  @override
  _WOWZCameraViewState createState() => _WOWZCameraViewState();
}

class _WOWZCameraViewState extends State<WOWZCameraView> {
  var viewId = 0;
  MethodChannel _channel;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Stack(
        children: <Widget>[
          SizedBox(
              height: 720,
              width: 1280,
              child: (defaultTargetPlatform == TargetPlatform.android)
                  ? AndroidView(
                      viewType: 'platform_wowz_camera_view',
                      onPlatformViewCreated: _onPlatformViewCreated,
                    )
                  : (defaultTargetPlatform == TargetPlatform.iOS)
                      ? UiKitView(
                          viewType: 'platform_wowz_camera_view',
                          onPlatformViewCreated: _onPlatformViewCreated,
                        )
                      : Text(
                          '$defaultTargetPlatform is not yet supported by the text_view plugin')),
          Wrap(
            children: <Widget>[
              _action('start', 'start'),
              _action('end', 'end'),
              _action('open_camera', 'open_camera'),
              _action('resume', 'resume'),
              _action('pause', 'pause'),
              _action('switch_camera', 'switch_camera'),
              _action('flashlight ', 'flashlight '),
            ],
          )
        ],
      ),
    );
  }

  _action(text, event) => RaisedButton(   
      child: Text(text),
      onPressed: () {
        _channel?.invokeMethod(event);
      });

  void _onPlatformViewCreated(int id) {
    viewId = id;
    _channel = MethodChannel('platform_wowz_camera_view_$id');
    _channel.setMethodCallHandler((call) async {
      print(
          'MethodCallHandler: \nMethod: ${call.method} \nArguments: ${call.arguments}');
      switch (call.method) {
        case 'state':
          _state(call.arguments);
          break;
        case 'error':
          break;
      }
    });
  }

  _state(arguments) {
    switch (arguments) {
      case 'IDLE':
        break;
      case 'READY':
        break;
      case 'BROADCASTING':
        break;
    }
  }
}

/// TextView
typedef void TextViewCreatedCallback(TextViewController controller);

class TextView extends StatefulWidget {
  const TextView({
    Key key,
    this.onTextViewCreated,
  }) : super(key: key);

  final TextViewCreatedCallback onTextViewCreated;

  @override
  _TextViewState createState() => _TextViewState();
}

class _TextViewState extends State<TextView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'platform_text_view',
        onPlatformViewCreated: _onPlatformTextViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformTextViewCreated(int id) {
    if (widget.onTextViewCreated == null) {
      return;
    }
    widget.onTextViewCreated(TextViewController._(id));
  }

  @override
  void dispose() {
    super.dispose();
  }
}

class TextViewController {
  TextViewController._(int id)
      : _channel = MethodChannel('platform_text_view_$id');

  final MethodChannel _channel;

  Future<void> setText(String text) async {
    assert(text != null);
    return _channel.invokeMethod('setText', text);
  }
}
