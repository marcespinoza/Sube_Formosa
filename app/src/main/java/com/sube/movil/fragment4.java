package com.sube.movil;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;

/**
 * Created by Marcelo on 22/10/2015.
 */
public class fragment4 extends Fragment {

   BtnFacebook facebook;
   TextView facetext;
   AwesomeTextView fatext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        View rootView = inflater.inflate(R.layout.fragment4, container, false);
        fatext = (AwesomeTextView) rootView.findViewById(R.id.fa_text);
        Typeface light = Typeface.createFromAsset(getContext().getAssets(), "fonts/light.ttf");
        facetext = (TextView) rootView.findViewById(R.id.facetext);
        facetext.setTypeface(light);
        facebook = (BtnFacebook) rootView.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookPage();
            }
        });
        fatext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyInfoDialog(getContext())
                        .setTopColorRes(R.color.azul)
                        .setIcon(R.drawable.ic_info_outline_white_36dp)
                        .setTitle("Desarrollado por")
                        .setMessage("Marcelo Espinoza \nFormosa (Argentina)")
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
