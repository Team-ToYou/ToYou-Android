package com.toyou.toyouandroid.data
//
//import android.content.Context
//import com.toyou.toyouandroid.R
//import com.toyou.toyouandroid.model.HomeOptionResult
//
//class HomeOptionResultDataSource @Inject constructor(
//    private val context: Context
//) {
//
//    fun getHappyData(): HomeOptionResult {
//        return HomeOptionResult(
//            sentence = context.getString(R.string.home_stamp_result_happy)
//        )
//    }
//
//    fun getAnxietyData(): HomeOptionResult {
//        return HomeOptionResult(
//            sentence = context.getString(R.string.home_stamp_result_anxiety)
//        )
//    }
//
//    fun getExcitingData(): HomeOptionResult {
//        return HomeOptionResult(
//            sentence = context.getString(R.string.home_stamp_result_exciting)
//        )
//    }
//
//    fun getNormalData(): HomeOptionResult {
//        return HomeOptionResult(
//            sentence = context.getString(R.string.home_stamp_result_normal)
//        )
//    }
//
//    fun getUpsetData(): HomeOptionResult {
//        return HomeOptionResult(
//            sentence = context.getString(R.string.home_stamp_result_upset)
//        )
//    }
//}