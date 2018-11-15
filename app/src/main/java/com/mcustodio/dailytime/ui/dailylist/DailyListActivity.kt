package com.mcustodio.dailytime.ui.dailylist

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.mcustodio.dailytime.FirebaseDB
import com.mcustodio.dailytime.R
import com.mcustodio.dailytime.data.Daily
import com.mcustodio.dailytime.ui.DbMockViewModel
import com.mcustodio.dailytime.ui.daily.DailyActivity
import kotlinx.android.synthetic.main.activity_dailylist.*

class DailyListActivity : AppCompatActivity() {

    private val adapter by lazy { DailyListRecyclerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dailylist)

        recycler_dailylist.layoutManager = LinearLayoutManager(this)
        recycler_dailylist.adapter = adapter

        DbMockViewModel.dailies.observe(this, Observer {
            adapter.dailyList = it ?: listOf()
        })

        adapter.onItemClick = {
            openDailyPage(it)
        }

        fab_dailylist.setOnClickListener {
            val newDaily = Daily.create(DbMockViewModel.selectedTeam.value)
            val newReference = FirebaseDB.dailies.ref.push()
            val newKey = newReference.key
            newReference.setValue(newDaily)
                .addOnSuccessListener {
                    newDaily.id = newKey
                    openDailyPage(newDaily)
                }
                .addOnFailureListener { Toast.makeText(this, it.message, Toast.LENGTH_LONG).show() }
        }
    }

    fun openDailyPage(selectedDaily: Daily) {
        DbMockViewModel.selectedDaily.value = selectedDaily
        val intent = Intent(this, DailyActivity::class.java)
        startActivity(intent)
    }
}
