<template>
  <el-dialog title="图片标注" v-model="labelVisible" width="1000px" :close-on-click-modal="false">
    <div class="annotator-container">
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

          <el-button type="danger" @click="clearCanvas" icon="Delete">清空标注</el-button>
          <el-button type="primary" @click="uploadToMinio" icon="Check">保存标注结果</el-button>
        </el-space>

        <div class="status-text" v-if="isDrawingMode">
          当前状态：<span style="color: red">绘制中...</span>
          <span style="margin-left: 15px;">Tips：开启绘制后按住ALT+鼠标左键可以拖拽图片</span>
        </div>
      </el-card>

      <div class="canvas-wrapper">
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
        <div v-if="!cameraConnected" class="camera-loading">
          <el-icon class="is-loading" :size="40">
            <Loading/>
          </el-icon>
          <p>正在连接相机信号...</p>
        </div>
        <img
            v-else
            :src="previewUrl"
            class="live-stream"
            alt="实时监控"
            @error="handleCameraError"
        />
      </div>

      <div class="camera-controls">
         <span style="color: #909399; margin-right: 20px;">
           <el-icon><VideoCamera/></el-icon> 实时模式
         </span>
        <el-button type="primary" size="large" icon="CameraFilled" :loading="isCapturing" @click="handleCapture">
          立即抓拍并去标注
        </el-button>
      </div>
    </el-dialog>

  </el-dialog>
</template>

<script setup>
import {ref, onMounted, watch, nextTick, getCurrentInstance} from 'vue';
import {fabric} from 'fabric';
import {getUploadUrl} from "@/api/algorithm/algorithm.js";
import {updateStep} from "@/api/craft/step.js";
import axios from "axios";
import {checkCamera} from "@/api/craft/craft.js";

const {proxy} = getCurrentInstance()

// --- 基础状态 ---
const labelVisible = defineModel()
const canvas = ref(null);
const isDrawingMode = ref(false);
const dialogVisible = ref(false);
const labelText = ref('');
const inputRef = ref(null);
const stepCount = ref(0);
const uploadFile = ref(null)
const remark = ref('')

// --- 相机相关状态 ---
const cameraVisible = ref(false);
const previewUrl = ref('');
const cameraConnected = ref(false);
const isCapturing = ref(false);
let previewTimer = null;
const BASE_API = import.meta.env.VITE_APP_BASE_API;

// 绘制临时变量
let isMouseDown = false;
let startX = 0;
let startY = 0;
let activeRect = null;

const props = defineProps({
  visible: Boolean,
  stepId: Number,
});

const emit = defineEmits(['change-status'])

// --- 生命周期 ---
onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
});

// 监听弹窗显示初始化
watch(() => props.visible, (visible) => {
  if (visible) {
    nextTick(() => {
      initCanvas();
      stepCount.value = 1
      uploadFile.value = null
    })
  } else {
    isDrawingMode.value = false;
    if (canvas.value) canvas.value.clear();
    stopPreview(); // 确保关闭时停止相机请求
  }
})

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

const initCanvas = () => {
  if (canvas.value) {
    canvas.value.dispose(); // 防止重复创建
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

/**
 * 将 File 对象加载到 Canvas 背景
 * @param {File} file 图片文件对象
 */
const loadFileToCanvas = (file) => {
  // 1. 保存文件引用（用于后续上传 MinIO）
  uploadFile.value = file;

  // 2. 读取并渲染
  const reader = new FileReader();
  reader.onload = (e) => {
    const imgObj = new Image();
    imgObj.src = e.target.result;
    imgObj.onload = () => {
      // 如果之前有背景，先清除
      canvas.value.clear();
      // 重置步骤计数（新图片）
      stepCount.value = 1;

      const fImg = new fabric.Image(imgObj);
      const canvasWidth = canvas.value.getWidth();
      const canvasHeight = canvas.value.getHeight();
      const scaleX = canvasWidth / fImg.width;
      const scaleY = canvasHeight / fImg.height;
      const scale = Math.min(scaleX, scaleY);

      canvas.value.setBackgroundImage(fImg, canvas.value.renderAll.bind(canvas.value), {
        scaleX: scale,
        scaleY: scale,
        top: canvasHeight / 2,
        left: canvasWidth / 2,
        originX: 'center',
        originY: 'center'
      });
      canvas.value.setViewportTransform([1, 0, 0, 1, 0, 0]);
    };
  };
  reader.readAsDataURL(file);
};

// 原有的本地上传回调
const handleFileChange = (file) => {
  // Element Plus 的 file 是封装对象，file.raw 才是原生 File
  loadFileToCanvas(file.raw);
};

// --- 核心修改：相机控制逻辑 ---

const openCameraDialog = async () => {
  cameraVisible.value = true;
  // await checkCamera().then(res => {
  //   if (res.code === 200){
  //     cameraConnected.value = true; // 假设一开始是连接的
  //     startPreview();
  //   }else
  //     cameraConnected.value = false;
  // })
  cameraConnected.value = true; // 假设一开始是连接的
  startPreview();
};

const closeCameraDialog = () => {
  stopPreview();
  cameraVisible.value = false;
};

// 开启预览轮询
const startPreview = () => {
  stopPreview();
  // 立即执行一次
  refreshPreview();
  // 设置 60ms 刷新一次 (约 16fps)
  previewTimer = setInterval(refreshPreview, 100);
};

const refreshPreview = () => {
  // 加上时间戳防止缓存
  previewUrl.value = `${BASE_API}/camera/preview?t=${Date.now()}`;
};

const stopPreview = () => {
  if (previewTimer) {
    clearInterval(previewTimer);
    previewTimer = null;
  }
};

const handleCameraError = () => {
  // 图片加载失败（可能是后端还没启动好）
  // cameraConnected.value = false;
  // 这里的错误处理要小心，因为轮询很快，偶尔一帧失败不用管，一直失败才提示
};

// 拍照并加载到画布
const handleCapture = async () => {
  isCapturing.value = true;
  try {
    const res = await axios({
      method: 'get',
      url: `${BASE_API}/camera/capture`,
      responseType: 'blob' // 关键
    });

    if (res.data.type && res.data.type.includes('json')) {
      proxy.$modal.msgError('拍照失败，后端返回了错误信息');
      return;
    }

    // 将 Blob 转换为 File 对象
    const blob = res.data;
    const filename = `capture_${Date.now()}.jpg`;
    const file = new File([blob], filename, {type: 'image/jpeg'});

    // 调用统一的加载方法
    loadFileToCanvas(file);

    proxy.$modal.msgSuccess('抓拍成功！');
    closeCameraDialog(); // 关闭相机弹窗，回到标注界面

  } catch (error) {
    console.error(error);
    proxy.$modal.msgError('拍照请求失败');
  } finally {
    isCapturing.value = false;
  }
};

// --- 绘制逻辑 (保持原有逻辑，稍作优化) ---

const toggleDrawMode = () => {
  // 必须先有背景图才能标注
  if (!canvas.value.backgroundImage) {
    proxy.$modal.msgWarning('请先上传图片或使用相机拍照！');
    return;
  }
  isDrawingMode.value = !isDrawingMode.value;
  canvas.value.skipTargetFind = isDrawingMode.value;
  canvas.value.selection = !isDrawingMode.value;
};

const onMouseDown = (opt) => {
  if (!isDrawingMode.value) return;

  isMouseDown = true;
  const pointer = canvas.value.getPointer(opt.e);
  startX = pointer.x;
  startY = pointer.y;

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
  let width = Math.abs(pointer.x - startX);
  let height = Math.abs(pointer.y - startY);
  if (pointer.x < startX) activeRect.set({left: pointer.x});
  if (pointer.y < startY) activeRect.set({top: pointer.y});
  activeRect.set({width, height});
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
  nextTick(() => {
    inputRef.value?.focus();
  });
};

const confirmLabel = () => {
  if (!labelText.value || !remark.value) {
    proxy.$modal.msgWarning('请输入标签和数量');
    return;
  }
  const text = new fabric.Text(labelText.value, {
    fontSize: 16,
    fill: 'white',
    backgroundColor: 'red',
    left: activeRect.left,
    top: activeRect.top - 20 < 0 ? activeRect.top : activeRect.top - 20,
    padding: 5
  });
  const group = new fabric.Group([activeRect, text], {
    left: activeRect.left,
    top: text.top,
    selectable: true,
  });

  // 保存坐标信息
  group.set({
    customData: {
      label: labelText.value,
      remark: remark.value,
    }
  });

  canvas.value.remove(activeRect);

  stepCount.value += 1;
  canvas.value.add(group);
  canvas.value.setActiveObject(group);
  canvas.value.renderAll();

  dialogVisible.value = false;
  labelText.value = ''
  remark.value = '';
  activeRect = null;
};

const cancelAnnotation = () => {
  if (activeRect) {
    canvas.value.remove(activeRect);
    canvas.value.renderAll();
  }
  dialogVisible.value = false;
  labelText.value = '';
  remark.value = '';
  activeRect = null;
};

const clearCanvas = () => {
  const bg = canvas.value.backgroundImage;
  stepCount.value = 1;
  canvas.value.clear();
  if (bg) {
    canvas.value.setBackgroundImage(bg, canvas.value.renderAll.bind(canvas.value));
  }
};

// 上传逻辑 (MinIO)
const uploadToMinio = async () => {
  if (!canvas.value || canvas.value.getObjects().length === 0) {
    proxy.$modal.msgWarning('请先完成标注！')
    return
  }

  // 1. 检查是否有文件
  if (!uploadFile.value) {
    proxy.$modal.msgError('未找到原始图片文件');
    return;
  }

  proxy.$modal.loading('正在上传图片和标注数据...')

  try {
    // 获取上传链接
    let url = '';
    let objectName = '';
    await getUploadUrl().then(res => {
      url = res.data.url
      objectName = res.data.objectName
    })

    // 2. 上传原始图片 (或者你可以上传带标注的截图，取决于业务需求)
    const annotationFile = getFile()
    await axios.put(url, annotationFile, {
      headers: {'Content-Type': 'image/png'}
    });

    const data = {
      id: props.stepId,
      guideMapUrl: objectName,
      coordsInfo: JSON.stringify(getDataEasy()),
    }
    await updateStep(data)


    emit('change-status')
    proxy.$modal.msgSuccess('绑定成功！');

    // 清理
    uploadFile.value = null;
    labelVisible.value = false;

  } catch (error) {
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

    const gLeft = group.left;
    const gTop = group.top;
    const gScaleX = group.scaleX;
    const gScaleY = group.scaleY;

    const OFFSET_Y = 20;

    const rectCanvasLeft = gLeft;
    const rectCanvasTop = gTop + (OFFSET_Y * gScaleY);

    const rectObj = group.getObjects().find(o => o.type === 'rect');
    const rectCanvasWidth = rectObj.width * rectObj.scaleX * gScaleX;
    const rectCanvasHeight = rectObj.height * rectObj.scaleY * gScaleY;

    labelSet.add(group.customData.label);

    // --- 最终转回原图坐标 ---
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
    results.forEach(result => {
      if (result.label === label) {
        tempCoordsList.push(result.pos)
      }
    })
    coordsInfo.push({
      label: label,
      posList: tempCoordsList
    })
  })
  return coordsInfo;
}

const getFile = () => {
  const bgImage = canvas.value.backgroundImage;

  // 获取当前背景图在画布中的缩放比例
  // 例如：原图 1920x1080，画布 800x600，图片为了适应画布被缩放了 0.4 倍
  const currentScale = bgImage ? bgImage.scaleX : 1;

  // 计算导出倍率：我们要导出的图大小应该是 画布大小 / 0.4 = 原图大小
  const multiplier = 1 / currentScale;

  // 导出为 DataURL (Base64)
  const dataURL = canvas.value.toDataURL({
    format: 'jpeg',
    quality: 0.9,
    multiplier: multiplier // 关键参数：让导出的图片恢复到原图分辨率
  });

  // 转换为 Blob -> File
  const blob = dataURLtoBlob(dataURL);

  // 生成一个新的文件名
  const filename = `annotated_${Date.now()}.jpg`;
  return new File([blob], filename, {type: 'image/jpeg'});
}

// Base64 转 Blob 的辅助函数
const dataURLtoBlob = (dataurl) => {
  const arr = dataurl.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], {type: mime});
};
</script>

<style scoped>
.annotator-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
  padding: 10px;
}

.toolbar {
  width: 100%;
  max-width: 850px;
}

.canvas-wrapper {
  border: 1px solid #ccc;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.status-text {
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

/* 相机弹窗样式 */
.camera-preview-box {
  width: 100%;
  height: 500px;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
}

.live-stream {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持比例 */
}

.camera-loading {
  color: #fff;
  text-align: center;
}

.camera-controls {
  display: flex;
  justify-content: flex-end; /* 按钮靠右 */
  align-items: center;
  padding: 0 10px;
}
</style>