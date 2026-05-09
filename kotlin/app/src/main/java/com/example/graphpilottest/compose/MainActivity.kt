package com.example.graphpilottest.compose

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GraphPilotSampleApp()
                }
            }
        }
    }
}

private enum class BottomTab(val label: String, val tag: String) {
    Home("홈", "tab_home"),
    Products("상품목록", "tab_products"),
    WebView("웹뷰", "tab_webview"),
    Settings("설정", "tab_settings"),
}

private data class SampleProduct(
    val name: String,
    val category: String,
    val price: String,
    val rating: String,
    val shipping: String,
    val popular: Boolean,
)

private data class SettingEntry(
    val title: String,
    val description: String,
)

private val products = listOf(
    SampleProduct("노트북", "전자제품", "1,290,000원", "평점 4.8", "내일 도착", true),
    SampleProduct("무선 이어폰", "전자제품", "129,000원", "평점 4.6", "오늘 도착", true),
    SampleProduct("커피머신", "생활용품", "219,000원", "평점 4.5", "무료 배송", false),
    SampleProduct("기계식 키보드", "전자제품", "89,000원", "평점 4.7", "모레 도착", false),
    SampleProduct("사무용 의자", "생활용품", "159,000원", "평점 4.4", "화물 배송", true),
)

private val settings = listOf(
    SettingEntry("알림 설정", "앱 알림과 소리 옵션을 확인합니다."),
    SettingEntry("화면 설정", "밝기와 화면 표시 방식을 확인합니다."),
    SettingEntry("개인정보 설정", "개인정보 표시 옵션을 확인합니다."),
    SettingEntry("저장공간 설정", "저장공간 사용량 옵션을 확인합니다."),
)

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
private fun GraphPilotSampleApp() {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var selectedProduct by remember { mutableStateOf<SampleProduct?>(null) }
    var selectedSetting by remember { mutableStateOf<SettingEntry?>(null) }
    val isDetailScreen = selectedProduct != null || selectedSetting != null
    val screenTitle = when {
        selectedProduct != null -> "${selectedProduct!!.name} 상세"
        selectedSetting != null -> "${selectedSetting!!.title} 상세"
        else -> selectedTab.label
    }

    fun closeDetailScreen() {
        selectedProduct = null
        selectedSetting = null
    }

    BackHandler(enabled = isDetailScreen) {
        closeDetailScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screenTitle) },
                    navigationIcon = {
                        if (isDetailScreen) {
                            TextButton(
                                onClick = { closeDetailScreen() },
                                modifier = Modifier.semantics { contentDescription = "뒤로가기" },
                            ) {
                                Text("뒤로")
                            }
                        }
                    },
                )
            },
            bottomBar = {
                if (!isDetailScreen) {
                    NavigationBar(modifier = Modifier.testTag("bottom_navigation")) {
                        BottomTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = {
                                    selectedTab = tab
                                    selectedProduct = null
                                    selectedSetting = null
                                },
                                label = { Text(tab.label) },
                                icon = {},
                                modifier = Modifier
                                    .testTag(tab.tag)
                                    .semantics { contentDescription = "하단 탭 ${tab.label}" },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                when {
                    selectedProduct != null -> ProductDetailScreen(
                        product = selectedProduct!!,
                        onBack = { selectedProduct = null },
                    )
                    selectedSetting != null -> SettingDetailScreen(
                        setting = selectedSetting!!,
                        onBack = { selectedSetting = null },
                    )
                    selectedTab == BottomTab.Home -> HomeScreen()
                    selectedTab == BottomTab.Products -> ProductListScreen(
                        onProductClick = { selectedProduct = it },
                    )
                    selectedTab == BottomTab.WebView -> LocalWebViewScreen()
                    selectedTab == BottomTab.Settings -> SettingsScreen(
                        onSettingClick = { selectedSetting = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenTitle(title: String, description: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .testTag("screen_title_$title")
            .semantics { contentDescription = "화면 제목 $title" },
    )
    Text(text = description, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun HomeScreen() {
    val ringtones = listOf("기본 벨소리", "아침 알림", "클래식", "무음")
    var selectedRingtone by remember { mutableStateOf(ringtones.first()) }

    ScreenTitle("홈", "단순 선택 리스트 패턴을 확인하는 화면입니다.")
    Text(
        text = "현재 선택: $selectedRingtone",
        modifier = Modifier
            .testTag("selected_ringtone_text")
            .semantics { contentDescription = "현재 선택 벨소리 $selectedRingtone" },
    )
    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ringtones.forEach { ringtone ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedRingtone = ringtone }
                    .testTag("ringtone_option_$ringtone")
                    .semantics { contentDescription = "벨소리 선택 $ringtone" }
                    .padding(8.dp),
            ) {
                RadioButton(
                    selected = selectedRingtone == ringtone,
                    onClick = { selectedRingtone = ringtone },
                )
                Text(text = ringtone, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    OverlaySurfaceSection()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun OverlaySurfaceSection() {
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPopupMenu by remember { mutableStateOf(false) }
    var bottomSheetStep by remember { mutableStateOf("요약 단계") }
    var overlayStatus by remember { mutableStateOf("대기 중") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("overlay_surface_card")
            .semantics { contentDescription = "Surface Overlay 테스트 영역" },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Text("Surface Overlay 테스트", style = MaterialTheme.typography.titleMedium)
            Text("현재 상태: $overlayStatus")
            Button(
                onClick = {
                    overlayStatus = "Modal Dialog 열림"
                    showDialog = true
                },
                modifier = Modifier
                    .testTag("open_modal_dialog_button")
                    .semantics { contentDescription = "Modal Dialog 열기" },
            ) {
                Text("Modal Dialog 열기")
            }
            Button(
                onClick = {
                    bottomSheetStep = "요약 단계"
                    overlayStatus = "Modal BottomSheet 열림"
                    showBottomSheet = true
                },
                modifier = Modifier
                    .testTag("open_bottom_sheet_button")
                    .semantics { contentDescription = "Modal BottomSheet 열기" },
            ) {
                Text("Modal BottomSheet 열기")
            }
            Box {
                Button(
                    onClick = { showPopupMenu = true },
                    modifier = Modifier
                        .testTag("open_popup_menu_button")
                        .semantics { contentDescription = "Popup Menu 열기" },
                ) {
                    Text("Popup Menu 열기")
                }
                DropdownMenu(
                    expanded = showPopupMenu,
                    onDismissRequest = { showPopupMenu = false },
                ) {
                    listOf("정렬: 최신순", "정렬: 가격순", "보기: 카드형").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                overlayStatus = "Popup 선택 $option"
                                showPopupMenu = false
                            },
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                overlayStatus = "Modal Dialog 닫힘"
                showDialog = false
            },
            title = { Text("Modal Dialog") },
            text = { Text("중앙에 뜨는 확인/취소형 modal surface 입니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        overlayStatus = "Dialog 확인 선택"
                        showDialog = false
                    },
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        overlayStatus = "Dialog 취소 선택"
                        showDialog = false
                    },
                ) {
                    Text("취소")
                }
            },
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                overlayStatus = "Modal BottomSheet 닫힘"
                showBottomSheet = false
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text("Modal BottomSheet", style = MaterialTheme.typography.titleLarge)
                Text("현재 단계: $bottomSheetStep")
                Button(
                    onClick = {
                        bottomSheetStep = "상세 옵션 선택됨"
                        overlayStatus = "BottomSheet 내부 상태 변경"
                    },
                    modifier = Modifier
                        .testTag("bottom_sheet_change_button")
                        .semantics { contentDescription = "BottomSheet 내부 상태 변경" },
                ) {
                    Text("시트 내부 상태 변경")
                }
                Button(
                    onClick = {
                        overlayStatus = "BottomSheet 적용 완료"
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .testTag("bottom_sheet_apply_button")
                        .semantics { contentDescription = "BottomSheet 적용" },
                ) {
                    Text("적용")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ProductListScreen(onProductClick: (SampleProduct) -> Unit) {
    var selectedFilter by remember { mutableStateOf("전체") }
    val filters = listOf("전체", "전자제품", "생활용품", "인기상품")
    val visibleProducts = products.filter { product ->
        selectedFilter == "전체" ||
            product.category == selectedFilter ||
            (selectedFilter == "인기상품" && product.popular)
    }

    ScreenTitle("상품목록", "복잡한 카드 리스트와 필터 탐지용 화면입니다.")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { selectedFilter = filter },
                label = { Text(filter) },
                modifier = Modifier
                    .testTag("filter_chip_$filter")
                    .semantics { contentDescription = "상품 필터 $filter" },
            )
        }
    }
    Button(
        onClick = { selectedFilter = "전체" },
        modifier = Modifier
            .padding(top = 8.dp)
            .testTag("filter_reset_button")
            .semantics { contentDescription = "필터 초기화" },
    ) {
        Text("필터 초기화")
    }
    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.testTag("product_lazy_column"),
    ) {
        items(visibleProducts, key = { it.name }) { product ->
            ProductCard(product = product, onClick = { onProductClick(product) })
        }
    }
}

@Composable
private fun ProductCard(product: SampleProduct, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.name}")
            .semantics { contentDescription = "상품 카드 ${product.name}" },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text("카테고리: ${product.category}")
            Text("가격: ${product.price}")
            Text(product.rating)
            Text("배송정보: ${product.shipping}")
            Button(
                onClick = onClick,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("product_detail_button_${product.name}")
                    .semantics { contentDescription = "${product.name} 상세 보기" },
            ) {
                Text("상세 보기")
            }
        }
    }
}

@Composable
private fun ProductDetailScreen(product: SampleProduct, onBack: () -> Unit) {
    ScreenTitle("${product.name} 상세", "리스트 아이템에서 진입한 상세 화면입니다.")
    Text("상품명: ${product.name}")
    Text("분류: ${product.category}")
    Text("가격: ${product.price}")
    Text("평점: ${product.rating}")
    Text("배송: ${product.shipping}")
    Button(
        onClick = onBack,
        modifier = Modifier
            .padding(top = 16.dp)
            .testTag("back_to_product_list")
            .semantics { contentDescription = "상품목록으로 돌아가기" },
    ) {
        Text("상품목록으로 돌아가기")
    }
}

@Composable
private fun LocalWebViewScreen() {
    ScreenTitle("웹뷰 샘플", "로컬 HTML WebView 탐지용 화면입니다.")
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                contentDescription = "로컬 HTML 웹뷰"
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        return true
                    }
                }
                loadDataWithBaseURL(
                    "file:///android_asset/",
                    miniWebAppHtml,
                    "text/html",
                    "UTF-8",
                    null,
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .testTag("local_webview"),
    )
}

private val miniWebAppHtml = """
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
""".trimIndent()

@Composable
private fun SettingsScreen(onSettingClick: (SettingEntry) -> Unit) {
    ScreenTitle("설정", "설정 리스트와 각각의 상세 페이지 탐지용 화면입니다.")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        settings.forEach { setting ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSettingClick(setting) }
                    .testTag("setting_row_${setting.title}")
                    .semantics { contentDescription = "설정 항목 ${setting.title}" },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(setting.title, style = MaterialTheme.typography.titleMedium)
                    Text(setting.description)
                }
            }
        }
    }
}

@Composable
private fun SettingDetailScreen(setting: SettingEntry, onBack: () -> Unit) {
    ScreenTitle("${setting.title} 상세", "설정 항목에서 진입한 상세 화면입니다.")
    Text(setting.description)

    when (setting.title) {
        "알림 설정" -> NotificationSettingContent(setting.title)
        "화면 설정" -> DisplaySettingContent(setting.title)
        "개인정보 설정" -> PrivacySettingContent(setting.title)
        "저장공간 설정" -> StorageSettingContent(setting.title)
        else -> GenericSettingContent(setting.title)
    }

    Button(
        onClick = onBack,
        modifier = Modifier
            .testTag("back_to_settings")
            .semantics { contentDescription = "설정으로 돌아가기" },
    ) {
        Text("설정으로 돌아가기")
    }
}

@Composable
private fun NotificationSettingContent(title: String) {
    var enabled by remember(title) { mutableStateOf(true) }
    var selectedSound by remember(title) { mutableStateOf("기본 알림음") }
    val sounds = listOf("기본 알림음", "짧은 알림음", "진동만")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .semantics { contentDescription = "알림 설정 토글" },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("알림 받기")
        Switch(
            checked = enabled,
            onCheckedChange = { enabled = it },
            modifier = Modifier.testTag("notification_master_switch"),
        )
    }
    Text("알림음 선택")
    sounds.forEach { sound ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedSound = sound }
                .testTag("notification_sound_$sound")
                .semantics { contentDescription = "알림음 선택 $sound" }
                .padding(vertical = 6.dp),
        ) {
            RadioButton(selected = selectedSound == sound, onClick = { selectedSound = sound })
            Text(sound, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun DisplaySettingContent(title: String) {
    var brightness by remember(title) { mutableStateOf(0.65f) }
    var darkMode by remember(title) { mutableStateOf(false) }

    Text(
        text = "밝기: ${(brightness * 100).toInt()}%",
        modifier = Modifier
            .padding(top = 16.dp)
            .testTag("display_brightness_value")
            .semantics { contentDescription = "화면 밝기 ${(brightness * 100).toInt()} 퍼센트" },
    )
    Slider(
        value = brightness,
        onValueChange = { brightness = it },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("display_brightness_slider")
            .semantics { contentDescription = "화면 밝기 슬라이더" },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .semantics { contentDescription = "다크 모드 토글" },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("다크 모드")
        Switch(
            checked = darkMode,
            onCheckedChange = { darkMode = it },
            modifier = Modifier.testTag("display_dark_mode_switch"),
        )
    }
}

@Composable
private fun PrivacySettingContent(title: String) {
    var profileVisible by remember(title) { mutableStateOf(true) }
    var analyticsAllowed by remember(title) { mutableStateOf(false) }
    var locationAllowed by remember(title) { mutableStateOf(false) }

    PrivacyCheckboxRow("프로필 공개", profileVisible, { profileVisible = it }, "privacy_profile_checkbox")
    PrivacyCheckboxRow("사용 분석 허용", analyticsAllowed, { analyticsAllowed = it }, "privacy_analytics_checkbox")
    PrivacyCheckboxRow("위치 기반 추천 허용", locationAllowed, { locationAllowed = it }, "privacy_location_checkbox")
}

@Composable
private fun PrivacyCheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .testTag(tag)
            .semantics { contentDescription = "개인정보 옵션 $label" }
            .padding(vertical = 8.dp),
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun StorageSettingContent(title: String) {
    val rows = listOf(
        "이미지 캐시" to "128MB",
        "임시 파일" to "64MB",
        "오프라인 데이터" to "310MB",
    )

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .testTag("storage_usage_list")
            .semantics { contentDescription = "저장공간 사용량 목록" },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rows.forEach { (name, size) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(name)
                Text(size)
            }
        }
        Button(
            onClick = {},
            modifier = Modifier
                .testTag("storage_cache_clear_button")
                .semantics { contentDescription = "캐시 정리 실행" },
        ) {
            Text("캐시 정리")
        }
    }
}

@Composable
private fun GenericSettingContent(title: String) {
    var enabled by remember(title) { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("$title 사용")
        Switch(checked = enabled, onCheckedChange = { enabled = it })
    }
}
