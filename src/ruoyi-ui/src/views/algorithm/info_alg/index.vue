<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="算法编码" prop="code">
        <el-input
            v-model="queryParams.code"
            placeholder="请输入算法编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="算法名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入算法名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="算法版本" prop="version">
        <el-input
            v-model="queryParams.version"
            placeholder="请输入算法版本"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="algorithmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="算法名称" align="center" prop="name"/>
      <el-table-column label="算法版本" align="center" prop="version"/>
      <el-table-column label="算法大小" align="center" prop="size">
        <template #default="scope">
          {{ scope.row.size }} MB
        </template>
      </el-table-column>
      <el-table-column label="算法描述" align="center" prop="desc"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
          >删除
          </el-button>
          <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)">下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改算法对话框 -->
    <!--    <AddAlgorithm v-model="open" :formData="form" :fileList="fileList"></AddAlgorithm>-->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body @close="onClose">
      <el-form ref="algorithmRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="算法名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入算法名称"/>
        </el-form-item>
        <el-form-item label="算法版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入算法版本"/>
        </el-form-item>
        <el-form-item label="算法描述" prop="desc">
          <el-input type="textarea" v-model="form.desc" placeholder="请输入算法描述"/>
        </el-form-item>
        <el-form-item label="上传算法">
          <el-upload
              ref="uploadRef"
              drag
              action=""
              :http-request="customUpload"
              :on-change="handleFileChange"
              :file-list="fileList"
              :show-file-list="false"
              accept=".pdf,.doc,.docx,.jpg,.png,.zip,.rar"
              :disabled="isUploading"
          >
            <el-icon class="el-icon--upload">
              <upload-filled/>
            </el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或 <em>点击上传</em>
            </div>
            <div class="el-upload__tip" v-if="selectedFile">
              已选择: {{ selectedFile.name }} ({{ formatSize(selectedFile.size) }})
            </div>
          </el-upload>

          <transition name="el-fade-in">
            <div v-if="uploadProgress > 0 || isUploading" class="upload-status-panel">
              <div class="progress-info">
                <span>{{ uploadStatus.text }}</span>
                <span>{{ uploadStatus.percentage }}%</span>
              </div>

              <el-progress
                  :percentage="uploadStatus.percentage"
                  :status="uploadStatus.percentage === 100 ? 'success' : ''"
                  :stroke-width="18"
                  text-inside
                  striped
                  striped-flow
                  :duration="10"
              />

              <div class="upload-metrics" v-if="uploadStatus.percentage < 100">
                <el-tag type="info" size="small" effect="plain">
                  <el-icon>
                    <Top/>
                  </el-icon>
                  {{ uploadStatus.speed }}
                </el-tag>
                <el-tag type="info" size="small" effect="plain">
                  <el-icon>
                    <Timer/>
                  </el-icon>
                  剩余 {{ uploadStatus.remainingTime }}
                </el-tag>
                <el-tag type="info" size="small" effect="plain">
                  {{ uploadStatus.uploadedSize }} / {{ uploadStatus.totalSize }}
                </el-tag>
              </div>
            </div>
          </transition>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Algorithm">
import {
  addAlgorithm,
  delAlgorithm,
  getAlgorithm,
  getUploadUrl,
  listAlgorithm, removeUploadFile, safeDelCheck,
  updateAlgorithm
} from "@/api/algorithm/algorithm"
import axios from "axios";
import {UploadFilled} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from 'element-plus'
import {Top, Timer} from "@element-plus/icons-vue";

const {proxy} = getCurrentInstance()

const algorithmList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const fileList = ref([])
const uploadRef = ref(null)
// 提交加载状态
const submitLoading = ref(false)
// 选中的文件对象
const selectedFile = ref(null)
const uploadProgress = ref(0)
const upLoadFlag = ref(false)
const tempObj = ref(null)
//下载状态
const isUploading = ref(false)
const uploadStatus = reactive({
  percentage: 0,
  speed: '0 KB/s',
  remainingTime: '--',
  uploadedSize: '0 MB',
  totalSize: '0 MB',
  text: '准备上传...'
})
// 用于控制中断请求
const uploadController = ref(null)

const data = reactive({
  form: {
    code: '',
    name: undefined,
    version: undefined,
    size: undefined,
    desc: undefined,
    objectName: null
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    code: null,
    version: null,
    desc: null,
    objectName: null,
  },
  rules: {
    code: [
      {required: true, message: "算法编码不能为空", trigger: "blur"}
    ],
    name: [
      {required: true, message: "算法名称不能为空", trigger: "blur"}
    ],
    version: [
      {required: true, message: "算法版本不能为空", trigger: "blur"}
    ]
  }
})

const {queryParams, form, rules} = toRefs(data)


// 辅助函数：格式化文件大小
const formatSize = (size) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024).toFixed(2) + ' MB'
  return (size / 1024 / 1024 / 1024).toFixed(2) + ' GB'
}

/** 查询算法列表 */
function getList() {
  loading.value = true
  listAlgorithm(queryParams.value).then(response => {
    algorithmList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 处理文件选择
const handleFileChange = (file) => {
  fileList.value = [file]
  selectedFile.value = file.raw
  form.value.size = (selectedFile.value.size / 1024 / 1024).toFixed(2)
}

const customUpload = async () => {
  if (!selectedFile.value) {
    proxy.$modal.warning('请先选择文件')
    return
  }

  isUploading.value = true
  uploadStatus.percentage = 0
  uploadStatus.text = '正在请求上传链接...'

  let url = ''
  try {
    const res = await getUploadUrl()
    url = res.data.url
    form.value.objectName = res.data.objectName
    tempObj.value = res.data.objectName
  } catch (e) {
    isUploading.value = false
    proxy.$modal.error('获取上传URL失败')
    return
  }

  uploadStatus.text = '正在上传...'

  // 记录开始时间和初始数据用于计算速度
  let startTime = Date.now()
  let lastLoaded = 0

  // 每次上传前，先实例化一个新的控制器
  uploadController.value = new AbortController()

  try {
    await axios.put(url, selectedFile.value, {
      headers: {
        'Content-Type': selectedFile.value.type || 'application/octet-stream'
      },
      timeout: 0,
      signal: uploadController.value.signal,
      onUploadProgress: (progressEvent) => {
        const currentTime = Date.now()
        const timeDiff = (currentTime - startTime) / 1000 // 秒

        // 计算百分比
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        uploadStatus.percentage = percent

        // 每 500ms 更新一次速度和剩余时间，避免界面闪烁
        if (timeDiff >= 0.5) {
          const loadedDiff = progressEvent.loaded - lastLoaded
          const speed = loadedDiff / timeDiff // bytes per second

          // 格式化速度
          uploadStatus.speed = formatSize(speed) + '/s'

          // 计算剩余时间
          const remainingBytes = progressEvent.total - progressEvent.loaded
          const remainingSeconds = speed > 0 ? remainingBytes / speed : 0

          if (remainingSeconds > 60) {
            uploadStatus.remainingTime = (remainingSeconds / 60).toFixed(0) + ' 分钟'
          } else {
            uploadStatus.remainingTime = remainingSeconds.toFixed(0) + ' 秒'
          }

          // 更新显示大小
          uploadStatus.uploadedSize = formatSize(progressEvent.loaded)
          uploadStatus.totalSize = formatSize(progressEvent.total)

          // 重置计数器
          startTime = currentTime
          lastLoaded = progressEvent.loaded
        }
      }
    })

    uploadStatus.percentage = 100
    uploadStatus.text = '上传完成'
    upLoadFlag.value = true
    proxy.$modal.success('文件上传成功')
  } catch (error) {
    if (axios.isCancel(error)) {
      ElMessage.warning('操作已取消');
      return; // 直接退出，不报错
    }

    const isNetworkError = error.code === 'ERR_NETWORK' || (!error.response && error.request);

    if (isNetworkError) {
      await ElMessageBox.alert(
          '检测到网络连接中断，无法连接到文件存储服务器。请检查您的网络设置或联系管理员。',
          '上传连接断开',
          {
            confirmButtonText: '知道了',
            type: 'error',
            draggable: true
          }
      )
    }
    // 3. 判断是否有服务端返回的错误 (例如 403 签名过期, 500 MinIO崩溃)
    else if (error.response) {
      const status = error.response.status;
      let msg = `上传失败 (错误码: ${status})`;

      if (status === 403) {
        msg = '上传链接已过期或权限不足，请重试';
      } else if (status === 500 || status === 502) {
        msg = '文件存储服务暂时不可用';
      } else if (status === 413) {
        msg = '文件体积过大，服务器拒绝接收';
      }

      proxy.$modal.error(msg);
    }
    // 4. 其他未知错误
    else {
      proxy.$modal.error('发生未知错误: ' + (error.message || '请重试'));
    }

    // 5. 错误发生后的状态清理 (非常重要，否则界面会卡在进度条上)
    uploadProgress.value = 0;
    uploadStatus.text = '上传失败';
    uploadStatus.percentage = 0;
  } finally {
    uploadController.value = null // 重置
    isUploading.value = false
  }
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

function onClose() {
  // 1. 如果正在上传中，强制中断网络请求
  if (uploadController.value) {
    uploadController.value.abort() // 这句代码会直接掐断 axios 连接
    uploadController.value = null
  }

  // 2. 如果已经上传完成（upLoadFlag为true），但用户没点"确定"保存，则删除远程文件
  if (upLoadFlag.value && tempObj.value) {
    removeUploadFile(tempObj.value)
  }
  // 重置状态
  upLoadFlag.value = false
  tempObj.value = null
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    code: null,
    version: null,
    desc: null,
    objectName: null
  }
  proxy.resetForm("algorithmRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加算法"
  uploadProgress.value = 0
  selectedFile.value = null
  fileList.value = []
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  uploadProgress.value = 0
  selectedFile.value = null
  fileList.value = []
  const _id = row.id || ids.value
  getAlgorithm(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改算法"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["algorithmRef"].validate(async valid => {
    if (valid) {
      if (form.value.id != null) {
        updateAlgorithm(form.value).then(async response => {
          proxy.$modal.msgSuccess("修改成功")
          upLoadFlag.value = false
          open.value = false
          getList()
        })
      } else {
        if (selectedFile.value != null) {
          addAlgorithm(form.value).then(async response => {
            proxy.$modal.msgSuccess("新增成功")
            upLoadFlag.value = false
            open.value = false
            getList()
          })
        } else {
          proxy.$modal.msgError("请上传算法")
        }
      }
    }
  })
}

/** 删除按钮操作 */
async function handleDelete(row) {
  const _ids = row.id || ids.value
  try {
    await safeDelCheck(_ids)
    proxy.$modal.confirm('是否确认删除该数据项？').then(function () {
      return delAlgorithm(_ids)
    }).then(() => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
    }).catch(() => {
    })
  } catch (e) {
  }
}

function handleDownload(row) {
  const link = document.createElement('a');
  link.href = row.url;
  link.download = '下载文件'; // 无自定义名称则用默认
  link.target = '_blank'; // 部分浏览器需加此属性
  document.body.appendChild(link);
  link.click(); // 触发下载
  document.body.removeChild(link); // 移除临时标签
}

getList()
</script>

<style scoped>
.upload-status-panel {
  margin-top: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border: 1px dashed #dcdfe6;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 13px;
  color: #606266;
}

.upload-metrics {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  justify-content: flex-start;
}
</style>
