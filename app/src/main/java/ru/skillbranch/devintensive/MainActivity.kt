package ru.skillbranch.devintensive

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.models.Bender
import android.R.attr.bottom
import android.graphics.Rect
import android.util.DisplayMetrics
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.fragment.app.FragmentActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView
    lateinit var  benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //benderImage = findViewById(R.id.iv_bender) old
        benderImage=iv_bender
        textTxt = tv_text
        messageEt = et_message
        sendBtn = iv_send

        messageEt.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                sendMessage()
                hideSoftKeyboard()
                true
            } else {
                false
            }
        }

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name

        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        Log.d("M_MainActivity","onCreate $status $question")
        val (r,g,b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r,g,b), PorterDuff.Mode.MULTIPLY)

        textTxt.text = benderObj.askQuestion()
        sendBtn.setOnClickListener(this)


    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity","onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity","onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity","onDestroy")
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.iv_send){
           sendMessage()
        }
    }

    fun sendMessage(){
        val(phrase,color)= benderObj.listenAnswer(messageEt.text.toString().toLowerCase())
        messageEt.setText("")
        val (r,g,b) = color
        benderImage.setColorFilter(Color.rgb(r,g,b), PorterDuff.Mode.MULTIPLY)
        textTxt.text=phrase


    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)

        Log.d("M_MainActivity","onSaveInstanceState ${benderObj.status.name}")
        Log.d("M_MainActivity","onSaveInstanceState ${benderObj.question.name}")
    }

    fun Activity.hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun Activity.isKeyboardOpen(rootView: View): Boolean {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        val SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128

        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        val heightDiff = rootView.bottom - r.bottom
        /* Threshold size: dp to pixels, multiply with display density */
        val isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density

        Log.d("M_MainActivity",
            "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                    + "root view height:" + rootView.height + ", rect:" + r
        )

        return isKeyboardShown
    }

}

