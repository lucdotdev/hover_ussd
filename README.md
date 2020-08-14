# hover_ussd

![build](https://github.com/lucdotdev/hover_ussd/workflows/build/badge.svg)
[![Pub](https://img.shields.io/pub/v/hover_ussd)](https://pub.dartlang.org/packages/hover_ussd)
[![Star on GitHub](https://img.shields.io/github/stars/lucdotdev/hover_ussd)](https://github.com/lucdotdev/hover_ussd)
[![style: effective dart](https://img.shields.io/badge/style-effective_dart-40c4ff.svg)](https://github.com/tenhobi/effective_dart)
[![Flutter Website](https://img.shields.io/badge/flutter-website-deepskyblue.svg)](https://flutter.dev/docs/development/data-and-backend/state-mgmt/options#bloc--rx)
[![License: MIT](https://img.shields.io/badge/license-MIT-purple.svg)](https://opensource.org/licenses/MIT)

<img src="docs/hover.png" alt="drawing" width="350 px"/>

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
* Initialize the plugin in main method
```dart
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HoverUssd().initialize();
  runApp(MyApp());
}
```
* Start a transaction
```dart 
import 'package:hover_ussd/hover_ussd.dart';
...
final HoverUssd _hoverUssd = HoverUssd();

///Begin transaction
void send(){
   ///First param @String [action_id]
   ///Second param @ Map [step_variables]
  _hoverUssd.sendUssd("c6e45e62", {"price": "4000"});
}

///Listen for transaction status
 _hoverUssd.onTransactiontateChanged.listen((event) {
        // Do something with new state
        if (event == TransactionState.succesfull) {
          print("succesfull");
        } else if (event == TransactionState.waiting) {
          print("pending");
        } else if (event == TransactionState.failed) {
          print('failed');
        }
  });
///You can listen with StreamBuilder to update ui
 StreamBuilder(
     stream: _hoverUssd.onTransactiontateChanged,
        builder: (BuildContext context, AsyncSnapshot snapshot) {
                  if (snapshot.data == TransactionState.succesfull) {
                    return Text("succesfull");
                  } else if (snapshot.data == TransactionState.waiting) {
                    return Text("pending");
                  } else if (snapshot.data == TransactionState.failed) {
                    return Text("failed");
                  }
          return Text("no transaction");
   },
);

```
## Features
  - [x] start a transaction
  - [x] listen for result  
  - [ ] customization
  - [ ] translation
  
## Important
 
 * **support only basic feature**
 * **always in developpement**
 * **this isn't a officialy plugin**
      
## Maintainers
- [Lucdotdev](https://twitter.com/lucdotdev)
 
