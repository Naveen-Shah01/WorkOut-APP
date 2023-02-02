package com.example.a7minutesworkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNITS_VIEW" // metric unit view
        private const val US_UNITS_VIEW = "US_UNITS_VIEW" // US units view
    }

    /** A variable to hold a value to make a selected view visible */
    private var currentVisibleView: String = METRIC_UNITS_VIEW

    private var binding: ActivityBmiBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // helps to use our own action bar that is toolbar
        setSupportActionBar(binding?.toolbarBmiActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI" // will change the title of action bar
        }
        // this will lead us to the main screen as exercise activity is already finished
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        // When the activity is launched make METRIC UNITS VIEW visible./
        makeVisibleMetricUnitsView()

        // Adding a check change listener to the radio group and according to the radio button.
        // Radio Group change listener is set to the radio group which is added in XML.
        //we use _ for the first value because we don't need it
        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->

            // Here if the checkId is METRIC UNITS view then make the view visible else US UNITS view.
            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUsUnitsView()
            }
        }


        binding?.btnCalculateUnits?.setOnClickListener {
            calculateBMI()
        }

    }

    /** calculate BMI */
    private fun calculateBMI() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                //as our value will be in cm so converting to meter by dividing by 100

                val heightValue: Float =
                    binding?.etMetricUnitHeight?.text.toString().toFloat() / 100
                val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val bmi = weightValue / (heightValue * heightValue)
                displayBMIResults(bmi)
            } else {
                Toast.makeText(this, "Enter Both the values", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (validateUSUnits()) {
                val usUnitHeightValueFeet: String = binding?.etUSUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch: String = binding?.etUSUnitHeightInch?.text.toString()

                val usUnitWeightValue: Float = binding?.etUSUnitWeight?.text.toString().toFloat()
                val usHeightValue: Float =
                    usUnitHeightValueFeet.toFloat() * 12 + (usUnitHeightValueInch.toFloat())

                val bmi = 703 * (usUnitWeightValue / (usHeightValue * usHeightValue))
                displayBMIResults(bmi)
            } else {
                Toast.makeText(this, "Enter All the values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function is used to make the METRIC UNITS VIEW visible and hide the US UNITS VIEW.
     */
    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW // Current View is updated here.
        binding?.llMetricUnits?.visibility = View.VISIBLE // make METRIC system view Visible.
        binding?.llUSUnits?.visibility = View.GONE // make US system view Gone.

        binding?.etMetricUnitHeight?.text!!.clear() // height value of metric is cleared if it is added.
        binding?.etMetricUnitWeight?.text!!.clear() // weight value of metric is cleared if it is added.

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }


    /** Making a function to make the US UNITS view visible.*/
    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIEW // Current View is updated here.

        binding?.llMetricUnits?.visibility = View.GONE // make METRIC system view gone.
        binding?.llUSUnits?.visibility = View.VISIBLE // make US system view visible.


        binding?.etUSUnitWeight?.text!!.clear() // weight value is cleared.
        binding?.etUSUnitHeightFeet?.text!!.clear() // height feet value is cleared.
        binding?.etUSUnitHeightInch?.text!!.clear() // height inch is cleared.

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }


    private fun displayBMIResults(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are  in a very dangerous condition! Act now!"
        }

        //Use to set the result layout visible
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        // what does this line do?
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue // Value is set to TextView
        binding?.tvBMIType?.text = bmiLabel // Label is set to TextView
        binding?.tvBMIDescription?.text = bmiDescription // Description is set to TextView


    }


    private fun validateMetricUnits(): Boolean {
        var isValid = true
        if (binding?.etMetricUnitHeight?.text.toString().isEmpty() ||
            binding?.etMetricUnitWeight?.text.toString().isEmpty()
        ) {
            isValid = false
        }
        return isValid
    }

    private fun validateUSUnits(): Boolean {
        var isValid = true
        if (binding?.etUSUnitWeight?.text.toString().isEmpty() ||
            binding?.etUSUnitHeightFeet?.text.toString().isEmpty() ||
            binding?.etUSUnitHeightInch?.text.toString().isEmpty()
        ) {
            isValid = false
        }
        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}