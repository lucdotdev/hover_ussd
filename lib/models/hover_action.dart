class HoverAction {
  String id;
  String name;
  String networkName;
  String rootCode;
  String transportType;
  String country;
  List<String> steps;
  List<String> hnis;

  HoverAction({
    required this.id,
    required this.name,
    required this.networkName,
    required this.rootCode,
    required this.transportType,
    required this.country,
    required this.steps,
    required this.hnis,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'networkName': networkName,
      'rootCode': rootCode,
      'transportType': transportType,
      'country': country,
      'steps': steps,
      'hnis': hnis,
    };
  }

  //from map
  factory HoverAction.fromMap(Map<String, dynamic> json) {
    return HoverAction(
      id: json['id'] as String,
      name: json['name'] as String,
      networkName: json['networkName'] as String,
      rootCode: json['rootCode'] as String,
      transportType: json['transportType'] as String,
      country: json['country'] as String,
      steps: json['steps'].cast<String>(),
      hnis: json['hnis'].cast<String>(),
    );
  }
}