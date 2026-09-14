package com.imustsz.collect.service.impl;

import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/** Full-resolution re-encoding. Never resize original pixels: annotation coordinates stay valid. */
public class CollectionImageCodec {
    public static void convert(Path source, Path target, Path thumbnail, String format, Integer quality, long maxPixels) throws IOException {
        if (!("jpeg".equals(format) || "png".equals(format))) throw new IOException("不支持的目标格式");
        if ("jpeg".equals(format) && (quality == null || quality < 1 || quality > 100)) throw new IOException("JPEG质量无效");
        BufferedImage original = null, opaque = null, thumb = null;
        try (FileImageInputStream input = new FileImageInputStream(source.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IOException("无法识别原图格式");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, false, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long)width * height > maxPixels) throw new IOException("原图像素超过配置上限，未转换");
                if (reader.getNumImages(true) > 1) throw new IOException("多帧/多页图像不支持此转换，避免丢失帧数据");
                original = reader.read(0);
                BufferedImage encoded = original;
                if ("jpeg".equals(format) && (original.getColorModel().hasAlpha() || original.getColorModel() instanceof java.awt.image.IndexColorModel)) {
                    opaque = draw(original, width, height, false); encoded = opaque;
                }
                write(encoded, target, format, quality);
                double scale = Math.min(1.0, 800.0 / Math.max(width, height));
                thumb = draw(encoded, Math.max(1,(int)Math.round(width*scale)), Math.max(1,(int)Math.round(height*scale)), "png".equals(format) && encoded.getColorModel().hasAlpha());
                write(thumb, thumbnail, format, quality);
            } finally { reader.dispose(); }
        } finally {
            if (original != null) original.flush();
            if (opaque != null) opaque.flush();
            if (thumb != null) thumb.flush();
        }
    }
    private static BufferedImage draw(BufferedImage source, int width, int height, boolean alpha) {
        BufferedImage image = new BufferedImage(width,height,alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            if (!alpha) { g.setColor(Color.WHITE); g.fillRect(0,0,width,height); }
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(source,0,0,width,height,null);
        } finally { g.dispose(); }
        return image;
    }
    private static void write(BufferedImage image, Path path, String format, Integer quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) throw new IOException("目标格式编码器不可用");
        ImageWriter writer = writers.next();
        try (FileImageOutputStream output = new FileImageOutputStream(path.toFile())) {
            writer.setOutput(output);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            if ("jpeg".equals(format)) {
                parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                parameters.setCompressionQuality(quality / 100f);
            }
            writer.write(null, new IIOImage(image,null,null), parameters);
        } finally { writer.dispose(); }
    }
}
