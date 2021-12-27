package com.waymusics.android.component.network.model
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FintechReview(
    @SerializedName("user_review_name") val fintechUserReviewName: String?,
    @SerializedName("user_review_content") val fintechUserContentName: String?
)
