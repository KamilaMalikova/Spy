package com.spy.spydemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class UserLocation {
    private Context context;

    public UserLocation(Context context) {
        this.context = context;
    }

    //Данная функция спрашивает систему о последних координатах,
    // полученных с помощью определения местоположения по сотовым вышкам и по GPS,
    // затем берет самые свежие данные и возвращает их в форме объекта Location.
    Location getLastKnownLocation(Context context) {
        LocationManager lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        android.location.Location locationGPS = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        android.location.Location locationNet = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;
        if (null != locationNet) { NetLocationTime = locationNet.getTime(); }

        Location loc;
        if ( 0 < GPSLocationTime - NetLocationTime ) {
            loc = locationGPS;
        } else {
            loc = locationNet;
        }

        if (loc != null) {
            return loc;
        } else {
            return null;
        }
    }

    //извлечь широту и долготу и записать их в файл внутри приватного каталога приложения:
    public void saveLocationToFile(){
        Location loc = getLastKnownLocation(context);
        String locationFile = context.getApplicationInfo().dataDir + "/location";

        try {
            PrintWriter pw = new PrintWriter(new File(locationFile));
            pw.println(loc.getLatitude() + " " + loc.getLongitude());
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(locationFile, Context.MODE_PRIVATE));
//            outputStreamWriter.write(loc.getLatitude() + " " + loc.getLongitude());
//            outputStreamWriter.close();
            pw.close();
        }
        catch (IOException e) {}
    }
}
