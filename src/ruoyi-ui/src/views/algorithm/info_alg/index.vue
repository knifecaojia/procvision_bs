<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="minio上的算法objectName" prop="objectName">
        <el-input
          v-model="queryParams.objectName"
          placeholder="请输入minio上的算法objectName"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createdTime">
        <el-date-picker clearable
          v-model="queryParams.createdTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="更新时间" prop="updatedTime">
        <el-date-picker clearable
          v-model="queryParams.updatedTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择更新时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="创建人" prop="createdBy">
        <el-input
          v-model="queryParams.createdBy"
          placeholder="请输入创建人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新人" prop="updatedBy">
        <el-input
          v-model="queryParams.updatedBy"
          placeholder="请输入更新人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remarks">
        <el-input
          v-model="queryParams.remarks"
          placeholder="请输入备注"
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
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['algorithm:algorithm:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['algorithm:algorithm:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['algorithm:algorithm:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="algorithmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="算法名称" align="center" prop="name" />
      <el-table-column label="算法版本" align="center" prop="version" />
      <el-table-column label="算法描述" align="center" prop="desc" />
      <el-table-column label="minio上的算法objectName" align="center" prop="objectName" />
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updatedTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updatedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createdBy" />
      <el-table-column label="更新人" align="center" prop="updatedBy" />
      <el-table-column label="备注" align="center" prop="remarks" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['algorithm:algorithm:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['algorithm:algorithm:remove']">删除</el-button>
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
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="algorithmRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="算法名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入算法名称" />
        </el-form-item>
        <el-form-item label="算法版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入算法版本" />
        </el-form-item>
        <el-form-item label="算法描述" prop="desc">
          <el-input v-model="form.desc" placeholder="请输入算法描述" />
        </el-form-item>
        <el-form-item label="minio上的算法objectName" prop="objectName">
          <el-input v-model="form.objectName" placeholder="请输入minio上的算法objectName" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdTime">
          <el-date-picker clearable
            v-model="form.createdTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="更新时间" prop="updatedTime">
          <el-date-picker clearable
            v-model="form.updatedTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择更新时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="创建人" prop="createdBy">
          <el-input v-model="form.createdBy" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="更新人" prop="updatedBy">
          <el-input v-model="form.updatedBy" placeholder="请输入更新人" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
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
import { listAlgorithm, getAlgorithm, delAlgorithm, addAlgorithm, updateAlgorithm } from "@/api/algorithm/algorithm"

const { proxy } = getCurrentInstance()

const algorithmList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    version: null,
    desc: null,
    objectName: null,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询算法列表 */
function getList() {
  loading.value = true
  listAlgorithm(queryParams.value).then(response => {
    algorithmList.value = response.rows
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
    name: null,
    version: null,
    desc: null,
    objectName: null,
    createdTime: null,
    updatedTime: null,
    createdBy: null,
    updatedBy: null,
    remarks: null
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
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除算法编号为"' + _ids + '"的数据项？').then(function() {
    return delAlgorithm(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('algorithm/algorithm/export', {
    ...queryParams.value
  }, `algorithm_${new Date().getTime()}.xlsx`)
}

getList()
</script>
