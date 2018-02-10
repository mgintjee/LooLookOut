package com.happybananastudio.loolookout;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

/**
 * Created by mgint on 2/9/2018.
 */
public class customInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public customInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        infoWindowData info = (infoWindowData) marker.getTag();
        TextView tVGender = (TextView) view.findViewById(R.id.tV_Gender);
        TextView tVSize = (TextView) view.findViewById(R.id.tV_Size);
        TextView tVClean = (TextView) view.findViewById(R.id.tV_Clean);
        TextView tVTraffic = (TextView) view.findViewById(R.id.tV_Traffic);
        TextView tVAccess = (TextView) view.findViewById(R.id.tV_Access);
        TextView tVClosing = (TextView) view.findViewById(R.id.tV_Closing);
        TextView tVAmenities = (TextView) view.findViewById(R.id.tV_Amenities);

        String gender = info.getGender();
        String size = info.getSize();
        String clean = info.getClean();
        String traffic = info.getTraffic();
        String access = info.getAccess();
        String closing = info.getClosing();
        int count = info.getAmenityCount();
        String spacer = String.format("%0" + count + "d", 0).replace("0", "\n");
        String amenities = info.getAmenities() + spacer;

        tVGender.setText(gender);
        tVSize.setText(size);
        tVClean.setText(clean);
        tVTraffic.setText(traffic);
        tVAccess.setText(access);
        tVClosing.setText(closing);
        tVAmenities.setText(amenities);

        return view;
    }
}
