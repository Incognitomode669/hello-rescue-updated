package com.example.hellorescue.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.hellorescue.R;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class MdrrmoStationFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long LOCATION_UPDATE_INTERVAL = 2000L;
    private static final float DEFAULT_ZOOM = 18f;
    private static final float DEFAULT_TILT = 90f;
    private static final LatLng TARGET_LOCATION = new LatLng(10.07991, 124.34261);
    private static final String TAG = "FireStationFragment";

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private ExecutorService executorService;

    private TextView routeDistanceText;
    private TextView routeEtaText;
    private Button followUserButton;
    private Marker userLocationMarker;
    private Marker targetLocationMarker;
    private Polyline currentPolyline;
    private LatLng currentUserLocation;

    private boolean followUserMode = false;
    private boolean isMapReady = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mdrrmo_station, container, false);
        initializeViews(view);
        initializeServices();
        setupMapAndListeners(savedInstanceState);
        return view;
    }

    private void initializeViews(View view) {
        mapView = view.findViewById(R.id.mdrrmo_map_view);
        routeDistanceText = view.findViewById(R.id.route_distance);
        routeEtaText = view.findViewById(R.id.route_eta);
        followUserButton = view.findViewById(R.id.btn_follow_user);
        ImageButton backButton = view.findViewById(R.id.mdrrmo_station_back);

        backButton.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigateUp());
    }

    private void initializeServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true); // Makes the thread a daemon thread
            return thread;
        });
        initializeLocationCallback();
    }

    private void setupMapAndListeners(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        followUserButton.setOnClickListener(v -> toggleFollowMode());
    }

    private void initializeLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateUserLocationOnMap(currentUserLocation);
                    if (isMapReady) {
                        drawRoute(currentUserLocation, TARGET_LOCATION);
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(LOCATION_UPDATE_INTERVAL);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                updateUserLocationOnMap(currentUserLocation);
                if (followUserMode) {
                    updateCameraPosition(currentUserLocation, DEFAULT_ZOOM, DEFAULT_TILT);
                }
            }
        });
    }

    private void updateUserLocationOnMap(LatLng location) {
        if (!isMapReady) return;

        BitmapDescriptor userIcon = BitmapDescriptorFactory.fromResource(R.drawable.user_location);

        if (userLocationMarker == null) {
            userLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Your Location")
                    .icon(userIcon));
        } else {
            userLocationMarker.setPosition(location);
        }

        if (followUserMode) {
            updateCameraPosition(location, DEFAULT_ZOOM, DEFAULT_TILT);
        }
    }

    private void toggleFollowMode() {
        followUserMode = !followUserMode;
        followUserButton.setText(followUserMode ? "Stop Following" : "Follow User");
        if (followUserMode) {
            getCurrentLocation();
        } else if (currentUserLocation != null) {
            updateCameraPosition(currentUserLocation, DEFAULT_ZOOM, 0);
        }
    }

    private void updateCameraPosition(LatLng target, float zoom, float tilt) {
        if (googleMap != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(target)
                    .zoom(zoom)
                    .tilt(tilt)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        isMapReady = true;
        setupMap();
        getCurrentLocation();
        startLocationUpdates();
    }

    private void setupMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        BitmapDescriptor targetIcon = BitmapDescriptorFactory.fromResource(R.drawable.target_location);
        targetLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(TARGET_LOCATION)
                .title("Destination")
                .icon(targetIcon));
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        if (!isAdded()) return;

        databaseReference.child("config").child("api_keys").child("google_maps")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (!isAdded()) return;
                    String apiKey = dataSnapshot.getValue(String.class); // Fixed the syntax error here
                    if (apiKey != null) {
                        fetchRouteData(origin, destination, apiKey);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to retrieve API key", e));
    }


    private void fetchRouteData(LatLng origin, LatLng destination, String apiKey) {
        if (executorService == null || executorService.isShutdown()) {
            Log.e(TAG, "ExecutorService is not available");
            return;
        }

        String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                origin.latitude, origin.longitude, destination.latitude, destination.longitude, apiKey);

        try {
            executorService.execute(() -> {
                try {
                    String response = makeHttpRequest(url);
                    if (isAdded()) {
                        processRouteResponse(response);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching route", e);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Task rejected", e);
        }
    }

    private String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private void processRouteResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray routes = jsonResponse.getJSONArray("routes");

            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject leg = route.getJSONArray("legs").getJSONObject(0);

                String distance = leg.getJSONObject("distance").getString("text");
                String duration = leg.getJSONObject("duration").getString("text");

                List<LatLng> path = decodePoly(route.getJSONObject("overview_polyline").getString("points"));

                requireActivity().runOnUiThread(() -> {
                    updateRouteOnMap(path);
                    updateRouteInfo(distance, duration);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing route response", e);
        }
    }

    private void updateRouteOnMap(List<LatLng> path) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        PolylineOptions options = new PolylineOptions()
                .addAll(path)
                .color(requireContext().getColor(R.color.colorAccent))
                .width(20f);
        currentPolyline = googleMap.addPolyline(options);
    }

    private void updateRouteInfo(String distance, String duration) {
        routeDistanceText.setText(String.format("Distance: %s", distance));
        routeEtaText.setText(String.format("ETA: %s", duration));
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            poly.add(new LatLng(lat / 1e5, lng / 1e5));
        }
        return poly;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (isMapReady) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        if (executorService != null) {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            } finally {
                executorService = null;
            }
        }
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
