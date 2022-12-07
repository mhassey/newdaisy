package com.daisy.broadcast.bootcomplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.daisy.activity.mainActivity.MainActivity;
import com.daisy.activity.splash.SplashScreen;

/**
 *  Broadcast receiver
 */
public class BootReceiver extends BroadcastReceiver {


	/**
	 * Handle phone reboot
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		

		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent intent2 = new Intent(context, SplashScreen.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			context.startActivity(intent2);
		}

	}

}
