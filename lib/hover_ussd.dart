import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

enum TransactionState { succesfull, waiting, failed }

class HoverUssd {
  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;

  factory HoverUssd() {
    if (_instance == null) {
      final MethodChannel methodChannel = const MethodChannel('hover_ussd');
      final EventChannel eventChannel = const EventChannel('transaction_event');
      _instance = HoverUssd.private(methodChannel, eventChannel);
    }
    return _instance;
  }

  static HoverUssd _instance;

  @visibleForTesting
  HoverUssd.private(this._methodChannel, this._eventChannel);

  Stream<TransactionState> _onTransactionStateChanged;

  Future sendUssd(
          {@required String actionId, Map<String, String> extras}) async =>
      await _methodChannel.invokeMethod(
          "hoverStartTransaction", {"action_id": actionId, "extras": extras});

  Stream<TransactionState> get onTransactiontateChanged {
    if (_onTransactionStateChanged == null) {
      _onTransactionStateChanged = _eventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => _parseTransactionState(event));
    }
    return _onTransactionStateChanged;
  }

  Future initialize() async {
    await _methodChannel.invokeMethod("hoverInitial");
  }

  TransactionState _parseTransactionState(String state) {
    switch (state) {
      case "succeeded":
        return TransactionState.succesfull;
        break;
      case "pending":
        return TransactionState.waiting;
      case "failed":
        return TransactionState.failed;
      default:
        throw ArgumentError('$state');
    }
  }
}
