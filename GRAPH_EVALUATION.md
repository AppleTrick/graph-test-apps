# Graph Evaluation 기준

이 문서는 테스트 앱을 graph 형태로 평가할 때 사용하는 기본 node/edge 기준이다.

기본 기준은 **13 nodes / 36 edges** 이다.

## 평가 관점

- Node: 사용자가 도달 가능한 화면 또는 독립 surface
- Edge: 사용자가 수행하는 화면 전환 또는 surface open/close 행동
- 반복 item은 동일한 UI template으로 본다.
- 필터, 라디오, 스위치, 체크박스 같은 단순 상태 변경은 기본 edge 수에 포함하지 않는다.
- WebView 내부 화면 전환은 앱 탐색 대상에 포함한다.
- Modal Dialog, Modal BottomSheet, Popup Menu는 독립 surface node로 본다.

## Node 기준

| ID | Node | 설명 |
| --- | --- | --- |
| N1 | 홈 | 앱 진입 후 기본 탭 화면 |
| N2 | 상품목록 | 반복 상품 리스트와 필터가 있는 탭 화면 |
| N3 | 상품상세 | 상품 item 클릭 후 진입하는 상세 화면 template |
| N4 | 웹뷰 홈 | WebView 탭의 로컬 HTML 첫 화면 |
| N5 | 웹뷰 지도상세 | WebView 내부 `지도 보기` 후 표시되는 지도 화면 |
| N6 | 설정목록 | 설정 item 리스트가 있는 탭 화면 |
| N7 | 알림 설정 상세 | Switch/Radio 기반 설정 상세 |
| N8 | 화면 설정 상세 | 밝기/다크모드 기반 설정 상세 |
| N9 | 개인정보 설정 상세 | Checkbox 기반 설정 상세 |
| N10 | 저장공간 설정 상세 | 저장공간 rows/캐시 정리 기반 설정 상세 |
| N11 | Modal Dialog | 홈 화면에서 열리는 dialog surface |
| N12 | Modal BottomSheet | 홈 화면에서 열리는 bottom sheet surface |
| N13 | Popup Menu | 홈 화면에서 열리는 popup/dropdown surface |

총 node 수: **13**

## Edge 기준

### Top-level tab navigation

하단 탭 간 이동은 현재 탭에서 다른 3개 탭으로 이동 가능한 directed edge로 본다.

| Edge group | Count |
| --- | ---: |
| 홈 -> 상품목록/웹뷰/설정 | 3 |
| 상품목록 -> 홈/웹뷰/설정 | 3 |
| 웹뷰 -> 홈/상품목록/설정 | 3 |
| 설정 -> 홈/상품목록/웹뷰 | 3 |

Subtotal: **12**

### Product navigation

| From | To | Action |
| --- | --- | --- |
| 상품목록 | 상품상세 | 상품 item/detail action |
| 상품상세 | 상품목록 | back action |

Subtotal: **2**

### WebView navigation

| From | To | Action |
| --- | --- | --- |
| 웹뷰 홈 | 웹뷰 지도상세 | 지도 보기 |
| 웹뷰 지도상세 | 웹뷰 홈 | 웹 홈으로 돌아가기 |

Subtotal: **2**

### Settings navigation

| From | To | Action |
| --- | --- | --- |
| 설정목록 | 알림 설정 상세 | 알림 설정 선택 |
| 알림 설정 상세 | 설정목록 | back action |
| 설정목록 | 화면 설정 상세 | 화면 설정 선택 |
| 화면 설정 상세 | 설정목록 | back action |
| 설정목록 | 개인정보 설정 상세 | 개인정보 설정 선택 |
| 개인정보 설정 상세 | 설정목록 | back action |
| 설정목록 | 저장공간 설정 상세 | 저장공간 설정 선택 |
| 저장공간 설정 상세 | 설정목록 | back action |

Subtotal: **8**

### Overlay surfaces

각 overlay는 열기와 닫기를 각각 edge로 본다.

| From | To | Action |
| --- | --- | --- |
| 홈 | Modal Dialog | Modal Dialog 열기 |
| Modal Dialog | 홈 | 확인/취소/닫기 |
| 홈 | Modal BottomSheet | Modal BottomSheet 열기 |
| Modal BottomSheet | 홈 | 적용/닫기 |
| 홈 | Popup Menu | Popup Menu 열기 |
| Popup Menu | 홈 | menu item 선택/닫기 |

Subtotal: **6**

### Detail to top-level escape navigation

하위 depth 화면에서 하단 탭은 숨겨지지만, 뒤로가기 후 top-level 탭 탐색이 가능하다. 평가 graph에서는 상세 화면에서 복귀 가능한 부모 화면 외에 앱 전체 top-level로 빠지는 대표 escape edge를 포함한다.

| From | To | Count |
| --- | --- | ---: |
| 상품상세 | 홈/웹뷰/설정 | 3 |
| 설정 상세 계열 | 홈/상품목록/웹뷰 | 3 |

Subtotal: **6**

## Edge 합계

| Group | Count |
| --- | ---: |
| Top-level tab navigation | 12 |
| Product navigation | 2 |
| WebView navigation | 2 |
| Settings navigation | 8 |
| Overlay surfaces | 6 |
| Detail to top-level escape navigation | 6 |
| Total | **36** |

## 제외 기준

다음 행동은 기본 edge 수에 포함하지 않는다.

- 상품 반복 item 5개 각각을 별도 node로 분리하는 경우
- 필터 선택
  - 전체
  - 전자제품
  - 생활용품
  - 인기상품
- 벨소리 single choice 선택
- Switch on/off
- Slider 또는 brightness step 변경
- Checkbox 선택/해제
- BottomSheet 내부 상태 변경
- WebView 내부 상태 텍스트 변경
- 장바구니 담기
- 캐시 정리

이 항목들은 화면 전환보다 상태 변경 action에 가깝기 때문에 기본 graph에서는 제외한다.

## 확장 기준

더 세밀하게 평가할 경우 다음 방식으로 확장할 수 있다.

| 기준 | 예상 node/edge |
| --- | --- |
| 기본 화면/surface 기준 | 13 nodes / 36 edges |
| 상품 5개 상세를 개별 node로 분리 | 약 17 nodes / 44 edges |
| 상태 변경 action까지 edge로 포함 | 13 nodes / 약 62 edges |

기본 평가에서는 **13 nodes / 36 edges** 를 사용한다.
