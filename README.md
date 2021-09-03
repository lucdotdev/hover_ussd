# hover_ussd

![build](https://github.com/lucdotdev/hover_ussd/workflows/build/badge.svg)
[![Pub](https://img.shields.io/pub/v/hover_ussd)](https://pub.dartlang.org/packages/hover_ussd)
[![Star on GitHub](https://img.shields.io/github/stars/lucdotdev/hover_ussd)](https://github.com/lucdotdev/hover_ussd)
[![Flutter Website](https://img.shields.io/badge/flutter-website-deepskyblue.svg)](https://flutter.dev/docs/development/data-and-backend/state-mgmt/options#bloc--rx)
[![License: MIT](https://img.shields.io/badge/license-MIT-purple.svg)](https://opensource.org/licenses/MIT)

<img src= "https://raw.githubusercontent.com/lucdotdev/hover_ussd/nullsafetty/doc/hover.png" width="350 px"/>
© image by Francis Mwakitumbula


A flutter plugin to make payments by usehover.com ussd gateway using Android Intent and receiving the transaction information back in response. 
**android only**

## Getting Started

* Adding The hover api key refert to documentation at [docs.usehover.com](https://docs.usehover.com/)

```xml
 <meta-data
        android:name="com.hover.ApiKey"  
        android:value="<YOUR_API_TOKEN>"/>
```
## Usage
* Example
```dart 
import 'package:flutter/material.dart';

import 'package:hover_ussd/hover_ussd.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HoverUssd.initialize();
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final HoverUssd _hoverUssd = HoverUssd();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Row(
            children: [
              FlatButton(
                onPressed: () {
                  _hoverUssd.sendUssd(
                      actionId: "c6e45e62", extras: {"price": "4000"});
                },
                child: Text("Start Trasaction"),
              ),
              StreamBuilder(
                stream: _hoverUssd.getUssdTransactionState,
                builder: (BuildContext context, AsyncSnapshot snapshot) {
                  if (snapshot.data == TransactionState.succesfull) {
                    return Text("succesfull");
                  } else if (snapshot.data ==
                      TransactionState.actionDowaloadFailed) {
                    return Text("action download failed");
                  } else if (snapshot.data == TransactionState.failed) {
                    return Text("failed");
                  }
                  return Text("no transaction");
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

```
## Features
  - [x] start a transaction
  - [x] listen for result  
  - [x] customization
  - [ ] translation
  
## Important
 

 * **Production ready**
 * **This is a unofficial plugin**
      
## Maintainers
- [lucdotdev](mailto:lucdotdev@gmail.com)