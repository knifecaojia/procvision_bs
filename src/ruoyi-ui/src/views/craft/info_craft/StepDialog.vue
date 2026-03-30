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
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="success"
              plain
              icon="Picture"
              size="small"
              :disabled="multiple"
              @click="handleBatchBind"
          >批量绑定引导图</el-button>
        </el-col>
      </el-row>
      <el-table v-loading="loading" :data="stepList" height="600px" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="名称" align="center" prop="name" />
        <el-table-column label="引导图" align="center">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.guideMapUrl === '' || scope.row.guideMapUrl === null">未绑定</el-tag>
            <el-tag v-else type="success">已绑定</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容" align="center">
          <template #default="scope">
            <el-popover width="200" title="内容" placement="top" :content="scope.row.content">
              <template #reference>
                <el-button link type="primary" icon="view">查看</el-button>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="300px" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Picture" @click="handleBind(scope.row)">修改引导图</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <LabelDialog v-model="labelVisible" :visible="labelVisible" :stepIds="targetStepIds" @change-status="changeStepStatus"/>
  </div>
</template>

<script setup name="Step">
import { listStep, getStep, delStep, addStep, updateStep } from "@/api/craft/step"
import LabelDialog from "@/views/craft/info_craft/LabelDialog.vue";
import {changeStatus} from "@/api/craft/craft.js";
import { ref, reactive, toRefs, getCurrentInstance, watch } from "vue";

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
const labelVisible = ref(false)

// 新增：用于传递给标注组件的实际操作 ID 数组
const targetStepIds = ref([])

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
  processId: Number,
  craftId: Number
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

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加工步信息"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getStep(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工步信息"
  })
}

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
          changeStatus(props.craftId)
          open.value = false
          getList()
        })
      }
    }
  })
}

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

// 单个修改引导图
function handleBind(row){
  targetStepIds.value = [row.id] // 包装成数组
  labelVisible.value = true
}

// 批量连续绑定引导图
function handleBatchBind() {
  if (ids.value.length === 0) return;
  targetStepIds.value = [...ids.value] // 传入勾选的所有ID
  labelVisible.value = true
}

function changeStepStatus(){
  changeStatus(props.craftId)
  getList()
}

watch(() => stepOpen.value, (value) => {
  if(value){
    getList()
  }
})

getList()
</script>