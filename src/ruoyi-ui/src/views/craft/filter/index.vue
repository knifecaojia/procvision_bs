<template>
  <div class="app-container">
    <!-- 搜索筛选区 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="产品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择产品类型" clearable style="width: 180px">
          <el-option
              v-for="item in productTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="关键工序" prop="processName">
        <el-input
            v-model="queryParams.processName"
            placeholder="请输入工序名称"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
        />
      </el-form-item>

      <el-form-item label="过滤关键词" prop="keyword">
        <el-input
            v-model="queryParams.keyword"
            placeholder="请输入步骤关键词"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
        />
      </el-form-item>

      <el-form-item label="状态" prop="isEnabled">
        <el-select v-model="queryParams.isEnabled" placeholder="规则状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['process:craftFilter:add']">
          新增规则
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['process:craftFilter:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['process:craftFilter:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Refresh" @click="handleRefreshCache" v-hasPermi="['process:craftFilter:edit']">
          刷新缓存
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="产品类型" align="center" prop="productType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.productType === 0" type="warning">天线</el-tag>
          <el-tag v-else-if="scope.row.productType === 1" type="primary">板级</el-tag>
          <el-tag v-else-if="scope.row.productType === 2" type="success">模块</el-tag>
          <el-tag v-else type="info">未知({{ scope.row.productType }})</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="绑定工序名称" align="center" prop="processName" min-width="150" />
      <el-table-column label="待过滤步骤关键词" align="center" prop="keyword" min-width="160">
        <template #default="scope">
          <el-tag type="danger">{{ scope.row.keyword }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="匹配模式" align="center" prop="matchMode" width="110">
        <template #default="scope">
          <span v-if="scope.row.matchMode === 1">精确匹配</span>
          <span v-else-if="scope.row.matchMode === 2">模糊包含</span>
          <span v-else-if="scope.row.matchMode === 3">正则匹配</span>
        </template>
      </el-table-column>
      <el-table-column label="启用状态" align="center" prop="isEnabled" width="90">
        <template #default="scope">
          <el-switch
              v-model="scope.row.isEnabled"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="130" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="165" />
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['process:craftFilter:edit']">
            修改
          </el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['process:craftFilter:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 新增 / 修改对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="产品类型" prop="productType">
          <el-select v-model="form.productType" placeholder="请选择产品类型" style="width: 100%">
            <el-option
                v-for="item in productTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="绑定工序" prop="processName">
          <el-input v-model="form.processName" placeholder="如：装配、检验（SMT）" />
        </el-form-item>

        <el-form-item label="步骤关键词" prop="keyword">
          <el-input v-model="form.keyword" placeholder="请输入该工序下需剔除的关键词" />
        </el-form-item>

        <el-form-item label="匹配模式" prop="matchMode">
          <el-radio-group v-model="form.matchMode">
            <el-radio :label="2">包含匹配</el-radio>
            <el-radio :label="1">精确匹配</el-radio>
            <el-radio :label="3">正则匹配</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="启用状态" prop="isEnabled">
          <el-radio-group v-model="form.isEnabled">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入过滤原因或备注说明" :rows="3" />
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

<script setup name="BizCraftFilter">
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import {
  listFilterRule,
  getFilterRule,
  delFilterRule,
  addFilterRule,
  updateFilterRule,
  changeRuleStatus,
  refreshCache
} from '@/api/craft/filterRules'

const { proxy } = getCurrentInstance()

const ruleList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

// 0:天线, 1:板级, 2:模块
const productTypeOptions = [
  { label: '天线', value: 0 },
  { label: '板级', value: 1 },
  { label: '模块', value: 2 }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productType: undefined,
    processName: undefined,
    keyword: undefined,
    isEnabled: undefined
  },
  rules: {
    productType: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
    processName: [{ required: true, message: '关键工序不能为空', trigger: 'blur' }],
    keyword: [{ required: true, message: '过滤关键词不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listFilterRule(queryParams.value).then(response => {
    ruleList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    id: undefined,
    productType: undefined,
    processName: undefined,
    keyword: undefined,
    matchMode: 2,
    isEnabled: 1,
    remark: undefined
  }
  proxy.resetForm('ruleRef')
}

function handleAdd() {
  reset()
  if (queryParams.value.productType !== undefined) {
    form.value.productType = queryParams.value.productType
  }
  open.value = true
  title.value = '新增工序步骤工艺过滤规则'
}

function handleUpdate(row) {
  reset()
  const ruleId = row.id || ids.value[0]
  getFilterRule(ruleId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改工序步骤工艺过滤规则'
  })
}

function handleStatusChange(row) {
  const text = row.isEnabled === 1 ? '启用' : '停用'
  proxy.$modal.confirm('确认要' + text + '该条规则吗?').then(() => {
    return changeRuleStatus(row.id, row.isEnabled)
  }).then(() => {
    proxy.$modal.msgSuccess(text + '成功')
  }).catch(() => {
    row.isEnabled = row.isEnabled === 1 ? 0 : 1
  })
}

function handleRefreshCache() {
  proxy.$modal.confirm('确认要全量刷新工艺过滤规则的 Redis 缓存吗？').then(() => {
    return refreshCache()
  }).then(() => {
    proxy.$modal.msgSuccess('Redis 缓存刷新成功')
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs['ruleRef'].validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        updateFilterRule(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addFilterRule(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function cancel() {
  open.value = false
  reset()
}

function handleDelete(row) {
  const ruleIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除规则编号为 "' + ruleIds + '" 的数据项？').then(() => {
    return delFilterRule(ruleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>