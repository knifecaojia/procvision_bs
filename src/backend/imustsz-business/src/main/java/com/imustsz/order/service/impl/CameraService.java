package com.imustsz.order.service.impl;

import MvCameraControlWrapper.*;
import static MvCameraControlWrapper.MvCameraControlDefines.*;

import com.imustsz.order.service.ICameraService;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CameraService implements ICameraService {

    private Handle hCamera = null;
    private long lastProcessTime = 0;
    // 使用 AtomicBoolean 保证线程安全
    private AtomicBoolean isOpened = new AtomicBoolean(false);
    private AtomicBoolean isReconnecting = new AtomicBoolean(false);
    // 添加在类成员变量区域
    private volatile long lastFrameTime = System.currentTimeMillis();

    // 缓存数据
    private byte[] currentRawData;
    private MV_FRAME_OUT_INFO currentFrameInfo;
    private byte[] latestPreviewImage = null;

    // 重连线程池
    private ScheduledExecutorService reconnectScheduler;

    @PostConstruct
    public void init() {
        System.out.println("[Camera] 服务启动，开始初始化相机...");
        // 初始化 SDK
        int nRet = MvCameraControl.MV_CC_Initialize();
        if (MV_OK != nRet) {
            System.err.printf("[Camera] SDK初始化失败: [%#x]\n", nRet);
            return;
        }

        // 尝试首次连接，如果失败则自动进入重连模式
        startReconnectTask();
    }

    /**
     * 开启重连任务（包含心跳看门狗）
     */
    private void startReconnectTask() {
        if (isReconnecting.get()) return;
        isReconnecting.set(true);

        if (reconnectScheduler == null || reconnectScheduler.isShutdown()) {
            reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        System.out.println("[Camera] 启动自动重连与心跳监控...");

        // 每 3 秒检测一次
        reconnectScheduler.scheduleWithFixedDelay(() -> {
            try {
                if (isOpened.get()) {
                    long timeGap = System.currentTimeMillis() - lastFrameTime;
                    // 如果超过 5000ms (5秒) 没有收到图像，认为设备已死
                    if (timeGap > 5000) {
                        System.err.printf("[Camera] 心跳超时！已 %d ms 未收到图像，强制重置连接...\n", timeGap);
                        closeCamera();
                    }
                    return;
                }

                // 执行【重连逻辑】
                System.out.println("[Camera] 监控检测到相机离线，正在尝试搜索设备...");
                boolean success = connectCamera();
                if (success) {
                    System.out.println("[Camera] 相机重连成功！");
                }

            } catch (Exception e) {
                System.err.println("[Camera] 监控线程异常: " + e.getMessage());
            }

        }, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * 具体的连接逻辑
     * @return true if connected successfully
     */
    private synchronized boolean connectCamera() throws CameraControlException {
        int nRet = MV_OK;

        // 1. 枚举设备
        ArrayList<MV_CC_DEVICE_INFO> stDeviceList = MvCameraControl.MV_CC_EnumDevices(
                MV_GIGE_DEVICE | MV_USB_DEVICE | MV_GENTL_CAMERALINK_DEVICE
        );

        if (stDeviceList == null || stDeviceList.isEmpty()) {
            // 没找到设备，返回 false，让定时任务过 5 秒再试
            return false;
        }

        // 2. 创建句柄 (默认连接第 0 个)
        try {
            hCamera = MvCameraControl.MV_CC_CreateHandle(stDeviceList.get(0));
        } catch (CameraControlException e) {
            e.printStackTrace();
            return false;
        }

        // 3. 打开设备
        nRet = MvCameraControl.MV_CC_OpenDevice(hCamera);
        if (MV_OK != nRet) {
            System.err.printf("[Camera] 打开设备失败: [%#x]\n", nRet);
            MvCameraControl.MV_CC_DestroyHandle(hCamera);
            hCamera = null;
            return false;
        }

        // 当相机断开时，SDK 会自动调用这个回调
        nRet = MvCameraControl.MV_CC_RegisterExceptionCallBack(hCamera, new CameraExceptionCallBack() {
            @Override
            public int OnExceptionCallBack(int i) {
                System.err.printf("[Camera] !!! 发生异常/断线，错误类型: [%#x] !!!\n", i);
                return 1;
            }
        });

        if (MV_OK != nRet) {
            System.err.println("[Camera] 注册异常回调失败，无法自动检测断线！");
        }

        // 4. 注册图像回调 (实时流)
        nRet = MvCameraControl.MV_CC_RegisterImageCallBack(hCamera, new CameraImageCallBack() {
            @Override
            public int OnImageCallBack(byte[] bytes, MV_FRAME_OUT_INFO stFrameInfo) {
                processPreviewStream(bytes, stFrameInfo);
                return 0;
            }
        });

        if (MV_OK != nRet) {
            System.err.println("[Camera] 注册图像回调失败");
            closeCamera();
            return false;
        }


        // 设置自动曝光 (Continuous = 连续自动)
        nRet = MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "ExposureAuto", "Continuous");
        if (MV_OK != nRet) {
            System.err.println("开启自动曝光失败，尝试手动设置...");
            MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "ExposureAuto", "Off");
            MvCameraControl.MV_CC_SetFloatValue(hCamera, "ExposureTime", 20000.0f);
        }

        // 设置自动增益 (Continuous = 连续自动)
        MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "GainAuto", "Continuous");

        // 设置自动白平衡，防止画面偏色
        MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "BalanceWhiteAuto", "Continuous");

        //设置参数并开始采集
        MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "TriggerMode", "Off");
        nRet = MvCameraControl.MV_CC_StartGrabbing(hCamera);

        if (MV_OK == nRet) {
            isOpened.set(true);
            lastFrameTime = System.currentTimeMillis();
            return true;
        } else {
            closeCamera();
            return false;
        }
    }

    /**
     * 关闭并清理相机资源
     */
    private synchronized void closeCamera() {
        if (hCamera != null) {
            // 必须先停止采集，再关闭，再销毁
            MvCameraControl.MV_CC_StopGrabbing(hCamera);
            MvCameraControl.MV_CC_CloseDevice(hCamera);
            MvCameraControl.MV_CC_DestroyHandle(hCamera);
            hCamera = null;
        }
        isOpened.set(false);
        System.out.println("[Camera] 相机资源已释放，等待重连...");
    }


    /**
     * 修正后的实时流处理
     * 策略：使用“保存到文件”接口（因为这是目前确定存在的 API），保存后再读取为字节流
     */
    private void processPreviewStream(byte[] rawData, MV_FRAME_OUT_INFO stFrameInfo) {
        if (rawData == null) return;

        // 1. 【限流】降低频率，防止 CPU 爆满
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastProcessTime < 200) { // 200ms 处理一次
            return;
        }
        lastProcessTime = currentTime;
        lastFrameTime = System.currentTimeMillis();

        // 2. 【核心修复】保存当前帧的数据（必须深拷贝 Arrays.copyOf）
        // 如果不拷贝，回调结束后这块内存就失效了，拍照就会失败！
        synchronized (this) {
            this.currentRawData = Arrays.copyOf(rawData, rawData.length);
            // 这里 stFrameInfo 是对象，最好也 new 一个新的或者手动拷贝字段，
            // 但通常 SDK 的 Info 对象生命周期稍长，暂时直接引用，不行再深拷贝对象
            this.currentFrameInfo = stFrameInfo;
        }

        // 3. 生成预览流 (沿用之前的逻辑，存临时文件再读)
        MV_SAVE_IMAGE_PARAM_EX3 stSaveParam = new MV_SAVE_IMAGE_PARAM_EX3();

        int safeWidth = stFrameInfo.width > 0 ? stFrameInfo.width : stFrameInfo.ExtendWidth;
        int safeHeight = stFrameInfo.height > 0 ? stFrameInfo.height : stFrameInfo.ExtendHeight;

        stSaveParam.data = rawData;             // 输入数据
        stSaveParam.dataLen = stFrameInfo.frameLen;
        stSaveParam.pixelType = stFrameInfo.pixelType;
        stSaveParam.width = safeWidth;
        stSaveParam.height = safeHeight;

        stSaveParam.imageType = MV_SAVE_IAMGE_TYPE.MV_Image_Jpeg; // 输出格式
        stSaveParam.jpgQuality = 60;            // 压缩质量

        int outputSize = safeWidth * safeHeight * 3 + 2048;
        stSaveParam.bufferSize = outputSize;
        stSaveParam.imageBuffer = new byte[outputSize];

        int nRet = MvCameraControl.MV_CC_SaveImageEx3(hCamera, stSaveParam);

        if (MV_OK == nRet) {
            // 转换成功！
            // stSaveParam.nImageLen 是实际的 JPEG 图片大小
            // stSaveParam.pImageBuffer 里装的是 JPEG 数据，但后面有多余的空字节，需要截取
            byte[] finalJpeg = Arrays.copyOf(stSaveParam.imageBuffer, stSaveParam.imageLen);

            this.latestPreviewImage = finalJpeg;
        } else {
            System.err.printf("内存转码失败: [0x%x]\n", nRet);
        }

    }

    // 获取最新的预览图 (用于前端实时展示)
    public synchronized byte[] getLatestPreviewImage() {
        return this.latestPreviewImage;
    }

    /**
     * 高清拍照（内存转码版）
     * 逻辑与预览流一样，但是使用最高画质 (99)，且不丢帧
     */
    private byte[] convertRawToJpeg(byte[] rawData, MV_FRAME_OUT_INFO stFrameInfo) {
        if (rawData == null || stFrameInfo == null) return null;

        MV_SAVE_IMAGE_PARAM_EX3 stSaveParam = new MV_SAVE_IMAGE_PARAM_EX3();

        // 宽高容错
        int safeWidth = stFrameInfo.width > 0 ? stFrameInfo.width : stFrameInfo.ExtendWidth;
        int safeHeight = stFrameInfo.height > 0 ? stFrameInfo.height : stFrameInfo.ExtendHeight;

        stSaveParam.data = rawData;
        stSaveParam.dataLen = stFrameInfo.frameLen;
        stSaveParam.pixelType = stFrameInfo.pixelType;
        stSaveParam.width = safeWidth;
        stSaveParam.height = safeHeight;

        stSaveParam.imageType = MV_SAVE_IAMGE_TYPE.MV_Image_Jpeg;
        // 【关键点】拍照使用最高画质
        stSaveParam.jpgQuality = 99;

        // 分配足够大的内存 (宽*高*3 + 头部)
        int bufferSize = safeWidth * safeHeight * 3 + 2048;
        stSaveParam.bufferSize = bufferSize;
        stSaveParam.imageBuffer = new byte[bufferSize];

        // 执行转换
        int nRet = MvCameraControl.MV_CC_SaveImageEx3(hCamera, stSaveParam);

        if (MV_OK == nRet) {
            // 截取有效数据并返回
            return Arrays.copyOf(stSaveParam.imageBuffer, stSaveParam.imageLen);
        } else {
            System.err.printf("拍照转码失败: [0x%x]\n", nRet);
            return null;
        }
    }

    public byte[] snapShotFromCache() {
        byte[] rawDataCopy;
        MV_FRAME_OUT_INFO frameInfoCopy;

        // 【核心修复】加锁取出数据，防止取到写了一半的数据
        synchronized (this) {
            if (currentRawData == null || currentFrameInfo == null) {
                System.err.println("拍照失败：缓存中没有数据（可能相机还没启动或还没收到第一帧）");
                return null;
            }
            // 再次拷贝一份给拍照逻辑用，防止在使用过程中被回调线程修改
            rawDataCopy = Arrays.copyOf(currentRawData, currentRawData.length);
            frameInfoCopy = currentFrameInfo;
        }

        return convertRawToJpeg(rawDataCopy, frameInfoCopy);
    }

    @PreDestroy
    public void cleanup() {
        if (reconnectScheduler != null) reconnectScheduler.shutdownNow();
        closeCamera();
        MvCameraControl.MV_CC_Finalize();
    }

    @Override
    public int checkCamera() throws CameraControlException {
        ArrayList<MV_CC_DEVICE_INFO> stDeviceList = MvCameraControl.MV_CC_EnumDevices(MV_USB_DEVICE);
        return stDeviceList.size();
    }
}