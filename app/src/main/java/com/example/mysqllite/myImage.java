package com.example.mysqllite;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public class myImage {


    public String getRealPathFromUri(Uri contentUri, Context context){
        String result ="";
        Cursor cursor = context.getContentResolver().query(contentUri, null,null,null,null);
        if(cursor==null){
            result = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Bitmap automatic_rotateImg(Bitmap img, String pathImg){
        Bitmap rotateBitmap=null;
        try {
            ExifInterface exifInterface = new ExifInterface(pathImg);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate_Image(img, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate_Image(img, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate_Image(img, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL: default:
                    rotateBitmap=img;
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rotateBitmap;
    }

    Bitmap rotate_Image(Bitmap imgSource, float angel){
        Matrix matrix = new Matrix();
        matrix.postRotate(angel);
        return Bitmap.createBitmap(imgSource, 0, 0, imgSource.getWidth(), imgSource.getHeight(), matrix, true);

    }
}
