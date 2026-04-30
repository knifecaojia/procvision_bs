<template>
  <div class="app-container">
    <div v-if="isPageAlive">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="工艺编码" prop="code">
          <el-input
              v-model="queryParams.code"
              placeholder="请输入编码"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="工艺名称" prop="name">
          <el-input
              v-model="queryParams.name"
              placeholder="请输入名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="工艺版本" prop="version">
          <el-input
              v-model="queryParams.version"
              placeholder="请输入版本"
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
              type="warning"
              plain
              icon="upload"
              @click="handleImport"
          >导入
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

      <el-table v-loading="loading" :data="craftList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="生产订单号" align="center", prop="productionOrderNo"/>
        <el-table-column label="编码" align="center" prop="code"/>
        <el-table-column label="名称" align="center" prop="name"/>
        <el-table-column label="状态" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" size="small" type="info">待处理</el-tag>
            <el-tag v-if="scope.row.status === 2" size="small" type="danger">未配置引导信息</el-tag>
            <el-tag v-if="scope.row.status === 3" size="small" type="danger">未配置检测算法</el-tag>
            <el-tag v-if="scope.row.status === 4" size="small" type="success">已就绪</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="版本" align="center" prop="version"/>
        <el-table-column label="说明" align="center" prop="desc"/>
        <el-table-column label="详细" align="center" prop="">
          <template #default="scope">
            <el-button link icon="view" type="primary" @click="showProcess(scope.row)">查看工序</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">
              修改
            </el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除
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

      <!-- 添加或修改工艺信息对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
        <el-form ref="craftRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="编码" prop="code">
            <el-input v-model="form.code" placeholder="请输入编码"/>
          </el-form-item>
          <el-form-item label="名称" prop="code">
            <el-input v-model="form.name" placeholder="请输入名称"/>
          </el-form-item>
          <el-form-item label="版本" prop="version">
            <el-input v-model="form.version" placeholder="请输入版本"/>
          </el-form-item>
          <el-form-item label="说明" prop="desc">
            <el-input v-model="form.desc" type="textarea" placeholder="请输入内容"/>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>

      <ProcessDialog v-model="processOpen" :processOpen="processOpen" :craftId="tempCraftId" :tempCraftType="tempCraftType"
                     @check-status="checkStatus"/>

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
  </div>
</template>

<script setup name="Craft">
import {listCraft, getCraft, delCraft, addCraft, updateCraft, importFromMOM} from "@/api/craft/craft.js"
import ProcessDialog from "@/views/craft/info_craft/ProcessDialog.vue";
import {getToken} from "@/utils/auth.js";
import {UploadFilled} from "@element-plus/icons-vue";

const {proxy} = getCurrentInstance()

const craftList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const tempCraftId = ref(null)
const tempCraftType = ref(null)
const isPageAlive = ref(true)
const uploading = ref(false)

const data = reactive({
  processOpen: false,
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    code: null,
    name: null,
    version: null,
    desc: null
  },
  rules: {
    code: [
      {required: true, message: "编码不能为空", trigger: "blur"}
    ],
    name: [
      {required: true, message: "名称不能为空", trigger: "blur"}
    ],
    version: [
      {required: true, message: "版本不能为空", trigger: "blur"}
    ]
  }
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
  url: import.meta.env.VITE_APP_BASE_API + "/craft/info/importData"
})

/** 下载模板操作 */
function importTemplate() {
  proxy.download("craft/info/importTemplate", {
  }, `craft_template_${new Date().getTime()}.xlsx`)
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

const {processOpen, queryParams, form, rules} = toRefs(data)

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
    const response = await importFromMOM(formData)
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

/** 查询工艺信息列表 */
function getList() {
  loading.value = true
  listCraft(queryParams.value).then(response => {
    craftList.value = response.rows
    total.value = response.total
    loading.value = false
  })

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
    code: null,
    name: null,
    version: null,
    desc: null
  }
  proxy.resetForm("craftRef")
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
  title.value = "添加工艺信息"
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  await getCraft(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工艺信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["craftRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateCraft(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCraft(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除工艺信息？').then(function () {
    return delCraft(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function showProcess(row) {
  processOpen.value = true
  tempCraftId.value = row.id
  tempCraftType.value = row.code.includes("TX") ? "TX" : "BJ"
}

function checkStatus() {
  processOpen.value = false
  craftList.value = []
  getList()
}

getList()
</script>
