package fmt.febe.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationEnabler {

    private static final int GPS_REQUEST = 1001;

    private Context context;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationManager locationManager;

    public LocationEnabler(Context context) {

        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);

    }

    public void turnGPSOn(final onGpsListener onGpsListener) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (onGpsListener != null) {
                onGpsListener.gpsStatus();
            }

        } else {

            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                            if (onGpsListener != null)
                                onGpsListener.gpsStatus();

                        }
                    })
                    .addOnFailureListener((Activity) context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            int statusCode = ((ApiException) e).getStatusCode();

                            switch (statusCode) {

                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {

                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) context, GPS_REQUEST);

                                    } catch (IntentSender.SendIntentException sie) {

                                        Toast.makeText(context, "Unable to enable Location !", Toast.LENGTH_LONG).show();

                                    }

                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                    Toast.makeText(context, "Unable to enable Location, kindly enable the same from Settings !", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
    }

    public interface onGpsListener {
        void gpsStatus();
    }

}