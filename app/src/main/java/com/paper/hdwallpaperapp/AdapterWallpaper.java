package com.paper.hdwallpaperapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.io.File.separator;


public class AdapterWallpaper extends RecyclerView.Adapter<AdapterWallpaper.MyViewHolder> {
    private ArrayList<ModelWallpaper> modelWallpaperArrayList;
//    public final ClickListner mListner;
    public Context cxt;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_wal);

        }
    }

    public AdapterWallpaper(ArrayList<ModelWallpaper> exampleList, Context context) {
        this.modelWallpaperArrayList = exampleList;
        this.cxt = context;
//        mListner = (ClickListner) context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return modelWallpaperArrayList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ModelWallpaper currentItem = modelWallpaperArrayList.get(position);

        holder.imageView.setImageResource(currentItem.getImage());

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getPurchaseSharedPreference()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
                    builder.setMessage("Do you want to Download Image")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    BitmapDrawable drawable = (BitmapDrawable) holder.imageView.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    saveToGallery(bitmap);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else {
                    Toast.makeText(cxt, "Please Subscribe First", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }

    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(cxt.getApplicationContext());
        return prefs.getBoolean(cxt.getString(R.string.adsubscribed), false);
    }
    private void saveToGallery(Bitmap bmp) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Photo Lab");
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            Uri uri = cxt.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);
            if (uri != null) {
                try {
                    saveImageToStream(bmp, cxt.getContentResolver().openOutputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                cxt.getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString()
                    + separator + "Photo Lab");

            //getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bmp, new FileOutputStream(file));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (file.getAbsolutePath() != null) {
                ContentValues values = contentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                cxt.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }
    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }
    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Toast.makeText(cxt, "Image Save to Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

