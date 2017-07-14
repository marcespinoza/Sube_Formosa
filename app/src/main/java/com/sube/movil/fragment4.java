package com.sube.movil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcelo on 22/10/2015.
 */
public class fragment4 extends Fragment {

   FancyButton facebook;
   TextView facetext, ubicacionTexto;
   FancyButton about;
   FancyButton button;
   SharedPreferences prefs;
   SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View rootView = inflater.inflate(R.layout.fragment4, container, false);
        button = (FancyButton) rootView.findViewById(R.id.cambiarUbicacion);
        ubicacionTexto = (TextView) rootView.findViewById(R.id.miubicacion);
        about = (FancyButton) rootView.findViewById(R.id.about);
        Typeface light = Typeface.createFromAsset(getContext().getAssets(), "fonts/light.ttf");
        facetext = (TextView) rootView.findViewById(R.id.facetext);
        facetext.setTypeface(light);
        facebook = (FancyButton) rootView.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookPage();
            }
        });
        prefs = getActivity().getSharedPreferences("ubicacion", MODE_PRIVATE);
        final String restoredText = prefs.getString("provincia", null);
        ubicacionTexto.setText(restoredText);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Idea y Desarrollo")
                        .setContentText("Marcelo Espinoza \nFormosa - Argentina")
                        .setCustomImage(R.drawable.exclamation)
                        .show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Provincia")
                        .items(R.array.provincia)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                editor = prefs.edit();
                                if (which==0){
                                    editor.putString("provincia", "Chaco");}
                                else
                                {
                                    editor.putString("provincia", "Formosa");
                                }
                                editor.commit();
                                Intent i = getActivity().getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                System.exit(0);
                                return true;
                            }
                        })
                        .positiveText("Seleccionar")
                        .show();
            }
        });

        return rootView;
    }


    protected void openFacebookPage(){

        String facebookPageID = "1573880172901216";
        String facebookUrl = "https://www.facebook.com/" + facebookPageID;
        String facebookUrlScheme = "fb://page/" + facebookPageID;

        try {
            int versionCode = getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrlScheme)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));

        }

    }

}
