package com.example.litmaps;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.pm.PackageManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.example.litmaps.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isStreetView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if Google Play Services is available
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Toast.makeText(this, "Google Play Services is not available on this device.", Toast.LENGTH_SHORT).show();
                apiAvailability.getErrorDialog(this, resultCode, 0).show();
            } else {
                // If Google Play Services cannot be fixed, show an error message
                Toast.makeText(this, "Google Play Services is not available on this device.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for Kalibaya Park
        LatLng kalibayaPark = new LatLng(-7.111957922571531, 108.79843183833226);
        mMap.addMarker(new MarkerOptions().position(kalibayaPark).title("Kalibaya Park"));

        // Add a marker for Jumbleng Forest Nature Tourism
        LatLng jumblengForest = new LatLng(-7.0979070550374095, 108.79844857415654);
        mMap.addMarker(new MarkerOptions().position(jumblengForest).title("Jumbleng Forest Nature Tourism"));

        // Move camera to a suitable zoom level to show all markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(kalibayaPark);
        builder.include(jumblengForest);
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, padding));

        // Activate user location view and zoom controls
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If location access permission has not been granted, request permission from the user
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            // If location access permission has been granted, enable user location view
            mMap.setMyLocationEnabled(true);
        }

        // Enable compass
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        // Add listener to handle marker click events
        mMap.setOnMarkerClickListener(marker -> {
            // Display a toast message when marker is clicked
            Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();

            // Switch to Street View
            if (!isStreetView) {
                isStreetView = true;
                switchToStreetView(marker.getPosition());
            }
            return true; // consume the event, to prevent default behavior
        });
    }

    private void switchToStreetView(LatLng position) {
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment)
                getSupportFragmentManager().findFragmentById(R.id.street_view_panorama_container);
        if (streetViewPanoramaFragment == null) {
            streetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance(
                    new StreetViewPanoramaOptions().position(position));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.street_view_panorama_container, streetViewPanoramaFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(panorama -> {
                panorama.setPosition(position);
                panorama.setUserNavigationEnabled(true);
            });
        }
    }
}
