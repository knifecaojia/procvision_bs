// Shared by the dashboard and the PDF. Export charts are rendered from the report snapshot.
export function resultChartOptions(charts) {
  const trend = charts.trendData || { dates: [], records: [], images: [] }
  const ng = charts.ngStepStats || { categories: [], counts: [] }
  const pie = (data, color) => ({
    animation: false, color, tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{ type: 'pie', radius: ['35%', '62%'], center: ['50%', '44%'],
      label: { formatter: '{b}: {c}' }, data }]
  })
  return [
    { animation: false, tooltip: { trigger: 'axis' }, legend: { data: ['记录数', '图像数'] },
      grid: { left: 55, right: 25, top: 45, bottom: 60 },
      xAxis: { type: 'category', data: trend.dates }, yAxis: { type: 'value', minInterval: 1 },
      dataZoom: trend.dates.length > 31 ? [{ type: 'slider', start: 0, end: 100 }] : [],
      series: [{ name: '记录数', type: 'line', data: trend.records, itemStyle: { color: '#409eff' } },
        { name: '图像数', type: 'line', data: trend.images, itemStyle: { color: '#67c23a' } }] },
    pie(charts.statusData || [], ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#33cc99']),
    { animation: false, tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '40%', right: 40, top: 15, bottom: 35 },
      xAxis: { type: 'value', minInterval: 1 },
      yAxis: { type: 'category', inverse: true, data: ng.categories,
        axisLabel: { width: 210, overflow: 'truncate' } },
      series: [{ name: 'NG次数', type: 'bar', data: ng.counts, itemStyle: { color: '#f56c6c' }, label: { show: true, position: 'right' } }] },
    pie(charts.algResultData || [], ['#67c23a', '#f56c6c', '#909399', '#e6a23c'])
  ]
}

export const chartTitles = ['装配记录与图像趋势', '关联装配任务状态分布', '高频出错工步（前10项）', '算法检测结果占比']
export const algResultLabel = value => value != null && ({ 0: 'OK（合格）', 1: 'NG（不合格）', '-1': '执行失败' })[value] || '未检测/未知'
