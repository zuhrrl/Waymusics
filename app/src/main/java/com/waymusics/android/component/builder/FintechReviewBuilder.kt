package com.waymusics.android.component.builder
import com.waymusics.android.component.network.model.FintechReview

data class FintechReviewBuilder(var fintechReviewName: String? = null,
                                var fintechReviewContent: String? = null) {

   fun setFintechUserReviewName(value: String?): FintechReviewBuilder {
       this.fintechReviewName = value
       return this
   }

    fun setFintechUserReviewContent(value: String?): FintechReviewBuilder {
        this.fintechReviewContent = value
        return this
    }


    fun build(): FintechReview {
        return FintechReview(fintechReviewName, fintechReviewContent)
    }
}