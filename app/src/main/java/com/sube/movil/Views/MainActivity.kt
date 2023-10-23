package com.sube.movil.Views

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.afollestad.materialdialogs.MaterialDialog
import com.special.ResideMenu.ResideMenu
import com.special.ResideMenu.ResideMenuItem
import com.sube.movil.ListInterface
import com.sube.movil.R
import hotchemi.android.rate.AppRate
import org.json.JSONArray

class MainActivity : FragmentActivity(), View.OnClickListener, ListInterface {
    private var misube: ResideMenuItem? = null
    private var saldo: ResideMenuItem? = null
    private var fragment2: fragment2? = null
    private var lm: LocationManager? = null
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        context = applicationContext
        AppRate.with(this) //Llamo ventana para calificar aplicacion
            .setInstallDays(2) // default 10, 0 means install day.
            .setLaunchTimes(9) // default 10
            .setRemindInterval(1) // default 1
            .setShowLaterButton(true) // default true
            .setDebug(false) // default false
            .setOnClickButtonListener { which ->
                // callback listener.
                Log.d(MainActivity::class.java.name, which.toString())
            }
            .monitor()

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this)
        setUpMenu()
        fragment2 = fragment2()
        if (savedInstanceState == null) changeFragment(MapFragment())
        checkLocation()
    }

    private fun setUpMenu() {

        // attach to current activity;
        Companion.resideMenu = ResideMenu(this)
        Companion.resideMenu!!.setBackground(R.drawable.fondomain)
        Companion.resideMenu!!.attachToActivity(this)
        Companion.resideMenu!!.menuListener = menuListener
        Companion.resideMenu!!.setScaleValue(0.6f)

        // create menu items;
        mapa = ResideMenuItem(this, R.drawable.puntosrecarga, "Mapa")
        misube = ResideMenuItem(this, R.drawable.horarios, "Mi Sube")
        val contacto = ResideMenuItem(this, R.drawable.contacto, "Contacto")
        val compartir = ResideMenuItem(this, R.drawable.compartir, "Compartí")
        saldo = ResideMenuItem(this, R.drawable.horarios, "Mi saldo")
        mapa!!.setOnClickListener(this)
        misube!!.setOnClickListener(this)
        saldo!!.setOnClickListener(this)
        contacto.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "marceloespinoza00@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sube Móvil")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        })
        compartir.setOnClickListener(View.OnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.sube.movil"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        })
        Companion.resideMenu!!.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT)
        Companion.resideMenu!!.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT)
        Companion.resideMenu!!.addMenuItem(misube, ResideMenu.DIRECTION_LEFT)
        Companion.resideMenu!!.addMenuItem(mapa, ResideMenu.DIRECTION_LEFT)
        Companion.resideMenu!!.addMenuItem(contacto, ResideMenu.DIRECTION_LEFT)
        Companion.resideMenu!!.addMenuItem(compartir, ResideMenu.DIRECTION_RIGHT)
        Companion.resideMenu!!.addMenuItem(saldo, ResideMenu.DIRECTION_RIGHT)
        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        findViewById<View>(R.id.title_bar_left_menu).setOnClickListener(
            View.OnClickListener { Companion.resideMenu!!.openMenu(ResideMenu.DIRECTION_LEFT) })
        findViewById<View>(R.id.title_bar_right_menu).setOnClickListener(
            View.OnClickListener { Companion.resideMenu!!.openMenu(ResideMenu.DIRECTION_RIGHT) })
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return Companion.resideMenu!!.dispatchTouchEvent(ev)
    }

    override fun onClick(view: View) {
        if (view === mapa) {
            changeFragment(MapFragment())
        } else if (view === saldo) {
            changeFragment(SaldoFragment())
        } else if (view === misube) {
            changeFragment(fragment4())
        }
        Companion.resideMenu!!.closeMenu()
    }

    private val menuListener: ResideMenu.OnMenuListener = object : ResideMenu.OnMenuListener {
        //llama cuando el menu esta abierto
        override fun openMenu() {}

        //llama cuando el menu esta cerrado
        override fun closeMenu() {}
    }

    private fun changeFragment(targetFragment: Fragment) {
        if (targetFragment is fragment2) {
            fragment2?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, it, "fragment2")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    private fun checkLocation() {
        var network_enabled: Boolean? = null
        lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        network_enabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!network_enabled) {
           /* Builder(this).title("Advertencia")
                .content("Para visualizar los puntos de venta debe permitir el acceso a su ubicacion mediante Wifi y Redes Moviles")
                .negativeText("Cancelar")
                .neutralText("Activar")
                .onNeutral(object : SingleButtonCallback() {
                    fun onClick(dialog: MaterialDialog, which: DialogAction) {
                        val viewIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(viewIntent)
                    }
                }).show()*/
            MaterialDialog(this).show {
                title(text = "Advertencia")
                message (text = "Para visualizar los puntos de venta debe permitir el acceso a su ubicacion mediante Wifi y Redes Moviles")

                positiveButton(text = "Aceptar") { dialog ->
                            val viewIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(viewIntent)
                }

                negativeButton(text = "Cancelar") { dialog ->
                    dialog.dismiss()
                }
            }
        }
    }

    // What good method is to access resideMenu？
    val resideMenu: ResideMenu?
        get() = Companion.resideMenu

    override fun onCreateList(list: JSONArray) {}

    companion object {
        private var resideMenu: ResideMenu? = null
        private var mapa: ResideMenuItem? = null
        var context: Context? = null
            private set
    }
}