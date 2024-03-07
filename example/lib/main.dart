import 'dart:async';
import 'package:flutter/material.dart';
import 'package:hover_ussd/hover_ussd.dart';
import 'package:hover_ussd/models/transaction_state.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hover USSD Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyAppHomePage(),
    );
  }
}

class MyAppHomePage extends StatefulWidget {
  @override
  _MyAppHomePageState createState() => _MyAppHomePageState();
}

class _MyAppHomePageState extends State<MyAppHomePage> {
  final HoverUssd _hoverUssd = HoverUssd();
  late StreamSubscription _transactionListening;

  Future<void> initHover(BuildContext context) async {
    List<HoverAction>? actions = await _hoverUssd.initialize(
        branding: 'Hover Ussd Example', logo: "mipmap/ic_launcher");

    if (actions == null) {
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Action download failed")));
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Action download successfully")));
    }
  }

  Future<void> getAllActions() async {
    List<HoverAction>? action = await _hoverUssd.getActions();

    if (action == null) {
      print("Action download failed");
    } else {
      print(action);
    }
  }

  @override
  void initState() {
    initHover(context);
    getAllActions();

    _transactionListening =
        _hoverUssd.getUssdTransactionState().listen((event) {
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(event.toMap().toString())));
    });
    super.initState();
  }

  @override
  void dispose() {
    _transactionListening.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hover Ussd Example'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              TextButton(
                onPressed: () {
                  _hoverUssd.startTransaction(
                      actionId: "93c9da25",
                      extras: {},
                      theme: "myHoverTheme",
                      header: "Hover Ussd Example",
                      showUserStepDescriptions: true);
                },
                child: const Text("Start Transaction"),
              ),
              StreamBuilder<TransactionState>(
                stream: _hoverUssd.getUssdTransactionState(),
                builder: (BuildContext context, snapshot) {
                  if (snapshot.data is SmsParsed) {
                    return Text(
                        "Sms parsed : \n${(snapshot.data as SmsParsed).toMap()}");
                  }

                  if (snapshot.data is UssdSucceeded) {
                    return Text("Ussd Succeded : \n${(snapshot.data as UssdSucceeded).toMap()}");
                  }
                  if (snapshot.data is UssdLoading) {
                    return const Text("loading...");
                  }
                  if (snapshot.data is UssdFailed) {
                    return Text("Ussd Failed : \n${(snapshot.data as UssdFailed).toMap()}");
                  }
                  if (snapshot.data is EmptyState) {
                    return const Text("Empty State");
                  }

                  return const Text("No state");
                },
              ),
            ],
          ),
        ],
      ),
    );
  }
}
