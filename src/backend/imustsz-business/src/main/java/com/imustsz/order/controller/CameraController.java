package com.imustsz.order.controller;

import MvCameraControlWrapper.CameraControlException;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.order.service.ICameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/camera")
public class CameraController extends BaseController {

//    @Autowired
    private ICameraService cameraService;

    // 1. 实时预览接口 (前端使用 <img src="/api/camera/preview" /> 并设置定时刷新)
    @GetMapping(value = "/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPreview() throws CameraControlException {

        byte[] img = cameraService.getLatestPreviewImage();
        if (img == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img);
    }

    // 2. 拍照接口 (点击按钮 -> 保存高质量图片)
    @GetMapping(value = "/capture", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> capturePhoto() {
        // 使用 Demo 中的 SaveImageToFile 逻辑，确保稳健
        byte[] img = cameraService.snapShotFromCache();

        if (img == null) {
            return ResponseEntity.status(500).body(null);
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=capture.jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(img);
    }

    @GetMapping("/check")
    public AjaxResult checkCamera() throws CameraControlException {
        int count = cameraService.checkCamera();
        return count > 0 ? success("相机已启动") : error("未找到相机");
    }
}
