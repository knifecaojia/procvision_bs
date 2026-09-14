package com.imustsz.order;

import com.imustsz.order.service.impl.ReportImageProcessor;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.*;
import java.util.Set;
import java.util.stream.Collectors;

/** Standalone real-image regression. Run main with -Xmx96m and a writable fixture directory. */
public class ReportLargeImageRegressionTest {
    public static void main(String[] args) throws Exception {
        Path directory = Paths.get(args[0]);
        Files.createDirectories(directory);
        ReportImageProcessor processor = new ReportImageProcessor(256, 2048, 1);
        Set<String> before = temporaryFiles();
        for (int height : new int[]{3500, 5600}) {
            Path source = directory.resolve("large-" + height + ".bmp");
            try {
                writeBmp(source, 5000, height);
                long size = Files.size(source);
                if (size < (height == 3500 ? 50L : 80L) * 1024 * 1024) throw new AssertionError("Fixture too small");
                ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
                try (InputStream input = Files.newInputStream(source)) { processor.writeJpeg(input, jpeg); }
                BufferedImage result = ImageIO.read(new ByteArrayInputStream(jpeg.toByteArray()));
                if (result == null || result.getWidth() > 2048 || result.getHeight() > 2048) throw new AssertionError("Invalid output dimensions");
                if (Math.abs((double)result.getWidth() / result.getHeight() - 5000.0 / height) > .005) throw new AssertionError("Aspect ratio changed");
                System.out.println("PASS: source=" + size + " bytes, JPEG=" + jpeg.size() + " bytes, dimensions=" + result.getWidth() + "x" + result.getHeight());
                result.flush();
                try (InputStream input = Files.newInputStream(source)) {
                    new ReportImageProcessor(1, 2048, 1).writeJpeg(input, new ByteArrayOutputStream());
                    throw new AssertionError("Source size limit ignored");
                } catch (IOException expected) {
                    if (!expected.getMessage().contains("1MB")) throw expected;
                }
            } finally { Files.deleteIfExists(source); }
        }
        try {
            processor.writeJpeg(new ByteArrayInputStream(new byte[]{1,2,3}), new ByteArrayOutputStream());
            throw new AssertionError("Invalid image accepted");
        } catch (IOException expected) { }
        // The prior failure must release its semaphore and delete its temporary file.
        BufferedImage transparent = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(transparent, "png", png); transparent.flush();
        ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
        processor.writeJpeg(new ByteArrayInputStream(png.toByteArray()), jpeg);
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(jpeg.toByteArray()));
        if ((result.getRGB(50,50) & 0xffffff) != 0xffffff) throw new AssertionError("Transparent image should have white background");
        result.flush();
        if (!temporaryFiles().equals(before)) throw new AssertionError("Temporary image files leaked");
        System.out.println("PASS: 50MB/80MB sources under 96MB JVM heap; bounded dimensions, source limit, invalid image, permit recovery, transparency and temp cleanup.");
    }

    private static Set<String> temporaryFiles() throws IOException {
        try (java.util.stream.Stream<Path> paths = Files.list(Paths.get(System.getProperty("java.io.tmpdir")))) {
            return paths.map(p -> p.getFileName().toString()).filter(n -> n.startsWith("assembly-report-image-")).collect(Collectors.toSet());
        }
    }
    private static void writeBmp(Path path, int width, int height) throws IOException {
        int stride = (width * 3 + 3) & ~3;
        ByteBuffer header = ByteBuffer.allocate(54).order(ByteOrder.LITTLE_ENDIAN);
        header.put((byte)'B').put((byte)'M').putInt(54 + stride * height).putInt(0).putInt(54);
        header.putInt(40).putInt(width).putInt(height).putShort((short)1).putShort((short)24);
        header.putInt(0).putInt(stride * height).putInt(2835).putInt(2835).putInt(0).putInt(0);
        try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(path))) {
            output.write(header.array());
            byte[] row = new byte[stride];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    row[x * 3] = (byte)(x % 256); row[x * 3 + 1] = (byte)(y % 256); row[x * 3 + 2] = (byte)128;
                }
                output.write(row);
            }
        }
    }
}
