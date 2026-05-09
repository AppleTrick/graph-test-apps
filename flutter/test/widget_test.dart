import 'package:flutter_test/flutter_test.dart';
import 'package:graphpilot_flutter_sample/main.dart';

void main() {
  testWidgets('renders GraphPilot sample home', (tester) async {
    await tester.pumpWidget(const GraphPilotFlutterApp());

    expect(find.text('GraphPilot Flutter Sample'), findsOneWidget);
    expect(find.text('벨소리 선택'), findsOneWidget);
    expect(find.text('상품목록'), findsWidgets);
    expect(find.text('웹뷰'), findsWidgets);
    expect(find.text('설정'), findsWidgets);
  });
}
