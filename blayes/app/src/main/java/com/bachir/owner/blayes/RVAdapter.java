package com.bachir.owner.blayes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bachir on 5/27/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TripViewHolder> {

    List<com.bachir.owner.blayes.Trip> trips;
    Context here;

    RVAdapter(List<com.bachir.owner.blayes.Trip> trips, Context var){
        this.trips = trips;
        here = var;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        TripViewHolder pvh = new TripViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
        String var = trips.get(i).getUserid();
        SharedPreferences pref = here.getSharedPreferences("MyPref", MODE_PRIVATE);
        final String mUserEmail = pref.getString("user", "");
        final String useremail = Utils.encodeEmail(mUserEmail);
        HashMap<String, HashMap<String, String>> pen_hash = trips.get(i).getPen_list();
        HashMap<String, HashMap<String, String>> book_hash = trips.get(i).getBook_list();


        if (pen_hash != null && pen_hash.containsKey(useremail)) {
            tripViewHolder.statusSent.setVisibility(LinearLayout.VISIBLE);
        }
        else if (book_hash != null && book_hash.containsKey(useremail)) {
            tripViewHolder.statusConf.setVisibility(LinearLayout.VISIBLE);
        }


        if (mUserEmail.equals(var)) {
            tripViewHolder.cv.setCardBackgroundColor(Color.CYAN);
            tripViewHolder.tripItem.setBackgroundColor(Color.CYAN);

        }
        Log.i("email 1: ", mUserEmail); Log.i("email 2: ", var);

        tripViewHolder.departure.setText(trips.get(i).getDeparture());
        tripViewHolder.destination.setText(trips.get(i).getDestination());
        tripViewHolder.driver.setText(trips.get(i).getDriver());
        tripViewHolder.time.setText(Trip.gToString(trips.get(i).getDatetime(),1));
        tripViewHolder.id = trips.get(i).getId();
        tripViewHolder.creator = trips.get(i).getUserid();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    public static class TripViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        LinearLayout tripItem, statusSent, statusConf;
        TextView departure;
        TextView destination;
        TextView time;
        TextView driver;
        String id;
        String creator;
        Button info;


        TripViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tripItem = (LinearLayout) itemView.findViewById(R.id.trip);
            departure = (TextView)itemView.findViewById(R.id.departure);
            destination = (TextView)itemView.findViewById(R.id.destination);
            driver = (TextView)itemView.findViewById(R.id.driver);
            time = (TextView) itemView.findViewById(R.id.time);
            info = (Button) itemView.findViewById(R.id.info);

            statusSent = (LinearLayout) itemView.findViewById(R.id.sent);
            statusConf = (LinearLayout) itemView.findViewById(R.id.conf);

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
                    info.startAnimation(myAnim);
                    //Snackbar.make(view, "Info: " + id, Snackbar.LENGTH_LONG)
                     //       .setAction("Action", null).show();
                    SharedPreferences pref = view.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();

                        editor.putString("id", id);
                        editor.putString("userkey", creator);  // Saving string
                        editor.commit();
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        view.getContext().startActivity(intent);

                }
            });


        }
        }
    }

