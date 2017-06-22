package com.github.eight8ta.tetheringswitch;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.*;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    static final String tag = "[TetheringSwitch]";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        this.getApplicationContext().registerReceiver(receiver_on, filter);

        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.getApplicationContext().registerReceiver(receiver_off, filter);

        findViewById(R.id.button_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toggle();
            }
        });
    }
    long offtime=0;
    private final BroadcastReceiver receiver_on = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(tag, "onReceive ACTION_SCREEN_ON offtime=" + offtime);
            long cur = System.currentTimeMillis();
            if (offtime != 0) {
                Log.i(tag, "diff=" + (cur - offtime));
                if (cur - offtime < 500) {
                    Toggle();
                }
            }
        }
    };
    private final BroadcastReceiver receiver_off = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            offtime = System.currentTimeMillis();
            Log.i(tag, "onReceive ACTION_SCREEN_OFF offtime=" + offtime);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver_on);
        unregisterReceiver(receiver_off);
    }
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
            Log.e(tag, e.getMessage(), e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Log.i(tag, "is_ap_enabled=" + is_ap_enabled);
        try {
            Method method;
            method = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifi, null, !is_ap_enabled);
        } catch (Exception e) {
            Log.e(tag, e.getMessage(), e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context, is_ap_enabled ? "on -> off" : "off -> on", Toast.LENGTH_LONG).show();
    }
}
