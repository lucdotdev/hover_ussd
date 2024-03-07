import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hover_ussd/models/hover_action.dart';

import 'models/transaction.dart';
import 'models/transaction_state.dart';

class HoverUssd {
  static const MethodChannel _methodChannel =
      MethodChannel('HoverUssdChannel');
  static const EventChannel _eventChannel =
      EventChannel('TransactionEvent');

  Stream<TransactionState>? _onTransactionStateChanged;

  /// Initialize HoverUssd with branding and logo information.
  Future<List<HoverAction>?> initialize(
      {String? branding, String? logo}) async {
    List<HoverAction>? hoverActions = await _methodChannel.invokeMethod(
        "Initialize",
        {"branding": branding ?? "Flutter App", "logo": logo ?? "ic_launcher"});

    return hoverActions;
  }

  /// Start a transaction with the provided parameters.
  Future<void> startTransaction({
    required String actionId,
    Map<String, String>? extras,
    String? theme,
    String? header,
    String? initialProcessingMessage,
    int finalMsgDisplayTime = 5000, bool? showUserStepDescriptions,
  }) async {
    await _methodChannel.invokeMethod("HoverStartATransaction", {
      "actionId": actionId,
      "extras": extras ?? {},
      "theme": theme,
      "header": header,
      "initialProcessingMessage": initialProcessingMessage,
      "finalMsgDisplayTime": finalMsgDisplayTime,
    });
  }

  /// Check if the app has all the necessary permissions.
  Future<bool> hasAllPermissions() async {
    return await _methodChannel.invokeMethod("HasAllPermissions");
  }

  /// Check if the app has downloaded actions.
  Future<bool> hasActionsDownloaded() async {
    return await _methodChannel.invokeMethod("HasActionsDownloaded");
  }

  /// Retrieve a list of available actions.
  Future<List<HoverAction>> getActions() async {
    List<Map<String, dynamic>> result = await _methodChannel.invokeListMethod(
        "getAllActions") as List<Map<String, dynamic>>;
    List<HoverAction> actions = result.map((item) => HoverAction.fromMap(item)).toList();
    return actions;
  }

  /// Deprecated: Retrieve all transactions.
  @Deprecated('As usehover v2')
  Future<List<Transaction>> getAllTransactions() async {
    List<Map<String, dynamic>> result = await _methodChannel
        .invokeListMethod("getAllTransaction") as List<Map<String, dynamic>>;
    List<Transaction> transactions = result.map((item) => Transaction.fromMap(item)).toList();
    return transactions;
  }

  /// Get the stream of transaction states.
  Stream<TransactionState> getUssdTransactionState() {
    if (_onTransactionStateChanged == null) {
      _onTransactionStateChanged ??= _eventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => _parseTransactionState(
                event['state'],
                Map<String, dynamic>.from(event),
              ))
          .asBroadcastStream();
    }

    return _onTransactionStateChanged!;
  }

  /// Parse transaction state from received event.
  TransactionState _parseTransactionState(
    String state,
    Map<String, dynamic> result,
  ) {
    switch (state) {
      case "smsParsed":
        return SmsParsed.fromMap(result);
      case "ussdSucceded":
        return UssdSucceeded.fromMap(result);
      case "ussdFailed":
        return UssdFailed.fromMap(result);
      case "ussdLoading":
        return UssdLoading();
      default:
        return EmptyState();
    }
  }
}