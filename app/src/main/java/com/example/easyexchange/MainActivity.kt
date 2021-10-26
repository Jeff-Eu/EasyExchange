package com.example.easyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private val currencyTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            vm.updatePropertiesFromMemory()
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

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
        // The spinner of source currencies uses the same list from target currencies.
        ArrayAdapter(this, android.R.layout.simple_spinner_item, vm.targetCurrencyList)
            .also { spinnerAdapter ->
                // Apply the adapter to the spinner
                sourceCurrencySpinner.adapter = spinnerAdapter
                // Specify the layout to use when the list of choices appears
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        sourceCurrencySpinner.onItemSelectedListener = this
        sourceCurrencySpinner.setSelection(vm.targetCurrencyList.indexOf(vm.sourceCurrency))

        /// EditText of currency value
        binding.editTextCurrency.addTextChangedListener(currencyTextWatcher)

        /// View observes changes from ViewModel's LiveData
        vm.exchangeRateDataList.observe(this) {
            if(it == null) return@observe

            adapter.update(it.map { v ->

                val sourceCurrencyValue = binding.editTextCurrency.text.toString().toDoubleOrNull()
                val targetCurrencyValue =
                    v.getTargetCurrencyValueText(sourceCurrencyValue, vm.findExchangeRateOfUSD(vm.sourceCurrency)!!)

                ExchangeItem(v.targetCurrency, targetCurrencyValue)
            }.toMutableList())
        }

        vm.exchangeRateRetrieved.observe(this) {
            if (it)
                binding.swipeRefreshLayout.isRefreshing = false
        }

        vm.timestamp.observe(this) {
            binding.textviewTimestamp.text = it.toString()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        vm.sourceCurrency = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}