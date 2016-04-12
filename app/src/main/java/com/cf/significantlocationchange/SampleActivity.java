package com.cf.significantlocationchange;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.cf.significantlocationlib.SignificantLocationChange;

public class SampleActivity extends Activity implements SignificantLocationChange.SignificantLocation{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        /*Start Receiving Updates*/
        SignificantLocationChange.sharedInstance().startReceivingUpdates(SampleActivity.this);


    }

    @Override
    public void onLocationChanged(Location mLocation) {

        System.out.println("Updated Location : " + mLocation);

        Intent mIntent_content = new Intent(this, SampleActivity.class);
        mIntent_content.setAction(Long.toString(System.currentTimeMillis()));
        mIntent_content.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent mContentIntent = PendingIntent.getActivity(this, 10, mIntent_content, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(SampleActivity.this)
                        .setAutoCancel(true)
                        .setContentIntent(mContentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("Location Changed")
                        .setContentTitle("Significant Location Library")
                        .setContentText("Lat : "+mLocation.getLatitude() +" Long : "+mLocation.getLongitude());
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(10, mBuilder.build());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*Stop ReceivingUpdates*/
        SignificantLocationChange.sharedInstance().stopReceivingUpdates(SampleActivity.this);

    }
}
