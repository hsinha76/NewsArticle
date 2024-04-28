package com.hsdroid.newsarticle.ui.view.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hsdroid.newsarticle.R
import com.hsdroid.newsarticle.data.models.ServerResponse
import com.hsdroid.newsarticle.databinding.ActivityMainBinding
import com.hsdroid.newsarticle.ui.view.adapter.ArticleAdapter
import com.hsdroid.newsarticle.utils.Helper.Companion.BASE_URL
import com.hsdroid.newsarticle.utils.Helper.Companion.isPostNotificationPermissionGranted
import com.hsdroid.newsarticle.utils.Helper.Companion.setViewsVisibility
import com.hsdroid.newsarticle.utils.Helper.Companion.sortList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val articleAdapter by lazy {
        ArticleAdapter(this@MainActivity)
    }
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Toast.makeText(
                    this@MainActivity,
                    "Notification permission granted!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Can show a compulsory view here to enable permissions to continue further
                Toast.makeText(
                    this@MainActivity,
                    "Notification permission denied!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        // Check permission for notifications.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isPostNotificationPermissionGranted(this@MainActivity)) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val keys = resources.getStringArray(R.array.sort_by)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, keys)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        binding.recyclerView.adapter = articleAdapter

        //Network call to fetch JSON
        fetchNews()

        binding.imgRefresh.setOnClickListener {
            fetchNews()
        }
    }

    private fun fetchNews() {

        with(binding) {
            setViewsVisibility(View.GONE, errorView, recyclerView, viewSort)
            setViewsVisibility(View.VISIBLE, progressCircular)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = URL(BASE_URL).readText() //Fetch JSON and store it to variable
                val json: ServerResponse = Gson().fromJson(
                    response,
                    object : TypeToken<ServerResponse>() {}.type
                ) //Parse JSON

                withContext(Dispatchers.Main) {
                    with(binding) {
                        setViewsVisibility(View.GONE, progressCircular, errorView)
                        setViewsVisibility(View.VISIBLE, recyclerView, viewSort)
                    }
                }

                //Sort the list
                binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    val sortByKey = when (selectedItem) {
                        "Old to New" -> sortList("old-to-new")
                        "New to Old" -> sortList("new-to-old")
                        else -> throw IllegalArgumentException("Invalid selection")
                    }
                    articleAdapter.setList(json.articles.sortedWith(sortByKey))
                }

                val defaultSortByKey = sortList("new-to-old")
                articleAdapter.setList(json.articles.sortedWith(defaultSortByKey))

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    with(binding) {
                        setViewsVisibility(View.GONE, progressCircular, recyclerView, viewSort)
                        setViewsVisibility(View.VISIBLE, errorView)
                    }
                }
            }
        }
    }
}