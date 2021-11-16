# hover_ussd

![build](https://github.com/lucdotdev/hover_ussd/workflows/build/badge.svg)
[![Pub](https://img.shields.io/pub/v/hover_ussd)](https://pub.dartlang.org/packages/hover_ussd)
[![Star on GitHub](https://img.shields.io/github/stars/lucdotdev/hover_ussd)](https://github.com/lucdotdev/hover_ussd)
[![Flutter Website](https://img.shields.io/badge/flutter-website-deepskyblue.svg)](https://flutter.dev/docs/development/data-and-backend/state-mgmt/options#bloc--rx)
[![License: MIT](https://img.shields.io/badge/license-MIT-purple.svg)](https://opensource.org/licenses/MIT)

<img src= "https://raw.githubusercontent.com/lucdotdev/hover_ussd/nullsafetty/doc/hover.png" width="350 px"/>
© image by Francis Mwakitumbula


A flutter plugin implemanting usehover.com ussd gateway sdk using Android Intent and receiving the transaction information back in response. 
**android only**

## Getting Started

* Adding The hover api key refert to documentation at [docs.usehover.com](https://docs.usehover.com/)

```xml
 <meta-data
        android:name="com.hover.ApiKey"  
        android:value="<YOUR_API_TOKEN>"/>
```
## Basic Usage

```dart 
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  HoverUssd.initialize(branding: 'Hover Ussd Example');
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
        body: Center(
          child: Row(
            children: [
              FlatButton(
                onPressed: () {
                  _hoverUssd.sendUssd(
                      actionId: "c6e45e62",
                      extras: {"price": "4000"},
                      theme: "myHoverTheme",
                      header: "Hover Ussd Example",
                      showUserStepDescriptions: true);
                },
                child: Text("Start Trasaction"),
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

                  return Text("");
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

## Customization
To use your logo on the processing screen 
```dart
 HoverUssd.initialize(
      branding: 'Hover Ussd Example', logo: "mipmap/ic_launcher");
```
## Important
 * **This is a unofficial plugin**
## Credit
* Thanks to the authors of useHover android sdk, this work based of it  
      
## Maintainers
- [lucdotdev](mailto:lucdotdev@gmail.com)