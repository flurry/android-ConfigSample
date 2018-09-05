/*
 * Copyright 2018, Oath Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flurry.configsample;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ContentsFragment extends Fragment {

    // Flurry Config instance
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    // Contents list view
    private ProductAdapter mProductAdapter;
    private RecyclerView mProductRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.contents, container, false);

        // Setup Flurry Config
        mFlurryConfig = FlurryConfig.getInstance();
        mFlurryConfigListener = new FlurryConfigListener() {
            @Override
            public void onFetchSuccess() {
                mFlurryConfig.activateConfig();
            }

            @Override
            public void onFetchNoChange() {
                // Use the Config cached data if available
            }

            @Override
            public void onFetchError(boolean isRetrying) {
                // Use the Config cached data if available
            }

            @Override
            public void onActivateComplete(boolean isCache) {
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);

        mProductAdapter = new ProductAdapter(getContext(), getProducts());
        mProductRecyclerView = (RecyclerView) rootView.findViewById(R.id.contents);
        mProductRecyclerView.setAdapter(mProductAdapter);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFlurryConfig.unregisterListener(mFlurryConfigListener);
    }

    private List<ProductItem> getProducts() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("MZNA", 991.04, 1000));
        list.add(new ProductItem("APLA", 148.88,  400));
        list.add(new ProductItem("ZV",    48.30, 3000));
        list.add(new ProductItem("ABAA",  58.40, 2000));
        list.add(new ProductItem("T",     38.97,  200));
        list.add(new ProductItem("EG",    25.65, 1000));
        list.add(new ProductItem("CDM",  155.31,  300));
        list.add(new ProductItem("SCOC",  31.51, 2000));
        list.add(new ProductItem("OOGG", 930.84,  800));
        list.add(new ProductItem("BF",   169.92, 1200));
        list.add(new ProductItem("RCLO",  49.96,  200));
        list.add(new ProductItem("BMI",  144.90,  100));
        list.add(new ProductItem("PQH",   19.21,  400));
        list.add(new ProductItem("SFTM",  72.90, 1600));
        list.add(new ProductItem("NTCI",  35.51, 1200));
        list.add(new ProductItem("BAYE",  35.78,  600));
        list.add(new ProductItem("BYB",   58.34,  100));
        list.add(new ProductItem("DBEA",  35.78,  200));
        list.add(new ProductItem("MDA",   13.61,  800));
        list.add(new ProductItem("WXT",  102.42,  600));

        return list;
    }

    class ProductItem {
        String product;
        double price;
        int quantity;
        double total;

        ProductItem(String product, double price, int quantity) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
            total = price * quantity;
        }
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        LayoutInflater inflater;
        private List<ProductItem> productItemList;

        public ProductAdapter(Context context, List<ProductItem> list) {
            inflater = LayoutInflater.from(context);
            productItemList = list;
        }

        public void updateList(List<ProductItem> list) {
            productItemList = list;
            notifyDataSetChanged();
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.contents_item, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            ProductItem entry = productItemList.get(position);
            holder.productTextView.setText(entry.product);
            holder.priceTextView.setText((new DecimalFormat("#,###.00")).format(entry.price));
            holder.quantityTextView.setText((new DecimalFormat("#####")).format(entry.quantity));
            holder.totalTextView.setText((new DecimalFormat("##,###.00")).format(entry.total));
        }

        @Override
        public int getItemCount() {
            return (productItemList == null) ? 0 : productItemList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            // Product item's member variables
            public TextView productTextView;
            public TextView priceTextView;
            public TextView quantityTextView;
            public TextView totalTextView;

            // Holder for the entire item row
            public ProductViewHolder(View itemView) {
                super(itemView);

                productTextView = (TextView) itemView.findViewById(R.id.product);
                priceTextView = (TextView) itemView.findViewById(R.id.price);
                quantityTextView = (TextView) itemView.findViewById(R.id.quantity);
                totalTextView = (TextView) itemView.findViewById(R.id.total);
            }
        }
    }

}