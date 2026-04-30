<template>
  <el-dialog title="图片人工标注" v-model="labelVisible" width="1200px" :close-on-click-modal="false" @close="handleClose">
    <div class="annotator-container">
      <el-card class="toolbar">
        <el-space>
          <el-button
              type="warning"
              @click="toggleDrawMode"
              :plain="!isDrawingMode"
              icon="Edit"
          >
            {{ isDrawingMode ? '结束绘制' : '开启绘制' }}
          </el-button>

          <el-button type="danger" @click="clearCanvasAnnotations" icon="Delete">清空标注</el-button>

          <el-button type="primary" @click="handleSaveProcess" icon="Check">完成标注并保存</el-button>
        </el-space>

        <div class="status-text" v-if="isDrawingMode">
          当前状态：<span style="color: red">绘制中...</span>
          <span style="margin-left: 15px;">Tips：开启绘制后按住ALT+鼠标左键可以拖拽图片，选中框后按 Delete 键可删除</span>
        </div>
      </el-card>

      <div class="main-workspace">
        <div class="canvas-wrapper" v-loading="canvasLoading" element-loading-text="正在加载原图...">
          <canvas id="label-canvas"></canvas>
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
    </div>

    <el-dialog
        v-model="dialogVisible"
        title="添加标注信息"
        width="300px"
        append-to-body
        :close-on-click-modal="false"
        @close="cancelAnnotation"
    >
      <el-input v-model="labelText" placeholder="请输入标签名称" />
      <el-input style="margin-top: 20px;" v-model="remark" placeholder="请输入备注（可选）" @keyup.enter="confirmLabel" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelAnnotation">取消</el-button>
          <el-button type="primary" @click="confirmLabel">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        v-model="datasetDialogVisible"
        title="选择保存的数据集"
        width="400px"
        append-to-body
        :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="数据集">
          <el-select v-model="selectedDatasetId" placeholder="请选择归属数据集" style="width: 100%">
            <el-option
                v-for="item in datasetOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="datasetDialogVisible = false">返回修改</el-button>
          <el-button type="primary" @click="submitFinalAnnotation" :loading="submitLoading">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick, getCurrentInstance } from 'vue';
import { fabric } from 'fabric';
import {listDataset} from "@/api/collection/dataset.js";
import {getUploadUrl} from "@/api/algorithm/algorithm.js";
import { updateData } from "@/api/collection/data" // 根据你的实际API引入
import axios from "axios";

const { proxy } = getCurrentInstance();

const props = defineProps({
  rowData: {
    type: Object,
    default: () => null
  }
});

const emit = defineEmits(['success']);
const labelVisible = defineModel();

// 画布相关状态
const canvas = ref(null);
const isDrawingMode = ref(false);
const canvasLoading = ref(false);
const annotationList = ref([]);
const uploadFile = ref(null); // 存储原始图片 Blob

// 标注框录入弹窗状态
const dialogVisible = ref(false);
const labelText = ref('');
const remark = ref('');

// 数据集选择弹窗状态
const datasetDialogVisible = ref(false);
const selectedDatasetId = ref(null);
const submitLoading = ref(false);
// 模拟的数据集列表，实际项目中需从接口获取
const datasetOptions = ref([
  { id: 1, name: '检测缺陷数据集 v1' },
  { id: 2, name: '样本训练数据集 v2' }
]);

const BASE_API = import.meta.env.VITE_APP_BASE_API;

// 绘图临时变量
let isMouseDown = false;
let startX = 0;
let startY = 0;
let activeRect = null;

watch(() => labelVisible.value, (visible) => {
  if (visible) {
    nextTick(() => {
      initCanvas();
      loadTargetImage();
      listDataset().then(res => {
        datasetOptions.value = res.rows;
      })
    });
  }
});

const handleClose = () => {
  isDrawingMode.value = false;
  if (canvas.value) canvas.value.dispose();
  annotationList.value = [];
  selectedDatasetId.value = null;
};

// ================= 画布初始化与图片加载 =================
const initCanvas = () => {
  if (canvas.value) canvas.value.dispose();
  canvas.value = new fabric.Canvas('label-canvas', {
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
  if (!labelVisible.value) return;
  if (e.key === 'Delete' || e.key === 'Backspace') {
    if (!canvas.value) return;
    const activeObjects = canvas.value.getActiveObjects();
    if (activeObjects.length) {
      canvas.value.discardActiveObject();
      activeObjects.forEach((obj) => {
        if (obj.id) annotationList.value = annotationList.value.filter(item => item.id !== obj.id);
        canvas.value.remove(obj);
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
    if (opt.e.altKey === true) {
      this.isDragging = true;
      this.selection = false;
      this.lastPosX = opt.e.clientX;
      this.lastPosY = opt.e.clientY;
    }
  });

  canvas.value.on('mouse:move', function (opt) {
    if (this.isDragging) {
      const vpt = this.viewportTransform;
      vpt[4] += opt.e.clientX - this.lastPosX;
      vpt[5] += opt.e.clientY - this.lastPosY;
      this.requestRenderAll();
      this.lastPosX = opt.e.clientX;
      this.lastPosY = opt.e.clientY;
    }
  });

  canvas.value.on('mouse:up', function () {
    if (this.isDragging) {
      this.setViewportTransform(this.viewportTransform);
      this.isDragging = false;
      this.selection = true;
    }
  });
};

const loadTargetImage = async () => {
  if (!props.rowData || !props.rowData.imagePath) return;

  canvasLoading.value = true;
  // 获取原图 URL (根据 index.vue 设定，[0]是原图)
  const originalUrl = props.rowData.imagePath[0];
  const fullUrl = originalUrl.startsWith('http') ? originalUrl : BASE_API + originalUrl;

  try {
    // 将远端图片抓取为 File/Blob 对象，解决跨域及后续上传复用问题
    const response = await fetch(fullUrl, { cache: "no-cache" });
    const blob = await response.blob();
    uploadFile.value = new File([blob], `source_${Date.now()}.jpg`, { type: blob.type });

    const reader = new FileReader();
    reader.onload = (e) => {
      const imgObj = new Image();
      imgObj.src = e.target.result;
      imgObj.onload = () => {
        canvas.value.clear();
        annotationList.value = [];
        const fImg = new fabric.Image(imgObj);
        const scale = Math.min(canvas.value.getWidth() / fImg.width, canvas.value.getHeight() / fImg.height);

        canvas.value.setBackgroundImage(fImg, canvas.value.renderAll.bind(canvas.value), {
          scaleX: scale, scaleY: scale,
          top: canvas.value.getHeight() / 2, left: canvas.value.getWidth() / 2,
          originX: 'center', originY: 'center'
        });
        canvas.value.setViewportTransform([1, 0, 0, 1, 0, 0]);
        canvasLoading.value = false;
      };
    };
    reader.readAsDataURL(uploadFile.value);
  } catch (error) {
    console.error("图片加载失败", error);
    proxy.$modal.msgError("图片加载失败");
    canvasLoading.value = false;
  }
};

// ================= 绘制与标注逻辑 =================
const toggleDrawMode = () => {
  if (!canvas.value.backgroundImage) return proxy.$modal.msgWarning('图片尚未加载完成！');
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
    left: startX, top: startY, width: 0, height: 0,
    fill: 'rgba(255, 0, 0, 0)', stroke: 'red', strokeWidth: 2,
    selectable: false, evented: false
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
  if (activeRect.width < 5 || activeRect.height < 5) {
    canvas.value.remove(activeRect);
    activeRect = null;
    return;
  }
  dialogVisible.value = true;
};

const confirmLabel = () => {
  if (!labelText.value) return proxy.$modal.msgWarning('请输入标签');
  const uniqueId = `rect_${Date.now()}`;
  activeRect.set({
    id: uniqueId, selectable: true, evented: true,
    customData: { label: labelText.value, remark: remark.value || '' }
  });

  annotationList.value.push({
    id: uniqueId, label: labelText.value, remark: remark.value || ''
  });

  canvas.value.setActiveObject(activeRect);
  canvas.value.renderAll();
  cancelAnnotation();
};

const cancelAnnotation = () => {
  if (activeRect && !activeRect.id) {
    canvas.value.remove(activeRect);
    canvas.value.renderAll();
  }
  dialogVisible.value = false;
  labelText.value = '';
  remark.value = '';
  activeRect = null;
};

const clearCanvasAnnotations = () => {
  if (!canvas.value) return;
  const objects = canvas.value.getObjects();
  objects.forEach(obj => canvas.value.remove(obj));
  annotationList.value = [];
  canvas.value.requestRenderAll();
};

const removeAnnotation = (index, id) => {
  annotationList.value.splice(index, 1);
  const objToRemove = canvas.value.getObjects().find(obj => obj.id === id);
  if (objToRemove) {
    canvas.value.remove(objToRemove);
    canvas.value.requestRenderAll();
  }
};

const highlightAnnotation = (id, isHover) => {
  const obj = canvas.value.getObjects().find(o => o.id === id);
  if (obj) {
    obj.set('strokeWidth', isHover ? 4 : 2);
    obj.set('stroke', isHover ? '#409EFF' : 'red');
    canvas.value.requestRenderAll();
  }
};

// ================= 保存核心流程：选择数据集 -> 提取坐标 -> 提交 =================

// 步骤 1：触发保存，弹出数据集选择框
const handleSaveProcess = () => {
  if (!canvas.value || annotationList.value.length === 0) {
    return proxy.$modal.msgWarning('请先完成至少一个框的标注！');
  }
  datasetDialogVisible.value = true;
};

// 步骤 2：确认数据集并上传
const submitFinalAnnotation = async () => {
  if (!selectedDatasetId.value) {
    return proxy.$modal.msgWarning('必须选择一个数据集才能保存');
  }

  submitLoading.value = true;
  try {
    const coordsData = extractCoords();

    // 【替换以下块】：换成你实际的 Minio 上传逻辑和后台更新接口
    const annotImageFile = getAnnotatedFile();
    const resAnnot = await getUploadUrl();
    await axios.put(resAnnot.data.url, annotImageFile, { headers: {'Content-Type': 'image/jpeg'} });
    const labelImage = resAnnot.data.objectName;

    // 组装提交给后端的数据包
    const submitPayload = {
      id: props.rowData.id,               // 对应当前编辑数据的ID
      datasetId: selectedDatasetId.value, // 选择的数据集ID
      coordsInfo: JSON.stringify(coordsData), // 绝对坐标JSON
      labelImage: labelImage,  // 如需保存画了框的图
    };

    await updateData(submitPayload);

    // ============================================

    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 800));

    proxy.$modal.msgSuccess('标注数据已归档至所选数据集！');
    datasetDialogVisible.value = false;
    emit('success');
  } catch (error) {
    console.error(error);
    proxy.$modal.msgError('保存失败，请重试');
  } finally {
    submitLoading.value = false;
  }
};

// 提取绝对坐标核心方法（基于原图真实分辨率）
const extractCoords = () => {
  const bgImg = canvas.value.backgroundImage;
  if (!bgImg) return [];
  const scaleX = bgImg.scaleX;
  const scaleY = bgImg.scaleY;
  const bgLogicalLeft = bgImg.left - (bgImg.width * scaleX) / 2;
  const bgLogicalTop = bgImg.top - (bgImg.height * scaleY) / 2;

  const labelMap = {};

  canvas.value.getObjects().forEach(rectObj => {
    if (rectObj.type === 'rect' && rectObj.customData) {
      const label = rectObj.customData.label;
      if (!labelMap[label]) labelMap[label] = [];

      let realX = Math.round((rectObj.left - bgLogicalLeft) / scaleX);
      let realY = Math.round((rectObj.top - bgLogicalTop) / scaleY);
      let realW = Math.round((rectObj.width * rectObj.scaleX) / scaleX);
      let realH = Math.round((rectObj.height * rectObj.scaleY) / scaleY);

      // 防越界保护
      realX = Math.max(0, realX);
      realY = Math.max(0, realY);
      realW = Math.min(realW, bgImg.width - realX);
      realH = Math.min(realH, bgImg.height - realY);

      labelMap[label].push({ x: realX, y: realY, width: realW, height: realH, remark: rectObj.customData.remark });
    }
  });

  return Object.keys(labelMap).map(key => ({ label: key, posList: labelMap[key] }));
};

// 提取带框图片的File流(可选)
const getAnnotatedFile = () => {
  const bgImage = canvas.value.backgroundImage;
  const multiplier = 1 / (bgImage ? bgImage.scaleX : 1);
  const dataURL = canvas.value.toDataURL({ format: 'jpeg', quality: 0.9, multiplier });
  const arr = dataURL.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) u8arr[n] = bstr.charCodeAt(n);
  return new File([new Blob([u8arr], {type: mime})], `annotated_${Date.now()}.jpg`, {type: 'image/jpeg'});
};
</script>

<style scoped>
.annotator-container { display: flex; flex-direction: column; gap: 20px; align-items: center; padding: 10px; }
.toolbar { width: 100%; max-width: 1150px; }
.main-workspace { display: flex; gap: 20px; width: 100%; max-width: 1150px; align-items: stretch; }
.canvas-wrapper { border: 1px solid #ccc; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); width: 800px; height: 600px; flex-shrink: 0; }
.annotation-list-wrapper { flex: 1; display: flex; flex-direction: column; }
.annotation-card { height: 600px; border-radius: 4px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
.annotation-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 10px; border-bottom: 1px solid #ebeef5; transition: background-color 0.3s; }
.annotation-item:hover { background-color: #f5f7fa; }
.item-info { display: flex; flex-direction: column; gap: 5px; flex: 1; overflow: hidden; }
.item-label { font-weight: bold; }
.item-remark { font-size: 13px; color: #606266; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.status-text { margin-top: 10px; font-size: 14px; color: #666; }
</style>