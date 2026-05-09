import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

void main() {
  runApp(const GraphPilotFlutterApp());
}

class GraphPilotFlutterApp extends StatelessWidget {
  const GraphPilotFlutterApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'GraphPilot Flutter Sample',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF2563EB)),
        useMaterial3: true,
      ),
      home: const MainShell(),
    );
  }
}

class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _tabIndex = 0;

  static const _titles = ['홈', '상품목록', '웹뷰', '설정'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_titles[_tabIndex]), centerTitle: false),
      body: IndexedStack(
        index: _tabIndex,
        children: const [
          HomeTab(),
          ProductListTab(),
          WebViewTab(),
          SettingsTab(),
        ],
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _tabIndex,
        onDestinationSelected: (index) => setState(() => _tabIndex = index),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: '홈',
          ),
          NavigationDestination(
            icon: Icon(Icons.list_alt_outlined),
            selectedIcon: Icon(Icons.list_alt),
            label: '상품목록',
          ),
          NavigationDestination(
            icon: Icon(Icons.public_outlined),
            selectedIcon: Icon(Icons.public),
            label: '웹뷰',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings),
            label: '설정',
          ),
        ],
      ),
    );
  }
}

class HomeTab extends StatefulWidget {
  const HomeTab({super.key});

  @override
  State<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab> {
  String _ringtone = '기본 벨소리';
  String _overlayStatus = '대기 중';

  final List<String> _ringtones = const ['기본 벨소리', '맑은 종소리', '디지털 알림음', '무음'];

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text(
          'GraphPilot Flutter Sample',
          style: Theme.of(context).textTheme.headlineSmall,
        ),
        const SizedBox(height: 8),
        const Text('단순 선택 리스트와 상태 변화를 확인하는 홈 화면입니다.'),
        const SizedBox(height: 20),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('벨소리 선택', style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 8),
                RadioGroup<String>(
                  groupValue: _ringtone,
                  onChanged: (value) {
                    if (value != null) {
                      setState(() => _ringtone = value);
                    }
                  },
                  child: Column(
                    children: [
                      for (final ringtone in _ringtones)
                        Semantics(
                          label: '벨소리 항목 $ringtone',
                          button: true,
                          child: RadioListTile<String>(
                            title: Text(ringtone),
                            value: ringtone,
                          ),
                        ),
                    ],
                  ),
                ),
                const Divider(),
                Text('현재 선택: $_ringtone'),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        _buildOverlayCard(context),
      ],
    );
  }

  Widget _buildOverlayCard(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Surface Overlay 테스트',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            Text('Overlay 상태: $_overlayStatus'),
            const SizedBox(height: 12),
            FilledButton(
              onPressed: () => _showNativeDialog(context),
              child: const Text('Modal Dialog 열기'),
            ),
            const SizedBox(height: 8),
            FilledButton(
              onPressed: () => _showNativeBottomSheet(context),
              child: const Text('Modal BottomSheet 열기'),
            ),
            const SizedBox(height: 8),
            PopupMenuButton<String>(
              tooltip: 'Popup Menu 열기',
              onSelected: (value) {
                setState(() => _overlayStatus = 'Popup 선택 $value');
              },
              itemBuilder: (context) => const [
                PopupMenuItem(value: '정렬: 최신순', child: Text('정렬: 최신순')),
                PopupMenuItem(value: '정렬: 가격순', child: Text('정렬: 가격순')),
                PopupMenuItem(value: '보기: 카드형', child: Text('보기: 카드형')),
              ],
              child: const ListTile(
                contentPadding: EdgeInsets.zero,
                title: Text('Popup Menu 열기'),
                trailing: Icon(Icons.arrow_drop_down),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showNativeDialog(BuildContext context) {
    setState(() => _overlayStatus = 'Modal Dialog 열림');
    showDialog<void>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Modal Dialog'),
          content: const Text('중앙에 뜨는 확인/취소형 modal surface 입니다.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
                setState(() => _overlayStatus = 'Dialog 취소 선택');
              },
              child: const Text('취소'),
            ),
            FilledButton(
              onPressed: () {
                Navigator.of(context).pop();
                setState(() => _overlayStatus = 'Dialog 확인 선택');
              },
              child: const Text('확인'),
            ),
          ],
        );
      },
    );
  }

  void _showNativeBottomSheet(BuildContext context) {
    setState(() => _overlayStatus = 'Modal BottomSheet 열림');
    showModalBottomSheet<void>(
      context: context,
      builder: (context) {
        var sheetStep = '요약 단계';
        return StatefulBuilder(
          builder: (context, setSheetState) {
            return Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Modal BottomSheet',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  Text('현재 단계: $sheetStep'),
                  const SizedBox(height: 12),
                  FilledButton(
                    onPressed: () {
                      setSheetState(() => sheetStep = '상세 옵션 선택됨');
                      setState(() => _overlayStatus = 'BottomSheet 내부 상태 변경');
                    },
                    child: const Text('시트 내부 상태 변경'),
                  ),
                  const SizedBox(height: 8),
                  FilledButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                      setState(() => _overlayStatus = 'BottomSheet 적용 완료');
                    },
                    child: const Text('적용'),
                  ),
                  const SizedBox(height: 12),
                ],
              ),
            );
          },
        );
      },
    );
  }
}

class Product {
  const Product({
    required this.name,
    required this.category,
    required this.price,
    required this.badge,
    required this.description,
  });

  final String name;
  final String category;
  final String price;
  final String badge;
  final String description;
}

const _products = [
  Product(
    name: '스마트 체중계',
    category: '전자제품',
    price: '39,900원',
    badge: '인기상품',
    description: '앱 연동과 가족 프로필을 지원하는 테스트 상품입니다.',
  ),
  Product(
    name: '무선 키보드',
    category: '전자제품',
    price: '64,000원',
    badge: '추천',
    description: '리스트 아이템 클릭과 상세 페이지 이동을 확인합니다.',
  ),
  Product(
    name: '주방 수납함',
    category: '생활용품',
    price: '12,500원',
    badge: '할인',
    description: '일반 생활용품 카테고리 필터 검증용 항목입니다.',
  ),
  Product(
    name: '욕실 정리 선반',
    category: '생활용품',
    price: '24,800원',
    badge: '인기상품',
    description: '동일 리스트 안에서 다른 카테고리를 구분합니다.',
  ),
  Product(
    name: 'USB-C 충전기',
    category: '전자제품',
    price: '18,900원',
    badge: '신상품',
    description: '작은 액세서리 상품 상세 화면 테스트용입니다.',
  ),
];

class ProductListTab extends StatefulWidget {
  const ProductListTab({super.key});

  @override
  State<ProductListTab> createState() => _ProductListTabState();
}

class _ProductListTabState extends State<ProductListTab> {
  String _filter = '전체';
  final List<String> _filters = const ['전체', '전자제품', '생활용품', '인기상품'];

  List<Product> get _visibleProducts {
    if (_filter == '전체') {
      return _products;
    }
    if (_filter == '인기상품') {
      return _products.where((product) => product.badge == '인기상품').toList();
    }
    return _products.where((product) => product.category == _filter).toList();
  }

  @override
  Widget build(BuildContext context) {
    final products = _visibleProducts;

    return Column(
      children: [
        SizedBox(
          height: 64,
          child: ListView.separated(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            scrollDirection: Axis.horizontal,
            itemBuilder: (context, index) {
              final filter = _filters[index];
              return Semantics(
                label: '필터 $filter',
                button: true,
                selected: _filter == filter,
                child: ChoiceChip(
                  label: Text(filter),
                  selected: _filter == filter,
                  onSelected: (_) => setState(() => _filter = filter),
                ),
              );
            },
            separatorBuilder: (context, index) => const SizedBox(width: 8),
            itemCount: _filters.length,
          ),
        ),
        Expanded(
          child: ListView.separated(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
            itemCount: products.length,
            separatorBuilder: (context, index) => const SizedBox(height: 10),
            itemBuilder: (context, index) {
              final product = products[index];
              return Semantics(
                label: '상품 항목 ${product.name}',
                button: true,
                child: Card(
                  child: ListTile(
                    leading: CircleAvatar(child: Text('${index + 1}')),
                    title: Text(product.name),
                    subtitle: Text('${product.category} · ${product.badge}'),
                    trailing: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text(
                          product.price,
                          style: const TextStyle(fontWeight: FontWeight.w700),
                        ),
                        const Icon(Icons.chevron_right),
                      ],
                    ),
                    onTap: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (_) => ProductDetailPage(product: product),
                        ),
                      );
                    },
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}

class ProductDetailPage extends StatelessWidget {
  const ProductDetailPage({super.key, required this.product});

  final Product product;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(product.name)),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Text(product.name, style: Theme.of(context).textTheme.headlineSmall),
          const SizedBox(height: 12),
          Text(product.price, style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            children: [
              Chip(label: Text(product.category)),
              Chip(label: Text(product.badge)),
            ],
          ),
          const SizedBox(height: 24),
          Text(product.description),
          const SizedBox(height: 24),
          FilledButton.icon(
            onPressed: () {},
            icon: const Icon(Icons.shopping_cart_outlined),
            label: const Text('장바구니 담기'),
          ),
        ],
      ),
    );
  }
}

class WebViewTab extends StatefulWidget {
  const WebViewTab({super.key});

  @override
  State<WebViewTab> createState() => _WebViewTabState();
}

class _WebViewTabState extends State<WebViewTab> {
  late final WebViewController _controller;

  @override
  void initState() {
    super.initState();
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted);
    _loadMapHtml();
  }

  Future<void> _loadMapHtml() async {
    final imageBytes = await rootBundle.load('assets/webview-map.png');
    final imageSource =
        'data:image/png;base64,${base64Encode(imageBytes.buffer.asUint8List())}';
    await _controller.loadHtmlString(_sampleHtml(imageSource));
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(16),
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: const Text('앱 내부 WebView 샘플'),
        ),
        Expanded(
          child: Semantics(
            label: '웹뷰 콘텐츠 영역',
            child: WebViewWidget(controller: _controller),
          ),
        ),
      ],
    );
  }
}

String _sampleHtml(String mapImageSource) =>
    '''
<!doctype html>
<html lang="ko">
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
      * { box-sizing: border-box; }
      body { margin: 0; padding: 18px; background: #f8fafc; color: #111827; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
      h1 { margin: 0 0 8px; font-size: 24px; }
      p { line-height: 1.45; }
      button { width: 100%; min-height: 46px; border: 0; border-radius: 8px; margin-top: 10px; padding: 12px; background: #2563eb; color: white; font-size: 16px; font-weight: 700; }
      button.secondary { background: white; color: #111827; border: 1px solid #cbd5e1; }
      .card { background: white; border: 1px solid #e5e7eb; border-radius: 10px; padding: 14px; margin-top: 12px; }
      .map { display: block; width: 100%; max-height: 300px; object-fit: cover; border-radius: 10px; border: 1px solid #d1d5db; margin-top: 12px; }
      .status { background: #ecfeff; border: 1px solid #67e8f9; border-radius: 8px; color: #155e75; margin-top: 12px; padding: 10px; font-weight: 700; }
    </style>
  </head>
  <body>
    <main id="app"></main>
    <script>
      var page = 'home';
      var status = '대기 중';

      function renderHome() {
        return "<h1>지도 웹뷰 홈</h1><p>버튼 클릭 후 큰 지도 이미지 화면으로 전환되는지만 확인합니다.</p>" +
          "<section class='card'><button data-action='detail'>지도 보기</button>" +
          "<button class='secondary' data-action='status'>상태 텍스트 변경</button>" +
          "<div class='status'>상태: " + status + "</div></section>";
      }

      function renderDetail() {
        return "<h1>지도 보기</h1><p>WebView 내부 화면이 지도 이미지 중심 화면으로 전환되었습니다.</p>" +
          "<section class='card'><img class='map' src='$mapImageSource' alt='샘플 지도 이미지' />" +
          "<button data-action='status'>지도 상태 변경</button>" +
          "<button class='secondary' data-action='home'>웹 홈으로 돌아가기</button>" +
          "<div class='status'>상태: " + status + "</div></section>";
      }

      function render() {
        document.getElementById('app').innerHTML = page === 'detail' ? renderDetail() : renderHome();
        Array.prototype.forEach.call(document.querySelectorAll('[data-action]'), function(node) {
          node.addEventListener('click', function() {
            var action = node.getAttribute('data-action');
            if (action === 'detail') { page = 'detail'; status = '지도 화면 진입'; }
            if (action === 'home') { page = 'home'; status = '홈 화면 복귀'; }
            if (action === 'status') { status = page === 'detail' ? '지도 상태 변경됨' : '홈 상태 변경됨'; }
            render();
          });
        });
      }

      render();
    </script>
  </body>
</html>
''';

class SettingItem {
  const SettingItem({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.type,
  });

  final String title;
  final String subtitle;
  final IconData icon;
  final SettingType type;
}

enum SettingType { notification, display, privacy, storage }

const _settings = [
  SettingItem(
    title: '알림 설정',
    subtitle: '스위치와 라디오 버튼',
    icon: Icons.notifications_outlined,
    type: SettingType.notification,
  ),
  SettingItem(
    title: '화면 설정',
    subtitle: '밝기 슬라이더와 다크 모드',
    icon: Icons.tune_outlined,
    type: SettingType.display,
  ),
  SettingItem(
    title: '개인정보 설정',
    subtitle: '체크박스 권한 목록',
    icon: Icons.privacy_tip_outlined,
    type: SettingType.privacy,
  ),
  SettingItem(
    title: '저장공간 설정',
    subtitle: '용량 행과 정리 버튼',
    icon: Icons.storage_outlined,
    type: SettingType.storage,
  ),
];

class SettingsTab extends StatelessWidget {
  const SettingsTab({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.all(16),
      itemCount: _settings.length,
      separatorBuilder: (context, index) => const SizedBox(height: 8),
      itemBuilder: (context, index) {
        final item = _settings[index];
        return Semantics(
          label: '설정 항목 ${item.title}',
          button: true,
          child: Card(
            child: ListTile(
              leading: Icon(item.icon),
              title: Text(item.title),
              subtitle: Text(item.subtitle),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => SettingDetailPage(item: item),
                  ),
                );
              },
            ),
          ),
        );
      },
    );
  }
}

class SettingDetailPage extends StatefulWidget {
  const SettingDetailPage({super.key, required this.item});

  final SettingItem item;

  @override
  State<SettingDetailPage> createState() => _SettingDetailPageState();
}

class _SettingDetailPageState extends State<SettingDetailPage> {
  bool _notificationEnabled = true;
  bool _darkMode = false;
  double _brightness = 70;
  String _sound = '기본 알림음';
  final Set<String> _privacyOptions = {'위치 정보', '사용 기록'};

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('${widget.item.title} 상세')),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Text(
            widget.item.title,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 8),
          Text(widget.item.subtitle),
          const SizedBox(height: 24),
          _buildDetailBody(context),
        ],
      ),
    );
  }

  Widget _buildDetailBody(BuildContext context) {
    switch (widget.item.type) {
      case SettingType.notification:
        return Column(
          children: [
            SwitchListTile(
              title: const Text('알림 받기'),
              subtitle: const Text('전체 푸시 알림을 켜거나 끕니다.'),
              value: _notificationEnabled,
              onChanged: (value) =>
                  setState(() => _notificationEnabled = value),
            ),
            const Divider(),
            RadioGroup<String>(
              groupValue: _sound,
              onChanged: (value) {
                if (value != null) {
                  setState(() => _sound = value);
                }
              },
              child: Column(
                children: [
                  for (final sound in const ['기본 알림음', '짧은 알림음', '진동만'])
                    RadioListTile<String>(title: Text(sound), value: sound),
                ],
              ),
            ),
          ],
        );
      case SettingType.display:
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('화면 밝기 ${_brightness.round()}%'),
            Slider(
              label: '${_brightness.round()}%',
              min: 0,
              max: 100,
              divisions: 10,
              value: _brightness,
              onChanged: (value) => setState(() => _brightness = value),
            ),
            SwitchListTile(
              title: const Text('다크 모드'),
              value: _darkMode,
              onChanged: (value) => setState(() => _darkMode = value),
            ),
          ],
        );
      case SettingType.privacy:
        return Column(
          children: [
            for (final option in const ['위치 정보', '사용 기록', '광고 맞춤 설정', '진단 데이터'])
              CheckboxListTile(
                title: Text(option),
                value: _privacyOptions.contains(option),
                onChanged: (checked) {
                  setState(() {
                    if (checked == true) {
                      _privacyOptions.add(option);
                    } else {
                      _privacyOptions.remove(option);
                    }
                  });
                },
              ),
          ],
        );
      case SettingType.storage:
        return Column(
          children: [
            _storageRow('앱 데이터', '124 MB'),
            _storageRow('캐시', '36 MB'),
            _storageRow('다운로드', '8 MB'),
            const SizedBox(height: 16),
            FilledButton.icon(
              onPressed: () {},
              icon: const Icon(Icons.cleaning_services_outlined),
              label: const Text('캐시 정리'),
            ),
          ],
        );
    }
  }

  Widget _storageRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w700)),
        ],
      ),
    );
  }
}
