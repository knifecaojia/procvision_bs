<template>
  <el-dialog title="图片标注" v-model="labelVisible" width="1000px" :close-on-click-modal="false">
    <div class="annotator-container">

      <div v-if="stepIds.length > 1" class="step-navigator">
        <el-button type="info" plain size="small" icon="ArrowLeft" @click="prevStep" :disabled="currentIndex === 0">上一步</el-button>
        <span style="margin: 0 15px; font-weight: bold; color: #409EFF">
          当前进度：{{ currentIndex + 1 }} / {{ stepIds.length }}
        </span>
        <el-button type="info" plain size="small" @click="nextStep" :disabled="currentIndex === stepIds.length - 1">下一步<el-icon class="el-icon--right"><ArrowRight /></el-icon></el-button>
      </div>

      <el-card class="toolbar">
        <el-space>
          <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleFileChange"
          >
            <el-button type="primary" icon="FolderOpened">本地上传</el-button>
          </el-upload>

          <el-button type="success" icon="Camera" @click="openCameraDialog">打开相机</el-button>

          <el-divider direction="vertical"/>

          <el-button
              type="warning"
              @click="toggleDrawMode"
              :plain="!isDrawingMode"
              icon="Edit"
          >
            {{ isDrawingMode ? '结束标注' : '开始标注' }}
          </el-button>

          <el-button type="danger" @click="clearCanvasAnnotations" icon="Delete">清空标注</el-button>
          <el-button type="primary" @click="uploadToMinio" icon="Check">保存标注结果</el-button>
        </el-space>

        <div class="status-text" v-if="isDrawingMode">
          当前状态：<span style="color: red">绘制中...</span>
          <span style="margin-left: 15px;">Tips：开启绘制后按住ALT+鼠标左键可以拖拽图片</span>
        </div>
      </el-card>

      <div class="canvas-wrapper" v-loading="canvasLoading" element-loading-text="正在加载原图...">
        <canvas id="c"></canvas>
      </div>

      <el-dialog
          v-model="dialogVisible"
          title="添加标注文字"
          width="300px"
          append-to-body
          :close-on-click-modal="false"
          @close="cancelAnnotation"
      >
        <el-input
            v-model="labelText"
            placeholder="请输入标签"
            @keyup.enter="confirmLabel"
            ref="inputRef"
        />
        <el-input
            style="margin-top: 20px;"
            v-model="remark"
            placeholder="请输入备注"
            @keyup.enter="confirmLabel"
            ref="inputRef"
        />
        <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelAnnotation">取消</el-button>
          <el-button type="primary" @click="confirmLabel">确定</el-button>
        </span>
        </template>
      </el-dialog>
    </div>

    <el-dialog
        v-model="cameraVisible"
        title="相机实时监控"
        width="850px"
        append-to-body
        @close="closeCameraDialog"
    >
      <div class="camera-preview-box">
        <video ref="videoRef" class="live-stream" autoplay playsinline v-show="cameraConnected"></video>
        <div v-if="!cameraConnected" class="camera-loading">
          <el-icon class="is-loading" :size="40"><Loading/></el-icon>
          <p>正在启动摄像头...</p>
        </div>
      </div>
      <div class="camera-controls">
         <span style="color: #909399; margin-right: 20px;">
           <el-icon><VideoCamera/></el-icon> 本地相机模式
         </span>
        <el-button type="primary" size="large" icon="CameraFilled" :loading="isCapturing" @click="handleCapture">
          立即抓拍并去标注
        </el-button>
      </div>
    </el-dialog>

  </el-dialog>
</template>

<script setup>
import {ref, computed, watch, nextTick, getCurrentInstance} from 'vue';
import {fabric} from 'fabric';
import {getUploadUrl} from "@/api/algorithm/algorithm.js";
import {updateStep, getStep} from "@/api/craft/step.js";
import axios from "axios";

const {proxy} = getCurrentInstance()

const labelVisible = defineModel()
const canvas = ref(null);
const isDrawingMode = ref(false);
const dialogVisible = ref(false);
const labelText = ref('');
const inputRef = ref(null);
const stepCount = ref(0);
const uploadFile = ref(null)
const remark = ref('')
const canvasLoading = ref(false);

const cameraVisible = ref(false);
const cameraConnected = ref(false);
const isCapturing = ref(false);
const videoRef = ref(null);
let mediaStream = null;
const BASE_API = import.meta.env.VITE_APP_BASE_API;

let isMouseDown = false;
let startX = 0;
let startY = 0;
let activeRect = null;

const props = defineProps({
  visible: Boolean,
  stepIds: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['change-status'])

// 连续标注进度控制
const currentIndex = ref(0);
const currentStepId = computed(() => props.stepIds[currentIndex.value]);

watch(() => props.visible, (visible) => {
  if (visible) {
    currentIndex.value = 0;
    nextTick(() => {
      initCanvas();
      loadCurrentStepData();
    })
  } else {
    isDrawingMode.value = false;
    if (canvas.value) canvas.value.clear();
    stopLocalCamera();
  }
})

// 加载当前进度对应工步的历史原图（若有）
const loadCurrentStepData = async () => {
  if (!currentStepId.value) return;

  canvasLoading.value = true;
  uploadFile.value = null; // 重置文件
  stepCount.value = 1;
  clearCanvasAnnotations();

  try {
    const res = await getStep(currentStepId.value);
    const urlsStr = res.data.guideMapUrl;

    if (urlsStr) {
      let urls = [];
      try {
        urls = JSON.parse(urlsStr); // 解析后端返回的 JSON 数组
      } catch (e) {
        urls = [urlsStr]; // 兼容旧版本只有一张图字符串的情况
      }

      const originalUrl = urls[0]; // 约定数组第一项是原图
      if (originalUrl) {
        const fullUrl = originalUrl.startsWith('http') ? originalUrl : BASE_API + originalUrl;

        // 使用 fetch 获取图片并转为 File，保证可以再次被上传
        const response = await fetch(fullUrl, { cache: "no-cache" });
        const blob = await response.blob();
        const file = new File([blob], `history_${Date.now()}.jpg`, { type: blob.type });
        loadFileToCanvas(file);
      }
    } else {
      // 当前工步没有原图，如果不是第一步，则默认保留上一步的底图(不清空 canvas 背景)
      if (currentIndex.value === 0 && canvas.value) {
        canvas.value.clear();
      }
    }
  } catch (err) {
    console.error("加载工步历史图片失败", err);
  } finally {
    canvasLoading.value = false;
  }
}

// 导航功能
const prevStep = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
    loadCurrentStepData();
  }
}

const nextStep = () => {
  if (currentIndex.value < props.stepIds.length - 1) {
    currentIndex.value++;
    loadCurrentStepData();
  }
}

const initCanvas = () => {
  if (canvas.value) {
    canvas.value.dispose();
  }
  canvas.value = new fabric.Canvas('c', {
    width: 800,
    height: 600,
    selection: true
  });
  initZoom();
  canvas.value.on('mouse:down', onMouseDown);
  canvas.value.on('mouse:move', onMouseMove);
  canvas.value.on('mouse:up', onMouseUp);
  window.addEventListener('keydown', handleKeydown);
};

// ... 此处保留原有的 initZoom, handleKeydown 等函数不变 ...
const handleKeydown = (e) => {
  if (e.key === 'Delete' || e.key === 'Backspace') {
    if (!canvas.value) return;
    const activeObjects = canvas.value.getActiveObjects();
    if (activeObjects.length) {
      canvas.value.discardActiveObject();
      activeObjects.forEach((obj) => {
        canvas.value.remove(obj);
        stepCount.value -= 1;
      });
      canvas.value.requestRenderAll();
    }
  }
};

const initZoom = () => {
  canvas.value.on('mouse:wheel', function (opt) {
    const delta = opt.e.deltaY;
    let zoom = canvas.value.getZoom();
    zoom *= 0.999 ** delta;
    if (zoom > 20) zoom = 20;
    if (zoom < 0.1) zoom = 0.1;
    canvas.value.zoomToPoint({x: opt.e.offsetX, y: opt.e.offsetY}, zoom);
    opt.e.preventDefault();
    opt.e.stopPropagation();
  });

  canvas.value.on('mouse:down', function (opt) {
    const evt = opt.e;
    if (evt.altKey === true) {
      this.isDragging = true;
      this.selection = false;
      this.lastPosX = evt.clientX;
      this.lastPosY = evt.clientY;
    }
  });

  canvas.value.on('mouse:move', function (opt) {
    if (this.isDragging) {
      const e = opt.e;
      const vpt = this.viewportTransform;
      vpt[4] += e.clientX - this.lastPosX;
      vpt[5] += e.clientY - this.lastPosY;
      this.requestRenderAll();
      this.lastPosX = e.clientX;
      this.lastPosY = e.clientY;
    }
  });

  canvas.value.on('mouse:up', function (opt) {
    if (this.isDragging) {
      this.setViewportTransform(this.viewportTransform);
      this.isDragging = false;
      this.selection = true;
    }
  });
};


const loadFileToCanvas = (file) => {
  uploadFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    const imgObj = new Image();
    imgObj.src = e.target.result;
    imgObj.onload = () => {
      canvas.value.clear();
      stepCount.value = 1;
      const fImg = new fabric.Image(imgObj);
      const canvasWidth = canvas.value.getWidth();
      const canvasHeight = canvas.value.getHeight();
      const scale = Math.min(canvasWidth / fImg.width, canvasHeight / fImg.height);
      canvas.value.setBackgroundImage(fImg, canvas.value.renderAll.bind(canvas.value), {
        scaleX: scale, scaleY: scale,
        top: canvasHeight / 2, left: canvasWidth / 2,
        originX: 'center', originY: 'center'
      });
      canvas.value.setViewportTransform([1, 0, 0, 1, 0, 0]);
    };
  };
  reader.readAsDataURL(file);
};

// const handleFileChange = (file) => loadFileToCanvas(file.raw);

// 修改后：拦截上传文件并进行压缩
const handleFileChange = async (file) => {
  proxy.$modal.loading('正在压缩处理图片...');
  try {
    // 限制最大 1920x1080，质量 0.8
    const compressedFile = await compressImage(file.raw, 1920, 1080, 0.8);
    loadFileToCanvas(compressedFile);
  } catch (error) {
    console.error("图片压缩失败", error);
    proxy.$modal.msgError('图片处理失败，已回退至原图');
    loadFileToCanvas(file.raw); // 容错：如果压缩失败，尝试硬加载原图
  } finally {
    proxy.$modal.closeLoading();
  }
};

// --- 相机逻辑保持不变 ---
const openCameraDialog = async () => {
  cameraVisible.value = true;
  cameraConnected.value = false;
  await nextTick();
  startLocalCamera();
};

const startLocalCamera = async () => {
  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    try {
      mediaStream = await navigator.mediaDevices.getUserMedia({ video: { width: { ideal: 1920 }, height: { ideal: 1080 } } });
      if (videoRef.value) { videoRef.value.srcObject = mediaStream; videoRef.value.play(); cameraConnected.value = true; }
    } catch (err) {
      proxy.$modal.msgError('无法启动摄像头');
      cameraVisible.value = false;
    }
  } else { proxy.$modal.msgError('浏览器不支持访问摄像头'); }
};

const closeCameraDialog = () => { stopLocalCamera(); cameraVisible.value = false; };
const stopLocalCamera = () => {
  if (mediaStream) { mediaStream.getTracks().forEach(track => track.stop()); mediaStream = null; }
  if (videoRef.value) videoRef.value.srcObject = null;
  cameraConnected.value = false;
};

const handleCapture = () => {
  if (!videoRef.value || !cameraConnected.value) return;
  isCapturing.value = true;
  try {
    const video = videoRef.value;
    const cvs = document.createElement('canvas');
    cvs.width = video.videoWidth; cvs.height = video.videoHeight;
    cvs.getContext('2d').drawImage(video, 0, 0, cvs.width, cvs.height);
    cvs.toBlob((blob) => {
      if (!blob) return;
      loadFileToCanvas(new File([blob], `capture_${Date.now()}.jpg`, { type: 'image/jpeg' }));
      proxy.$modal.msgSuccess('抓拍成功！');
      closeCameraDialog();
      isCapturing.value = false;
    }, 'image/jpeg', 1);
  } catch (error) { proxy.$modal.msgError('抓拍异常'); isCapturing.value = false; }
};

// --- 标注事件 ---
const toggleDrawMode = () => {
  if (!canvas.value.backgroundImage) return proxy.$modal.msgWarning('请先上传图片或使用相机拍照！');
  isDrawingMode.value = !isDrawingMode.value;
  canvas.value.skipTargetFind = isDrawingMode.value;
  canvas.value.selection = !isDrawingMode.value;
};

const onMouseDown = (opt) => {
  if (!isDrawingMode.value) return;
  isMouseDown = true;
  const pointer = canvas.value.getPointer(opt.e);
  startX = pointer.x; startY = pointer.y;
  activeRect = new fabric.Rect({ left: startX, top: startY, width: 0, height: 0, fill: 'rgba(255, 0, 0, 0)', stroke: 'red', strokeWidth: 2, selectable: false, evented: false });
  canvas.value.add(activeRect);
};

const onMouseMove = (opt) => {
  if (!isDrawingMode.value || !isMouseDown) return;
  const pointer = canvas.value.getPointer(opt.e);
  if (pointer.x < startX) activeRect.set({left: pointer.x});
  if (pointer.y < startY) activeRect.set({top: pointer.y});
  activeRect.set({width: Math.abs(pointer.x - startX), height: Math.abs(pointer.y - startY)});
  canvas.value.renderAll();
};

const onMouseUp = () => {
  if (!isDrawingMode.value || !isMouseDown) return;
  isMouseDown = false;
  if (activeRect.width < 5 || activeRect.height < 5) { canvas.value.remove(activeRect); activeRect = null; return; }
  dialogVisible.value = true;
  nextTick(() => { inputRef.value?.focus(); });
};

const confirmLabel = () => {
  if (!labelText.value || !remark.value) return proxy.$modal.msgWarning('请输入标签和备注');
  const text = new fabric.Text(labelText.value, { fontSize: 16, fill: 'white', backgroundColor: 'red', left: activeRect.left, top: activeRect.top - 20 < 0 ? activeRect.top : activeRect.top - 20, padding: 5 });
  const group = new fabric.Group([activeRect, text], { left: activeRect.left, top: text.top, selectable: true });
  group.set({ customData: { label: labelText.value, remark: remark.value } });
  canvas.value.remove(activeRect);
  stepCount.value += 1;
  canvas.value.add(group); canvas.value.setActiveObject(group); canvas.value.renderAll();
  dialogVisible.value = false; labelText.value = ''; remark.value = ''; activeRect = null;
};

const cancelAnnotation = () => {
  if (activeRect) { canvas.value.remove(activeRect); canvas.value.renderAll(); }
  dialogVisible.value = false; labelText.value = ''; remark.value = ''; activeRect = null;
};

// 仅清空标注（保留背景）
const clearCanvasAnnotations = () => {
  if (!canvas.value) return;
  const objects = canvas.value.getObjects();
  objects.forEach(obj => canvas.value.remove(obj));
  stepCount.value = 1;
  canvas.value.requestRenderAll();
};

// --- 修改后的上传逻辑：双图上传并支持连续提示 ---
const uploadToMinio = async () => {
  if (!canvas.value || canvas.value.getObjects().length === 0) {
    return proxy.$modal.msgWarning('请先完成标注！')
  }
  if (!uploadFile.value) {
    return proxy.$modal.msgError('未找到原始图片文件');
  }

  proxy.$modal.loading('正在上传图片和标注数据...')

  try {
    // 1. 获取两次上传链接（分别给原图和标注图）
    const resOrig = await getUploadUrl();
    const resAnnot = await getUploadUrl();

    // 2. 上传原图
    await axios.put(resOrig.data.url, uploadFile.value, {
      headers: {'Content-Type': uploadFile.value.type || 'image/jpeg'}
    });

    // 3. 上传标注截图
    const annotationFile = getFile();
    await axios.put(resAnnot.data.url, annotationFile, {
      headers: {'Content-Type': 'image/jpeg'}
    });

    // 4. 将两个 objectName 封装为列表 JSON 字符串
    const urlList = [resOrig.data.objectName, resAnnot.data.objectName];

    const data = {
      id: currentStepId.value,
      guideMapUrl: JSON.stringify(urlList), // 返回给后端的列表
      coordsInfo: JSON.stringify(getDataEasy()),
    }
    await updateStep(data)

    emit('change-status')
    proxy.$modal.msgSuccess('保存成功！');

    // 5. 提示是否进行下一次标注（如果是批量/连续标注）
    if (props.stepIds.length > 1) {
      proxy.$modal.confirm('已保存当前结果。是否清空标注并开始下一个工步？', '连续标注提示', {
        confirmButtonText: '下一工步',
        cancelButtonText: '留在当前'
      }).then(() => {
        clearCanvasAnnotations();
        if (currentIndex.value < props.stepIds.length - 1) {
          currentIndex.value++;
          loadCurrentStepData(); // 会尝试拉取下一工步的原图，如果没有则继续使用当前画布的原图
        } else {
          proxy.$modal.msgSuccess('当前已经是最后一个工步');
        }
      }).catch(() => {});
    } else {
      // 并非连续标注或者只有一个，保存完不清空窗口，可自行点击关闭
    }

  } catch (error) {
    console.error(error);
    proxy.$modal.msgError('绑定失败，请检查网络');
  } finally {
    proxy.$modal.closeLoading()
  }
};

const getDataEasy = () => {
  const bgImg = canvas.value.backgroundImage;
  const bgRect = bgImg.getBoundingRect();
  const scale = bgImg.scaleX;
  const labelSet = new Set();

  const results = canvas.value.getObjects().filter(obj => obj.type === 'group' && obj.customData).map(group => {
    const gLeft = group.left; const gTop = group.top;
    const gScaleX = group.scaleX; const gScaleY = group.scaleY;
    const OFFSET_Y = 20;
    const rectCanvasLeft = gLeft;
    const rectCanvasTop = gTop + (OFFSET_Y * gScaleY);
    const rectObj = group.getObjects().find(o => o.type === 'rect');
    const rectCanvasWidth = rectObj.width * rectObj.scaleX * gScaleX;
    const rectCanvasHeight = rectObj.height * rectObj.scaleY * gScaleY;

    labelSet.add(group.customData.label);
    return {
      label: group.customData.label,
      pos: {
        x: Math.round((rectCanvasLeft - bgRect.left) / scale),
        y: Math.round((rectCanvasTop - bgRect.top) / scale),
        width: Math.round(rectCanvasWidth / scale),
        height: Math.round(rectCanvasHeight / scale),
        remark: group.customData.remark,
      }
    };
  });

  const coordsInfo = []
  labelSet.forEach(label => {
    let tempCoordsList = []
    results.forEach(result => { if (result.label === label) tempCoordsList.push(result.pos) })
    coordsInfo.push({ label: label, posList: tempCoordsList })
  })
  return coordsInfo;
}

const getFile = () => {
  const bgImage = canvas.value.backgroundImage;
  const currentScale = bgImage ? bgImage.scaleX : 1;
  const multiplier = 1 / currentScale;
  const dataURL = canvas.value.toDataURL({ format: 'jpeg', quality: 0.9, multiplier: multiplier });
  const blob = dataURLtoBlob(dataURL);
  return new File([blob], `annotated_${Date.now()}.jpg`, {type: 'image/jpeg'});
}

const dataURLtoBlob = (dataurl) => {
  const arr = dataurl.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) u8arr[n] = bstr.charCodeAt(n);
  return new Blob([u8arr], {type: mime});
};

/**
 * 纯前端图片压缩函数
 * @param {File} file 原始图片文件
 * @param {number} maxWidth 最大宽度
 * @param {number} maxHeight 最大高度
 * @param {number} quality 压缩质量 (0.1 - 1.0)
 * @returns {Promise<File>} 返回压缩后的 File 对象
 */
const compressImage = (file, maxWidth = 1920, maxHeight = 1080, quality = 0.8) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) => {
      const img = new Image();
      img.src = event.target.result;
      img.onload = () => {
        let targetWidth = img.width;
        let targetHeight = img.height;

        // 等比缩放计算
        if (targetWidth > maxWidth || targetHeight > maxHeight) {
          const ratio = Math.min(maxWidth / targetWidth, maxHeight / targetHeight);
          targetWidth = Math.round(targetWidth * ratio);
          targetHeight = Math.round(targetHeight * ratio);
        }

        const offscreenCanvas = document.createElement('canvas');
        offscreenCanvas.width = targetWidth;
        offscreenCanvas.height = targetHeight;
        const ctx = offscreenCanvas.getContext('2d');

        // 绘制压缩图
        ctx.drawImage(img, 0, 0, targetWidth, targetHeight);

        // 导出为 Blob
        offscreenCanvas.toBlob((blob) => {
          if (blob) {
            // 保持原有文件名，但强制转为 jpeg 格式以减小体积
            const newFileName = file.name.replace(/\.[^/.]+$/, "") + ".jpg";
            const compressedFile = new File([blob], newFileName, {
              type: 'image/jpeg',
              lastModified: Date.now()
            });
            resolve(compressedFile);
          } else {
            reject(new Error('Canvas to Blob failed'));
          }
        }, 'image/jpeg', quality);
      };
      img.onerror = (e) => reject(e);
    };
    reader.onerror = (e) => reject(e);
  });
};
</script>

<style scoped>
.annotator-container { display: flex; flex-direction: column; gap: 20px; align-items: center; padding: 10px; }
.step-navigator { display: flex; align-items: center; justify-content: center; width: 100%; max-width: 850px; margin-bottom: -10px; }
.toolbar { width: 100%; max-width: 850px; }
.canvas-wrapper { border: 1px solid #ccc; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); min-height: 600px; }
.status-text { margin-top: 10px; font-size: 14px; color: #666; }
.camera-preview-box { width: 100%; height: 500px; background-color: #000; display: flex; justify-content: center; align-items: center; border-radius: 4px; overflow: hidden; margin-bottom: 20px; }
.live-stream { width: 100%; height: 100%; object-fit: contain; }
.camera-loading { color: #fff; text-align: center; }
.camera-controls { display: flex; justify-content: flex-end; align-items: center; padding: 0 10px; }
</style>