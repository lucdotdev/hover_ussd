import 'package:flutter/material.dart';

import 'package:hover_ussd/hover_ussd.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HoverUssd.initialize(
      branding: 'Hover Ussd Example', logo: "mipmap/ic_launcher");
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final HoverUssd _hoverUssd = HoverUssd();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Hover Ussd Example'),
        ),
        body: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            TextButton(
              onPressed: () {
                _hoverUssd.sendUssd(
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
                  return Text(
                      "Ussd Succeded : \n" + snapshot.data!.toMap().toString());
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
      ),
    );
  }
}
