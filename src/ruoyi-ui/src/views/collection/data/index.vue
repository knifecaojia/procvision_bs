<template>
  <div class="collection-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><CameraFilled /></el-icon> 产品图像快速采集
          </span>
          <el-tag type="warning" effect="dark">扫码 -> 拍照 -> 自动归档</el-tag>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <div class="step-block" :class="{ 'active-step': !currentBarcode }">
            <div class="step-title">Step 1: 扫描条码</div>

            <el-input
                v-model="barcodeInput"
                ref="barcodeInputRef"
                placeholder="请扫描或输入条码"
                prefix-icon="Scissor"
                clearable
                :disabled="!!currentBarcode"
                @keyup.enter="handleScan"
            >
              <template #append>
                <el-button @click="handleScan" icon="Check" :disabled="!!currentBarcode">确定</el-button>
              </template>
            </el-input>

            <div class="scan-tip">光标聚焦后，扫码枪会自动触发</div>
          </div>

          <div class="step-block" v-if="currentBarcode" style="border-color: #67C23A; background: #f0f9eb;">
            <div class="step-title" style="color: #67C23A; border-color: #67C23A;">
              扫描结果
            </div>
            <div class="current-task-info">
              <span class="label">产品信息：</span>
              <span class="value">{{ currentBarcode }}</span>
              <el-button type="danger" link icon="Close" @click="resetFlow(false)" size="small">取消/重扫</el-button>
            </div>
          </div>

          <div class="action-area" v-if="capturedImageFile">
            <el-divider>Step 3: 保存</el-divider>
            <el-button type="primary" size="large" style="width: 100%" @click="submitData" :loading="submitting">
              保存并上传
            </el-button>
            <div style="text-align: center; margin-top: 10px; color: #909399; font-size: 12px;">
              完成保存后将自动重置，可直接扫下一个
            </div>
          </div>
        </el-col>

        <el-col :span="16">
          <div class="camera-wrapper">
            <div class="preview-box">
              <div class="box-label">
                <el-icon><VideoCamera /></el-icon> 实时监控
              </div>
              <img
                  v-if="cameraConnected"
                  :src="previewUrl"
                  class="live-stream"
                  alt="实时监控"
              />
              <div v-else class="camera-placeholder">
                <el-icon class="is-loading" :size="30"><Loading /></el-icon>
                <p>正在连接相机...</p>
              </div>
            </div>

            <div class="capture-box">
              <div class="box-label">
                <el-icon><Picture /></el-icon> 采集预览
              </div>
              <img v-if="capturedImageUrl" :src="capturedImageUrl" class="capture-img" />
              <div v-else class="empty-capture">暂无抓拍</div>
            </div>
          </div>

          <div class="camera-control-bar">
            <el-button
                type="danger"
                icon="Camera"
                circle
                style="width: 60px; height: 60px; font-size: 24px;"
                :disabled="!currentBarcode"
                :loading="isCapturing"
                @click="handleCapture"
            ></el-button>
            <div class="control-tip">
              {{ !currentBarcode ? '请先扫描条码' : '点击按钮拍照 (Step 2)' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue';
import axios from "axios";
import { getUploadUrl } from "@/api/algorithm/algorithm.js";
import {addData} from "@/api/collection/data.js";
// 引入您的业务保存接口，例如 saveProductImage(data)
// import { saveProductImage } from "@/api/production/index.js";

const { proxy } = getCurrentInstance();
const BASE_API = import.meta.env.VITE_APP_BASE_API;

// --- 基础数据 ---
const barcodeInput = ref('');
const currentBarcode = ref(''); // 锁定后的条码，用于提交
const barcodeInputRef = ref(null);
const submitting = ref(false);

// --- 相机相关 ---
const previewUrl = ref('');
const cameraConnected = ref(true); // 默认设为true，开始轮询
const isCapturing = ref(false);
const capturedImageFile = ref(null);
const capturedImageUrl = ref('');
let previewTimer = null;

// --- 生命周期 ---
onMounted(() => {
  startPreview();
  focusInput();
  // 全局回车监听：如果在保存阶段，按回车直接提交
  // window.addEventListener('keydown', handleGlobalEnter);
});

onBeforeUnmount(() => {
  stopPreview();
  // window.removeEventListener('keydown', handleGlobalEnter);
});

// const handleGlobalEnter = (e) => {
//   if (e.key === 'Enter') {
//     // 如果已经抓拍了图片，且不在输入框内，则触发保存
//     if (capturedImageFile.value && document.activeElement !== barcodeInputRef.value?.$el.querySelector('input')) {
//       submitData();
//     }
//   }
// };

const focusInput = () => {
  nextTick(() => {
    barcodeInputRef.value?.focus();
  });
};

// --- 1. 扫码逻辑 (简化版) ---
const handleScan = () => {
  const code = barcodeInput.value.trim();
  if (!code) {
    proxy.$modal.msgWarning('条码不能为空');
    return;
  }

  // 直接锁定条码，不需要查询后端详情
  currentBarcode.value = code;

  // 提示用户下一步
  proxy.$modal.msgSuccess(`条码已录入: ${code}，请拍照`);

  // 可选：如果需要在扫码瞬间通知后端记录日志，可在此处调用 API
  // await logScanAction(code);
};

// --- 2. 相机逻辑 (保持复用) ---
const startPreview = () => {
  stopPreview();
  refreshPreview();
  previewTimer = setInterval(refreshPreview, 100);
};

const refreshPreview = () => {
  // 添加时间戳防止缓存
  previewUrl.value = `${BASE_API}/camera/preview?t=${Date.now()}`;
};

const stopPreview = () => {
  if (previewTimer) clearInterval(previewTimer);
};

const handleCapture = async () => {
  if (!currentBarcode.value) {
    proxy.$modal.msgWarning('请先扫描条码！');
    focusInput();
    return;
  }

  isCapturing.value = true;
  try {
    const res = await axios({
      method: 'get',
      url: `${BASE_API}/camera/capture`,
      responseType: 'blob'
    });

    if (res.data.type && res.data.type.includes('json')) {
      proxy.$modal.msgError('拍照异常');
      return;
    }

    const blob = res.data;
    const filename = `${currentBarcode.value}_${Date.now()}.jpg`;
    capturedImageFile.value = new File([blob], filename, { type: 'image/jpeg' });
    capturedImageUrl.value = URL.createObjectURL(blob);

  } catch (error) {
    proxy.$modal.msgError('拍照请求失败');
  } finally {
    isCapturing.value = false;
  }
};

// --- 3. 提交保存 ---
const submitData = async () => {
  if (!capturedImageFile.value || submitting.value) return;

  submitting.value = true;
  try {
    // 1. 获取 MinIO 上传地址
    let uploadUrl = '';
    let objectName = '';
    await getUploadUrl().then(res => {
      uploadUrl = res.data.url;
      objectName = res.data.objectName;
    });

    // 2. 上传图片
    await axios.put(uploadUrl, capturedImageFile.value, {
      headers: { 'Content-Type': 'image/jpeg' }
    });

    // 3. 提交业务数据 (条码 + 图片路径)
    // 这里根据您后端的实际接口修改
    const postData = {
      data: currentBarcode.value,  // 直接传条码字符串
      imagePath: objectName,          // 图片在MinIO的路径
    };

    await addData(postData);
    // console.log('发送给后端的数据:', postData);

    proxy.$modal.msgSuccess('保存成功！');
    resetFlow(true); // 成功后重置

  } catch (error) {
    console.error(error);
    proxy.$modal.msgError('上传保存失败');
  } finally {
    submitting.value = false;
  }
};

// --- 重置流程 ---
const resetFlow = (isSuccess) => {
  barcodeInput.value = '';
  currentBarcode.value = '';
  capturedImageFile.value = null;
  capturedImageUrl.value = '';

  // 重新聚焦输入框，方便连续作业
  focusInput();
};
</script>

<style scoped>
.collection-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.step-block {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.active-step {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.step-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  color: #303133;
}

.scan-tip {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

.current-task-info {
  display: flex;
  align-items: center;
  font-size: 16px;
}
.current-task-info .label {
  font-weight: bold;
  color: #67C23A;
}
.current-task-info .value {
  font-weight: bold;
  font-size: 20px;
  margin: 0 10px;
}

/* 相机区域样式 */
.camera-wrapper {
  display: flex;
  gap: 15px;
  height: 450px;
}

.preview-box, .capture-box {
  flex: 1;
  background: #000;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.box-label {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  z-index: 10;
}

.live-stream, .capture-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.camera-placeholder, .empty-capture {
  color: #909399;
  text-align: center;
}

.camera-control-bar {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.control-tip {
  margin-top: 10px;
  color: #606266;
}
</style>