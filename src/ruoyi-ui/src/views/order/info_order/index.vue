<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="98px">
<!--      <el-form-item label="项目号" prop="projectNo">-->
<!--        <el-input-->
<!--            v-model="queryParams.projectNo"-->
<!--            placeholder="请输入项目号"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="订单编码" prop="workOrderCode">
        <el-input
            v-model="queryParams.workOrderCode"
            placeholder="请输入订单编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="装配工人编码" prop="workerCode">
        <el-input
            v-model="queryParams.workerCode"
            placeholder="请输入装配工人编码"
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
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
        <el-col :span="1.5">
          <el-button
              type="warning"
              plain
              icon="upload"
              @click="handleImport"
          >导入
          </el-button>
        </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="装配任务编码" align="center" prop="workOrderCode"/>
      <el-table-column label="工艺编码" align="center" prop="craftCode"/>
      <el-table-column label="工艺版本" align="center" prop="craftVersion"/>
      <el-table-column label="资源状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === -1" size="small" type="info">未配置引导资源</el-tag>
          <el-tag v-else-if="scope.row.status === -2" size="small" type="warning">未配置检测资源</el-tag>
          <el-tag v-else size="small" type="success">已就绪</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工作状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="info">待派单</el-tag>
          <el-tag v-else-if="scope.row.status === 2" size="small" type="primary">进行中</el-tag>
          <el-tag v-else-if="scope.row.status === 3" size="small" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 4" size="small" type="success">手工通过</el-tag>
          <el-tag v-else size="small" type="danger">资源未就绪</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="计划开始时间" align="center" prop="startTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计划结束时间" align="center" prop="endTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务详情" align="center">
        <template #default="scope">
          <el-button type="primary" link icon="Pointer" plain @click="handleDetail(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250px" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-config-provider :message="config">
            <el-button link type="primary" icon="View" @click="handleResultShow(scope.row)">结果查看</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </el-config-provider>
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

    <!-- 添加或修改工单对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="workOrderRef" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单编码" prop="workOrderCode">
              <el-input v-model="form.workOrderCode" placeholder="请输入工单编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺编码" prop="craftCode">
              <el-input v-model="form.craftCode" placeholder="请输入工艺编码"/>
              <!--              <el-select v-model="form.craftCode" placeholder="请选择工艺编码">-->
              <!--                <el-option v-for="item in craftList" :key="item.craftCode" :label="item.craftCode" :value="item.craftCode">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺版本" prop="craftVersion">
              <el-input v-model="form.craftVersion" placeholder="请输入工艺版本"/>
              <!--              <el-select v-model="form.craftVersion" placeholder="请选择工艺版本">-->
              <!--                <el-option v-for="item in craftVersionList" :key="item.craftVersion" :label="item.craftVersion" :value="item.craftVersion">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序编码" prop="processCode">
              <el-input v-model="form.processCode" placeholder="请输入工序编码"/>
              <!--              <el-select v-model="form.processCode" placeholder="请选择工序编码">-->
              <!--                <el-option v-for="item in processList" :key="item.processCode" :label="item.processName" :value="item.processCode">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序名称" prop="processName">
              <el-input v-model="form.processName" placeholder="请输入工序名称"/>
              <!--              <el-select v-model="form.processName" placeholder="请选择工序名称" >-->
              <!--                <el-option v-for="item in processNameList" :key="item.processName" :label="item.processName" :value="item.processName">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人姓名" prop="workerName">
              <el-input v-model="form.workerName" placeholder="请输入装配工人姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人编码" prop="workerCode">
              <el-input v-model="form.workerCode" placeholder="请输入装配工人编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
          </el-col>
        </el-row>
        <el-form-item label="计划开始时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择计划开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="计划结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择计划结束时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="任务详情" v-model="isWorkOrderInfoOpen" width="580px">
      <el-descriptions size="large" :column="2" border>
            <el-descriptions-item label="工序编码">
              {{ workOrderInfo.processCode }}
            </el-descriptions-item>
            <el-descriptions-item label="工序名称">
              {{ workOrderInfo.processName }}
            </el-descriptions-item>
            <el-descriptions-item label="装配工人编码">
              {{ workOrderInfo.workerCode }}
            </el-descriptions-item>
            <el-descriptions-item label="装配工人姓名">
              {{ workOrderInfo.workerName }}
            </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="任务结果" v-model="isWorkOrderResult" width="600px">
      <el-scrollbar max-height="600px">
        <el-timeline v-if="stepRecordList.length > 0">
          <el-timeline-item
              v-for="(activity, index) in stepRecordList"
              :key="index"
              color="blue"
          >
            <el-descriptions :title="'工步' + activity.stepNo" border column="2" style="width: 500px">
              <el-descriptions-item label="工步名称" width="100px">
                {{ activity.stepName }}
              </el-descriptions-item>
              <el-descriptions-item label="图片" width="100px" span="2" :rowspan="2" align="center">
                <el-image :src="activity.imgUrl" :previewSrcList="[activity.imgUrl]" fit="cover" :preview-teleported="true" lazy style="width: 100px; height: 100px; border-radius: 4px;"/>
              </el-descriptions-item>
              <el-descriptions-item label="工步状态" width="100px">
                <el-tag v-if="activity.stepStatus === 2" type="success">已完成</el-tag>
                <el-tag v-else type="danger">未完成</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="当前任务没有工步结果"/>
      </el-scrollbar>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="WorkOrder">
import {listWorkOrder, getWorkOrder, delWorkOrder, addWorkOrder, updateWorkOrder} from "@/api/order/workOrder"
import {getRecordByTaskNo} from "@/api/process/record.js";
import {importFromMOM} from "@/api/craft/craft.js";
import {importOrderFromMOM} from "@/api/order/order.js";
import {UploadFilled} from "@element-plus/icons-vue";
import {getToken} from "@/utils/auth.js";

const {proxy} = getCurrentInstance()

const workOrderList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isWorkOrderInfoOpen = ref(false)
const isWorkOrderResult = ref(false)
const uploading = ref(false)
const stepRecordList = ref([])


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workOrderCode: null,
    workOrderQuantity: null,
    craftCode: null,
    craftVersion: null,
    status: null,
    processCode: null,
    processName: null,
    dispatchQuantity: null,
    startTime: null,
    endTime: null,
    guideMapUrl: null,
    workerCode: null,
    workerName: null,
    prodOrderNo: null,
    projectNo: null,
    materialNo: null,
    materialName: null
  },
  rules: {},
  workOrderInfo: {}
})

const config = reactive({
  max: 3,
  plain: true,
  placement: 'bottom',
})

/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/workOrder/importData"
})

/** 下载模板操作 */
function importTemplate() {
  proxy.download("workOrder/importTemplate", {
  }, `order_template_${new Date().getTime()}.xlsx`)
}

/** 提交上传文件 */
function submitFileForm() {
  const file = upload.selectedFile
  if (!file || file.length === 0 || !file.name.toLowerCase().endsWith('.xls') && !file.name.toLowerCase().endsWith('.xlsx')) {
    proxy.$modal.msgError("请选择后缀为 “xls”或“xlsx”的文件。")
    return
  }
  proxy.$refs["uploadRef"].submit()
}

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true
}

/** 文件选择处理 */
const handleFileChange = (file, fileList) => {
  upload.selectedFile = file
}

/** 文件删除处理 */
const handleFileRemove = (file, fileList) => {
  upload.selectedFile = null
}

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false
  upload.isUploading = false
  proxy.$refs["uploadRef"].handleRemove(file)
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
  getList()
}

const {queryParams, form, rules, workOrderInfo} = toRefs(data)

const beforeUpload = (rawFile) => {
  const isJSON = rawFile.type === 'application/json' || rawFile.name.endsWith('.json')
  const isLt5M = rawFile.size / 1024 / 1024 < 5;
  if (!isJSON) {
    proxy.$modal.msgError('上传文件只能是 JSON 格式!')
    return false
  }
  if (!isLt5M) {
    proxy.$modal.msgError('上传文件大小不能超过 5MB!')
    return false
  }
  return true
}

const customUploadRequest = async (options) => {
  const {file} = options

  const formData = new FormData()
  formData.append('file', file)

  uploading.value = true

  try {
    // 发送请求
    const response = await importOrderFromMOM(formData)
    // 成功处理
    proxy.$modal.msgSuccess('上传成功')
    getList()
  } catch (error) {
    proxy.$modal.msgError('上传失败')
  } finally {
    uploading.value = false
  }
}

function handleImport(){
  upload.title = "用户导入"
  upload.open = true
  upload.selectedFile = null
}
/** 查询工单列表 */
function getList() {
  loading.value = true
  listWorkOrder(queryParams.value).then(response => {
    workOrderList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

async function handleResultShow(row) {
  if (row.status >= 2) {
    stepRecordList.value = []
    await getRecordByTaskNo(row.workOrderCode, row.processCode).then(response => {
      isWorkOrderResult.value = true
      stepRecordList.value = response.data.stepInfo
    })
  } else
    proxy.$modal.msgWarning("任务未分配")
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    workOrderCode: null,
    workOrderQuantity: null,
    craftCode: null,
    craftVersion: null,
    status: null,
    processCode: null,
    processName: null,
    dispatchQuantity: null,
    startTime: null,
    endTime: null,
    guideMapUrl: null,
    workerCode: null,
    workerName: null,
    prodOrderNo: null,
    projectNo: null,
    materialNo: null,
    materialName: null
  }
  proxy.resetForm("workOrderRef")
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
  title.value = "添加工单"
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  await getWorkOrder(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工单"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["workOrderRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateWorkOrder(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addWorkOrder(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除工单？').then(function () {
    return delWorkOrder(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleDetail(row) {
  isWorkOrderInfoOpen.value = true
  workOrderInfo.value = row
}

getList()
</script>
