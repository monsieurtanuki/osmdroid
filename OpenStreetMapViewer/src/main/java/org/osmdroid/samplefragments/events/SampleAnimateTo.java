package org.osmdroid.samplefragments.events;

import android.widget.Toast;

import org.osmdroid.samplefragments.data.SampleGridlines;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * https://github.com/osmdroid/osmdroid/issues/264
 * extends gridlines to provide visual confirmation
 * Created by alex on 2/22/16.
 */
public class SampleAnimateTo extends SampleGridlines {
    Random rand = new Random();
    Timer t = new Timer();
    boolean alive = true;

    @Override
    public String getSampleTitle() {
        return "Animate To";
    }

    @Override
    public void addOverlays() {
        super.addOverlays();
        mMapView.getController().setZoom(10);

    }

    @Override
    public void onResume() {
        super.onResume();
        alive=true;
        //some explanation here.
        //we using a timer task with a delayed start up to move the map around. during CI tests
        //this fragment can crash the app if you navigate away from the fragment before the initial fire
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runTask();
            }
        };

        t = new Timer();
        t.schedule(task, 1000, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        alive = false;
        if (t != null)
            t.cancel();
        t = null;
    }


    private void runTask() {
        if (!alive)
            return;
        try {
            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rand != null && mMapView != null && getActivity() != null) {
                            final double lat = TileSystem.getRandomLatitude(rand.nextDouble(), TileSystem.MinLatitude);
                            final double lon = TileSystem.getRandomLongitude(rand.nextDouble());
                            mMapView.getController().animateTo(new GeoPoint(lat, lon));
                            Toast.makeText(getActivity(), "Animate to " + SampleMapEventListener.df.format(lat) + "," + SampleMapEventListener.df.format(lon), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        } catch (Throwable ex) {
        }
    }
}
