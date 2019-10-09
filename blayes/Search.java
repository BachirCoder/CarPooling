package com.bachir.owner.blayes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.bachir.owner.blayes.Constants.commune;
import static com.bachir.owner.blayes.Constants.wilaya;
import static com.bachir.owner.blayes.Constants.wilaya0;

public class Search extends AppCompatActivity implements View.OnClickListener {

    private Button dep, des, search, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dep = (Button) findViewById(R.id.dep);
        des = (Button) findViewById(R.id.des);
        search = (Button) findViewById(R.id.search);
        cancel = (Button) findViewById(R.id.cancel);
        dep.setOnClickListener(this);
        des.setOnClickListener(this);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == dep) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez votre départ (Wilaya)");
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder2.setTitle("Choisissez votre départ (Commune)");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]

                    final int var = which;
                    builder2.setItems(commune[var], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) dep.setText(wilaya0[var]);
                            else dep.setText(wilaya0[var] + " (" + commune[var][which] + ")");
                        }
                    });
                    builder2.show();

                }
            });
            builder.show();

        }

        if (v == des) {
            //final CharSequence dest[] = new CharSequence[] {"Alger", "Oran", "Annaba", "Ghardaïa", "Blida", "Batna", "Sétif", "Laghouat", "Tlemcen", "Tiaret", "Constantine"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder.setTitle("Choisissez votre destination (Wilaya)");
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Wallpaper);
            builder2.setTitle("Choisissez votre destination (Commune)");
            builder.setItems(wilaya, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]

                    final int var = which;
                    builder2.setItems(commune[var], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) des.setText(wilaya0[var]);
                            else des.setText(wilaya0[var] + " (" + commune[var][which] + ")");
                        }
                    });
                    builder2.show();

                }
            });
            builder.show();
        }

        if (v == search) {
            search(dep, des);
        }

        if (v == cancel) {
            Intent intent = new Intent(Search.this, TripList.class);
            startActivity(intent);
        }
    }

    public void search(Button departure, Button destination) {

        String dep, des;
        dep = departure.getText().toString();
        des = destination.getText().toString();
        if (!dep.equals("Départ") && !des.equals("Destination")) {
            Intent intent = new Intent(Search.this, TripList.class);
            intent.putExtra("departure", dep);
            intent.putExtra("destination", des);
            startActivity(intent);
        }
        else Toast.makeText(Search.this, "Entrez votre départ et destination!", Toast.LENGTH_LONG).show();
    }
}
