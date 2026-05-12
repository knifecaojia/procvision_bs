<template>
  <div>
    <el-dialog title="工步信息" v-model="stepOpen" width="900px" @close="onClose">
      <el-row :gutter="10" class="mb8">
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
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Finished" size="small" @click="handleGenerateFinalStep">生成终检</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" plain icon="HelpFilled" size="small" @click="handleExceptionCheckChange">异物检测</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="stepList" height="600px" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="序号" align="center" prop="code"/>
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
            <el-button link type="primary" icon="Top"
                       :disabled="scope.$index === 0 || scope.row.code === '-1' || scope.row.code === '99'"
                       @click="handleMoveUp(scope.$index, scope.row)">上移
            </el-button>
            <el-button link type="primary" icon="Bottom"
                       :disabled="scope.$index === stepList.length - 1 || scope.row.code === '-1' || scope.row.code === '99'"
                       @click="handleMoveDown(scope.$index, scope.row)">下移
            </el-button>
            <el-button link type="primary" icon="Picture" :disabled="scope.row.code === '-1'"
                       @click="handleBind(scope.row)">修改引导图
            </el-button>
            <el-button link type="primary" icon="Edit" :disabled="scope.row.code === '-1'"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button link type="primary" icon="Delete"
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
                 :borrowImageUrl="borrowImageUrl"
                 @change-status="changeStepStatus"/>
  </div>
</template>

<script setup name="Step">
import {
  listStep,
  getStep,
  delStep,
  addStep,
  updateStep, getStepOri,
} from "@/api/craft/step"
import LabelDialog from "@/views/craft/info_craft/LabelDialog.vue";
import {changeStatus} from "@/api/craft/craft.js";
import {ref, reactive, toRefs, getCurrentInstance, watch} from "vue";
import { ElMessageBox } from 'element-plus';
import {changeExceptionCheck} from "@/api/craft/process.js";

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
const borrowImageUrl = ref('')

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

// ================= 新增：生成终检工步核心逻辑 =================
async function handleGenerateFinalStep() {
  // 1. 获取除终检(99)及异物检测(-1)之外的所有正常工步
  const normalSteps = stepList.value.filter(s => s.code !== '99' && s.code !== '-1');
  if (normalSteps.length === 0) {
    return proxy.$modal.msgWarning("当前没有任何工步，无法生成终检！");
  }

  // 2. 找到最后一个带有引导图的工步
  const lastStepWithImage = [...normalSteps].reverse().find(s => s.guideMapUrl);
  if (!lastStepWithImage) {
    return proxy.$modal.msgWarning("前面的工步均未绑定引导图，无法提取背景图！");
  }

  proxy.$modal.loading("正在聚合数据并生成终检工步...");

  try {
    const result = await getStep(lastStepWithImage.id);
    let fullImageUrl = '';
    try {
      const urls = JSON.parse(result.data.guideMapUrl);
      fullImageUrl = urls[0];
    } catch (e) {
      fullImageUrl = result.data.guideMapUrl;
    }

    if (!fullImageUrl) {
      proxy.$modal.closeLoading();
      return proxy.$modal.msgError("无法获取最后一个工步的背景图");
    }

    // 3. 遍历所有正常工步，把 coordsInfo 中的坐标按 label 进行聚合
    const mergedCoordsMap = new Map();
    normalSteps.forEach(step => {
      if (step.coordsInfo) {
        try {
          const coords = JSON.parse(step.coordsInfo);
          coords.forEach(group => {
            if (!mergedCoordsMap.has(group.label)) {
              mergedCoordsMap.set(group.label, []);
            }
            // 将相同 label 的框合并到一起
            mergedCoordsMap.get(group.label).push(...group.posList);
          });
        } catch (e) {
          console.error(`解析工步 [${step.name}] 坐标失败`);
        }
      }
    });

    // 组装成后端需要的 JSON 数组结构
    const finalCoordsInfo = [];
    mergedCoordsMap.forEach((posList, label) => {
      finalCoordsInfo.push({ label, posList });
    });

    // 4. 准备终检工步(99)的数据
    const existingStep99 = stepList.value.find(s => s.code === '99');

    const finalStepData = {
      code: '99',
      name: '终检',
      content: '终检',
      processId: props.processId,
      coordsInfo: JSON.stringify(finalCoordsInfo),
      guideMapUrl: null
    };

    // 5. 保存数据到数据库
    if (existingStep99) {
      finalStepData.id = existingStep99.id;
      await updateStep(finalStepData);
      console.log("终检工步已更新")
    } else {
      await addStep(finalStepData);
      console.log("终检工步已添加")
      await changeStatus(props.craftId);
    }

    proxy.$modal.msgSuccess("终检工步生成成功！");

    // 重新获取列表，以获取最新的数据和生成的ID
    await getListPromise();

    // 6. 自动唤起标注弹窗
    proxy.$modal.confirm('数据已聚合。是否立即打开标注面板，预览并保存最终的组合标注图？', '提示', {
      confirmButtonText: '去预览并保存',
      cancelButtonText: '稍后处理'
    }).then(() => {
      const step99 = stepList.value.find(s => s.code === '99');
      if (step99) {
        borrowImageUrl.value = fullImageUrl;
        handleBind(step99);
      }
    }).catch(() => {});

  } catch (error) {
    console.error(error);
    proxy.$modal.msgError("生成终检工步失败，请检查网络或后端接口");
  } finally {
    proxy.$modal.closeLoading();
  }
}

// 封装一个 Promise 版本的 getList 确保同步执行完毕后再打开弹窗
function getListPromise() {
  return new Promise((resolve) => {
    loading.value = true;
    queryParams.value.processId = props.processId;
    listStep(queryParams.value).then(response => {
      stepList.value = response.rows;
      total.value = response.total;
      loading.value = false;
      resolve();
    });
  });
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

function handleExceptionCheckChange() {
  // 1. 检查终检工步是否已生成
  const existingStep99 = stepList.value.find(s => s.code === '99');
  if (!existingStep99) {
    return proxy.$modal.msgWarning("必须先生成终检工步，才能生成异物检测工步！");
  }

  // 2. 获取某一个工步的原图（这里取最后一个绑定了引导图的正常工步）
  const normalSteps = stepList.value.filter(s => s.code !== '99' && s.code !== '-1');
  const lastStepWithImage = [...normalSteps].reverse().find(s => s.guideMapUrl);
  if (!lastStepWithImage) {
    return proxy.$modal.msgWarning("前面没有任何工步绑定引导图，无法提取原图作为异物检测图！");
  }

  // 3. 弹窗让用户选择工步顺序
  ElMessageBox.confirm(
      '请选择异物检测工步在流程中的顺序位置：',
      '生成异物检测',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '在终检后',
        cancelButtonText: '在终检前',
        type: 'info'
      }
  ).then(() => {
    // 用户点击了“在终检后”
    executeGenerateExceptionStep(lastStepWithImage.id, true, existingStep99);
  }).catch((action) => {
    if (action === 'cancel') {
      // 用户点击了“在终检前”
      executeGenerateExceptionStep(lastStepWithImage.id, false, existingStep99);
    }
    // 如果 action 是 'close'（点击右上角X或遮罩层）则什么都不做
  });
}

// 提取实际生成/保存逻辑
async function executeGenerateExceptionStep(stepId, position, finalCheckStep) {
  proxy.$modal.loading("正在生成异物检测工步...");
  try {
    // 重新获取该工步信息以拿到完整的背景图 URL
    const result = await getStepOri(stepId);
    let fullImageUrl = '';
    try {
      const urls = JSON.parse(result.data.guideMapUrl);
      fullImageUrl = urls[0];
    } catch (e) {
      fullImageUrl = result.data.guideMapUrl;
    }

    if (!fullImageUrl) {
      proxy.$modal.closeLoading();
      return proxy.$modal.msgError("无法获取工步的原图");
    }

    // 4. 构造异物检测数据
    const existingExceptionStep = stepList.value.find(s => s.code === '-1');
    const stepData = {
      code: '-1',
      name: '异物检测',
      // 通过 content 记录位置标识，方便在表格中直观查看（若后端有其他专属字段可放在对应字段）
      content: `异物检测`,
      processId: props.processId,
      sort: position ? finalCheckStep.sort + 1 : finalCheckStep.sort - 1,
      guideMapUrl: JSON.stringify([fullImageUrl, fullImageUrl])
    };

    // 5. 保存或更新
    if (existingExceptionStep) {
      stepData.id = existingExceptionStep.id;
      await updateStep(stepData);
      console.log("异物检测工步已更新");
    } else {
      await addStep(stepData);
      console.log("异物检测工步已添加");
      await changeStatus(props.craftId);
    }

    proxy.$modal.msgSuccess("异物检测工步生成成功！");
    await getListPromise(); // 刷新列表，获取最新状态

  } catch (error) {
    console.error(error);
    proxy.$modal.msgError("生成异物检测工步失败，请检查网络或后端接口");
  } finally {
    proxy.$modal.closeLoading();
  }
}

watch(() => stepOpen.value, (value) => {
  if (value) {
    getList()
  }else {
    changeStatus(props.craftId)
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