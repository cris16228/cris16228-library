package com.github.cris16228.library.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.github.cris16228.library.R;

import java.text.NumberFormat;
import java.util.Locale;

public class BatteryReceiver extends BroadcastReceiver {

    public String charging_status, battery_condition, power_source, technology, _voltage;
    public int level;
    public int scale;
    public int health;
    public int temperature_c;
    public int temperature_f;
    public int source;
    public int status;
    public long current_mah;
    public int voltage;
    public int energy_io_now;
    public int energy_io_average;
    public onBatteryChange onBatteryChange;
    private Context _context;

    public void onBatteryChange(onBatteryChange _onBatteryChange) {
        onBatteryChange = _onBatteryChange;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (_context == null)
            _context = context;
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                battery_condition = context.getResources().getString(R.string.battery_condition_cold);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                battery_condition = context.getResources().getString(R.string.battery_condition_dead);
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                battery_condition = context.getResources().getString(R.string.battery_condition_good);
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                battery_condition = context.getResources().getString(R.string.battery_condition_unknown);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                battery_condition = context.getResources().getString(R.string.battery_condition_overheat);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                battery_condition = context.getResources().getString(R.string.battery_condition_high_voltage);
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                battery_condition = context.getResources().getString(R.string.battery_condition_unspecified_failure);
                break;
        }
        temperature_c = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
        temperature_f = (int) (temperature_c * 1.8 + 32);
        source = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (source) {
            case 0:
                power_source = context.getResources().getString(R.string.battery_power_source_unplugged);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                power_source = context.getResources().getString(R.string.battery_power_source_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                power_source = context.getResources().getString(R.string.battery_power_source_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                power_source = context.getResources().getString(R.string.battery_power_source_wireless);
                break;
        }
        status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                charging_status = context.getResources().getString(R.string.battery_status_charging);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                charging_status = context.getResources().getString(R.string.battery_status_discharging);
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                charging_status = context.getResources().getString(R.string.battery_status_unknown);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                charging_status = context.getResources().getString(R.string.battery_status_not_charging);
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                charging_status = context.getResources().getString(R.string.battery_status_full);
                break;
        }
        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        _voltage = NumberFormat.getNumberInstance(Locale.US)
                .format(voltage)
                .replace(",", "،");
        BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        current_mah = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        energy_io_now = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        energy_io_average = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        if (onBatteryChange != null)
            onBatteryChange.update();
    }

    public interface onBatteryChange {
        void update();
    }
}