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
public class PLAdapter extends RecyclerView.Adapter<PLAdapter.TripViewHolder> {

    List<HashMap<String, HashMap<String, String>>> trips;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();


    PLAdapter(List<HashMap<String, HashMap<String, String>>> trips){
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pen_item, viewGroup, false);
        TripViewHolder pvh = new TripViewHolder(v, viewGroup.getContext());
        return pvh;
    }

    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
        tripViewHolder.trip = trips.get(i);
        Log.d(LOG_TAG, "item retrieved is: " + tripViewHolder.trip);
        for ( String key : tripViewHolder.trip.keySet() ) {

            tripViewHolder.email = key;
            tripViewHolder.token = tripViewHolder.trip.get(key).get("token");
            //tripViewHolder.refToken = tripViewHolder.refToken.child(key).child("token");
            tripViewHolder.name1 = tripViewHolder.trip.get(key).get("name");
            tripViewHolder.name.setText("Nom: " + tripViewHolder.name1);

            try {
            tripViewHolder.departure = tripViewHolder.trip.get(key).get("departure");
            tripViewHolder.destination = tripViewHolder.trip.get(key).get("destination");
            tripViewHolder.seatNum = tripViewHolder.trip.get(key).get("seats");
            tripViewHolder.contact = tripViewHolder.trip.get(key).get("contact");
            tripViewHolder.seatN = Integer.parseInt(tripViewHolder.seatNum);
            tripViewHolder.dep.setText("De: " + tripViewHolder.departure);
            tripViewHolder.des.setText("À: " + tripViewHolder.destination);
            tripViewHolder.seat.setText("Places: " + tripViewHolder.seatNum);


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
        //Integer index;
        CardView cv;
        String email;
        String name1, departure, destination, seatNum, contact;
        TextView name, seat, dep, des;
        Button accept, decline, call;
        Integer seatN = 1;
        String token = "";
        Firebase refToken = new Firebase(Constants.FIREBASE_URL_USERS);

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
            accept = (Button) itemView.findViewById(R.id.btnAccept);
            decline = (Button) itemView.findViewById(R.id.btnDecline);
            call = (Button) itemView.findViewById(R.id.btnCall);
            final SharedPreferences pref = itemView.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            final String id = pref.getString("id", null);
            final Integer penNum = pref.getInt("pending", 0);
            final Integer bookNum = pref.getInt("booked", 0);
            final Integer remNum = pref.getInt("remaining", 0);
            //final Integer totalNum = pref.getInt("total", 0) - 1;

            //final String mUserEmail = pref.getString("user", null);
            //final String useremail = Utils.encodeEmail(mUserEmail);
            //final String username = pref.getString("name", null);
            //penNum -= 1; bookNum += 1;
            //Log.e("pen eamil: ", email);

            final Firebase ref1 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("pen_list");
            final Firebase ref2 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("book_list");
            final Firebase ref3 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("pending");
            final Firebase ref4 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("booked");
            //final Firebase ref5 = new Firebase(Constants.FIREBASE_URL_TRIPS).child(id).child("seat");

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Integer var = seatN -3;
                    if ((remNum - seatN) >= 0) {
                        HashMap<String, String> pen2 = new HashMap<>();
                        pen2.put("name", name1);
                        pen2.put("departure", departure);
                        pen2.put("destination", destination);
                        pen2.put("seats", seatNum);
                        pen2.put("contact", contact);
                        pen2.put("token", token);
                        HashMap<String, HashMap<String, String>> pen = new HashMap<>();
                        pen.put(email, pen2);
                        bookk.put("post", id);
                        bookk.put("token", token);
                        ref1.child(email).removeValue();
                        ref2.child(email).setValue(pen2);
                        ref3.setValue(penNum - seatN);
                        ref4.setValue(bookNum + seatN);
                        //ref5.setValue(totalNum);
                        refNot.child("confirm").setValue(null);
                        refNot.child("confirm").setValue(bookk);
                        Snackbar.make(view, "Accepté", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else {
                        Snackbar.make(view, "Pas assez de places restantes!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    HashMap<String, String> pen2 = new HashMap<>();
                    pen2.put("name",name1);
                    pen2.put("token",token);
                    HashMap<String, HashMap<String, String>> pen = new HashMap<>();
                    pen.put(email,pen2);
                    bookk.put("post", id);
                    bookk.put("token", token);
                    ref1.child(email).removeValue();
                    ref3.setValue(penNum - seatN);
                    refNot.child("reject").setValue(null);
                    refNot.child("reject").setValue(bookk);
                    Snackbar.make(view, "Rejeté", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (contact != null) {
                            Log.i(LOG_TAG, "Phone number: " + contact);
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + contact));
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

        public void accept(Firebase ref, String username) {

            //ref.child("pending").setValue(post.getPending());
            //ref.child("pen_list").setValue(post.getPen_list());
        }

        public void getToken(Firebase ref) {
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

