/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gallery.images;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import gallery.enums.ImageAngle;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author maartenl
 */
public class ImageOperations
{

        public static boolean isImage(String path)
    {
        return !path.endsWith(".avi") && !path.endsWith(".AVI");
    }

    public static boolean isImage(Path path)
    {
        return isImage(path.toString());
    }

    public static ImageAngle getAngle(File jpegFile) throws ImageProcessingException, IOException, MetadataException
    {
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
        if (directory == null)
        {
            return null;
        }
        return ImageAngle.getAngle(directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION));
    }

    private ImageOperations()
    {
        // defeat instantiation
    }

    public static BufferedImage rotate(BufferedImage originalImage, ImageAngle imageAngle)
    {
        if (imageAngle == null)
        {
            return originalImage;
        }
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        int newWidth = width;
        int newHeight = height;
        // 2π radians is equal to 360 degrees
        double angle = 0;
        switch (imageAngle)
        {
            case NINETYDEGREE_CLOCKWISE:
                angle = Math.PI / 2.0;
                newWidth = height;
                newHeight = width;
                break;
            case NINETYDEGREE_COUNTER_CLOCKWISE:
                newWidth = height;
                newHeight = width;
                angle = -Math.PI / 2.0;
                break;
            case UPSIDE_DOWN:
                angle = Math.PI;
                break;

        }
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = originalImage.getWidth(), h = originalImage.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
        //GraphicsConfiguration gc = getDefaultConfiguration();
        int type = (originalImage.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage result = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(originalImage, null);
        g.dispose();
        return result;
    }

    private static GraphicsConfiguration getDefaultConfiguration()
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    /**
     * Scale an image down to a new size. Keeps ratio.
     * @param originalImage the original image
     * @param newWidth the new width
     * @param newHeight the new height
     * @return a new image that is at most newWidth
     */
    public static BufferedImage scaleImage(BufferedImage originalImage, int newWidth, int newHeight)
    {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        double newRatio = (newWidth + 0.0) / (newHeight + 0.0);
        double ratio = (width + 0.0) / (height + 0.0);
        if (ratio > newRatio)
        {
            // original picture is larger
            // take newWidth
            newHeight = (int) Math.round((newWidth + 0.0) / ratio);
        } else
        {
            // original picture is taller
            // take newHeight
            newWidth = (int) Math.round((newHeight + 0.0) * ratio);
        }
        int type = (originalImage.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage tmp = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return tmp;
    }

    public static List<PhotoMetadata> getMetadata(File jpegFile) throws ImageProcessingException, IOException
    {
        // JDK7 : empty diamond
        List<PhotoMetadata> metadatas = new ArrayList<>();
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        for (Directory directory : metadata.getDirectories())
        {
            PhotoMetadata mymetadata = new PhotoMetadata();
            mymetadata.name = directory.getName();

            mymetadata.taken = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            mymetadata.angle = directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            for (Tag tag : directory.getTags())
            {
                mymetadata.tags.add(new PhotoTag(tag.getTagName(),
                        tag.getDescription()));
            }
            metadatas.add(mymetadata);
        }
        return metadatas;
    }

    /**
     * Returns the date and time when the photograph was taken, null if unable to retrieve.
     * @param jpegFile
     * @return null or Date
     * @throws ImageProcessingException
     * @throws IOException
     */
    public static Date getDateTimeTaken(File jpegFile) throws ImageProcessingException, IOException
    {
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
        if (directory == null)
        {
            return null;
        }
        return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
}
