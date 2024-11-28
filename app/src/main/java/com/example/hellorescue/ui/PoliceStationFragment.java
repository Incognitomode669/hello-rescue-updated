package com.example.hellorescue.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.hellorescue.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PoliceStationFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private final LatLng targetLocation = new LatLng(10.07991, 124.34261);
    private boolean followUserMode = false;

    private TextView routeDistanceText;
    private TextView routeEtaText;
    private Button followUserButton;
    private Button refreshRouteButton;
    private LocationCallback locationCallback;
    private LatLng currentUserLocation;

    // Declare markers for user and target location
    private Marker userLocationMarker;
    private Marker targetLocationMarker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.police_station, container, false);

        // Initialize UI Elements
        mapView = view.findViewById(R.id.police_map_view);
        routeDistanceText = view.findViewById(R.id.route_distance);
        routeEtaText = view.findViewById(R.id.route_eta);
        followUserButton = view.findViewById(R.id.btn_follow_user);
        refreshRouteButton = view.findViewById(R.id.btn_refresh_route);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ImageButton backButton = view.findViewById(R.id.police_station_back);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });

        followUserButton.setOnClickListener(v -> {
            followUserMode = !followUserMode;
            if (followUserMode) {

                followUserButton.setText("Stop Following");
                getCurrentLocation();
            } else {
                followUserButton.setText("Follow User");
                if (currentUserLocation != null) {
                    // Update camera to currentUserLocation
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(currentUserLocation)
                                    .zoom(15)
                                    .tilt(0)  // Reset tilt to 0 degrees (top-down view) if not following.
                                    .build()
                    ));
                }
            }
        });


        // Optional: Remove unnecessary manual refresh functionality if auto-synchronization is sufficient
        refreshRouteButton.setOnClickListener(v -> {
            // Only required if manual route refresh is needed, otherwise, this can be omitted
            if (currentUserLocation != null) {
                drawRoute(currentUserLocation, targetLocation);
            }
        });

        // In locationCallback, ensure synchronization of user location updates and route drawing
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Update user location on map
                    updateUserLocationOnMap(currentUserLocation);

                    // Synchronize route drawing
                    drawRoute(currentUserLocation, targetLocation);
                }
            }
        };




        return view;
    }

    // Use the same interval settings for both interval and fastest interval
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)  // Sync update interval with route updates
                .setFastestInterval(2000);  // Fastest interval should match

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        BitmapDescriptor TargetLocation = BitmapDescriptorFactory.fromResource(R.drawable.target_location);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        targetLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(targetLocation)
                .title("Destination")
                .icon(TargetLocation));

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            BitmapDescriptor UserLocation = BitmapDescriptorFactory.fromResource(R.drawable.user_location);
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                if (userLocationMarker == null) {
                    userLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("Your Location")
                            .icon(UserLocation));
                } else {
                    userLocationMarker.setPosition(userLocation);
                }

                if (followUserMode) {
                    // Get the next point on the route
                    LatLng nextPoint = getNextRoutePoint(userLocation, targetLocation);

                    // Calculate bearing between current and next point
                    float bearing = calculateBearing(userLocation, nextPoint);

                    // Animate the camera to face the direction of travel
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(userLocation)
                                    .zoom(18) // Adjust zoom level for better visualization
                                    .bearing(bearing) // Set the calculated bearing
                                    .tilt(90) // Tilt for a more realistic 3D perspective
                                    .build()
                    ));
                }

                // Refresh the route, distance, and ETA whenever the user location changes
                drawRoute(userLocation, targetLocation);
            }
        });
    }


    private void updateUserLocationOnMap(LatLng userLocation) {
        if (userLocationMarker == null) {
            userLocationMarker = googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        } else {
            userLocationMarker.setPosition(userLocation);
        }

        if (followUserMode) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(userLocation)
                            .zoom(18)
                            .tilt(90)
                            .build()
            ));
        }
    }

    private float calculateBearing(LatLng start, LatLng end) {
        double lat1 = Math.toRadians(start.latitude);
        double lng1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double lng2 = Math.toRadians(end.longitude);

        double deltaLng = lng2 - lng1;

        double x = Math.sin(deltaLng) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLng);

        return (float) (Math.toDegrees(Math.atan2(x, y)) + 360) % 360; // Normalize to 0-360
    }


    private LatLng getNextRoutePoint(LatLng currentLocation, LatLng targetLocation) {
        // For simplicity, return the target location as the next point in this example.
        // In a real-world scenario, calculate the closest or next point along the polyline route.
        return targetLocation;
    }

    private Polyline currentPolyline;
    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyCDxZSvkL9P-bAqxgbURYckriIWaaldCsM";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude + "&key=" + apiKey;

        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray routes = jsonResponse.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                    List<LatLng> path = new ArrayList<>();
                    JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);

                    // Extracting distance and duration
                    String distance = leg.getJSONObject("distance").getString("text");
                    String duration = leg.getJSONObject("duration").getString("text");

                    // Update distance and ETA
                    requireActivity().runOnUiThread(() -> {
                        routeDistanceText.setText("Distance: " + distance);
                        routeEtaText.setText("ETA: " + duration);
                    });

                    // Decoding polyline for the route path
                    for (int i = 0; i < steps.length(); i++) {
                        String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                        path.addAll(decodePolyline(polyline));
                    }

                    // Update the polyline (only if it's not already the same)
                    requireActivity().runOnUiThread(() -> {
                        if (currentPolyline != null) {
                            currentPolyline.remove();  // Remove the previous polyline
                        }
                        currentPolyline = googleMap.addPolyline(new PolylineOptions()
                                .addAll(path)
                                .color(getResources().getColor(R.color.colorAccent))
                                .width(20f));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<LatLng> decodePolyline(String encoded) {
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
            lat += (result & 0x1) != 0 ? ~(result >> 1) : (result >> 1);

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 0x1) != 0 ? ~(result >> 1) : (result >> 1);

            poly.add(new LatLng(((double) lat / 1E5), ((double) lng / 1E5)));
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
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



}