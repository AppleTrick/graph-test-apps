# Test App Structure

## Project Layout

| Path | Stack | Android Entry | Main UI |
| --- | --- | --- | --- |
| `kotlin/` | Kotlin + Jetpack Compose | `kotlin/app/src/main/java/com/example/graphpilottest/compose/MainActivity.kt` | `GraphPilotSampleApp()` |
| `java/` | Java + XML + Fragment | `java/app/src/main/java/com/example/graphpilottest/javaxml/MainActivity.java` | `activity_main.xml`, Fragment classes |
| `flutter/` | Flutter | `flutter/android/app/src/main/kotlin/com/example/graphpilottest/graphpilot_flutter_sample/MainActivity.kt` | `flutter/lib/main.dart` |
| `react-native/` | React Native | `react-native/android/app/src/main/java/com/graphpilotreactnativesample/MainActivity.kt` | `react-native/App.tsx` |

## Main App Structure

| Area | Elements |
| --- | --- |
| App shell | Top app bar/header, content area, bottom tab navigation |
| Bottom tabs | 홈, 상품목록, 웹뷰, 설정 |
| Detail depth | 상품 상세, 설정 상세 |
| Overlay surfaces | Modal Dialog, Modal BottomSheet, Popup Menu |
| Embedded web | Local HTML WebView, map view state, status text |

## Top-Level Tabs

| Tab | Screen | Main Elements |
| --- | --- | --- |
| 홈 | Home | 벨소리 선택, 현재 선택 텍스트, overlay 테스트 영역 |
| 상품목록 | Product list | 필터, 상품 리스트, 상품 상세 진입 |
| 웹뷰 | WebView | 로컬 HTML WebView, 지도 보기, 상태 변경 |
| 설정 | Settings | 설정 항목 리스트, 설정 상세 진입 |

## Home Elements

- Screen title/description
- Ringtone selection
  - 기본 벨소리
  - 아침 알림/맑은 종소리
  - 클래식/디지털 알림음
  - 무음
- Selected ringtone text
- Surface Overlay test area
  - Modal Dialog 열기
  - Modal BottomSheet 열기
  - Popup Menu 열기
  - Overlay status text
- Modal Dialog
  - Title
  - Description
  - 확인
  - 취소
- Modal BottomSheet
  - Current step text
  - 시트 내부 상태 변경
  - 적용
- Popup Menu
  - 정렬: 최신순
  - 정렬: 가격순
  - 보기: 카드형

## Product Elements

- Product filters
  - 전체
  - 전자제품
  - 생활용품
  - 인기상품
  - 필터 초기화
- Product list
  - Kotlin/Java: 노트북, 무선 이어폰, 커피머신, 기계식 키보드, 사무용 의자
  - Flutter/React Native: 스마트 체중계, 무선 키보드, 주방 수납함, 욕실 정리 선반, USB-C 충전기
- Product card fields
  - Product name
  - Category
  - Price
  - Rating or badge
  - Shipping or description
  - Detail action
- Product detail
  - Product name
  - Category
  - Price
  - Rating/shipping or badge/description
  - 장바구니 담기 or list back action

## WebView Elements

- WebView container
- Local HTML mini app
  - 지도 웹뷰 홈
  - 지도 보기
  - 상태 텍스트 변경
  - 지도 보기 상세
  - 지도 상태 변경
  - 웹 홈으로 돌아가기
  - Status area
- Map image
  - Flutter: `assets/webview-map.png`
  - Java/Kotlin/React Native: `webview-map.png` referenced from local HTML

## Settings Elements

- Settings list
  - 알림 설정
  - 화면 설정
  - 개인정보 설정
  - 저장공간 설정
- 알림 설정 상세
  - 알림 받기 Switch
  - 알림음 Radio options: 기본 알림음, 짧은 알림음, 진동만
- 화면 설정 상세
  - Brightness slider or step buttons
  - 다크 모드 Switch
- 개인정보 설정 상세
  - Profile/location/history/privacy checkbox options
- 저장공간 설정 상세
  - Storage usage rows
  - 캐시 정리 button

## Navigation Structure

| Stack | Top-level navigation | Detail navigation |
| --- | --- | --- |
| Kotlin Compose | `selectedTab` state | `selectedProduct`, `selectedSetting` state |
| Java/XML | `BottomNavigationView` + Fragment replace | Product/setting detail Fragment |
| Flutter | `NavigationBar` + `IndexedStack` | `Navigator.push()` detail routes |
| React Native | Root `tab` state | Root `selectedProduct`, `selectedSetting` state |
