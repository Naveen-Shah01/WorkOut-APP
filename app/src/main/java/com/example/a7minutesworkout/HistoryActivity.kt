package com.example.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityBmiBinding
import com.example.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    private var binding: ActivityHistoryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // helps to use our own action bar that is toolbar
        setSupportActionBar(binding?.toolbarHistory)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History" // will change the title of action bar
        }
        // this will lead us to the main screen as exercise activity is already finished
        binding?.toolbarHistory?.setNavigationOnClickListener {
            onBackPressed()
        }

        val dao = (application as WorkOutApp).db.historyDao()
        getAllCompleteDates(dao)


    }




     private fun getAllCompleteDates(historyDao: HistoryDao){
         lifecycleScope.launch {
             historyDao.fetchAllDates().collect{  allCompleteDatesList ->

                if(allCompleteDatesList.isNotEmpty()){

                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoRecordsAvailable?.visibility = View.INVISIBLE

                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    val dates = ArrayList<String>()
                    for(date in allCompleteDatesList){
                        dates.add(date.date)
                    }
                    val historyAdapter = HistoryItemAdapter(dates)
                    binding?.rvHistory?.adapter= historyAdapter

                }
                 else {
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
                }

             }

         }
     }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}