package wzp.com.texturemusic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ExecutionException;

import wzp.com.texturemusic.GlideApp;
import wzp.com.texturemusic.interf.OnImageLoadListener;


/**
 * Created by Go_oG
 * Description:专门用于加载图片显示的问题
 * on 2018/2/19.
 */

public class ImageUtil {
    /**
     * **************加载的是字符串资源
     */
    public static void loadImage(View view, String imgUrl, ImageView imageView) {
        if (StringUtil.isNotEmpty(imgUrl) && imgUrl.startsWith("http")) {
            GlideApp.with(view).load(new GlideUrlUtil(imgUrl))
                    .into(imageView);
        } else {
            GlideApp.with(view).load(imgUrl)
                    .into(imageView);
        }
    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView) {
        if (imgUrl != null && imgUrl.startsWith("http")) {
            GlideApp.with(context).load(new GlideUrlUtil(imgUrl))
                    .into(imageView);
        } else {
            GlideApp.with(context).load(imgUrl)
                    .into(imageView);
        }

    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView, @DrawableRes int errorId) {
        RequestOptions requestOptions = new RequestOptions()
                .error(errorId);
        if (imgUrl == null) {
            GlideApp.with(context)
                    .load(errorId)
                    .into(imageView);
        } else {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context)
                        .load(new GlideUrlUtil(imgUrl))
                        .apply(requestOptions)
                        .into(imageView);
            } else {
                GlideApp.with(context)
                        .load(imgUrl)
                        .apply(requestOptions)
                        .into(imageView);
            }
        }

    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView, @DrawableRes int placeId, @DrawableRes int errorId) {
        RequestOptions requestOptions = new RequestOptions()
                .error(errorId)
                .placeholder(placeId);
        if (imgUrl == null) {
            GlideApp.with(context)
                    .load(errorId)
                    .apply(requestOptions)
                    .into(imageView);
        } else {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context)
                        .load(new GlideUrlUtil(imgUrl))
                        .apply(requestOptions)
                        .into(imageView);
            } else {
                GlideApp.with(context)
                        .load(imgUrl)
                        .apply(requestOptions)
                        .into(imageView);
            }

        }

    }

    public static void loadImage(Context context, String imgUrl, @DrawableRes int errorId, @DrawableRes int placeId, @NonNull final OnImageLoadListener loadListener) {
        RequestOptions requestOptions = new RequestOptions()
                .error(errorId)
                .placeholder(placeId);
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(new GlideUrlUtil(imgUrl))
                        .apply(requestOptions)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                loadListener.onSuccess(resource);
                            }
                        });
            } else {
                GlideApp.with(context)
                        .asBitmap()
                        .load(imgUrl)
                        .apply(requestOptions)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                loadListener.onSuccess(resource);
                            }
                        });
            }
        } else {
            GlideApp.with(context)
                    .asBitmap()
                    .load(errorId)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            loadListener.onSuccess(resource);
                        }
                    });
        }

    }

    public static void loadImage(Context mContext, String imgUrl, @DrawableRes int errorId, final OnImageLoadListener listener) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(mContext).asBitmap().load(new GlideUrlUtil(imgUrl)).error(errorId).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            } else {
                GlideApp.with(mContext).asBitmap().load(imgUrl).error(errorId).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            }
        } else {
            GlideApp.with(mContext).asBitmap().load(errorId).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    listener.onSuccess(resource);
                }
            });
        }
    }

    public static void loadImage(Context mContext, String imgUrl, Drawable drawable, final OnImageLoadListener listener) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {

                GlideApp.with(mContext).asBitmap().load(new GlideUrlUtil(imgUrl)).error(drawable).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            } else {
                GlideApp.with(mContext).asBitmap().load(imgUrl).error(drawable).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            }
        } else {
            GlideApp.with(mContext).asBitmap().load(drawable).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    listener.onSuccess(resource);
                }
            });
        }
    }

    public static void loadImage(Context context, String imgUrl, int w, int h, @DrawableRes int errorId, final OnImageLoadListener listener) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context).asBitmap().load(new GlideUrlUtil(imgUrl)).error(errorId).override(w, h).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            } else {
                GlideApp.with(context).asBitmap().load(imgUrl).error(errorId).override(w, h).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
            }
        } else {
            GlideApp.with(context).asBitmap()
                    .load(errorId)
                    .override(w, h)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            listener.onSuccess(resource);
                        }
                    });
        }
    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView, Transformation transformation, @DrawableRes int errorId) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context)
                        .load(new GlideUrlUtil(imgUrl))
                        .error(errorId)
                        .placeholder(errorId)
                        .transform(transformation)
                        .into(imageView);
            } else {
                GlideApp.with(context).load(imgUrl).error(errorId)
                        .placeholder(errorId)
                        .transform(transformation)
                        .into(imageView);
            }
        } else {
            GlideApp.with(context).load(errorId)
                    .placeholder(errorId)
                    .transform(transformation)
                    .into(imageView);
        }
    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView, int w, int h, Transformation transformation, @DrawableRes int errorId) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context).load(new GlideUrlUtil(imgUrl)).error(errorId)
                        .override(w, h).transform(transformation)
                        .into(imageView);
            } else {
                GlideApp.with(context).load(imgUrl).error(errorId)
                        .override(w, h).transform(transformation)
                        .into(imageView);
            }
        } else {
            GlideApp.with(context).load(errorId)
                    .override(w, h).transform(transformation)
                    .into(imageView);
        }
    }

    public static void loadImage(Context context, String imgUrl, BitmapTransitionOptions transformation, @DrawableRes int errorId, final OnImageLoadListener listener) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context).asBitmap().load(new GlideUrlUtil(imgUrl)).error(errorId).transition(transformation)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                listener.onSuccess(resource);
                            }
                        });
            } else {
                GlideApp.with(context).asBitmap().load(imgUrl).error(errorId).transition(transformation)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                listener.onSuccess(resource);
                            }
                        });
            }
        } else {
            GlideApp.with(context)
                    .asBitmap()
                    .load(errorId)
                    .transition(transformation)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            listener.onSuccess(resource);
                        }
                    });
        }
    }

    /**
     * **************加载的是本地
     */
    public static void loadImage(Context context, @DrawableRes int srcId, ImageView imageView, int errorId) {
        RequestOptions requestOptions = new RequestOptions()
                .error(errorId);
        GlideApp.with(context)
                .load(srcId)
                .apply(requestOptions)
                .into(imageView);
    }

    public static void loadImage(Context context, @DrawableRes int resouceId, ImageView imageView, int w, int h,
                                 Transformation transformation, @DrawableRes int errorId) {
        GlideApp.with(context).load(resouceId).error(errorId)
                .override(w, h).transform(transformation)
                .into(imageView);
    }

    public static void loadImage(Context context, @DrawableRes int resouceId, int w, int h, @DrawableRes int errorId, final OnImageLoadListener listener) {
        GlideApp.with(context)
                .asBitmap()
                .load(resouceId)
                .error(errorId)
                .override(w, h)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        listener.onSuccess(resource);
                    }
                });
    }

    public static void loadImage(Context context, int resouceId, ImageView imageView, DrawableTransitionOptions transformation, int errorId) {
        GlideApp.with(context)
                .load(resouceId)
                .error(errorId)
                .transition(transformation)
                .into(imageView);
    }

    /**
     * **************跳过内存
     */
    public static void loadImageSkipMemory(Context context, String imgUrl, @DrawableRes int errorId, final OnImageLoadListener listener) {
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                GlideApp.with(context).asBitmap().
                        load(new GlideUrlUtil(imgUrl))
                        .skipMemoryCache(true)
                        .error(errorId)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                listener.onSuccess(resource);
                            }
                        });
            } else {
                GlideApp.with(context).asBitmap().
                        load(imgUrl)
                        .skipMemoryCache(true)
                        .error(errorId)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                listener.onSuccess(resource);
                            }
                        });
            }
        } else {
            GlideApp.with(context).asBitmap().
                    load(errorId)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            listener.onSuccess(resource);
                        }
                    });
        }

    }

    public static Bitmap getBitmap(Context context, String imgUrl, int w, int h) throws ExecutionException, InterruptedException {
        Bitmap bitmap = null;
        if (!StringUtil.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                bitmap = GlideApp.with(context).asBitmap().skipMemoryCache(true).load(new GlideUrlUtil(imgUrl)).into(w, h).get();
            } else {
                bitmap = GlideApp.with(context).asBitmap().skipMemoryCache(true).load(imgUrl).into(w, h).get();
            }
        }
        return bitmap;
    }

    public static void loadImage(Context mContext, int resouceId, final OnImageLoadListener listener) {
        GlideApp.with(mContext)
                .asBitmap()
                .load(resouceId)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (listener != null) {
                            listener.onSuccess(resource);
                        }
                    }
                });
    }

    public static void loadImage(ImageView img, String url, int errorId) {
        GlideApp.with(img)
                .load(url)
                .error(errorId)
                .into(img);
    }
}

