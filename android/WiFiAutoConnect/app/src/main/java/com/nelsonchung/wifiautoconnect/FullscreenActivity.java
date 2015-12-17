package com.nelsonchung.wifiautoconnect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    public static final int TIMEOUT_BETWEEN_CONNECTION = 5000;
    public static final int MSG_START = 1;
    public static final int MSG_DISCONNECT = 2;
    public static final int MSG_CONNECT_SUCCESSFUL = 3;
    public static final int MSG_24G_WPAWPA2_DISCONNECT = 4;
    public static final int MSG_24G_WPAWPA2_CONNECTING = 5;
    public static final int MSG_24G_WPAWPA2_CONNECT = 6;
    public static final int MSG_24G_WPAWPA2_FAIL = 7;
    public static final int MSG_24G_OPEN_DISCONNECT = 8;
    public static final int MSG_24G_OPEN_CONNECTING = 9;
    public static final int MSG_24G_OPEN_CONNECT = 10;
    public static final int MSG_24G_OPEN_FAIL = 11;
    public static final int MSG_WIFI_DISABLE = 12;
    public static final int MSG_WIFI_WAITING_IP_ADDRESS = 13;
    public static final int MSG_24G_EAP_DISCONNECT = 14;
    public static final int MSG_24G_EAP_CONNECTING = 15;
    public static final int MSG_24G_EAP_CONNECT = 16;
    public static final int MSG_24G_EAP_FAIL = 17;
    //for eap authentication
    private static final String STR_EAP_USERNAME = "cbn";
    private static final String STR_EAP_PASSWORD = "cbn";

    private WifiManager wifiManager;
    private int netid_24g_wpawpa2, netid_24g_open, netid_24g_eap;
    private int timeout;

    private String ssid_24g_wpawpa2 = new String("00Nelson_24G_Private");
    private String key_24g_wpawpa2 = new String("12345678");
    private String ssid_24g_open = new String("TELENETHOMESPOT");
    private String ssid_24g_eap = new String("1-TelenetWiFree");
    public static final String zeroipaddress = new String("0.0.0.0");

    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    SupplicantState supState;
    private Handler g_handler;
    private HandlerThread g_handlerthread;
    WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
    TextView output;
    private String g_strWiFiStatus = new String("Under testing.....");
    private EditText edittext_ssid_24g_wpawpa2, edittext_ssid_24g_password, edittext_ssid_24g_open, edittext_ssid_24g_eap;

    //wifi configuration
    WifiConfiguration wificonfig_24g_wpawpa2 = new WifiConfiguration();
    WifiConfiguration wificonfig_24g_open = new WifiConfiguration();
    WifiConfiguration wificonfig_24g_wep = new WifiConfiguration();
    WifiConfiguration wificonfig_5g_wpawpa2 = new WifiConfiguration();
    WifiConfiguration wificonfig_5g_open = new WifiConfiguration();
    WifiConfiguration wificonfig_5g_wep = new WifiConfiguration();
    WifiConfiguration wificonfig_24g_eap = new WifiConfiguration();




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

        //Nelson Add
        edittext_ssid_24g_wpawpa2   = (EditText) findViewById(R.id.ssid_24g_wpawpa2);
        edittext_ssid_24g_password  = (EditText) findViewById(R.id.ssid_24g_wpawpa2_password);
        edittext_ssid_24g_open      = (EditText) findViewById(R.id.ssid_24g_open);
        edittext_ssid_24g_eap       = (EditText) findViewById(R.id.ssid_24g_eap);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //Nelson add
        /* show ok
        AlertDialog.Builder aaa = new AlertDialog.Builder(this);
        aaa.setTitle("");
        aaa.setMessage("Start testing");
        aaa.show();
        */
        output = (TextView)findViewById(R.id.output);

        //handle 1
        g_handlerthread = new HandlerThread("do_wifi_auto_connect");
        g_handlerthread.start();
        g_handler = new Handler(g_handlerthread.getLooper());
        g_handler.post(runner);
    }


    Handler myMessengeHandler = new Handler() {
        public void handleMessage(Message msg){
            switch(msg.what) {
                case MSG_START:
                    output.setText("Starting...................");
                    break;
                case MSG_DISCONNECT:
                    output.setText("Disconnecting...............");
                    break;
                case MSG_CONNECT_SUCCESSFUL:
                    output.setText("Connecting...............");
                    break;
                case MSG_24G_OPEN_DISCONNECT:
                    output.setText("Disconnecting from "+ssid_24g_open);
                    break;
                case MSG_24G_OPEN_CONNECTING:
                    output.setText("Connecting to "+ssid_24g_open+" "+timeout+" times");
                    break;
                case MSG_24G_OPEN_CONNECT:
                    output.setText("Connect to "+ssid_24g_open+" successfully");
                    break;
                case MSG_24G_OPEN_FAIL:
                    output.setText("Connect to "+ssid_24g_open+" fail");
                    break;
                case MSG_24G_WPAWPA2_DISCONNECT:
                    output.setText("Disconnecting from "+ssid_24g_wpawpa2);
                    break;
                case MSG_24G_WPAWPA2_CONNECTING:
                    output.setText("Connecting to "+ssid_24g_wpawpa2+" "+timeout+" times");
                    break;
                case MSG_24G_WPAWPA2_CONNECT:
                    output.setText("Connect to "+ssid_24g_wpawpa2+" successfully");
                    break;
                case MSG_24G_WPAWPA2_FAIL:
                    output.setText("Connect to "+ssid_24g_wpawpa2+" fail");
                    break;
                case MSG_24G_EAP_DISCONNECT:
                    output.setText("Disconnecting from "+ssid_24g_eap);
                    break;
                case MSG_24G_EAP_CONNECTING:
                    output.setText("Connecting to "+ssid_24g_eap+" "+timeout+" times");
                    break;
                case MSG_24G_EAP_CONNECT:
                    output.setText("Connect to "+ssid_24g_eap+" successfully");
                    break;
                case MSG_24G_EAP_FAIL:
                    output.setText("Connect to "+ssid_24g_eap+" fail");
                    break;
                case MSG_WIFI_DISABLE:
                    output.setText("WiFi關閉，請打開。");
                    break;
                case MSG_WIFI_WAITING_IP_ADDRESS:
                    output.setText("Waiting for valid ip address");
                    break;
                default:
                    output.setText("Not supported");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable runner = new Runnable() {
        @Override
        public void run() {
            wifi_auto_connection();
        }
    };

    private void updateoutput(int def){
        Message m = new Message();
        m.what = def;
        FullscreenActivity.this.myMessengeHandler.sendMessage(m);
    }

    private void DisableWiFiOpen(){
        SystemClock.sleep(2000);
        wifiManager.disableNetwork(netid_24g_open);
        wifiManager.removeNetwork(netid_24g_open);
    }
    private void DisableWiFiWpa(){
        SystemClock.sleep(2000);
        wifiManager.disableNetwork(netid_24g_wpawpa2);
        wifiManager.removeNetwork(netid_24g_wpawpa2);
    }
    private void DisableWiFiEap(){
        SystemClock.sleep(2000);
        wifiManager.disableNetwork(netid_24g_eap);
        wifiManager.removeNetwork(netid_24g_eap);
    }

    private void AddWiFiConfigWpa(){

        cleartimeout();
        updateoutput(MSG_DISCONNECT);

        ssid_24g_wpawpa2    = edittext_ssid_24g_wpawpa2.getText().toString();
        key_24g_wpawpa2     = edittext_ssid_24g_password.getText().toString();

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

        //Add wifi network config
        netid_24g_wpawpa2 = wifiManager.addNetwork(wificonfig_24g_wpawpa2);
    }
    private void AddWiFiConfigOpen(){

        cleartimeout();
        updateoutput(MSG_DISCONNECT);

        ssid_24g_open       = edittext_ssid_24g_open.getText().toString();

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

        netid_24g_open = wifiManager.addNetwork(wificonfig_24g_open);
    }
    private void AddWiFiConfigWep(){
        //we need to modify wificonfig_24g_open to another wep parameter
        wificonfig_24g_open.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wificonfig_24g_open.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfig_24g_open.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfig_24g_open.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wificonfig_24g_open.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wificonfig_24g_open.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wificonfig_24g_open.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wificonfig_24g_open.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
    }
    private void AddWiFiConfigEap(){

        /*
        Ref:
        http://stackoverflow.com/questions/19170260/how-to-connect-to-wpa-eap-wifi-on-android-with-4-3-api
        http://developer.android.com/reference/android/net/wifi/WifiEnterpriseConfig.html
         */
        cleartimeout();
        updateoutput(MSG_DISCONNECT);
        ssid_24g_eap    = edittext_ssid_24g_eap.getText().toString();

        wificonfig_24g_eap.SSID = String.format("\"%s\"", ssid_24g_eap);
        wificonfig_24g_eap.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wificonfig_24g_eap.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        enterpriseConfig.setIdentity(STR_EAP_USERNAME);
        enterpriseConfig.setPassword(STR_EAP_PASSWORD);
        enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
        wificonfig_24g_eap.enterpriseConfig = enterpriseConfig;

        //Add wifi network config
        netid_24g_eap = wifiManager.addNetwork(wificonfig_24g_eap);
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

        if(!wifiManager.isWifiEnabled()){
            updateoutput(MSG_WIFI_DISABLE);
            SystemClock.sleep(3000);
            finish();
        }

        updateoutput(MSG_START);

        while(true) {
            connect_24g_wpawpa2();
            connect_24g_open();
            connect_24g_eap();
        }
    }

    private void showoutput(String stroutput){

        String.format(g_strWiFiStatus, stroutput);
        /* move code to thread, maybe we can't use original design to show message
        TextView output = (TextView)findViewById(R.id.output);
        output.setText(stroutput);
        */
        /* can't show successful in thread
        AlertDialog.Builder outputdialog = new AlertDialog.Builder(this);
        outputdialog.setTitle("");
        outputdialog.setMessage(stroutput);
        outputdialog.show();
        */
    }

    private void cleartimeout(){
        timeout=0;
    }
    private void checkwifistatus(String str_ssid){
    //private void checkwifistatus(){

        supState = wifiManager.getConnectionInfo().getSupplicantState();

        /*
        //The mechanism is strange.
        isConnected is false when wifi disable
        isConnected is true when wifi enable
        isAvailable is alwalys true whether wifi is enable or disable
        while ( !mWifi.isAvailable() ||
            (!mWifi.isConnected() && timeout<TIMEOUT)
        */
        //Ref: http://developer.android.com/reference/android/net/wifi/SupplicantState.html
        while (supState != SupplicantState.COMPLETED && timeout < TIMEOUT) {
            //showoutput("Connecting to " + str_ssid);
            //SystemClock.sleep(1000);
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            timeout++;
            if(str_ssid.equalsIgnoreCase(ssid_24g_open)){
                updateoutput(MSG_24G_OPEN_CONNECTING);
            }
            else if(str_ssid.equalsIgnoreCase(ssid_24g_wpawpa2)){
                updateoutput(MSG_24G_WPAWPA2_CONNECTING);
            }
            else if(str_ssid.equalsIgnoreCase(ssid_24g_eap)){
                updateoutput(MSG_24G_EAP_CONNECTING);
            }
            supState = wifiManager.getConnectionInfo().getSupplicantState();
        }

        //check ip address is ready
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        while(ipAddress.equalsIgnoreCase(zeroipaddress) && timeout < TIMEOUT){
        //while(ipAddress.compareTo(zeroipaddress)!=0 && timeout < TIMEOUT){
            updateoutput(MSG_WIFI_WAITING_IP_ADDRESS);
            SystemClock.sleep(1000);
            ip = wifiManager.getConnectionInfo().getIpAddress();
            ipAddress = Formatter.formatIpAddress(ip);
            timeout++;
        }

    }
    private void connect_24g_wpawpa2(){
        AddWiFiConfigWpa();
        wifiManager.disconnect();
        wifiManager.enableNetwork(netid_24g_wpawpa2, true);
        wifiManager.reconnect();

        updateoutput(MSG_24G_WPAWPA2_CONNECTING);
        SystemClock.sleep(1000);
        checkwifistatus(ssid_24g_wpawpa2);
        //checkwifistatus();


        if( timeout < TIMEOUT)
            //showoutput("Connect to " + ssid_24g_wpawpa2 + " successfully.");
            updateoutput(MSG_24G_WPAWPA2_CONNECT);
        else
            updateoutput(MSG_24G_WPAWPA2_FAIL);

        SystemClock.sleep(TIMEOUT_BETWEEN_CONNECTION);
        DisableWiFiWpa();
    }

    private void connect_24g_open() {
        AddWiFiConfigOpen();
        wifiManager.disconnect();
        wifiManager.enableNetwork(netid_24g_open, true);
        wifiManager.reconnect();

        updateoutput(MSG_24G_OPEN_CONNECTING);
        SystemClock.sleep(1000);
        //checkwifistatus();
        checkwifistatus(ssid_24g_open);

        if( timeout < TIMEOUT)
            //showoutput("Connect to " + ssid_24g_open + " successfully.");
            updateoutput(MSG_24G_OPEN_CONNECT);
        else
            //showoutput("Connect to " + ssid_24g_open + " fail.");
            updateoutput(MSG_24G_OPEN_FAIL);

        SystemClock.sleep(TIMEOUT_BETWEEN_CONNECTION);
        DisableWiFiOpen();
    }
    private void connect_24g_eap(){
        AddWiFiConfigEap();
        wifiManager.disconnect();
        wifiManager.enableNetwork(netid_24g_eap, true);
        wifiManager.reconnect();

        updateoutput(MSG_24G_EAP_CONNECTING);
        SystemClock.sleep(1000);
        //checkwifistatus();
        checkwifistatus(ssid_24g_eap);

        if( timeout < TIMEOUT)
            updateoutput(MSG_24G_EAP_CONNECT);
        else
            updateoutput(MSG_24G_EAP_FAIL);

        SystemClock.sleep(TIMEOUT_BETWEEN_CONNECTION);
        DisableWiFiEap();

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

    protected void onDestory(){

        super.onDestroy();

        if(g_handler!=null){
            g_handler.removeCallbacks(runner);
        }

        if(g_handlerthread!=null){
            g_handlerthread.quit();
        }

    }
}
