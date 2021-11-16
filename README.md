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


```

## Customization

* ### To use your logo on the processing screen 
```dart
 HoverUssd.initialize(
      branding: 'Hover Ussd Example', logo: "mipmap/ic_launcher");
```


* ### To use your own theme style

#### add your style to android/app/main/values/styles.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Theme applied to the Android Window while the process is starting -->
    <style name="LaunchTheme" parent="@android:style/Theme.Black.NoTitleBar">
        <!-- Show a splash screen on the activity. Automatically removed when
             Flutter draws its first frame -->
        <item name="android:windowBackground">@drawable/launch_background</item>
    </style>
    <!-- Theme applied to the Android Window as soon as the process has started.
         This theme determines the color of the Android Window while your
         Flutter UI initializes, as well as behind your Flutter UI while its
         running.
         
         This Theme is only used starting with V2 of Flutter's Android embedding. -->
    <style name="NormalTheme" parent="@android:style/Theme.Black.NoTitleBar">
        <item name="android:windowBackground">@android:color/white</item>
    </style>
	<style name="myHoverTheme" parent="Theme.AppCompat.Light.NoActionBar" >
		<item name="android:windowActionBar">true</item>
		<item name="android:windowContentTransitions">true</item>
		<item name="colorPrimary">@color/colorPrimary</item>
		<item name="colorPrimaryDark">@color/colorPrimaryDark</item>
		<item name="colorAccent">@color/colorAccent</item>
		<item name="hover_primaryColor">@color/colorPrimaryDark</item>
		<item name="hover_textColor">@color/colorPureWhite</item>
		<item name="hover_textColorTitle">@color/colorHoverWhite</item>
		<item name="hover_pinEntryColor">@color/colorPureWhite</item>

		<item name="hover_pinTheme">@style/PinButtonTheme</item>
		<item name="hover_posTheme">@style/PosButtonTheme</item>
	</style>

	<style name="PinButtonTheme" parent="Widget.AppCompat.Button.Borderless">
		<item name="android:textColor">@color/colorPureWhite</item>
		<item name="android:textSize">24sp</item>
		<item name="android:background">@color/colorPrimaryDark</item>
		<item name="android:typeface">normal</item>
		<item name="android:textAllCaps">true</item>
	</style>

	<style name="PosButtonTheme" parent="Widget.AppCompat.Button.Colored">
		<item name="android:textColor">@color/colorPureWhite</item>
		<item name="background">@color/colorPrimary</item>
		<item name="android:typeface">normal</item>
		<item name="android:textAllCaps">false</item>
	</style>

</resources>

```

#### add the theme name when calling **HoverUssd().startTransaction()**
```dart 
   _hoverUssd.startTransaction(
                    theme: "myHoverTheme",);
```
* ### Others customizations
```dart
startTransaction(
          {required String actionId,
          Map<String, String>? extras,
          String? theme,
          String? header,
          String? initialProcessingMessage,
          int? finalMsgDisplayTime,
          bool? showUserStepDescriptions})
```

## Important
 * **This is a unofficial plugin**
## Credit
* Thanks to the authors of useHover android sdk, this work based of it  
      
## Maintainers
- [lucdotdev](mailto:lucdotdev@gmail.com)