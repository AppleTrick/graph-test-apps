package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private enum TopLevelDestination {
        HOME,
        PRODUCTS,
        WEBVIEW,
        SETTINGS
    }

    private BottomNavigationView bottomNavigationView;
    private TopLevelDestination currentTopLevelDestination = TopLevelDestination.HOME;
    private boolean isDetailScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showHome();
                return true;
            }
            if (itemId == R.id.nav_products) {
                showProducts();
                return true;
            }
            if (itemId == R.id.nav_webview) {
                showWebView();
                return true;
            }
            if (itemId == R.id.nav_settings) {
                showSettings();
                return true;
            }
            return false;
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDetailScreen) {
                    closeDetailScreen();
                    return;
                }
                finish();
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void showHome() {
        currentTopLevelDestination = TopLevelDestination.HOME;
        isDetailScreen = false;
        setBottomNavigationVisible(true);
        replaceFragment(new HomeFragment(), "홈");
    }

    public void showProducts() {
        currentTopLevelDestination = TopLevelDestination.PRODUCTS;
        isDetailScreen = false;
        setBottomNavigationVisible(true);
        replaceFragment(new ProductListFragment(), "상품목록");
    }

    public void showWebView() {
        currentTopLevelDestination = TopLevelDestination.WEBVIEW;
        isDetailScreen = false;
        setBottomNavigationVisible(true);
        replaceFragment(new WebViewFragment(), "웹뷰");
    }

    public void showSettings() {
        currentTopLevelDestination = TopLevelDestination.SETTINGS;
        isDetailScreen = false;
        setBottomNavigationVisible(true);
        replaceFragment(new SettingsFragment(), "설정");
    }

    public void showProductDetail(@NonNull Product product) {
        currentTopLevelDestination = TopLevelDestination.PRODUCTS;
        isDetailScreen = true;
        setBottomNavigationVisible(false);
        replaceFragment(ProductDetailFragment.newInstance(product), product.name + " 상세");
    }

    public void showSettingDetail(@NonNull SettingEntry setting) {
        currentTopLevelDestination = TopLevelDestination.SETTINGS;
        isDetailScreen = true;
        setBottomNavigationVisible(false);
        replaceFragment(SettingDetailFragment.newInstance(setting), setting.title + " 상세");
    }

    private void setBottomNavigationVisible(boolean visible) {
        bottomNavigationView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void closeDetailScreen() {
        if (currentTopLevelDestination == TopLevelDestination.SETTINGS) {
            showSettings();
        } else {
            showProducts();
        }
    }

    private void replaceFragment(Fragment fragment, String label) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, label)
                .commit();
    }
}
