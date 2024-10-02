abstract class DonwloadActionState {
  const DonwloadActionState();
  Map<String, dynamic> toMap();
}

class ActionDownloadFailed extends DonwloadActionState {
  final String error;

  ActionDownloadFailed(this.error);

  @override
  Map<String, dynamic> toMap() {
    return {
      "state": "actionDownloadFailed",
      "error": error,
    };
  }

  factory ActionDownloadFailed.fromMap(Map<String, dynamic> map) {
    return ActionDownloadFailed(map['error']);
  }
}

class ActionDownloading extends DonwloadActionState {
  @override
  Map<String, dynamic> toMap() {
    return {
      "state": "actionDownloading",
    };
  }
}

class ActionDownloaded extends DonwloadActionState {
  final bool isDownloaded;

  ActionDownloaded(this.isDownloaded);

  @override
  Map<String, dynamic> toMap() {
    return {
      "state": "actionDownloaded",
      "isDownloaded": isDownloaded,
    };
  }

  factory ActionDownloaded.fromMap(Map<String, dynamic> map) {
    return ActionDownloaded(map['isDownloaded']);
  }

}

class EmptyDownloadState extends DonwloadActionState {
  @override
  Map<String, dynamic> toMap() {
    return {
      "state": "empty",
    };
  }
}