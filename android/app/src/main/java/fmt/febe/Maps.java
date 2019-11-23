package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LocationFinder;
import fmt.febe.helper.Menu;


public class Maps extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    ImageButton IB_SEARCH, IB_MENU, IB_FROM_CANCEL, IB_TO_CANCEL,
            IB_FROM_MY_LOC, IB_TO_MY_LOC, IB_ZOOM_IN, IB_ZOOM_OUT;

    private EditText ET_FROM;
    private EditText ET_TO;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    private ProgressDialog progressDialog;

    private Menu menu;

    SupportMapFragment mapFragment;

    private BasicFunctions basicFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        basicFunctions = new BasicFunctions(Maps.this);

        menu = new Menu(Maps.this);

        IB_MENU = findViewById(R.id.ma_menu);
        IB_SEARCH = findViewById(R.id.ma_search);
        ET_FROM = findViewById(R.id.ma_from);
        ET_TO = findViewById(R.id.ma_to);
        IB_FROM_CANCEL = findViewById(R.id.ma_from_cancel);
        IB_FROM_MY_LOC = findViewById(R.id.ma_from_my_location);
        IB_TO_CANCEL = findViewById(R.id.ma_to_cancel);
        IB_TO_MY_LOC = findViewById(R.id.ma_to_my_location);
        IB_ZOOM_IN = findViewById(R.id.ma_zoom_in);
        IB_ZOOM_OUT = findViewById(R.id.ma_zoom_out);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ma_map);

        IB_MENU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        if (basicFunctions.isConnectingToInternet())
            mapFragment.getMapAsync(Maps.this);

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            if (basicFunctions.isConnectingToInternet())
                                mapFragment.getMapAsync(Maps.this);

                            else {

                                Toast.makeText(Maps.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(Maps.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Maps.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

        IB_SEARCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        IB_FROM_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_FROM.setText("");

            }
        });

        IB_TO_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_TO.setText("");

            }
        });

        IB_FROM_MY_LOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_FROM.setText(R.string.ma_your_location);

            }
        });

        IB_TO_MY_LOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_TO.setText(R.string.ma_your_location);

            }
        });


        IB_ZOOM_IN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.moveCamera(CameraUpdateFactory.zoomBy(1));

            }
        });

        IB_ZOOM_OUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.moveCamera(CameraUpdateFactory.zoomBy(-1));

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        setCurrentLocation();

    }


    private void sendRequest() {

        String from = ET_FROM.getText().toString();

        String to = ET_TO.getText().toString();

        if (TextUtils.isEmpty(from))
            ET_FROM.setError("Type in the From Location !");

        else if (TextUtils.isEmpty(to))
            ET_FROM.setError("Type in the To Location !");

        else {

            if (from.equals("Your Location"))
                from = getCurrentLocation();

            else if (to.equals("Your Location"))
                to = getCurrentLocation();

            try {

                new DirectionFinder(from, to).execute();

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }

        }

    }


    private String getCurrentLocation() {

        LocationFinder finder;

        finder = new LocationFinder(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(finder.getLatitude(), finder.getLongitude(), 1);

        } catch (IOException e) {

            e.printStackTrace();

        }

        assert addresses != null;

        return addresses.get(0).getAddressLine(0);

    }


    @SuppressLint("MissingPermission")
    private void setCurrentLocation() {

        LocationFinder finder;

        finder = new LocationFinder(this);

        LatLng currentLocation = new LatLng(finder.getLatitude(), finder.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Your Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ma_user_location))
                .position(currentLocation)));

        mMap.setMyLocationEnabled(false);

    }

    private class DirectionFinder {

        private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
        private static final String GOOGLE_API_KEY = "AIzaSyD4jy-NlnE-rknzbRRjuC0QoQSXBHG7-hk";
        private String origin;
        private String destination;


        DirectionFinder(String origin, String destination) {

            this.origin = origin;
            this.destination = destination;

        }


        void execute() throws UnsupportedEncodingException {

            onDirectionFinderStart();
            new DownloadRawData().execute(createUrl());

        }


        private String createUrl() throws UnsupportedEncodingException {

            String urlOrigin = URLEncoder.encode(origin, "utf-8");
            String urlDestination = URLEncoder.encode(destination, "utf-8");

            return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;

        }


        @SuppressLint("StaticFieldLeak")
        private class DownloadRawData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String link = params[0];

                try {

                    URL url = new URL(link);
                    InputStream is = url.openConnection().getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }

                    return buffer.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;

            }

            @Override
            protected void onPostExecute(String res) {

                try {
                    parseJSon(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }


        private void parseJSon(String data) throws JSONException {

            if (data == null)
                return;

            List<Route> routes = new ArrayList<>();
            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonRoutes = jsonData.getJSONArray("routes");

            for (int i = 0; i < jsonRoutes.length(); i++) {

                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                Route route = new Route();

                JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                route.endAddress = jsonLeg.getString("end_address");
                route.startAddress = jsonLeg.getString("start_address");
                route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                route.points = decodePolyLine(overview_polylineJson.getString("points"));

                routes.add(route);

            }

            onDirectionFinderSuccess(routes);

        }


        private List<LatLng> decodePolyLine(final String poly) {

            int len = poly.length();
            int index = 0;
            List<LatLng> decoded = new ArrayList<>();
            int lat = 0;
            int lng = 0;

            while (index < len) {

                int b;
                int shift = 0;
                int result = 0;

                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;

                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                decoded.add(new LatLng(
                        lat / 100000d, lng / 100000d
                ));

            }

            return decoded;

        }


        void onDirectionFinderStart() {

            progressDialog = new ProgressDialog(Maps.this);
            progressDialog.setMessage("Finding Route ... ");
            progressDialog.show();

            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }

        }


        @SuppressLint("MissingPermission")
        void onDirectionFinderSuccess(List<Route> routes) {

            progressDialog.dismiss();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(ET_FROM.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(ET_TO.getWindowToken(), 0);

            mMap.setMyLocationEnabled(true);

            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();

            for (Route route : routes) {

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                boundsBuilder.include(route.startLocation);

                boundsBuilder.include(route.endLocation);

                int routePadding = 60;

                LatLngBounds latLngBounds = boundsBuilder.build();

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));

                ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ma_user_location))
                        .title(route.startAddress)
                        .position(route.startLocation)));

                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ma_destination))
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)).
                        width(8);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));

            }
        }
    }


    private class Distance {

        String text;
        int value;

        Distance(String text, int value) {
            this.text = text;
            this.value = value;
        }

    }


    private class Route {

        Distance distance;
        Duration duration;
        String endAddress;
        LatLng endLocation;
        String startAddress;
        LatLng startLocation;
        List<LatLng> points;

    }


    private class Duration {

        public String text;
        int value;

        Duration(String text, int value) {
            this.text = text;
            this.value = value;
        }

    }

}