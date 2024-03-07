import 'package:flutter_test/flutter_test.dart';
import 'package:hover_ussd/hover_ussd.dart';
import 'package:hover_ussd/hover_ussd_platform_interface.dart';
import 'package:hover_ussd/hover_ussd_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHoverUssdPlatform
    with MockPlatformInterfaceMixin
    implements HoverUssdPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HoverUssdPlatform initialPlatform = HoverUssdPlatform.instance;

  test('$MethodChannelHoverUssd is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHoverUssd>());
  });

  test('getPlatformVersion', () async {
    HoverUssd hoverUssdPlugin = HoverUssd();
    MockHoverUssdPlatform fakePlatform = MockHoverUssdPlatform();
    HoverUssdPlatform.instance = fakePlatform;

    expect(await hoverUssdPlugin.getPlatformVersion(), '42');
  });
}
