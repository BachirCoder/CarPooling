package com.bachir.owner.blayes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bachir on 5/27/2016.
 */
public class BLAdapter extends RecyclerView.Adapter<BLAdapter.TripViewHolder> {

    List<HashMap<String, HashMap<String, String>>> trips;
    String token0 = "";
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    BLAdapter(List<HashMap<String, HashMap<String, String>>> trips){
        this.trips = trips;
    }

    @Override
    public int getItemCount() {
        if (trips != null) {
        return trips.size();
        }
        else return 0;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_item, viewGroup, false);
        TripViewHolder pvh = new TripViewHolder(v, viewGroup.getContext());
        return pvh;
    }

    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
        tripViewHolder.trip = trips.get(i);
        Log.d(LOG_TAG, "item retrieved is: " + tripViewHolder.trip);
        for ( String key : tripViewHolder.trip.keySet() ) {
            tripViewHolder.name.setText("Nom: " + tripViewHolder.trip.get(key).get("name"));
            tripViewHolder.email = key;
            tripViewHolder.token = tripViewHolder.trip.get(key).get("token");

            try {
                tripViewHolder.dep.setText("De: " + tripViewHolder.trip.get(key).get("departure"));
                tripViewHolder.des.setText("À: " + tripViewHolder.trip.get(key).get("destination"));
                tripViewHolder.seatNum = tripViewHolder.trip.get(key).get("seats");
                tripViewHolder.seat.setText("Places: " + tripViewHolder.seatNum);
                tripViewHolder.seatN = Integer.parseInt(tripViewHolder.seatNum);
                tripViewHolder.contact = tripViewHolder.trip.get(key).get("contact");

            } catch (Exception e) {
            }

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class TripViewHolder extends RecyclerView.ViewHolder {

        HashMap<String, HashMap<String, String>> trip;
        CardView cv;
        String email, contact, seatNum;
        TextView name, seat, dep, des;
        Button cancel, call;
        Integer seatN = 1;
        String token = "";

        final Firebase refNot = new Firebase(Constants.FIREBASE_URL_NOT);
        HashMap<String, String> bookk = new HashMap<String, String>();

        TripViewHolder(View itemView, Context context) {
            super(itemView);
            final Context var = context;
            cv = (CardView)itemView.findViewById(R.id.cvbook);
            name = (TextView)itemView.findViewById(R.id.name);
            seat = (TextView)itemView.findViewById(R.id.seat);
            dep = (TextView)itemView.findViewById(R.id.departure);
            des = (TextView)itemView.findViewById(R.id.destination);
            cancel = (Button) itemView.findViewById(R.id.btnDecline);
            call = (Button) itemView.findViewById(R.id.btnCall);

            final SharedPreferences pref = itemView.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            final String id = pref.getString("id", null);
            final Integer bookNum = pref.getInt("booked", 0);
            //final Integer totalNum = pref.getInt("total", 0) + 1;

            final Firebase ref1 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("book_list");
            final Firebase ref3 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("booked");
            //final Firebase ref4 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("seat");

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //getToken(refToken);
                    //token = token0;
                    bookk.put("post", id);
                    bookk.put("token", token);
                    ref1.child(email).removeValue();
                    ref3.setValue(bookNum - seatN);
                    //ref4.setValue(totalNum);
                    refNot.child("rejectConfirmation").setValue(null);
                    refNot.child("rejectConfirmation").setValue(bookk);
                    Snackbar.make(view, "Vous avez annulé une réservation.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (contact != null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+contact));
                        var.startActivity(intent);
                        }
                        else Snackbar.make(view, "Le passager n'a pas fourni son numéro de téléphone.", Snackbar.LENGTH_LONG).
                                setAction("Action", null).show();
                    } catch (Exception e) {
                        Snackbar.make(view, "Vous ne pouvez pas faire l'appel.", Snackbar.LENGTH_LONG).
                                setAction("Action", null).show();            }
                }
            });

        }

        public void getToken(Firebase ref) {
            //String token = "";
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        token = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(LOG_TAG, "Error: " + firebaseError.getMessage());
                }
            });
        }

    }


}

