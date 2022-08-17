package wee.digital.sample.ui.widgets.fragment.cam;

import android.graphics.ImageFormat;
import android.media.Image;

import java.nio.ByteBuffer;


public class CamUtil {

    public static byte[] imageToMat(Image image) {

        Image.Plane[] planes = image.getPlanes();

        ByteBuffer buffer0 = planes[0].getBuffer();
        ByteBuffer buffer1 = planes[1].getBuffer();
        ByteBuffer buffer2 = planes[2].getBuffer();

        int offset = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        byte[] data = new byte[image.getWidth() * image.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
        byte[] rowData1 = new byte[planes[1].getRowStride()];
        byte[] rowData2 = new byte[planes[2].getRowStride()];

        int bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;

        // loop via rows of u/v channels

        int offsetY = 0;

        int sizeY =  width * height * bytesPerPixel;
        int sizeUV = (width * height * bytesPerPixel) / 4;

        for (int row = 0; row < height ; row++) {

            // fill data for Y channel, two row
            {
                int length = bytesPerPixel * width;
                buffer0.get(data, offsetY, length);

                if ( height - row != 1)
                    buffer0.position(buffer0.position()  +  planes[0].getRowStride() - length);

                offsetY += length;
            }

            if (row >= height/2)
                continue;

            {
                int uvlength = planes[1].getRowStride();

                if ( (height / 2 - row) == 1 ) {
                    uvlength = width / 2 - planes[1].getPixelStride() + 1;
                }

                buffer1.get(rowData1, 0, uvlength);
                buffer2.get(rowData2, 0, uvlength);

                // fill data for u/v channels
                for (int col = 0; col < width / 2; ++col) {
                    // u channel
                    data[sizeY + (row * width)/2 + col] = rowData1[col * planes[1].getPixelStride()];

                    // v channel
                    data[sizeY + sizeUV + (row * width)/2 + col] = rowData2[col * planes[2].getPixelStride()];
                }
            }

        }

        return data;
    }
}
