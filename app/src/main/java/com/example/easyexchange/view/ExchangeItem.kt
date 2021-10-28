package com.example.easyexchange.view

import android.view.View
import com.example.easyexchange.R
import com.example.easyexchange.databinding.ConvertedCurrencyItemBinding
import com.xwray.groupie.viewbinding.BindableItem


class ExchangeItem(private val targetType: String, private val exchangeText: String) :
    BindableItem<ConvertedCurrencyItemBinding>() {

    override fun initializeViewBinding(view: View): ConvertedCurrencyItemBinding {
        return ConvertedCurrencyItemBinding.bind(view)
    }

    override fun getLayout() = R.layout.converted_currency_item

    override fun bind(viewBinding: ConvertedCurrencyItemBinding, position: Int) {
        viewBinding.currencyType.text = targetType
        viewBinding.currencyValue.text = exchangeText
    }
}