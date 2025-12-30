<template>
  <div>
    <el-dialog title="工序信息" v-model="processOpen" width="900px" @close="onClose">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
              size="small"
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="['process:process:add']"
          >新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              size="small"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['process:process:remove']"
          >删除
          </el-button>
        </el-col>
      </el-row>
      <el-table :data="processList" v-loading="processLoading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="工序号" align="center" prop="code"/>
        <el-table-column label="工序名称" align="center" prop="name"/>
        <el-table-column label="说明" align="center" prop="desc"/>
        <el-table-column label="查看引导图" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="showStep(scope.row)">查看引导图</el-button>
          </template>
        </el-table-column>
        <el-table-column label="详细" align="center" prop="">
          <template #default="scope">
            <el-button link type="primary" @click="showStep(scope.row)">查看工步</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleProcessUpdate(scope.row)" v-hasPermi="['process:process:edit']">
              修改
            </el-button>
            <el-button link type="primary" icon="Delete" @click="handleProcessDelete(scope.row)"
                       v-hasPermi="['craft:craft:remove']">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
          v-show="processTotal>0"
          :total="processTotal"
          v-model:page="processPageParms.pageNum"
          v-model:limit="processPageParms.pageSize"
          @pagination="getProcessList"
      />
    </el-dialog>

    <StepDialog v-model="stepOpen" :stepOpen="stepOpen" :processId="tempProcessId"></StepDialog>

    <!-- 添加或修改工序信息对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="processRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码"/>
        </el-form-item>
        <el-form-item label="名称" prop="code">
          <el-input v-model="form.name" placeholder="请输入名称"/>
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

  </div>
</template>

<script setup name="Process">
import {addProcess, delProcess, getProcess, listProcess, updateProcess} from "@/api/craft/process.js";
import StepDialog from "@/views/craft/info_craft/StepDialog.vue";

const {proxy} = getCurrentInstance()

const processLoading = ref(true)
const processList = ref([])
const processTotal = ref(0)
const processOpen = defineModel()
const title = ref('')
const open = ref(false)
const multiple = ref(true)
const ids = ref([])
const single = ref(true)
const tempProcessId = ref(null)

const data = reactive({
  stepOpen: false,
  processPageParms: {
    pageNum: 1,
    pageSize: 10,
    craftId: null
  },
  form: {},
  rules:{
    code: [
      {required: true, message: "编码不能为空", trigger: "blur"}
    ],
    name: [
      {required: true, message: "名称不能为空", trigger: "blur"}
    ]
  }
})

const {stepOpen, processPageParms, form, rules} = toRefs(data)

const props = defineProps({
  craftId: {
    type: Number,
    default: null
  },
  processOpen: {
    type: Boolean,
    default: false
  }
})

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加工艺信息"
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除工序信息？').then(function () {
    return delProcess(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function reset() {
  form.value = {
    id: null,
    code: null,
    name: null,
    desc: null
  }
  proxy.resetForm("craftRef")
}

function onClose(){
  processOpen.value = false
}

function getProcessList() {
  processLoading.value = true
  processPageParms.value.craftId = props.craftId
  listProcess(processPageParms.value).then(response => {
    processList.value = response.rows
    processTotal.value = response.total
    processLoading.value = false
  })
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleProcessUpdate(row){
  reset()
  open.value = true
  const _id = row.id || ids.value
  getProcess(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工序信息"
  })
}

function submitForm() {
  proxy.$refs["processRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProcess(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getProcessList()
        })
      } else {
        form.value.craftId = props.craftId
        addProcess(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getProcessList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleProcessDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除工艺信息？').then(function () {
    return delProcess(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function showStep(row) {
  stepOpen.value =  true
  tempProcessId.value = row.id
}

function cancel() {
  open.value = false
  reset()
}

watch(() => props.processOpen, (val) => {
  if (val) {
    getProcessList()
  }
})

</script>