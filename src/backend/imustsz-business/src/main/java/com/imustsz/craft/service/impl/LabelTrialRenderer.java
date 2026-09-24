package com.imustsz.craft.service.impl;

import com.fasterxml.jackson.databind.JsonNode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.*;

/**
 * No resizing: boxes and output both use the original pixel grid.
 */
public class LabelTrialRenderer {
    public static void render(Path source, Path target, int width, int height, JsonNode groups, boolean packaging, long maxPixels) throws IOException {
        if (width <= 0 || height <= 0 || (long) width * height > maxPixels)
            throw new IOException("图片像素超过配置上限，未缩放或保存");
        rejectRotatedExif(source);
        BufferedImage image = null;
        try (ImageInputStream input = new FileImageInputStream(source.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IOException("服务器不支持该图片编码，请使用JPEG、PNG或BMP");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                if (reader.getWidth(0) != width || reader.getHeight(0) != height)
                    throw new IOException("浏览器与原图尺寸不一致，已停止保存以避免坐标错位");
                if (reader.getNumImages(true) != 1) throw new IOException("试验版不支持多帧图片");
                image = reader.read(0);
            } finally {
                reader.dispose();
            }
            if (image.getColorModel() instanceof java.awt.image.IndexColorModel) {
                BufferedImage expanded = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D copy = expanded.createGraphics();
                try {
                    copy.drawImage(image, 0, 0, null);
                } finally {
                    copy.dispose();
                }
                image.flush();
                image = expanded;
            }
            if (!packaging) {
                Graphics2D g = image.createGraphics();
                try {
                    // Match the existing red, unfilled rectangles. Never bake transient hover styles into output.
                    g.setColor(Color.RED);
                    g.setStroke(new BasicStroke(1));
                    for (JsonNode group : groups)
                        for (JsonNode box : group.path("posList")) {
                            int x = integer(box, "x"), y = integer(box, "y"), w = integer(box, "width"), h = integer(box, "height");
                            if (x < 0 || y < 0 || w <= 0 || h <= 0 || (long) x + w > width || (long) y + h > height)
                                throw new IOException("标注框超出原图范围");
                            g.drawRect(x, y, w, h);
                        }
                } finally {
                    g.dispose();
                }
            }
            // Lossless PNG preserves dimensions and avoids another lossy JPEG generation.
            if (!ImageIO.write(image, "png", target.toFile())) throw new IOException("PNG编码器不可用");
        } finally {
            if (image != null) image.flush();
        }
    }

    private static int integer(JsonNode box, String field) throws IOException {
        JsonNode n = box.get(field);
        if (n == null || !n.isIntegralNumber() || !n.canConvertToInt())
            throw new IOException("标注坐标必须为原图整数像素");
        return n.intValue();
    }

    /**
     * Browser auto-orientation differs from ImageIO. Refuse these inputs rather than silently shift coordinates.
     */
    private static void rejectRotatedExif(Path file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file.toFile())))) {
            if (in.readUnsignedShort() != 0xffd8) return;
            while (true) {
                int prefix = in.readUnsignedByte();
                if (prefix != 255) return;
                int marker;
                do {
                    marker = in.readUnsignedByte();
                } while (marker == 255);
                if (marker == 0xda || marker == 0xd9) return;
                if (marker == 1 || marker >= 0xd0 && marker <= 0xd7) continue;
                int length = in.readUnsignedShort() - 2;
                if (length < 0) throw new IOException("JPEG段长度无效");
                byte[] data = new byte[length];
                in.readFully(data);
                if (marker != 0xe1 || length < 14 || data[0] != 'E' || data[1] != 'x' || data[2] != 'i' || data[3] != 'f')
                    continue;
                try {
                    ByteBuffer b = ByteBuffer.wrap(data).order(data[6] == 'I' ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
                    int offset = 6 + b.getInt(10), count = Short.toUnsignedInt(b.getShort(offset));
                    for (int i = 0; i < count; i++) {
                        int p = offset + 2 + i * 12;
                        if (Short.toUnsignedInt(b.getShort(p)) == 0x112) {
                            int orientation = Short.toUnsignedInt(b.getShort(p + 8));
                            if (orientation != 1)
                                throw new IOException("原图含EXIF旋转或镜像，请先统一图片方向后再标注；未保存错位坐标");
                        }
                    }
                } catch (IndexOutOfBoundsException ex) {
                    throw new IOException("EXIF数据无效", ex);
                }
            }
        }
    }
}
