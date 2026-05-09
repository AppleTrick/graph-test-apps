# GraphPilot Compose Sample

Jetpack Compose 기반 GraphPilot 탐색 테스트 앱입니다.

포함 패턴:

- Bottom navigation: 홈, 상품목록, 웹뷰, 설정
- WebView: 로컬 HTML 문자열 렌더링
- 복잡 리스트: 상품 카드형 LazyColumn
- 단순 선택 리스트: 벨소리 RadioButton 선택
- 리스트 + 상세: 상품 상세, 설정 상세
- Filter: 상품 카테고리/인기 필터와 필터 초기화

주요 UI에는 `text`, `contentDescription`, `testTag`가 들어가며 root에 `testTagsAsResourceId`가 설정되어 있습니다.
