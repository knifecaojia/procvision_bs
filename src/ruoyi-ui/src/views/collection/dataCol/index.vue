<template>
  <div class="collection-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Monitor/></el-icon> 图像采集
          </span>
        </div>
      </template>

      <el-row :gutter="40">
        <el-col :span="9">
          <div class="step-container">
            <h3 class="step-header">1：扫码</h3>
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
            <el-divider>3：归档</el-divider>
            <div class="file-info">
              <el-tag type="info" size="small" :type="isProcessed ? 'success' : 'info'">
                {{ isProcessed ? '已处理待上传' : '待上传' }}: {{ resultFile.name }}
              </el-tag>
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
              <h3 class="step-header" style="margin:0; border:none">2：图像采集和处理</h3>
              <el-radio-group v-model="mode" size="small" @change="handleModeChange"
                              :disabled="!currentBarcode || processing">
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
                <img :src="previewImage" class="captured-img"/>
                <div class="re-capture-overlay">
                  <el-button type="warning" icon="Refresh" round @click="clearCapture" :disabled="processing">重拍
                  </el-button>
                </div>
              </div>

              <div class="camera-controls" v-if="isCameraOpen && !previewImage">
                <el-button type="danger" circle size="large" class="shutter-btn" @click="takePhoto"
                           icon="Camera"></el-button>
                <div class="control-tip">点击拍照</div>
              </div>
            </div>

            <div v-show="mode === 'upload'" class="upload-wrapper">
              <el-upload
                  v-if="!previewImage"
                  drag
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleFileChange"
                  accept="image/*"
                  :disabled="!currentBarcode"
              >
                <div>
                  <el-icon class="el-icon--upload">
                    <upload-filled/>
                  </el-icon>
                  <div class="el-upload__text">拖拽图片或 <em>点击上传</em></div>
                </div>
              </el-upload>
              <div v-else class="preview-box">
                <img :src="previewImage" class="captured-img"/>
                <div style="text-align: center; margin-top: 10px; margin-right: 10px">
                  <el-button type="text" icon="Delete" @click="clearCapture" :disabled="processing">清除重选</el-button>
                </div>
              </div>
            </div>

            <transition name="el-fade-in">
              <div class="algorithm-toolbar" v-if="previewImage">
                <div class="toolbar-title">
                  <el-icon>
                    <MagicStick/>
                  </el-icon>
                  图像预处理与特征提取
                </div>
                <div class="toolbar-actions">
                  <el-button-group>
                    <el-button type="primary" plain size="small" @click="processImage('GRAY')" :loading="processing"
                               icon="Picture">灰度化
                    </el-button>
                    <el-button type="primary" plain size="small" @click="processImage('BLUR')" :loading="processing"
                               icon="Filter">高斯去噪
                    </el-button>
                    <el-button type="primary" plain size="small" @click="processImage('EDGE_CANNY')"
                               :loading="processing" icon="Scissor">边缘提取
                    </el-button>
                    <el-button type="warning" plain size="small" @click="openCropperDialog" :disabled="processing" icon="Crop">手动裁剪</el-button>
                  </el-button-group>
                  <el-button type="info" plain size="small" @click="resetOriginalImage"
                             :disabled="!isProcessed || processing" icon="RefreshLeft" style="margin-left: 10px;">
                    恢复原图
                  </el-button>
                </div>
              </div>
            </transition>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog
        v-model="cropDialogVisible"
        title="图像裁剪"
        width="800px"
        append-to-body
        destroy-on-close
        @opened="initCropper"
        @closed="destroyCropper"
    >
      <div style="width: 100%; height: 500px; background: #000; overflow: hidden;">
        <img
            ref="cropperImgRef"
            :src="previewImage"
            style="display: block; max-width: 100%;"
            alt="crop-target"
        />
      </div>

      <template #footer>
    <span class="dialog-footer">
      <el-button @click="cropDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmCrop" :loading="cropping">确认裁剪</el-button>
    </span>
      </template>
    </el-dialog>

    <canvas ref="canvasRef" style="display: none;"></canvas>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance} from 'vue';
import axios from "axios";
import {getUploadUrl} from "@/api/algorithm/algorithm.js";
import {addData, checkExist, updateData, uploadData} from "@/api/collection/data.js";
import 'cropperjs/dist/cropper.css';
import Cropper from 'cropperjs';

const {proxy} = getCurrentInstance();

// --- 状态管理 ---
const barcodeInput = ref('');
const currentBarcode = ref('');
const barcodeInputRef = ref(null);
const submitting = ref(false);

// --- 状态管理 ---
const mode = ref('camera');
const rawCaptureFile = ref(null); // 🌟 新增：这是绝对的“相机底片”，永远不被污染
const originalFile = ref(null);   // OpenCV 算法处理的基础图 (可能是裁剪后的)
const resultFile = ref(null);     // 最终要上传的文件
const previewImage = ref('');     // 预览 URL

// 新增：算法处理状态
const processing = ref(false);
const isProcessed = ref(false); // 标记当前展示的是否为处理后的图片

// 相机相关 Ref
const videoRef = ref(null);
const canvasRef = ref(null);
const isCameraOpen = ref(false);
let mediaStream = null;

const cropDialogVisible = ref(false);
const cropping = ref(false);
const cropperImgRef = ref(null); // 指向模板里的 <img>
let cropperInstance = null;      // 原生 cropper 实例，不使用 ref 包裹以避免 Proxy 污染

// 打开弹窗
const openCropperDialog = () => {
  if (!previewImage.value) return;
  cropDialogVisible.value = true;
};

// 弹窗动画结束，初始化 Cropper
const initCropper = () => {
  // 如果已存在实例，先销毁
  if (cropperInstance) {
    cropperInstance.destroy();
  }

  // 确保 DOM 已经拿到
  if (cropperImgRef.value) {
    cropperInstance = new Cropper(cropperImgRef.value, {
      viewMode: 1,           // 限制裁剪框不能超出图片范围
      dragMode: 'crop',      // 允许在画布上画出新的裁剪框
      autoCropArea: 0.8,     // 自动生成一个占据画面 80% 的裁剪框
      restore: false,
      guides: true,          // 显示九宫格辅助线
      center: true,          // 显示中心点
      highlight: false,
      cropBoxMovable: true,  // 允许拖动裁剪框
      cropBoxResizable: true,// 允许调整裁剪框大小
      toggleDragModeOnDblclick: false,
    });
  }
};

// 弹窗关闭，清理内存
const destroyCropper = () => {
  if (cropperInstance) {
    cropperInstance.destroy();
    cropperInstance = null;
  }
};

// 确认裁剪，输出结果
const confirmCrop = () => {
  if (!cropperInstance) return;
  cropping.value = true;

  // 获取裁剪后的 Canvas 并转为 Blob
  const canvas = cropperInstance.getCroppedCanvas({
    imageSmoothingEnabled: true,
    imageSmoothingQuality: 'high',
  });

  if (!canvas) {
    proxy.$modal.msgError('裁剪器未就绪');
    cropping.value = false;
    return;
  }

  canvas.toBlob((blob) => {
    if (!blob) {
      proxy.$modal.msgError('裁剪失败');
      cropping.value = false;
      return;
    }

    // 1. 更新预览图
    previewImage.value = URL.createObjectURL(blob);

    // 2. 将 Blob 转为 File 对象
    const originalName = originalFile.value ? originalFile.value.name : 'capture.jpg';
    const newFilename = originalName.replace(/\.[^/.]+$/, "") + `_cropped.jpg`;
    const croppedFile = new File([blob], newFilename, { type: 'image/jpeg' });

    // 3. 更新当前文件，使其可用于后续的 Canny 边缘检测等算法
    originalFile.value = croppedFile;
    resultFile.value = croppedFile;
    isProcessed.value = true;

    cropDialogVisible.value = false;
    cropping.value = false;
    proxy.$modal.msgSuccess('裁剪成功');
  }, 'image/jpeg', 0.95); // 0.95 保证高质量输出
};

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

  if (mode.value === 'camera') {
    startCamera();
  }
};

// --- Step 2: 相机控制逻辑 ---
const startCamera = async () => {
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    const isSecure = window.isSecureContext;
    if (!isSecure) {
      proxy.$modal.msgError('浏览器安全限制：不能使用摄像头');
    } else {
      proxy.$modal.msgError('当前浏览器不支持摄像头，请更换浏览器');
    }
    return;
  }

  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: {ideal: 1920},
        height: {ideal: 1080},
        facingMode: "environment"
      }
    });

    mediaStream = stream;
    if (videoRef.value) {
      videoRef.value.srcObject = stream;
      videoRef.value.play();
    }
    isCameraOpen.value = true;
  } catch (err) {
    let msg = '无法启动摄像头';
    if (err.name === 'NotAllowedError') msg = '请允许浏览器访问摄像头权限';
    if (err.name === 'NotFoundError') msg = '未检测到摄像头设备';
    proxy.$modal.msgError(msg);
  }
};

const stopCamera = () => {
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop());
    mediaStream = null;
  }
  isCameraOpen.value = false;
};

const takePhoto = () => {
  if (!videoRef.value || !canvasRef.value) return;

  const video = videoRef.value;
  const canvas = canvasRef.value;
  const context = canvas.getContext('2d');

  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;
  context.drawImage(video, 0, 0, canvas.width, canvas.height);

  canvas.toBlob((blob) => {
    const filename = `${currentBarcode.value}_${Date.now()}.jpg`;
    const file = new File([blob], filename, {type: 'image/jpeg'});

    rawCaptureFile.value = file; // 🌟 存入底片
    originalFile.value = file;
    resultFile.value = file;
    previewImage.value = URL.createObjectURL(blob);
    isProcessed.value = false;

    stopCamera();
  }, 'image/jpeg', 0.95);
};

const clearCapture = () => {
  originalFile.value = null;
  resultFile.value = null;
  previewImage.value = '';
  isProcessed.value = false;
  if (mode.value === 'camera' && !isCameraOpen.value) {
    startCamera();
  }
};

const handleFileChange = (file) => {
  const rawFile = file.raw;
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    proxy.$modal.msgError('仅支持 JPG/PNG 图片');
    return;
  }
  rawCaptureFile.value = rawFile; // 🌟 存入底片
  originalFile.value = rawFile;
  resultFile.value = rawFile;
  previewImage.value = URL.createObjectURL(rawFile);
  isProcessed.value = false;
};

const handleModeChange = (val) => {
  if (val === 'upload') {
    stopCamera();
    clearCapture();
  } else {
    clearCapture();
    if (currentBarcode.value) startCamera();
  }
};

// --- 新增：OpenCV 算法处理逻辑 ---
const processImage = async (algorithmType) => {
  // 1. 逻辑锁：如果正在处理中，直接拦截，防止DOM未及时禁用导致的连点

  if (processing.value) {
    // 可选：给个轻提示，或者直接 return 默默拦截
    // proxy.$modal.msgWarning('图像处理中，请稍候...');
    return;
  }

  if (!originalFile.value) return;

  processing.value = true;
  try {
    const formData = new FormData();
    formData.append('file', originalFile.value);
    formData.append('type', algorithmType);

    const res = await uploadData(formData)
    const base64Str = res.data;

    previewImage.value = base64Str;

    const newFilename = originalFile.value.name.replace(/\.[^/.]+$/, "") + `_${algorithmType}.jpg`;
    resultFile.value = base64ToFile(base64Str, newFilename);
    isProcessed.value = true;

    proxy.$modal.msgSuccess('图像处理完成');
  } catch (e) {
    // 若依抛出的错误有时是直接的字符串，有时是 Error 对象
    const errMsg = typeof e === 'string' ? e : (e.message || '');

    // 识别若依拦截器的防重复提交报错，安静地退出
    if (errMsg.includes('数据正在处理，请勿重复提交') || errMsg.includes('repeat submit')) {
      console.warn('重复请求已被拦截');
      return;
    }

    // 真正的后端报错才走这里
    console.error(e);
    proxy.$modal.msgError('算法处理失败，请检查后端服务');
  } finally {
    // 注意：如果有强烈的连续点击场景，这里甚至可以加一个极短的 setTimeout
    // setTimeout(() => { processing.value = false; }, 300);
    processing.value = false;
  }
};

// 恢复原图
const resetOriginalImage = () => {
  if (!rawCaptureFile.value) return;
  originalFile.value = rawCaptureFile.value;
  resultFile.value = rawCaptureFile.value;
  previewImage.value = URL.createObjectURL(rawCaptureFile.value);
  isProcessed.value = false;
};

// 工具方法：Base64 转 File 对象
const base64ToFile = (dataurl, filename) => {
  let arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
      bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new File([u8arr], filename, {type: mime});
}

const generateThumbnail = (file, maxWidth = 800) => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');

      // 等比例缩放计算
      const scale = maxWidth / img.width;
      // 如果原图宽度本来就比 maxWidth 小，则不放大，保持原尺寸
      if (scale >= 1) {
        canvas.width = img.width;
        canvas.height = img.height;
      } else {
        canvas.width = maxWidth;
        canvas.height = img.height * scale;
      }

      // 绘制图像到 canvas
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

      // 导出为 Blob
      canvas.toBlob((blob) => {
        const thumbFilename = file.name.replace(/\.[^/.]+$/, "_thumb.jpg");
        const thumbFile = new File([blob], thumbFilename, { type: 'image/jpeg' });
        URL.revokeObjectURL(img.src);
        resolve(thumbFile);
      }, 'image/jpeg', 0.8); // 0.8 为压缩质量，可按需调整
    };
    img.onerror = reject;
    img.src = URL.createObjectURL(file);
  });
};

// --- Step 3: 上传归档 ---
const submitData = async () => {
  if (!resultFile.value || !currentBarcode.value) return;

  submitting.value = true;
  try {
    // ================= 1. 上传原图 =================
    let origUploadUrl = '';
    let origObjectName = '';
    await getUploadUrl().then(res => {
      origUploadUrl = res.data.url;
      origObjectName = res.data.objectName;
    });

    await axios.put(origUploadUrl, resultFile.value, {
      headers: { 'Content-Type': resultFile.value.type }
    });

    // ================= 2. 生成并上传缩略图 =================
    const thumbFile = await generateThumbnail(resultFile.value, 800);

    let thumbUploadUrl = '';
    let thumbObjectName = '';
    await getUploadUrl().then(res => {
      thumbUploadUrl = res.data.url;
      thumbObjectName = res.data.objectName;
    });

    await axios.put(thumbUploadUrl, thumbFile, {
      headers: { 'Content-Type': thumbFile.type }
    });

    // ================= 3. 构造并提交业务数据 =================
    const postData = {
      data: currentBarcode.value,
      imagePath: JSON.stringify([origObjectName, thumbObjectName])
    };

    const isExist = await checkExist(postData.data)

    if (!isExist.data) {
      await addData(postData);
      proxy.$modal.msgSuccess('归档成功！');
      resetFlow(true);
    }else{
      proxy.$modal.confirm('数据已存在，是否替换？').then(async function () {
        postData.id = isExist.data.id
        await updateData(postData)
        resetFlow(true)
      });
    }


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
  originalFile.value = null;
  resultFile.value = null;
  previewImage.value = '';
  isProcessed.value = false;
  focusInput();
};
</script>

<style scoped>
/* 原有样式保留 */
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
  height: 550px; /* 增加了一点高度以容纳操作栏 */
  display: flex;
  flex-direction: column;
}

.media-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.camera-wrapper, .upload-wrapper {
  flex: 1;
  position: relative;
  background: #000;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.upload-wrapper {
  background: #f9fafc;
  border: 2px dashed #dcdfe6;
  justify-content: center;
  align-items: center;
}

.video-box, .preview-box {
  flex: 1;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.video-stream, .captured-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.camera-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  background: rgba(0, 0, 0, 0.8);
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
  border: 4px solid rgba(255, 255, 255, 0.3);
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

/* 新增：算法工具栏样式 */
.algorithm-toolbar {
  margin-top: 15px;
  padding: 10px 15px;
  background-color: #f0f2f5;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #e4e7ed;
}

.toolbar-title {
  font-size: 13px;
  color: #606266;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 5px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
}

/* 状态和操作区 */
.current-task {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 4px;
  margin-top: 15px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

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

.task-content-scroll {
  width: 100%;
  margin: 5px 0;
}

.task-value {
  padding: 0 15px;
  font-size: 16px;
  line-height: 1.5;
  font-weight: bold;
  color: #303133;
  word-break: break-all;
  white-space: pre-wrap;
  text-align: left;
  font-family: monospace;
}

.task-actions {
  background-color: rgba(103, 194, 58, 0.1);
  padding: 5px 0;
  text-align: center;
  border-top: 1px solid #e1f3d8;
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
.cropper-container {
  width: 100%;
  height: 500px;
  /* 下面这两行是防止溢出的绝对核心 */
  position: relative;
  overflow: hidden;
  background: #000;   /* 工业风建议用纯黑底色，更容易看清边缘 */
  border-radius: 4px;
}
</style>