package ru.kpfu.itis.vkgram.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import ru.kpfu.itis.vkgram.R;

public final class ImageLoadHelper {

    private ImageLoadHelper() {
    }

    public static void loadPicture(@NonNull ImageView imageView, @NonNull String url) {
        if (url.isEmpty() || url.equals("https://vk.com/images/camera_b.gif")){
            Picasso.with(imageView.getContext())
                    .load(R.drawable.camera_b)
                    .noFade()
                    .into(imageView);
        } else {
            Picasso.with(imageView.getContext())
                    .load(url)
                    .placeholder(R.drawable.camera_b)
                    .noFade()
                    .into(imageView);
        }
    }

    public static void loadPictureByDrawable(@NonNull ImageView imageView, @DrawableRes int drawable) {
        Picasso.with(imageView.getContext())
                .load(drawable)
                //.resize(1280, 720)
                .noFade()
                .into(imageView);
    }

    public static void loadBackgroundByDrawable(@NonNull ImageView imageView, @DrawableRes int drawable) {
        Picasso.with(imageView.getContext())
                .load(drawable)
                .noFade()
                .into((new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
                        imageView.setBackground(new BitmapDrawable(imageView.getContext().getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(final Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(final Drawable placeHolderDrawable) {

                    }
                }));
    }
}
