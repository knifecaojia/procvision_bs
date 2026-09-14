<template>
  <div class="app-container assembly-results">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" label-width="100px" @submit.prevent="handleQuery">
        <el-form-item label="记录时间">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
                          start-placeholder="开始日期" end-placeholder="结束日期" :clearable="false"
                          :shortcuts="dateShortcuts"/>
        </el-form-item>
        <el-form-item v-for="field in searchFields" :key="field.key" :label="field.label">
          <el-input v-model="filters[field.key]" :placeholder="field.placeholder" clearable maxlength="100"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="检测结果">
          <el-select v-model="filters.algResult" clearable placeholder="全部结果" style="width: 190px">
            <el-option v-for="option in algOptions" :key="option.value" :label="option.label" :value="option.value"/>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="图像条件">-->
<!--          <el-select v-model="filters.hasImage" clearable placeholder="全部记录" style="width: 190px">-->
<!--            <el-option label="有图像" :value="true"/>-->
<!--            <el-option label="无图像" :value="false"/>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
        <el-form-item>
          <el-button type="primary" icon="Search" :loading="loading" :disabled="exporting" @click="handleQuery">查询
          </el-button>
          <el-button icon="Refresh" :disabled="loading || exporting" @click="handleReset">重置</el-button>
          <el-button type="warning" plain icon="Download" :loading="exporting" :disabled="loading || !loaded"
                     @click="handleExportPDF">
            {{ exporting ? exportProgress : '一键导出多媒体 PDF 报告' }}
          </el-button>
        </el-form-item>
      </el-form>
      <!--      <div class="query-note">按提交时间查询，提交时间为空时取创建时间。默认近7天，单次查询最多366天；PDF包含全部匹配记录，单份最多500条。</div>-->
    </el-card>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon :closable="false" class="status-alert"/>
    <template v-if="loaded">
      <div class="applied-query">当前结果：{{ appliedQuery.startDate }} 至 {{ appliedQuery.endDate }} ·
        {{ appliedDescription }}
      </div>
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="card in statCards" :key="card.key" :xs="12" :sm="8" :lg="4">
          <el-card shadow="hover" class="stat-card">
            <div>{{ card.label }}</div>
            <strong :style="{ color: card.color }">{{ statistics[card.key] || 0 }}</strong></el-card>
        </el-col>
      </el-row>
      <div class="query-note">资源未就绪 {{ statistics.abnormal || 0 }} 个，待派单 {{ statistics.pending || 0 }} 个。
      </div>
      <el-row :gutter="16" class="charts" v-loading="loading">
        <el-col v-for="(title, index) in chartTitles" :key="title" :xs="24" :lg="12">
          <el-card shadow="hover">
            <template #header>{{ title }}</template>
            <div :ref="el => chartElements[index] = el" class="chart"/>
          </el-card>
        </el-col>
      </el-row>
      <el-card shadow="never" class="records-panel">
        <template #header>
          <div class="records-header"><span>装配图像明细（共 {{ total }} 条记录）</span>
            <el-button link type="primary" icon="Refresh" :disabled="loading || exporting" @click="refreshApplied">
              刷新当前结果
            </el-button>
          </div>
        </template>
        <el-empty v-if="!rows.length && !loading" description="当前条件下暂无装配记录"/>
        <el-row :gutter="16" v-loading="loading">
          <el-col v-for="item in rows" :key="item.id" :xs="24" :sm="12" :lg="6" class="record-column">
            <el-card :body-style="{ padding: '0' }" shadow="hover">
              <el-image v-if="item.hasImage" :src="item.imgUrl" :preview-src-list="item.imgUrl ? [item.imgUrl] : []"
                        fit="contain" class="record-image" lazy preview-teleported>
                <template #error>
                  <div class="image-empty">{{ item.imageError || '图片暂不可用' }}</div>
                </template>
              </el-image>
              <div v-else class="record-image image-empty">未保存图像</div>
              <div class="record-details">
                <div>工单：{{ item.workOrderCode || '—' }}</div>
                <div>工序：{{ item.processName || item.processCode || '—' }}</div>
                <div>工步：{{ item.stepName || item.stepCode || '—' }}</div>
                <div>时间：{{ item.recordTime || '—' }}</div>
                <div class="record-tags">
                  <el-tag :type="Number(item.stepStatus) === 2 ? 'success' : 'info'">
                    {{ Number(item.stepStatus) === 2 ? '已完成' : '未完成' }}
                  </el-tag>
                  <el-tag
                      :type="item.algResult != null && Number(item.algResult) === 0 ? 'success' : Number(item.algResult) === 1 ? 'danger' : 'info'">
                    {{ algResultLabel(item.algResult) }}
                  </el-tag>
                </div>
                <!--                <div-->
                <!--                    v-if="item.ngReason"-->
                <!--                    class="ng-reason"-->
                <!--                    :title="item.ngReason"-->
                <!--                >-->
                <!--                  异常原因：{{ item.ngReason }}-->
                <!--                </div>-->
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-pagination v-if="total" v-model:current-page="pageNum" v-model:page-size="pageSize"
                       :page-sizes="[12, 24, 48]"
                       :total="total" layout="total, sizes, prev, pager, next" :disabled="loading || exporting"
                       @current-change="changePage" @size-change="changeSize"/>
      </el-card>
    </template>
  </div>
</template>

<script setup name="AssemblyDashboard">
import {ref, reactive, computed, nextTick, onMounted, onBeforeUnmount, onActivated} from 'vue'
import {ElMessage} from 'element-plus'
import * as echarts from 'echarts'
import {getResultOverview, getRecentResults, getResultReport, getResultImage} from '@/api/order/result'
import {resultChartOptions, chartTitles, algResultLabel} from '@/utils/assemblyResultCharts'
import {createAssemblyResultPdf} from '@/utils/assemblyResultReport'

function lastDays(days) {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - days + 1);
  return [start, end]
}

function dateString(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const dateRange = ref(lastDays(7).map(dateString))
const dateShortcuts = [7, 30, 90].map(days => ({text: `近${days}天`, value: () => lastDays(days)}))
const defaults = () => ({
  product: '',
  prodBatchNo: '',
  workOrderCode: '',
  process: '',
  step: '',
  algResult: null,
  hasImage: null
})
const filters = reactive(defaults())
const searchFields = [
  {key: 'workOrderCode', label: '工单编码', placeholder: '输入工单编码'},
  {key: 'process', label: '工序', placeholder: '工序名称或编码'},
  {key: 'step', label: '工步', placeholder: '工步名称或编码'}
]
const algOptions = [0, 1, -1, 2].map(value => ({value, label: algResultLabel(value)}))
const statCards = [
  {key: 'recordCount', label: '匹配记录', color: '#409eff'}, {key: 'imageCount', label: '装配图像', color: '#409eff'},
  {key: 'total', label: '关联装配任务', color: '#606266'}, {key: 'completed', label: '已完成任务', color: '#67c23a'},
  {key: 'inProgress', label: '进行中任务', color: '#e6a23c'}, {key: 'abnormal', label: '资源未就绪', color: '#f56c6c'}
]
const loading = ref(false), loaded = ref(false), exporting = ref(false), exportProgress = ref('准备报告…')
const errorMessage = ref(''), statistics = ref({}), rows = ref([]), total = ref(0)
const pageNum = ref(1), pageSize = ref(12), appliedQuery = ref({})
const appliedDescription = computed(() => {
  const q = appliedQuery.value
  const parts = searchFields.filter(f => q[f.key]).map(f => `${f.label}：${q[f.key]}`)
  if (q.algResult != null) parts.push(algResultLabel(q.algResult))
  if (q.hasImage != null) parts.push(q.hasImage ? '有图像' : '无图像')
  return parts.join('；') || '全部产品、工序与检测结果'
})
const chartElements = []
let charts = [], observer, requestVersion = 0, disposed = false

function renderCharts(data) {
  const options = resultChartOptions(data)
  options.forEach((option, index) => {
    if (!charts[index]) {
      charts[index] = echarts.init(chartElements[index])
      observer?.observe(chartElements[index])
    }
    charts[index].setOption(option, true)
  })
}

function queryFromForm() {
  if (!dateRange.value || dateRange.value.length !== 2) throw new Error('请选择完整日期范围')
  const [startDate, endDate] = dateRange.value
  const diff = (Date.parse(endDate) - Date.parse(startDate)) / 86400000
  if (!Number.isFinite(diff) || diff < 0 || diff > 365) throw new Error('日期范围不能超过366天，开始日期不能晚于结束日期')
  return {
    ...Object.fromEntries(Object.entries(filters).map(([key, value]) => [key, typeof value === 'string' ? value.trim() || null : value === undefined ? null : value])),
    startDate, endDate, algResult: filters.algResult === '' ? null : filters.algResult ?? null,
    hasImage: filters.hasImage === '' ? null : filters.hasImage ?? null
  }
}

async function loadOverview(query) {
  const version = ++requestVersion
  loading.value = true;
  errorMessage.value = ''
  try {
    const {data} = await getResultOverview({...query, pageNum: 1, pageSize: pageSize.value})
    if (disposed || version !== requestVersion) return
    statistics.value = data.statistics;
    rows.value = data.rows;
    total.value = Number(data.total)
    appliedQuery.value = {...query};
    pageNum.value = 1;
    loaded.value = true
    await nextTick()
    if (!disposed) renderCharts(data.charts)
  } catch (error) {
    if (version === requestVersion) errorMessage.value = '查询失败，请重试。已显示的结果仍属于上一次成功查询。'
  } finally {
    if (version === requestVersion) loading.value = false
  }
}

function handleQuery() {
  if (exporting.value) return
  try {
    return loadOverview(queryFromForm())
  } catch (error) {
    ElMessage.warning(error.message)
  }
}

function handleReset() {
  Object.assign(filters, defaults());
  dateRange.value = lastDays(7).map(dateString);
  handleQuery()
}

function refreshApplied() {
  return loadOverview(appliedQuery.value)
}

async function changePage() {
  if (loading.value || exporting.value) return
  const version = ++requestVersion
  loading.value = true;
  errorMessage.value = ''
  try {
    const {data} = await getRecentResults({...appliedQuery.value, pageNum: pageNum.value, pageSize: pageSize.value})
    if (disposed || version !== requestVersion) return
    rows.value = data.rows;
    total.value = Number(data.total)
  } catch (error) {
    rows.value = [];
    errorMessage.value = '明细加载失败，请刷新当前结果。'
  } finally {
    if (version === requestVersion) loading.value = false
  }
}

function changeSize() {
  pageNum.value = 1;
  changePage()
}

async function handleExportPDF() {
  if (exporting.value || loading.value || !loaded.value) return
  exporting.value = true;
  exportProgress.value = '正在读取全部记录…'
  try {
    const {data} = await getResultReport({...appliedQuery.value})
    const {failures} = await createAssemblyResultPdf(data, {
      fetchImage: getResultImage,
      onProgress: (current, count) => {
        exportProgress.value = `正在生成 ${current}/${count}`
      }
    })
    if (failures.length) ElMessage.warning(`PDF已导出，其中${failures.length}条图像无法读取，已在报告中标注。`)
    else ElMessage.success('PDF报告已导出，包含全部匹配记录。')
  } catch (error) {
    ElMessage.error(error?.message || '报告导出失败，请重试')
  } finally {
    exporting.value = false
  }
}

function resizeCharts() {
  charts.forEach(chart => chart.resize())
}

onMounted(() => {
  observer = new ResizeObserver(resizeCharts);
  handleQuery();
  window.addEventListener('resize', resizeCharts)
})
onActivated(() => nextTick(resizeCharts))
onBeforeUnmount(() => {
  disposed = true;
  requestVersion++;
  observer?.disconnect();
  window.removeEventListener('resize', resizeCharts)
  charts.forEach(chart => chart.dispose());
  charts = []
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 18px;
}

.query-note {
  color: #737b86;
  font-size: 13px;
  line-height: 1.7;
}

.applied-query {
  margin: 16px 0;
  font-size: 14px;
  color: #495565;
  overflow-wrap: anywhere;
}

.status-alert {
  margin-bottom: 16px;
}

.stat-card {
  margin-bottom: 16px;
  color: #606266;
}

.stat-card strong {
  display: block;
  font-size: 30px;
  margin-top: 14px;
}

.charts {
  margin-top: 18px;
}

.charts .el-col {
  margin-bottom: 16px;
}

.chart {
  height: 350px;
  width: 100%;
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-column {
  margin-bottom: 16px;
}

.record-image {
  display: block;
  width: 100%;
  height: 210px;
  background: #f5f7fa;
}

.image-empty {
  display: flex;
  height: 210px;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.record-details {
  padding: 15px;
  font-size: 13px;
  line-height: 1.9;
  overflow-wrap: anywhere;
  color: #606266;
}

.record-details strong {
  color: #303133;
  font-size: 15px;
}

.record-tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  flex-wrap: wrap;
}

.ng-reason {
  color: #c45656;
  margin-top: 8px;
  max-height: 90px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.el-pagination {
  margin-top: 14px;
  flex-wrap: wrap;
}
</style>
