package com.example.peta;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.peta.model.UploadImageResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.opensooq.supernova.gligar.GligarPicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.gotev.uploadservice.data.UploadInfo;
import net.gotev.uploadservice.network.ServerResponse;
import net.gotev.uploadservice.observer.request.RequestObserverDelegate;
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class lapor extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private AutoCompleteTextView jenisBencanaDropdown;
    private Toolbar toolbar;
    private TextInputEditText etName;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private TextInputEditText etInformation;
    private TextInputEditText etDate;
    private TextInputLayout etDateLayout;
    private TextInputEditText etTime;
    private TextInputEditText etLatLang;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private LinearLayout mapWrapper;
    private Button btnLapor;
    private Button btnPickImage;
    private ImageView imgPreview;


    private FusedLocationProviderClient mFusedLocationClient;

    private int requestCode = 10;
    private int PERMISSION_ID = 44;
    private LatLng currentLocation = new LatLng(-8.579892, 116.095239);
    private String uploadUrl = "http://192.168.1.73/peta/upload.php";
    private String serverUrl = "http://192.168.1.73/peta/laporan.php";
    private String paramName = "file";
    private boolean uploading = false;
    private String imageUrl = "";
    private String jenisBencana="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor);
        bindView();
        setupToolbar();
        setupJenisBencana();
        setupMap();
        setupDatePicker();
        setupTimePicker();
        checkLocationPermissions();
        setupButtonLapor();
        setupButtonPickImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 10) {
            String[] pathsList = data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT); // return list of selected images paths.
            //imagesCount.text = "Number of selected Images: " + pathsList.length;
            if (pathsList.length > 0) {
                imgPreview.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(pathsList[0])
                        .centerCrop()
                        //.placeholder(R.drawable.loading_spinner)
                        .into(imgPreview);
                //todo upload file to server
                sendUploadFileRequest(pathsList[0]);
            }
        }
    }

    private void setupButtonLapor() {
        btnLapor.setOnClickListener(view -> {
            //validate??
            postReportToServer();
        });
    }

    private void setupButtonPickImage() {
        btnPickImage.setOnClickListener(view -> {
                    new GligarPicker().requestCode(requestCode).withActivity(this).limit(1).show();
                }
        );
    }

    private void sendUploadFileRequest(String filePath) {
        MultipartUploadRequest uploadRequest = new MultipartUploadRequest(this, uploadUrl);
        uploadRequest.setMethod("POST");
        try {
            uploadRequest.addFileToUpload(filePath, paramName);
            uploadRequest.subscribe(this, this, new RequestObserverDelegate() {
                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {
                    uploading = true;
                }

                @Override
                public void onSuccess(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                      uploading = false;
                    Log.d("uploader", serverResponse.getBodyString());
                    Toast.makeText(lapor.this, "Upload file sukses " + serverResponse.getBodyString(), Toast.LENGTH_LONG).show();
                    Gson gson = new Gson();
                    UploadImageResponse response = gson.fromJson(serverResponse.getBodyString(),UploadImageResponse.class);
                    Log.d("imageUrl","url : "+response.getData().getFilelocation());
                    Log.d("imageUrl","url : "+response.toString());
                    imageUrl = response.getData().getFilelocation();
                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo, Throwable throwable) {
                    uploading = false;
                    Toast.makeText(lapor.this, "Upload file gagal " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo) {

                }

                @Override
                public void onCompletedWhileNotObserving() {

                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found ", Toast.LENGTH_LONG).show();
        }
        uploadRequest.startUpload();

    }

    private void postReportToServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, serverUrl,
                response -> {
                    // response
                    Log.d("Response", response);
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Laporan Diterima!")
                            .setContentText("laporan telah berhasil diterima, Terima kasih atas laporan anda")
                            .show();
                    finish();
                },
                error -> {
                    // error
                    Log.d("Error.Response",error.getMessage());
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Laporan Gagal Dikirim!")
                            .setContentText("Laporan tidak berhasil kami terima, coba beberapa saat lg")
                            .show();
                }
        ) {

            @Override
            protected java.util.Map<String, String> getParams()
            {
                Log.d("test","name : "+etName.getText());
                Log.d("test","telp : "+etPhone.getText());
                Log.d("test","tanggal : "+etDate.getText());
                Log.d("test","jam : "+etTime.getText());
                Log.d("test","kategori : "+jenisBencana);
                Log.d("test","lokasi : "+etAddress.getText());
                Log.d("test","lat : "+currentLocation.latitude);
                Log.d("test","lng : "+currentLocation.longitude);
                Log.d("test","penyebab : "+etInformation.getText());
                Log.d("test","gambar : "+imageUrl);
                HashMap<String, String> params = new HashMap<>();
                params.put("nama", etName.getText().toString());
                params.put("no.telepon", etPhone.getText().toString());
                params.put("tanggal", etDate.getText().toString());
                params.put("jam", etTime.getText().toString());
                params.put("kategori", jenisBencana);
                params.put("lokasi", etAddress.getText().toString());
                params.put("lat",""+currentLocation.latitude);
                params.put("lng", ""+currentLocation.longitude);
                params.put("penyebab", etInformation.getText().toString());
                params.put("gambar", imageUrl);
                return params;
            }
        };

        queue.add(putRequest);
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                            16));
                    pinMarker(currentLocation);
                }
            });
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void pinMarker(LatLng latLng) {
        currentLocation = latLng;
        etLatLang.setText(latLng.latitude + "," + latLng.longitude);
        etLatLang.setEnabled(false);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Lokasi bencana"));
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager
                .isProviderEnabled(
                        LocationManager.GPS_PROVIDER)
                || locationManager
                .isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER);
    }


    private void checkLocationPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            getUserLocation();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void setupToolbar() {
        toolbar.setTitle("Laporkan Bencana");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etInformation = findViewById(R.id.et_information);
        etLatLang = findViewById(R.id.et_latlang);
        jenisBencanaDropdown = findViewById(R.id.filled_exposed_dropdown);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etDateLayout = findViewById(R.id.et_date_input_layout);
        btnLapor = findViewById(R.id.btn_lapor);
        btnPickImage = findViewById(R.id.btn_pick_image);
        mapWrapper = findViewById(R.id.mapWrapper);
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        imgPreview = findViewById(R.id.img_preview);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    lapor.this, now);
// If you're calling this from a support Fragment
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    TimePickerDialog.Type.GREGORIAN,
                    lapor.this
                    , true);
            tpd.show(getSupportFragmentManager(), "TimePickerDialog");
        });
    }

    private void setupMap() {
        //memastikan mapfragment tidak nulld
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("map", "map ready");
        googleMap = map;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                pinMarker(latLng);
            }
        });
    }

    private void setupJenisBencana() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.jenis_marka_array));
        jenisBencanaDropdown.setAdapter(adapter);
        jenisBencanaDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                jenisBencana = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        etTime.setText(hourOfDay + ":" + minute);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgbytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgbytes, Base64.DEFAULT);

    }

}