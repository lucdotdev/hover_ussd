import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hover_ussd/hover_ussd.dart';

void main() {
  const MethodChannel channel = MethodChannel('hover_ussd');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}
