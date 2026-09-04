<template>
  <div>
    <el-dialog title="工序信息" v-model="processOpen" width="1000px" @close="onClose">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
              size="small"
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
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
          >删除
          </el-button>
        </el-col>
      </el-row>
      <el-table :data="processList" v-loading="processLoading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="工序号" align="center" prop="code"/>
        <el-table-column label="工序名称" align="center" prop="name"/>
        <el-table-column label="算法" align="center" :show-overflow-tooltip="true">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.algorithmId == null">未绑定</el-tag>
            <el-tag v-else type="success">
              {{ algList.find(item => item.id === scope.row.algorithmId).name + ' -- ' + algList.find(item => item.id === scope.row.algorithmId).version}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="详细" align="center" prop="">
          <template #default="scope">
            <el-button link icon="view" type="primary" @click="showStep(scope.row)">查看工步</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300px">
          <template #default="scope">
            <el-button link type="primary" icon="link" @click="showBindAlg(scope.row)">
              绑定算法
            </el-button>
            <el-button link type="primary" icon="Edit" @click="handleProcessUpdate(scope.row)">
              查看详情
            </el-button>
            <el-button link type="primary" icon="Delete" @click="handleProcessDelete(scope.row)">
              删除
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

    <StepDialog v-model="stepOpen" :stepOpen="stepOpen" :tempAlgType="selectedAlgType" :processId="tempProcessId" :craftId="props.craftId" :tempCraftType="props.tempCraftType"></StepDialog>

    <el-dialog :title="title" v-model="open" width="850px" append-to-body>
      <el-form ref="processRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="说明" prop="desc">
          <el-input v-model="form.desc" type="textarea" placeholder="请输入内容"/>
        </el-form-item>

        <el-divider content-position="left">物料信息</el-divider>
        <el-row class="mb8">
          <el-button type="primary" icon="Plus" size="small" @click="handleAddMaterial">添加物料</el-button>
        </el-row>
        <el-table :data="materialList" border size="small" style="width: 100%; margin-bottom: 20px;">
          <el-table-column label="物料号" align="center" width="140">
            <template #default="scope">
              <el-input v-model="scope.row.materialNo" placeholder="请输入物料号" />
            </template>
          </el-table-column>
          <el-table-column label="物料名称" align="center" width="160">
            <template #default="scope">
              <el-input v-model="scope.row.materialName" placeholder="请输入物料名称" />
            </template>
          </el-table-column>
          <el-table-column label="数量" align="center" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.materialQuantity" :min="0" :controls="false" style="width: 100%" placeholder="数量" />
            </template>
          </el-table-column>
          <el-table-column label="单位" align="center" width="100">
            <template #default="scope">
              <el-input v-model="scope.row.materialUnit" placeholder="如: 件" />
            </template>
          </el-table-column>
          <el-table-column label="防错标识" align="center">
            <template #default="scope">
              <el-input v-model="scope.row.errorPreventionMark" placeholder="请输入防错标识" />
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="80" fixed="right">
            <template #default="scope">
              <el-button type="danger" icon="Delete" link @click="handleDeleteMaterial(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="绑定算法" v-model="bindAlgShow" width="500px" append-to-body>
      <el-select clearable v-model="selectedAlgId" style="width: 100%">
        <el-option v-for="item in algList" :key="item.id" :label="item.name + ' —— Ver：' + item.version" :value="item.id">
        </el-option>
      </el-select>
      <div style="margin-top: 20px; display: flex; justify-content: center">
        <el-button type="primary" @click="handleBind">确 定</el-button>
      </div>
    </el-dialog>

    <LabelDialog v-model="labelVisible" :visible="labelVisible" :stepIds="targetStepIds"
                 :tempCraftType="props.tempCraftType"/>

  </div>
</template>

<script setup name="Process">
import { ref, reactive, toRefs, watch, onMounted, getCurrentInstance } from 'vue'
import {
  addProcess,
  bindProcessAlgorithm, changeExceptionCheck, changeFinalCheckCheck,
  delProcess,
  getProcess,
  listProcess,
  updateProcess
} from "@/api/craft/process.js";
import StepDialog from "@/views/craft/info_craft/StepDialog.vue";
import {getAllAlg, listAlgorithm} from "@/api/algorithm/algorithm.js";
import {changeStatus} from "@/api/craft/craft.js";
import LabelDialog from "@/views/craft/info_craft/LabelDialog.vue";

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
const bindAlgShow = ref(false)
const selectedAlgId = ref(null)
const labelVisible = ref(false)
const materialOpen = ref(false)
const materialInfoList = ref([])
const selectedAlgType = ref(null)

// 新增：用于传递给标注组件的实际操作 ID 数组
const targetStepIds = ref([])

// 新增：物料列表的数据源
const materialList = ref([])

const data = reactive({
  algList: [],
  stepOpen: false,
  processPageParms: {
    pageNum: 1,
    pageSize: 10,
    craftId: null
  },
  form: {},
  rules: {
    code: [
      {required: true, message: "编码不能为空", trigger: "blur"}
    ],
    name: [
      {required: true, message: "名称不能为空", trigger: "blur"}
    ]
  }
})

const {stepOpen, processPageParms, form, rules, algList} = toRefs(data)

const props = defineProps({
  craftId: {
    type: Number,
    default: null
  },
  processOpen: {
    type: Boolean,
    default: false
  },
  tempCraftType: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['check-status'])

/** 新增物料行 */
function handleAddMaterial() {
  materialList.value.push({
    materialNo: undefined,
    materialName: undefined,
    materialQuantity: undefined,
    materialUnit: undefined,
    errorPreventionMark: undefined
  })
}

/** 删除物料行 */
function handleDeleteMaterial(index) {
  materialList.value.splice(index, 1)
}

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
    getProcessList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function reset() {
  form.value = {
    id: null,
    code: null,
    name: null,
    processMaterialInfo: null,
    desc: null
  }
  materialList.value = [] // 重置时清空物料列表
  // 修正：表单的ref是 processRef，原代码写成了 craftRef 会导致报错或无法重置
  proxy.resetForm("processRef")
}

function onClose() {
  processList.value = []
  emit('check-status')
}

function getProcessList() {
  processLoading.value = true
  processPageParms.value.craftId = props.craftId
  listProcess(processPageParms.value).then(response => {
    processList.value = response.rows
    processList.value.forEach(item => {
      item.processMaterialInfo = item.processMaterialInfo ? JSON.parse(item.processMaterialInfo) : []
    })
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

function handleProcessUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getProcess(_id).then(response => {
    form.value = response.data
    // 【核心】回显时：将 JSON 字符串还原成数组
    if (form.value.processMaterialInfo) {
      try {
        materialList.value = JSON.parse(form.value.processMaterialInfo)
      } catch (e) {
        console.error("物料信息解析失败", e)
        materialList.value = []
      }
    }
    open.value = true
    title.value = "修改工序信息"
  })
}

function submitForm() {
  proxy.$refs["processRef"].validate(valid => {
    if (valid) {
      form.value.processMaterialInfo = materialList.value.length > 0
          ? JSON.stringify(materialList.value)
          : null;
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
          changeStatus(props.craftId)
          open.value = false
          getProcessList()
        })
      }
    }
  })
}

/** 内部列表单独操作的删除 */
function handleProcessDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除工序信息？').then(function () {
    return delProcess(_ids)
  }).then(() => {
    getProcessList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function getAlgList() {
  getAllAlg().then(response => {
    algList.value = response.rows
  })
}

async function showBindAlg(row) {
  bindAlgShow.value = true
  selectedAlgId.value = null
  tempProcessId.value = row.id
}

async function handleBind() {
  if (selectedAlgId.value) {
    await bindProcessAlgorithm(tempProcessId.value, selectedAlgId.value).then(response => {
      proxy.$modal.msgSuccess("绑定成功")
      bindAlgShow.value = false
    })
    await changeStatus(props.craftId)
    getProcessList()
  } else {
    proxy.$modal.msgError("请选择要绑定的算法")
  }
}

function showStep(row) {
  stepOpen.value = true
  tempProcessId.value = row.id
  selectedAlgType.value = algList.value.find(item => item.id === row?.algorithmId).type
}

function cancel() {
  open.value = false
  reset()
}

function handleExceptionCheckChange(row) {
  let text = row.exceptionCheck === "0" ? "启用" : "取消"
  proxy.$modal.confirm('确认要' + text + '异物检测吗?').then(function () {
    return changeExceptionCheck(row.id, row.exceptionCheck)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(function () {
    row.exceptionCheck = row.exceptionCheck === "0" ? "1" : "0"
  })
}

watch(() => props.processOpen, (val) => {
  if (val) {
    proxy.$nextTick().then(() => {
      getProcessList()
    })
  }
})

onMounted(() => {
  getAlgList()
})
</script>