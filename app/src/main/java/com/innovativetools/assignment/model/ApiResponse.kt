package com.innovativetools.assignment.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("support_whatsapp_number")
    val supportWhatsappNumber: String,
    @SerializedName("total_links")
    val totalLinks: Int,
    @SerializedName("total_clicks")
    val totalClicks: Int,
    @SerializedName("today_clicks")
    val todayClicks: Int,
    @SerializedName("top_source")
    val topSource: String,
    @SerializedName("top_location")
    val topLocation: String,
    @SerializedName("data")
    val data: Data
)
