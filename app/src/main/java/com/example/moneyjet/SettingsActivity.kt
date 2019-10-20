package com.example.moneyjet

import FOR_SHARED
import LOCLANG
import SETTINGS
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import balance
import kotlinx.android.synthetic.main.activity_settings.*
import languageVar
import java.util.*

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var etBalanc: EditText

    //попытка освоить SettingsActivity.
    //пол третьего ночи, пока сделаю по старому
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        when(languageVar){
            "en" -> title = "Settings"
            "ru" -> title = "Настройки"
        }

        etBalanc = findViewById(R.id.etSetBalance)

        radioGroup = findViewById(R.id.radSetGroup)
        radioGroup.setOnCheckedChangeListener{_, isChecked ->
            when(isChecked){
                R.id.radSetEn -> languageVar = "en"
                R.id.radSetRu -> languageVar = "ru"
            }
        }

        btnSetOk.setOnClickListener {
            val sharedPreference =  getSharedPreferences(SETTINGS,
                 Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(LOCLANG, languageVar)


            if(etBalanc.text.toString() != "")
                {
                    balance = etBalanc.text.toString().toFloat()
                    editor.putFloat(FOR_SHARED, balance)
                }
            Log.d("BAL_",balance.toString())
            editor.apply()

            val locale = Locale(languageVar)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            baseContext.resources.updateConfiguration(configuration, null)
            onBackPressed()
        }
    }
}
