<template>
  <div>
    <el-dialog title="工步信息" v-model="stepOpen" width="900px" @close="onClose">
      <el-row :gutter="10" class="mb8" style="display: flex; justify-content: space-between">
        <div style="display: flex">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                icon="Plus"
                size="small"
                @click="handleAdd"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                plain
                icon="Delete"
                size="small"
                :disabled="multiple"
                @click="handleDelete"
            >删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="success"
                plain
                icon="Picture"
                size="small"
                :disabled="multiple"
                @click="handleBatchBind"
            >批量绑定引导图
            </el-button>
          </el-col>
        </div>
<!--        <div style="display:flex;">-->
<!--          <el-col :span="1.5">-->
<!--            <el-checkbox-->
<!--                v-model="isException"-->
<!--                label="是否检测异常"-->
<!--            >-->
<!--            </el-checkbox>-->
<!--          </el-col>-->
<!--          <el-col :span="1.5">-->
<!--            <el-checkbox-->
<!--                v-model="isFinalCheck"-->
<!--                label="是否终检"-->
<!--            >-->
<!--            </el-checkbox>-->
<!--          </el-col>-->
<!--        </div>-->
      </el-row>

      <el-table v-loading="loading" :data="stepList" height="600px" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="顺序" align="center" prop="code"/>
        <el-table-column label="名称" align="center" prop="name"/>
        <el-table-column label="引导图" align="center">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.guideMapUrl === '' || scope.row.guideMapUrl === null">未绑定</el-tag>
            <el-tag v-else type="success">已绑定</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容" width="150px" align="center" prop="content" style="text-align:center;"
                         show-overflow-tooltip/>
        <el-table-column label="引导图" align="center">
          <template #default="scope">
            <el-image
                v-if="scope.row.guideMapUrl"
                :src="scope.row.guideMapUrl"
                :preview-src-list="[scope.row.guideMapUrl]"
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
        <el-table-column label="操作" align="center" width="300px" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Top" :disabled="scope.$index === 0 || scope.row.code === '00' || scope.row.code === '99'"
                       @click="handleMoveUp(scope.$index, scope.row)">上移
            </el-button>
            <el-button link type="primary" icon="Bottom"
                       :disabled="scope.$index === stepList.length - 1 || scope.row.code === '00' || scope.row.code === '99'"
                       @click="handleMoveDown(scope.$index, scope.row)">下移
            </el-button>
            <el-button link type="primary" icon="Picture" :disabled="scope.row.code === '00' || scope.row.code === '99'"
                       @click="handleBind(scope.row)">修改引导图
            </el-button>
            <el-button link type="primary" icon="Edit" :disabled="scope.row.code === '00' || scope.row.code === '99'"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button link type="primary" icon="Delete" :disabled="scope.row.code === '00' || scope.row.code === '99'"
                       @click="handleDelete(scope.row)">删除
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
    </el-dialog>

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="stepRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码"/>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"/>
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

    <LabelDialog v-model="labelVisible" :visible="labelVisible" :stepIds="targetStepIds"
                 :tempCraftType="props.tempCraftType"
                 @change-status="changeStepStatus"/>
  </div>
</template>

<script setup name="Step">
import {listStep, getStep, delStep, addStep, updateStep, deleteStepByCodeAndProcessId} from "@/api/craft/step"
import LabelDialog from "@/views/craft/info_craft/LabelDialog.vue";
import {changeStatus} from "@/api/craft/craft.js";
import {ref, reactive, toRefs, getCurrentInstance, watch} from "vue";
import LabelDialog_ from "@/views/craft/info_craft/LabelDialog_.vue";

const {proxy} = getCurrentInstance()

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
const isException = ref(false)
const isFinalCheck = ref(false)

watch(isException, async (value) => {
  if (value) {
    await addStep({
      code: '00',
      name: '异常处理',
      content: '异常处理',
      guideMapUrl: null,
      processId: props.processId,
    })
    getList()
  } else {
    await deleteStepByCodeAndProcessId('00', props.processId)
    getList()
  }
})

watch(isFinalCheck, async (value) => {
  if (value) {
    await addStep({
      code: '99',
      name: '终审',
      content: '终审',
      guideMapUrl: null,
      processId: props.processId,
    })
    getList()
  } else {
    await deleteStepByCodeAndProcessId('99', props.processId)
    getList()
  }
})

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
  craftId: Number,
  tempCraftType: String
})

const {queryParams, form, rules} = toRefs(data)

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

// 上移
function handleMoveUp(index, row) {
  const prevRow = stepList.value[index - 1];
  swapOrder(row, prevRow);
}

// 下移
function handleMoveDown(index, row) {
  const nextRow = stepList.value[index + 1];
  swapOrder(row, nextRow);
}

// 交换排序核心逻辑
async function swapOrder(row1, row2) {
  // 1. 交换前端显示的 code (假设你们的顺序就是根据 code 排序的)
  const tempCode = row1.code;
  row1.code = row2.code;
  row2.code = tempCode;

  row1.guideMapUrl = null
  row2.guideMapUrl = null

  loading.value = true;

  // 2. 将修改后的两条数据发送给后端保存
  // 提示：如果后端支持批量修改，建议写一个批量修改的接口 batchUpdateStep([row1, row2])
  // 这里演示连续发两次单个修改请求 (Promise.all)
  Promise.all([
    await updateStep(row1),
    await updateStep(row2)
  ]).then(() => {
    proxy.$modal.msgSuccess("顺序调整成功");
    getList(); // 重新拉取列表，确保排序生效
  }).catch(() => {
    loading.value = false;
    proxy.$modal.msgError("顺序调整失败");
    getList(); // 如果失败了，重置回原来的顺序
  });
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
    form.value.guideMapUrl = null
    open.value = true
    title.value = "修改工步信息"
  })
}

function submitForm() {
  proxy.$refs["stepRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        form.value.guideMapUrl = null
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
  proxy.$modal.confirm('是否确认删除工步信息？').then(function () {
    return delStep(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function onClose() {
  stepOpen.value = false
}

// 单个修改引导图
function handleBind(row) {
  targetStepIds.value = [row.id] // 包装成数组
  labelVisible.value = true
}

// 批量连续绑定引导图
function handleBatchBind() {
  if (ids.value.length === 0) return;
  targetStepIds.value = [...ids.value] // 传入勾选的所有ID
  labelVisible.value = true
}

function changeStepStatus() {
  changeStatus(props.craftId)
  getList()
}

watch(() => stepOpen.value, (value) => {
  if (value) {
    getList()
  }
})

getList()
</script>

<style scoped>
.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px; /* 强制和你的图片一样宽 */
  height: 60px; /* 强制和你的图片一样高 */
  background-color: #f5f7fa; /* 浅灰背景色 */
  color: #a8abb2; /* 浅灰文字色 */
  font-size: 13px;
  border-radius: 4px;
  margin: 0 auto; /* 保证在表格单元格里居中 */
}
</style>