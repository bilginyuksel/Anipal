package c.bilgin.anipal.Ui;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import c.bilgin.anipal.Adapters.HomePageAdapter;
import c.bilgin.anipal.Model.FedAnimalLocation;
import c.bilgin.anipal.R;

public class MapsFragment extends Fragment  {

    private static MapsFragment instance = new MapsFragment();
    private MapsFragment(){}

    public static MapsFragment getInstance(){
        return instance;
    }

    private GoogleMap googleMap;
    private MapView mMapView;
    private ImageButton buttonBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps,null);
        mMapView = rootView.findViewById(R.id.mapView);
        this.buttonBack = rootView.findViewById(R.id.imageButtonBack);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.getInstance().pager.setCurrentItem(0);
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                // googleMap.setMyLocationEnabled(true);
                // System.out.println("Latitude : "+googleMap.getMyLocation().getLatitude());

                // For dropping a marker at a point on the Map
                LatLng ankara = new LatLng(39, 35);
                // add markers with the data which we get from server

                FirebaseDatabase.getInstance().getReference("FedAnimalLocations").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot sp : dataSnapshot.getChildren()){
                            FedAnimalLocation loc = sp.getValue(FedAnimalLocation.class);
                            double latitude = loc.getLatitude();
                            double longitude = loc.getLongitude();
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude))
                                    .title(loc.getFirstName()).snippet(loc.getDescription()))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pati));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ankara).zoom(4).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
