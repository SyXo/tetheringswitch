package com.github.eight8ta.tetheringswitch;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.util.Log;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("onReceive ACTION_SCREEN_ON");
                if (ontime == 0) {
                    ontime = System.currentTimeMillis();
                }
                else {
                    long cur = System.currentTimeMillis();
                    if (cur - ontime < 500) {
                        Toggle();
                    }
                    ontime = 0;
                }
            }
        }, filter);

        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("onReceive ACTION_SCREEN_OFF");
            }
        }, filter);
    }
    long ontime=0;
    void Toggle()
    {
        Context context = this.getApplicationContext();
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Boolean is_ap_enabled = false;
        try {
            Method method = wifi.getClass().getMethod("isWifiApEnabled");
            is_ap_enabled = "true".equals(method.invoke(wifi).toString());
        }
        catch (Exception e) {
            Log.e("[TetheringSwitch]", e.getMessage(), e);
        }
        try {
            Method method;
            method = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifi, null, !is_ap_enabled);
        } catch (Exception e) {
            Log.e("[TetheringSwitch]", e.getMessage(), e);
        }
    }
}
