package com.cf.significantlocationlib;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class GeofenceRequester implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Storage for a reference to the calling client
    private final Context mActivity;
    // Stores the PendingIntent used to send geofence transitions back to the app
    private PendingIntent mGeofencePendingIntent;
    // Stores the current list of geofences
    private ArrayList<Geofence> mCurrentGeofences;
    private List<String> mGeofenceRequestIds;
    private GoogleApiClient mApiClient;
    public static int mode = 0;
    /*
     * Flag that indicates whether an add or remove request is underway. Check this
     * flag before attempting to start a new request.
     */
    private boolean mInProgress;

    public GeofenceRequester(Context activityContext) {

        // Save the context
        mActivity = activityContext;
        // Initialize the globals to null
        mGeofencePendingIntent = null;
        mInProgress = false;

        if (!isGooglePlayServicesAvailable()) {
            return;
        }

        mApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     *
     * @param flag Turn the in progress flag on or off.
     */
    public void setInProgressFlag(boolean flag) {
        // Set the "In Progress" flag.
        mInProgress = flag;
    }

    /**
     * Get the current in progress status.
     *
     * @return The current value of the in progress flag.
     */
    public boolean getInProgressFlag() {
        return mInProgress;
    }

    /**
     * Returns the current PendingIntent to the caller.
     *
     * @return The PendingIntent used to create the current set of geofences
     */
    public PendingIntent getRequestPendingIntent() {
        return createRequestPendingIntent();
    }

    /**
     * Start adding geofences. Save the geofences, then start adding them by requesting a
     * connection
     *
     * @param geofences A List of one or more geofences to add
     */
    public void addGeofences(List<Geofence> geofences) throws UnsupportedOperationException {

        /*
         * Save the geofences so that they can be sent to Location Services once the
         * connection is available.
         */
        mode = 1;
        mCurrentGeofences = new ArrayList<Geofence>();
        mCurrentGeofences.addAll(geofences);

        // Toggle the flag and continue
        mInProgress = true;

        // Request a connection to Location Services
        requestConnection();


    }
    /**
     * Request a connection to Location Services. This call returns immediately,
     * but the request is not complete until onConnected() or onConnectionFailure() is called.
     */
    int i = 10;
    private void requestConnection() {
        i = 31;
        if (!mApiClient.isConnected())
            mApiClient.connect();
    }

    /**
     * Once the connection is available, send a request to add the Geofences
     */
    private void continueAddGeofences() {

        /*if (!PermissionU.havePermissions(PermissionUtils.locationPermissions()))
            return;*/

        // Get a PendingIntent that Location Services issues when a geofence transition occurs
        mGeofencePendingIntent = createRequestPendingIntent();

        // Send a request to add the current geofences
        //        mLocationClient.addGeofences(mCurrentGeofences, mGeofencePendingIntent, this);

        GeofencingRequest geofenceRequest = new GeofencingRequest.Builder().addGeofences(mCurrentGeofences).build();

        //        LocationServices.GeofencingApi.addGeofences(mApiClient,mCurrentGeofences,mGeofencePendingIntent);

        try {

            PendingResult<Status> result;

            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            result = LocationServices.GeofencingApi.addGeofences(mApiClient, geofenceRequest, mGeofencePendingIntent);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                    String[] geofenceRequestIds = new String[mCurrentGeofences.size()];
                    for (int index = 0; index < mCurrentGeofences.size(); index++) {
                    geofenceRequestIds[index] = mCurrentGeofences.get(index).getRequestId();
                       }


                        requestDisconnection();
                    }
                });

        }catch (Exception e){

        }

        // Disconnect the location client

    }

    /**
     * Get a location client and disconnect from Location Services
     */
    private void requestDisconnection() {

        // A request is no longer in progress
        mInProgress = false;
        mApiClient.disconnect();
    }
    /*
     * Called by Location Services once the location client is connected.
     *
     * Continue by adding the requested geofences.
     */
    @Override
    public void onConnected(Bundle arg0) {

        mInProgress = false;
    	if(mode== 1){
            continueAddGeofences();    		
    	}else if(mode == 2){
    		continueRemoveGeofences();
    	}
    }
    /**
     * Get a PendingIntent to send with the request to add Geofences. Location Services issues
     * the Intent inside this PendingIntent whenever a geofence transition occurs for the current
     * list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent createRequestPendingIntent() {

        // If the PendingIntent already exists
        if (null != mGeofencePendingIntent) {

            // Return the existing intent
            return mGeofencePendingIntent;

        // If no PendingIntent exists
        } else {

            // Create an Intent pointing to the IntentService
            Intent intent = new Intent("com.dubaipolice.app.ACTION_RECEIVE_GEOFENCE");
            /*
             * Return a PendingIntent to start the IntentService.
             * Always create a PendingIntent sent to Location Services
             * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
             * again updates the original. Otherwise, Location Services
             * can't match the PendingIntent to requests made with it.
             */
            return PendingIntent.getBroadcast(
                    mActivity,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /*
     * Implementation of OnConnectionFailedListener.onConnectionFailed
     * If a connection or disconnection request fails, report the error
     * connectionResult is passed in from Location Services
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Turn off the request flag
        mInProgress = false;

        if (connectionResult.hasResolution()) {

        } else {

        }
    }
    
    public void removeGeofences(List<String> geofenceRequestIds) throws UnsupportedOperationException {
    	mGeofenceRequestIds = geofenceRequestIds;
        mode =2;
        mInProgress = false;
        if (!mInProgress) {
            mInProgress = true;
            requestConnection();
        } else {
            throw new UnsupportedOperationException();
        }

	}
    
    private void continueRemoveGeofences(){

       PendingResult<Status> pendingIntent = LocationServices.GeofencingApi.removeGeofences(mApiClient,mGeofenceRequestIds);
       pendingIntent.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess())
                {

                }
            }
        });

        requestDisconnection();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofencePendingIntent) {
//            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofencePendingIntent);
        }
    }

    /**
     * Checks if Google Play services is available.
     * @return true if it is.
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
//            Log.e(TAG, "Google Play services is unavailable.");
            return false;
        }
    }

}
