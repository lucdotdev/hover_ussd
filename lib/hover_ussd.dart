import 'dart:async';

import 'package:flutter/services.dart';

class HoverUssd {
  static const MethodChannel _channel = const MethodChannel('hover_ussd');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future sendUssd(String actionId, Map<String, String> extras) async {
    await _channel.invokeMethod(
        "hoverStartTransaction", {"action_id": actionId, "extras": extras});
  }
}
