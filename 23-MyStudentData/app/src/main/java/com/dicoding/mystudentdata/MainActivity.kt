package com.dicoding.mystudentdata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystudentdata.adapter.StudentAndUniversityAdapter
import com.dicoding.mystudentdata.adapter.StudentListAdapter
import com.dicoding.mystudentdata.adapter.StudentWithCourseAdapter
import com.dicoding.mystudentdata.adapter.UniversityAndStudentAdapter
import com.dicoding.mystudentdata.databinding.ActivityMainBinding
import com.dicoding.mystudentdata.helper.SortType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStudent.layoutManager = LinearLayoutManager(this)

        getStudent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_single_table -> {
                getStudent()
                return true
            }
            R.id.action_many_to_one -> {
                getStudentAndUniversity()
                true
            }
            R.id.action_one_to_many -> {
                getUniversityAndStudent()
                true
            }
            R.id.action_many_to_many -> {
                getStudentWithCourse()
                true
            }
            R.id.action_sort -> {
                showSortingPopupMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getStudent() {
        val adapter = StudentListAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudent().observe(this, {
            adapter.submitList(it)
        })
    }

    private fun getStudentAndUniversity() {
        val adapter = StudentAndUniversityAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudentAndUniversity().observe(this, {
            adapter.submitList(it)
            Log.d(TAG, "getStudentAndUniversity: $it")
        })
    }

    private fun getUniversityAndStudent() {
        val adapter = UniversityAndStudentAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllUniversityAndStudent().observe(this, {
            Log.d(TAG, "getUniversityAndStudent: $it")
            adapter.submitList(it)
        })
    }


    private fun getStudentWithCourse() {
        val adapter = StudentWithCourseAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudentWithCourse().observe(this, {
            Log.d(TAG, "getStudentWithCourse: $it")
            adapter.submitList(it)
        })
    }

    private fun showSortingOptionMenu(isShow: Boolean) {
        val view = findViewById<View>(R.id.action_sort) ?: return
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun showSortingPopupMenu() {
        val view = findViewById<View>(R.id.action_sort) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.sorting_menu, menu)
            setOnMenuItemClickListener {
                mainViewModel.changeSortType(
                    when (it.itemId) {
                        R.id.action_ascending -> SortType.ASCENDING
                        R.id.action_descending -> SortType.DESCENDING
                        else -> SortType.RANDOM
                    }
                )
                true
            }
            show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}