import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  BackHandler,
  FlatList,
  Modal,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Switch,
  Text,
  View,
} from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { WebView } from 'react-native-webview';

type TabKey = 'home' | 'products' | 'webview' | 'settings';
type SettingType = 'notification' | 'display' | 'privacy' | 'storage';

type Product = {
  name: string;
  category: string;
  price: string;
  badge: string;
  description: string;
};

type SettingItem = {
  title: string;
  subtitle: string;
  type: SettingType;
};

const tabs: Array<{ key: TabKey; label: string }> = [
  { key: 'home', label: '홈' },
  { key: 'products', label: '상품목록' },
  { key: 'webview', label: '웹뷰' },
  { key: 'settings', label: '설정' },
];

const products: Product[] = [
  {
    name: '스마트 체중계',
    category: '전자제품',
    price: '39,900원',
    badge: '인기상품',
    description: '앱 연동과 가족 프로필을 지원하는 테스트 상품입니다.',
  },
  {
    name: '무선 키보드',
    category: '전자제품',
    price: '64,000원',
    badge: '추천',
    description: '리스트 아이템 클릭과 상세 페이지 이동을 확인합니다.',
  },
  {
    name: '주방 수납함',
    category: '생활용품',
    price: '12,500원',
    badge: '할인',
    description: '일반 생활용품 카테고리 필터 검증용 항목입니다.',
  },
  {
    name: '욕실 정리 선반',
    category: '생활용품',
    price: '24,800원',
    badge: '인기상품',
    description: '동일 리스트 안에서 다른 카테고리를 구분합니다.',
  },
  {
    name: 'USB-C 충전기',
    category: '전자제품',
    price: '18,900원',
    badge: '신상품',
    description: '작은 액세서리 상품 상세 화면 테스트용입니다.',
  },
];

const settings: SettingItem[] = [
  {
    title: '알림 설정',
    subtitle: '스위치와 라디오 버튼',
    type: 'notification',
  },
  {
    title: '화면 설정',
    subtitle: '밝기 단계 버튼과 다크 모드',
    type: 'display',
  },
  {
    title: '개인정보 설정',
    subtitle: '체크박스 권한 목록',
    type: 'privacy',
  },
  {
    title: '저장공간 설정',
    subtitle: '용량 행과 정리 버튼',
    type: 'storage',
  },
];

const html = `
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
          "<section class='card'><img class='map' src='webview-map.png' alt='샘플 지도 이미지' />" +
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
`;

function App(): React.JSX.Element {
  const [tab, setTab] = useState<TabKey>('home');
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [selectedSetting, setSelectedSetting] = useState<SettingItem | null>(
    null,
  );
  const isDetailScreen = selectedProduct !== null || selectedSetting !== null;

  let headerTitle = titleForTab(tab);
  if (selectedProduct) {
    headerTitle = selectedProduct.name;
  }
  if (selectedSetting) {
    headerTitle = `${selectedSetting.title} 상세`;
  }

  const selectTab = (nextTab: TabKey) => {
    setTab(nextTab);
    setSelectedProduct(null);
    setSelectedSetting(null);
  };

  const closeDetailScreen = useCallback(() => {
    setSelectedProduct(null);
    setSelectedSetting(null);
  }, []);

  useEffect(() => {
    const subscription = BackHandler.addEventListener(
      'hardwareBackPress',
      () => {
        if (!isDetailScreen) {
          return false;
        }
        closeDetailScreen();
        return true;
      },
    );

    return () => subscription.remove();
  }, [closeDetailScreen, isDetailScreen]);

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.app}>
        <StatusBar barStyle="dark-content" backgroundColor="#ffffff" />
        <View style={styles.header}>
          {isDetailScreen && (
            <Pressable
              accessibilityLabel="뒤로가기"
              accessibilityRole="button"
              onPress={closeDetailScreen}
              style={styles.headerBackButton}
            >
              <Text style={styles.headerBackText}>‹</Text>
            </Pressable>
          )}
          <Text numberOfLines={1} style={styles.headerTitle}>
            {headerTitle}
          </Text>
        </View>
        <View style={styles.content}>
          {renderTab(
            tab,
            selectedProduct,
            setSelectedProduct,
            selectedSetting,
            setSelectedSetting,
          )}
        </View>
        {!isDetailScreen && (
          <View style={styles.tabBar} accessibilityRole="tablist">
            {tabs.map(item => {
              const selected = item.key === tab;
              return (
                <Pressable
                  key={item.key}
                  accessibilityLabel={`하단 탭 ${item.label}`}
                  accessibilityRole="tab"
                  accessibilityState={{ selected }}
                  onPress={() => selectTab(item.key)}
                  style={[
                    styles.tabButton,
                    selected && styles.tabButtonSelected,
                  ]}
                >
                  <Text
                    style={[styles.tabText, selected && styles.tabTextSelected]}
                  >
                    {item.label}
                  </Text>
                </Pressable>
              );
            })}
          </View>
        )}
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

function titleForTab(tab: TabKey): string {
  switch (tab) {
    case 'products':
      return '상품목록';
    case 'webview':
      return '웹뷰';
    case 'settings':
      return '설정';
    case 'home':
    default:
      return '홈';
  }
}

function renderTab(
  tab: TabKey,
  selectedProduct: Product | null,
  onSelectProduct: (product: Product) => void,
  selectedSetting: SettingItem | null,
  onSelectSetting: (setting: SettingItem) => void,
): React.JSX.Element {
  switch (tab) {
    case 'products':
      return (
        <ProductListScreen
          selectedProduct={selectedProduct}
          onSelectProduct={onSelectProduct}
        />
      );
    case 'webview':
      return <WebViewScreen />;
    case 'settings':
      return (
        <SettingsScreen
          selectedSetting={selectedSetting}
          onSelectSetting={onSelectSetting}
        />
      );
    case 'home':
    default:
      return <HomeScreen />;
  }
}

function HomeScreen(): React.JSX.Element {
  const [ringtone, setRingtone] = useState('기본 벨소리');
  const [dialogVisible, setDialogVisible] = useState(false);
  const [sheetVisible, setSheetVisible] = useState(false);
  const [sheetStep, setSheetStep] = useState('요약 단계');
  const [popupOpen, setPopupOpen] = useState(false);
  const [overlayStatus, setOverlayStatus] = useState('대기 중');
  const ringtones = ['기본 벨소리', '맑은 종소리', '디지털 알림음', '무음'];

  return (
    <ScrollView contentContainerStyle={styles.scrollContent}>
      <Text style={styles.screenTitle}>GraphPilot React Native Sample</Text>
      <Text style={styles.description}>
        단순 선택 리스트와 상태 변화를 확인하는 홈 화면입니다.
      </Text>
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>벨소리 선택</Text>
        {ringtones.map(item => (
          <Pressable
            key={item}
            accessibilityLabel={`벨소리 항목 ${item}`}
            accessibilityRole="radio"
            accessibilityState={{ selected: ringtone === item }}
            onPress={() => setRingtone(item)}
            style={styles.radioRow}
          >
            <View
              style={[
                styles.radioCircle,
                ringtone === item && styles.radioCircleSelected,
              ]}
            />
            <Text style={styles.rowTitle}>{item}</Text>
          </Pressable>
        ))}
        <View style={styles.divider} />
        <Text style={styles.valueText}>현재 선택: {ringtone}</Text>
      </View>
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Surface Overlay 테스트</Text>
        <Text style={styles.valueText}>Overlay 상태: {overlayStatus}</Text>
        <Pressable
          accessibilityLabel="Modal Dialog 열기"
          accessibilityRole="button"
          onPress={() => {
            setOverlayStatus('Modal Dialog 열림');
            setDialogVisible(true);
          }}
          style={styles.primaryButton}
        >
          <Text style={styles.primaryButtonText}>Modal Dialog 열기</Text>
        </Pressable>
        <Pressable
          accessibilityLabel="Modal BottomSheet 열기"
          accessibilityRole="button"
          onPress={() => {
            setSheetStep('요약 단계');
            setOverlayStatus('Modal BottomSheet 열림');
            setSheetVisible(true);
          }}
          style={styles.primaryButton}
        >
          <Text style={styles.primaryButtonText}>Modal BottomSheet 열기</Text>
        </Pressable>
        <Pressable
          accessibilityLabel="Popup Menu 열기"
          accessibilityRole="button"
          onPress={() => setPopupOpen(!popupOpen)}
          style={styles.outlineButton}
        >
          <Text style={styles.outlineButtonText}>Popup Menu 열기</Text>
        </Pressable>
        {popupOpen && (
          <View style={styles.popupPanel}>
            {['정렬: 최신순', '정렬: 가격순', '보기: 카드형'].map(option => (
              <Pressable
                key={option}
                accessibilityLabel={option}
                accessibilityRole="menuitem"
                onPress={() => {
                  setOverlayStatus(`Popup 선택 ${option}`);
                  setPopupOpen(false);
                }}
                style={styles.popupOption}
              >
                <Text style={styles.rowTitle}>{option}</Text>
              </Pressable>
            ))}
          </View>
        )}
      </View>
      <Modal
        transparent
        visible={dialogVisible}
        animationType="fade"
        onRequestClose={() => setDialogVisible(false)}
      >
        <View style={styles.dialogBackdrop}>
          <View style={styles.dialogCard}>
            <Text style={styles.screenTitle}>Modal Dialog</Text>
            <Text style={styles.description}>
              중앙에 뜨는 확인/취소형 modal surface 입니다.
            </Text>
            <Pressable
              accessibilityLabel="Dialog 확인"
              accessibilityRole="button"
              onPress={() => {
                setOverlayStatus('Dialog 확인 선택');
                setDialogVisible(false);
              }}
              style={styles.primaryButton}
            >
              <Text style={styles.primaryButtonText}>확인</Text>
            </Pressable>
            <Pressable
              accessibilityLabel="Dialog 취소"
              accessibilityRole="button"
              onPress={() => {
                setOverlayStatus('Dialog 취소 선택');
                setDialogVisible(false);
              }}
              style={styles.outlineButton}
            >
              <Text style={styles.outlineButtonText}>취소</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
      <Modal
        transparent
        visible={sheetVisible}
        animationType="slide"
        onRequestClose={() => setSheetVisible(false)}
      >
        <View style={styles.bottomSheetBackdrop}>
          <View style={styles.bottomSheetCard}>
            <Text style={styles.screenTitle}>Modal BottomSheet</Text>
            <Text style={styles.valueText}>현재 단계: {sheetStep}</Text>
            <Pressable
              accessibilityLabel="BottomSheet 내부 상태 변경"
              accessibilityRole="button"
              onPress={() => {
                setSheetStep('상세 옵션 선택됨');
                setOverlayStatus('BottomSheet 내부 상태 변경');
              }}
              style={styles.primaryButton}
            >
              <Text style={styles.primaryButtonText}>시트 내부 상태 변경</Text>
            </Pressable>
            <Pressable
              accessibilityLabel="BottomSheet 적용"
              accessibilityRole="button"
              onPress={() => {
                setOverlayStatus('BottomSheet 적용 완료');
                setSheetVisible(false);
              }}
              style={styles.outlineButton}
            >
              <Text style={styles.outlineButtonText}>적용</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </ScrollView>
  );
}

function ProductListScreen({
  selectedProduct,
  onSelectProduct,
}: {
  selectedProduct: Product | null;
  onSelectProduct: (product: Product) => void;
}): React.JSX.Element {
  const [filter, setFilter] = useState('전체');
  const filters = ['전체', '전자제품', '생활용품', '인기상품'];

  const visibleProducts = useMemo(() => {
    if (filter === '전체') {
      return products;
    }
    if (filter === '인기상품') {
      return products.filter(product => product.badge === '인기상품');
    }
    return products.filter(product => product.category === filter);
  }, [filter]);

  if (selectedProduct) {
    return <ProductDetailScreen product={selectedProduct} />;
  }

  return (
    <View style={styles.full}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.filterRow}
      >
        {filters.map(item => (
          <Pressable
            key={item}
            accessibilityLabel={`필터 ${item}`}
            accessibilityRole="button"
            accessibilityState={{ selected: filter === item }}
            onPress={() => setFilter(item)}
            style={[styles.filterChip, filter === item && styles.filterActive]}
          >
            <Text
              style={[
                styles.filterText,
                filter === item && styles.filterTextActive,
              ]}
            >
              {item}
            </Text>
          </Pressable>
        ))}
      </ScrollView>
      <FlatList
        data={visibleProducts}
        keyExtractor={item => item.name}
        contentContainerStyle={styles.listContent}
        renderItem={({ item, index }) => (
          <Pressable
            accessibilityLabel={`상품 항목 ${item.name}`}
            accessibilityRole="button"
            onPress={() => onSelectProduct(item)}
            style={styles.productCard}
          >
            <View style={styles.avatar}>
              <Text style={styles.avatarText}>{index + 1}</Text>
            </View>
            <View style={styles.productText}>
              <Text style={styles.rowTitle}>{item.name}</Text>
              <Text style={styles.rowSubtitle}>
                {item.category} · {item.badge}
              </Text>
            </View>
            <Text style={styles.price}>{item.price}</Text>
          </Pressable>
        )}
      />
    </View>
  );
}

function ProductDetailScreen({
  product,
}: {
  product: Product;
}): React.JSX.Element {
  return (
    <ScrollView contentContainerStyle={styles.scrollContent}>
      <Text style={styles.screenTitle}>{product.name}</Text>
      <Text style={styles.detailPrice}>{product.price}</Text>
      <View style={styles.badgeRow}>
        <Text style={styles.badge}>{product.category}</Text>
        <Text style={styles.badge}>{product.badge}</Text>
      </View>
      <Text style={styles.description}>{product.description}</Text>
      <Pressable accessibilityRole="button" style={styles.primaryButton}>
        <Text style={styles.primaryButtonText}>장바구니 담기</Text>
      </Pressable>
    </ScrollView>
  );
}

function WebViewScreen(): React.JSX.Element {
  return (
    <View style={styles.full}>
      <View style={styles.webHeader}>
        <Text style={styles.webHeaderText}>앱 내부 WebView 샘플</Text>
      </View>
      <WebView
        accessibilityLabel="웹뷰 콘텐츠 영역"
        javaScriptEnabled
        allowFileAccess
        originWhitelist={['*']}
        source={{ html, baseUrl: 'file:///android_asset/' }}
        style={styles.webView}
      />
    </View>
  );
}

function SettingsScreen({
  selectedSetting,
  onSelectSetting,
}: {
  selectedSetting: SettingItem | null;
  onSelectSetting: (setting: SettingItem) => void;
}): React.JSX.Element {
  if (selectedSetting) {
    return <SettingDetailScreen item={selectedSetting} />;
  }

  return (
    <ScrollView contentContainerStyle={styles.listContent}>
      {settings.map(item => (
        <Pressable
          key={item.title}
          accessibilityLabel={`설정 항목 ${item.title}`}
          accessibilityRole="button"
          onPress={() => onSelectSetting(item)}
          style={styles.settingCard}
        >
          <View style={styles.settingIcon}>
            <Text style={styles.settingIconText}>{item.title.slice(0, 1)}</Text>
          </View>
          <View style={styles.productText}>
            <Text style={styles.rowTitle}>{item.title}</Text>
            <Text style={styles.rowSubtitle}>{item.subtitle}</Text>
          </View>
          <Text style={styles.chevron}>›</Text>
        </Pressable>
      ))}
    </ScrollView>
  );
}

function SettingDetailScreen({
  item,
}: {
  item: SettingItem;
}): React.JSX.Element {
  const [notificationEnabled, setNotificationEnabled] = useState(true);
  const [sound, setSound] = useState('기본 알림음');
  const [darkMode, setDarkMode] = useState(false);
  const [brightness, setBrightness] = useState(70);
  const [privacyOptions, setPrivacyOptions] = useState(
    new Set(['위치 정보', '사용 기록']),
  );

  return (
    <ScrollView contentContainerStyle={styles.scrollContent}>
      <Text style={styles.screenTitle}>{item.title} 상세</Text>
      <Text style={styles.description}>{item.subtitle}</Text>
      <View style={styles.card}>{renderSettingDetail()}</View>
    </ScrollView>
  );

  function renderSettingDetail(): React.JSX.Element {
    switch (item.type) {
      case 'notification':
        return (
          <View>
            <View style={styles.switchRow}>
              <View>
                <Text style={styles.rowTitle}>알림 받기</Text>
                <Text style={styles.rowSubtitle}>
                  전체 푸시 알림을 켜거나 끕니다.
                </Text>
              </View>
              <Switch
                accessibilityLabel="알림 받기"
                value={notificationEnabled}
                onValueChange={setNotificationEnabled}
              />
            </View>
            <View style={styles.divider} />
            {['기본 알림음', '짧은 알림음', '진동만'].map(value => (
              <Pressable
                key={value}
                accessibilityLabel={value}
                accessibilityRole="radio"
                accessibilityState={{ selected: sound === value }}
                onPress={() => setSound(value)}
                style={styles.radioRow}
              >
                <View
                  style={[
                    styles.radioCircle,
                    sound === value && styles.radioCircleSelected,
                  ]}
                />
                <Text style={styles.rowTitle}>{value}</Text>
              </Pressable>
            ))}
          </View>
        );
      case 'display':
        return (
          <View>
            <Text style={styles.sectionTitle}>화면 밝기 {brightness}%</Text>
            <View style={styles.stepperRow}>
              {[30, 50, 70, 100].map(value => (
                <Pressable
                  key={value}
                  accessibilityLabel={`밝기 ${value} 퍼센트`}
                  accessibilityRole="button"
                  accessibilityState={{ selected: brightness === value }}
                  onPress={() => setBrightness(value)}
                  style={[
                    styles.stepButton,
                    brightness === value && styles.stepButtonActive,
                  ]}
                >
                  <Text
                    style={[
                      styles.stepButtonText,
                      brightness === value && styles.stepButtonTextActive,
                    ]}
                  >
                    {value}%
                  </Text>
                </Pressable>
              ))}
            </View>
            <View style={styles.switchRow}>
              <Text style={styles.rowTitle}>다크 모드</Text>
              <Switch
                accessibilityLabel="다크 모드"
                value={darkMode}
                onValueChange={setDarkMode}
              />
            </View>
          </View>
        );
      case 'privacy':
        return (
          <View>
            {['위치 정보', '사용 기록', '광고 맞춤 설정', '진단 데이터'].map(
              value => {
                const checked = privacyOptions.has(value);
                return (
                  <Pressable
                    key={value}
                    accessibilityLabel={value}
                    accessibilityRole="checkbox"
                    accessibilityState={{ checked }}
                    onPress={() => {
                      const next = new Set(privacyOptions);
                      if (checked) {
                        next.delete(value);
                      } else {
                        next.add(value);
                      }
                      setPrivacyOptions(next);
                    }}
                    style={styles.checkboxRow}
                  >
                    <View
                      style={[styles.checkbox, checked && styles.checkboxOn]}
                    >
                      <Text style={styles.checkboxMark}>
                        {checked ? '✓' : ''}
                      </Text>
                    </View>
                    <Text style={styles.rowTitle}>{value}</Text>
                  </Pressable>
                );
              },
            )}
          </View>
        );
      case 'storage':
        return (
          <View>
            <StorageRow label="앱 데이터" value="124 MB" />
            <StorageRow label="캐시" value="36 MB" />
            <StorageRow label="다운로드" value="8 MB" />
            <Pressable accessibilityRole="button" style={styles.primaryButton}>
              <Text style={styles.primaryButtonText}>캐시 정리</Text>
            </Pressable>
          </View>
        );
      default:
        return <View />;
    }
  }
}

function StorageRow({
  label,
  value,
}: {
  label: string;
  value: string;
}): React.JSX.Element {
  return (
    <View style={styles.storageRow}>
      <Text style={styles.rowTitle}>{label}</Text>
      <Text style={styles.price}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  app: {
    flex: 1,
    backgroundColor: '#f8fafc',
  },
  header: {
    alignItems: 'center',
    borderBottomColor: '#e5e7eb',
    borderBottomWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    gap: 10,
    minHeight: 56,
    paddingHorizontal: 20,
    paddingVertical: 14,
    backgroundColor: '#ffffff',
  },
  headerBackButton: {
    alignItems: 'center',
    height: 32,
    justifyContent: 'center',
    width: 32,
  },
  headerBackText: {
    color: '#111827',
    fontSize: 30,
    fontWeight: '600',
    lineHeight: 30,
  },
  headerTitle: {
    color: '#111827',
    flexShrink: 1,
    fontSize: 22,
    fontWeight: '700',
  },
  content: {
    flex: 1,
  },
  full: {
    flex: 1,
  },
  scrollContent: {
    padding: 20,
    gap: 14,
  },
  screenTitle: {
    color: '#111827',
    fontSize: 24,
    fontWeight: '700',
  },
  description: {
    color: '#4b5563',
    fontSize: 15,
    lineHeight: 22,
  },
  card: {
    backgroundColor: '#ffffff',
    borderColor: '#e5e7eb',
    borderRadius: 8,
    borderWidth: 1,
    padding: 16,
  },
  sectionTitle: {
    color: '#111827',
    fontSize: 17,
    fontWeight: '700',
    marginBottom: 10,
  },
  radioRow: {
    alignItems: 'center',
    flexDirection: 'row',
    minHeight: 48,
    gap: 12,
  },
  radioCircle: {
    borderColor: '#9ca3af',
    borderRadius: 10,
    borderWidth: 2,
    height: 20,
    width: 20,
  },
  radioCircleSelected: {
    backgroundColor: '#2563eb',
    borderColor: '#2563eb',
  },
  rowTitle: {
    color: '#111827',
    fontSize: 16,
    fontWeight: '600',
  },
  rowSubtitle: {
    color: '#6b7280',
    fontSize: 13,
    lineHeight: 19,
    marginTop: 2,
  },
  divider: {
    backgroundColor: '#e5e7eb',
    height: StyleSheet.hairlineWidth,
    marginVertical: 8,
  },
  valueText: {
    color: '#374151',
    fontSize: 15,
    fontWeight: '600',
  },
  filterRow: {
    gap: 8,
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  filterChip: {
    alignItems: 'center',
    borderColor: '#d1d5db',
    borderRadius: 18,
    borderWidth: 1,
    height: 36,
    justifyContent: 'center',
    paddingHorizontal: 14,
  },
  filterActive: {
    backgroundColor: '#2563eb',
    borderColor: '#2563eb',
  },
  filterText: {
    color: '#374151',
    fontSize: 14,
    fontWeight: '600',
  },
  filterTextActive: {
    color: '#ffffff',
  },
  listContent: {
    gap: 10,
    padding: 16,
  },
  productCard: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderColor: '#e5e7eb',
    borderRadius: 8,
    borderWidth: 1,
    flexDirection: 'row',
    gap: 12,
    minHeight: 76,
    padding: 14,
  },
  avatar: {
    alignItems: 'center',
    backgroundColor: '#dbeafe',
    borderRadius: 20,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  avatarText: {
    color: '#1d4ed8',
    fontWeight: '700',
  },
  productText: {
    flex: 1,
  },
  price: {
    color: '#111827',
    fontSize: 15,
    fontWeight: '700',
  },
  backButton: {
    alignSelf: 'flex-start',
    borderColor: '#d1d5db',
    borderRadius: 6,
    borderWidth: 1,
    paddingHorizontal: 14,
    paddingVertical: 8,
  },
  backButtonText: {
    color: '#111827',
    fontSize: 15,
    fontWeight: '700',
  },
  detailPrice: {
    color: '#111827',
    fontSize: 22,
    fontWeight: '700',
  },
  badgeRow: {
    flexDirection: 'row',
    gap: 8,
  },
  badge: {
    backgroundColor: '#ecfeff',
    borderColor: '#67e8f9',
    borderRadius: 16,
    borderWidth: 1,
    color: '#155e75',
    fontWeight: '700',
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  primaryButton: {
    alignItems: 'center',
    backgroundColor: '#2563eb',
    borderRadius: 8,
    justifyContent: 'center',
    marginTop: 14,
    minHeight: 48,
  },
  primaryButtonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '700',
  },
  outlineButton: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderColor: '#cbd5e1',
    borderRadius: 8,
    borderWidth: 1,
    justifyContent: 'center',
    marginTop: 10,
    minHeight: 48,
  },
  outlineButtonText: {
    color: '#111827',
    fontSize: 16,
    fontWeight: '700',
  },
  dialogBackdrop: {
    alignItems: 'center',
    backgroundColor: 'rgba(15, 23, 42, 0.48)',
    flex: 1,
    justifyContent: 'center',
    padding: 20,
  },
  dialogCard: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    gap: 10,
    padding: 18,
    width: '100%',
  },
  bottomSheetBackdrop: {
    backgroundColor: 'rgba(15, 23, 42, 0.48)',
    flex: 1,
    justifyContent: 'flex-end',
  },
  bottomSheetCard: {
    backgroundColor: '#ffffff',
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    gap: 10,
    padding: 20,
  },
  popupPanel: {
    backgroundColor: '#ffffff',
    borderColor: '#cbd5e1',
    borderRadius: 8,
    borderWidth: 1,
    marginTop: 8,
    overflow: 'hidden',
  },
  popupOption: {
    borderBottomColor: '#e5e7eb',
    borderBottomWidth: StyleSheet.hairlineWidth,
    minHeight: 44,
    justifyContent: 'center',
    paddingHorizontal: 12,
  },
  webHeader: {
    backgroundColor: '#e0f2fe',
    padding: 16,
  },
  webHeaderText: {
    color: '#0c4a6e',
    fontSize: 16,
    fontWeight: '700',
  },
  webView: {
    flex: 1,
  },
  settingCard: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderColor: '#e5e7eb',
    borderRadius: 8,
    borderWidth: 1,
    flexDirection: 'row',
    gap: 12,
    minHeight: 76,
    padding: 14,
  },
  settingIcon: {
    alignItems: 'center',
    backgroundColor: '#fef3c7',
    borderRadius: 20,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  settingIconText: {
    color: '#92400e',
    fontWeight: '800',
  },
  chevron: {
    color: '#6b7280',
    fontSize: 28,
  },
  switchRow: {
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'space-between',
    minHeight: 56,
  },
  stepperRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 18,
  },
  stepButton: {
    alignItems: 'center',
    borderColor: '#d1d5db',
    borderRadius: 8,
    borderWidth: 1,
    height: 44,
    justifyContent: 'center',
    minWidth: 68,
  },
  stepButtonActive: {
    backgroundColor: '#2563eb',
    borderColor: '#2563eb',
  },
  stepButtonText: {
    color: '#374151',
    fontWeight: '700',
  },
  stepButtonTextActive: {
    color: '#ffffff',
  },
  checkboxRow: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 12,
    minHeight: 48,
  },
  checkbox: {
    alignItems: 'center',
    borderColor: '#9ca3af',
    borderRadius: 4,
    borderWidth: 2,
    height: 22,
    justifyContent: 'center',
    width: 22,
  },
  checkboxOn: {
    backgroundColor: '#2563eb',
    borderColor: '#2563eb',
  },
  checkboxMark: {
    color: '#ffffff',
    fontSize: 15,
    fontWeight: '800',
  },
  storageRow: {
    alignItems: 'center',
    borderBottomColor: '#e5e7eb',
    borderBottomWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    justifyContent: 'space-between',
    minHeight: 48,
  },
  tabBar: {
    backgroundColor: '#ffffff',
    borderTopColor: '#e5e7eb',
    borderTopWidth: StyleSheet.hairlineWidth,
    flexDirection: 'row',
    paddingHorizontal: 8,
    paddingVertical: 8,
  },
  tabButton: {
    alignItems: 'center',
    borderRadius: 8,
    flex: 1,
    minHeight: 44,
    justifyContent: 'center',
  },
  tabButtonSelected: {
    backgroundColor: '#dbeafe',
  },
  tabText: {
    color: '#4b5563',
    fontSize: 13,
    fontWeight: '700',
  },
  tabTextSelected: {
    color: '#1d4ed8',
  },
});

export default App;
