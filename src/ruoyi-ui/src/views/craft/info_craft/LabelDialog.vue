<template>
  <el-dialog title="图片标注" v-model="labelVisible" width="1200px" :close-on-click-modal="false">
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
          <span style="margin-left: 15px;">Tips：开启绘制后按住ALT+鼠标左键可以拖拽图片，选中框后按 Delete 键可删除</span>
        </div>
      </el-card>

      <div class="main-workspace">
        <div class="canvas-wrapper" v-loading="canvasLoading" element-loading-text="正在加载原图...">
          <canvas id="c"></canvas>
        </div>

        <div class="annotation-list-wrapper">
          <el-card shadow="never" class="annotation-card">
            <template #header>
              <div class="card-header">
                <span>标注信息面板</span>
                <el-tag type="info" size="small">共 {{ annotationList.length }} 个</el-tag>
              </div>
            </template>
            <el-scrollbar height="500px">
              <el-empty v-if="annotationList.length === 0" description="暂无标注信息" :image-size="80" />
              <div
                  v-for="(item, index) in annotationList"
                  :key="item.id"
                  class="annotation-item"
                  @mouseenter="highlightAnnotation(item.id, true)"
                  @mouseleave="highlightAnnotation(item.id, false)"
              >
                <div class="item-info">
                  <div class="item-label"><el-tag size="small">{{ item.label }}</el-tag></div>
                  <div class="item-remark">{{ item.remark }}</div>
                </div>
                <el-button type="danger" icon="Delete" circle plain size="small" @click="removeAnnotation(index, item.id)"></el-button>
              </div>
            </el-scrollbar>
          </el-card>
        </div>
      </div>

      <el-dialog
          v-model="dialogVisible"
          title="添加标注信息"
          width="300px"
          append-to-body
          :close-on-click-modal="false"
          @close="cancelAnnotation"
      >
        <el-select v-model="labelText" placeholder="请选择标签" style="width: 100%; margin-bottom: 20px;">
          <el-option v-if="props.tempCraftType === 'TX'" v-for="item in label_tianxian" :key="item.value" :label="item.label" :value="item.value"></el-option>
          <el-option v-else v-for="item in label_banji" :key="item.label" :label="item.label" :value="item.value"></el-option>
        </el-select>
<!--        <el-input-->
<!--            v-model="labelText"-->
<!--            placeholder="请输入标签"-->
<!--            @keyup.enter="confirmLabel"-->
<!--            ref="inputRef"-->
<!--        />-->
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

const {label_tianxian, label_banji} = proxy.useDict("label_tianxian", 'label_banji')
const labelVisible = defineModel()
const canvas = ref(null);
const isDrawingMode = ref(false);
const dialogVisible = ref(false);
const labelText = ref('');
const inputRef = ref(null);
const stepCount = ref(0);

// 新增：右侧标注列表状态管理
const annotationList = ref([]);

// 用于保存最原始、未被任何压缩的物理图片文件，上传给后端必用它
const uploadFile = ref(null);

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
  },
  tempCraftType: {
    type: String,
    default: ''
  },
  borrowImageUrl: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['change-status'])

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
    annotationList.value = [];
  }
})

// 加载当前进度对应工步的历史原图
const loadCurrentStepData = async () => {
  if (!currentStepId.value) return;

  canvasLoading.value = true;
  stepCount.value = 1;

  clearCanvasAnnotations();

  try {
    const res = await getStep(currentStepId.value);
    let urlsStr = res.data.guideMapUrl;

    if (!urlsStr && res.data.code === '99' && props.borrowImageUrl) {
      // 伪造成后端返回的格式喂给画板
      urlsStr = JSON.stringify([props.borrowImageUrl]);
    }

    let coordsInfo = [];
    if (res.data.coordsInfo) {
      try {
        coordsInfo = JSON.parse(res.data.coordsInfo);
      } catch (e) {
      }
    }

    if (urlsStr) {
      let urls = [];
      try {
        urls = JSON.parse(urlsStr);
      } catch (e) {
        urls = [urlsStr];
      }

      const originalUrl = urls[0];
      if (originalUrl) {
        const fullUrl = originalUrl.startsWith('http') ? originalUrl : BASE_API + originalUrl;
        const response = await fetch(fullUrl, { cache: "no-cache" });
        const blob = await response.blob();
        const file = new File([blob], `history_${Date.now()}.jpg`, { type: blob.type });

        loadFileToCanvas(file, coordsInfo);
      }
    } else {
      if (currentIndex.value === 0) {
        if (canvas.value) canvas.value.clear();
        uploadFile.value = null;
      }
    }
  } catch (err) {
    console.error("加载工步历史图片失败", err);
    if (currentIndex.value === 0) uploadFile.value = null;
  } finally {
    canvasLoading.value = false;
  }
}

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

// 键盘 Delete 键删除联动（同步删除右侧列表）
const handleKeydown = (e) => {
  if (e.key === 'Delete' || e.key === 'Backspace') {
    if (!canvas.value) return;
    const activeObjects = canvas.value.getActiveObjects();
    if (activeObjects.length) {
      canvas.value.discardActiveObject();
      activeObjects.forEach((obj) => {
        // 同步从右侧列表中删除
        if (obj.id) {
          annotationList.value = annotationList.value.filter(item => item.id !== obj.id);
        }
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

const loadFileToCanvas = (file, historyCoords = []) => {
  uploadFile.value = file;
  proxy.$modal.loading('正在加载原图...');

  const reader = new FileReader();
  reader.onload = (e) => {
    const imgObj = new Image();
    imgObj.src = e.target.result;
    imgObj.onload = () => {
      canvas.value.clear();
      annotationList.value = []; // 清空右侧列表准备接收新数据
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

      if (historyCoords && historyCoords.length > 0) {
        // 计算图片在画布上的实际渲染起点（左上角）
        const bgLogicalLeft = (canvasWidth / 2) - (fImg.width * scale) / 2;
        const bgLogicalTop = (canvasHeight / 2) - (fImg.height * scale) / 2;

        historyCoords.forEach(group => {
          if (group.posList && group.posList.length > 0) {
            group.posList.forEach(pos => {
              // 重新生成该框的专属 ID
              const uniqueId = `rect_load_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`;

              // 物理坐标 -> 画布坐标 逆推算
              const rectLeft = pos.x * scale + bgLogicalLeft;
              const rectTop = pos.y * scale + bgLogicalTop;
              const rectWidth = pos.width * scale;
              const rectHeight = pos.height * scale;

              const rect = new fabric.Rect({
                left: rectLeft,
                top: rectTop,
                width: rectWidth,
                height: rectHeight,
                fill: 'rgba(255, 0, 0, 0)',
                stroke: 'red',
                strokeWidth: 2,
                selectable: true,
                evented: true,
                id: uniqueId,
                customData: {label: group.label, remark: pos.remark || ''}
              });

              canvas.value.add(rect);

              // 同步推送至右侧信息列表
              annotationList.value.push({
                id: uniqueId,
                label: group.label,
                remark: pos.remark || ''
              });
            });
          }
        });
        // 通知画布渲染新加进来的框
        canvas.value.requestRenderAll();
      }
      proxy.$modal.closeLoading();
    };
    imgObj.onerror = () => proxy.$modal.closeLoading();
  };
  reader.onerror = () => proxy.$modal.closeLoading();
  reader.readAsDataURL(file);
};

const handleFileChange = (file) => {
  if (file && file.raw) {
    loadFileToCanvas(file.raw);
  }
};

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
  proxy.$modal.loading('正在处理抓拍画面...');

  try {
    const video = videoRef.value;
    const cvs = document.createElement('canvas');
    cvs.width = video.videoWidth; cvs.height = video.videoHeight;
    cvs.getContext('2d').drawImage(video, 0, 0, cvs.width, cvs.height);

    cvs.toBlob((blob) => {
      if (!blob) return;
      const rawFile = new File([blob], `capture_${Date.now()}.jpg`, { type: 'image/jpeg' });
      loadFileToCanvas(rawFile);

      proxy.$modal.msgSuccess('抓拍成功！');
      closeCameraDialog();
      isCapturing.value = false;
      proxy.$modal.closeLoading();
    }, 'image/jpeg', 1);
  } catch (error) {
    proxy.$modal.msgError('抓拍异常');
    isCapturing.value = false;
    proxy.$modal.closeLoading();
  }
};

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
  activeRect = new fabric.Rect({
    left: startX,
    top: startY,
    width: 0,
    height: 0,
    fill: 'rgba(255, 0, 0, 0)',
    stroke: 'red',
    strokeWidth: 2,
    selectable: false,
    evented: false
  });
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

// 【核心改造】：不再添加文字和组合体，直接给矩形框赋予信息和交互能力
const confirmLabel = () => {
  if (!labelText.value) return proxy.$modal.msgWarning('请输入标签');

  if (!remark) remark.value = '';

  const uniqueId = `rect_${Date.now()}`;

  // 将 activeRect 变更为可交互、可选中状态，并绑定专属 ID 及数据
  activeRect.set({
    id: uniqueId,
    selectable: true,
    evented: true,
    customData: { label: labelText.value, remark: remark.value }
  });

  // 同步推送至右侧信息列表
  annotationList.value.push({
    id: uniqueId,
    label: labelText.value,
    remark: remark.value
  });

  stepCount.value += 1;
  canvas.value.setActiveObject(activeRect);
  canvas.value.renderAll();

  dialogVisible.value = false; labelText.value = ''; remark.value = ''; activeRect = null;
};

// 从右侧列表主动删除某项及画布上的框
const removeAnnotation = (index, id) => {
  annotationList.value.splice(index, 1);
  const objects = canvas.value.getObjects();
  const objToRemove = objects.find(obj => obj.id === id);
  if (objToRemove) {
    canvas.value.remove(objToRemove);
    canvas.value.requestRenderAll();
  }
};

// 右侧列表悬浮时高亮对应的框（可选的用户体验提升）
const highlightAnnotation = (id, isHover) => {
  const obj = canvas.value.getObjects().find(o => o.id === id);
  if (obj) {
    obj.set('strokeWidth', isHover ? 4 : 2);
    obj.set('stroke', isHover ? '#409EFF' : 'red');
    canvas.value.requestRenderAll();
  }
};

const cancelAnnotation = () => {
  if (activeRect) { canvas.value.remove(activeRect); canvas.value.renderAll(); }
  dialogVisible.value = false; labelText.value = ''; remark.value = ''; activeRect = null;
};

const clearCanvasAnnotations = () => {
  if (!canvas.value) return;
  const objects = canvas.value.getObjects();
  objects.forEach(obj => canvas.value.remove(obj));
  annotationList.value = []; // 同步清空右侧列表
  stepCount.value = 1;
  canvas.value.requestRenderAll();
};

const uploadToMinio = async () => {
  if (!canvas.value || canvas.value.getObjects().length === 0) return proxy.$modal.msgWarning('请先完成标注！')
  if (!uploadFile.value) return proxy.$modal.msgError('未找到原始图片文件');

  proxy.$modal.loading('正在上传图片和标注数据...')
  try {
    const resOrig = await getUploadUrl();
    const resAnnot = await getUploadUrl();

    await axios.put(resOrig.data.url, uploadFile.value, {
      headers: {'Content-Type': uploadFile.value.type || 'image/jpeg'}
    });

    const annotationFile = getFile();
    await axios.put(resAnnot.data.url, annotationFile, {
      headers: {'Content-Type': 'image/jpeg'}
    });

    const urlList = [resOrig.data.objectName, resAnnot.data.objectName];

    const data = {
      id: currentStepId.value,
      guideMapUrl: JSON.stringify(urlList),
      coordsInfo: JSON.stringify(getDataEasy()),
    }

    await updateStep(data)

    emit('change-status')
    proxy.$modal.msgSuccess('保存成功！');

    if (props.stepIds.length > 1) {
      proxy.$modal.confirm('已保存当前结果。是否清空标注并开始下一个工步？', '连续标注提示', {
        confirmButtonText: '下一工步',
        cancelButtonText: '留在当前'
      }).then(() => {
        clearCanvasAnnotations();
        if (currentIndex.value < props.stepIds.length - 1) {
          currentIndex.value++;
          loadCurrentStepData();
        } else {
          proxy.$modal.msgSuccess('当前已经是最后一个工步');
        }
      }).catch(() => {});
    }
  } catch (error) {
    proxy.$modal.msgError('绑定失败，请检查网络');
  } finally {
    proxy.$modal.closeLoading()
  }
};

/**
 * 【核心改造】：因为已经没有 Group 包裹，需要直接针对 Rect 进行绝对坐标换算
 */
const getDataEasy = () => {
  const bgImg = canvas.value.backgroundImage;
  if (!bgImg) return [];

  const scaleX = bgImg.scaleX;
  const scaleY = bgImg.scaleY;

  // 计算图片左上角在画板上的真实起点坐标
  const bgLogicalLeft = bgImg.left - (bgImg.width * scaleX) / 2;
  const bgLogicalTop = bgImg.top - (bgImg.height * scaleY) / 2;

  const labelSet = new Set();

  const results = canvas.value.getObjects()
      .filter(obj => obj.type === 'rect' && obj.customData)
      .map(rectObj => {
        labelSet.add(rectObj.customData.label);

        // 计算框在画板上的绝对宽高（包含用户缩放框的情况）
        const canvasRectWidth = rectObj.width * rectObj.scaleX;
        const canvasRectHeight = rectObj.height * rectObj.scaleY;

        // 【修复核心】：直接减去背景起点，除以缩放比，获得基于上传原图的真实像素坐标
        let realX = Math.round((rectObj.left - bgLogicalLeft) / scaleX);
        let realY = Math.round((rectObj.top - bgLogicalTop) / scaleY);
        let realW = Math.round(canvasRectWidth / scaleX);
        let realH = Math.round(canvasRectHeight / scaleY);

        // 防抖与越界保护
        realX = Math.max(0, realX);
        realY = Math.max(0, realY);
        realW = Math.min(realW, bgImg.width - realX);
        realH = Math.min(realH, bgImg.height - realY);

        return {
          label: rectObj.customData.label,
          pos: { x: realX, y: realY, width: realW, height: realH, remark: rectObj.customData.remark }
        };
      });

  const coordsInfo = [];
  labelSet.forEach(label => {
    let tempCoordsList = [];
    results.forEach(result => {
      if (result.label === label) tempCoordsList.push(result.pos);
    });
    coordsInfo.push({ label: label, posList: tempCoordsList });
  });

  return coordsInfo;
};

// const getFile = () => {
//   const bgImage = canvas.value.backgroundImage;
//   const currentScale = bgImage ? bgImage.scaleX : 1;
//   const multiplier = 1 / currentScale;
//   const dataURL = canvas.value.toDataURL({ format: 'jpeg', quality: 1, multiplier: multiplier });
//   const blob = dataURLtoBlob(dataURL);
//   return new File([blob], `annotated_${Date.now()}.jpg`, {type: 'image/jpeg'});
// }

const getFile = () => {
  const bgImage = canvas.value.backgroundImage;
  if (!bgImage) {
    return null;
  }

  // 1. 保存当前的视口变换矩阵（包含拖拽、缩放状态），并暂时重置视口
  const originalVpt = [...canvas.value.viewportTransform];
  canvas.value.setViewportTransform([1, 0, 0, 1, 0, 0]);

  // 2. 获取当前的背景图缩放比例
  const currentScale = bgImage.scaleX;

  // 3. 计算背景图在画板上的实际渲染起始坐标和宽高
  // 因为 loadFileToCanvas 中设置了 originX/Y 为 'center'，所以必须基于中心点往回减去一半
  const bgLogicalLeft = bgImage.left - (bgImage.width * currentScale) / 2;
  const bgLogicalTop = bgImage.top - (bgImage.height * currentScale) / 2;
  const bgLogicalWidth = bgImage.width * currentScale;
  const bgLogicalHeight = bgImage.height * currentScale;

  // 4. 精准导出图片
  const dataURL = canvas.value.toDataURL({
    format: 'jpeg',
    quality: 1, // 设为 1 以保证标注后的图片质量最大化（无损压缩）
    left: bgLogicalLeft,
    top: bgLogicalTop,
    width: bgLogicalWidth,
    height: bgLogicalHeight,
    multiplier: 1 / currentScale // 利用倒数直接放大回原图的物理尺寸
  });

  // 5. 恢复用户的拖拽和缩放视口状态（用户体验无缝衔接）
  canvas.value.setViewportTransform(originalVpt);

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
</script>

<style scoped>
.annotator-container { display: flex; flex-direction: column; gap: 20px; align-items: center; padding: 10px; }
.step-navigator { display: flex; align-items: center; justify-content: center; width: 100%; max-width: 1000px; margin-bottom: -10px; }
.toolbar { width: 100%; max-width: 1150px; }

/* 左右布局核心样式 */
.main-workspace {
  display: flex;
  gap: 20px;
  width: 100%;
  max-width: 1150px;
  align-items: stretch;
}

.canvas-wrapper {
  border: 1px solid #ccc;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 800px;
  height: 600px;
  flex-shrink: 0;
}

.annotation-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.annotation-card {
  height: 600px;
  border-radius: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.annotation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 10px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.annotation-item:hover {
  background-color: #f5f7fa;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  overflow: hidden;
}

.item-label { font-weight: bold; }
.item-remark { font-size: 13px; color: #606266; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.status-text { margin-top: 10px; font-size: 14px; color: #666; }
.camera-preview-box { width: 100%; height: 500px; background-color: #000; display: flex; justify-content: center; align-items: center; border-radius: 4px; overflow: hidden; margin-bottom: 20px; }
.live-stream { width: 100%; height: 100%; object-fit: contain; }
.camera-loading { color: #fff; text-align: center; }
.camera-controls { display: flex; justify-content: flex-end; align-items: center; padding: 0 10px; }
</style>