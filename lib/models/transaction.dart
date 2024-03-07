class Transaction {
  final String? id;
  final String? actionId;
  final String? uuid;
  final String? status;
  final String? category;
  final String? userMessage;
  final String? networkHni;
  final String? inputExtras;
  final String? parsedVariables;
  final List<dynamic>? ussdMessages;
  final List<dynamic>? enteredValues;
  final List<dynamic>? smsHits;
  final List<dynamic>? smsMisses;
  final List<dynamic>? logMessages;
  final List<dynamic>? matchedParsers;
  final int? reqTimestamp;
  final int? updatedTimestamp;

  Transaction(
      {this.uuid,
      this.id,
      this.actionId,
      this.status,
      this.category,
      this.userMessage,
      this.networkHni,
      this.inputExtras,
      this.parsedVariables,
      this.ussdMessages,
      this.enteredValues,
      this.smsHits,
      this.smsMisses,
      this.logMessages,
      this.matchedParsers,
      this.reqTimestamp,
      this.updatedTimestamp});

  factory Transaction.fromMap(Map<String, dynamic> map) {
    return Transaction(
      uuid: map['uuid'],
      id: map['id'],
      actionId: map['actionId'],
      status: map['status'],
      category: map['category'],
      userMessage: map['userMessage'],
      networkHni: map['networkHni'],
      inputExtras: map['inputExtras'],
      parsedVariables: map['parsedVariables'],
      ussdMessages: map['ussdMessages'],
      enteredValues: map['enteredValues'],
      smsHits: map['smsHits'],
      smsMisses: map['smsMisses'],
      logMessages: map['logMessages'],
      matchedParsers: map['matchedParsers'],
      reqTimestamp: map['reqTimestamp'],
      updatedTimestamp: map['updatedTimestamp'],
    );
  }
}


