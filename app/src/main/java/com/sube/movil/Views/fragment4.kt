package com.sube.movil.Views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import mehdi.sakout.fancybuttons.FancyButton
import com.brouding.blockbutton.BlockButton
import com.beardedhen.androidbootstrap.TypefaceProvider
import com.marcoscg.headerdialog.HeaderDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.brouding.simpledialog.SimpleDialog
import com.sube.movil.R

/**
 * Created by Marcelo on 22/10/2015.
 */
class fragment4 : Fragment() {
    lateinit var facebook: FancyButton
    lateinit var facetext: TextView
    lateinit var ubicacionTexto: TextView
    lateinit var about: BlockButton
    lateinit var leer: BlockButton
    private lateinit var provincia: FancyButton
    lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        TypefaceProvider.registerDefaultIconSets()
        val rootView = inflater.inflate(R.layout.fragment4, container, false)
        provincia = rootView.findViewById(R.id.provincia)
        ubicacionTexto = rootView.findViewById(R.id.miubicacion)
        about = rootView.findViewById(R.id.acerca_de)
        leer = rootView.findViewById(R.id.leer)
        val light = Typeface.createFromAsset(
            requireContext().assets, "fonts/light.ttf"
        )
        facetext = rootView.findViewById(R.id.facetext)
        facetext.setTypeface(light)
        facebook = rootView.findViewById(R.id.facebook)
        facebook.setOnClickListener(View.OnClickListener { v: View? -> openFacebookPage() })
        prefs = requireActivity().getSharedPreferences("ubicacion", Context.MODE_PRIVATE)
        val provincia_shared = prefs.getString("provincia", null)
        val ciudad_shared = prefs.getString("ciudad", null)
        ubicacionTexto.setText(setProvincia(provincia_shared, ciudad_shared))
        leer.setOnClickListener(View.OnClickListener { view: View? ->
            SimpleDialog.Builder(
                requireContext()
            )
                .setTitle("Sube Móvil")
                .setCustomView(R.layout.leer)
                .setBtnConfirmText("Entendido!")
                .setBtnConfirmTextSizeDp(16)
                .setBtnConfirmTextColor("#FF3872C9")
                .show()
        })
        about.setOnClickListener(View.OnClickListener {
            HeaderDialog(activity)
                .setColor(resources.getColor(R.color.azul)) // Sets the header background color
                .setElevation(false) // Sets the header elevation, true by default
                .setIcon(resources.getDrawable(R.drawable.ic_info_outline_white_36dp)) // Sets the dialog icon image
                .setTitle("Idea y desarrollo") // Sets the dialog title
                .setMessage("Marcelo Espinoza \n Formosa - Argentina") // Sets the dialog message
                //.justifyContent(true) // Justifies the message text, false by default
                .setTitleColor(Color.parseColor("#212121")) // Sets the header title text color
                .setIconColor(Color.parseColor("#212121")) // Sets the header icon color
                .setTitleGravity(Gravity.CENTER_HORIZONTAL) // Sets the header title text gravity
                .setMessageGravity(Gravity.CENTER_HORIZONTAL) // Sets the message text gravity
                .setTitleMultiline(true) // Multiline header title text option, true by default
                // .setView(R.layout.acerca)// Set custom view to the dialog (only possible via layout resource)
                .setPositiveButton("Listo") { dialog, which ->
                    // Your action
                }
                .show()
        })
        provincia.setOnClickListener(View.OnClickListener {
            MaterialDialog(requireContext()).show {
                title(text = "Provincia")
                positiveButton(text = "Seleccionar")

                listItems(R.array.provincia) { _, index, provincia ->
                        editor = prefs.edit()
                        when (provincia) {
                            "Buenos Aires" -> {
                                editor.putString("provincia", "BA")
                                cargarCiudades(R.array.buenos_aires)
                            }
                            "Capital Federal" -> {
                                editor.putString("provincia", "CF")
                                cargarCiudades(R.array.capital_federal)
                            }
                            "Corrientes" -> {
                                editor.putString("provincia", "CT")
                                editor.putString("ciudad", "")
                            }
                            "Chaco" -> {
                                editor.putString("provincia", "CH")
                                editor.putString("ciudad", "")
                            }
                            "Chubut" -> {
                                editor.putString("provincia", "CB")
                                editor.putString("ciudad", "")
                            }
                            "Corrientes" -> {
                                editor.putString("provincia", "CR")
                                editor.putString("ciudad", "")
                            }
                            "Entre Rios" -> {
                                editor.putString("provincia", "ER")
                                editor.putString("ciudad", "")
                            }
                            "Formosa" -> {
                                editor.putString("provincia", "FO")
                                editor.putString("ciudad", "")
                            }
                            "Jujuy" -> {
                                editor.putString("provincia", "JU")
                                editor.putString("ciudad", "")
                            }
                            "Mendoza" -> {
                                editor.putString("provincia", "MZ")
                                editor.putString("ciudad", "")
                            }
                            "Neuquen" -> {
                                editor.putString("provincia", "NQ")
                                editor.putString("ciudad", "")
                            }
                            "Rio Negro" -> {
                                editor.putString("provincia", "RN")
                                editor.putString("ciudad", "")
                            }
                            "San Juan" -> {
                                editor.putString("provincia", "SJ")
                                editor.putString("ciudad", "")
                            }
                            "San Luis" -> {
                                editor.putString("provincia", "SL")
                                editor.putString("ciudad", "")
                            }
                            "Santa Fe" -> {
                                editor.putString("provincia", "SF")
                                cargarCiudades(R.array.santa_fe)
                            }
                            "Tierra del Fuego" -> {
                                editor.putString("provincia", "TF")
                                editor.putString("ciudad", "")
                            }
                        }
                        editor.commit()
                        reiniciarApp()
                    }
                }
        })
        return rootView
    }

    private fun setProvincia(provincia_shared: String?, ciudad_shared: String?): String? {
        var provincia = provincia_shared
        when (provincia_shared) {
            "BA" -> provincia = "Buenos Aires"
            "CF" -> provincia = "Capital Federal"
            "CT" -> provincia = "Catamarca"
            "CH" -> provincia = "Chaco"
            "CB" -> provincia = "Chubut"
            "CR" -> provincia = "Corrientes"
            "ER" -> provincia = "Entre Rios"
            "FO" -> provincia = "Formosa"
            "JU" -> provincia = "Jujuy"
            "MZ" -> provincia = "Mendoza"
            "NQ" -> provincia = "Neuquen"
            "RN" -> provincia = "Rio Negro"
            "SJ" -> provincia = "San Juan"
            "SL" -> provincia = "San Luis"
            "SF" -> provincia = "Santa Fe"
        }
        return provincia
    }

    private fun reiniciarApp() {
        val i = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        System.exit(0)
    }

    private fun cargarCiudades(provincia: Int) {
        MaterialDialog(requireContext()).show {
            title(text = "Ciudad/Localidad")
            listItems(provincia) { _, _, text ->
                editor!!.putString("ciudad", text.toString())
                editor!!.commit()
                reiniciarApp()
            }
            negativeButton(text = "Cancelar") { dialog ->
                dialog.dismiss()
            }
        }
    }

    protected fun openFacebookPage() {
        val FACEBOOK_URL = "https://www.facebook.com/subemovil"
        val FACEBOOK_PAGE_ID = "subemovil"
        val facebookIntent = Intent(Intent.ACTION_VIEW)
        val packageManager = requireContext().packageManager
        try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                facebookIntent.data = Uri.parse("fb://facewebmodal/f?href=$FACEBOOK_URL")
                startActivity(facebookIntent)
            } else { //older versions of fb app
                facebookIntent.data = Uri.parse("fb://page/$FACEBOOK_PAGE_ID")
                startActivity(facebookIntent)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            facebookIntent.data = Uri.parse(FACEBOOK_URL)
            startActivity(facebookIntent) //normal web url
        }
    }
}