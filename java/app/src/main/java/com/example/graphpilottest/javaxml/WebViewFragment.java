package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WebViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = view.findViewById(R.id.local_webview);
        webView.setContentDescription("로컬 HTML 웹뷰");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                miniWebAppHtml(),
                "text/html",
                "UTF-8",
                null
        );
        return view;
    }

    private String miniWebAppHtml() {
        return """
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
                """;
    }
}
