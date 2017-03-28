package tddd82.healthcare;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng[] pinList;
    private String[] damageList;
    private Context context;
    private HashMap<String, String> groupMap;
    private HashMap<Marker, String> markerMap;
    private GetGroupTask getGroupTask;
    private GetMapPinsTask getMapPinsTask;
    private String[] groups;
    private SupportMapFragment mapFragment;
    private JSONArray markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        getGroupTask = new GetGroupTask(this, this);
        getGroupTask.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/groups");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void requestMapPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            Log.d("PERMISSION","No permission for location");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestMapPermission();
        mMap.setOnMapLongClickListener(addPins);
        mMap.setOnMarkerClickListener(onMarkerClickListener);

        getMapPinsTask = new GetMapPinsTask(this, mMap, this);
        getMapPinsTask.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/pins");

        Log.d("sydney", "sydney");
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //LatLng myPosition = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /*public void addPinsToMap(GoogleMap mMap){
        for(int i=0; i < pinList.length; i++){
            mMap.addMarker(new MarkerOptions().position(pinList[i]).title(damageList[i]));
        }
    }*/

    public void addPinsToMap(GoogleMap mMap){
        mMap.clear();
        markerMap = new HashMap<>();

        for(int i=0; i < markers.length(); i++) {
            try {
                Marker marker;
                JSONObject row = markers.getJSONObject(i);
                marker = mMap.addMarker(new MarkerOptions().position((LatLng)row.get("latlng")).title(row.getString("type")));
                markerMap.put(marker, row.getString("id"));
            }catch (Exception e){
                Log.d("AddPinsToMap", e.getMessage());
            }
        }
    }

    public void setMapPinsList(LatLng[] mapPinsList, String[] typeOfDamageList) {
        pinList = mapPinsList;
        damageList = typeOfDamageList;
    }
    public void setMarkerList(JSONArray markerList){
        this.markers = markerList;
    }


    GoogleMap.OnMapLongClickListener addPins = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {

            Bundle bundle = new Bundle();
            bundle.putStringArray("groupArray", groups);
            bundle.putDouble("longitude", latLng.longitude);
            bundle.putDouble("latitude", latLng.latitude);

            FireMissilesDialogFragment fire= new FireMissilesDialogFragment();
            fire.setArguments(bundle);
            fire.show(getFragmentManager(),"AddPin");
        }
    };

    GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle bundle = new Bundle();
            bundle.putString("id", markerMap.get(marker));
            bundle.putDouble("longitude", marker.getPosition().longitude);
            bundle.putDouble("latitude", marker.getPosition().latitude);

            DeleteMarkerDialog delete = new DeleteMarkerDialog();
            delete.setArguments(bundle);
            delete.show(getFragmentManager(), "DELETE");
            return false;
        }
    };

    public void updatePinsOnMap(){
        new GetMapPinsTask(this, mMap, this).execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/pins");
    }

    public void setGroupArray(String[] newgroups){
        groups = newgroups;
    }
    public void deletePin(LatLng pin){

    }
}
