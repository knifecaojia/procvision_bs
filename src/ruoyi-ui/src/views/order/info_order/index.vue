<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="98px">
      <el-form-item label="装配任务编码" prop="workOrderCode">
        <el-input
            v-model="queryParams.workOrderCode"
            placeholder="请输入任务编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="装配工人编码" prop="workerCode">
        <el-input
            v-model="queryParams.workerCode"
            placeholder="请输入装配工人编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="装配工人姓名" prop="workerName" style="margin-right: 10px; vertical-align: bottom">
        <el-input
            v-model="queryParams.workerName"
            placeholder="请输入装配工人姓名"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

<!--    <el-row :gutter="10" class="mb8">-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="primary"-->
<!--            plain-->
<!--            icon="Plus"-->
<!--            @click="handleAdd"-->
<!--        >新增-->
<!--        </el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="success"-->
<!--            plain-->
<!--            icon="Edit"-->
<!--            :disabled="single"-->
<!--            @click="handleUpdate"-->
<!--        >修改-->
<!--        </el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="danger"-->
<!--            plain-->
<!--            icon="Delete"-->
<!--            :disabled="multiple"-->
<!--            @click="handleDelete"-->
<!--        >删除-->
<!--        </el-button>-->
<!--      </el-col>-->
<!--      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>-->
<!--    </el-row>-->

    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column label="装配任务编码" align="center" prop="workOrderCode"/>
      <el-table-column label="项目号" align="center" prop="projectNo"/>
      <el-table-column label="资源状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === -1" size="small" type="info">未配置引导资源</el-tag>
          <el-tag v-else-if="scope.row.status === -2" size="small" type="warning">未配置检测资源</el-tag>
          <el-tag v-else size="small" type="success">已就绪</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工作状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="info">待派单</el-tag>
          <el-tag v-else-if="scope.row.status === 2" size="small" type="primary">进行中</el-tag>
          <el-tag v-else-if="scope.row.status === 3" size="small" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 4" size="small" type="success">手工通过</el-tag>
          <el-tag v-else size="small" type="danger">资源未就绪</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="计划开始时间" align="center" prop="startTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计划结束时间" align="center" prop="endTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务详情" align="center">
        <template #default="scope">
          <el-button type="primary" link icon="view" plain @click="handleDetail(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250px" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-config-provider :message="config">
            <el-button link type="primary" icon="View" @click="handleResultShow(scope.row)">结果查看</el-button>
          </el-config-provider>
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

    <!-- 添加或修改工单对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="workOrderRef" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单编码" prop="workOrderCode">
              <el-input v-model="form.workOrderCode" placeholder="请输入工单编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺编码" prop="craftCode">
              <el-input v-model="form.craftCode" placeholder="请输入工艺编码"/>
              <!--              <el-select v-model="form.craftCode" placeholder="请选择工艺编码">-->
              <!--                <el-option v-for="item in craftList" :key="item.craftCode" :label="item.craftCode" :value="item.craftCode">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺版本" prop="craftVersion">
              <el-input v-model="form.craftVersion" placeholder="请输入工艺版本"/>
              <!--              <el-select v-model="form.craftVersion" placeholder="请选择工艺版本">-->
              <!--                <el-option v-for="item in craftVersionList" :key="item.craftVersion" :label="item.craftVersion" :value="item.craftVersion">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序编码" prop="processCode">
              <el-input v-model="form.processCode" placeholder="请输入工序编码"/>
              <!--              <el-select v-model="form.processCode" placeholder="请选择工序编码">-->
              <!--                <el-option v-for="item in processList" :key="item.processCode" :label="item.processName" :value="item.processCode">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序名称" prop="processName">
              <el-input v-model="form.processName" placeholder="请输入工序名称"/>
              <!--              <el-select v-model="form.processName" placeholder="请选择工序名称" >-->
              <!--                <el-option v-for="item in processNameList" :key="item.processName" :label="item.processName" :value="item.processName">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人姓名" prop="workerName">
              <el-input v-model="form.workerName" placeholder="请输入装配工人姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人编码" prop="workerCode">
              <el-input v-model="form.workerCode" placeholder="请输入装配工人编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
          </el-col>
        </el-row>
        <el-form-item label="计划开始时间" prop="startTime">
          <el-date-picker clearable
                          v-model="form.startTime"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择计划开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="计划结束时间" prop="endTime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择计划结束时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="任务详情" v-model="isWorkOrderInfoOpen" width="580px">
      <el-form :model="workOrderInfo">
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item label="工艺编码">
              {{ workOrderInfo.craftCode }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工艺版本">
              {{ workOrderInfo.craftVersion }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序编码">
              {{ workOrderInfo.processCode }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工序名称">
              {{ workOrderInfo.processName }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人编码">
              {{ workOrderInfo.workerCode }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装配工人姓名">
              {{ workOrderInfo.workerName }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产订单号">
              {{ workOrderInfo.prodOrderNo }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产批次号">
              {{ workOrderInfo.prodBatchNo }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="材料号">
              {{ workOrderInfo.materialNo }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="材料名称">
              {{ workOrderInfo.materialName }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>

    <el-dialog title="任务结果" v-model="isWorkOrderResult" width="600px">
      <el-scrollbar max-height="600px">
        <el-timeline v-if="stepRecordList.length > 0">
          <el-timeline-item
              v-for="(activity, index) in stepRecordList"
              :key="index"
              color="blue"
          >
            <el-descriptions :title="'工步' + activity.stepNo" border column="2" style="width: 500px">
              <el-descriptions-item label="工步名称" width="100px">
                {{ activity.stepName }}
              </el-descriptions-item>
              <el-descriptions-item label="图片" width="100px" span="2" :rowspan="2" align="center">
                <el-image :src="activity.imgUrl" :previewSrcList="[activity.imgUrl]" style="width: 200px; height: 100px;"/>
              </el-descriptions-item>
              <el-descriptions-item label="工步状态" width="100px">
                <el-tag v-if="activity.stepStatus === 1" type="danger">未完成</el-tag>
                <el-tag v-else-if="activity.stepStatus === 2" type="success">已完成</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="当前任务没有工步结果"/>
      </el-scrollbar>
    </el-dialog>
  </div>
</template>

<script setup name="WorkOrder">
import {listWorkOrder, getWorkOrder, delWorkOrder, addWorkOrder, updateWorkOrder} from "@/api/order/workOrder"
import {getRecordByTaskNo} from "@/api/process/record.js";

const {proxy} = getCurrentInstance()

const workOrderList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isWorkOrderInfoOpen = ref(false)
const isWorkOrderResult = ref(false)
const imageUrl = ref("http://39.104.202.123:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2Rldi8yMDI2LTAxLTE3MDdiOWEyNDMtNTdhYy00MGY1LTlhZTItYzlkNTQyMGQ2NGI4P1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9WkNXMVVNVkFWOENJTjRGMVBDQ0MlMkYyMDI2MDExNyUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNjAxMTdUMDU0MzU4WiZYLUFtei1FeHBpcmVzPTQzMjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPWV5SmhiR2NpT2lKSVV6VXhNaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJObGMzTkxaWGtpT2lKYVExY3hWVTFXUVZZNFEwbE9ORVl4VUVORFF5SXNJbVY0Y0NJNk1UYzJPRFkzTVRBeE5pd2ljR0Z5Wlc1MElqb2lZV1J0YVc0aWZRLm9KMDVQeWtJNmEyOUp4SG9sMDZnalhJb0pqNlM5QUE1eUhZZk0xSjVxRVJWX2R6ck5mZnV0MGhuQ3hpNkVLYkJ1WFhDRlZybUlPdEtWX2N5NW9FamxBJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZ2ZXJzaW9uSWQ9bnVsbCZYLUFtei1TaWduYXR1cmU9NDhhZmQyNDk0OWJhZDQ5NGY1ZWE0OGExZTU4OGYzNTQ3ODA2NmE0NDc2Mjc2YTJmOTQ1Y2IzMjdiOTZkMmI4Mg")

const stepRecordList = ref([])

const activities = [
  {
    content: '工步1',
    status: 2
  },
  {
    content: '工步2',
    status: 1
  },
]


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workOrderCode: null,
    workOrderQuantity: null,
    craftCode: null,
    craftVersion: null,
    status: null,
    processCode: null,
    processName: null,
    dispatchQuantity: null,
    startTime: null,
    endTime: null,
    guideMapUrl: null,
    workerCode: null,
    workerName: null,
  },
  rules: {},
  workOrderInfo: {}
})

const config = reactive({
  max: 3,
  plain: true,
  placement: 'bottom',
})

const {queryParams, form, rules, workOrderInfo} = toRefs(data)

/** 查询工单列表 */
function getList() {
  loading.value = true
  listWorkOrder(queryParams.value).then(response => {
    workOrderList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

async function handleResultShow(row) {
  if (row.status >= 2) {
    stepRecordList.value = []
    await getRecordByTaskNo(row.workOrderCode).then(response => {
      isWorkOrderResult.value = true
      stepRecordList.value = response.data.stepInfo
    })
  } else
    proxy.$modal.msgWarning("任务未分配")
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    workOrderCode: null,
    workOrderQuantity: null,
    craftCode: null,
    craftVersion: null,
    status: null,
    processCode: null,
    processName: null,
    dispatchQuantity: null,
    startTime: null,
    endTime: null,
    guideMapUrl: null,
    workerCode: null,
    workerName: null,
  }
  proxy.resetForm("workOrderRef")
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
  title.value = "添加工单"
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  await getWorkOrder(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工单"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["workOrderRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateWorkOrder(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addWorkOrder(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除工单编号为"' + _ids + '"的数据项？').then(function () {
    return delWorkOrder(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleDetail(row) {
  isWorkOrderInfoOpen.value = true
  workOrderInfo.value = row
}

getList()
</script>
