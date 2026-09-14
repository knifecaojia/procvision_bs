import jsPDF from 'jspdf'
import * as echarts from 'echarts'
import { resultChartOptions, chartTitles, algResultLabel } from './assemblyResultCharts'

const WIDTH = 1240
const HEIGHT = 1754
const MARGIN = 76
const BOTTOM = HEIGHT - 100
const fontFamily = '"Microsoft YaHei", "Noto Sans CJK SC", sans-serif'

function loadImage(source) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    const timer = setTimeout(() => { img.src = ''; reject(new Error('图片解码超时')) }, 30000)
    img.onload = () => { clearTimeout(timer); resolve(img) }
    img.onerror = () => { clearTimeout(timer); reject(new Error('图片无法解码')) }
    img.src = source
  })
}

/** Sequential A4 rendering bounds canvas memory, embeds Chinese text and images for offline reading.
 *  No presigned URLs are used in the PDF, and no page is cut through a screenshot. */
export async function createAssemblyResultPdf(report, { fetchImage, onProgress = () => {}, save = true } = {}) {
  if (!report || !Array.isArray(report.rows) || report.rows.length !== Number(report.total)) {
    throw new Error('报告明细不完整，请重新查询后导出')
  }
  await document.fonts?.ready
  const pdf = new jsPDF({ orientation: 'portrait', unit: 'pt', format: 'a4', compress: true })
  const canvas = document.createElement('canvas')
  canvas.width = WIDTH; canvas.height = HEIGHT
  const ctx = canvas.getContext('2d')
  let y = MARGIN, pages = 0
  const failures = []
  function blank() {
    ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, WIDTH, HEIGHT); y = MARGIN
  }
  function flush() {
    ctx.font = `20px ${fontFamily}`; ctx.fillStyle = '#687582'
    ctx.fillText(`产品装配多媒体报告  |  ${report.generatedAt}  |  第 ${pages + 1} 页`, MARGIN, HEIGHT - 48)
    if (pages++) pdf.addPage()
    pdf.addImage(canvas.toDataURL('image/jpeg', 0.88), 'JPEG', 0, 0, 595.28, 841.89)
    blank()
  }
  function text(value, size = 25, color = '#263445') {
    const content = String(value ?? '—')
    const lineHeight = Math.ceil(size * 1.6)
    ctx.font = `${size}px ${fontFamily}`
    for (const paragraph of content.split('\n')) {
      let line = ''
      const draw = () => {
        if (y + lineHeight > BOTTOM) flush()
        ctx.font = `${size}px ${fontFamily}`; ctx.fillStyle = color
        ctx.fillText(line, MARGIN, y + size); y += lineHeight
      }
      for (const char of paragraph) {
        if (ctx.measureText(line + char).width > WIDTH - MARGIN * 2) { draw(); line = char }
        else line += char
      }
      draw()
    }
  }
  function drawContained(img, height) {
    if (y + height > BOTTOM) flush()
    const ratio = Math.min((WIDTH - MARGIN * 2) / img.width, height / img.height)
    const w = img.width * ratio, h = img.height * ratio
    ctx.drawImage(img, (WIDTH - w) / 2, y + (height - h) / 2, w, h)
    y += height + 15
  }
  blank()
  text('产品装配多媒体数据报告', 44)
  text(`生成时间：${report.generatedAt}`, 23, '#687582')
  text('一、查询范围与统计', 32)
  const q = report.query
  text(`记录时间：${q.startDate} 至 ${q.endDate}`)
  for (const [key, label] of [['product', '产品名称/编码'], ['prodBatchNo', '生产批次'], ['workOrderCode', '工单'], ['process', '工序名称/编码'], ['step', '工步名称/编码']]) {
    text(`${label}：${q[key] || '全部'}`)
  }
  text(`检测结果：${q.algResult == null ? '全部' : algResultLabel(q.algResult)}`)
  text(`图像条件：${q.hasImage == null ? '全部记录' : q.hasImage ? '有图像' : '无图像'}`)
  const s = report.statistics
  text(`匹配记录 ${s.recordCount} 条，图像 ${s.imageCount} 张，关联装配任务 ${s.total} 个。`, 28)
  text(`任务状态：已完成 ${s.completed}；进行中 ${s.inProgress}；待派单 ${s.pending}；资源未就绪 ${s.abnormal}。`)
  text('统计口径：按记录提交时间筛选，提交时间为空时取创建时间。任务按工单编码与工序编码去重，状态为查询时的当前状态；未匹配任何记录的任务不计入。', 22, '#687582')
  text('图像数表示记录中保存了图像路径的数量；不可读取的图像会在明细及报告末尾标注。报告包含本次查询的全部匹配记录。', 22, '#687582')
  flush()
  const options = resultChartOptions(report.charts)
  for (let i = 0; i < options.length; i++) {
    text(chartTitles[i], 30)
    const element = document.createElement('div')
    const chart = echarts.init(element, null, { renderer: 'canvas', width: 1080, height: 510, devicePixelRatio: 1.5 })
    try {
      chart.setOption(options[i])
      drawContained(await loadImage(chart.getDataURL({ type: 'png', pixelRatio: 1.5, backgroundColor: '#fff' })), 550)
    } finally { chart.dispose() }
    if (i % 2 === 1) flush()
  }
  for (let i = 0; i < report.rows.length; i++) {
    const row = report.rows[i]
    onProgress(i + 1, report.rows.length)
    text(`装配记录 ${i + 1} / ${report.rows.length}`, 34)
    text(`记录编号：${row.id}    记录时间：${row.recordTime || '—'}`, 23)
    if (row.hasImage) {
      let objectUrl
      try {
        const blob = await fetchImage(row.id)
        if (!blob.size || blob.type.includes('json')) throw new Error('图片服务未返回有效图像')
        objectUrl = URL.createObjectURL(blob)
        drawContained(await loadImage(objectUrl), 620)
      } catch (error) {
        failures.push({ id: row.id, reason: '图片缺失、不可读取或格式不支持' })
        text('图像未能载入：图片缺失、不可读取或格式不支持。', 27, '#bd3030')
      } finally { if (objectUrl) URL.revokeObjectURL(objectUrl) }
    } else text('此记录未保存图像。', 26, '#687582')
    for (const [label, value] of [
      ['产品名称', row.materialName], ['产品编码', row.materialNo], ['生产批次', row.prodBatchNo],
      ['生产订单', row.prodOrderNo], ['工单编码', row.workOrderCode],
      ['工序', `${row.processName || '—'} / ${row.processCode || '—'}`],
      ['工步', `${row.stepName || '—'} / ${row.stepCode || '—'}`],
      ['工步状态', Number(row.stepStatus) === 2 ? '已完成' : '未完成'],
      ['检测结果', algResultLabel(row.algResult)], ['装配人员', row.workerName], ['异常原因', row.ngReason]
    ]) text(`${label}：${value || '—'}`, 23)
    flush()
    // Allow progress updates and avoid blocking the page during large exports.
    await new Promise(resolve => setTimeout(resolve, 0))
  }
  text('报告完整性说明', 34)
  text(`已收录全部 ${report.total} 条匹配记录；有图像路径 ${s.imageCount} 条；图像读取失败 ${failures.length} 条。`)
  if (!report.rows.length) text('当前筛选条件下没有装配记录。')
  for (const failure of failures) text(`记录 ${failure.id}：${failure.reason}`, 23, '#bd3030')
  text('PDF嵌入的是按报告分辨率生成的图片，可离线查看；完整分辨率原图仍保存在系统中。', 23)
  flush()
  const pageCount = pdf.getNumberOfPages()
  for (let i = 1; i <= pageCount; i++) {
    pdf.setPage(i); pdf.setFontSize(8); pdf.setTextColor(105)
    pdf.text(`${i} / ${pageCount}`, 553, 823, { align: 'right' })
  }
  if (save) await pdf.save(`装配多媒体报告_${q.startDate}_${q.endDate}_${Date.now()}.pdf`, { returnPromise: true })
  canvas.width = 0; canvas.height = 0
  return { pdf, failures, pageCount }
}
