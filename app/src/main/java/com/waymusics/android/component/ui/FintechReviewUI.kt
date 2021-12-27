package com.waymusics.android.component.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.waymusics.android.R
import com.waymusics.android.component.adapter.FintechReviewAdapter
import com.waymusics.android.component.module.ModuleHelper
import com.waymusics.android.component.network.model.FintechReview
import com.waymusics.android.component.network.retro.Retro
import com.waymusics.android.component.network.retro.RetroInterface
import kotlinx.android.synthetic.main.fragment_fintech_review.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FintechReview.newInstance] factory method to
 * create an instance of this fragment.
 */
class FintechReview : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = FintechReview::class.java.simpleName
    val listFintechReview = mutableListOf<FintechReview>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModuleHelper.isReviewFintech = true
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fintech_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ModuleHelper.btnOpenCloseDrawer?.setImageResource(R.drawable.baseline_arrow_back_white_24)
        // init review recyclerview
        initFintechReviewAdapter(requireContext())
    }

    private fun initFintechReviewAdapter(context: Context) {



        // adapter
        val fintechReviewAdapter = FintechReviewAdapter(listFintechReview)
        val packname = "api/get/reviews/" + ModuleHelper.reviewPackname
        val retro = Retro().getRetrofitInstance().create(RetroInterface::class.java)


        review_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fintechReviewAdapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FintechReview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FintechReview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}