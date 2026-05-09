package com.example.graphpilottest.javaxml;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface Listener {
        void onProductClick(Product product);
    }

    private final Listener listener;
    private final List<Product> items = new ArrayList<>();

    public ProductAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Product> products) {
        items.clear();
        items.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = items.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView price;
        private final TextView rating;
        private final TextView shipping;
        private final Button detailButton;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            rating = itemView.findViewById(R.id.product_rating);
            shipping = itemView.findViewById(R.id.product_shipping);
            detailButton = itemView.findViewById(R.id.product_detail_button);
        }

        void bind(Product product, Listener listener) {
            name.setText(product.name);
            price.setText("가격: " + product.price);
            rating.setText(product.rating);
            shipping.setText("배송정보: " + product.shipping);
            itemView.setContentDescription("상품 카드 " + product.name);
            detailButton.setContentDescription(product.name + " 상세 보기");

            View.OnClickListener clickListener = v -> listener.onProductClick(product);
            itemView.setOnClickListener(clickListener);
            detailButton.setOnClickListener(clickListener);
        }
    }
}
