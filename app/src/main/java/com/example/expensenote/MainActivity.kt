package com.example.expensenote   // แก้ให้ตรงกับของโปรเจกต์

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MainActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var btnAdd: Button
    private lateinit var btnClear: Button
    private lateinit var tvLastItem: TextView
    private lateinit var tvTotal: TextView
    private lateinit var pieChart: PieChart

    // ยอดรวมตามหมวด
    private var total = 0f
    private var foodTotal = 0f
    private var travelTotal = 0f
    private var shoppingTotal = 0f
    private var otherTotal = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etAmount = findViewById(R.id.etAmount)
        spCategory = findViewById(R.id.spCategory)
        btnAdd = findViewById(R.id.btnAdd)
        btnClear = findViewById(R.id.btnClear)
        tvLastItem = findViewById(R.id.tvLastItem)
        tvTotal = findViewById(R.id.tvTotal)
        pieChart = findViewById(R.id.pieChart)

        setupCategorySpinner()
        setupDonutStyle()

        btnAdd.setOnClickListener { addExpense() }
        btnClear.setOnClickListener { clearAll() }
    }

    private fun setupCategorySpinner() {
        val categories = listOf("อาหาร", "เดินทาง", "ช้อปปิ้ง", "อื่น ๆ")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter
    }

    private fun setupDonutStyle() {
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 60f
        pieChart.transparentCircleRadius = 65f
        pieChart.setHoleColor(Color.parseColor("#FAFAFA"))

        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = true

        pieChart.centerText = "ยังไม่มีข้อมูล"
    }

    private fun addExpense() {
        val amountText = etAmount.text.toString().trim()
        val categoryText = spCategory.selectedItem.toString()

        if (amountText.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกจำนวนเงิน", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toFloatOrNull()
        if (amount == null) {
            Toast.makeText(this, "กรุณากรอกจำนวนเงินเป็นตัวเลข", Toast.LENGTH_SHORT).show()
            return
        }

        // ยอดรวมทั้งหมด
        total += amount

        // ใช้ตำแหน่ง Spinner กันชื่อเพี้ยน
        when (spCategory.selectedItemPosition) {
            0 -> foodTotal += amount        // อาหาร
            1 -> travelTotal += amount      // เดินทาง
            2 -> shoppingTotal += amount    // ช้อปปิ้ง
            3 -> otherTotal += amount       // อื่น ๆ
        }

        tvLastItem.text = "ล่าสุด: [$categoryText] %.2f บาท".format(amount)
        tvTotal.text = "ยอดรวมวันนี้: %.2f บาท".format(total)

        updateChart()

        etAmount.text.clear()
    }

    private fun updateChart() {
        val entries = ArrayList<PieEntry>()

        if (foodTotal > 0) entries.add(PieEntry(foodTotal, "อาหาร"))
        if (travelTotal > 0) entries.add(PieEntry(travelTotal, "เดินทาง"))
        if (shoppingTotal > 0) entries.add(PieEntry(shoppingTotal, "ช้อปปิ้ง"))
        if (otherTotal > 0) entries.add(PieEntry(otherTotal, "อื่น ๆ"))

        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = "ยังไม่มีข้อมูล"
            return
        } else {
            pieChart.centerText = ""
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#81C784"),
            Color.parseColor("#64B5F6"),
            Color.parseColor("#FFB74D"),
            Color.parseColor("#E57373")
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun clearAll() {
        total = 0f
        foodTotal = 0f
        travelTotal = 0f
        shoppingTotal = 0f
        otherTotal = 0f

        etAmount.text.clear()
        tvLastItem.text = "รายการล่าสุด: -"
        tvTotal.text = "ยอดรวมวันนี้: 0.00 บาท"

        pieChart.clear()
        pieChart.centerText = "ยังไม่มีข้อมูล"
    }
}