<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="产品名称" prop="imagePath">
        <el-input
            v-model="queryParams.data"
            placeholder="请输入产品名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据集">
        <el-select v-model="queryParams.datasetId" placeholder="请选择数据集" clearable>
          <el-option label="未归属" value=0 />
          <el-option
              v-for="item in datasetList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据" align="center" prop="data" />
      <el-table-column label="图片" align="center">
        <template #default="scope">
          <el-image
              v-if="scope.row.imagePath"
              :src="scope.row.imagePath[1]"
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
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['collection:data:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['collection:data:remove']">删除</el-button>
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

    <DataLabelDialog
        v-model="labelVisible"
        :row-data="currentRow"
        @success="handleLabelSuccess"
    />
  </div>
</template>

<script setup name="Data">
import { listData, getData, delData, addData, updateData } from "@/api/collection/data"
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

const labelVisible = ref(false)
const currentRow = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    imagePath: null,
    data: null,
    datasetId: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

onMounted(() => {
  getDatasetList()
})

async function getDatasetList() {
  await listDataset().then(res => {
    datasetList.value = res.rows
    getList()
  })
}

/** 查询数据采集列表 */
function getList() {
  loading.value = true
  listData(queryParams.value).then(response => {
    dataList.value = response.rows
    dataList.value.forEach(item => {
      if (item.imagePath && typeof item.imagePath === 'string') {
        item.imagePath = item.imagePath.split(',')
      }
      item.datasetName = datasetList.value.find(i => i.id === item.datasetId)?.name
    })
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
    imagePath: null,
    data: null,
    createTime: null,
    remark: null
  }
  proxy.resetForm("dataRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value.datasetId = null
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
        updateData(form.value).then(response => {
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
