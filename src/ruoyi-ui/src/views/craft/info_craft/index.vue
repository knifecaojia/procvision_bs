<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="code">
        <el-input
            v-model="queryParams.code"
            placeholder="请输入编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="code">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本" prop="version">
        <el-input
            v-model="queryParams.version"
            placeholder="请输入版本"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['craft:craft:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['craft:craft:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['craft:craft:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="craftList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="编码" align="center" prop="code"/>
      <el-table-column label="名称" align="center" prop="name"/>
      <el-table-column label="版本" align="center" prop="version"/>
      <el-table-column label="说明" align="center" prop="desc"/>
      <el-table-column label="详细" align="center" prop="">
        <template #default="scope">
          <el-button link type="primary" @click="showProcess(scope.row)">查看工序</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['craft:craft:edit']">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['craft:craft:remove']">删除
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

    <!-- 添加或修改工艺信息对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="craftRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码"/>
        </el-form-item>
        <el-form-item label="名称" prop="code">
          <el-input v-model="form.name" placeholder="请输入名称"/>
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本"/>
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

    <el-dialog title="工序信息" v-model="processOpen" width="900px" append-to-body>
      <el-table v-loading="loading" :data="processList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="工序号" align="center" prop="code"/>
        <el-table-column label="工序名称" align="center" prop="name"/>
        <el-table-column label="说明" align="center" prop="desc"/>
        <el-table-column label="首部检验" align="center" prop=""/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleProcessUpdate(scope.row)" v-hasPermi="['craft:craft:edit']">
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
  </div>
</template>

<script setup name="Craft">
import {listCraft, getCraft, delCraft, addCraft, updateCraft} from "@/api/craft/craft.js"
import {listProcess} from "@/api/craft/process.js";

const {proxy} = getCurrentInstance()

const craftList = ref([])
const processList = ref([])
const open = ref(false)
const loading = ref(true)
const processLoading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const processTotal = ref(0)
const title = ref("")
const processOpen = ref(false)
const tempCraftId = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    code: null,
    name: null,
    version: null,
    desc: null
  },
  processPageParms: {
    pageNum: 1,
    pageSize: 10,
    craftId: null
  },
  rules: {}
})

const {queryParams, form, rules, processPageParms} = toRefs(data)

/** 查询工艺信息列表 */
function getList() {
  loading.value = true
  listCraft(queryParams.value).then(response => {
    craftList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function getProcessList() {
  processLoading.value = true
  processPageParms.craftId = tempCraftId.value
  listProcess(processPageParms.value).then(response => {
    processList.value = response.rows
    processTotal.value = response.total
    processLoading.value = false
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
    version: null,
    desc: null
  }
  proxy.resetForm("craftRef")
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
  title.value = "添加工艺信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCraft(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工艺信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["craftRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateCraft(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCraft(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除工艺信息编号为"' + _ids + '"的数据项？').then(function () {
    return delCraft(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function showProcess(row) {
  processOpen.value = true
  processLoading.value = true
  tempCraftId.value = row.id
  listProcess({craftId: row.id}).then(res => {
    if (res.code === 200) {
      processList.value = res.rows
      processTotal.value = res.total
      processLoading.value = false
    }
  })
}

getList()
</script>
