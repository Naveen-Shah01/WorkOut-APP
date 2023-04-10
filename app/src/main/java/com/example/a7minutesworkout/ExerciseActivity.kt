package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.ToneGenerator.MAX_VOLUME
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExcerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExcerciseBinding? = null

    /** for rest time*/
    // how much time we want to rest
    private var restTimer: CountDownTimer? = null

    // how far we have come
    private var restProgress = 0
    private var restTimerDuration: Long = 10

    /** for Exercise time*/
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 30

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    /** for the text to speech, and check text to speech app for more detail*/
    private var tts: TextToSpeech? = null

    /** for the beep sound for each restView */
    private var toneGenerator: ToneGenerator? = null


    /** object of adapter */
    private var exerciseStatusAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // helps to use our own action bar that is toolbar
        setSupportActionBar(binding?.toolbarExercise)

        /** Returns: The Activity's ActionBar, or null if it does not have one.**/
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            // will show dialog first instead of going back
            customDialogForBackButton()
            //onBackPressed()
        }


        exerciseList = Constants.defaultExerciseList()


        tts = TextToSpeech(this, this)
        //  binding?.flProgressBar?.visibility = View.INVISIBLE
        setupRestView()

        // only should be called after exerciseList is assigned
        setUpExerciseStatusRecyclerView()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this@ExerciseActivity)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    // when our phone back button pressed then we want to override this
    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun setUpExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList!!)

        binding?.rvExerciseStatus?.adapter = exerciseStatusAdapter
    }

    /** will call the Rest ProgressBar */
    private fun setupRestView() {
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, MAX_VOLUME)
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
        //  val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        //  toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        // opposite of setupExerciseView()
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        // text like upcoming label and exercise name will be visible in setupRestView but invisible in setupExerciseView
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        // if go back to main screen
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0;
        }
        // for the setup of text in restView to show the name of the upcoming exercise
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].name


        /** val exerciseNameToSpeak = exerciseList!![currentExercisePosition + 1].name
        will speak the rest time
        speakOut("Please rest for 10 seconds and the next exercise is $exerciseNameToSpeak")*/
        setRestProgressBar()

    }


    /** will call the Exercise ProgressBar*/
    private fun setupExerciseView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        // text like upcoming label and exercise name will be visible in setupRestView but invisible in setupExerciseView
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0;
        }
        // will speak the name of exercise
        speakOut(exerciseList!![currentExercisePosition].name)

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].image)
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].name
        setExerciseProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress


        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            // countDown interval will be of 1 second
            // after 1000 millisecond this onTick will be called
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            // after 10000 is over
            override fun onFinish() {

                Toast.makeText(this@ExerciseActivity, "Timer is finished", Toast.LENGTH_LONG).show()

                /** after Each Rest We will move to the next Exercise*/
                currentExercisePosition++

                // setting the status adapter to select the current status
                exerciseList!![currentExercisePosition].isSelected = true

                //notifying the adapter that data is changed
                exerciseStatusAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress


        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            // countDown interval will be of 1 second
            // after 1000 millisecond this onTick will be called
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            // after 10000 is over
            override fun onFinish() {

                // checking if not exceeding the exercise array
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    // setting the status adapter to set the current status to completed
                    exerciseList!![currentExercisePosition].isSelected = false // false
                    exerciseList!![currentExercisePosition].isCompleted = true // true
                    //notifying the adapter that data is changed
                    exerciseStatusAdapter!!.notifyDataSetChanged()
                    setupRestView()

                } else { // if exceeding we will stop

                    finish() // close this activity
                    val intentToJumpFinishActivity =
                        Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intentToJumpFinishActivity)
                }
            }
        }.start()
    }

    /** callback to the signal indicating the completion of the TextToSpeech engine initialization*/
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(java.util.Locale.US)
            // for the speech speed
            tts?.setSpeechRate(0.8.toFloat())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language Not supported", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Initialisation failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0;
        }

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0;
        }
        // text to speech
        if (tts != null) {
            tts?.stop()
            tts?.shutdown() //call the shutdown() method to release the native resources used by the TextToSpeech engine
        }

        // tone generator used for beep sound
        if (toneGenerator != null) {
            toneGenerator?.stopTone()
            toneGenerator?.release() //Releases resources associated with this ToneGenerator object.
        }

        binding = null
    }
}