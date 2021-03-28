import 'dart:async';
import 'package:flutter/services.dart';

enum TransactionState {
  // when the ussd code run succesfully

  succesfull,
  // when the ussd code failed; this can be caused by user
  // dissmiss or request refuse
  failed,
  // this is when hover failed to dowload the list of your
  // action , usualy when the app is not connected
  actionDowaloadFailed
}

class HoverUssd {
  static const MethodChannel _methodChannel = MethodChannel('HoverUssdChannel');
  static const EventChannel _eventChannel = EventChannel('TransactionEvent');

  static Future<void> initialize({String? branding, String? logo}) async {
    await _methodChannel.invokeMethod("Initialize",
        {"branding": branding ?? "Flutter App", "logo": logo ?? "ic_launcher"});
  }

  Stream<TransactionState>? _onTransactionStateChanged;

  // when you call send ussd, automatically the ussd procces begin
  // and the permission is autaumatically handle
  // this method came with 3 params
  // @actionId is the id for the action you trying to call
  // cc: https://docs.usehover.com/ussd
  // @extras is the extras variable which came with action id , you can
  // convigure tha on : https://www.usehover.com/
  // optional: if you want to customize the ussd theme then you can add a theme at
  // your style.xml and then add the style string on @theme param
  Future<void> sendUssd(
          {required String actionId,
          Map<String, String>? extras,
          String? theme}) async =>
      await _methodChannel.invokeMethod("HoverStartATransaction",
          {"actionId": actionId, "extras": extras ?? {}, "theme": theme});

  // this is for getting response of the current ussd session

  Stream<TransactionState>? get getUssdTransactionState {
    if (_onTransactionStateChanged == null) {
      _onTransactionStateChanged = _eventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => _parseTransactionState(event));
    }
    return _onTransactionStateChanged;
  }

  TransactionState _parseTransactionState(String? state) {
    switch (state) {
      case "success":
        return TransactionState.succesfull;
      case "failed":
        return TransactionState.failed;
      case "actionDownloadFailed":
        return TransactionState.actionDowaloadFailed;
      default:
        throw ArgumentError('$state');
    }
  }
}
