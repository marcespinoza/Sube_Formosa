package com.sube.movil;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.brouding.blockbutton.BlockButton;
import com.brouding.simpledialog.SimpleDialog;
import com.marcoscg.headerdialog.HeaderDialog;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcelo on 22/10/2015.
 */
public class fragment4 extends Fragment {

   FancyButton facebook;
   TextView facetext, ubicacionTexto;
   BlockButton about;
   BlockButton leer;
   FancyButton provincia;
   SharedPreferences prefs;
   SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View rootView = inflater.inflate(R.layout.fragment4, container, false);
        provincia = rootView.findViewById(R.id.provincia);
        ubicacionTexto =  rootView.findViewById(R.id.miubicacion);
        about =  rootView.findViewById(R.id.acerca_de);
        leer =  rootView.findViewById(R.id.leer);
        Typeface light = Typeface.createFromAsset(getContext().getAssets(), "fonts/light.ttf");
        facetext = rootView.findViewById(R.id.facetext);
        facetext.setTypeface(light);
        facebook =  rootView.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookPage();
            }
        });
        prefs = getActivity().getSharedPreferences("ubicacion", MODE_PRIVATE);
        final String provincia_shared = prefs.getString("provincia", null);
        final String ciudad_shared = prefs.getString("ciudad", null);
        ubicacionTexto.setText(provincia_shared+"\n"+ciudad_shared);
        leer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleDialog.Builder(getContext())
                        .setTitle("Sube Móvil")
                        .setCustomView(R.layout.leer)
                        .setBtnConfirmText("Entendido!")
                        .setBtnConfirmTextSizeDp(16)
                        .setBtnConfirmTextColor("#FF3872C9")
                        .show();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HeaderDialog(getActivity())
                        .setColor(getResources().getColor(R.color.colorAccent)) // Sets the header background color
                        .setElevation(false) // Sets the header elevation, true by default
                        .setIcon(getResources().getDrawable(R.drawable.ic_info_outline_white_36dp)) // Sets the dialog icon image
                        .setTitle("Idea y desarrollo") // Sets the dialog title
                        .setMessage("Marcelo Espinoza \n Formosa - Argentina") // Sets the dialog message
                        //.justifyContent(true) // Justifies the message text, false by default
                        .setTitleColor(Color.parseColor("#212121")) // Sets the header title text color
                        .setIconColor(Color.parseColor("#212121")) // Sets the header icon color
                        .setTitleGravity(Gravity.CENTER_HORIZONTAL) // Sets the header title text gravity
                        .setMessageGravity(Gravity.CENTER_HORIZONTAL) // Sets the message text gravity
                        .setTitleMultiline(true) // Multiline header title text option, true by default
                       // .setView(R.layout.acerca)// Set custom view to the dialog (only possible via layout resource)
                .setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Your action
                    }
                })
                        .show();
            }
        });
        provincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Provincia")
                        .items(R.array.provincia)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                editor = prefs.edit();
                                switch (which){
                                    case 0:editor.putString("provincia", "Buenos Aires"); cargarCiudades(R.array.buenos_aires);break;
                                    case 1:editor.putString("provincia", "Capital Federal");cargarCiudades(R.array.capital_federal);break;
                                    case 2:editor.putString("provincia", "Catamarca");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 3:editor.putString("provincia", "Chaco");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 4:editor.putString("provincia", "Chubut");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 5:editor.putString("provincia", "Corrientes");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 6:editor.putString("provincia", "Entre Rios");editor.putString("ciudad", "");editor.commit();reiniciarApp();break;
                                    case 7:editor.putString("provincia", "Formosa"); editor.putString("ciudad", "");editor.commit();reiniciarApp();break;
                                    case 8:editor.putString("provincia", "Jujuy"); editor.putString("ciudad", "");editor.commit();reiniciarApp();break;
                                    case 9:editor.putString("provincia", "Rio negro");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 10:editor.putString("provincia", "San Juan");editor.putString("ciudad", "");editor.commit(); reiniciarApp();break;
                                    case 11:editor.putString("provincia", "San Luis");  editor.putString("ciudad", "");editor.commit();reiniciarApp();break;
                                    case 12:editor.putString("provincia", "Santa Fe"); cargarCiudades(R.array.santa_fe); break;
                                }
                                editor.commit();
                                return true;
                            }
                        })
                        .positiveText("Seleccionar")
                        .show();
            }
        });

        return rootView;
    }

    private void reiniciarApp(){
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        System.exit(0);
    }

    private void cargarCiudades(int provincia){
        new MaterialDialog.Builder(getContext())
                .title("Ciudad/Localidad")
                .items(provincia)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        editor.putString("ciudad", String.valueOf(text));
                        editor.commit();
                        reiniciarApp();
                        return true;
                    }
                })
                .positiveText("Seleccionar")
                .show();
    }

    protected void openFacebookPage(){

        String FACEBOOK_URL = "https://www.facebook.com/subemovil";
        String FACEBOOK_PAGE_ID = "subemovil";
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        PackageManager packageManager = getContext().getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                facebookIntent.setData(Uri.parse("fb://facewebmodal/f?href=" + FACEBOOK_URL));
                startActivity(facebookIntent);
            } else { //older versions of fb app
                facebookIntent.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
                startActivity(facebookIntent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            facebookIntent.setData(Uri.parse(FACEBOOK_URL));
            startActivity(facebookIntent);//normal web url
        }

    }

}
