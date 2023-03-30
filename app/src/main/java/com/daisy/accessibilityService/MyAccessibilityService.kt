package com.daisy.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.daisy.common.session.SessionManager

class MyAccessibilityService : AccessibilityService() {
    private val TAG = "MyAccessibilityService"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e(TAG, "onAccessibilityEvent: ${event?.text.toString()} ")

        /**
         * This event is detecting long press of power button
         * Note:- Tested on Pixel 5 may be there are different events for other providers
         */

            if (event?.text.toString() == "[Phone options]") {

                performGlobalAction(GLOBAL_ACTION_BACK)
            }
            if (event?.text.toString() == "[Side key settings]") {
                performGlobalAction(GLOBAL_ACTION_BACK)
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
//            if (event?.text.toString() == "[]") {
//                performGlobalAction(GLOBAL_ACTION_BACK)
//                performGlobalAction(GLOBAL_ACTION_BACK)
//            }



    }


    override fun onInterrupt() {
        SessionManager.get().setAccessibilityService(false)

        Log.e(TAG, "onInterrupt: ")

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        SessionManager.get().setAccessibilityService(true)

        info.apply {
            // These events are help to detect multiple triggers of user interactions
            // TYPE_WINDOW_STATE_CHANGED This Event help to detect window focus change events of device
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//            or; AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
            notificationTimeout = 500
        }

        this.serviceInfo = info
        Log.e(TAG, "onServiceConnected: ")
    }


}