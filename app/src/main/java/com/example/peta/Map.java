package com.example.peta;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;

public class Map extends AppCompatActivity {
    MapView map;
    Context ctx;

    String URL = ServerUrl.URL + "admin/admin/marker.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = (MapView) findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        Configuration.getInstance().setCacheMapTileCount((short) 12);
        Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
        // Create a custom tile source
        map.setTileSource(new OnlineTileSourceBase("", 1, 20, 512, ".png",
                new String[]{"https://a.tile.osm.org/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(Map.this,String.valueOf("lalala"),Toast.LENGTH_LONG).show();
            }

        });


//        map.
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        GeoPoint startPoint;
        startPoint = new GeoPoint(-8.173579, 113.698025);
        mapController.setZoom(10.0);
        mapController.setCenter(startPoint);
        final Context context = this;

//                        Toast.makeText(Map.this,"tes",Toast.LENGTH_LONG).show();
        map.invalidate();
//        createmarker("Angin","Jember Kota",-8.1440703,113.3350449,ctx.getDrawable(R.drawable.mark_green));
//        createmarker(-8.154144,113.647498);
        get_data();
//        tes();
    }

    public void createmarker(final String bencana, final String lokasi, double lat, double longi, final Drawable icon_marker, final String jam, final String tanggal, final String penyebab, final String akibat, final String material, final String upaya) {
        if (map == null) {
            return;
        }

        Marker my_marker = new Marker(map);
        my_marker.setPosition(new GeoPoint(lat, longi));
        my_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        my_marker.setTitle(bencana+" ("+lokasi+")");
//        my_marker.setIcon(ctx.getDrawable(R.drawable.mark_blue));
        my_marker.setIcon(icon_marker);
        my_marker.setPanToView(true);
        my_marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
//                Toast.makeText(Map.this, bencana+""+lokasi,Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Map.this, informasi.class);startActivity(intent);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Map.this,R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_info,null);
                TextView lab_bencana = bottomSheetView.findViewById(R.id.bencana);
                TextView lab_tanggal = bottomSheetView.findViewById(R.id.tangal);
                TextView lab_jam = bottomSheetView.findViewById(R.id.jam);
                TextView lab_lokasi = bottomSheetView.findViewById(R.id.ket_lokasi);
                ImageView i_lokasi = bottomSheetView.findViewById(R.id.marker);
                i_lokasi.setImageDrawable(icon_marker);
                TextView lab_penyebab = bottomSheetView.findViewById(R.id.ket_penyebab);
                TextView lab_upaya = bottomSheetView.findViewById(R.id.ket_upaya);
                TextView lab_akibat = bottomSheetView.findViewById(R.id.ket_kerugian);
                lab_bencana.setText(bencana);
                lab_tanggal.setText(tanggal);
                lab_jam.setText(jam);
                lab_lokasi.setText(lokasi);
                lab_penyebab.setText(penyebab);
                lab_upaya.setText(upaya);
                lab_akibat.setText(material+". "+akibat);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                return false;
            }
        });
        map.getOverlays().add(my_marker);
        map.invalidate();

    }


    public void tes(){
//        createmarker("Angin","Jember Kota",-8.1440703,113.3350449,ctx.getDrawable(R.drawable.mark_green));

    }

    public void get_data(){

        RequestQueue queue = Volley.newRequestQueue(Map.this);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
//                        Toast.makeText(Map.this,response,Toast.LENGTH_LONG).show();
                        try{
                            JSONObject d = new JSONObject(response);
                            JSONArray data = new JSONArray(d.getString("lokasi"));
                            for (int i=0;i<data.length();i++){
                                JSONObject lo = data.getJSONObject(i);
                                String nama_bencana =lo.getString("bencana");
                                String lokasi = lo.getString("nama");
                                double lat = lo.getDouble("lat");
                                double lng = lo.getDouble("lng");
                                Drawable marker;
                                if (nama_bencana.equals("Kebakaran")){
                                    marker = ctx.getDrawable(R.drawable.mark_green);
                                }else if(nama_bencana.equals("Banjir")){
                                    marker = ctx.getDrawable(R.drawable.marker_red);
                                }else if(nama_bencana.equals("Angin Kencang")){
                                    marker = ctx.getDrawable(R.drawable.marker_yellow);
                                }else if(nama_bencana.equals("Tanah Longsor")){
                                    marker = ctx.getDrawable(R.drawable.mark_gold);
                                }else if(nama_bencana.equals("Abrasi")){
                                    marker = ctx.getDrawable(R.drawable.marker_violet);
                                }else if(nama_bencana.equals("Kekeringan")){
                                    marker = ctx.getDrawable(R.drawable.marker_black);
                                }else if(nama_bencana.equals("Kebakaran Hutan dan Lahan")){
                                    marker = ctx.getDrawable(R.drawable.marker_orange);
                                }else{
                                    marker = ctx.getDrawable(R.drawable.marker_grey);
                                }
                                String jam = lo.getString("jam");
                                String tanggal = lo.getString("tanggal");
                                String penyebab = lo.getString("penyebab");
                                String akibat = lo.getString("akibat");
                                String material = lo.getString("material");
                                String upaya = lo.getString("upaya");
                                createmarker(nama_bencana,lokasi,lat,lng,marker,jam,tanggal,penyebab,akibat,material,upaya);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }
        ){

            @Override
            protected java.util.Map<String, String> getParams()
            {
                java.util.Map<String, String> params = new HashMap<String, String>();

                return params;
            }
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strReq);

    }


}
