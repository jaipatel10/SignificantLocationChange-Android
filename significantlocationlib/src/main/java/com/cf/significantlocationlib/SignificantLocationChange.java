package com.cf.significantlocationlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

/**
 * Created by dp1patel on 4/5/16.
 */

public class SignificantLocationChange {


    /*Global Variable Declaration*/
    private Activity mContext;
    private static SignificantLocationChange mChanger;
    public SignificantLocation mSignificantLocation;


    public static SignificantLocationChange sharedInstance(){

        if(mChanger == null)
           mChanger = new SignificantLocationChange();

        return  mChanger;

    }

     public void startReceivingUpdates(Activity mContext){

         /*Init Context*/
         this.mContext = mContext;
         /*Initialization of SignificantLocationChange*/
         registerClient(mContext);
         configureSignificant(mContext);


    }

    public void stopReceivingUpdates(Activity mContext){

         /*Remove location updates from SignificantLocationChange*/
        removeSignificatArea(mContext);

    }

    private void configureSignificant(Activity mContext){

        /*Setting initial configuration*/
        /*Get accurate location*/
        Intent mIntent = new Intent(mContext, LocationChangeService.class);
        mContext.startService(mIntent);


    }

    public void setSignificantArea(Context mContext, Location mLocation){

        ArrayList<Geofence> mCurrentFences = new ArrayList<Geofence>();
        GeofenceRequester mRequester = new GeofenceRequester(mContext);
        try {
            if(mLocation!=null) {

                Geofence.Builder builder = new Geofence.Builder();
                builder.setRequestId(String.valueOf("CurrentLocation"));
                builder.setCircularRegion(mLocation.getLatitude(), mLocation.getLongitude(), 500);
                builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
                builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
                mCurrentFences.add(builder.build());
                mRequester.addGeofences(mCurrentFences);
            }

        }catch (Exception e){

        }

    }

    public void removeSignificatArea(Context mContext){

        try{

            ArrayList<String> mRemoveGeofenceIDs = new ArrayList<String>();
            GeofenceRequester mRequester = new GeofenceRequester(mContext);
            mRemoveGeofenceIDs.add("CurrentLocation");
            mRequester.removeGeofences(mRemoveGeofenceIDs);

        }catch (Exception e){

        }

    }

    public interface SignificantLocation {

        void onLocationChanged(Location mLocation);
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity mActivityCallback){

        this.mSignificantLocation = (SignificantLocation)mActivityCallback;

    }




}
