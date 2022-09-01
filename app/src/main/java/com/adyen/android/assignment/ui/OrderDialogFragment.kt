package com.adyen.android.assignment.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adyen.android.assignment.R
import com.adyen.android.assignment.component
import com.adyen.android.assignment.databinding.FragmentOrderDialogBinding
import com.adyen.android.assignment.provider.Order
import com.adyen.android.assignment.provider.OrderProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderDialogFragment : DialogFragment(R.layout.fragment_order_dialog) {

    @Inject
    lateinit var orderProvider: OrderProvider

    private var _binding: FragmentOrderDialogBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderDialogBinding.bind(view)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.reset.setOnClickListener {
            orderProvider.updateOrder(Order.NONE)
            dismiss()
        }

        binding.apply.setOnClickListener {
            val radioButtonId = binding.orderGroup.checkedRadioButtonId

            orderProvider.updateOrder(
                when(radioButtonId) {
                    R.id.orderByTitle -> Order.ORDER_BY_TITLE
                    R.id.orderByDate -> Order.ORDER_BY_DATE
                    else -> Order.NONE
                }
            )
            dismiss()
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderProvider.orderFlow.collect {
                    with(binding) {
                        when (it) {
                            Order.ORDER_BY_DATE -> orderByDate.isChecked = true
                            Order.ORDER_BY_TITLE -> orderByTitle.isChecked = true
                            else -> {
                                orderByDate.isChecked = false
                                orderByTitle.isChecked = false
                            }
                        }
                    }
                }
            }
        }
    }
}