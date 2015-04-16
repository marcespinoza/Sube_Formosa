package com.sube.movil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.json.JSONArray;


public class MainActivity extends FragmentActivity implements View.OnClickListener , ListInterface{

private ResideMenu resideMenu;
private MainActivity mContext;
private ResideMenuItem itemHome;
private ResideMenuItem itemProfile;
private ResideMenuItem itemCalendar;
fragment2 fragment2;

/**
 * Called when the activity is first created.
 */
@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        setUpMenu();
        fragment2 = new fragment2();
        if( savedInstanceState == null )
        changeFragment(new fragment1());

        }

private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.fondo);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.cuenta, "Mi cuenta");
        itemProfile  = new ResideMenuItem(this, R.drawable.puntosrecarga,  "Puntos de venta/recarga");
        itemCalendar = new ResideMenuItem(this, R.drawable.horarios, "Horarios de micros");


        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemCalendar.setOnClickListener(this);


        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);


        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
        }
        });
        }

@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
        }

@Override
public void onClick(View view) {

        if (view == itemHome){
        changeFragment(new fragment1());
        }else if (view == itemProfile){
        changeFragment(new fragment2());
        }else if (view == itemCalendar){
        changeFragment(new fragment3());
        }

        resideMenu.closeMenu();
        }

private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
@Override
public void openMenu() {
        Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

@Override
public void closeMenu() {
        Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
        };

private void changeFragment(Fragment targetFragment){
    if(targetFragment instanceof fragment2){
         getSupportFragmentManager().beginTransaction()
          .replace(R.id.main_fragment, fragment2, "fragment2")
          .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
          .commit();
    }else{

        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.main_fragment, targetFragment, "fragment")
        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .commit();
    }

        }

// What good method is to access resideMenu？
public ResideMenu getResideMenu(){
        return resideMenu;
        }

    @Override
    public void onCreateList(JSONArray list) {
         fragment2.getList(list);
    }
}