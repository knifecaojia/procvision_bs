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

          <el-button type="primary" plain icon="pointer" @click="openAnnotationTool">打开标注工具</el-button>

          <el-upload
              v-if="!props.packageFlag"
              :auto-upload="false"
              :show-file-list="false"
              accept=".xml,text/xml,application/xml"
              :on-change="handleVocFileChange"
          >
            <el-button type="primary" plain icon="DocumentAdd">导入标注</el-button>
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
            <el-button type="danger" @click="clearCurrentAnnotations" icon="Delete">清空标注</el-button>
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

          <el-card shadow="never" class="annotation-card annotation-info-card" v-else>
            <template #header>
              <div class="annotation-panel-heading">
                <div class="panel-heading-main">
                  <div class="panel-heading-mark">标</div>
                  <div class="panel-heading-copy">
                    <div class="panel-heading-title">标注信息</div>
                    <div class="panel-heading-subtitle">悬停可定位，点击编辑可修改标签信息</div>
                  </div>
                </div>
                <div class="annotation-count-badge">
                  <strong>{{ annotationList.length }}</strong>
                  <span>个标注</span>
                </div>
              </div>
            </template>

            <div v-if="annotationList.length > 0" class="annotation-batch-toolbar">
              <div class="batch-select-area">
                <el-checkbox
                    :model-value="isAllAnnotationsSelected"
                    :indeterminate="isAnnotationSelectionIndeterminate"
                    @change="toggleSelectAllAnnotations"
                >
                  全选
                </el-checkbox>
                <span class="selection-summary">
                  已选 <strong>{{ selectedAnnotationIds.length }}</strong> 项
                </span>
              </div>
              <el-button
                  type="danger"
                  plain
                  size="small"
                  icon="Delete"
                  :disabled="selectedAnnotationIds.length === 0"
                  @click="removeSelectedAnnotations"
              >
                删除所选
              </el-button>
            </div>

            <el-scrollbar class="annotation-scrollbar">
              <el-empty
                  v-if="annotationList.length === 0"
                  class="annotation-empty"
                  description="暂无标注信息"
                  :image-size="88"
              >
                <template #description>
                  <div class="empty-description">
                    <strong>暂无标注信息</strong>
                    <span>导入 VOC 文件或在图片上绘制标注框</span>
                  </div>
                </template>
              </el-empty>

              <div v-else class="annotation-list-content">
                <div
                    v-for="(item, index) in annotationList"
                    :key="item.id"
                    class="annotation-item annotation-info-item"
                    :class="{
                      'is-highlighted': hoveredAnnotationId === item.id,
                      'is-selected': selectedAnnotationIds.includes(item.id)
                    }"
                    @mouseenter="highlightAnnotation(item.id, true)"
                    @mouseleave="highlightAnnotation(item.id, false)"
                >
                  <el-checkbox
                      class="annotation-checkbox"
                      :model-value="selectedAnnotationIds.includes(item.id)"
                      @change="(checked) => toggleAnnotationSelection(item.id, checked)"
                      @click.stop
                  />

                  <div class="annotation-sequence">{{ String(index + 1).padStart(2, '0') }}</div>

                  <div class="item-info annotation-item-info">
                    <div class="annotation-item-topline">
                      <el-tag class="annotation-label-tag" size="small" effect="light" round>
                        {{ item.label }}
                      </el-tag>
                      <span v-if="hoveredAnnotationId === item.id" class="locating-text">定位中</span>
                    </div>
                    <div class="item-remark annotation-remark" :class="{ 'is-empty': !item.remark }">
                      {{ item.remark || '暂无备注' }}
                    </div>
                  </div>

                  <div class="annotation-action-group">
                    <el-tooltip content="修改标签和备注" placement="top">
                      <el-button
                          class="annotation-edit-button"
                          type="primary"
                          icon="EditPen"
                          circle
                          text
                          size="small"
                          aria-label="编辑该标注"
                          @click.stop="openEditAnnotation(item)"
                      />
                    </el-tooltip>
                    <el-tooltip content="删除该标注" placement="top">
                      <el-button
                          class="annotation-delete-button"
                          type="danger"
                          icon="Delete"
                          circle
                          text
                          size="small"
                          aria-label="删除该标注"
                          @click.stop="removeAnnotation(index, item.id)"
                      />
                    </el-tooltip>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </el-card>
        </div>
      </div>

      <el-dialog
          v-model="dialogVisible"
          :title="annotationDialogMode === 'edit' ? '修改标注信息' : '添加标注信息'"
          width="300px"
          append-to-body
          :close-on-click-modal="false"
          @close="cancelAnnotation"
      >
        <el-select
            v-model="labelText"
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入标签"
            style="width: 100%; margin-bottom: 20px;"
        >
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
          <el-button type="primary" @click="confirmLabel">
            {{ annotationDialogMode === 'edit' ? '保存修改' : '确定' }}
          </el-button>
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
const annotationDialogMode = ref('create');
const editingAnnotationId = ref(null);
const labelText = ref('');
const inputRef = ref(null);
const stepCount = ref(0);
const currentStepContent = ref('');

// 标注模式状态管理
const annotationList = ref([]);
const selectedAnnotationIds = ref([]);
const hoveredAnnotationId = ref(null);

// 批量绑定工步时仅保存最近一次导入 VOC 时的原始模板快照。
// 当前工步对标注的新增、删除、移动或缩放只作用于当前工步，不写回该模板。
// 进入其他工步时，始终按这份导入快照恢复标注框和标签列表。
const batchVocTemplate = ref(null);
const isUsingBatchVocTemplate = ref(false);

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

const isAllAnnotationsSelected = computed(() =>
    annotationList.value.length > 0 && selectedAnnotationIds.value.length === annotationList.value.length
);
const isAnnotationSelectionIndeterminate = computed(() =>
    selectedAnnotationIds.value.length > 0 && selectedAnnotationIds.value.length < annotationList.value.length
);

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
    selectedAnnotationIds.value = [];
    hoveredAnnotationId.value = null;
    batchVocTemplate.value = null;
    isUsingBatchVocTemplate.value = false;
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
        isUsingBatchVocTemplate.value = false;
      } else if (!props.packageFlag && canvas.value?.backgroundImage && batchVocTemplate.value) {
        // 后续工步没有独立引导图时，沿用当前图片并恢复导入时的原始标签模板。
        renderVocAnnotations(
            batchVocTemplate.value.objects,
            batchVocTemplate.value.size,
            { clearExisting: false, showMessage: false, markAsBatchTemplate: true }
        );
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
  canvas.value.on('mouse:over', handleCanvasObjectMouseOver);
  canvas.value.on('mouse:out', handleCanvasObjectMouseOut);
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
          selectedAnnotationIds.value = selectedAnnotationIds.value.filter(id => id !== obj.id);
          if (hoveredAnnotationId.value === obj.id) hoveredAnnotationId.value = null;
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
      selectedAnnotationIds.value = [];
      hoveredAnnotationId.value = null;
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

      // 批量绑定已导入 VOC 后，始终优先恢复导入时的原始模板。
      // 不读取目标工步的历史标签，也不沿用上一工步对模板所做的增删改。
      if (!props.packageFlag && props.stepIds.length > 1 && batchVocTemplate.value) {
        renderVocAnnotations(
            batchVocTemplate.value.objects,
            batchVocTemplate.value.size,
            { clearExisting: false, showMessage: false, markAsBatchTemplate: true }
        );
      } else if (historyCoords && Array.isArray(historyCoords) && historyCoords.length > 0) {
        // 非批量模板场景下，正常回显当前工步的历史标注。
        isUsingBatchVocTemplate.value = false;
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
      } else {
        isUsingBatchVocTemplate.value = false;
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

// 解析 Pascal VOC XML，并将其中的目标框绘制到当前图片上
const handleVocFileChange = async (file) => {
  if (!file?.raw) return;
  if (!canvas.value?.backgroundImage) {
    proxy.$modal.msgWarning('请先上传或加载对应的原始图片，再导入 VOC 标注文件！');
    return;
  }

  try {
    const xmlText = await file.raw.text();
    const xmlDoc = new DOMParser().parseFromString(xmlText, 'application/xml');

    if (xmlDoc.querySelector('parsererror')) {
      throw new Error('XML 文件格式不正确');
    }

    const objectNodes = Array.from(xmlDoc.getElementsByTagName('object'));
    if (objectNodes.length === 0) {
      proxy.$modal.msgWarning('该 VOC 文件中没有找到 object 标注节点！');
      return;
    }

    const getText = (root, tagName) => root?.getElementsByTagName(tagName)?.[0]?.textContent?.trim() || '';
    const getNumber = (root, tagName) => {
      const value = Number(getText(root, tagName));
      return Number.isFinite(value) ? value : NaN;
    };

    const sizeNode = xmlDoc.getElementsByTagName('size')[0];
    const vocWidth = getNumber(sizeNode, 'width');
    const vocHeight = getNumber(sizeNode, 'height');

    const vocObjects = objectNodes.map((node) => {
      const boxNode = node.getElementsByTagName('bndbox')[0];
      return {
        label: getText(node, 'name') || 'unknown',
        xmin: getNumber(boxNode, 'xmin'),
        ymin: getNumber(boxNode, 'ymin'),
        xmax: getNumber(boxNode, 'xmax'),
        ymax: getNumber(boxNode, 'ymax'),
        remark: ''
      };
    }).filter(item =>
        [item.xmin, item.ymin, item.xmax, item.ymax].every(Number.isFinite) &&
        item.xmax > item.xmin && item.ymax > item.ymin
    );

    if (vocObjects.length === 0) {
      proxy.$modal.msgWarning('VOC 文件中没有可用的矩形框坐标！');
      return;
    }

    if (canvas.value.getObjects().length > 0) {
      try {
        await proxy.$modal.confirm(
            '导入 VOC 标注会替换当前页面上已有的标注框，是否继续？',
            '导入确认',
            { confirmButtonText: '继续导入', cancelButtonText: '取消', type: 'warning' }
        );
      } catch (e) {
        return;
      }
    }

    // 只在导入动作发生时更新批量模板；后续工步中的任何编辑都不会改写这份快照。
    batchVocTemplate.value = {
      objects: vocObjects.map(item => ({ ...item })),
      size: { width: vocWidth, height: vocHeight }
    };

    renderVocAnnotations(
        vocObjects,
        { width: vocWidth, height: vocHeight },
        { markAsBatchTemplate: true }
    );
  } catch (error) {
    console.error('VOC 标注导入失败：', error);
    proxy.$modal.msgError(error?.message || 'VOC 标注文件解析失败，请检查 XML 格式！');
  }
};

const renderVocAnnotations = (vocObjects, vocSize, options = {}) => {
  const { clearExisting = true, showMessage = true, markAsBatchTemplate = false } = options;
  const bgImg = canvas.value?.backgroundImage;
  if (!bgImg) return;

  // VOC 文件尺寸可能与当前图片实际尺寸不同，这里先换算到原图坐标，再映射到画布。
  const sourceWidth = Number.isFinite(vocSize.width) && vocSize.width > 0 ? vocSize.width : bgImg.width;
  const sourceHeight = Number.isFinite(vocSize.height) && vocSize.height > 0 ? vocSize.height : bgImg.height;
  const sourceScaleX = bgImg.width / sourceWidth;
  const sourceScaleY = bgImg.height / sourceHeight;

  const bgScaleX = bgImg.scaleX;
  const bgScaleY = bgImg.scaleY;
  const bgLogicalLeft = bgImg.left - (bgImg.width * bgScaleX) / 2;
  const bgLogicalTop = bgImg.top - (bgImg.height * bgScaleY) / 2;

  if (clearExisting) clearCanvasAnnotations();
  isUsingBatchVocTemplate.value = markAsBatchTemplate;
  selectedAnnotationIds.value = [];
  hoveredAnnotationId.value = null;
  isDrawingMode.value = false;
  canvas.value.skipTargetFind = false;
  canvas.value.selection = true;

  let importedCount = 0;
  let skippedCount = 0;

  vocObjects.forEach((item, index) => {
    let realX = item.xmin * sourceScaleX;
    let realY = item.ymin * sourceScaleY;
    let realRight = item.xmax * sourceScaleX;
    let realBottom = item.ymax * sourceScaleY;

    realX = Math.max(0, Math.min(realX, bgImg.width));
    realY = Math.max(0, Math.min(realY, bgImg.height));
    realRight = Math.max(0, Math.min(realRight, bgImg.width));
    realBottom = Math.max(0, Math.min(realBottom, bgImg.height));

    const realWidth = realRight - realX;
    const realHeight = realBottom - realY;
    if (realWidth <= 0 || realHeight <= 0) {
      skippedCount++;
      return;
    }

    const uniqueId = `rect_voc_${Date.now()}_${index}_${Math.random().toString(36).substring(2, 8)}`;
    const rect = new fabric.Rect({
      left: bgLogicalLeft + realX * bgScaleX,
      top: bgLogicalTop + realY * bgScaleY,
      width: realWidth * bgScaleX,
      height: realHeight * bgScaleY,
      fill: 'rgba(255, 0, 0, 0)',
      stroke: 'red',
      strokeWidth: 1,
      selectable: true,
      evented: true,
      id: uniqueId,
      customData: { label: item.label, remark: item.remark || '' }
    });

    canvas.value.add(rect);
    annotationList.value.push({
      id: uniqueId,
      label: item.label,
      remark: item.remark || ''
    });
    importedCount++;
  });

  stepCount.value = importedCount + 1;
  canvas.value.discardActiveObject();
  canvas.value.requestRenderAll();

  if (showMessage) {
    if (skippedCount > 0) {
      proxy.$modal.msgWarning(`已导入 ${importedCount} 个标注框，另有 ${skippedCount} 个越界或无效框被忽略。`);
    } else {
      const batchTip = props.stepIds.length > 1 ? '，后续工步将恢复导入时的原始标注' : '';
      proxy.$modal.msgSuccess(`VOC 标注导入成功，共加载 ${importedCount} 个标注框${batchTip}。`);
    }
  }
};

const openAnnotationTool = () => {
  // 直接通过 window.location.href 触发自定义协议
  window.location.href = 'annotationdebug://';

  // 给用户一个反馈，因为唤起本地程序有时会有点慢
  proxy.$modal.msgSuccess('正在尝试唤起调试程序...');
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
  annotationDialogMode.value = 'create';
  editingAnnotationId.value = null;
  labelText.value = '';
  remark.value = '';
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

const openEditAnnotation = (item) => {
  if (!item?.id) return;

  annotationDialogMode.value = 'edit';
  editingAnnotationId.value = item.id;
  labelText.value = item.label || '';
  remark.value = item.remark || '';

  const target = canvas.value?.getObjects().find(obj => obj.id === item.id);
  if (target) {
    canvas.value.setActiveObject(target);
    canvas.value.requestRenderAll();
  }

  dialogVisible.value = true;
  nextTick(() => { inputRef.value?.focus(); });
};

const resetAnnotationDialog = () => {
  annotationDialogMode.value = 'create';
  editingAnnotationId.value = null;
  labelText.value = '';
  remark.value = '';
};

const confirmLabel = () => {
  const normalizedLabel = String(labelText.value || '').trim();
  const normalizedRemark = String(remark.value || '').trim();
  if (!normalizedLabel) return proxy.$modal.msgWarning('请输入标签');

  if (annotationDialogMode.value === 'edit') {
    const annotation = annotationList.value.find(item => item.id === editingAnnotationId.value);
    const target = canvas.value?.getObjects().find(obj => obj.id === editingAnnotationId.value);

    if (!annotation || !target) {
      proxy.$modal.msgWarning('未找到对应标注，可能已被删除');
      dialogVisible.value = false;
      resetAnnotationDialog();
      return;
    }

    annotation.label = normalizedLabel;
    annotation.remark = normalizedRemark;
    target.set({
      customData: {
        ...(target.customData || {}),
        label: normalizedLabel,
        remark: normalizedRemark
      }
    });
    target.setCoords();
    canvas.value.requestRenderAll();

    dialogVisible.value = false;
    resetAnnotationDialog();
    proxy.$modal.msgSuccess('标注信息已更新');
    return;
  }

  if (!activeRect) {
    proxy.$modal.msgWarning('未找到待添加的标注框');
    return;
  }

  const uniqueId = `rect_${Date.now()}`;

  activeRect.set({
    id: uniqueId,
    selectable: true,
    evented: true,
    customData: { label: normalizedLabel, remark: normalizedRemark }
  });

  annotationList.value.push({
    id: uniqueId,
    label: normalizedLabel,
    remark: normalizedRemark
  });

  stepCount.value += 1;
  canvas.value.setActiveObject(activeRect);
  canvas.value.renderAll();

  dialogVisible.value = false;
  activeRect = null;
  resetAnnotationDialog();
};

const removeAnnotation = (index, id) => {
  annotationList.value.splice(index, 1);
  selectedAnnotationIds.value = selectedAnnotationIds.value.filter(itemId => itemId !== id);
  if (hoveredAnnotationId.value === id) hoveredAnnotationId.value = null;

  const objects = canvas.value.getObjects();
  const objToRemove = objects.find(obj => obj.id === id);
  if (objToRemove) {
    canvas.value.remove(objToRemove);
    canvas.value.requestRenderAll();
  }
  stepCount.value = annotationList.value.length + 1;
};

const toggleAnnotationSelection = (id, checked) => {
  if (checked) {
    if (!selectedAnnotationIds.value.includes(id)) selectedAnnotationIds.value.push(id);
  } else {
    selectedAnnotationIds.value = selectedAnnotationIds.value.filter(itemId => itemId !== id);
  }
};

const toggleSelectAllAnnotations = (checked) => {
  selectedAnnotationIds.value = checked ? annotationList.value.map(item => item.id) : [];
};

const removeSelectedAnnotations = async () => {
  if (selectedAnnotationIds.value.length === 0) return;

  try {
    await proxy.$modal.confirm(
        `确定删除选中的 ${selectedAnnotationIds.value.length} 个标注吗？`,
        '批量删除确认',
        { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    );
  } catch (e) {
    return;
  }

  const idSet = new Set(selectedAnnotationIds.value);
  canvas.value.getObjects().forEach(obj => {
    if (obj.id && idSet.has(obj.id)) canvas.value.remove(obj);
  });
  annotationList.value = annotationList.value.filter(item => !idSet.has(item.id));
  selectedAnnotationIds.value = [];
  if (hoveredAnnotationId.value && idSet.has(hoveredAnnotationId.value)) hoveredAnnotationId.value = null;
  stepCount.value = annotationList.value.length + 1;
  canvas.value.discardActiveObject();
  canvas.value.requestRenderAll();
};

const setCanvasObjectHighlight = (id, isHover) => {
  const obj = canvas.value?.getObjects().find(o => o.id === id);
  if (obj) {
    obj.set('strokeWidth', isHover ? 3 : 1);
    obj.set('stroke', isHover ? '#409EFF' : 'red');
    canvas.value.requestRenderAll();
  }
};

const highlightAnnotation = (id, isHover) => {
  hoveredAnnotationId.value = isHover ? id : (hoveredAnnotationId.value === id ? null : hoveredAnnotationId.value);
  setCanvasObjectHighlight(id, isHover);
};

const handleCanvasObjectMouseOver = (opt) => {
  const id = opt?.target?.id;
  if (!id) return;
  hoveredAnnotationId.value = id;
  setCanvasObjectHighlight(id, true);
};

const handleCanvasObjectMouseOut = (opt) => {
  const id = opt?.target?.id;
  if (!id) return;
  if (hoveredAnnotationId.value === id) hoveredAnnotationId.value = null;
  setCanvasObjectHighlight(id, false);
};

const cancelAnnotation = () => {
  if (annotationDialogMode.value === 'create' && activeRect) {
    canvas.value.remove(activeRect);
    canvas.value.renderAll();
    activeRect = null;
  }
  dialogVisible.value = false;
  resetAnnotationDialog();
};

const clearCanvasAnnotations = () => {
  if (!canvas.value) return;
  const objects = canvas.value.getObjects();
  objects.forEach(obj => canvas.value.remove(obj));
  annotationList.value = [];
  selectedAnnotationIds.value = [];
  hoveredAnnotationId.value = null;
  stepCount.value = 1;
  canvas.value.requestRenderAll();
};

const clearCurrentAnnotations = () => {
  clearCanvasAnnotations();
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
  resetAnnotationDialog();
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
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.annotation-card {
  height: 600px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  background: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

/* 标注信息面板 */
.annotation-info-card {
  box-shadow: 0 8px 24px rgba(31, 45, 61, 0.06);
}

.annotation-info-card :deep(.el-card__header) {
  padding: 16px 16px 14px;
  border-bottom: 1px solid #edf0f5;
  background: linear-gradient(180deg, #fbfdff 0%, #ffffff 100%);
}

.annotation-info-card :deep(.el-card__body) {
  height: calc(100% - 67px);
  min-height: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.annotation-panel-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-heading-main {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.panel-heading-mark {
  width: 34px;
  height: 34px;
  flex: 0 0 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff, #79bbff);
  box-shadow: 0 5px 12px rgba(64, 158, 255, 0.24);
}

.panel-heading-copy {
  min-width: 0;
}

.panel-heading-title {
  color: #303133;
  font-size: 16px;
  font-weight: 700;
  line-height: 22px;
}

.panel-heading-subtitle {
  margin-top: 2px;
  color: #909399;
  font-size: 11px;
  line-height: 16px;
  white-space: nowrap;
}

.annotation-count-badge {
  flex: 0 0 auto;
  display: flex;
  align-items: baseline;
  gap: 4px;
  padding: 5px 9px;
  color: #606266;
  font-size: 11px;
  border: 1px solid #d9ecff;
  border-radius: 999px;
  background: #ecf5ff;
}

.annotation-count-badge strong {
  color: #409eff;
  font-size: 17px;
  line-height: 1;
}

.annotation-batch-toolbar {
  height: 52px;
  padding: 9px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  box-sizing: border-box;
  border-bottom: 1px solid #edf0f5;
  background: #fafbfc;
}

.batch-select-area {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.selection-summary {
  padding-left: 8px;
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
  border-left: 1px solid #dcdfe6;
}

.selection-summary strong {
  color: #409eff;
  font-weight: 700;
}

.annotation-scrollbar {
  flex: 1;
  min-height: 0;
  background: #f7f9fc;
}

.annotation-scrollbar :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

.annotation-list-content {
  padding: 10px;
}

.annotation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}


/* 包装信息面板继续沿用紧凑列表，避免被标注卡片样式影响 */
.annotation-card:not(.annotation-info-card) .annotation-item {
  padding: 12px 10px;
  border-bottom: 1px solid #ebeef5;
}

.annotation-card:not(.annotation-info-card) .annotation-item:hover {
  background: #f5f7fa;
}

.annotation-info-item {
  position: relative;
  min-height: 68px;
  margin-bottom: 8px;
  padding: 10px 8px 10px 10px;
  box-sizing: border-box;
  border: 1px solid #e4e7ed;
  border-radius: 9px;
  background: #fff;
  cursor: default;
}

.annotation-info-item:last-child {
  margin-bottom: 0;
}

.annotation-info-item:hover,
.annotation-info-item.is-highlighted {
  border-color: #a0cfff;
  background: #f5faff;
  box-shadow: 0 5px 14px rgba(64, 158, 255, 0.11);
  transform: translateY(-1px);
}

.annotation-info-item.is-highlighted::before {
  position: absolute;
  top: 10px;
  bottom: 10px;
  left: -1px;
  width: 3px;
  content: '';
  border-radius: 0 3px 3px 0;
  background: #409eff;
}

.annotation-info-item.is-selected {
  border-color: #b3d8ff;
  background: #ecf5ff;
}

.annotation-info-item.is-selected.is-highlighted {
  border-color: #409eff;
}

.annotation-checkbox {
  margin-right: 8px;
  flex-shrink: 0;
}

.annotation-sequence {
  width: 26px;
  height: 26px;
  flex: 0 0 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 9px;
  color: #909399;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.3px;
  border-radius: 7px;
  background: #f0f2f5;
}

.annotation-info-item.is-highlighted .annotation-sequence,
.annotation-info-item.is-selected .annotation-sequence {
  color: #409eff;
  background: #d9ecff;
}

.item-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 5px;
  overflow: hidden;
}

.annotation-item-info {
  min-width: 0;
  padding-right: 4px;
}

.annotation-item-topline {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.annotation-label-tag {
  max-width: 100%;
  font-weight: 600;
}

.annotation-label-tag :deep(.el-tag__content) {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.locating-text {
  flex: 0 0 auto;
  color: #409eff;
  font-size: 11px;
  font-weight: 600;
}

.item-label {
  font-weight: bold;
}

.item-remark {
  color: #606266;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.annotation-remark {
  line-height: 18px;
}

.annotation-remark.is-empty {
  color: #b1b3b8;
  font-style: italic;
}

.annotation-action-group {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 1px;
  margin-left: 3px;
}

.annotation-edit-button,
.annotation-delete-button {
  flex: 0 0 auto;
  opacity: 0.32;
  transition: opacity 0.2s ease, background-color 0.2s ease, transform 0.2s ease;
}

.annotation-edit-button:hover,
.annotation-delete-button:hover {
  transform: scale(1.06);
}

.annotation-info-item:hover .annotation-edit-button,
.annotation-info-item:hover .annotation-delete-button,
.annotation-info-item.is-highlighted .annotation-edit-button,
.annotation-info-item.is-highlighted .annotation-delete-button,
.annotation-info-item.is-selected .annotation-edit-button,
.annotation-info-item.is-selected .annotation-delete-button {
  opacity: 1;
}

.annotation-empty {
  height: 100%;
  padding-top: 70px;
  box-sizing: border-box;
}

.empty-description {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.empty-description strong {
  color: #606266;
  font-size: 14px;
}

.empty-description span {
  color: #a8abb2;
  font-size: 12px;
}

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