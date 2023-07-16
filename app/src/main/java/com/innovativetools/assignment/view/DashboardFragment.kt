package com.innovativetools.assignment.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import com.innovativetools.assignment.R
import com.innovativetools.assignment.adapter.LinksAdapter
import com.innovativetools.assignment.databinding.FragmentDahsboardBinding
import com.innovativetools.assignment.model.Link
import com.innovativetools.assignment.model.RequestBody
import com.innovativetools.assignment.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var chart: LineChart
    private lateinit var selectedStartDate: Calendar
    private lateinit var selectedEndDate: Calendar
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: FragmentDahsboardBinding
    private lateinit var viewModel: DashboardViewModel
    private var recentLinksData: List<Link>? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_dahsboard,container,false)

        chart  = binding.chart
        tabLayout = binding.tabLayout

        val sharedPref = context?.getSharedPreferences("KeyAccessToken", Context.MODE_PRIVATE)
        val accessToken = sharedPref?.getString("accessToken","")

        val sharedViewModel: DashboardViewModel by activityViewModels()
        viewModel = sharedViewModel

        if (accessToken != null) { viewModel.initialize(accessToken) }

        val requestBody = RequestBody("NewEndPointAdded")
        lifecycleScope.launch { val response = viewModel.postData("newEndpoin", requestBody, accessToken!!) }


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) { binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        viewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.tvUserName.text = userName
        }

        viewModel.greeting.observe(viewLifecycleOwner) { greeting ->
            binding.tvGreeting.text = greeting
        }

        viewModel.totalClicks.observe(viewLifecycleOwner) { totalClicks ->
            binding.tvTotalClicksCount.text = totalClicks
        }

        viewModel.todayClicks.observe(viewLifecycleOwner) { todayClicks ->
            binding.tvTodayClicksCount.text = todayClicks
        }

        viewModel.totalLinks.observe(viewLifecycleOwner) { totalLinks ->
            binding.tvTotalLinksCount.text = totalLinks
        }

        viewModel.topLocation.observe(viewLifecycleOwner) { topLocation ->
            binding.tvTopLocName.text = topLocation
        }

        viewModel.topSource.observe(viewLifecycleOwner) { topSource ->
            binding.tvTopSourceName.text = topSource
        }


        setUpTabs()
        setUpRecentLinkChart()
        setUpDate()

        return binding.root
    }

    private fun setUpTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Top Links"))
        tabLayout.addTab(tabLayout.newTab().setText("Recent Links"))
        val selectedTabDrawable = resources.getDrawable(R.drawable.fill_btn_round_30)
        tabLayout.setSelectedTabIndicator(null)
        val defaultTab = tabLayout.getTabAt(0)
        defaultTab?.select()
        defaultTab?.view?.background = selectedTabDrawable
        displayTopLinks()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab)
                {
                tab.view.background = selectedTabDrawable
                val position = tab.position
                if (position == 0) {
                    displayTopLinks()
                } else if (position == 1) {
                    displayRecentLinks()
                }
                }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.background = null
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun displayTopLinks() {
        viewModel.topLinks.observe(viewLifecycleOwner) { toplinks ->
            val linksAdapter = LinksAdapter(toplinks)
            binding.listViewLinks.adapter = linksAdapter

        }
    }

    private fun displayRecentLinks() {
        viewModel.recentLinks.observe(viewLifecycleOwner) { recentlinks ->
            val linksAdapter = LinksAdapter(recentlinks)
            binding.listViewLinks.adapter = linksAdapter
            recentLinksData
            setUpRecentLinkChart()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpDate() {

        binding.dateTextView.setOnClickListener {
            showDatePickerDialog()
        }
        val calendar = Calendar.getInstance()
        selectedStartDate = Calendar.getInstance().apply {
            set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
        }
        selectedEndDate = Calendar.getInstance().apply {
            set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            )
        }
        updateDateRangeText()
        updateChart(selectedStartDate, selectedEndDate)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerDialog() {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    selectedStartDate.apply {
                        set(year, month, 1)
                    }
                    selectedEndDate.apply {
                        set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                    updateDateRangeText()
                    updateChart(selectedStartDate, selectedEndDate)
                },
                selectedStartDate.get(Calendar.YEAR),
                selectedStartDate.get(Calendar.MONTH),
                selectedStartDate.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog?.show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateRangeText() {
        val startDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedStartDate.time)
        val endDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedEndDate.time)
        val selectedRange = "$startDateString - $endDateString"
        binding.dateTextView.text = selectedRange
    }


    private fun updateChart(startDate: Calendar, endDate: Calendar) {

        viewModel.chartDataEntries.observe(viewLifecycleOwner) { chartdataentries ->
            val dataSet = LineDataSet(chartdataentries, " ")
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2f
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()

            viewModel.chartLabels.observe(viewLifecycleOwner) { chartlabels ->
                val xAxis = chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = IndexAxisValueFormatter(chartlabels)

                val yAxis = chart.axisLeft
                yAxis.axisMinimum = 0f
                yAxis.axisMaximum = 50f
                yAxis.setLabelCount(5, true)
                yAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return (value.toInt() * 25).toString()
                    }
                }

                //filter dates
                val filteredDataEntries = chartdataentries.filterIndexed { index, _ ->
                    val date = chartlabels.getOrNull(index)?.let { parseChartLabelToDate(it) }
                    date?.let { it.after(startDate) && it.before(endDate) } ?: false
                }

                val filteredLineData = LineData(LineDataSet(filteredDataEntries, " "))
                chart.data = filteredLineData
                chart.notifyDataSetChanged()

                val chartWidth = chart.width.toFloat()

                val xAxisMinValue = 0f
                val xAxisMaxValue = filteredDataEntries.size.toFloat() - 1

                val yAxisMinValue = 0f
                val yAxisMaxValue = 100f

                xAxis.axisMinimum = xAxisMinValue
                xAxis.axisMaximum = xAxisMaxValue

                yAxis.axisMinimum = yAxisMinValue
                yAxis.axisMaximum = yAxisMaxValue

                yAxis.setDrawGridLines(false)
                val visibleRange = chartWidth / filteredDataEntries.size
                xAxis.setLabelCount((chartWidth / visibleRange).toInt(), true)

                chart.description.isEnabled = false
                chart.legend.isEnabled = false
                chart.setDrawMarkers(true)
                chart.fitScreen()

                chart.setTouchEnabled(false)
                chart.isDragEnabled = false
                chart.setScaleEnabled(true)
                chart.setPinchZoom(false)

                chart.isScaleXEnabled = false
                chart.isScaleYEnabled = false
                chart.isDragEnabled = false

                chart.animateY(1000, Easing.EaseInOutCubic)
            }
        }
    }

    private fun parseChartLabelToDate(chartLabel: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val date = sdf.parse(chartLabel)
            Calendar.getInstance().apply {
                time = date
            }
        } catch (e: ParseException) {
            null
        }
    }

    private fun setUpRecentLinkChart() {

        var numDataPoints: Long = 0

        viewModel.chartDataEntries.observe(viewLifecycleOwner) { chartdataentries ->
            val dataSet = LineDataSet(chartdataentries, " ")
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2f
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
            numDataPoints = chartdataentries.size.toLong()
            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }

        viewModel.chartLabels.observe(viewLifecycleOwner) { chartlabels ->
            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setLabelCount(5, true)
            xAxis.valueFormatter = IndexAxisValueFormatter(chartlabels)


            val rightYAxis = chart.axisRight
            rightYAxis.isEnabled = false


            val yAxis = chart.axisLeft
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = recentLinksData!![0].totalClicks.toFloat()
            yAxis.setLabelCount(5, true)
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return (value.toInt() * 25).toString()
                }
            }
            val chartWidth = resources.displayMetrics.widthPixels.toFloat()
            val xAxisMinValue = 0f
            val xAxisMaxValue = numDataPoints - 1f
            val yAxisMinValue = 0f
            val yAxisMaxValue = 100f

            xAxis.axisMinimum = xAxisMinValue
            xAxis.axisMaximum = xAxisMaxValue
            yAxis.axisMinimum = yAxisMinValue
            yAxis.axisMaximum = yAxisMaxValue
            val visibleRange = chartWidth / numDataPoints
            xAxis.setLabelCount((chartWidth / visibleRange).toInt(), true)


            yAxis.setDrawGridLines(false)

            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.setDrawMarkers(false)
            chart.fitScreen()
            chart.setTouchEnabled(false)
            chart.isDragEnabled = false
            chart.setScaleEnabled(false)
            chart.setPinchZoom(false)
            chart.isScaleXEnabled = false
            chart.isScaleYEnabled = false
            chart.isDragEnabled = false

            chart.animateY(1000, Easing.EaseInOutCubic)
        }
    }

}






