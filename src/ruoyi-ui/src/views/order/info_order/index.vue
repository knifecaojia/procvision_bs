<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="98px">
      <el-form-item label="工单编码" style="margin-left: -26px" prop="workOrderCode">
        <el-input
          v-model="queryParams.workOrderCode"
          placeholder="请输入工单编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计划开始时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择计划开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="计划结束时间" prop="endTime">
        <el-date-picker clearable
          v-model="queryParams.endTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择计划结束时间">
        </el-date-picker>
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
      <div style="display: inline-flex; margin-top: -10px;">
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
    </el-form>

    <el-row :gutter="10" class="mb8">
        <el-col :span="1.5" >
          <el-button
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="['wo:workOrder:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['wo:workOrder:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['wo:workOrder:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="['wo:workOrder:export']"
          >导出</el-button>
        </el-col>
    </el-row>

    <el-table v-loading="loading" :data="workOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="工单编码" align="center" prop="workOrderCode" />
      <el-table-column label="工单数量" align="center" prop="workOrderQuantity" />
      <el-table-column label="工艺编码" align="center" prop="craftCode" />
      <el-table-column label="工艺版本" align="center" prop="craftVersion" />
      <el-table-column label="工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)" align="center" prop="status" />
      <el-table-column label="工序编码" align="center" prop="processCode" />
      <el-table-column label="工序名称" align="center" prop="processName" />
      <el-table-column label="派单数量" align="center" prop="dispatchQuantity" />
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
      <el-table-column label="引导图url" align="center" prop="guideMapUrl" />
      <el-table-column label="装配工人编码" align="center" prop="workerCode" />
      <el-table-column label="装配工人姓名" align="center" prop="workerName" />
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
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['wo:workOrder:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['wo:workOrder:remove']">删除</el-button>
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
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="workOrderRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工单编码" prop="workOrderCode">
          <el-input v-model="form.workOrderCode" placeholder="请输入工单编码" />
        </el-form-item>
        <el-form-item label="工单数量" prop="workOrderQuantity">
          <el-input v-model="form.workOrderQuantity" placeholder="请输入工单数量" />
        </el-form-item>
        <el-form-item label="工艺编码" prop="craftCode">
          <el-input v-model="form.craftCode" placeholder="请输入工艺编码" />
        </el-form-item>
        <el-form-item label="工艺版本" prop="craftVersion">
          <el-input v-model="form.craftVersion" placeholder="请输入工艺版本" />
        </el-form-item>
        <el-form-item label="工序编码" prop="processCode">
          <el-input v-model="form.processCode" placeholder="请输入工序编码" />
        </el-form-item>
        <el-form-item label="工序名称" prop="processName">
          <el-input v-model="form.processName" placeholder="请输入工序名称" />
        </el-form-item>
        <el-form-item label="派单数量" prop="dispatchQuantity">
          <el-input v-model="form.dispatchQuantity" placeholder="请输入派单数量" />
        </el-form-item>
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
        <el-form-item label="引导图url" prop="guideMapUrl">
          <el-input v-model="form.guideMapUrl" placeholder="请输入引导图url" />
        </el-form-item>
        <el-form-item label="装配工人编码" prop="workerCode">
          <el-input v-model="form.workerCode" placeholder="请输入装配工人编码" />
        </el-form-item>
        <el-form-item label="装配工人姓名" prop="workerName">
          <el-input v-model="form.workerName" placeholder="请输入装配工人姓名" />
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

<script setup name="WorkOrder">
import { listWorkOrder, getWorkOrder, delWorkOrder, addWorkOrder, updateWorkOrder } from "@/api/order/workOrder"

const { proxy } = getCurrentInstance()

const workOrderList = ref([])
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
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

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
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getWorkOrder(_id).then(response => {
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
  proxy.$modal.confirm('是否确认删除工单编号为"' + _ids + '"的数据项？').then(function() {
    return delWorkOrder(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('wo/workOrder/export', {
    ...queryParams.value
  }, `workOrder_${new Date().getTime()}.xlsx`)
}

getList()
</script>
