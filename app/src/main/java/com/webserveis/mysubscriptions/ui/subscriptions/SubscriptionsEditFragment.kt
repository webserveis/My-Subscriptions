package com.webserveis.mysubscriptions.ui.subscriptions

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.webserveis.statelayout.StateLayout
import com.tiper.MaterialSpinner
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.*
import com.webserveis.mysubscriptions.models.SubscriptionStatusBill
import com.webserveis.mysubscriptions.ui.dialogs.BillPeriodPickerDialog
import com.webserveis.mysubscriptions.ui.dialogs.DatePickerFragment
import com.webserveis.mysubscriptions.ui.dialogs.DeleteDialog
import com.webserveis.mysubscriptions.usecases.SubscriptionState
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.activity_subscription_detail.*
import kotlinx.android.synthetic.main.fragment_subscription_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SubscriptionsEditFragment : Fragment(),
    DatePickerFragment.OnDatePickerListener,
    ValidatorFieldHelper.ValidatorFieldsListener,
    BillPeriodPickerDialog.OnBillPeriodPickerListener,
    DeleteDialog.OnDeleteDialogListener {

    companion object {
        private val TAG = SubscriptionsEditFragment::class.java.simpleName
        private const val DATE_PICKER = "date_picker"
        const val REQ_FIRST_PAYMENT = "req_first_payment"
        const val REQ_NEXT_PAYMENT = "req_next_payment"

        private const val PERIOD_BILL_PICKER = "period_bill_picker"
        private const val DELETE_DIALOG = "delete_dialog"

    }

    private var editMode = false
    private var itemUID: String? = null

    private val mViewModel: SubscriptionsViewModel by lazy {
        ViewModelProvider(this@SubscriptionsEditFragment).get(SubscriptionsViewModel::class.java)
    }

    private val dataItem by lazy { mViewModel.dataItem }
    private val myValidator by lazy { SubscriptionValidatorFields(requireContext()) }

    private lateinit var listCurrencies: List<Pair<String, String>>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemUID = it.getString(ARG_ITEM_ID)
        }
        editMode = !itemUID.isNullOrEmpty()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_edit, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //menu.clear()
        inflater.inflate(R.menu.subscription_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_delete).isVisible = itemUID != null
        super.onPrepareOptionsMenu(menu)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenuVisibility(false)

        (activity as AppCompatActivity).let {
            it.toolbar.setNavigationIcon(R.drawable.ic_close_24) //it.toolbar
            it.supportActionBar?.title = null
        }

        if (savedInstanceState == null) {
            if (editMode) {
                mViewModel.getSubscriptionById(itemUID)
            } else {
                mViewModel.fetchSubscriptionNew()
            }
        }

        setupInputFields()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                this.parentFragmentManager.popBackStack()
            }
            R.id.action_save -> {
                myValidator.checkName(dataItem.name.toString())
                myValidator.checkNextPayment(dataItem.nextPayment, dataItem.firstPayment)
                myValidator.validate()
            }
            R.id.action_delete -> {
                showDeleteDialog()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        myValidator.setOnValidateListener(this)
    }

    private fun setupObservers() {
        mViewModel.resultSate.observe(viewLifecycleOwner, {

            when (it) {
                is SubscriptionState.Success -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        refreshDataUI()
                    }
                    layout_state.content()
                    setMenuVisibility(true)
                }
                SubscriptionState.Loading -> {
                    layout_state.loading()
                }
                SubscriptionState.Empty -> {
                    showEmptyUI(getString(R.string.state_empty_view_title), getString(R.string.state_empty_view_summary))
                    itemUID = null

                }
                is SubscriptionState.Failure -> {
                }
            }
        })

    }

    private fun setupInputFields() {
        listCurrencies = Currency.getAvailableCurrencies()
            .filter { !it.displayName.contains("(") }
            .sortedBy { it.currencyCode }
            .map { Pair<String, String>(it.currencyCode, it.displayName) }

        val values = listCurrencies.map { it.first + " - " + it.second.capitalize(Locale.getDefault()) }

        val arrayAdapter = ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, values)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spnCurrency.apply {
            adapter = arrayAdapter
            onItemSelectedListener = object : MaterialSpinner.OnItemSelectedListener {
                override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                    Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
                    /*if (!mSpinnerInitialized) {
                        mSpinnerInitialized = true
                        return
                    }*/
                    dataItem.currencyCode = listCurrencies[position].first
                    //parent.focusSearch(View.FOCUS_UP)?.requestFocus()
                }

                override fun onNothingSelected(parent: MaterialSpinner) {
                    Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
                }
            }
            /*onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                Log.v("MaterialSpinner", "onFocusChange hasFocus=$hasFocus")
            }*/
        }

        edtPrice.filters = arrayOf<InputFilter>(InputFilterMinMax(0f, 9999999f))
        edtPrice.onChange { if (it.isNotEmpty()) dataItem.price = it.toFloat() }

        tlyName.markRequiredInRed()
        edtName.onChange { dataItem.name = it }
        edtName.onChangeDebounce {
            myValidator.checkName(it)
            myValidator.getAssertion(SubscriptionValidatorFields.FIELD_NAME)?.let { assertion ->
                tlyName.error = assertion.error
            }
        }
        edtDescription.onChange { dataItem.description = it }

        edtBillPeriod.keyListener = null
        edtBillPeriod.setOnClickListener {
            showDialogPeriodBill()
        }

        btnSelectColorBg.setOnClickListener {
            val colors = resources.getIntArray(R.array.materialColors)
            ColorSheet().colorPicker(
                colors = colors,
                selectedColor = dataItem.color,
                noColorOption = false,
                listener = { color ->
                    dataItem.color = color
                    it.focusSearch(View.FOCUS_DOWN)?.requestFocus()
                    refreshDataUI()
                })
                .show(requireActivity().supportFragmentManager)
        }


        edtFirstPayment.keyListener = null
        /*edtFirstPayment.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edtFirstPayment.performClick()
            }
        }*/

        edtFirstPayment.setOnClickListener {
            showDatePickerDialog(dataItem.firstPayment, REQ_FIRST_PAYMENT)
        }

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.sub_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnStatus.adapter = adapter

            spnStatus.onItemSelectedListener = object : MaterialSpinner.OnItemSelectedListener {
                override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                    dataItem.status = position
                    refreshUI()
                }

                override fun onNothingSelected(parent: MaterialSpinner) {

                }
            }

        }



        edtNextPayment.keyListener = null
        edtNextPayment.setOnClickListener {
            showDatePickerDialog(dataItem.nextPayment, REQ_NEXT_PAYMENT)
        }
        edtNextPayment.onChangeDebounce {
            Log.d(TAG, "onChangeDebounce: " + dataItem.nextPayment + " :" + dataItem.firstPayment)
            myValidator.checkNextPayment(dataItem.nextPayment, dataItem.firstPayment)
            myValidator.getAssertion(SubscriptionValidatorFields.FIELD_NEXT_PAYMENT)?.let { assertion ->
                tlyNextPayment.error = assertion.error
            }
        }

    }


    private fun refreshDataUI() {


        btnSelectColorBg.backgroundTintList = ColorStateList.valueOf(dataItem.color)

        //btnSelectColorBg.back(dataItem.color)

        if (dataItem.color.isDark()) {
            btnSelectColorBg.setTextColor(Color.WHITE)
        } else {
            btnSelectColorBg.setTextColor(Color.BLACK)
        }

        //Prefetch item data
        val c = Currency.getInstance(dataItem.currencyCode)
        val pos = listCurrencies.map { it.first }.indexOf(c.currencyCode)
        spnCurrency.selection = pos

        edtName.setText(dataItem.name)
        edtDescription.setText(dataItem.description)
        if (dataItem.price > 0F) edtPrice.setText(dataItem.price.toString())
        spnStatus.selection = dataItem.status

        val valuesList = resources.getStringArray(R.array.bill_period_units)
        edtBillPeriod.setText(dataItem.circleValue.toString())
        tlyBillPeriod.suffixText = valuesList[dataItem.circleUnits]

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        edtFirstPayment.setText(simpleDateFormat.format(dataItem.firstPayment))
        edtNextPayment.setText(simpleDateFormat.format(dataItem.nextPayment))
        edtPreviousPayment.setText(simpleDateFormat.format(dataItem.previousPayment))

    }

    private fun refreshUI() {

        when (dataItem.status) {
            SubscriptionStatusBill.AUTO_RENEW -> {
                tlyBillPeriod.visibility = View.VISIBLE
                tlyPreviousPayment.visibility = View.VISIBLE
                tlyNextPayment.hint = getString(R.string.sub_next_payment)
                edtNextPayment.parent
            }
            SubscriptionStatusBill.MANUAL_RENEW -> {
                tlyBillPeriod.visibility = View.VISIBLE
                tlyPreviousPayment.visibility = View.VISIBLE
                tlyNextPayment.hint = getString(R.string.sub_next_payment)
            }
            SubscriptionStatusBill.NOT_RENEW -> {
                tlyBillPeriod.visibility = View.VISIBLE
                tlyPreviousPayment.visibility = View.VISIBLE
                tlyNextPayment.hint = getString(R.string.sub_expire_date_in)
            }
            SubscriptionStatusBill.ONE_TIME -> {
                tlyBillPeriod.visibility = View.GONE
                tlyPreviousPayment.visibility = View.GONE
                tlyNextPayment.hint = getString(R.string.sub_expire_date_in)
            }

        }

    }

    private fun recalculationDates() {

        val cal = Calendar.getInstance()
        val dateNow = cal.time
        cal.timeInMillis = dataItem.firstPayment.time
        var nextPayment = cal.time

        while (nextPayment.before(dateNow) && !DateUtils.isToday(nextPayment.time)) {
            nextPayment = MySubsUtils.getNextPayment(nextPayment, dataItem.circleUnits, dataItem.circleValue)
        }

        if (nextPayment <= dataItem.firstPayment) {
            nextPayment = MySubsUtils.getNextPayment(nextPayment, dataItem.circleUnits, dataItem.circleValue)
        }

        //if (dataItem.firstPayment.before(dateNow)) {        }

        dataItem.nextPayment = nextPayment

        val previousPayment = MySubsUtils.getPreviousPayment(nextPayment, dataItem.circleUnits, dataItem.circleValue)
        dataItem.previousPayment = if (previousPayment.after(dataItem.firstPayment)) {
            previousPayment
        } else {
            dataItem.firstPayment
        }
    }

    private fun showDatePickerDialog(date: Date?, tag: String) {
        //Prevent open multiple calendar views
        if (requireActivity().supportFragmentManager.findFragmentByTag(DATE_PICKER) != null) return

        val datePickerFragment = DatePickerFragment().newInstance(date, tag)
        datePickerFragment?.setTargetFragment(this, 0)
        datePickerFragment?.show(
            requireActivity().supportFragmentManager,
            DATE_PICKER
        )
    }

    override fun onDateSelected(year: Int, month: Int, dayOfMonth: Int, tag: String?) {

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        when (tag) {
            REQ_FIRST_PAYMENT -> {
                dataItem.firstPayment = cal.time
                edtFirstPayment.setText(simpleDateFormat.format(dataItem.firstPayment))
                GlobalScope.launch(Dispatchers.Main) {
                    recalculationDates()
                    refreshDataUI()
                }
            }
            REQ_NEXT_PAYMENT -> {
                dataItem.nextPayment = cal.time
                edtNextPayment.setText(simpleDateFormat.format(dataItem.nextPayment))
            }
        }
    }

    private fun showDialogPeriodBill() {
        try {
            if (requireActivity().supportFragmentManager.findFragmentByTag(PERIOD_BILL_PICKER) != null) return
            val dialogFragment = BillPeriodPickerDialog().newInstance(dataItem.circleValue, dataItem.circleUnits)
            dialogFragment?.setTargetFragment(this, 0) //for auto listener
            dialogFragment?.show(requireActivity().supportFragmentManager, PERIOD_BILL_PICKER)
            dialogFragment?.listener
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPositiveClick(billPeriodValue: Int, billPeriodUnit: Int) {
        dataItem.circleValue = billPeriodValue
        dataItem.circleUnits = billPeriodUnit
        recalculationDates()
        refreshDataUI()
    }

    private fun showDeleteDialog() {
        if (requireActivity().supportFragmentManager.findFragmentByTag(DELETE_DIALOG) != null) return
        val modalDialog = DeleteDialog()
        modalDialog.setTargetFragment(this, 0)
        modalDialog.isCancelable = false
        modalDialog.show(requireActivity().supportFragmentManager, DELETE_DIALOG)
    }


    override fun onSuccessfulValidator() {
        context.toast("save")
        mViewModel.addSubscription(dataItem)
        requireActivity().onBackPressed()
    }

    override fun onErrorValidator(assertionList: HashMap<String, ValidatorFieldHelper.AssertionItem>) {
        assertionList[SubscriptionValidatorFields.FIELD_NAME]?.let {
            tlyName.error = it.error
        }
        assertionList[SubscriptionValidatorFields.FIELD_NEXT_PAYMENT]?.let {
            tlyNextPayment.error = it.error
        }
        requireContext().vibrate()
    }

    override fun onDeleteConfirm() {
        mViewModel.deleteSubscriptionById(dataItem.uid)
        requireActivity().onBackPressed()
    }

    private fun showEmptyUI(title: String, summary: String, @DrawableRes icon: Int? = null) {
        val view = layout_state.getView(StateLayout.STATE_EMPTY)
        view?.findViewById<TextView>(R.id.empty_title)?.text = title
        view?.findViewById<TextView>(R.id.empty_summary)?.text = summary
        if (icon != null) view?.findViewById<ImageView>(R.id.empty_icon)?.setImageResource(icon)

        layout_state.empty()

    }


}