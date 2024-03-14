import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hover_ussd/hover_ussd.dart';

import 'models/download_action_state.dart';

class HoverUssd {
  static const MethodChannel _methodChannel = MethodChannel('HoverUssdChannel');
  static const EventChannel _transactionEventChannel = EventChannel('TransactionEvent');
  static const EventChannel _downloadEventChannel = EventChannel('ActionDownloadEvent');

  Stream<TransactionState>? _onTransactionStateChanged;

  /// Initialize HoverUssd with branding and logo information.
  ///
  /// [apiKey] is the Hover API key.
  /// [branding] is the name of the app.
  /// [logo] is the id of the app's logo in ressource.
  /// [notificationLogo] is the id of the app's logo in ressource.
  ///
  Future<void> initialize({
    required String apiKey,
    String? branding,
    String? logo,
    String? notificationLogo,
  }) async {
    await _methodChannel.invokeMethod("Initialize", {
      "branding": branding ?? "Flutter App",
      "logo": logo ?? "ic_launcher",
      "notificationLogo": notificationLogo ?? "ic_launcher",
      "apiKey": apiKey,
    });
  }

  /// Start a transaction with the provided parameters.
  /// [actionId] is the id of the action to be performed.
  /// [extras] is a map of extra parameters to be sent to the action.
  /// [theme] is the theme of the transaction.
  /// [header] is the header of the transaction.
  /// [initialProcessingMessage] is the message to be displayed while the transaction is being processed.
  /// [finalMsgDisplayTime] is the time to display the final message.
  /// [showUserStepDescriptions] is a flag to show the user step descriptions.
  ///
  Future<void> startTransaction({
    required String actionId,
    Map<String, String>? extras,
    String? theme,
    String? header,
    String? initialProcessingMessage,
    int finalMsgDisplayTime = 5000,
    bool? showUserStepDescriptions,
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

  // this check if app accessibility permission is granted
  Future<bool> isAccessibilityEnabled() async {
    return await _methodChannel.invokeMethod("IsAccessibilityEnabled");
  }

  // check if app has overlay permission
  Future<bool> isOverlayEnabled() async {
    return await _methodChannel.invokeMethod("IsOverlayEnabled");
  }
  

  /// Check if the app has downloaded actions.
  Future<bool> hasActionsDownloaded() async {
    return await _methodChannel.invokeMethod("HasActionsDownloaded");
  }

  /// Retrieve a list of available actions.
  Future<List<HoverAction>> getAllActions() async {
    try {
      final result = await _methodChannel.invokeListMethod("getAllActions");
      if (result != null) {
        List<HoverAction> actions =
            result.map((item) => HoverAction.fromMap(item.cast<String, dynamic>())).toList();
        return actions;
      } else {
        return []; 
      }
    } catch (e) {
      print('Error retrieving actions: $e');
      return [];
    }
  }

  //refresh actions 
  Future<void> refreshActions() async {
    await _methodChannel.invokeMethod("refreshActions");
  }

  Future<List<Transaction>> getAllTransactions() async {
    try {
      final result =
          await _methodChannel.invokeListMethod("getAllTransactions");
      if (result != null) {
        final List<Map<String, dynamic>> resultList =
            List<Map<String, dynamic>>.from(result);
        List<Transaction> transactions =
            resultList.map((item) => Transaction.fromMap(item)).toList();
        return transactions;
      } else {
        return []; 
      }
    } catch (e) {
      print('Error retrieving transactions: $e');
      return []; 
    }
  }

  /// Get the stream of transaction states.
  Stream<TransactionState> getUssdTransactionState() {
    if (_onTransactionStateChanged == null) {
      _onTransactionStateChanged ??= _transactionEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => _parseTransactionState(
                event['state'],
                Map<String, dynamic>.from(event),
              ))
          .asBroadcastStream();
    }

    return _onTransactionStateChanged!;
  }

  Stream<DonwloadActionState> getDownloadActionState() {
    return _downloadEventChannel
        .receiveBroadcastStream()
        .map((dynamic event) => _parseDownloadActionState(
              event['state'],
              Map<String, dynamic>.from(event),
            ))
      .asBroadcastStream();
  }

  DonwloadActionState _parseDownloadActionState(
    String state,
    Map<String, dynamic> result,
  ) {
    switch (state) {
      case "actionDownloaded":
        return ActionDownloaded.fromMap(result);
      case "actionDownloadFailed":
        return ActionDownloadFailed.fromMap(result);
      case "actionDownloading":
        return ActionDownloading();
      default:
        return EmptyDownloadState();
    }
  }

  /// Parse transaction state from received event.
  TransactionState _parseTransactionState(
    String state,
    Map<String, dynamic> result,
  ) {
    print("State: $state");
    switch (state) {
      case "smsParsed":
        return SmsParsed.fromMap(result);
      case "ussdSucceeded":
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
