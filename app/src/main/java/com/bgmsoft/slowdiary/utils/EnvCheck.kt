package com.bgmsoft.slowdiary.utils

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class EnvCheck {

    companion object {

        val REQUEST_CODE_RECOVER_PLAY_SERVICES = 100

        fun checkGooglePlayServices(act: Activity): Boolean {
            /**
             * Google Play Services is missing or update is required
             * return code could be
             * SUCCESS,
             * SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
             * SERVICE_DISABLED, SERVICE_INVALID.
             */

            // check google play service
            GoogleApiAvailability.getInstance().let {
                val status = it.isGooglePlayServicesAvailable(act)
                if (status != ConnectionResult.SUCCESS) {
                    if (it.isUserResolvableError(status)) {
                        it.getErrorDialog(act, status, REQUEST_CODE_RECOVER_PLAY_SERVICES).show()
                    } else {
                        L.e("Google Play Services is not installed!!")

                        act.finishAffinity()
                    }

                    return false
                }

                return true
            }
        }
    }
}