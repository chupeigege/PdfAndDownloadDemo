package vip.aquan.pdfdemo.util.pdf.itext;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

/**
    简单pdf导出
 */
public class PdfDemo {
    public static void main(String[] args) throws Exception {
        new PdfDemo().test6();
    }
    /**
     * @Author:         wgy
     * @CreateDate:     2020/1/3 15:55
     * @Description:    导出文字
     * @reture
     */
    public void test5() throws Exception {
        // 1.新建document对象
        Document document = new Document();
        // 2.建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
        // 创建 PdfWriter 对象 第一个参数是对文档对象的引用，第二个参数是文件的实际名称，在该名称中还会给出其输出路径。
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("e:\\test.pdf"));
        // 3.打开文档
        document.open();
        // 4.添加一个内容段落
        document.add(new Paragraph("Hello World!"));
        // 5.关闭文档
        document.close();
    }
    /**
     * @Author:         wgy
     * @CreateDate:     2020/1/3 15:55
     * @Description:    导出表格
     * @reture
     */
    public void test6() throws Exception {
        //创建文件
        Document document = new Document();
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("e:\\test.pdf"));
        //打开文件
        document.open();
        //添加内容
        document.add(new Paragraph("HD content here"));

        // 3列的表.
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100); // 宽度100%填充
        table.setSpacingBefore(10f); // 前间距
        table.setSpacingAfter(10f); // 后间距

        List<PdfPRow> listRow = table.getRows();
        //设置列宽
        float[] columnWidths = { 1f, 2f, 3f };
        table.setWidths(columnWidths);

        //行1
        PdfPCell cells1[]= new PdfPCell[3];
        PdfPRow row1 = new PdfPRow(cells1);

        //单元格
        cells1[0] = new PdfPCell(new Paragraph("111"));//单元格内容
        cells1[0].setBorderColor(BaseColor.BLUE);//边框验证
        cells1[0].setPaddingLeft(20);//左填充20
        cells1[0].setHorizontalAlignment(Element.ALIGN_CENTER);//水平居中
        cells1[0].setVerticalAlignment(Element.ALIGN_MIDDLE);//垂直居中

        cells1[1] = new PdfPCell(new Paragraph("222"));
        cells1[2] = new PdfPCell(new Paragraph("333"));

        //行2
        PdfPCell cells2[]= new PdfPCell[3];
        PdfPRow row2 = new PdfPRow(cells2);
        cells2[0] = new PdfPCell(new Paragraph("444"));

        //把第一行添加到集合
        listRow.add(row1);
        listRow.add(row2);
        //把表格添加到文件中
        document.add(table);

        //关闭文档
        document.close();
        //关闭书写器
        writer.close();
    }
    /**
     * @Author:         wgy
     * @CreateDate:     2020/1/3 15:55
     * @Description:    导出图片
     * @reture
     */
    public void test7() throws Exception {
        //创建文件
        Document document = new Document();
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\test.pdf"));
        //打开文件
        document.open();
        //添加内容
        document.add(new Paragraph("HD content here"));

        //图片1
        Image image1 = Image.getInstance("e:\\yr6.jpg");
        //设置图片位置的x轴和y周
        image1.setAbsolutePosition(100f, 550f);
        //设置图片的宽度和高度
        image1.scaleAbsolute(200, 200);
        //将图片1添加到pdf文件中
        document.add(image1);
/*

        //图片2
        Image image2 = Image.getInstance("C:\\Users\\admin\\Desktop\\1.png");
        //将图片2添加到pdf文件中
        document.add(image2);
*/

        //关闭文档
        document.close();
        //关闭书写器
        writer.close();
    }


}
