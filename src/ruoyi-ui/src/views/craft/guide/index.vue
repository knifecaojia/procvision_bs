<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="16" :xs="24">
        <el-card shadow="hover" class="camera-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Monitor /></el-icon> 实时监控 (Live View)</span>
              <el-tag type="success" effect="dark" v-if="isConnected">信号正常</el-tag>
              <el-tag type="danger" effect="dark" v-else>信号中断</el-tag>
            </div>
          </template>

          <div class="monitor-screen">
            <img
                v-if="previewUrl"
                :src="previewUrl"
                alt="监控画面"
                class="live-image"
                @error="handleImageError"
                @load="handleImageLoad"
            />

            <div v-else class="empty-placeholder">
              <el-icon :size="50" class="is-loading"><Loading /></el-icon>
              <p>正在连接相机...</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8" :xs="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><Operation /></el-icon> 操作面板</span>
            </div>
          </template>

          <div class="control-panel">
            <el-alert
                title="操作提示"
                type="info"
                description="点击下方按钮抓取当前高清帧并下载。"
                show-icon
                :closable="false"
                style="margin-bottom: 20px"
            />

            <el-form label-width="100px">
              <el-form-item label="自动刷新">
                <el-switch v-model="isAutoRefresh" active-text="开启" inactive-text="暂停" />
              </el-form-item>
              <el-form-item label="刷新频率">
                <el-slider v-model="refreshInterval" :min="40" :max="1000" :step="10" :format-tooltip="val => val + 'ms'" />
              </el-form-item>
            </el-form>

            <div class="action-buttons">
              <el-button
                  type="primary"
                  size="large"
                  icon="Camera"
                  :loading="capturing"
                  @click="handleCapture"
                  class="capture-btn"
              >
                立即拍照 (高清)
              </el-button>
            </div>

            <div class="status-info" style="margin-top: 30px; color: #909399; font-size: 13px;">
              <p>相机状态: {{ isConnected ? '在线' : '离线' }}</p>
              <p>当前延迟:约 {{ refreshInterval }} ms</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="CameraMonitor">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios'; // 建议直接引入 axios 处理二进制流，或者使用若依的 request.js

// --- 状态定义 ---
const previewUrl = ref('');
const isConnected = ref(false);
const capturing = ref(false);
const isAutoRefresh = ref(true);
const refreshInterval = ref(50); // 默认 50ms (约20帧/秒)
let timer = null;

const BASE_URL = import.meta.env.VITE_APP_BASE_API;

// --- 核心逻辑 1: 实时预览 ---
const startPreview = () => {
  stopPreview(); // 防止重复开启

  if (!isAutoRefresh.value) return;

  timer = setInterval(() => {
    // 加上时间戳 t，强制浏览器不缓存，向后端请求最新的一张图
    previewUrl.value = `${BASE_URL}/camera/preview?t=${Date.now()}`;
    // 只要还在请求，就认为大致是连接状态 (简易判断)
    isConnected.value = true;
  }, refreshInterval.value);
};

const stopPreview = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
};

// 监听刷新频率变化，动态调整
watch(refreshInterval, () => {
  if (isAutoRefresh.value) {
    startPreview();
  }
});

// 监听开关
watch(isAutoRefresh, (val) => {
  if (val) startPreview();
  else stopPreview();
});

const handleImageError = () => {
  // 图片加载失败（可能是后端重启中，或者还没准备好）
  isConnected.value = false;
};

const handleImageLoad = () => {
  isConnected.value = true;
};

// --- 核心逻辑 2: 高清拍照 ---
const handleCapture = async () => {
  capturing.value = true;
  try {
    // 请求二进制流 (blob)
    const res = await axios({
      method: 'get',
      url: `${BASE_URL}/camera/capture`,
      responseType: 'blob', // 关键！必须声明接收 blob
      timeout: 5000
    });

    if (res.data.type === 'application/json') {
      // 如果后端返回的是 JSON 报错而不是图片
      ElMessage.error('拍照失败：后端返回错误数据');
      return;
    }

    // 此时 res.data 就是图片 Blob 对象
    downloadBlob(res.data, `Capture_${getNowTime()}.jpg`);
    ElMessage.success('拍照成功，已开始下载');
  } catch (error) {
    console.error(error);
    ElMessage.error('拍照请求失败，请检查相机连接');
  } finally {
    capturing.value = false;
  }
};

// 通用下载工具函数
const downloadBlob = (blob, fileName) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();

  // 清理
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

// 获取格式化时间字符串
const getNowTime = () => {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  const h = String(now.getHours()).padStart(2, '0');
  const min = String(now.getMinutes()).padStart(2, '0');
  const s = String(now.getSeconds()).padStart(2, '0');
  return `${y}${m}${d}_${h}${min}${s}`;
};

// --- 生命周期 ---
onMounted(() => {
  startPreview();
});

onUnmounted(() => {
  stopPreview();
});
</script>

<style scoped>
.camera-card {
  height: 600px;
  display: flex;
  flex-direction: column;
}

.monitor-screen {
  width: 100%;
  height: 500px;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.live-image {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 保持图片比例，不拉伸 */
  display: block;
}

.empty-placeholder {
  color: #fff;
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.capture-btn {
  width: 100%;
  margin-top: 20px;
  height: 50px;
  font-size: 16px;
}
</style>