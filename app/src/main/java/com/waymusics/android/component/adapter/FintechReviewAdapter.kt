package com.waymusics.android.component.adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.waymusics.android.R
import com.waymusics.android.component.network.model.FintechReview


class FintechReviewAdapter(private val fintechReviews: MutableList<FintechReview>) : RecyclerView.Adapter<FintechReviewAdapter.AppViewHolder>() {

    var activity: Activity? = null


    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userReviewName : TextView = itemView.findViewById(R.id.user_review_name)
        val userReviewContent: TextView = itemView.findViewById(R.id.user_review_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        // Inflate layout
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val appListView = layoutInflater.inflate(R.layout.fintech_review_card_item, parent, false)
        return AppViewHolder(appListView)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.userReviewName.text = fintechReviews[position].fintechUserReviewName
        holder.userReviewContent.text = fintechReviews[position].fintechUserContentName


    }

    override fun getItemCount(): Int {
        return fintechReviews.size
    }











}