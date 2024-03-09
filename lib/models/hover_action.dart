class HoverAction {
  int? id;
  String? publicId;
  String? name;
  int? channelId;
  String? networkName;
  String? countryAlpha2;
  String? rootCode;
  String? transportType;
  String? transactionType;
  int? fromInstitutionId;
  String? fromInstitutionName;
  String? fromInstitutionLogo;
  int? toInstitutionId;
  String? toInstitutionName;
  String? toInstitutionLogo;
  String? toCountryAlpha2;
  int? createdTimestamp;
  int? updatedTimestamp;
  int? bountyAmount;
  bool? bountyIsOpen;
  bool? isReady;
  int? bonusPercent;
  String? bonusMessage;
  List<dynamic>? hniList;
  List<dynamic>? customSteps;
  List<dynamic>? tagsList;
  Map<String, dynamic>? requiredParams;
  Map<String, dynamic>? outputParams;

  HoverAction({
    this.id,
    this.publicId,
    this.name,
    this.channelId,
    this.networkName,
    this.countryAlpha2,
    this.rootCode,
    this.transportType,
    this.transactionType,
    this.fromInstitutionId,
    this.fromInstitutionName,
    this.fromInstitutionLogo,
    this.toInstitutionId,
    this.toInstitutionName,
    this.toInstitutionLogo,
    this.toCountryAlpha2,
    this.createdTimestamp,
    this.updatedTimestamp,
    this.bountyAmount,
    this.bountyIsOpen,
    this.isReady,
    this.bonusPercent,
    this.bonusMessage,
    this.hniList,
    this.customSteps,
    this.tagsList,
    this.requiredParams,
    this.outputParams,
  });

  factory HoverAction.fromMap(Map<String, dynamic> json) {
    return HoverAction(
      id: json['id'],
      publicId: json['public_id'],
      name: json['name'],
      channelId: json['channel_id'],
      networkName: json['network_name'],
      countryAlpha2: json['country_alpha2'],
      rootCode: json['root_code'],
      transportType: json['transport_type'],
      transactionType: json['transaction_type'],
      fromInstitutionId: json['from_institution_id'],
      fromInstitutionName: json['from_institution_name'],
      fromInstitutionLogo: json['from_institution_logo'],
      toInstitutionId: json['to_institution_id'],
      toInstitutionName: json['to_institution_name'],
      toInstitutionLogo: json['to_institution_logo'],
      toCountryAlpha2: json['to_country_alpha2'],
      createdTimestamp: json['created_timestamp'],
      updatedTimestamp: json['updated_timestamp'],
      bountyAmount: json['bounty_amount'],
      bountyIsOpen: json['bounty_is_open'],
      isReady: json['is_ready'],
      bonusPercent: json['bonus_percent'],
      bonusMessage: json['bonus_message'],
      requiredParams: json['required_params'],
      outputParams: json['output_params'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'public_id': publicId,
      'name': name,
      'channel_id': channelId,
      'network_name': networkName,
      'country_alpha2': countryAlpha2,
      'root_code': rootCode,
      'transport_type': transportType,
      'transaction_type': transactionType,
      'from_institution_id': fromInstitutionId,
      'from_institution_name': fromInstitutionName,
      'from_institution_logo': fromInstitutionLogo,
      'to_institution_id': toInstitutionId,
      'to_institution_name': toInstitutionName,
      'to_institution_logo': toInstitutionLogo,
      'to_country_alpha2': toCountryAlpha2,
      'created_timestamp': createdTimestamp,
      'updated_timestamp': updatedTimestamp,
      'bounty_amount': bountyAmount,
      'bounty_is_open': bountyIsOpen,
      'is_ready': isReady,
      'bonus_percent': bonusPercent,
      'bonus_message': bonusMessage,
    };
  }
}
