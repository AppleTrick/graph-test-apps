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

public class ProductDetailFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_PRICE = "price";
    private static final String ARG_RATING = "rating";
    private static final String ARG_SHIPPING = "shipping";

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, product.name);
        args.putString(ARG_CATEGORY, product.category);
        args.putString(ARG_PRICE, product.price);
        args.putString(ARG_RATING, product.rating);
        args.putString(ARG_SHIPPING, product.shipping);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        Bundle args = requireArguments();
        String name = args.getString(ARG_NAME, "상품");
        String detail = "상품명: " + name
                + "\n분류: " + args.getString(ARG_CATEGORY, "")
                + "\n가격: " + args.getString(ARG_PRICE, "")
                + "\n" + args.getString(ARG_RATING, "")
                + "\n배송: " + args.getString(ARG_SHIPPING, "");

        TextView title = view.findViewById(R.id.product_detail_title);
        TextView description = view.findViewById(R.id.product_detail_description);
        Button back = view.findViewById(R.id.back_to_products_button);

        title.setText(name + " 상세");
        title.setContentDescription("화면 제목 " + name + " 상세");
        description.setText(detail);
        back.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showProducts();
            }
        });
        return view;
    }
}
