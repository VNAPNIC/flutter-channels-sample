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
    if (defaultTargetPlatform == TargetPlatform.android) {
      return SingleChildScrollView(
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 720,
              width: 1280,
              child: AndroidView(
                viewType: 'platform_wowz_camera_view',
                onPlatformViewCreated: _onPlatformViewCreated,
              ),
            ),
            RaisedButton(onPressed: () {
              _channel?.invokeMethod('start');
            })
          ],
        ),
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    print('----------------------> $id');
    viewId = id;
    _channel = MethodChannel('platform_wowz_camera_view_$id');
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
    print('----------------------> $id');
    if (widget.onTextViewCreated == null) {
      return;
    }
    widget.onTextViewCreated(TextViewController._(id));
  }

  @override
  void dispose() {
    print('----------------------> dispose');
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
