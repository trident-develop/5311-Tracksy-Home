package com.bailing.lark.roll.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.YearMonth

private const val PREFS_NAME = "home_bills_prefs"
private const val KEY_BILLS = "bills_v1"
private const val KEY_RECORDS = "records_v1"

class AppRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadBillsOrDefault(): List<UtilityBill> {
        val saved = loadBills()
        val source = saved ?: defaultBillsForFirstRun()
        val rebased = rolloverPaidStatuses(source)
        if (saved == null || rebased != source) {
            saveBills(rebased)
        }
        return rebased
    }

    fun loadRecordsOrDefault(): List<UtilityRecord> =
        loadRecords() ?: MockRecords.initial()

    private fun defaultBillsForFirstRun(): List<UtilityBill> {
        val ym = currentYearMonthKey()
        return MockData.bills.map { bill ->
            if (bill.status == BillStatus.Paid) bill.copy(paidForMonth = ym) else bill
        }
    }

    private fun rolloverPaidStatuses(bills: List<UtilityBill>): List<UtilityBill> {
        val ym = currentYearMonthKey()
        return bills.map { bill ->
            if (bill.status == BillStatus.Paid && bill.paidForMonth != ym) {
                bill.copy(
                    status = BillStatus.DueSoon,
                    paidForMonth = null,
                    dueDate = "Due this month"
                )
            } else bill
        }
    }

    fun saveBills(bills: List<UtilityBill>) {
        prefs.edit().putString(KEY_BILLS, encodeBills(bills)).apply()
    }

    fun saveRecords(records: List<UtilityRecord>) {
        prefs.edit().putString(KEY_RECORDS, encodeRecords(records)).apply()
    }

    private fun loadBills(): List<UtilityBill>? {
        val raw = prefs.getString(KEY_BILLS, null) ?: return null
        return runCatching { decodeBills(raw) }.getOrNull()
    }

    private fun loadRecords(): List<UtilityRecord>? {
        val raw = prefs.getString(KEY_RECORDS, null) ?: return null
        return runCatching { decodeRecords(raw) }.getOrNull()
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun get(context: Context): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(context).also { instance = it }
            }
    }
}

private fun encodeBills(bills: List<UtilityBill>): String {
    val arr = JSONArray()
    for (b in bills) {
        val o = JSONObject()
        o.put("id", b.id)
        o.put("kind", b.kind.name)
        o.put("amount", b.amount)
        o.put("dueDate", b.dueDate)
        o.put("status", b.status.name)
        o.put("usageNote", b.usageNote)
        o.put("provider", b.provider)
        if (b.paidForMonth != null) o.put("paidForMonth", b.paidForMonth)
        arr.put(o)
    }
    return arr.toString()
}

private fun decodeBills(raw: String): List<UtilityBill> {
    val arr = JSONArray(raw)
    val out = ArrayList<UtilityBill>(arr.length())
    for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        out += UtilityBill(
            id = o.getInt("id"),
            kind = UtilityKind.valueOf(o.getString("kind")),
            amount = o.getString("amount"),
            dueDate = o.getString("dueDate"),
            status = BillStatus.valueOf(o.getString("status")),
            usageNote = o.getString("usageNote"),
            provider = o.getString("provider"),
            paidForMonth = if (o.has("paidForMonth") && !o.isNull("paidForMonth"))
                o.getString("paidForMonth") else null
        )
    }
    return out
}

internal fun currentYearMonthKey(): String = YearMonth.now().toString()

private fun encodeRecords(records: List<UtilityRecord>): String {
    val arr = JSONArray()
    for (r in records) {
        val o = JSONObject()
        o.put("kind", r.kind.name)
        o.put("providerName", r.providerName)
        o.put("tariff", r.tariff)
        o.put("discountPercent", r.discountPercent)
        val readingsArr = JSONArray()
        for (reading in r.readings) {
            val ro = JSONObject()
            ro.put("id", reading.id)
            ro.put("year", reading.year)
            ro.put("month", reading.month)
            ro.put("value", reading.value)
            readingsArr.put(ro)
        }
        o.put("readings", readingsArr)
        arr.put(o)
    }
    return arr.toString()
}

private fun decodeRecords(raw: String): List<UtilityRecord> {
    val arr = JSONArray(raw)
    val out = ArrayList<UtilityRecord>(arr.length())
    for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val readingsArr = o.getJSONArray("readings")
        val readings = ArrayList<MeterReading>(readingsArr.length())
        for (j in 0 until readingsArr.length()) {
            val ro = readingsArr.getJSONObject(j)
            readings += MeterReading(
                id = ro.getLong("id"),
                year = ro.getInt("year"),
                month = ro.getInt("month"),
                value = ro.getDouble("value")
            )
        }
        out += UtilityRecord(
            kind = UtilityKind.valueOf(o.getString("kind")),
            providerName = o.getString("providerName"),
            tariff = o.getDouble("tariff"),
            readings = readings,
            discountPercent = if (o.has("discountPercent")) o.getDouble("discountPercent") else 0.0
        )
    }
    return out
}
