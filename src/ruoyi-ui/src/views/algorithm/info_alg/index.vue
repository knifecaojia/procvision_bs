<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="算法编码" prop="code">
        <el-input
            v-model="queryParams.code"
            placeholder="请输入算法名称"
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
      <el-form-item label="算法描述" prop="desc">
        <el-input
            v-model="queryParams.desc"
            placeholder="请输入算法描述"
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
            v-hasPermi="['algorithm:algorithm:add']"
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
            v-hasPermi="['algorithm:algorithm:edit']"
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
            v-hasPermi="['algorithm:algorithm:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="algorithmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="算法编码" align="center" prop="code"/>
      <el-table-column label="算法名称" align="center" prop="name"/>
      <el-table-column label="算法版本" align="center" prop="version"/>
      <el-table-column label="算法描述" align="center" prop="desc"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['algorithm:algorithm:edit']">修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['algorithm:algorithm:remove']">删除
          </el-button>
          <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)"
                     v-hasPermi="['algorithm:algorithm:remove']">下载
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
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="algorithmRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="算法编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入算法编码"/>
        </el-form-item>
        <el-form-item label="算法名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入算法名称"/>
        </el-form-item>
        <el-form-item label="算法版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入算法版本"/>
        </el-form-item>
        <el-form-item label="算法描述" prop="desc">
          <el-input v-model="form.desc" placeholder="请输入算法描述"/>
        </el-form-item>
        <el-form-item label="算法上传" prop="file">
          <el-upload ref="file" :file-list="fileList" :auto-upload="false"
                     :before-upload="fileBeforeUpload" @change="handleFileChange">
            <el-button plain size="small" type="primary" >点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Algorithm">
import {
  listAlgorithm,
  getAlgorithm,
  delAlgorithm,
  addAlgorithm,
  updateAlgorithm
} from "@/api/algorithm/algorithm"

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

const data = reactive({
  form: {
    code: '',
    name: undefined,
    version: undefined,
    desc: undefined,
    file: null,
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
    ],
    desc: [
      {required: true, message: "算法描述不能为空", trigger: "blur"}
    ],
  }
})

const {queryParams, form, rules} = toRefs(data)

/** 查询算法列表 */
function getList() {
  loading.value = true
  listAlgorithm(queryParams.value).then(response => {
    algorithmList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function fileBeforeUpload(file) {
  let isRightSize = file.size / 1024 / 1024 < 2
  if (!isRightSize) {
    proxy.$modal.msgError('文件大小超过 2MB')
  }
  return isRightSize
}

function handleFileChange(file, fileList) {
  // 将选中的文件赋值给 formData.file（取最后一个选中的文件）
  form.value.file = fileList.length > 0 ? fileList[fileList.length - 1].raw : null;
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
    name: null,
    code: null,
    version: null,
    desc: null,
    file: null
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
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getAlgorithm(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改算法"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["algorithmRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateAlgorithm(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addAlgorithm(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          fileList.value = []
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除算法编号为"' + _ids + '"的数据项？').then(function () {
    return delAlgorithm(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
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
