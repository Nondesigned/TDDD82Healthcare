package tddd82.healthcare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddPinDialogFragment extends DialogFragment {

    private ArrayAdapter<String> spinAdapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_pin_dialog, null);
        builder.setView(view);

        String[] groupArray = getArguments().getStringArray("groupArray");
        final LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(groupArray));

        final Spinner groupSpin = (Spinner)view.findViewById(R.id.groups);
        spinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpin.setAdapter(spinAdapter);
        TextView latlngTextView = (TextView)view.findViewById(R.id.Latlng);
        latlngTextView.setText("Latitude: " + String.valueOf(latLng.latitude) +"\n" +"Longitude: " + String.valueOf(latLng.longitude));

        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES! (Adding pins to map)
                        AddPinsToMapTask addPinsToMapTask = new AddPinsToMapTask(view.getContext(), latLng, String.valueOf(groupSpin.getSelectedItem()) , (MapsActivity) getActivity());
                        addPinsToMapTask.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/pins", "new pin");


                        /*JSONArray unLoadedPins = CacheManager.getJSON("/localPins", getContext());
                        CacheManager.clear("/localPins", getContext());
                        for (int i = 0; i<unLoadedPins.length(); i++){
                            try {
                                JSONObject p = unLoadedPins.getJSONObject(i);
                                AddPinsToMapTask addPinsToMapTaskLoop = new AddPinsToMapTask(view.getContext(), (MapsActivity) getActivity());
                                addPinsToMapTaskLoop.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/pins", p.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}