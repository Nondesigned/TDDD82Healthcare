package tddd82.healthcare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class DeleteMarkerDialog extends DialogFragment {

    private ArrayAdapter<String> spinAdapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_pin_dialog, null);
        builder.setView(view);


        final String pinId = new String(getArguments().getString("id"));
        final String pinType = new String(getArguments().getString("type"));
        final LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        TextView latlngTextView = (TextView)view.findViewById(R.id.LatlngDelete);
        TextView typeTextview = (TextView)view.findViewById(R.id.idView);
        typeTextview.setText("Accident: " + pinType);
        latlngTextView.setText("Latitude: " + String.valueOf(latLng.latitude) +"\n"+ "Longitude: " + String.valueOf(latLng.longitude));

        builder.setMessage(R.string.dialog_delete_pin)
                .setPositiveButton(R.string.DeletePin, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        DeleteMarkerTask deleteMarkerTask = new DeleteMarkerTask(view.getContext(), pinId, (MapsActivity) getActivity());
                        deleteMarkerTask.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/deletepin");
                    }
                })
                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}