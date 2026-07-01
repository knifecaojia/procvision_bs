<template>
  <el-dialog title="图片标注/包装信息录入" v-model="labelVisible" width="1200px" @close="handleLabelDialogClose" :close-on-click-modal="false">
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

          <template v-if="!props.packageFlag">
            <el-button
                type="warning"
                @click="toggleDrawMode"
                :plain="!isDrawingMode"
                icon="Edit"
            >
              {{ isDrawingMode ? '结束标注' : '开始标注' }}
            </el-button>
            <el-button type="danger" @click="clearCanvasAnnotations" icon="Delete">清空标注</el-button>
          </template>

          <el-button
              v-if="props.packageFlag"
              type="success"
              icon="Box"
              @click="packageDialogVisible = true"
          >
            添加包装信息
          </el-button>

          <el-button type="primary" @click="uploadToMinio" icon="Check">
            {{ props.packageFlag ? '保存包装(及图片)' : '保存标注结果' }}
          </el-button>
        </el-space>

        <div class="status-panel" v-if="isDrawingMode && !props.packageFlag">
          <el-alert
              type="warning"
              show-icon
              :closable="false"
              class="drawing-alert"
          >
            <template #title>
              <span class="alert-title">当前状态：<span class="highlight-text">绘制中...</span></span>
            </template>
            <template #default>
              <div class="alert-tips">
                <strong>Tips：</strong>
                开启绘制后按住 <el-tag size="small" type="info" effect="plain">ALT + 鼠标左键</el-tag> 可以拖拽图片，
                选中框后按 <el-tag size="small" type="danger" effect="plain">Delete</el-tag> 键可删除
              </div>
            </template>
          </el-alert>

          <div class="step-content-box" v-if="currentStepContent">
            <div class="step-header">
              <el-icon color="#409EFF"><Document /></el-icon>
              <span>工步内容</span>
            </div>
            <div class="step-text">{{ currentStepContent }}</div>
          </div>
        </div>
      </el-card>

      <div class="main-workspace">
        <div class="canvas-wrapper" v-loading="canvasLoading" element-loading-text="正在加载原图...">
          <canvas id="c"></canvas>
        </div>

        <div class="annotation-list-wrapper">
          <el-card shadow="never" class="annotation-card" v-if="props.packageFlag">
            <template #header>
              <div class="card-header">
                <span>包装信息面板</span>
                <el-tag type="success" size="small">共 {{ packageList.length }} 条</el-tag>
              </div>
            </template>
            <el-scrollbar height="500px">
              <el-empty v-if="packageList.length === 0" description="暂无包装信息，请点击上方按钮添加" :image-size="80" />
              <div v-for="(item, index) in packageList" :key="item._id" class="annotation-item">
                <div class="item-info">
                  <div class="item-label">
                    <el-tag type="success" size="small">产品信息</el-tag>
                    <span style="margin-left: 8px; font-weight: bold;">{{ item.productInfo }}</span>
                  </div>
                  <div class="item-remark" style="margin-top: 8px; font-size: 14px;">
                    待检测数量：<strong style="color: #E6A23C;">{{ item.quantity }}</strong>
                  </div>
                </div>
                <el-button type="danger" icon="Delete" circle plain size="small" @click="removePackageInfo(index)"></el-button>
              </div>
            </el-scrollbar>
          </el-card>

          <el-card shadow="never" class="annotation-card" v-else>
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
        <el-select v-model="labelText" filterable placeholder="请选择标签" style="width: 100%; margin-bottom: 20px;">
          <el-option v-if="props.tempAlgType === 0" v-for="item in label_tianxian" :key="item.value" :label="item.label" :value="item.value"></el-option>
          <el-option v-else-if="props.tempAlgType === 1" v-for="item in label_banji" :key="item.label" :label="item.label" :value="item.value"></el-option>
          <el-option v-else v-for="(item, index) in label_mozu" :key="item.label + index" :label="item.label" :value="item.value"></el-option>
        </el-select>

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

      <el-dialog
          v-model="packageDialogVisible"
          title="填写包装信息"
          width="400px"
          append-to-body
          :close-on-click-modal="false"
      >
        <el-form label-width="110px">
          <el-form-item label="产品信息" required>
            <el-input v-model="packageData.productInfo" placeholder="请输入产品信息" />
          </el-form-item>
          <el-form-item label="待检测数量" required>
            <el-input-number v-model="packageData.quantity" :min="1" placeholder="待检测数量" style="width: 100%;" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="packageDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmPackageInfo">确定</el-button>
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
          立即抓拍并去配置
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
import {Loading, VideoCamera} from "@element-plus/icons-vue";
const {proxy} = getCurrentInstance()

const {label_tianxian, label_banji, label_mozu} = proxy.useDict("label_tianxian", 'label_banji', 'label_mozu')
const labelVisible = defineModel()
const canvas = ref(null);
const isDrawingMode = ref(false);
const dialogVisible = ref(false);
const labelText = ref('');
const inputRef = ref(null);
const stepCount = ref(0);
const currentStepContent = ref('');

// 标注模式状态管理
const annotationList = ref([]);

// 包装模式状态管理（现改为数组支持多条录入）
const packageDialogVisible = ref(false);
const packageData = ref({ productInfo: '', quantity: 1 });
const packageList = ref([]);

// 保存最原始、未被任何压缩的物理图片文件
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
  },
  tempAlgType: {
    type: Number
  },
  packageFlag: {
    type: Boolean,
    default: false
  }
});

const options = computed(() => [
  {
    value: 'tianxian',
    label: '天线',
    children: label_tianxian.value
  },
  {
    value: 'banji',
    label: '板级',
    children: label_banji.value
  },
  {
    value: 'mozu',
    label: '模组',
    children: label_mozu.value
  }
]);

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
    packageList.value = [];
    packageData.value = { productInfo: '', quantity: 1 };
  }
})

// 加载当前进度对应工步的历史原图和历史数据
const loadCurrentStepData = async () => {
  if (!currentStepId.value) return;

  canvasLoading.value = true;
  stepCount.value = 1;

  clearCanvasAnnotations();
  packageList.value = [];

  try {
    const res = await getStep(currentStepId.value);
    let urlsStr = res.data.guideMapUrl;
    currentStepContent.value = res.data.content;

    if (!urlsStr && res.data.code === '99' && props.borrowImageUrl) {
      urlsStr = JSON.stringify([props.borrowImageUrl]);
    }

    let coordsInfo = null;
    if (res.data.coordsInfo) {
      try {
        coordsInfo = JSON.parse(res.data.coordsInfo);
      } catch (e) {
      }
    }

    // 【核心新增】：包装模式下的多条数据回显
    if (props.packageFlag && coordsInfo) {
      if (Array.isArray(coordsInfo)) {
        if (coordsInfo.length === 0 || !coordsInfo[0].posList) {
          // 从后端拿到纯净数据后，动态映射加上 _id 供前端 v-for 使用
          packageList.value = coordsInfo.map(item => ({
            ...item,
            _id: `pkg_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`
          }));
        }
      } else {
        // 兼容单对象
        packageList.value = [{
          _id: `pkg_${Date.now()}`,
          productInfo: coordsInfo.productInfo,
          quantity: coordsInfo.quantity
        }];
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

        if (!response.ok) {
          throw new Error(`图片加载失败，状态码: ${response.status}`);
        }

        const blob = await response.blob();
        const file = new File([blob], `history_${Date.now()}.jpg`, { type: blob.type });

        loadFileToCanvas(file, props.packageFlag ? null : coordsInfo);
      }
    } else {
      if (currentIndex.value === 0) {
        if (canvas.value) canvas.value.clear();
        uploadFile.value = null;
      }
    }
  } catch (err) {
    proxy.$modal.msgWarning('历史原图已失效或加载失败，请通过“本地上传”或“打开相机”提供新图片！');
    if (canvas.value) canvas.value.clear();
    uploadFile.value = null;
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

const handleKeydown = (e) => {
  if (props.packageFlag) return; // 包装模式禁用键盘删除框
  if (e.key === 'Delete' || e.key === 'Backspace') {
    if (!canvas.value) return;
    const activeObjects = canvas.value.getActiveObjects();
    if (activeObjects.length) {
      canvas.value.discardActiveObject();
      activeObjects.forEach((obj) => {
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
      annotationList.value = [];
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

      // 普通模式下的标注框回显
      if (historyCoords && Array.isArray(historyCoords) && historyCoords.length > 0) {
        const bgLogicalLeft = (canvasWidth / 2) - (fImg.width * scale) / 2;
        const bgLogicalTop = (canvasHeight / 2) - (fImg.height * scale) / 2;

        historyCoords.forEach(group => {
          if (group.posList && group.posList.length > 0) {
            group.posList.forEach(pos => {
              const uniqueId = `rect_load_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`;
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
                strokeWidth: 1,
                selectable: true,
                evented: true,
                id: uniqueId,
                customData: {label: group.label, remark: pos.remark || ''}
              });

              canvas.value.add(rect);

              annotationList.value.push({
                id: uniqueId,
                label: group.label,
                remark: pos.remark || ''
              });
            });
          }
        });
        canvas.value.requestRenderAll();
      }
      proxy.$modal.closeLoading();
    };
    imgObj.onerror = () => {
      proxy.$modal.closeLoading();
      proxy.$modal.msgWarning('图片解析失败，可能文件已损坏，请重新上传！');
    };
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
  await startLocalCamera();
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
  if (props.packageFlag) return proxy.$modal.msgWarning('包装模式下无需框选标注！');
  if (!canvas.value.backgroundImage) return proxy.$modal.msgWarning('请先上传图片或使用相机拍照！');
  isDrawingMode.value = !isDrawingMode.value;
  canvas.value.skipTargetFind = isDrawingMode.value;
  canvas.value.selection = !isDrawingMode.value;
};

const onMouseDown = (opt) => {
  if (!isDrawingMode.value || props.packageFlag) return;
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
    strokeWidth: 1,
    selectable: false,
    evented: false
  });
  canvas.value.add(activeRect);
};

const onMouseMove = (opt) => {
  if (!isDrawingMode.value || !isMouseDown || props.packageFlag) return;
  const pointer = canvas.value.getPointer(opt.e);
  if (pointer.x < startX) activeRect.set({left: pointer.x});
  if (pointer.y < startY) activeRect.set({top: pointer.y});
  activeRect.set({width: Math.abs(pointer.x - startX), height: Math.abs(pointer.y - startY)});
  canvas.value.renderAll();
};

const onMouseUp = () => {
  if (!isDrawingMode.value || !isMouseDown || props.packageFlag) return;
  isMouseDown = false;
  if (activeRect.width < 5 || activeRect.height < 5) { canvas.value.remove(activeRect); activeRect = null; return; }
  dialogVisible.value = true;
  nextTick(() => { inputRef.value?.focus(); });
};

// 【包装修改】：去除上传引导图的强制校验
const confirmPackageInfo = () => {
  if (!packageData.value.productInfo) return proxy.$modal.msgWarning('请输入产品信息');

  // 生成唯一ID并推入列表
  packageList.value.push({
    _id: `pkg_${Date.now()}_${Math.random().toString(36).substring(2, 8)}`,
    productInfo: packageData.value.productInfo,
    quantity: packageData.value.quantity
  });

  // 添加完毕后自动清空表单，方便继续添加，然后关闭弹窗
  packageData.value = { productInfo: '', quantity: 1 };
  packageDialogVisible.value = false;
  proxy.$modal.msgSuccess('添加成功，可继续添加或点击“保存包装及图片”');
};

// 从右侧面板中删除某条包装信息
const removePackageInfo = (index) => {
  packageList.value.splice(index, 1);
};

const confirmLabel = () => {
  if (!labelText.value) return proxy.$modal.msgWarning('请输入标签');

  if (!remark) remark.value = '';

  const uniqueId = `rect_${Date.now()}`;

  activeRect.set({
    id: uniqueId,
    selectable: true,
    evented: true,
    customData: { label: labelText.value, remark: remark.value }
  });

  annotationList.value.push({
    id: uniqueId,
    label: labelText.value,
    remark: remark.value
  });

  stepCount.value += 1;
  canvas.value.setActiveObject(activeRect);
  canvas.value.renderAll();

  dialogVisible.value = false;
  remark.value = '';
  activeRect = null;
};

const removeAnnotation = (index, id) => {
  annotationList.value.splice(index, 1);
  const objects = canvas.value.getObjects();
  const objToRemove = objects.find(obj => obj.id === id);
  if (objToRemove) {
    canvas.value.remove(objToRemove);
    canvas.value.requestRenderAll();
  }
};

const highlightAnnotation = (id, isHover) => {
  const obj = canvas.value.getObjects().find(o => o.id === id);
  if (obj) {
    obj.set('strokeWidth', isHover ? 2 : 1);
    obj.set('stroke', isHover ? '#409EFF' : 'red');
    canvas.value.requestRenderAll();
  }
};

const cancelAnnotation = () => {
  if (activeRect) { canvas.value.remove(activeRect); canvas.value.renderAll(); }
  dialogVisible.value = false;
  remark.value = '';
  activeRect = null;
};

const clearCanvasAnnotations = () => {
  if (!canvas.value) return;
  const objects = canvas.value.getObjects();
  objects.forEach(obj => canvas.value.remove(obj));
  annotationList.value = [];
  stepCount.value = 1;
  canvas.value.requestRenderAll();
};

const uploadToMinio = async () => {
  // 根据不同模式走不同的检验规则
  if (props.packageFlag) {
    if (packageList.value.length === 0) return proxy.$modal.msgWarning('请先录入包装信息！');
    // 【包装修改】：去除必须有图片的拦截
  } else {
    if (!canvas.value || canvas.value.getObjects().length === 0) return proxy.$modal.msgWarning('请先完成标注！');
    if (!uploadFile.value) return proxy.$modal.msgError('未找到原始图片文件');
  }

  proxy.$modal.loading('正在保存数据...')
  try {
    let urlList = [];

    // 【包装修改】：只有用户传了图片，才去调用Minio接口
    if (uploadFile.value) {
      const resOrig = await getUploadUrl();
      const resAnnot = await getUploadUrl();

      await axios.put(resOrig.data.url, uploadFile.value, {
        headers: {'Content-Type': uploadFile.value.type || 'image/jpeg'}
      });

      const annotationFile = getFile();
      if (annotationFile) {
        await axios.put(resAnnot.data.url, annotationFile, {
          headers: {'Content-Type': 'image/jpeg'}
        });
        urlList = [resOrig.data.objectName, resAnnot.data.objectName];
      } else {
        urlList = [resOrig.data.objectName, resOrig.data.objectName];
      }
    }

    // 判断不同模式的数据结构 (包装模式将存为数组 [{productInfo, quantity}, ...])
    const coordsInfoData = props.packageFlag
        ? packageList.value.map(({ _id, ...rest }) => rest)
        : getDataEasy();

    const data = {
      id: currentStepId.value,
      // 如果没有传图片，urlList 为空数组，此时直接存 null，保证后端数据干净
      guideMapUrl: urlList.length > 0 ? JSON.stringify(urlList) : null,
      coordsInfo: JSON.stringify(coordsInfoData),
    }

    await updateStep(data)

    emit('change-status')
    proxy.$modal.msgSuccess('保存成功！');

    if (props.stepIds.length > 1) {
      proxy.$modal.confirm('已保存当前结果。是否清空并开始下一个工步？', '连续处理提示', {
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
    proxy.$modal.msgError('保存失败，请检查网络');
  } finally {
    proxy.$modal.closeLoading()
  }
};

const getDataEasy = () => {
  const bgImg = canvas.value.backgroundImage;
  if (!bgImg) return [];

  const scaleX = bgImg.scaleX;
  const scaleY = bgImg.scaleY;

  const bgLogicalLeft = bgImg.left - (bgImg.width * scaleX) / 2;
  const bgLogicalTop = bgImg.top - (bgImg.height * scaleY) / 2;

  const labelSet = new Set();

  const results = canvas.value.getObjects()
      .filter(obj => obj.type === 'rect' && obj.customData)
      .map(rectObj => {
        labelSet.add(rectObj.customData.label);

        const canvasRectWidth = rectObj.width * rectObj.scaleX;
        const canvasRectHeight = rectObj.height * rectObj.scaleY;

        let realX = Math.round((rectObj.left - bgLogicalLeft) / scaleX);
        let realY = Math.round((rectObj.top - bgLogicalTop) / scaleY);
        let realW = Math.round(canvasRectWidth / scaleX);
        let realH = Math.round(canvasRectHeight / scaleY);

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

const getFile = () => {
  const bgImage = canvas.value.backgroundImage;
  if (!bgImage) {
    return null;
  }

  const originalVpt = [...canvas.value.viewportTransform];
  canvas.value.setViewportTransform([1, 0, 0, 1, 0, 0]);

  const currentScale = bgImage.scaleX;

  const bgLogicalLeft = bgImage.left - (bgImage.width * currentScale) / 2;
  const bgLogicalTop = bgImage.top - (bgImage.height * currentScale) / 2;
  const bgLogicalWidth = bgImage.width * currentScale;
  const bgLogicalHeight = bgImage.height * currentScale;

  const dataURL = canvas.value.toDataURL({
    format: 'jpeg',
    quality: 1,
    left: bgLogicalLeft,
    top: bgLogicalTop,
    width: bgLogicalWidth,
    height: bgLogicalHeight,
    multiplier: 1 / currentScale
  });

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

function handleLabelDialogClose(){
  labelText.value = ''
}
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

.camera-preview-box { width: 100%; height: 500px; background-color: #000; display: flex; justify-content: center; align-items: center; border-radius: 4px; overflow: hidden; margin-bottom: 20px; }
.live-stream { width: 100%; height: 100%; object-fit: contain; }
.camera-loading { color: #fff; text-align: center; }
.camera-controls { display: flex; justify-content: flex-end; align-items: center; padding: 0 10px; }
.status-panel {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.drawing-alert {
  border: 1px solid #faecd8;
  border-radius: 6px;
}

.alert-title {
  font-size: 15px;
  font-weight: bold;
  color: #e6a23c;
}

.highlight-text {
  color: #f56c6c;
  margin-left: 5px;
}

.alert-tips {
  margin-top: 6px;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.step-content-box {
  background-color: #f4f4f5;
  border-left: 4px solid #409eff;
  padding: 12px 16px;
  border-radius: 4px;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.step-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>