package com.pyotrpopov443.fourier

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        //get theme from shared preferences
        val darkMode = getPreferences(Context.MODE_PRIVATE).getBoolean("darkMode", true)
        setTheme(if (darkMode) R.style.DarkTheme else R.style.LightTheme)

        //on create
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        epicyclesNumberBar.max = fourier.getMaxEpicyclesNumber()
        epicyclesNumberBar.progress = fourier.getEpicyclesNumber()
        epicyclesNumberText.setText(epicyclesNumberBar.progress.toString())

        //watch if text changed
        epicyclesNumberText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                val max = fourier.getMaxEpicyclesNumber()
                var value: Int
                value = try {
                    epicyclesNumberText.text.toString().toInt()
                } catch (e: Exception) {
                    max
                }
                if (value > max) value = max
                epicyclesNumberText.setText(value.toString())
                epicyclesNumberBar.progress = value
                fourier.changeEpicyclesNumber(value)
            }
            false
        }

        //watch if seekBar changed
        epicyclesNumberBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                fourier.changeEpicyclesNumber(progress)
                epicyclesNumberText.setText(epicyclesNumberBar.progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                hideKeyboard()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //watch if shape changed
        fourier.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    fourier.touch(event)
                    epicyclesNumberBar.max = fourier.getMaxEpicyclesNumber()
                }
                else -> {
                    hideKeyboard()
                    fourier.touch(event)
                }
            }
            true
        }

        //set switch for drawer settings
        left_settings.menu.findItem(R.id.dark_theme).actionView = Switch(this)
        val switch = left_settings.menu.findItem(R.id.dark_theme).actionView as Switch
        switch.isChecked = darkMode
        left_settings.menu.findItem(R.id.dark_theme).actionView.setOnClickListener { setDarkMode(switch.isChecked) }

        //watch switch dark mode
        left_settings.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                //watch switch dark mode
                R.id.dark_theme -> {
                    switch.isChecked = !switch.isChecked
                    setDarkMode(switch.isChecked)
                }
                //change shape
                R.id.elephant -> {
                    setDemo(elephant, 50)
                }
            }
            true
        }
    }

    private fun setDemo(demo: ArrayList<IntArray>, epicyclesNumber: Int) {
        fourier.applyDemoPath(demo, epicyclesNumber)
        epicyclesNumberBar.max = fourier.getMaxEpicyclesNumber()
        epicyclesNumberBar.progress = epicyclesNumber
        epicyclesNumberText.setText(epicyclesNumberBar.progress.toString())
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        epicyclesNumberText.clearFocus()
    }

    private fun setDarkMode(darkMode: Boolean) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("darkMode", darkMode)
            commit()
        }
        finish()
        startActivity(intent)
    }

    private val elephant: ArrayList<IntArray> = arrayListOf(
        intArrayOf(-190,7), intArrayOf(-185,19), intArrayOf(-184,26), intArrayOf(-180,35),
        intArrayOf(-175,47), intArrayOf(-169,58), intArrayOf(-157,65), intArrayOf(-148,78),
        intArrayOf(-139,84), intArrayOf(-129,92), intArrayOf(-120,100), intArrayOf(-110,101),
        intArrayOf(-99,102), intArrayOf(-88,102), intArrayOf(-82,100), intArrayOf(-73,98),
        intArrayOf(-66,95), intArrayOf(-60,91), intArrayOf(-56,88), intArrayOf(-45,84),
        intArrayOf(-41,81), intArrayOf(-34,79), intArrayOf(-28,74), intArrayOf(-20,71),
        intArrayOf(-10,70), intArrayOf(0,70), intArrayOf(10,70), intArrayOf(20,70),
        intArrayOf(27,70), intArrayOf(38,68), intArrayOf(46,67), intArrayOf(55,66),
        intArrayOf(64,65), intArrayOf(75,65), intArrayOf(82,62), intArrayOf(91,63),
        intArrayOf(102,64), intArrayOf(111,64), intArrayOf(120,63), intArrayOf(126,60),
        intArrayOf(137,58), intArrayOf(143,56), intArrayOf(149,51), intArrayOf(156,48),
        intArrayOf(160,42), intArrayOf(166,40), intArrayOf(172,37), intArrayOf(179,32),
        intArrayOf(185,28), intArrayOf(191,23), intArrayOf(196,20), intArrayOf(202,16),
        intArrayOf(208,10), intArrayOf(213,6), intArrayOf(219,1), intArrayOf(221,-5),
        intArrayOf(224,-11), intArrayOf(229,-20), intArrayOf(230,-28), intArrayOf(232,-37),
        intArrayOf(233,-42), intArrayOf(236,-51), intArrayOf(236,-62), intArrayOf(236,-73),
        intArrayOf(234,-81), intArrayOf(232,-92), intArrayOf(232,-102), intArrayOf(231,-111),
        intArrayOf(229,-122), intArrayOf(229,-132), intArrayOf(229,-142), intArrayOf(231,-148),
        intArrayOf(232,-157), intArrayOf(235,-165), intArrayOf(234,-174), intArrayOf(227,-174),
        intArrayOf(223,-168), intArrayOf(223,-158), intArrayOf(222,-149), intArrayOf(219,-140),
        intArrayOf(220,-132), intArrayOf(220,-122), intArrayOf(220,-110), intArrayOf(220,-101),
        intArrayOf(220,-92), intArrayOf(220,-81), intArrayOf(219,-73), intArrayOf(217,-67),
        intArrayOf(211,-70), intArrayOf(211,-80), intArrayOf(212,-89), intArrayOf(214,-98),
        intArrayOf(215,-106), intArrayOf(213,-118), intArrayOf(210,-127), intArrayOf(208,-137),
        intArrayOf(211,-146), intArrayOf(213,-150), intArrayOf(215,-160), intArrayOf(218,-168),
        intArrayOf(219,-178), intArrayOf(219,-185), intArrayOf(219,-194), intArrayOf(219,-205),
        intArrayOf(219,-216), intArrayOf(217,-227), intArrayOf(204,-230), intArrayOf(194,-231),
        intArrayOf(185,-231), intArrayOf(176,-230), intArrayOf(167,-229), intArrayOf(159,-228),
        intArrayOf(152,-224), intArrayOf(147,-220), intArrayOf(149,-210), intArrayOf(158,-200),
        intArrayOf(164,-189), intArrayOf(163,-180), intArrayOf(160,-174), intArrayOf(157,-168),
        intArrayOf(155,-160), intArrayOf(153,-152), intArrayOf(148,-147), intArrayOf(144,-140),
        intArrayOf(138,-139), intArrayOf(138,-148), intArrayOf(139,-158), intArrayOf(139,-168),
        intArrayOf(139,-178), intArrayOf(138,-187), intArrayOf(137,-198), intArrayOf(126,-205),
        intArrayOf(118,-208), intArrayOf(109,-208), intArrayOf(98,-208), intArrayOf(90,-207),
        intArrayOf(81,-205), intArrayOf(74,-202), intArrayOf(66,-198), intArrayOf(66,-189),
        intArrayOf(77,-180), intArrayOf(84,-172), intArrayOf(84,-162), intArrayOf(81,-153),
        intArrayOf(80,-146), intArrayOf(80,-137), intArrayOf(77,-128), intArrayOf(71,-124),
        intArrayOf(64,-121), intArrayOf(57,-119), intArrayOf(52,-113), intArrayOf(44,-110),
        intArrayOf(38,-108), intArrayOf(33,-105), intArrayOf(22,-103), intArrayOf(18,-113),
        intArrayOf(17,-123), intArrayOf(19,-132), intArrayOf(21,-139), intArrayOf(25,-147),
        intArrayOf(26,-153), intArrayOf(26,-165), intArrayOf(26,-174), intArrayOf(28,-184),
        intArrayOf(29,-191), intArrayOf(31,-200), intArrayOf(35,-208), intArrayOf(34,-218),
        intArrayOf(26,-225), intArrayOf(18,-226), intArrayOf(7,-229), intArrayOf(-3,-232),
        intArrayOf(-12,-237), intArrayOf(-21,-237), intArrayOf(-26,-232), intArrayOf(-31,-227),
        intArrayOf(-34,-219), intArrayOf(-28,-209), intArrayOf(-23,-196), intArrayOf(-20,-188),
        intArrayOf(-22,-180), intArrayOf(-24,-175), intArrayOf(-30,-168), intArrayOf(-33,-160),
        intArrayOf(-35,-152), intArrayOf(-37,-145), intArrayOf(-38,-136), intArrayOf(-41,-136),
        intArrayOf(-44,-147), intArrayOf(-50,-155), intArrayOf(-55,-168), intArrayOf(-60,-178),
        intArrayOf(-65,-187), intArrayOf(-64,-195), intArrayOf(-63,-203), intArrayOf(-64,-215),
        intArrayOf(-77,-220), intArrayOf(-86,-220), intArrayOf(-95,-221), intArrayOf(-106,-221),
        intArrayOf(-116,-220), intArrayOf(-124,-219), intArrayOf(-130,-214), intArrayOf(-131,-208),
        intArrayOf(-125,-195), intArrayOf(-112,-185), intArrayOf(-109,-175), intArrayOf(-109,-168),
        intArrayOf(-104,-156), intArrayOf(-101,-145), intArrayOf(-98,-137), intArrayOf(-95,-127),
        intArrayOf(-94,-116), intArrayOf(-93,-105), intArrayOf(-91,-98), intArrayOf(-86,-87),
        intArrayOf(-83,-76), intArrayOf(-83,-68), intArrayOf(-84,-58), intArrayOf(-85,-49),
        intArrayOf(-86,-40), intArrayOf(-91,-37), intArrayOf(-94,-27), intArrayOf(-99,-23),
        intArrayOf(-104,-22), intArrayOf(-116,-29), intArrayOf(-125,-30), intArrayOf(-135,-36),
        intArrayOf(-142,-40), intArrayOf(-156,-44), intArrayOf(-162,-55), intArrayOf(-163,-64),
        intArrayOf(-166,-74), intArrayOf(-170,-84), intArrayOf(-172,-96), intArrayOf(-175,-104),
        intArrayOf(-178,-112), intArrayOf(-183,-125), intArrayOf(-186,-133), intArrayOf(-184,-142),
        intArrayOf(-181,-150), intArrayOf(-175,-149), intArrayOf(-176,-141), intArrayOf(-168,-137),
        intArrayOf(-160,-138), intArrayOf(-155,-140), intArrayOf(-149,-145), intArrayOf(-144,-150),
        intArrayOf(-157,-155), intArrayOf(-163,-165), intArrayOf(-173,-172), intArrayOf(-182,-173),
        intArrayOf(-190,-172), intArrayOf(-197,-168), intArrayOf(-205,-163), intArrayOf(-207,-158),
        intArrayOf(-210,-149), intArrayOf(-210,-140), intArrayOf(-209,-130), intArrayOf(-207,-121),
        intArrayOf(-205,-110), intArrayOf(-205,-101), intArrayOf(-206,-92), intArrayOf(-207,-82),
        intArrayOf(-207,-73), intArrayOf(-209,-64), intArrayOf(-207,-52), intArrayOf(-206,-44),
        intArrayOf(-204,-33), intArrayOf(-199,-23), intArrayOf(-194,-11), intArrayOf(-192,-4)
    )

}