package com.example.moneyjet

import FOR_SHARED
import LOCLANG
import SETTINGS
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import balance
import com.example.moneyjet.room.categs.CategoriesDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import expenseShare
import incomeShare
import languageVar
import java.util.*



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var navController:NavController
    private lateinit var db: CategoriesDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        //сначала сеттим язык! чтобы при загрузке bottom_nav_view
        //был на соответствующем языке. это работает!=)
        setLang()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = CategoriesDatabase.getAppDataBase(this)!!

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.bar_settings -> {
                navController.navigate(R.id.settingsActivity)
            }
            R.id.bar_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    if(languageVar == "en")
                    putExtra(Intent.EXTRA_TEXT, "" +
                            "_____(0_o)______\n" +
                            "My Daily Statistic\n" +
                            "┏━━━━━∪∪━━━━━━━━━━━━┓\n" +
                            " Total expenses: $expenseShare\n" +
                            " Total income: $incomeShare\n\t" +
                            "┗━━━━━━━━━━━━━━━━━━━┛"+
                            "\t\t<MoneyJet®>")
                    if(languageVar == "ru")
                        putExtra(Intent.EXTRA_TEXT, "" +
                                "_____(0_o)______\n" +
                                "Моя дневная статистика\n" +
                                "┏━━━━━∪∪━━━━━━━━━━━━┓\n" +
                                " Все расходы: $expenseShare\n" +
                                " Вся прибыль: $incomeShare\n\t" +
                                "┗━━━━━━━━━━━━━━━━━━━┛"+
                                "\t\t<MoneyJet®>")
                    type = "text/plain"
                }
                startActivity(sendIntent)
            }
            else -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreference =  getSharedPreferences(SETTINGS,
            Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putFloat(FOR_SHARED, balance)
        editor.putString(LOCLANG, languageVar)
        editor.apply()
    }

    override fun onRestart() {
        super.onRestart()
        setLang()
        //выглядит так себе, но костыль работает
        //TODO тааааааак!!! Раньше это выглядело стремно, а теперь уже все,
        // будто быстрее прогружается. Странненько
        recreate()
    }

    @SuppressLint("ResourceType")
    fun setLang(){
        val sharedPreference =  getSharedPreferences(SETTINGS,
            Context.MODE_PRIVATE)
        languageVar = sharedPreference.getString(LOCLANG, "ru").toString()
        val locale = Locale(languageVar)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, null)

        if(languageVar == "en")
            title = "Balance: ${sharedPreference.getFloat(FOR_SHARED, 0.0F)} Br"
        if(languageVar == "ru")
            title = "Баланс: ${sharedPreference.getFloat(FOR_SHARED, 0.0F)} Br"
        balance = sharedPreference.getFloat(FOR_SHARED, 0.0F)

    }
}
