package co.hollowlog.localrhythm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by michaeldavidholloway on 12/13/14.
 */
public class LandingActivity extends Activity {

    private LocationAgent mLocationAgent;
    private Location mLastLocation;

    private List<Address> mAddresses;
    public double mCurrentLat;
    public double mCurrentLong;
    public String mStreetAddress;
    public String mCityStateZip;
    public String mCityName;

    private LocationReceiver mLocationReceiver = new LocationReceiver(this) {

        @Override
        public void onReceive(Context context, Intent intent){
            super.onReceive(context, intent);
            Location loc = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
            if (loc != null){
                mCurrentLat = loc.getLatitude();
                mCurrentLong = loc.getLongitude();

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    mAddresses = geocoder.getFromLocation(mCurrentLat, mCurrentLong, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mAddresses != null) {
                    mStreetAddress = mAddresses.get(0).getAddressLine(0);
                    mCityStateZip = mAddresses.get(0).getAddressLine(1);
                    mCityName = mCityStateZip.split(",")[0];

                    Toast.makeText(context, "Found location: " + mStreetAddress + ", "
                           + mCityStateZip, Toast.LENGTH_SHORT).show();

                    Intent launchPlayer = new Intent(LandingActivity.this,
                            SpotifyPlayerActivity.class);
                    launchPlayer.putExtra("location", mCityName);
                    startActivity(launchPlayer);
                    finish();
                } else
                    Toast.makeText(LandingActivity.this, "Error finding location...",
                            Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mLocationAgent = LocationAgent.get(this);
        mLocationAgent.startLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(mLocationReceiver, new IntentFilter(LocationAgent.ACTION_LOCATION));
    }

    @Override
    protected void onStop() {
        mLocationAgent.stopLocationUpdates();
        this.unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

}