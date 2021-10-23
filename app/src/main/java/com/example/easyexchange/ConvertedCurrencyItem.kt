package com.example.easyexchange

import android.view.View
import com.example.easyexchange.databinding.ConvertedCurrencyItemBinding
import com.xwray.groupie.viewbinding.BindableItem


class ConvertedCurrencyItem(private val value: String) :
    BindableItem<ConvertedCurrencyItemBinding>() {

    override fun initializeViewBinding(view: View): ConvertedCurrencyItemBinding {
        return ConvertedCurrencyItemBinding.bind(view)
    }

    override fun getLayout() = R.layout.converted_currency_item

    override fun bind(viewBinding: ConvertedCurrencyItemBinding, position: Int) {
        viewBinding.currencyValue.text = value
    }
}