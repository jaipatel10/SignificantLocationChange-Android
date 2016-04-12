package com.cf.significantlocationlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by dp1patel on 4/6/16.
 */
public class SignificantReceiver extends BroadcastReceiver{

    /*Global variable Declaration*/

    public GeofencingEvent mGeofenceEvent;
    public List<Geofence> mList_geofences;
    public int mGeofenceTransition;
    public Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        /*Setting Context*/
        mContext = context;

        try{

            /*Get the GeofenceEvent*/
            mGeofenceEvent = GeofencingEvent.fromIntent(intent);

            if (mGeofenceEvent.hasError()) {

                  return;

            } else {

                  if (mGeofenceEvent == null)

                      return;

                    mList_geofences = mGeofenceEvent.getTriggeringGeofences();
                    mGeofenceTransition = mGeofenceEvent.getGeofenceTransition();

                    /*Check the Geofence ID*/
                    for (Geofence mGeofence : mList_geofences) {

                        System.out.println(":: For geofence ::" + mGeofence.getRequestId());

                        if (mGeofence.getRequestId().equalsIgnoreCase("CurrentLocation")){

                            if(mGeofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                                Intent mIntent = new Intent(mContext, LocationChangeService.class);
                                mContext.startService(mIntent);

                            }

                        }


                    }

            }

        }catch (Exception e){

        }



    }
}
