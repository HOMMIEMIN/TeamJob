package com.example.homin.test1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

public class EssayDetailActivity extends AppCompatActivity {



    private static final String TAG = "recycle";

    private static final String ESSAY_INDEX = "selected_ESSAY_index";
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private ImageView imageView;
    private TextView textView1 , textView2, textView3;
    private PopupWindow popupWindow;
    private UserDataTable userDataTable;
    //확대이미지 필요한 것들
    private static String key;
    // 맵 줌 위한 변수
    private int DEFAULT_ZOOM_LEVEL = 13;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay_detail);

        userDataTable = DaoImple.getInstance().getMyPageUserData();

        imageView = findViewById(R.id.imageview_detail);
        ImageView imageView2 = findViewById(R.id.DetailMapview2);
        textView1 = findViewById(R.id.Detali_textGemock);
        textView2 = findViewById(R.id.Detali_textgul);
        textView3 = findViewById(R.id.Detali_textNaljja);

        Double lat = userDataTable.getLocation().get(0);
        Double lng = userDataTable.getLocation().get(1);

        Glide.with(getApplicationContext()).load(userDataTable.getImageUrl()).override(100,100).into(imageView);
        Glide.with(getApplicationContext())
                .load("http://maps.googleapis.com/maps/api/staticmap?center=" + lat +","+ lng + "&zoom=14&size=400x220&maptype=roadmap%20&markers=color:blue|label:S|" + lat +","+ lng + "&sensor=false")
                .into(imageView2);

        //
        textView1.setText(userDataTable.getTitle());
        textView2.setText(userDataTable.getContent());
        textView3.setText(userDataTable.getData());


//        Double lat = userData.getLocation().get(0);
//        Double lng = userData.getLocation().get(1);
//        String address = getAddress(getContext(), lat, lng);
//        holder.textLocation.setText(address);

//        this.setGpsCurrent(userDataTable);





        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: 이미지 확대보기 클릭시 사진이 크게 나와야함  !!!! 내일 확인해야할껏!!!!!!
                key = DaoImple.getInstance().getKey();
                Log.i(TAG, "line157) key: " + key);
                String curProImgUrl = DaoImple.getInstance().getContact().getPictureUrl();
                //TODO:
                if (curProImgUrl == null) { // Firebase에 저장된 파일이 있을 때
                    popupWindow.dismiss();
                    Toast.makeText(EssayDetailActivity.this, "저장된 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                } else { // 없을 때
                    View popupView = getLayoutInflater().inflate(R.layout.popup_plusdetailimage, null);
                    //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정
                    popupWindow= new PopupWindow(
                            popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    }); // click

                    ImageView plusDetailImage = popupView.findViewById(R.id.popup_PlusDetailImageVeiw);
                    popupWindow.setFocusable(true); // 외부영역 선택시 종료
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    Glide.with(getApplicationContext()).load(userDataTable.getImageUrl()).into(plusDetailImage);
                }

            }
        });

    }







//    //TODO: 맵 관련 메소드
//    private void setGpsCurrent(UserDataTable userDataTable) {
//
//        Double lat = userDataTable.getLocation().get(0);
//        Double lng = userDataTable.getLocation().get(1);
//
//
//            // Creating a LatLng object for the current location
//            LatLng latLng = new LatLng(lat,lng);
//
//            // Showing the current location in Google Map
//            mGoogleMap.moveCamera(CameraUpdateFactory
//                    .newLatLng(latLng));
//
//            // Map 을 zoom 합니다.
//
//            this.setZoomLevel(DEFAULT_ZOOM_LEVEL);
//
//            // 마커 설정.
//            MarkerOptions optFirst = new MarkerOptions();
////            optFirst.position(latLng);// 위도 • 경도
////            optFirst.title("Current Position");// 제목 미리보기
////            optFirst.snippet("Snippet");
////            optFirst.icon(BitmapDescriptorFactory
////                    .fromResource(R.drawable.target));
////            mGoogleMap.addMarker(optFirst).showInfoWindow();
//    }
//
//    //TODO: 지도 줌 메소드!!!!
//    private void setZoomLevel(int level) {
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(level));
//        Toast.makeText(this, "Zoom Level : " + String.valueOf(level),
//                Toast.LENGTH_LONG).show();
//    }



    public static Intent newIntent (Context context, int index){
        Intent intent = new Intent(context, EssayDetailActivity.class);
        intent.putExtra(ESSAY_INDEX, index);
        return intent;

    }


}