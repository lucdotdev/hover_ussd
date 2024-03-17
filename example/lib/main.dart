import 'dart:async';
import 'package:flutter/material.dart';
import 'package:hover_ussd/hover_ussd.dart';
import 'package:hover_ussd/models/transaction_state.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hover USSD Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late final HoverUssd _hoverUssd = HoverUssd();
  late StreamSubscription _transactionListening;
  late StreamSubscription _actionDownloadListening;
  bool _hasPermissions = false;
  bool _isOverlayEnabled = false;
  bool _isAccessibilityEnabled = false;

  final String activityName = "com.hover.sdk.permissions.PermissionActivity";

  @override
  void initState() {
    _transactionListening =
        _hoverUssd.getUssdTransactionState().listen((event) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text(event.toMap().toString())));
    });

    _actionDownloadListening =
        _hoverUssd.getDownloadActionState().listen((event) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text(event.toString())));
    });
    _checkAccessibility();
    _checkPermissions();
    _checkOverlay();
    _actionDownloadListening.onData((event) {
      _initHover();
    });

    super.initState();
  }

  @override
  void dispose() {
    _transactionListening.cancel();
    _actionDownloadListening.cancel();
    super.dispose();
  }
  //check for permissions

  Future<void> _checkPermissions() async {
    _hasPermissions = await _hoverUssd.hasAllPermissions();
    setState(() {});
  }

  Future<void> _checkAccessibility() async {
    _isAccessibilityEnabled = await _hoverUssd.isAccessibilityEnabled();
    setState(() {});
  }

  Future<void> _checkOverlay() async {
    _isOverlayEnabled = await _hoverUssd.isOverlayEnabled();
    setState(() {});
  }

  Future<void> _initHover() async {
    try {
      await _hoverUssd.initialize(
        apiKey: "15ccc2bd81801d8c5fbfd5847d3b4e77",
        branding: "My Hover App",
        logo: "ic_launcher",
        notificationLogo: "ic_launcher",
      );

      _hoverUssd.setPermissionsActivity(activityName: activityName);
    } catch (e) {
      print(e);
    }
  }

  Future<void> _getAndGoToActions() async {
    final actions = await _hoverUssd.getAllActions();
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => HoverActionListPage(
          hoverActions: actions,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hover Ussd Example'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () {
                _hoverUssd.startTransaction(
                  actionId: "c6e45e62",
                  extras: {"price": "4000"},
                  theme: "HoverTheme",
                  header: "Hover Ussd Example",
                  showUserStepDescriptions: true,
                );
              },
              child: const Text("Start Transaction"),
            ),
            ElevatedButton(
              onPressed: _getAndGoToActions,
              child: const Text("Get Actions"),
            ),

            //refresh actions
            ElevatedButton(
              onPressed: () {
                _hoverUssd.refreshActions();
              },
              child: const Text("Refresh Actions"),
            ),
            Text(
              _hasPermissions
                  ? "Permissions Granted"
                  : "Permissions Not Granted",
              style: TextStyle(
                color: _hasPermissions ? Colors.green : Colors.red,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              _isAccessibilityEnabled
                  ? "Accessibility Granted"
                  : "Accessibility Not Granted",
              style: TextStyle(
                color: _isAccessibilityEnabled ? Colors.green : Colors.red,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              _isOverlayEnabled ? "Overlay Granted" : "Overlay Not Granted",
              style: TextStyle(
                color: _isOverlayEnabled ? Colors.green : Colors.red,
                fontWeight: FontWeight.bold,
              ),
            ),

            StreamBuilder<DonwloadActionState>(
              stream: _hoverUssd.getDownloadActionState(),
              builder: (context, snapshot) {
                final state = snapshot.data;

                String statusText;
                Color textColor;

                if (state is ActionDownloaded) {
                  statusText = "Actions Downloaded";
                  textColor = Colors.green;
                } else if (state is ActionDownloadFailed) {
                  statusText = "Actions Not Downloaded";
                  textColor = Colors.red;
                } else if (state is ActionDownloading) {
                  statusText = "Actions Downloading";
                  textColor =
                      Colors.blue; // Adjust color as per your preference
                } else {
                  statusText = "Action Download Status: Unknown";
                  textColor = Colors.grey;
                }

                return Text(
                  statusText,
                  style:
                      TextStyle(color: textColor, fontWeight: FontWeight.bold),
                );
              },
            ),
            StreamBuilder<TransactionState>(
              stream: _hoverUssd.getUssdTransactionState(),
              builder: (BuildContext context, snapshot) {
                if (snapshot.data is SmsParsed) {
                  return Text("Sms parsed : \n${snapshot.data!.toMap()}");
                }
                if (snapshot.data is UssdSucceeded) {
                  return Text("Ussd Succeded : \n${snapshot.data!.toMap()}");
                }
                if (snapshot.data is UssdLoading) {
                  return const Text("loading...");
                }
                if (snapshot.data is UssdFailed) {
                  return Text("Ussd Failed : \n${snapshot.data!.toMap()}");
                }
                if (snapshot.data is EmptyState) {
                  return const Text("Empty State");
                }
                return const Text("No state");
              },
            ),
          ],
        ),
      ),
    );
  }
}

class HoverActionListPage extends StatefulWidget {
  final List<HoverAction> hoverActions;

  const HoverActionListPage({Key? key, required this.hoverActions})
      : super(key: key);

  @override
  State<HoverActionListPage> createState() => _HoverActionListPageState();
}

class _HoverActionListPageState extends State<HoverActionListPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hover Action List'),
      ),
      body: ListView.builder(
        itemCount: widget.hoverActions.length,
        itemBuilder: (BuildContext context, int index) {
          final hoverAction = widget.hoverActions[index];
          return ListTile(
            title: Text(hoverAction.name ?? ''),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text('Network: ${hoverAction.networkName ?? ''}'),
                Text('Country: ${hoverAction.countryAlpha2 ?? ''}'),
                Text('Transaction Type: ${hoverAction.transactionType ?? ''}'),
                // Add more details as needed
              ],
            ),
            onTap: () {
              // Handle onTap if needed
            },
          );
        },
      ),
    );
  }
}
