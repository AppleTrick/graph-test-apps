# GraphPilot Test Apps

Flutter, React Native, Kotlin Compose, Java/XML 기반 Android 샘플 앱 모음이다.  
팀 내 앱 탐색/커버리지 평가를 위해 동일한 목적의 테스트 화면과 UI 요소를 4개 기술 스택으로 구현했다.

## 배경

기존 앱을 그대로 평가 대상으로 쓰면 다음 문제가 있다.

- 기존 앱이 어떤 기능과 화면 깊이를 가지고 있는지 명확하지 않다.
- 앱의 depth가 깊으면 탐색 시간이 과도하게 늘어난다.
- 어디까지 커버리지를 측정할 수 있는지 기준을 잡기 어렵다.
- 같은 환경에서 어떤 컴포넌트가 문제를 일으키는지 분리해서 보기 어렵다.
- 다양한 APK 유형을 함께 비교하기 어렵다.
  - Flutter
  - React Native
  - Kotlin Compose
  - Java/XML

이런 이유로 팀원들이 빠르게 평가할 수 있는 기준 앱을 별도로 만들었다.

## 목적

4개 앱은 서로 다른 기술 스택을 사용하지만, 평가 관점에서는 같은 종류의 화면과 UI 요소를 가지도록 구성했다.

주요 조건은 다음과 같다.

- 탐색 depth가 너무 깊어지지 않을 것
- 앱 탐색/커버리지 평가에 필요한 대표 UI 요소를 포함할 것
- 각 스택에서 유사한 화면 구조를 제공할 것
- 외부 서버 없이 로컬 asset과 로컬 HTML만으로 동작할 것
- 반복 리스트, 상세 화면, 필터, WebView, modal surface를 모두 포함할 것

## 프로젝트 구성

| Path            | Stack                    | Android Entry                      | Main UI                               |
| --------------- | ------------------------ | ---------------------------------- | ------------------------------------- |
| `kotlin/`       | Kotlin + Jetpack Compose | `MainActivity`                     | `GraphPilotSampleApp()`               |
| `java/`         | Java + XML + Fragment    | `MainActivity`                     | `activity_main.xml`, Fragment classes |
| `flutter/`      | Flutter                  | `MainActivity` / `FlutterActivity` | `flutter/lib/main.dart`               |
| `react-native/` | React Native             | `MainActivity` / `ReactActivity`   | `react-native/App.tsx`                |

구조와 요소 목록은 [TEST_APP_STRUCTURE.md](./TEST_APP_STRUCTURE.md)에 별도로 정리했다.
Graph 평가 기준은 [GRAPH_EVALUATION.md](./GRAPH_EVALUATION.md)에 정리했다.

## 포함 요소

| Category                  | 구현 내용                                                                         |
| ------------------------- | --------------------------------------------------------------------------------- |
| Bottom navigation         | 홈, 상품목록, 웹뷰, 설정 4개 탭                                                   |
| Fragment/screen switching | Java/XML은 Fragment + BottomNavigationView, 나머지는 각 프레임워크 방식의 탭 전환 |
| WebView                   | 로컬 HTML 기반 mini web app, 지도 보기 화면, 상태 변경                            |
| Repeated list             | 상품 목록 반복 item 리스트                                                        |
| Detail page               | 상품 상세, 설정 상세                                                              |
| Single choice list        | 벨소리 선택 Radio/SingleChoice UI                                                 |
| Navigation list           | 설정 메뉴 리스트와 항목별 상세 화면                                               |
| Filter controls           | 전체, 전자제품, 생활용품, 인기상품 필터                                           |
| Modal/surface             | Modal Dialog, Modal BottomSheet, Popup Menu/Dropdown                              |
| Local asset               | imagegen으로 생성한 샘플 지도 이미지 asset                                        |

## 화면 구성

| Tab      | 목적                                    | 주요 요소                                                 |
| -------- | --------------------------------------- | --------------------------------------------------------- |
| 홈       | 단순 선택 리스트와 overlay surface 평가 | 벨소리 선택, 선택 상태 텍스트, Dialog, BottomSheet, Popup |
| 상품목록 | 반복 리스트와 필터 평가                 | 상품 카드 리스트, 필터, 상품 상세 진입                    |
| 웹뷰     | WebView 탐색 평가                       | 로컬 HTML, 지도 보기, 상태 변경, 지도 이미지              |
| 설정     | Navigation list와 상세 화면 평가        | 설정 목록, 설정 상세별 서로 다른 UI                       |

설정 상세 화면은 4개 항목이 서로 다른 UI 형태를 가진다.

- 알림 설정: Switch, Radio button
- 화면 설정: Brightness slider/step controls, Dark mode switch
- 개인정보 설정: Checkbox permission list
- 저장공간 설정: Storage usage rows, Cache clear button

## Navigation 기준

일반적인 앱처럼 보이도록 하위 depth 화면에서는 하단 탭을 숨긴다.

- Top-level 화면: 하단 탭 표시
- 상품 상세/설정 상세: 하단 탭 숨김
- 상세 화면 뒤로가기: 부모 목록 화면으로 복귀
- Flutter: `IndexedStack`으로 탭별 상태 유지
- React Native: Android hardware back 처리
- Kotlin Compose: `BackHandler` 처리
- Java/XML: `OnBackPressedCallback` 처리

## 검증

현재 기준으로 아래 검증을 통과했다.

- Kotlin Compose: `./gradlew :app:assembleDebug`
- Java/XML: `./gradlew :app:assembleDebug`
- Flutter: `flutter analyze`
- React Native: `npx tsc --noEmit`, `npm run lint`, `npx prettier --check App.tsx`

## 결과

이 앱들은 다음 항목을 평가하기 위한 기준 샘플로 사용할 수 있다.

- 커버리지 또는 탐색율
- 오탐율
- 탐색 시간
- 스택별 UI 탐색 차이
- 특정 컴포넌트 유형별 탐색 문제

기본 graph 평가 기준은 **13 nodes / 36 edges** 로 둔다.

- Node: 도달 가능한 화면 또는 독립 surface
- Edge: 화면 전환 또는 surface open/close 행동
- 반복 item 개별화와 단순 상태 변경 action은 기본 graph에서 제외
