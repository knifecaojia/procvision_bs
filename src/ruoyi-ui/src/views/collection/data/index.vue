<template>
  <div class="collection-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Monitor /></el-icon> 图像采集
          </span>
        </div>
      </template>

      <el-row :gutter="40">
        <el-col :span="9">
          <div class="step-container">
            <h3 class="step-header">Step 1: 扫码</h3>
            <div class="step-content">
              <el-input
                  v-model="barcodeInput"
                  ref="barcodeInputRef"
                  placeholder="鼠标此处后进行扫码"
                  prefix-icon="Scissor"
                  clearable
                  size="large"
                  :disabled="!!currentBarcode"
                  @keyup.enter="handleScan"
              >
                <template #append>
                  <el-button @click="handleScan" :icon="currentBarcode ? 'Select' : 'ArrowRight'">
                    {{ currentBarcode ? '已锁定' : '确认' }}
                  </el-button>
                </template>
              </el-input>
              <div class="tip-text" v-if="!currentBarcode">请先扫描码以激活采集功能</div>
            </div>

            <transition name="el-zoom-in-top">
              <div class="current-task" v-if="currentBarcode">
                <div class="task-header">
                  <span class="task-label">产品信息</span>
                </div>

                <el-scrollbar max-height="120px" class="task-content-scroll">
                  <div class="task-value">{{ currentBarcode }}</div>
                </el-scrollbar>

                <div class="task-actions">
                  <el-button type="danger" link size="small" icon="RefreshLeft" @click="resetFlow(false)">
                    重新开始
                  </el-button>
                </div>
              </div>
            </transition>
          </div>

          <div class="action-area" v-if="resultFile">
            <el-divider>Step 3: 归档</el-divider>
            <div class="file-info">
              <el-tag type="info" size="small">待上传: {{ resultFile.name }}</el-tag>
              <span class="file-size">{{ (resultFile.size / 1024).toFixed(1) }} KB</span>
            </div>
            <el-button
                type="primary"
                size="large"
                class="submit-btn"
                @click="submitData"
                :loading="submitting"
                icon="UploadFilled"
            >
              保存并上传
            </el-button>
          </div>
        </el-col>

        <el-col :span="15">
          <div class="step-container media-container">
            <div class="media-header">
              <h3 class="step-header" style="margin:0; border:none">Step 2: 图像采集</h3>
              <el-radio-group v-model="mode" size="small" @change="handleModeChange" :disabled="!currentBarcode">
                <el-radio-button label="camera">摄像头拍照</el-radio-button>
                <el-radio-button label="upload">本地上传</el-radio-button>
              </el-radio-group>
            </div>

            <div v-show="mode === 'camera'" class="camera-wrapper">
              <div class="video-box" v-show="!previewImage">
                <video ref="videoRef" autoplay playsinline muted class="video-stream"></video>
                <div class="camera-mask" v-if="!isCameraOpen">
                  <el-button type="primary" icon="VideoCamera" @click="startCamera" :disabled="!currentBarcode">
                    打开摄像头
                  </el-button>
                  <p class="mask-tip" v-if="!currentBarcode">请先锁定条码</p>
                </div>
              </div>

              <div class="preview-box" v-if="previewImage">
                <img :src="previewImage" class="captured-img" />
                <div class="re-capture-overlay">
                  <el-button type="warning" icon="Refresh" round @click="clearCapture">重拍</el-button>
                </div>
              </div>

              <div class="camera-controls" v-if="isCameraOpen && !previewImage">
                <el-button type="danger" circle size="large" class="shutter-btn" @click="takePhoto" icon="Camera"></el-button>
                <div class="control-tip">点击拍照</div>
              </div>
            </div>

            <div v-show="mode === 'upload'" class="upload-wrapper">
              <el-upload
                  drag
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleFileChange"
                  accept="image/*"
                  :disabled="!currentBarcode"
              >
                <img v-if="previewImage" :src="previewImage" class="upload-preview" />
                <div v-else>
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">拖拽图片或 <em>点击上传</em></div>
                </div>
              </el-upload>
              <div v-if="previewImage" style="text-align: center; margin-top: 10px;">
                <el-button type="text" icon="Delete" @click="clearCapture">清除重选</el-button>
              </div>
            </div>

          </div>
        </el-col>
      </el-row>
    </el-card>

    <canvas ref="canvasRef" style="display: none;"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue';
import axios from "axios";
import { getUploadUrl } from "@/api/algorithm/algorithm.js";
import {addData} from "@/api/collection/data.js";

const { proxy } = getCurrentInstance();

// --- 状态管理 ---
const barcodeInput = ref('');
const currentBarcode = ref('');
const barcodeInputRef = ref(null);
const submitting = ref(false);

const mode = ref('camera'); // 'camera' | 'upload'
const resultFile = ref(null); // 最终要上传的文件对象 (Blob/File)
const previewImage = ref(''); // 最终图片的预览 URL

// 相机相关 Ref
const videoRef = ref(null);
const canvasRef = ref(null);
const isCameraOpen = ref(false);
let mediaStream = null;

// --- 生命周期 ---
onMounted(() => {
  focusInput();
});

onBeforeUnmount(() => {
  stopCamera();
});


const focusInput = () => {
  nextTick(() => {
    barcodeInputRef.value?.focus();
  });
};

// --- Step 1: 扫码 ---
const handleScan = () => {
  if (!barcodeInput.value) return proxy.$modal.msgWarning('请输入条码');
  currentBarcode.value = barcodeInput.value;
  proxy.$modal.msgSuccess('条码锁定，请采集图像');

  // 如果是相机模式，自动开启摄像头
  if (mode.value === 'camera') {
    startCamera();
  }
};

// --- Step 2: 相机控制逻辑 (核心) ---

// 打开摄像头
const startCamera = async () => {
  if (!currentBarcode.value) return;

  // 浏览器兼容性检查
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    proxy.$modal.msgError('当前浏览器不支持访问摄像头，请使用 Chrome/Edge');
    return;
  }

  try {
    // 请求视频流 (ideal 参数可以设置分辨率)
    const stream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1920 },
        height: { ideal: 1080 },
        facingMode: "environment" // 优先后置摄像头(移动端有效)
      }
    });

    mediaStream = stream;
    if (videoRef.value) {
      videoRef.value.srcObject = stream;
      videoRef.value.play(); // 必须调用 play
    }
    isCameraOpen.value = true;
  } catch (err) {
    console.error("摄像头启动失败:", err);
    let msg = '无法启动摄像头';
    if (err.name === 'NotAllowedError') msg = '请允许浏览器访问摄像头权限';
    if (err.name === 'NotFoundError') msg = '未检测到摄像头设备';
    proxy.$modal.msgError(msg);
  }
};

// 关闭摄像头
const stopCamera = () => {
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop());
    mediaStream = null;
  }
  isCameraOpen.value = false;
};

// 拍照 (Canvas 截图)
const takePhoto = () => {
  if (!videoRef.value || !canvasRef.value) return;

  const video = videoRef.value;
  const canvas = canvasRef.value;
  const context = canvas.getContext('2d');

  // 设置画布尺寸与视频实际尺寸一致
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;

  // 绘制当前帧
  context.drawImage(video, 0, 0, canvas.width, canvas.height);

  // 转换为 Blob (文件对象)
  canvas.toBlob((blob) => {
    const filename = `${currentBarcode.value}_${Date.now()}.jpg`;
    // 将 Blob 封装为 File 对象
    resultFile.value = new File([blob], filename, { type: 'image/jpeg' });

    // 生成预览
    previewImage.value = URL.createObjectURL(blob);

    // 拍照后暂停视频流，节省资源（可选，这里选择不关流，只是遮挡）
    stopCamera();
  }, 'image/jpeg', 0.95); // 0.95 是图片质量
};

// 清除抓拍，重新拍照
const clearCapture = () => {
  resultFile.value = null;
  previewImage.value = '';
  // 如果是相机模式，确保相机是开着的
  if (mode.value === 'camera' && !isCameraOpen.value) {
    startCamera();
  }
};

// --- 文件上传模式逻辑 ---
const handleFileChange = (file) => {
  const rawFile = file.raw;
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    proxy.$modal.msgError('仅支持 JPG/PNG 图片');
    return;
  }
  resultFile.value = rawFile;
  previewImage.value = URL.createObjectURL(rawFile);
};

const handleModeChange = (val) => {
  if (val === 'upload') {
    stopCamera();
    clearCapture();
  } else {
    // 切换回相机
    clearCapture();
    if (currentBarcode.value) {
      startCamera();
    }
  }
};

// --- Step 3: 上传归档 (通用) ---
const submitData = async () => {
  if (!resultFile.value || !currentBarcode.value) return;

  submitting.value = true;
  try {
    // 1. 获取MinIO上传地址
    let uploadUrl = '';
    let objectName = '';
    await getUploadUrl().then(res => {
      uploadUrl = res.data.url;
      objectName = res.data.objectName;
    });

    // 2. 上传文件 (无论是拍照的Blob还是本地File，处理方式一样)
    await axios.put(uploadUrl, resultFile.value, {
      headers: { 'Content-Type': resultFile.value.type }
    });

    // 3. 提交业务数据
    const postData = {
      data: currentBarcode.value,
      imagePath: objectName,
    };

    await addData(postData);

    proxy.$modal.msgSuccess('归档成功！');

    resetFlow(true);

  } catch (e) {
    console.error(e);
    proxy.$modal.msgError('上传失败');
  } finally {
    submitting.value = false;
  }
};

const resetFlow = () => {
  barcodeInput.value = '';
  currentBarcode.value = '';
  resultFile.value = null;
  previewImage.value = '';
  if (mode.value === 'upload') {
    // clear logic
  }
  focusInput();
};
</script>

<style scoped>
.collection-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.step-container {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.step-header {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  color: #303133;
}

.media-container {
  height: 500px; /* 固定高度 */
  display: flex;
  flex-direction: column;
}

.media-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

/* 摄像头区域 */
.camera-wrapper {
  flex: 1;
  position: relative;
  background: #000;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.video-box, .preview-box {
  flex: 1;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.video-stream, .captured-img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持比例 */
}

.camera-mask {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(30, 30, 30, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.mask-tip {
  color: #909399;
  margin-top: 10px;
  font-size: 12px;
}

.camera-controls {
  height: 80px;
  background: rgba(0,0,0,0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: absolute;
  bottom: 0;
  width: 100%;
  z-index: 20;
}

.shutter-btn {
  width: 50px;
  height: 50px;
  font-size: 20px;
  border: 4px solid rgba(255,255,255,0.3);
}

.control-tip {
  color: #fff;
  font-size: 12px;
  margin-top: 5px;
}

.re-capture-overlay {
  position: absolute;
  bottom: 20px;
  right: 20px;
}

/* 上传区域 */
.upload-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  background: #f9fafc;
}
.upload-preview {
  max-height: 300px;
  max-width: 100%;
}

/* 状态和操作区 */
/* 修改原有的 .current-task 样式 */
.current-task {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 4px;
  margin-top: 15px;
  overflow: hidden; /* 防止溢出 */
  display: flex;
  flex-direction: column;
}

/* 新增：头部布局 */
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 15px 0 15px;
}

.task-label {
  color: #67C23A;
  font-size: 12px;
  font-weight: bold;
}

/* 关键修改：内容区域样式 */
.task-content-scroll {
  width: 100%;
  margin: 5px 0;
}

.task-value {
  padding: 0 15px;       /*以此保留左右边距*/
  font-size: 16px;       /* 调小字体，原 24px 太大 */
  line-height: 1.5;      /* 增加行高提升可读性 */
  font-weight: bold;
  color: #303133;

  /* 核心代码：处理长文本 */
  word-break: break-all; /* 强制英文/数字换行 */
  white-space: pre-wrap; /* 保留原有换行符（如果有） */
  text-align: left;      /* 长文本左对齐更易读 */
  font-family: monospace;/* 等宽字体更适合看序列号 */
}

/* 底部按钮区 */
.task-actions {
  background-color: rgba(103, 194, 58, 0.1);
  padding: 5px 0;
  text-align: center;
  border-top: 1px solid #e1f3d8;
}
.task-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 5px 0;
}
.submit-btn {
  width: 100%;
  margin-top: 10px;
}
.tip-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
.file-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  color: #606266;
}
</style>