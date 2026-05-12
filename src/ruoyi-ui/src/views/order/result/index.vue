<template>
  <div class="app-container" id="pdf-content">
    <el-row :gutter="10" class="mb8" style="justify-content: flex-start;" data-html2canvas-ignore="true">
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExportPDF"
        >导出 PDF 报告
        </el-button>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="panel-group">
      <el-col :span="6" class="card-panel-col">
        <el-card shadow="hover" class="box-card">
          <div class="card-header">
            <el-icon class="icon-blue">
              <Document/>
            </el-icon>
            <span>总装配任务</span>
          </div>
          <div class="card-value">{{ statistics.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6" class="card-panel-col">
        <el-card shadow="hover" class="box-card">
          <div class="card-header">
            <el-icon class="icon-green">
              <CircleCheck/>
            </el-icon>
            <span>已完成任务</span>
          </div>
          <div class="card-value">{{ statistics.completed }}</div>
        </el-card>
      </el-col>
      <el-col :span="6" class="card-panel-col">
        <el-card shadow="hover" class="box-card">
          <div class="card-header">
            <el-icon class="icon-orange">
              <Loading/>
            </el-icon>
            <span>进行中任务</span>
          </div>
          <div class="card-value">{{ statistics.inProgress }}</div>
        </el-card>
      </el-col>
      <el-col :span="6" class="card-panel-col">
        <el-card shadow="hover" class="box-card">
          <div class="card-header">
            <el-icon class="icon-red">
              <Warning/>
            </el-icon>
            <span>资源未就绪</span>
          </div>
          <div class="card-value">{{ statistics.abnormal }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>近7天装配任务趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 350px;"/>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>近7天任务状态分布</span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 350px;"/>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>高频出错工步</span>
            </div>
          </template>
          <div ref="ngStepChartRef" style="height: 350px;" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>算法检测结果占比</span>
            </div>
          </template>
          <div ref="algResultChartRef" style="height: 350px;"/>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>最新装配工步结果</span>
              <el-button style="float: right; padding: 3px 0" link type="primary" icon="Refresh"
                         @click="fetchRecentResults">刷新
              </el-button>
            </div>
          </template>

          <el-row :gutter="20" v-loading="loadingResults">
            <el-col :span="4" v-for="(item, index) in recentStepResults" :key="index" style="margin-bottom: 20px;">
              <el-card :body-style="{ padding: '0px' }" shadow="never" class="result-card">
                <el-image
                    :src="item.imgUrl"
                    :preview-src-list="[item.imgUrl]"
                    fit="cover"
                    class="image"
                    lazy
                    :preview-teleported="true"
                >
                  <template #error>
                    <div class="image-slot">
                      <el-icon>
                        <Picture/>
                      </el-icon>
                    </div>
                  </template>
                </el-image>
                <div style="padding: 14px;">
                  <span class="step-title">{{ item.stepName }}</span>
                  <div class="bottom-info">
                    <span class="order-code">{{ item.workOrderCode }}</span>
                    <el-tag :type="item.stepStatus === 2 ? 'success' : 'danger'" size="small">
                      {{ item.stepStatus === 2 ? '已完成' : '未完成' }}
                    </el-tag>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="AssemblyDashboard">
import {ref, onMounted, onBeforeUnmount, nextTick} from 'vue'
import * as echarts from 'echarts'
import {getKpiStats, getChartAnalysis, getRecentResults} from '@/api/order/result.js'
import {CircleCheck, Document, Loading, Picture, Warning} from "@element-plus/icons-vue";
import {downloadPDF} from "@/utils/pdf.js";

// --- 图表引用与实例 ---
const trendChartRef = ref(null)
const statusChartRef = ref(null)
const algResultChartRef = ref(null) // 新增：算法结果图表
const ngStepChartRef = ref(null) // 新增：异常工步图表 DOM 引用
let ngStepChart = null           // 新增：异常工步 ECharts 实例
let trendChart = null
let defectChart = null
let statusChart = null
let algResultChart = null // 新增

// --- 响应式数据 ---
const loadingResults = ref(false)
const chartLoading = ref(true)

const statistics = ref({
  total: 0,
  completed: 0,
  inProgress: 0,
  abnormal: 0
})
const recentStepResults = ref([])

const handleExportPDF = () => {
  const el = document.getElementById('pdf-content')
  // 导出前可以根据需要暂时隐藏某些不希望出现在PDF里的UI元素（如按钮）
  downloadPDF(el, `装配结果分析报告_${new Date().getTime()}`)
}

// --- 1. 图表初始化 (仅配置基础样式，不包含数据) ---
const initCharts = () => {
  if (!trendChartRef.value || !statusChartRef.value)
    return;
  // 趋势图基础配置
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: {trigger: 'axis'},
    legend: {data: ['当天开始任务数', '当天完成任务数']},
    grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
    xAxis: {type: 'category', boundaryGap: false, data: []}, // 数据留空
    yAxis: {type: 'value'},
    series: [
      {name: '当天开始任务数', type: 'line', smooth: true, itemStyle: {color: '#409EFF'}, data: []},
      {name: '当天完成任务数', type: 'line', smooth: true, itemStyle: {color: '#67C23A'}, data: []}
    ]
  })

  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: {trigger: 'item'},
    legend: {top: '5%', left: 'center'},
    series: [
      {
        name: '任务状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {show: false, position: 'center'},
        emphasis: {
          label: {show: true, fontSize: 18, fontWeight: 'bold'}
        },
        labelLine: {show: false},
        // 这里留空，等待后端数据填入
        data: []
      }
    ]
  })

  algResultChart = echarts.init(algResultChartRef.value)
  algResultChart.setOption({
    tooltip: {trigger: 'item'},
    legend: {top: '5%', left: 'center'},
    // 预设颜色：绿色(OK), 红色(NG), 灰色(执行失败)
    color: ['#67C23A', '#F56C6C', '#909399'],
    series: [
      {
        name: '算法结果',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {show: false, position: 'center'},
        emphasis: {
          label: {show: true, fontSize: 18, fontWeight: 'bold'}
        },
        labelLine: {show: false},
        data: [] // 等待后端填入
      }
    ]
  })

  if (ngStepChartRef.value) {
    ngStepChart = echarts.init(ngStepChartRef.value)
    ngStepChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: [],
        axisLabel: { interval: 0, rotate: 15 } // 名称太长时倾斜显示防止重叠
      },
      yAxis: { type: 'value', name: '检测失败次数' },
      series: [
        {
          name: '出错频次',
          type: 'bar',
          barWidth: '40%', // 控制柱子宽度
          itemStyle: {
            color: '#F56C6C', // 使用红色警示色
            borderRadius: [4, 4, 0, 0] // 柱子顶部设为圆角，更美观
          },
          data: [],
          label: { show: true, position: 'top' } // 在柱子顶部显示具体数值
        }
      ]
    })
  }
}

// --- 2. 异步获取数据并更新图表 ---
const fetchDashboardData = async () => {
  chartLoading.value = true
  trendChart?.showLoading()
  statusChart?.showLoading()
  algResultChart?.showLoading() // 新增 Loading
  ngStepChart?.showLoading()

  try {
    // 1. 获取 KPI 数据
    const kpiRes = await getKpiStats()
    if (kpiRes.code === 200) {
      statistics.value = kpiRes.data
    }

    // 2. 获取图表数据
    const chartRes = await getChartAnalysis({timeRange: '7days'})
    if (chartRes.code === 200) {
      const {trendData, statusData, algResultData, ngStepStats} = chartRes.data

      algResultChart?.setOption({
        series: [{
          data: algResultData
        }]
      })

      ngStepChart?.setOption({
        xAxis: { data: ngStepStats.categories }, // X轴：工步名称
        series: [{ data: ngStepStats.counts }]   // Y轴：NG数量
      })


      // 动态更新趋势图数据
      trendChart?.setOption({
        xAxis: {data: trendData.dates}, // e.g., ['周一', '周二', ...]
        series: [
          {data: trendData.planned},    // e.g., [120, 132, ...]
          {data: trendData.actual}      // e.g., [110, 120, ...]
        ]
      })

      statusChart?.setOption({
        series: [{
          data: statusData
        }]
      })
    }

    // 3. 获取底部图片结果流
    await fetchRecentResults()

  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    chartLoading.value = false
    trendChart?.hideLoading()
    statusChart?.hideLoading()
    algResultChart?.hideLoading() // 关闭 Loading
    ngStepChart?.hideLoading()
  }
}

// 单独抽离获取图片的逻辑，方便局部刷新
const fetchRecentResults = async () => {
  loadingResults.value = true
  try {
    const res = await getRecentResults()
    if (res.code === 200) {
      recentStepResults.value = res.data
    }
  } finally {
    loadingResults.value = false
  }
}

// --- 生命周期与自适应 ---
const handleResize = () => {
  if (trendChart) trendChart.resize()
  if (defectChart) defectChart.resize()
  if (algResultChart) algResultChart.resize() // 新增
  if (ngStepChart) ngStepChart.resize()
}

onMounted(() => {
  nextTick(() => {
    // 如果大屏在 el-tab 或 el-dialog 中，可以加一个 setTimeout 缓冲
    setTimeout(() => {
      initCharts()
      fetchDashboardData()
    }, 100)
  })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) trendChart.dispose()
  if (defectChart) defectChart.dispose()
  if (algResultChart) algResultChart.dispose() // 新增
  if (ngStepChart) ngStepChart.dispose()
})
</script>

<style scoped>
.panel-group {
  margin-top: 18px;
}

.card-panel-col {
  margin-bottom: 32px;
}

.box-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #606266;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 20px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-top: 15px;
  text-align: center;
}

/* 图标颜色 */
.icon-blue {
  color: #409EFF;
}

.icon-green {
  color: #67C23A;
}

.icon-orange {
  color: #E6A23C;
}

.icon-red {
  color: #F56C6C;
}

/* 结果卡片样式 */
.result-card {
  transition: all 0.3s;
}

.result-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.image {
  width: 100%;
  height: 180px;
  display: block;
  background-color: #f5f7fa;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 30px;
}

.step-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  display: block;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bottom-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-code {
  font-size: 12px;
  color: #909399;
}
</style>