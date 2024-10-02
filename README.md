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


## Hover USSD Plugin

The Hover USSD plugin provides a simple and easy-to-use interface for integrating Hover USSD services into your Flutter applications. It allows you to start USSD transactions, listen to transaction states, and retrieve available actions from the Hover API.

## Usage

1. **Installation**

   Add `hover_ussd` to your `pubspec.yaml` file:

   ```yaml
   dependencies:
     hover_ussd: ^latest_version
   ```

   Then, run:

   ```sh
   flutter pub get
   ```

2. **Initialization**

   Initialize HoverUssd with your Hover API key at [docs.usehover.com](https://docs.usehover.com/), branding, and logo information:

   ```dart
   final HoverUssd _hoverUssd = HoverUssd();
   await _hoverUssd.initialize(
     apiKey: "YOUR_API_KEY",
     branding: "Your App Name",
     logo: "ic_launcher",
     notificationLogo: "ic_launcher",
   );
   ```

3. **Start a Transaction**

   Start a USSD transaction with the provided parameters:

   ```dart
   await _hoverUssd.startTransaction(
     actionId: "ACTION_ID",
     extras: {},
     theme: "myhoverTheme", //located in android/app/main/values/styles.xml
     header: "Hover Ussd Example",
     showUserStepDescriptions: true,
   );
   ```

4. **Permissions Check**

   Check if the app has all the necessary permissions:

   ```dart
   bool hasPermissions = await _hoverUssd.hasAllPermissions();
   ```

5. **Overlay and Accessibility Check**

   Check if the app has overlay and accessibility permissions:

   ```dart
   bool isOverlayEnabled = await _hoverUssd.isOverlayEnabled();
   bool isAccessibilityEnabled = await _hoverUssd.isAccessibilityEnabled();
   ```

6. **Retrieve Actions**

   Retrieve a list of available actions:

   ```dart
   List<HoverAction> actions = await _hoverUssd.getAllActions();
   ```

7. **Refresh Actions**

   Refresh actions to get the latest updates:

   ```dart
   await _hoverUssd.refreshActions();
   ```

8. **Listen to Transaction States**

   Get a stream of transaction states:

   ```dart
   Stream<TransactionState> transactionStream = _hoverUssd.getUssdTransactionState();
   ```

9. **Listen to Download Action States**

   Get a stream of download action states:

   ```dart
   Stream<DonwloadActionState> downloadStream = _hoverUssd.getDownloadActionState();
   ```

## Customization


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

* ### Customize an hover session
```dart
   await _hoverUssd.startTransaction(
     actionId: "ACTION_ID",
     extras: {},
     theme: "myhoverTheme", // as in android/app/main/values/styles.xml
     header: "Hover Ussd Example"// the title of your hover session,
     showUserStepDescriptions: true,
   );
```

* ### Add a custom permissions activity

```dart

  final String activityName = "com.example.yourApp.custompermissionsActivity";
  _hoverUssd.setPermissionsActivity(activityName: activityName);

   
```

### Example

```dart
class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late final HoverUssd _hoverUssd = HoverUssd(); 
  // Create an instance of HoverUssd you can pass it to a provider,
  // or use get_it to make it available to the entire app

  late StreamSubscription _transactionListening;
  late StreamSubscription _actionDownloadListening;
  bool _hasPermissions = false;
  bool _isOverlayEnabled = false;
  bool _isAccessibilityEnabled = false;

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

    //initialize hover after the downlad listener is set  
    
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


```


## Important
 * **This is a unofficial plugin**
## Credit
* Thanks to the authors of useHover android sdk, this work based of it  
      
## Maintainers
- [lucdotdev](mailto:lucdotdev@gmail.com)