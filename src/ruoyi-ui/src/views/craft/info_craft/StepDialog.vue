<template>
  <div>
    <el-dialog title="工步信息" v-model="stepOpen" width="950px" @close="onClose">
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
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Box" size="small" @click="handlePackage">包装功能</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Box" size="small" @click="handleScratch">划痕检测</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="stepList" height="650px" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="编号" align="center" prop="code"/>
        <el-table-column label="名称" align="center" prop="name"/>

        <!-- 易错工步行内快速切换 -->
<!--        <el-table-column label="易错物料" align="center" prop="errMaterial" width="100">-->
<!--          <template #default="scope">-->
<!--            <el-switch-->
<!--                v-model="scope.row.errMaterial"-->
<!--                :active-value="1"-->
<!--                :inactive-value="0"-->
<!--                :before-change="() => handleBeforeErrMaskChange(scope.row)"-->
<!--            />-->
<!--          </template>-->
<!--        </el-table-column>-->

        <el-table-column label="引导图" align="center">
          <template #default="scope">
            <el-tag type="danger" v-if="(scope.row.code !== '78' && scope.row.code !== '88') && (scope.row.guideMapUrl === '' || scope.row.guideMapUrl === null) ">未绑定</el-tag>
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
            <el-button link type="primary" icon="Picture" :disabled="scope.row.code === '-1' || scope.row.code === '78'"
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
<!--        <el-form-item label="易错工步" prop="errMaterial">-->
<!--          <el-switch-->
<!--              v-model="form.errMaterial"-->
<!--              :active-value="1"-->
<!--              :inactive-value="0"-->
<!--              active-text="是"-->
<!--              inactive-text="否"-->
<!--          />-->
<!--        </el-form-item>-->
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
                 :tempAlgType="props.tempAlgType"
                 :packageFlag="packageFlag"
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
const packageFlag = ref(false)

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
  tempCraftType: String,
  tempAlgType: Number
})

const {queryParams, form, rules} = toRefs(data)

function getList() {
  loading.value = true
  queryParams.value.processId = props.processId
  listStep(queryParams.value).then(response => {
    stepList.value = response.rows
    console.log(stepList.value)
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    code: null,
    name: null,
    content: null,
    processId: null,
    // errMaterial: 0 // 默认值设为 0 (非易错)
  }
  proxy.resetForm("stepRef")
}

// 行内直接修改 errMaterial 状态并同步到后台
// function handleBeforeErrMaskChange(row) {
//   return new Promise((resolve, reject) => {
//     // 计算点击后的目标值 (当前是1则变为0，反之亦然)
//     const targetVal = row.errMaterial === 1 ? 0 : 1;
//     const isErr = targetVal === 1;
//     const statusText = isErr ? "已标记为易错工步" : "已取消易错工步";
//
//     updateStep({
//       id: row.id,
//       errMaterial: targetVal
//     }).then(() => {
//       proxy.$modal.msgSuccess(`${row.name || '工步'}${statusText}`);
//       resolve(true); // 返回 true 允许开关切换状态
//     }).catch(() => {
//       proxy.$modal.msgError("状态更新失败");
//       reject(false); // 失败时自动拦截，开关保持原样
//     });
//   });
// }

function handleMoveUp(index, row) {
  const prevRow = stepList.value[index - 1];
  swapOrder(row, prevRow);
}

function handleMoveDown(index, row) {
  const nextRow = stepList.value[index + 1];
  swapOrder(row, nextRow);
}

async function swapOrder(row1, row2) {
  const tempSort = row1.sort;
  row1.sort = row2.sort;
  row2.sort = tempSort;

  row1.guideMapUrl = null
  row2.guideMapUrl = null

  loading.value = true;

  Promise.all([
    updateStep(row1),
    updateStep(row2)
  ]).then(() => {
    proxy.$modal.msgSuccess("顺序调整成功");
    getList();
  }).catch(() => {
    loading.value = false;
    proxy.$modal.msgError("顺序调整失败");
    getList();
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
    // 保证 errMaterial 有确切的数值 (兼容 null / undefined)
    // form.value.errMaterial = (response.data.errMaterial === 1 || response.data.errMaterial === true) ? 1 : 0;
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
        const maxSort = stepList.value.length > 0
            ? Math.max(...stepList.value.map(s => Number(s.sort) || 0))
            : 0;
        form.value.sort = maxSort + 1;
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

async function handlePackage() {
  proxy.$modal.loading("正在生成包装工步...");
  try {
    let packageStepId;
    const existingPackageStep = stepList.value.find(s => s.code === '88');

    if (existingPackageStep) {
      packageStepId = existingPackageStep.id;
    } else {
      const maxSort = stepList.value.length > 0
          ? Math.max(...stepList.value.map(s => Number(s.sort) || 0))
          : 0;

      const stepData = {
        code: '88',
        name: '包装',
        content: '包装检测',
        processId: props.processId,
        sort: maxSort + 1
      };

      await addStep(stepData);
      await changeStatus(props.craftId);
      await getListPromise();
      const newStep = stepList.value.find(s => s.code === '88');
      packageStepId = newStep.id;
    }

    proxy.$modal.closeLoading();
    proxy.$modal.msgSuccess("包装工步生成成功！");

    packageFlag.value = true;
    targetStepIds.value = [packageStepId];
    labelVisible.value = true;

  } catch (error) {
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("生成包装工步失败，请检查网络或后端接口");
  }
}

async function handleScratch() {
  proxy.$modal.loading("正在生成划痕检测工步...");

  try {
    const existingScratchStep = stepList.value.find(s => s.code === '78');

    if (existingScratchStep) {
      proxy.$modal.msgWarning("已存在划痕检测工步！");
    } else {
      const maxSort = stepList.value.length > 0
          ? Math.max(...stepList.value.map(s => Number(s.sort) || 0))
          : 0;

      const stepData = {
        code: '78',
        name: '划痕检测',
        content: '划痕检测',
        guideMapUrl: null,
        processId: props.processId,
        sort: maxSort + 1
      };

      await addStep(stepData);
      await changeStatus(props.craftId);
      await getListPromise();
      proxy.$modal.closeLoading();
      proxy.$modal.msgSuccess("划痕检测工步生成成功！");
    }
  } catch (error) {
    proxy.$modal.closeLoading();
    proxy.$modal.msgError("生成划痕检测工步失败，请检查网络或后端接口");
  }
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

async function handleGenerateFinalStep() {
  const isExistFinalStep = stepList.value.find(s => s.code === '99')
  if (isExistFinalStep){
    return proxy.$modal.msgWarning("当前已存在终检步骤，若要重新生成请删除旧的终检步骤")
  }
  const normalSteps = stepList.value.filter(s => s.code !== '99' && s.code !== '-1' && s.code !== '88');
  if (normalSteps.length === 0) {
    return proxy.$modal.msgWarning("当前没有任何工步，无法生成终检！");
  }

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

    const mergedCoordsMap = new Map();
    normalSteps.forEach(step => {
      if (step.coordsInfo) {
        try {
          const coords = JSON.parse(step.coordsInfo);
          coords.forEach(group => {
            if (!mergedCoordsMap.has(group.label)) {
              mergedCoordsMap.set(group.label, []);
            }
            mergedCoordsMap.get(group.label).push(...group.posList);
          });
        } catch (e) {
          console.error(`解析工步 [${step.name}] 坐标失败`);
        }
      }
    });

    const finalCoordsInfo = [];
    mergedCoordsMap.forEach((posList, label) => {
      finalCoordsInfo.push({ label, posList });
    });

    const existingStep99 = stepList.value.find(s => s.code === '99');

    const finalStepData = {
      code: '99',
      name: '终检',
      content: '终检',
      processId: props.processId,
      coordsInfo: JSON.stringify(finalCoordsInfo),
      guideMapUrl: null
    };

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

    await getListPromise();

    proxy.$modal.confirm('数据已聚合。是否立即打开标注面板，预览并保存最终的组合标注图？', '提示', {
      confirmButtonText: '去预览并保存',
      cancelButtonText: '稍后处理'
    }).then(() => {
      const step99 = stepList.value.find(s => s.code === '99');
      if (step99) {
        borrowImageUrl.value = fullImageUrl;
        packageFlag.value = false;
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

function handleBind(row) {
  packageFlag.value = false;
  targetStepIds.value = [row.id]
  packageFlag.value = row.code === '88'
  labelVisible.value = true
}

function handleBatchBind() {
  if (ids.value.length === 0) return;
  packageFlag.value = false;
  targetStepIds.value = [...ids.value]
  labelVisible.value = true
}

function changeStepStatus() {
  changeStatus(props.craftId)
  getList()
}

function handleExceptionCheckChange() {
  const existingStep99 = stepList.value.find(s => s.code === '99');
  if (!existingStep99) {
    return proxy.$modal.msgWarning("必须先生成终检工步，才能生成异物检测工步！");
  }

  const normalSteps = stepList.value.filter(s => s.code !== '99' && s.code !== '-1' && s.code !== '88');
  const lastStepWithImage = [...normalSteps].reverse().find(s => s.guideMapUrl);
  if (!lastStepWithImage) {
    return proxy.$modal.msgWarning("前面没有任何工步绑定引导图，无法提取原图作为异物检测图！");
  }

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
    executeGenerateExceptionStep(lastStepWithImage.id, true, existingStep99);
  }).catch((action) => {
    if (action === 'cancel') {
      executeGenerateExceptionStep(lastStepWithImage.id, false, existingStep99);
    }
  });
}

async function executeGenerateExceptionStep(stepId, position, finalCheckStep) {
  proxy.$modal.loading("正在生成异物检测工步...");
  try {
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

    const existingExceptionStep = stepList.value.find(s => s.code === '-1');
    const stepData = {
      code: '-1',
      name: '异物检测',
      content: `异物检测`,
      processId: props.processId,
      sort: position ? finalCheckStep.sort + 1 : finalCheckStep.sort - 1,
      guideMapUrl: JSON.stringify([fullImageUrl, fullImageUrl])
    };

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
    await getListPromise();

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
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  color: #a8abb2;
  font-size: 13px;
  border-radius: 4px;
  margin: 0 auto;
}
</style>