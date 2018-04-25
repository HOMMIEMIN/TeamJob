package com.example.homin.test1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.Set;

public class PersonItemRenderer extends DefaultClusterRenderer<ClusterItem> {
    Context context;
    GoogleMap googleMap;

    @Override
    protected void onClusterRendered(Cluster<ClusterItem> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    public PersonItemRenderer(Context context, GoogleMap map, ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.googleMap = map;
    }

    @Override
    public void setOnClusterInfoWindowClickListener(ClusterManager.OnClusterInfoWindowClickListener<ClusterItem> listener) {
        super.setOnClusterInfoWindowClickListener(listener);
    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterItem item, final MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        if(item instanceof ItemPerson) {

            if(((ItemPerson) item).getImage()!= null) {
                Log.i("hi", "이름 : " + ((ItemPerson) item).getTitle());
                Bitmap roundBitmap = getCircleBitmap(((ItemPerson) item).getImage());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitmap));
                markerOptions.title(((ItemPerson) item).getTitle());
            }else{
                Log.i("hi", "이름 : " + ((ItemPerson) item).getTitle());
                Bitmap rectBitmap = decodeSampledBitmapFromResource(context.getResources(),R.drawable.what,35,35);
                Bitmap roundBitmap = getCircleBitmap(rectBitmap);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitmap));
                markerOptions.title(((ItemPerson) item).getTitle());
            }


        }

        if(item instanceof ItemMemo){
            Bitmap roundBitmap = getCircleBitmap(((ItemMemo) item).getIcon());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitmap));
//            markerOptions.title(((ItemMemo) item).getTitle());
        }

    }

    //직사각형 비트맵을 원형으로 변환하는 메소드
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //비트맵 메모리 부족 현상때문에 비트맵 메모리 줄이는 메소드
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

}
