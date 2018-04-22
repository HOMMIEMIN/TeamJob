package com.example.homin.test1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
//import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.homin.test1.WriteActivity.*;
import static com.example.homin.test1.ReadMemoActivity.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    private MapView view;
    private static final int RESULT_CODE = 20;
    private LatLng addMakerLocation;
    private String email;
//    private LinearLayout actionLayout;
    private StorageReference firebaseStorage;
     private FrameLayout actionLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomview;
    private Menu mMenu;
    private FloatingActionButton actionButton;
    private DatabaseReference reference;
    private List<String> myFriendList;
    private List<String> memoFriendList;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng myLatLng;
    private ItemPerson myMarker;
    private ClusterManager<ClusterItem> clusterManager;
    private List<Contact> myFriendContactList;
    private List<Contact> contactList;
    private Map<String,Bitmap> pictureList;
    private boolean check;
    private Location location;
    private String provider;
    private boolean zoomCheck;
    private ClusterManager<ClusterItem> memoManager;

    // MyPage에 이용
    public static final int CAMERA_CODE = 100;
    private final int GALLERY_CODE = 1000;
    private Uri filePath;
    private String key;
    public static final String TAG = "mini";

    //자기위치로 되돌리는 버튼
    private FloatingActionButton selfLocationButton;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    FriendFragment friendFragment = new FriendFragment();
                    transaction.replace(R.id.container_main, friendFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    ChatListFragment chatListFragment = new ChatListFragment();
                    transaction1.replace(R.id.container_main, chatListFragment);
                    transaction1.commit();
                    return true;
                case R.id.navigation_notifications:
                    FragmentManager manager2 = getSupportFragmentManager();
                    FragmentTransaction transaction2 = manager2.beginTransaction();
                    MypageFragment mypageFragment = new MypageFragment();
                    transaction2.replace(R.id.container_main, mypageFragment);
                    transaction2.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        Log.i("qq23q","onCreate");
        myFriendContactList = new ArrayList<>();
        setContentView(R.layout.activity_maps);

            pictureList = DaoImple.getInstance().getPictureList();



        //자기위치찾아주는 버튼 찾기
        selfLocationButton = findViewById(R.id.selfLocationIdentifier);
        selfLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,16));

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        actionButton = findViewById(R.id.floatingActionButton);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FriendFragment friendFragment = new FriendFragment();
        transaction.replace(R.id.container_main, friendFragment);
        transaction.commit();

        registerForContextMenu(findViewById(R.id.map));

        bottomview = findViewById(R.id.bottom_sheet);
        actionLayout = findViewById(R.id.action_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomview);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    actionLayout.setVisibility(View.VISIBLE);

                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    actionLayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    actionLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


//        search(); //검색창 이벤트 헨들러 처리


    }
    //검색창 이벤트 핸들러 처리
//    private void search() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("hi", "Place: " + place.getName());
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("hi", "An error occurred: " + status);
//            }
//        });
//    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        email = DaoImple.getInstance().getLoginEmail();
        mMap = googleMap;

        reference = FirebaseDatabase.getInstance().getReference();
        if(memoManager == null){
            memoManager = new ClusterManager<>(MapsActivity.this, mMap);
            mMap.setOnCameraIdleListener(memoManager);
            memoManager.setRenderer(new PersonItemRenderer(MapsActivity.this,mMap,memoManager));
            memoManager.setAlgorithm(new GridBasedAlgorithm<ClusterItem>());
            mMap.setOnMarkerClickListener(memoManager);
        }
        if(clusterManager == null){
            clusterManager = new ClusterManager<>(MapsActivity.this,mMap);
            mMap.setOnCameraIdleListener(clusterManager);
        }
        Log.i("gg6","클러스터 설정");
        Log.i("asd123","onMapReady");
        myLocationUpdate(); // 내 위치 업데이트

        getFriendList(); // 친구 목록 가져오기



        // 친구 요청 Activity 실행
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MapsActivity.this, WatingActivity.class);
                startActivity(intent1);

            }
        });
        mMap.setOnMarkerClickListener(memoManager);

        // 내 마커 클릭시 현재 위치에 글 쓰고 마커 남기기
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItem>() {
            @Override
            public boolean onClusterItemClick(ClusterItem clusterItem) {
                // 내 마커가 맞는지 확인
                if (clusterItem instanceof ItemPerson) {
                    if (((ItemPerson) clusterItem).getTitle().equals(DaoImple.getInstance().getLoginId())) {
                        // 글을 남기기 위해 현재 위치 가져오기
                        writeMyLocation();
                        Intent intent = new Intent(MapsActivity.this, WriteActivity.class);
                        startActivityForResult(intent, RESULT_CODE);
                    }
                }
                return true;
            }
        });
        // 메모 마커라면 메모 ReadMemoActivity로 상세보기
        memoManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItem>() {
            @Override
            public boolean onClusterItemClick(ClusterItem clusterItem) {
                if(clusterItem instanceof ItemMemo){
                    Intent intent = new Intent(MapsActivity.this,ReadMemoActivity.class);
                    intent.putExtra(MEMO_NAME,((ItemMemo)clusterItem).getUserName());
                    intent.putExtra(MEMO_ID,((ItemMemo)clusterItem).getUserId());
                    intent.putExtra(MEMO_TITLE,((ItemMemo)clusterItem).getTitle());
                    intent.putExtra(MEMO_CONTENT,((ItemMemo)clusterItem).getContent());
                    intent.putExtra(MEMO_URL,((ItemMemo)clusterItem).getImageUrl());
                    intent.putExtra(MEMO_TIME,((ItemMemo)clusterItem).getTime());
                    startActivity(intent);
                }
                return true;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                writeMyLocation();
                Intent intent = new Intent(MapsActivity.this,WriteActivity.class);
                startActivityForResult(intent,RESULT_CODE);
            }
        });

        // 친구 위치정보 받아오기
        reference.child("Contact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(myFriendList != null){
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    for(int a = 0 ; a < myFriendList.size() ; a++){
                        if(myFriendList.get(a).equals(contact.getUserId())){
                            if(clusterManager == null){
                                clusterManager = new ClusterManager<>(MapsActivity.this,mMap);
                                mMap.setOnCameraIdleListener(clusterManager);
                            }
                            if(!(check)){
                                clusterManager.clearItems();
                                check = true;

                            }

                            // 친구들 위치정보 받아와서 구글맵에 갱신
                            List<Double> friendLocation = contact.getUserLocation();
                            if(pictureList.get(contact.getUserId()) != null) {
                                Bitmap picture = pictureList.get(contact.getUserId());
                                ItemPerson friendMarker = new ItemPerson(friendLocation.get(0),
                                        friendLocation.get(1), contact.getUserName(), picture);

                                Log.i("ggg2", contact.getUserId());
                                Log.i("ggg2", friendLocation.get(0) + " " + friendLocation.get(1));
                                clusterManager.addItem(friendMarker);
                                clusterManager.cluster();
                                memoManager.cluster();
                                Log.i("fffff", "친구위치 마커생성");
                                Log.i("fffff", "친구위치 : " + contact.getUserId());
                            }else{
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 1;
                                Bitmap otherPicture = BitmapFactory.decodeResource(getResources(),R.drawable.what,options);

                                Bitmap picture = Bitmap.createScaledBitmap(otherPicture, 128, 128, true);
                                ItemPerson friendMarker = new ItemPerson(friendLocation.get(0),
                                        friendLocation.get(1), contact.getUserName(), picture);
                                clusterManager.addItem(friendMarker);
                                clusterManager.cluster();
                                memoManager.cluster();
                            }

                        }

                    }
                }
            }
             // 친구 위치 바뀌었을때 정보 갱신
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                check = false;
                Log.i("fffff","체인지 들어옴");
                reference.child("Contact").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(myFriendList != null){
                            Contact contact = dataSnapshot.getValue(Contact.class);
                            for(int a = 0 ; a < myFriendList.size() ; a++){
                                if(myFriendList.get(a).equals(contact.getUserId())){
                                    if(clusterManager == null){
                                        clusterManager = new ClusterManager<>(MapsActivity.this,mMap);
                                        mMap.setOnCameraIdleListener(clusterManager);
                                    }
                                    if(!(check)){
                                        clusterManager.clearItems();
                                        Log.i("fffff", "체인지 : 모든 마커 삭제");
                                        check = true;
                                    }

                                    // 친구들 위치정보 받아와서 구글맵에 갱신
                                    List<Double> friendLocation = contact.getUserLocation();
                                    if(pictureList.get(contact.getUserId()) != null) {
                                        Bitmap picture = pictureList.get(contact.getUserId());

                                        ItemPerson friendMarker = new ItemPerson(friendLocation.get(0),
                                                friendLocation.get(1), contact.getUserName(), picture);
                                        Log.i("fffff", "체인지 : " + myFriendList.get(a));
                                        clusterManager.addItem(friendMarker);
                                        clusterManager.cluster();
                                        memoManager.cluster();
                                        Log.i("fffff", "체인지 : 친구위치 마커생성");
                                    }else{
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inSampleSize = 1;
                                        Bitmap otherPicture = BitmapFactory.decodeResource(getResources(),R.drawable.what,options);
                                        Bitmap picture = Bitmap.createScaledBitmap(otherPicture, 128, 128, true);
                                        ItemPerson friendMarker = new ItemPerson(friendLocation.get(0),
                                                friendLocation.get(1), contact.getUserName(), picture);
                                        clusterManager.addItem(friendMarker);
                                        clusterManager.cluster();
                                        memoManager.cluster();
                                        Log.i("fffff", "체인지 : else 친구위치 마커생성");


                                    }

                                }

                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    // 내 친구 리스트 받아오고 친구 메모 가져오기
    private void getFriendList() {
        contactList = new ArrayList<>();
        reference.child("Contact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                contactList.add(contact);
                if(contact.getUserId().equals(DaoImple.getInstance().getLoginEmail())){
                    if(contact.getFriendList() != null) {
                        myFriendList = contact.getFriendList(); // 친구 목록 저장
                        memoFriendList = contact.getFriendList();
                        memoFriendList.add(DaoImple.getInstance().getLoginEmail());
                        Log.i("fffff","친구 목록 저장");
                        memoManager.clearItems();
                        Log.i("fffff","친구 메모 삭제");
                        for(int a = 0 ; a < memoFriendList.size() ; a++){ //  친구 목록으로 메모 가져오기
                            String key = DaoImple.getInstance().getFirebaseKey(memoFriendList.get(a));
                            reference.child("userData").child(key).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    UserDataTable data = dataSnapshot.getValue(UserDataTable.class);
                                    List<Double> friendLocation = data.getLocation();
                                    ItemMemo friendMemo = new ItemMemo(friendLocation.get(0),friendLocation.get(1),
                                            data.getUserId(),data.getName(),data.getTitle(),data.getContent(),
                                            data.getData(),data.getImageUrl(),BitmapFactory.decodeResource(context.getResources(),R.drawable.letter));
                                    memoManager.addItem(friendMemo);
                                    memoManager.cluster();
                                    Log.i("fffff",data.getName());
                                    Log.i("fffff","친구 메모 에드");

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    // 내 gps 위치 받아오고, firebase에 contact 업데이트
    @SuppressLint("MissingPermission")
    private void myLocationUpdate() {
        Log.i("asd123","myLocationUpdate");
        if (locationManager == null) {
            locationManager = (LocationManager) this.getSystemService(context.LOCATION_SERVICE);
            Log.i("vvv456","로케이션 매니저 생성");
        }

        // 최적 gps 하드웨어 검색
        Criteria c = new Criteria();
        provider = locationManager.getBestProvider(c,true);
        // 사용가능한 장치가 없다면 모든 장치에서 검색
        if(provider == null || !locationManager.isProviderEnabled(provider)) {
            List<String> hardWare = locationManager.getAllProviders();
            for (int a = 0; a < hardWare.size(); a++) {
                String gpsHardware = hardWare.get(a);
                if (locationManager.isProviderEnabled(gpsHardware)) {
                    provider = gpsHardware;
                    break;
                }
            }
        }
        // 사용 가능한 gps 장치가 없다면, 마지막 수신 위치를 받아옴
        if(!locationManager.isProviderEnabled(provider)) {
            if(clusterManager == null) {
                clusterManager = new ClusterManager<>(MapsActivity.this, mMap);
                mMap.setOnCameraIdleListener(clusterManager);

            }
            Location myLocation = locationManager.getLastKnownLocation(provider);
            if(myLocation == null){
                Toast.makeText(context, "수신 가능한 위치 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }

                myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());


            Bitmap myPicture = null; //내 사진 url이 없는 경우 bit맵을 읽어오고 그렇지 않은 경우 null처리 된 bitmap를 cluster로 보냄
            if (pictureList.get(DaoImple.getInstance().getLoginEmail()) != null) {
                myPicture = pictureList.get(DaoImple.getInstance().getLoginEmail());
            } else {
                myPicture = null;
            }

            // 파이어베이스에 내 gps 정보 업데이트
            if(DaoImple.getInstance().getContact() != null) {
                Contact myContact = DaoImple.getInstance().getContact();
                List<Double> myLocationList = new ArrayList<>();
                myLocationList.add(myLocation.getLatitude());
                myLocationList.add(myLocation.getLongitude());
                myContact.setUserLocation(myLocationList);
                reference.child("Contact").child(DaoImple.getInstance().getKey()).setValue(myContact);

            }
            if(myMarker != null) {
                clusterManager.removeItem(myMarker);
                Log.i("fffff","내 마커 지움:수신장치 없음");
            }

            myMarker = new ItemPerson(myLocation.getLatitude(),myLocation.getLongitude(),
                    DaoImple.getInstance().getLoginId(),myPicture);
            Log.i("fffff","내 마커 생성:수신장치 없음");
            clusterManager.addItem(myMarker);
            if(!zoomCheck) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));
                zoomCheck = true;
            }
        }


            // 내 GPS 위치가 바뀔 때 마다, 내 마커 생성
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    check = false;
                    Log.i("fffff","리스너 들어옴");
                    if(clusterManager == null) {
                        clusterManager = new ClusterManager<>(MapsActivity.this, mMap);
                        mMap.setOnCameraIdleListener(clusterManager);

                    }
//                    if(myMarker != null) {
//                        clusterManager.removeItem(myMarker);
//                        Log.i("fffff","내 마커 지움");
//                    }

                    // 파이어베이스에 내 gps 정보 업데이트
                    if(DaoImple.getInstance().getContact() != null) {
                        Contact myContact = DaoImple.getInstance().getContact();
                        List<Double> myLocation = new ArrayList<>();
                        myLocation.add(location.getLatitude());
                        myLocation.add(location.getLongitude());
                        myContact.setUserLocation(myLocation);
                        reference.child("Contact").child(DaoImple.getInstance().getKey()).setValue(myContact);

                    }


                    // ClusterManagerItmes 이미지 추가/사이즈 줄이기
                    clusterManager.setRenderer(new PersonItemRenderer(MapsActivity.this,mMap,clusterManager));
                    clusterManager.setAlgorithm(new GridBasedAlgorithm<ClusterItem>());

                    // 내 위치로 내 마커 생성
                    myLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                    Bitmap myPicture = null; //내 사진 url이 없는 경우 bit맵을 읽어오고 그렇지 않은 경우 null처리 된 bitmap를 cluster로 보냄

                        if (pictureList.get(DaoImple.getInstance().getLoginEmail()) != null) {
                            myPicture = pictureList.get(DaoImple.getInstance().getLoginEmail());
                        } else {
                            myPicture = null;
                        }

                    if(!zoomCheck) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));
                        zoomCheck = true;
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);



    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "글 남기기");
        menu.add(0, 2, 0, "테스트1");
        menu.add(0, 3, 0, "테스트2");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, WriteActivity.class);
                startActivityForResult(intent, RESULT_CODE);
                break;
        }
        return super.onContextItemSelected(item);
    }

    // 갤러리에서 사진 선택하는 메소드
    public void clickedProImgBotton() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //TODO: ACTION_PICK(이미지가 저장되어있는 폴더를 선택) ACTION_GET_CONTENT(전체 이미지를 폴더 구분없이 최신 이미지 순)랑 둘 비교
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
        Log.i(TAG, "갤러리 코드: " + intent);
    } // end clickedProImgBotton()


    // 팝업뜰때 카메라 눌렀을때 발생하는 메소드  속에 내부메소드!
    public void popupCameraInCameraMethod() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.i(TAG, "intent.getData(): " + intent.getData());
//            Bitmap bitmap = BitmapFactory.d
            startActivityForResult(intent, CAMERA_CODE);

            Log.i(TAG, "팝업창에서 카메라 눌른후");
        }
    }





    @SuppressLint("MissingPermission")
    void writeMyLocation(){
        // 현재 내 위치 가져오기
        reference.child("Contact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contact con = dataSnapshot.getValue(Contact.class);
                if(con.getUserId().equals(DaoImple.getInstance().getLoginEmail())){
                    List<Double> location = con.getUserLocation();
                    LatLng myLL = new LatLng(location.get(0),location.get(1));
                    DaoImple.getInstance().setWriteLocation(myLL);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // 현재 gps 위치 저장


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ggv", "onActivityResult 들어옴");
        // WriteActivity에서 받아온 글 정보들을 마커로 생성
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "RESULT_OK");
            switch (requestCode) {
                case RESULT_CODE:
                    Log.i("ggv","onActivityResult");
                    String title = data.getStringExtra(TITLE_KEY);
                    String body = data.getStringExtra(BODY_KEY);
                    String time = data.getStringExtra(TIME_KEY);
                    String imageUrl = data.getStringExtra(IMAGEURL_KEY);
                    Log.i("ggv","onActivityResult 데이터 뺌");
                    Log.i("gg", title + body);
                    if (!(title.equals("")) && !(body.equals(""))) {
                        // 클러스터 매니저에 메모 에드
                        LatLng memoLocation = DaoImple.getInstance().getWriteLocation();
                        ItemMemo myMemo = new ItemMemo(memoLocation.latitude,memoLocation.longitude,
                                DaoImple.getInstance().getLoginEmail(),DaoImple.getInstance().getLoginId(),title,body,
                                time,imageUrl,BitmapFactory.decodeResource(context.getResources(),R.drawable.letter));
//                        memoManager.addItem(myMemo);
//                        memoManager.cluster();
                        Log.i("bb","onActivityResult 내 메모 add");
                        // 파이어베이스에 메모 업로드
                        List<Double> tableLocation = new ArrayList<>();
                        tableLocation.add(memoLocation.latitude);
                        tableLocation.add(memoLocation.longitude);
                        UserDataTable table = new UserDataTable(DaoImple.getInstance().getLoginEmail(),DaoImple.getInstance().getLoginId()
                                ,imageUrl,tableLocation,title,body,time);
                        reference.child("userData").child(DaoImple.getInstance().getKey()).push().setValue(table);
                        Log.i("ggv","onActivityResult 파이어베이스 push()");

                    }
                    break;

                case GALLERY_CODE: // 갤러리에서 선택한 사진처리

                    filePath = data.getData(); // 선택한 사진 Uri 정보
                    Log.i(TAG, "filePath: " + filePath);

                    MypageFragment.uploadFile(filePath); // 프로필 적용 & Storage 업로드
//                    uploadFile();

                    break;

                case CAMERA_CODE: // 팝업창에서 카메라 버튼 클릭
                    Log.i(TAG, "온액티비티리절트 카메라1 ");
                    if (true) {
                        Log.i(TAG, "온액티비티리절트 카메라2 ");

                        Bundle bundle = data.getExtras();

                        if (bundle != null) {
                            Bitmap bitmap = (Bitmap) bundle.get("data");

                            ImageView iv = findViewById(R.id.imageView);
                            iv.setImageBitmap(bitmap);


//                            getPictureForPhoto();

                        }
                        Toast.makeText(this, "저장 완료.", Toast.LENGTH_SHORT).show();
                        break;

                    } else {
                        Toast.makeText(this, "저장 취소", Toast.LENGTH_SHORT).show();

                    }
                    break;

                case 400:

                    boolean check = data.getBooleanExtra("check",false);
                    if(check){
                        actionButton.setImageResource(R.drawable.ddww);
                    }else{
                        actionButton.setImageResource(R.drawable.ic_notifications_black_24dp);
                    }

                    break;

            } // end switch

        } // end if

    } // onActivityResult()



}
