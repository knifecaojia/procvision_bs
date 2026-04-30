package com.imustsz.collect.service.impl;

import com.imustsz.collect.service.IOpenCvProcessService;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class OpenCvProcessServiceImpl implements IOpenCvProcessService {
    @Override
    public String processAndReturnBase64(byte[] imageBytes, String processType) {
        Mat src = null;
        Mat dst = null;
        try {
            // 1. byte[] 转 OpenCV 的 Mat 对象
            src = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
            if (src.empty()) {
                throw new RuntimeException("图片解码失败");
            }

            dst = new Mat();

            // 2. 根据类型调用不同的 OpenCV 算法
            switch (processType.toUpperCase()) {
                case "GRAY": // 灰度化
                    Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                    break;
                case "BLUR": // 高斯模糊 (去噪)
                    Imgproc.GaussianBlur(src, dst, new org.opencv.core.Size(5, 5), 0);
                    break;
                case "EDGE_CANNY": // Canny 边缘检测 (特征提取)
                    Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY); // Canny前通常先灰度化
                    Imgproc.Canny(dst, dst, 100, 200);
                    break;
                default:
                    src.copyTo(dst); // 不处理，原图返回
            }

            // 3. Mat 转回 byte[] 再转 Base64
            MatOfByte outputBytes = new MatOfByte();
            // 编码为 jpg 或 png
            Imgcodecs.imencode(".jpg", dst, outputBytes);

            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(outputBytes.toArray());

        } finally {
            // ⚠️ 极其重要：手动释放 C++ 层的内存，防止内存泄漏！
            if (src != null) src.release();
            if (dst != null) dst.release();
        }
    }
}
