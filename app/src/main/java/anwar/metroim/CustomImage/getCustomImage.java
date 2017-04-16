package anwar.metroim.CustomImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import anwar.metroim.R;

/**
 * Created by anwar on 1/30/2017.
 */

public class getCustomImage {
    Context context;
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage,int targetWidth,int targetHeight ) {
       // int targetWidth = 256;
        //int targetHeight = 256;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    // if the image get from database then replace all space by +
    public Bitmap getRoundedShape(String base64,int targetWidth,int targetHeight) {
        //base64=base64.replaceAll(" ", "+");
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap scaleBitmapImage=BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        //int targetWidth = 256;
        //int targetHeight = 256;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }
    // if the image get from database then replace all space by +
    public Bitmap base64Decode(String base64){
        //String encodedString =base64image;
        //String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",") + 1);
        //base64=base64.replaceAll(" ","+");
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap images = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return images;
    }
    public Bitmap getCropImage(Bitmap source){
        int newHeight=300;
        int newWidth=300;
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;

    }
    public String base64Encode(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byte_arr=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }
    public Bitmap getProImg(Context context){
        Bitmap bitmap =this.getRoundedShape(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image), 100, 100);
        return bitmap;
    }
}
