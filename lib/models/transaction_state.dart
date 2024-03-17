abstract class TransactionState {
  TransactionState();

  Map<String, dynamic> toMap();
}

/// when the message(sms) is successfully parsed
class SmsParsed extends TransactionState {
  /// Unique Identifier for the transaction
  final String? uuid;

  /// The action id from out supported operators page
  final String? actionId;

  /// Full message used for parsing
  final String? responseMessage;

  /// “pending”, “failed” or “succeeded”
  final String? status;

  /// What you specified for the latest matched parser or one of the default failed cases above
  final String? statusMeaning;

  /// Message you specified for the latest matched parser
  final String? statusDescription;

  /// Unique identifier for the parser which matched, causing this transaction to update
  final int? matchedParserId;

  ///	“ussd” or “sms”
  final String? messagetype;

  ///	If SMS, the sender id from the parser form, null if USSD
  final String? messageSender;

  /// Regular expression you specified in the parser form
  final String? regex;

  /// The Home Network Identifier (MCC + MNC) of the SIM used
  final String? simHni;

  /// 0 for normal, 1 for debug, 2 for test
  final int? environment;

  /// Time user initiated transaction (Unix time)
  final int? requestTimestamp;

  /// Time at which the transaction last updated (SMS arrival or USSD finished)
  final int? updateTimestamp;

  /// (depreciated)	Same as updated_timestamp
  final int? responseTimestamp;

  /// A HashMap object of all the extras you passed in using .extra(key, value)
  final Map<String, String>? inputExtras;

  /// A HashMap object of all named groups parsed out of the response message based on your regex
  final Map<String, String>? parsedVariables;

  /// Array of all USSD session messages in order encountered
  final List<String>? sessionMessages;

  SmsParsed(
      {this.uuid,
      this.actionId,
      this.responseMessage,
      this.status,
      this.statusMeaning,
      this.statusDescription,
      this.matchedParserId,
      this.messagetype,
      this.messageSender,
      this.regex,
      this.simHni,
      this.environment,
      this.requestTimestamp,
      this.updateTimestamp,
      this.responseTimestamp,
      this.inputExtras,
      this.parsedVariables,
      this.sessionMessages});
  @override
  Map<String, dynamic> toMap() {
    return {
      'uuid': uuid,
      'action_id': actionId,
      'response_message': responseMessage,
      'status': status,
      'status_meaning': statusMeaning,
      'status_description': statusDescription,
      'matched_parser_id': matchedParserId,
      'messagetype': messagetype,
      'message_sender': messageSender,
      'regex': regex,
      'sim_hni': simHni,
      'environment': environment,
      'request_timestamp': requestTimestamp,
      'update_timestamp': updateTimestamp,
      'response_timestamp': responseTimestamp,
      'input_extras': inputExtras,
      'parsed_variables': parsedVariables,
      'session_messages': sessionMessages,
    };
  }

  factory SmsParsed.fromMap(Map<String, dynamic> json) {
    return SmsParsed(
      uuid: json['uuid'] as String?,
      sessionMessages: json['session_messages'] as List<String>?,
      inputExtras: json['input_extras'] as Map<String, String>?,
      parsedVariables: json['parsed_variables'] as Map<String, String>?,
      responseTimestamp: json['response_timestamp'] as int?,
      updateTimestamp: json['update_timestamp'] as int?,
      requestTimestamp: json['request_timestamp'] as int?,
      environment: json['environment'] as int?,
      simHni: json['sim_hni'] as String?,
      regex: json['regex'] as String?,
      messageSender: json['message_sender'] as String?,
      messagetype: json['messagetype'] as String?,
      matchedParserId: json['matched_parser_id'] as int?,
      statusDescription: json['status_description'] as String?,
      statusMeaning: json['status_meaning'] as String?,
      actionId: json['action_id'] as String?,
      responseMessage: json['response_message'] as String?,
      status: json['status'] as String?,
    );
  }
}

/// when ussd session run succesfully
class UssdSucceeded extends TransactionState {
  /// Unique Identifier for the transaction
  final String? uuid;

  /// The action id from out supported operators page
  final String? actionId;

  /// Full message used for parsing
  final String? responseMessage;

  UssdSucceeded({this.uuid, this.actionId, this.responseMessage});
  @override
  Map<String, dynamic> toMap() {
    return {
      'uuid': uuid,
      'action_id': actionId,
      'response_message': responseMessage,
    };
  }

  factory UssdSucceeded.fromMap(Map<String, dynamic> json) {
    return UssdSucceeded(
      uuid: json['uuid'] as String?,
      actionId: json['action_id'] as String?,
      responseMessage: json['response_message'] as String?,
    );
  }
}

/// when the ussd code failed; this can be caused by user
/// dissmiss or request refuse
class UssdFailed extends TransactionState {
  /// error message if ussd call failed
  final String? errorMessage;

  UssdFailed({this.errorMessage});

  factory UssdFailed.fromMap(Map<String, dynamic> json) {
    return UssdFailed(errorMessage: json["errorMessage"]);
  }

  @override
  Map<String, dynamic> toMap() {
    return {"errorMessage": errorMessage};
  }
}

class UssdLoading extends TransactionState {
  @override
  Map<String, dynamic> toMap() {
   return {};
  }
}


class EmptyState extends TransactionState {
  @override
  Map<String, dynamic> toMap() {
    return {};
  }
}