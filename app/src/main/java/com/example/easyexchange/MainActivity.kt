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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vm = ViewModelProvider(this).get(MainViewModel::class.java)

        // SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.onSwipeRefreshed()
        }

        // RecyclerView
        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.recyclerviewConverted.adapter = adapter

        adapter.add(ConvertedCurrencyItem("100 yen"))
        adapter.add(ConvertedCurrencyItem("1782 dollar"))
        adapter.add(ConvertedCurrencyItem("3021 pond"))
        adapter.add(ConvertedCurrencyItem("70 dollar"))
        adapter.add(ConvertedCurrencyItem("1782 dollar"))
        adapter.add(ConvertedCurrencyItem("3021 pond"))
        adapter.add(ConvertedCurrencyItem("70 dollar"))
        adapter.add(ConvertedCurrencyItem("1782 dollar"))
        adapter.add(ConvertedCurrencyItem("3021 pond"))
        adapter.add(ConvertedCurrencyItem("70 dollar"))
        adapter.add(ConvertedCurrencyItem("3021 pond"))
        adapter.add(ConvertedCurrencyItem("70 dollar"))
        adapter.add(ConvertedCurrencyItem("1782 dollar"))
        adapter.add(ConvertedCurrencyItem("3021 pond"))
        adapter.add(ConvertedCurrencyItem("70 dollar"))
//        vm.convertedCurrencyList.observe(this) {
//            adapter.update(it.map { v -> ConvertedCurrencyItem(v.chtmessage) }.toMutableList())
//        }

        // Spinner
        val spinner = binding.spinnerSelectedCurrency

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.target_currencies,
            android.R.layout.simple_spinner_item
        ).also { spinnerAdapter ->
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = spinnerAdapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}