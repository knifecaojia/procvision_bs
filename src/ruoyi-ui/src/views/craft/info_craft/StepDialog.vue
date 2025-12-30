<template>
  <div>
  <el-dialog title="工步信息" v-model="stepOpen" width="800px" @close="onClose">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            size="small"
            @click="handleAdd"
            v-hasPermi="['craft:step:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            size="small"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['craft:step:remove']"
        >删除</el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="stepList" height="600px" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编码" align="center" prop="code" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="内容" align="center" show-overflow-tooltip prop="content" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['craft:step:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['craft:step:remove']">删除</el-button>
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
  </el-dialog>

    <!-- 添加或修改工步信息对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="stepRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="form.content" placeholder="请输入内容"/>
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

<script setup name="Step">
import { listStep, getStep, delStep, addStep, updateStep } from "@/api/craft/step"

const { proxy } = getCurrentInstance()

const stepList = ref([])
const open = ref(false)
const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const stepOpen = defineModel()

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    code: null,
    name: null,
    content: null,
    processId: null,
  },
  rules: {}
})

const props = defineProps({
  stepOpen: Boolean,
  processId: Number
})

const { queryParams, form, rules } = toRefs(data)

/** 查询工步信息列表 */
function getList() {
  loading.value = true
  queryParams.value.processId = props.processId
  listStep(queryParams.value).then(response => {
    stepList.value = response.rows
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
    content: null,
    processId: null,
  }
  proxy.resetForm("stepRef")
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
  title.value = "添加工步信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getStep(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工步信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["stepRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateStep(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        form.value.processId = props.processId
        addStep(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除工步信息？').then(function() {
    return delStep(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function onClose(){
  stepOpen.value = false
}

watch(() => stepOpen.value, (value) => {
  if(value){
    getList()
  }
})

getList()
</script>
