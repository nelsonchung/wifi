package com.nelsonchung.wifiautoconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    //Nelson add
    public static final int TIMEOUT = 30;
    WifiManager wifiManager;
    int netid_24g_wpawpa2;
    int netid_24g_open;
    int timeout;
    String ssid_24g_wpawpa2 = new String("1-TELENET-24g");
    String key_24g_wpawpa2 = new String("12345678");
    String ssid_24g_open = new String("1-TELENETHOMESPOT-24g");
    ConnectivityManager connManager;
    NetworkInfo mWifi;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //Nelson add
        wifi_auto_connection();
    }

    /*
    Ref:
    http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically
     */
    private void wifi_auto_connection(){

        //wifi manager
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        //wifi connection status
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        WifiConfiguration wificonfig_24g_wpawpa2 = new WifiConfiguration();
        WifiConfiguration wificonfig_24g_open = new WifiConfiguration();
        WifiConfiguration wificonfig_24g_wep = new WifiConfiguration();
        WifiConfiguration wificonfig_5g_wpawpa2 = new WifiConfiguration();
        WifiConfiguration wificonfig_5g_open = new WifiConfiguration();
        WifiConfiguration wificonfig_5g_wep = new WifiConfiguration();

        //For Open networks
        wificonfig_24g_open.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wificonfig_24g_open.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfig_24g_open.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfig_24g_open.allowedAuthAlgorithms.clear();
        wificonfig_24g_open.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wificonfig_24g_open.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        wificonfig_24g_open.SSID = String.format("\"%s\"", ssid_24g_open);

        //For WPA and WPA2
        wificonfig_24g_wpawpa2.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfig_24g_wpawpa2.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfig_24g_wpawpa2.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wificonfig_24g_wpawpa2.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wificonfig_24g_wpawpa2.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wificonfig_24g_wpawpa2.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wificonfig_24g_wpawpa2.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wificonfig_24g_wpawpa2.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wificonfig_24g_wpawpa2.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        wificonfig_24g_wpawpa2.SSID = String.format("\"%s\"", ssid_24g_wpawpa2);
        wificonfig_24g_wpawpa2.preSharedKey = String.format("\"%s\"", key_24g_wpawpa2);

//remember id
        //Add wifi network config
        netid_24g_wpawpa2 = wifiManager.addNetwork(wificonfig_24g_wpawpa2);
        netid_24g_open = wifiManager.addNetwork(wificonfig_24g_open);

        cleartimeout();
        connect_24g_wpawpa2();
        connect_24g_open();

    }

    private void showoutput(String stroutput){
        TextView output = (TextView)findViewById(R.id.output);
        output.setText(stroutput);
    }

    private void cleartimeout(){
        timeout=0;
    }
    private void checkwifistatus(String str_ssid){
        while (!mWifi.isConnected() && timeout<TIMEOUT) {
            showoutput("Connecting to " + str_ssid);
            SystemClock.sleep(1000);
            timeout++;
        }
    }
    private void connect_24g_wpawpa2(){
        cleartimeout();

        wifiManager.disconnect();
        wifiManager.enableNetwork(netid_24g_wpawpa2, true);
        wifiManager.reconnect();

        checkwifistatus(ssid_24g_wpawpa2);

        if( timeout <= TIMEOUT)
            showoutput("Connect to " + ssid_24g_wpawpa2 + " successfully.");
        else
            showoutput("Connect to " + ssid_24g_wpawpa2 + " fail.");
    }
    private void connect_24g_open(){
        cleartimeout();
        wifiManager.disconnect();
        wifiManager.enableNetwork(netid_24g_open, true);
        wifiManager.reconnect();

        checkwifistatus(ssid_24g_open);

        if( timeout <= TIMEOUT)
            showoutput("Connect to " + ssid_24g_open + " successfully.");
        else
            showoutput("Connect to " + ssid_24g_open + " fail.");
    }
    private void connect_5g_wpawpa2(){

    }
    private void connect_5g_open(){

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
