package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductListFragment extends Fragment {
    private final List<Product> products = Arrays.asList(
            new Product("노트북", "전자제품", "1,290,000원", "평점 4.8", "내일 도착", true),
            new Product("무선 이어폰", "전자제품", "129,000원", "평점 4.6", "오늘 도착", true),
            new Product("커피머신", "생활용품", "219,000원", "평점 4.5", "무료 배송", false),
            new Product("기계식 키보드", "전자제품", "89,000원", "평점 4.7", "모레 도착", false),
            new Product("사무용 의자", "생활용품", "159,000원", "평점 4.4", "화물 배송", true)
    );

    private ProductAdapter adapter;
    private TextView filterStatus;
    private String selectedFilter = "전체";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        filterStatus = view.findViewById(R.id.product_filter_status);

        adapter = new ProductAdapter(product -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showProductDetail(product);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.product_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        bindFilter(view.findViewById(R.id.filter_all), "전체");
        bindFilter(view.findViewById(R.id.filter_electronics), "전자제품");
        bindFilter(view.findViewById(R.id.filter_living), "생활용품");
        bindFilter(view.findViewById(R.id.filter_popular), "인기상품");
        Button reset = view.findViewById(R.id.filter_reset);
        reset.setOnClickListener(v -> applyFilter("전체"));

        applyFilter(selectedFilter);
        return view;
    }

    private void bindFilter(Button button, String filter) {
        button.setOnClickListener(v -> applyFilter(filter));
    }

    private void applyFilter(String filter) {
        selectedFilter = filter;
        filterStatus.setText("현재 필터: " + selectedFilter);
        filterStatus.setContentDescription("현재 상품 필터 " + selectedFilter);

        List<Product> filtered = new ArrayList<>();
        for (Product product : products) {
            if ("전체".equals(selectedFilter)
                    || product.category.equals(selectedFilter)
                    || ("인기상품".equals(selectedFilter) && product.popular)) {
                filtered.add(product);
            }
        }
        adapter.submitList(filtered);
    }
}
