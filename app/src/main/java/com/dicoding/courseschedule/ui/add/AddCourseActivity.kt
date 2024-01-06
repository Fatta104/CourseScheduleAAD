package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.Event
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var addCourseViewModel: AddCourseViewModel
    private lateinit var courseName: TextInputEditText
    private lateinit var courseLecturer: TextInputEditText
    private lateinit var courseNote: TextInputEditText
    private lateinit var ibStartTime: ImageButton
    private lateinit var ibEndTime: ImageButton
    private lateinit var courseStartTime: TextView
    private lateinit var courseEndTime: TextView
    private lateinit var spinnerDay: Spinner
    private var daySelected = 0
    private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_course)

        init()

        val factory = AddCourseViewModelFactory.createFactory(this)
        addCourseViewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]

        addCourseViewModel.saved.observe(this) { event ->
            if (event != null) {
                event.getContentIfNotHandled()?.let { result ->
                    if (result) {
                        Toast.makeText(
                            this@AddCourseActivity,
                            "1 Course Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(
                            this@AddCourseActivity,
                            "Failed to add new course !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        setNewCourse()


    }

    private fun init() {
        courseName = findViewById(R.id.ed_course_name)
        courseLecturer = findViewById(R.id.ed_lecturer)
        courseNote = findViewById(R.id.ed_note)
        courseStartTime = findViewById(R.id.tv_start_time)
        courseEndTime = findViewById(R.id.tv_end_time)
        ibStartTime = findViewById(R.id.ib_start_time)
        ibEndTime = findViewById(R.id.ib_end_time)
        spinnerDay = findViewById(R.id.spinner_day)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_insert -> {
                AlertDialog.Builder(this).apply {
                    setMessage("Are you sure add new course?")
                    setNegativeButton(getString(R.string.no), null)
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        if (insertOneCourse()) {
                            finish()
                        }
                    }
                    show()
                }
            }

            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setNewCourse() {

        val timePickerFragment = TimePickerFragment()
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.day,
            android.R.layout.simple_spinner_item,
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerDay.adapter = adapter

        ibStartTime.setOnClickListener {
            timePickerFragment.show(supportFragmentManager, START_TIME)
        }

        ibEndTime.setOnClickListener {
            timePickerFragment.show(supportFragmentManager, END_TIME)
        }


        spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long,
            ) {
                daySelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    private fun insertOneCourse(): Boolean {
        return if (
            courseName.text.toString().isEmpty() ||
//            courseLecturer.text.toString().isEmpty() ||
//            courseNote.text.toString().isEmpty() ||
            (courseStartTime.text.toString() == getString(R.string.dummy_time)) ||
            (courseEndTime.text.toString() == getString(R.string.dummy_time))
        ) {
            Toast.makeText(this, getString(R.string.input_empty_message), Toast.LENGTH_SHORT).show()
            false
        } else {
            addCourseViewModel.insertCourse(
                courseName = courseName.text.toString(),
                lecturer = courseLecturer.text.toString(),
                note = courseNote.text.toString(),
                startTime = courseStartTime.text.toString(),
                endTime = courseEndTime.text.toString(),
                day = daySelected
            )
            true
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            START_TIME -> courseStartTime.text = dateFormat.format(calendar.time)
            END_TIME -> courseEndTime.text = dateFormat.format(calendar.time)
            else -> {}
        }
    }

    companion object {
        private const val START_TIME = "start_time"
        private const val END_TIME = "end_time"
    }
}