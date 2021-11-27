package com.ut.firebaseejemplo.Adapters;

import android.database.DataSetObserver;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.firebaseejemplo.Activities.FirebaseList;
import com.ut.firebaseejemplo.Classes.Coordinates;
import com.ut.firebaseejemplo.R;

import java.util.ArrayList;

public class CoordinatesAdapter implements ListAdapter {

    ArrayList<Coordinates> arrayList;
    FirebaseList firebaseList;
    ListView listView;
    FirebaseFirestore db;

    public CoordinatesAdapter(ArrayList<Coordinates> arrayList,
                              FirebaseList firebaseList, ListView listView) {

        this.arrayList = arrayList;
        this.firebaseList = firebaseList;
        this.listView = listView;

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){

            LayoutInflater layoutInflater = LayoutInflater.from(firebaseList);
            convertView = layoutInflater.inflate(R.layout.coordinates_list_item, null);

            TextView latitudeAndLongitude = convertView.
                    findViewById(R.id.txtLatitudeAndLongitude);

            TextView date = convertView.findViewById(R.id.txtDate);

            latitudeAndLongitude.setText("" + arrayList.get(position).getLatitude()
            + ", " + arrayList.get(position).getLongitude());

            java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(firebaseList);
            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(firebaseList);


            date.setText("" + dateFormat.format(arrayList.get(position).getDate())
                    + " " + timeFormat.format(arrayList.get(position).getDate()));

            ImageButton delete = convertView.findViewById(R.id.ibtnDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Coordinates")
                            .document(arrayList.get(position).getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            arrayList.remove(position);
                            if (arrayList.size() > 0){
                                CoordinatesAdapter coordinatesAdapter =
                                        new CoordinatesAdapter(arrayList, firebaseList, listView);
                                listView.setAdapter(coordinatesAdapter);
                            } else {
                                listView.invalidateViews();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(firebaseList,
                                    "Ocurrio un error al eliminar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Los envie a Google Maps
                }
            });

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
