package com.ut.firebaseejemplo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ut.firebaseejemplo.Adapters.CoordinatesAdapter;
import com.ut.firebaseejemplo.Classes.Coordinates;
import com.ut.firebaseejemplo.R;

import java.util.ArrayList;

public class FirebaseList extends AppCompatActivity {

    ListView listView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "FirebaseList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_list);

        listView = findViewById(R.id.listviewCoordinates);

        loadCoordinates();
    }

    private void loadCoordinates() {
        db.collection("Coordinates")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    ArrayList<Coordinates> arrayList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, String.valueOf(document.get("latitude")) + "-" + document.getId());

                        arrayList.add(new Coordinates(
                             document.getId(),
                             document.getDate("date"),
                             Float.parseFloat(String.valueOf(document.get("latitude"))),
                             Float.parseFloat(String.valueOf(document.get("longitude")))
                        ));
                    }

                    CoordinatesAdapter coordinatesAdapter =
                            new CoordinatesAdapter(arrayList, FirebaseList.this, listView);
                    listView.setAdapter(coordinatesAdapter);

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}