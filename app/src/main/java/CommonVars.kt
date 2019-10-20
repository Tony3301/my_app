import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moneyjet.R
import java.time.ZoneId
import java.util.*

//for listview of categories
var itemName: String = ""
var forContextMenu: Boolean = false

//this var is for switch
//if false - expense?
var switchVar: Boolean = false

//vars for date
@RequiresApi(Build.VERSION_CODES.O)
val localDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()!!
@RequiresApi(Build.VERSION_CODES.O)
val currentDate = ""+localDate.dayOfMonth+"/"+localDate.monthValue+"/"+localDate.year
var dateDB:String = ""

//check radiobutton
var rbutton_id = 0

//array of listView in HomeFragment
//for setting listView in dialog
//when touch slicevalue in Diagramm
val arrayForSlices:MutableList<String> = mutableListOf()
val arrayCategsExpenses:MutableList<String> = mutableListOf()

//val for count users balance
var balance = 0.0F
var incomeShare = ""
var expenseShare = ""

//for shared
val SETTINGS = "settings"
val FOR_SHARED = "PREFERENCE_BALANCE"
val LOCLANG = "PREFERENCE_LANG"
var languageVar = "en"

//colors
val colorSource = arrayOf(
    R.drawable.list_item_red,
    R.drawable.list_item_orange_red,
    R.drawable.list_item_orange,
    R.drawable.list_item_yellow,
    R.drawable.list_item_khaki,
    R.drawable.list_item_olive,
    R.drawable.list_item_green,
    R.drawable.list_item_blue,
    R.drawable.list_item_navy,
    R.drawable.list_item_indigo,
    R.drawable.list_item_magneta,
    R.drawable.list_item_pink
)

//dailyDiagramm
var dayliDiagramCheck: Boolean = false