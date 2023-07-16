package com.innovativetools.assignment.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("recent_links")
    val recentLinks: List<Link>,
    @SerializedName("top_links")
    val topLinks: List<Link>
)
