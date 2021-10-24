package com.example.easyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.easyexchange.databinding.ActivityMainBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // todo: How could I replace var with val ?
    private lateinit var binding: ActivityMainBinding
    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)

        /// SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.onSwipeRefreshed()
        }

        /// RecyclerView
        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.recyclerviewExchangeRates.adapter = adapter

        /// Spinner
        val sourceCurrencySpinner = binding.spinnerSourceCurrency
        val targetCurrencies = SharedPreferencesHelper().selectedTargetCurrencies
        // The spinner of source currencies uses the same list from target currencies.
        ArrayAdapter(this, android.R.layout.simple_spinner_item, targetCurrencies.toList())
            .also { spinnerAdapter ->
                // Specify the layout to use when the list of choices appears
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                sourceCurrencySpinner.adapter = spinnerAdapter
            }
        sourceCurrencySpinner.onItemSelectedListener = this

        /// Callback from ViewModel
        vm.exchangeRateDataList.observe(this) {
            adapter.update(it.map { v -> ExchangeRateItem(v.targetCurrency, v.exchangeRate) }
                .toMutableList())
        }

        vm.exchangeRateRetrieved.observe(this) {
            if (it)
                binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val selectedSourceCurrency = parent?.getItemAtPosition(position) as String

        vm.calculateExchangeRate()
        vm.getExchangeRates()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}