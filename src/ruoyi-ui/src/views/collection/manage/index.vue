<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="产品名称" prop="data">
        <el-input
            v-model="queryParams.data"
            placeholder="请输入产品名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据集" prop="datasetId">
        <el-select v-model="queryParams.datasetId" placeholder="请选择数据集" clearable style="width: 200px">
          <el-option label="未归属" :value="0" />
          <el-option
              v-for="item in datasetList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="产品型号" prop="productModel"><el-input v-model="queryParams.productModel" maxlength="100" placeholder="请输入产品型号" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="产品批次" prop="productBatch"><el-input v-model="queryParams.productBatch" maxlength="100" placeholder="请输入产品批次" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="工序代号" prop="processNum"><el-input v-model="queryParams.processNum" maxlength="100" placeholder="请输入工序代号" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="生产日期">
        <el-date-picker v-model="productTimeRange" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :default-time="[new Date(2000, 0, 1, 0, 0, 0), new Date(2000, 0, 1, 23, 59, 59)]"
          start-placeholder="开始日期" end-placeholder="结束日期" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['collection:data:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['collection:data:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Picture" :disabled="single || storageBusy || storageLoading"
          @click="handleStorage" v-hasPermi="['collection:data:edit']">图像存储设置</el-button>
      </el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="Download" :disabled="multiple || loading || exporting"
        :loading="exporting" @click="handleExportImages" v-hasPermi="['collection:data:export']">导出所选图片（{{ ids.length }}）</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="dataTableRef" row-key="id" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
      <el-table-column label="数据" align="center" prop="data" />
      <el-table-column label="图片" align="center">
        <template #default="scope">
          <el-image
              v-if="scope.row.imagePath"
              :src="scope.row.imagePath[1] || scope.row.imagePath[0]"
              :preview-src-list="[scope.row.imagePath[0]]"
              :preview-teleported="true"
              fit="cover"
              lazy
              style="width: 60px; height: 60px; border-radius: 4px;"
          >
            <template #error>
              <div class="image-placeholder">失效/损坏</div>
            </template>
          </el-image>
          <div v-else class="image-placeholder">
            暂无图片
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数据集名称" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.datasetId" type="success">{{ scope.row.datasetName }}</el-tag>
          <el-tag v-else type="info">{{ "未归属" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="标注图片" align="center">
        <template #default="scope">
          <el-image
              v-if="scope.row.labelImage"
              :src="scope.row.labelImage"
              :preview-src-list="[scope.row.labelImage]"
              :preview-teleported="true"
              fit="cover"
              lazy
              style="width: 60px; height: 60px; border-radius: 4px;"
          >
            <template #error>
              <div class="image-placeholder">失效/损坏</div>
            </template>
          </el-image>
          <div v-else class="image-placeholder">
            暂无图片
          </div>
        </template>
      </el-table-column>
      <el-table-column label="生产日期" prop="productTime" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="产品型号" prop="productModel" align="center" min-width="110" show-overflow-tooltip />
      <el-table-column label="产品批次" prop="productBatch" align="center" min-width="110" show-overflow-tooltip />
      <el-table-column label="工序代号" prop="processNum" align="center" min-width="110" show-overflow-tooltip />
<!--      <el-table-column label="其他信息" prop="otherInfo" align="center" min-width="110" show-overflow-tooltip />-->
<!--      <el-table-column label="创建时间" align="center" prop="createTime" />-->
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button link type="primary" icon="Picture" :disabled="storageBusy || storageLoading"
            @click="handleStorage(scope.row)" v-hasPermi="['collection:data:edit']">存储设置</el-button>
          <el-button link type="primary" icon="Pointer" @click="handleLabel(scope.row)">标注</el-button>
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

    <!-- 添加或修改数据采集对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dataRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="data">
          <el-input v-model="form.data" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="数据集" >
          <el-select v-model="form.datasetId" placeholder="请选择数据集" clearable>
            <el-option
                v-for="item in datasetList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产品型号" prop="productModel">
          <el-input v-model="form.productModel" placeholder="请输入产品型号" />
        </el-form-item>
        <el-form-item label="产品批次" prop="productBatch">
          <el-input v-model="form.productBatch" placeholder="请输入产品批次" />
        </el-form-item>
        <el-form-item label="工序代号" prop="processNum">
          <el-input v-model="form.processNum" placeholder="请输入工序代号" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productTime">
          <el-date-picker v-model="form.productTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择生产日期" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="图像存储设置" v-model="storageOpen" width="560px" append-to-body
      :close-on-click-modal="!storageBusy" :close-on-press-escape="!storageBusy" :show-close="!storageBusy">
      <div v-loading="storageLoading">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="产品名称">{{ storageInfo.data || '—' }}</el-descriptions-item>
          <el-descriptions-item label="当前文件类型">{{ storageInfo.contentType || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="当前原图大小">{{ formatBytes(storageInfo.bytes) }}</el-descriptions-item>
        </el-descriptions>
        <el-form label-width="100px" style="margin-top: 20px" :disabled="storageBusy || storageLoading">
          <el-form-item label="目标格式">
            <el-select v-model="storageForm.format" style="width: 100%">
              <el-option label="JPEG（有损压缩）" value="jpeg" />
              <el-option label="PNG（无损存储）" value="png" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="storageForm.format === 'jpeg'" label="目标质量">
            <el-slider v-model="storageForm.quality" :min="1" :max="100" show-input />
          </el-form-item>
        </el-form>
        <el-alert :closable="false" type="info" show-icon
          :title="storageForm.format === 'jpeg' ? 'JPEG质量越高通常越清晰、文件越大；透明区域转为白色。重复有损压缩可能降低画质。' : 'PNG保持当前像素无损，不设置有损质量；转换为PNG不代表文件一定更小。'" />
        <p class="storage-note">大图处理可能需要数分钟，请等待完成。</p>
      </div>
      <template #footer>
        <el-button :disabled="storageBusy" @click="storageOpen = false">取消</el-button>
        <el-button type="primary" :loading="storageBusy" :disabled="storageLoading" @click="submitStorage">转换并保存</el-button>
      </template>
    </el-dialog>

    <DataLabelDialog
        v-model="labelVisible"
        :row-data="currentRow"
        @success="handleLabelSuccess"
    />
  </div>
</template>

<script setup name="Data">
import { getData, delData, addData, updateData } from "@/api/collection/data"
import { saveAs } from "file-saver"
import { exportManagedImages, listManagedData, getManagedStorage, convertManagedStorage } from "@/api/collection/manage"
import DataLabelDialog from "@/views/collection/manage/DataLabelDialog.vue";
import {listDataset} from "@/api/collection/dataset.js";

const { proxy } = getCurrentInstance()

const dataList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const datasetList = ref([])
const productTimeRange = ref([])
const dataTableRef = ref(null)
const exporting = ref(false)
const appliedExportQuery = ref({})
let appliedQuerySignature = ""
const storageOpen = ref(false)
const storageLoading = ref(false)
const storageBusy = ref(false)
const storageInfo = ref({})
const storageForm = reactive({ id: null, format: 'jpeg', quality: 85 })
let listRequestVersion = 0

const labelVisible = ref(false)
const currentRow = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    imagePath: null,
    data: null,
    datasetId: null,
    productModel: null,
    productBatch: null,
    processNum: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

onMounted(() => {
  getDatasetList()
})

async function getDatasetList() {
  try {
    const res = await listDataset()
    datasetList.value = res.rows || []
  } catch (error) { datasetList.value = [] } finally { getList() }
}

/** 独立type=1列表：生产时间范围与产品、数据集条件组合查询。 */
async function getList() {
  const version = ++listRequestVersion
  loading.value = true
  const dates = productTimeRange.value || []
  try {
    const params = { ...queryParams.value, beginProductTime: dates[0] || undefined, endProductTime: dates[1] || undefined }
    const { pageNum, pageSize, imagePath, ...filters } = params
    const response = await listManagedData(params)
    if (version !== listRequestVersion) return
    const signature = JSON.stringify(filters)
    if (signature !== appliedQuerySignature) { dataTableRef.value?.clearSelection(); ids.value = [] }
    appliedQuerySignature = signature
    appliedExportQuery.value = filters
    dataList.value = (response.rows || []).map(item => ({ ...item,
      imagePath: typeof item.imagePath === 'string' ? item.imagePath.split(',') : item.imagePath,
      datasetName: datasetList.value.find(i => i.id === item.datasetId)?.name
    }))
    total.value = response.total
  } catch (error) {
    if (version === listRequestVersion) { dataList.value = []; total.value = 0 }
  } finally { if (version === listRequestVersion) loading.value = false }
}

async function handleExportImages() {
  if (exporting.value || loading.value || !ids.value.length) return
  if (ids.value.length > 100) return proxy.$modal.msgWarning('单次最多导出100条，请分批选择')
  exporting.value = true
  try {
    const blob = await exportManagedImages({ ids: [...ids.value], query: { ...appliedExportQuery.value } })
    if (blob.type.includes('json')) {
      const error = JSON.parse(await blob.text())
      throw new Error(error.msg || '导出失败')
    }
    if (!blob.size) throw new Error('导出文件为空')
    saveAs(blob, `采集图像_${Date.now()}.zip`)
    proxy.$modal.msgSuccess('所选图片已导出，目录按产品型号、产品批次和工序代号分类')
  } catch (error) { proxy.$modal.msgError(error.message || '导出失败，请重试') }
  finally { exporting.value = false }
}

function formatBytes(bytes) {
  if (bytes == null) return '—'
  if (bytes >= 1024 * 1024) return (bytes / 1024 / 1024).toFixed(2) + ' MB'
  return (bytes / 1024).toFixed(2) + ' KB'
}

async function handleStorage(row) {
  if (storageBusy.value || storageLoading.value) return
  const id = row?.id || ids.value[0]
  if (!id) return
  storageInfo.value = {}
  Object.assign(storageForm, { id, format: 'jpeg', quality: 85 })
  storageOpen.value = true
  storageLoading.value = true
  try {
    const response = await getManagedStorage(id)
    storageInfo.value = response.data
  } catch (error) { storageOpen.value = false }
  finally { storageLoading.value = false }
}

async function submitStorage() {
  if (storageBusy.value || storageLoading.value) return
  storageBusy.value = true
  try {
    const response = await convertManagedStorage({ id: storageForm.id, format: storageForm.format,
      quality: storageForm.format === 'jpeg' ? storageForm.quality : null })
    proxy.$modal.msgSuccess(`转换成功，原图 ${formatBytes(response.data.beforeBytes)} → ${formatBytes(response.data.afterBytes)}`)
    storageOpen.value = false
    await getList()
  } catch (error) {
    // 接口错误由公共请求处理器提示，保留设置以便重试。
  } finally { storageBusy.value = false }
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    imagePath: null,
    data: null,
    createTime: null,
    remark: null
  }
  proxy.resetForm("dataRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  dataTableRef.value?.clearSelection()
  ids.value = []
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value.datasetId = null
  queryParams.value.data = null
  queryParams.value.productModel = null
  queryParams.value.productBatch = null
  queryParams.value.processNum = null
  productTimeRange.value = []
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getData(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改数据"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dataRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        // 只更新本对话框编辑的字段，防止旧imagePath覆盖正在完成的图像转换。
        updateData({
          id: form.value.id,
          data: form.value.data,
          datasetId: form.value.datasetId,
          productModel: form.value.productModel,
          productBatch: form.value.productBatch,
          processNum: form.value.processNum,
          productTime: form.value.productTime,
          remark: form.value.remark
        }).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addData(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除该数据项？').then(function() {
    return delData(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleLabel(row) {
  if (!row.imagePath || row.imagePath.length === 0) {
    proxy.$modal.msgWarning("该数据暂无图片，无法标注")
    return
  }
  currentRow.value = row
  labelVisible.value = true
}

/** 新增：标注成功后的回调 */
function handleLabelSuccess() {
  labelVisible.value = false
  getList() // 重新加载列表
}

// getList()
</script>

<style scoped>
.storage-note { font-size: 13px; line-height: 1.7; color: #909399; }
.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  color: #a8abb2;
  font-size: 13px;
  border-radius: 4px;
  margin: 0 auto;
}
</style>
