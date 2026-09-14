package com.imustsz.order.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/** Disk-backed source and subsampled decoding: never buffer a large source file in heap. */
@Component
public class ReportImageProcessor {
    private final long maxSourceBytes;
    private final int maxEdge;
    private final Semaphore permits;

    public ReportImageProcessor(@Value("${assembly.report.max-image-mb:256}") int maxImageMb,
                                @Value("${assembly.report.image-max-edge:2048}") int maxEdge,
                                @Value("${assembly.report.image-concurrency:2}") int concurrency) {
        if (maxImageMb < 1 || maxEdge < 256 || maxEdge > 4096 || concurrency < 1 || concurrency > 8) {
            throw new IllegalArgumentException("Invalid assembly.report image configuration");
        }
        this.maxSourceBytes = maxImageMb * 1024L * 1024L;
        this.maxEdge = maxEdge;
        this.permits = new Semaphore(concurrency, true);
    }

    public long getMaxSourceBytes() { return maxSourceBytes; }

    public void writeJpeg(InputStream source, OutputStream output) throws IOException {
        boolean acquired = false;
        Path temporary = null;
        try {
            acquired = permits.tryAcquire(90, TimeUnit.SECONDS);
            if (!acquired) throw new IOException("报告图像处理繁忙，请稍后重试");
            temporary = Files.createTempFile("assembly-report-image-", ".source");
            try (OutputStream file = Files.newOutputStream(temporary)) {
                copyBounded(source, file);
            }
            try (FileImageInputStream imageInput = new FileImageInputStream(temporary.toFile())) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
                if (!readers.hasNext()) throw new IOException("报告不支持此图片格式，请使用JPEG、PNG、BMP、GIF或TIFF");
                ImageReader reader = readers.next();
                BufferedImage sampled = null;
                BufferedImage rgb = null;
                try {
                    reader.setInput(imageInput, true, true);
                    int width = reader.getWidth(0), height = reader.getHeight(0);
                    if (width <= 0 || height <= 0) throw new IOException("图片尺寸无效");
                    // Tell the decoder to sample before allocating the output raster.
                    int sample = (int) Math.max(1L, ((long) Math.max(width, height) + maxEdge - 1) / maxEdge);
                    ImageReadParam parameters = reader.getDefaultReadParam();
                    parameters.setSourceSubsampling(sample, sample, 0, 0);
                    sampled = reader.read(0, parameters);
                    if (sampled == null) throw new IOException("图片解码失败");
                    double scale = Math.min(1.0, (double) maxEdge / Math.max(sampled.getWidth(), sampled.getHeight()));
                    int outWidth = Math.max(1, (int) Math.round(sampled.getWidth() * scale));
                    int outHeight = Math.max(1, (int) Math.round(sampled.getHeight() * scale));
                    rgb = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = rgb.createGraphics();
                    try {
                        graphics.setColor(Color.WHITE);
                        graphics.fillRect(0, 0, outWidth, outHeight);
                        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        graphics.drawImage(sampled, 0, 0, outWidth, outHeight, null);
                    } finally { graphics.dispose(); }
                    if (!ImageIO.write(rgb, "jpeg", output)) throw new IOException("报告图片编码失败");
                } finally {
                    if (sampled != null) sampled.flush();
                    if (rgb != null) rgb.flush();
                    reader.dispose();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("报告图像处理已中断", ex);
        } finally {
            try { if (temporary != null) Files.deleteIfExists(temporary); }
            finally { if (acquired) permits.release(); }
        }
    }

    public void copyBounded(InputStream source, OutputStream output) throws IOException {
        byte[] buffer = new byte[64 * 1024];
        long total = 0;
        int read;
        while ((read = source.read(buffer)) != -1) {
            total += read;
            if (total > maxSourceBytes) throw new IOException("图片超过配置上限" + maxSourceBytes / 1024 / 1024 + "MB");
            output.write(buffer, 0, read);
        }
    }
}
