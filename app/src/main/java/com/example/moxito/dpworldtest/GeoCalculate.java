package com.example.moxito.dpworldtest;

import android.location.Location;
import android.util.Log;


/**
 * Created by Moxito on 04/04/2016.
 */
public class GeoCalculate {
    private static final int minutes = 2;
    private static final int preferedTime = 100*60*minutes;

    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            Log.i("GeoCalculate","null");
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > preferedTime;
        boolean isSignificantlyOlder = timeDelta < -preferedTime;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            Log.i("GeoCalculate","1");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.i("GeoCalculate","2");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.i("GeoCalculate","3");
            return true;
        }
        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
