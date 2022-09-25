package com.himanshu.android.view.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.himanshu.android.MerchantQuery
import com.himanshu.android.R
import com.himanshu.android.databinding.ItemProductSectionBinding
import com.himanshu.android.utils.OnProductClickListener

class ProductsDiffUtil: DiffUtil.ItemCallback<MerchantQuery.Product_section>() {
    override fun areItemsTheSame(
        oldItem: MerchantQuery.Product_section,
        newItem: MerchantQuery.Product_section
    ): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(
        oldItem: MerchantQuery.Product_section,
        newItem: MerchantQuery.Product_section
    ): Boolean {
        return oldItem == newItem
    }
}

class ProductsSectionAdapter(private val listenerProduct: OnProductClickListener): ListAdapter<MerchantQuery.Product_section, ProductsSectionViewHolder>(ProductsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsSectionViewHolder {
        val binding: ItemProductSectionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_product_section,
            parent,
            false
        )
        return ProductsSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsSectionViewHolder, position: Int) {
        holder.binding.productSection = getItem(position)
        val productAdapter = ProductAdapter(getItem(position).products, listenerProduct)
        holder.binding.productSectionRv.adapter = productAdapter
    }
}

class ProductsSectionViewHolder(val binding: ItemProductSectionBinding): RecyclerView.ViewHolder(binding.root)
