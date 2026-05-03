import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

export const downloadPDF = async (el, title = '导出文件') => {
    if (!el) return

    // 1. 使用 html2canvas 将 DOM 转为 Canvas
    const canvas = await html2canvas(el, {
        useCORS: true, // 允许图片跨域
        scale: 2,      // 提高分辨率，解决清晰度问题
        backgroundColor: '#ffffff'
    })

    const contentWidth = canvas.width
    const contentHeight = canvas.height

    // 2. 计算 PDF 页面比例 (A4 纸张)
    const pageHeight = (contentWidth / 592.28) * 841.89
    let leftHeight = contentHeight
    let position = 0
    const imgWidth = 595.28
    const imgHeight = (592.28 / contentWidth) * contentHeight

    const pageData = canvas.toDataURL('image/jpeg', 1.0)
    const pdf = new jsPDF('', 'pt', 'a4')

    // 3. 处理分页逻辑
    if (leftHeight < pageHeight) {
        pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight)
    } else {
        while (leftHeight > 0) {
            pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
            leftHeight -= pageHeight
            position -= 841.89
            if (leftHeight > 0) {
                pdf.addPage()
            }
        }
    }

    // 4. 保存文件
    pdf.save(`${title}.pdf`)
}