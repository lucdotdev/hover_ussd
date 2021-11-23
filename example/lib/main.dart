import 'dart:async';

import 'package:flutter/material.dart';

import 'package:hover_ussd/hover_ussd.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HoverUssd.initialize(
      branding: 'Hover Ussd Example', logo: "mipmap/ic_launcher");
  runApp(App());
}

class App extends StatelessWidget {
  const App({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: MyApp(),
    );
  }
}

class MyApp extends StatefulWidget {
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final HoverUssd _hoverUssd = HoverUssd();

  late final StreamSubscription transactionListening;
  @override
  void initState() {
    transactionListening = _hoverUssd.getUssdTransactionState!.listen((event) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text(event.toMap().toString())));
    });
    super.initState();
  }

  @override
  void dispose() {
    transactionListening.cancel();
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
                      actionId: "c6e45e62",
                      extras: {"price": "4000"},
                      theme: "myHoverTheme",
                      header: "Hover Ussd Example",
                      showUserStepDescriptions: true);
                },
                child: Text("Start Transaction"),
              ),
              StreamBuilder<TransactionState>(
                stream: _hoverUssd.getUssdTransactionState,
                builder: (BuildContext context, snapshot) {
                  if (snapshot.data is SmsParsed) {
                    return Text(
                        "Sms parsed : \n" + snapshot.data!.toMap().toString());
                  }

                  if (snapshot.data is UssdSucceded) {
                    return Text("Ussd Succeded : \n" +
                        snapshot.data!.toMap().toString());
                  }
                  if (snapshot.data is UssdLoading) {
                    return Text("loading...");
                  }
                  if (snapshot.data is UssdFailed) {
                    return Text(
                        "Ussd Failed : \n" + snapshot.data!.toMap().toString());
                  }
                  if (snapshot.data is EmptyState) {
                    return Text("Empty State");
                  }

                  return Text("No state");
                },
              ),
            ],
          ),
        ],
      ),
    );
  }
}
