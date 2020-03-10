//
//  WOWZCameraView.swift
//  Runner
//
//  Created by NamIT on 3/10/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation
import UIKit
import WowzaGoCoderSDK

public class FlutterWOWZCameraView : NSObject, FlutterPlatformView,WOWZBroadcastStatusCallback, WOWZVideoSink, WOWZAudioSink{
    
    var cameraView: WOWZCameraPreview?
    // The top-level GoCoder API interface
    var goCoder: WowzaGoCoder?
    // The broadcast configuration settings
    let goCoderConfig: WowzaConfig = WowzaConfig()
    
    let channel: FlutterMethodChannel
    let controller: FlutterViewController
    let frame:CGRect
    let viewId: Int64
    
    public func view() -> UIView {
        print("------> getView")
        if(goCoder==nil){
            return UIView(frame: frame)
        } else{
            return goCoder!.cameraView!
        }
    }

    
    public init(_ frame: CGRect,viewId: Int64,channel: FlutterMethodChannel,controller: FlutterViewController, args: Any?) {
        self.channel = channel
        self.controller = controller
        self.frame = frame
        self.viewId = viewId
        super.init()
        
        WowzaGoCoder.setLogLevel(.default)
        
        channel.setMethodCallHandler({
            (call: FlutterMethodCall, result:FlutterResult) -> Void in
            
            print("--------> method: " + call.method)
            
            switch call.method{
            case "start":
                self.goCoder?.startStreaming(self)
                break;
            case "open_camera":
                 self.cameraView?.start()
                break;
            default:
                print("--------> nothing")
            }
        })
        
        if WowzaGoCoder.registerLicenseKey("GOSK-9B47-010C-876F-8576-5679") == nil {
            print("Register licenseKey is success!")
            if let goCoder = WowzaGoCoder.sharedInstance(){
                self.goCoder = goCoder
                print("goCoder make instance sucess!")
                // Request camera and microphone permissions
                WowzaGoCoder.requestPermission(for: .camera, response: { (permission) in
                print("Camera permission is: \(permission == .authorized ? "authorized" : "denied")")
                })

                WowzaGoCoder.requestPermission(for: .microphone, response: { (permission) in
                print("Microphone permission is: \(permission == .authorized ? "authorized" : "denied")")
                })
                
//                goCoderBroadcastConfig?.hostAddress = "20ae97.entrypoint.cloud.wowza.com"
//                goCoderBroadcastConfig?.portNumber = 1935
//                goCoderBroadcastConfig?.applicationName = "app-9887"
//                goCoderBroadcastConfig?.streamName = "eea812c0"
//                //authentication
//                goCoderBroadcastConfig?.username = "client49777"
//                goCoderBroadcastConfig?.password = "59afa4d1"
                
                self.goCoderConfig.hostAddress = "20ae97.entrypoint.cloud.wowza.com"
                self.goCoderConfig.portNumber = 1935
                self.goCoderConfig.applicationName = "app-9887"
                self.goCoderConfig.streamName = "eea812c0"
                // authentication
                self.goCoderConfig.username = "client49777"
                self.goCoderConfig.password = "59afa4d1"
                // Create a configuration instance for the broadcaster
                self.goCoderConfig.load(WOWZFrameSizePreset.preset1280x720)
                
                self.goCoder?.register(self as WOWZAudioSink)
                self.goCoder?.register(self as WOWZVideoSink)
                self.goCoder?.config = self.goCoderConfig

                // Specify the view in which to display the camera preview
                self.goCoder?.cameraView = UIView(frame: frame)
                
                cameraView = self.goCoder?.cameraPreview
                
            }else{
                print("goCoder make instance fail!")
            }
        }else{
            print("Register licenseKey is fail!")
        }
    }
    
    public func onWOWZStatus(_ status: WOWZBroadcastStatus!) {
        print("status -----------> " + String(status.state.rawValue))
    }
    
    public func onWOWZError(_ status: WOWZBroadcastStatus!) {
        print("error -----------> " + String(status.description))
    }
    
    public func videoFrameWasCaptured(_ imageBuffer: CVImageBuffer, framePresentationTime: CMTime, frameDuration: CMTime) {}
      
}
