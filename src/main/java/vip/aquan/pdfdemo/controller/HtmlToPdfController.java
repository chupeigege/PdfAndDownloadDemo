package vip.aquan.pdfdemo.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.aquan.pdfdemo.util.pdf.HtmlToPdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 导出pdf接口，需要wkhtmltopdf插件，插件位置在HtmlToPdf类中配置
 * linux环境下装wkhtmltopdf：https://blog.csdn.net/u012561176/article/details/86487664
 *
 * @author wcp
 * @date 2019/12/25
 */
@Controller
public class HtmlToPdfController {

    private static ExecutorService pdfExecutor = new ThreadPoolExecutor(10, 50, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new NameTreadFactory("pdf export"));

    private static final String PDF_RELATIVE_PATH = File.separator + "static" + File.separator + "pdf";

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>Title</title>\n</head>\n<body>\n<h1>哈哈哈哈哈</h1>\n</body>\n</html>", "UTF-8"));
    }

    /**
     * 导出pdf
     *
     */
    @ResponseBody
    @PostMapping("exportPdf")
    public String exportPdf(@RequestParam(value = "content") String content,@RequestParam(value = "name",required = false) String name, HttpServletRequest request) throws Exception {
//        String content = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>Title</title>\n</head>\n<body>\n<h1>哈哈哈哈哈</h1>\n</body>\n</html>";

        /*
        导出分页时出现图表被切割，在页面加以下代码可解决
        <style>
          div{
              page-break-inside: avoid;
          }
          </style>
         */
        if (StringUtils.isBlank(name)) {
            name = UUID.randomUUID().toString().replace("-", "");
        }
        String htmlName = name + ".html";

        String realPath = request.getSession().getServletContext().getRealPath(PDF_RELATIVE_PATH);
//        String contextPath = request.getSession().getServletContext().getContextPath();

        File file = new File(realPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        final String htmlPath = realPath + File.separator + htmlName;
        //  页面内容 转 静态文件
        boolean isToHtmlFile = contentToHtmlFile(htmlPath, URLDecoder.decode(content, "UTF-8"));

        if (!isToHtmlFile) {
            return "导出失败";
        }

        String pdfName = name + ".pdf";
        //磁盘路径
        final String destPath = realPath + File.separator + pdfName;

        //  html 生成 pdf文件
        pdfExecutor.execute(() -> {
            try {
                // 要进行的并发操作
                // 可以是磁盘路径，也可以是网络路径
                HtmlToPdf.convert(htmlPath, destPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //下载路径
        String filePath = PDF_RELATIVE_PATH + File.separator + pdfName;
        filePath = filePath.replaceAll("\\\\", "/");
        return filePath;
    }

    /**
     * 文件下载
     * originalFileUrl 文件的相对链接
     * aliasFileName  下载到的文件的自定义名称（允许为空）
     */
    @RequestMapping("downLoad")
    public void downLoad(String originalFileUrl, String aliasFileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        originalFileUrl = URLDecoder.decode(originalFileUrl, "UTF-8");
        response.setContentType("multipart/form-data");

        originalFileUrl = request.getHeader("host") + request.getServletContext().getContextPath() + originalFileUrl;
        String fileName;
        if (StringUtils.isBlank(aliasFileName)) {
            fileName = originalFileUrl.substring(originalFileUrl.lastIndexOf("/") + 1);
        } else {
            fileName = aliasFileName + originalFileUrl.substring(originalFileUrl.lastIndexOf("."));
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        URLConnection urlConnection = null;
        BufferedOutputStream outputStream = null;
        BufferedInputStream inputStream = null;
        try {
            // 将下载的文件名进行编码，因为URL对象不能接受中文链接
            originalFileUrl = originalFileUrl.substring(0, originalFileUrl.lastIndexOf("/") + 1) +
                    URLEncoder.encode(originalFileUrl.substring(originalFileUrl.lastIndexOf("/") + 1), "UTF-8");
            urlConnection = new URL("http://" + originalFileUrl).openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            outputStream = new BufferedOutputStream(response.getOutputStream());

            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            response.reset();
            response.setHeader("content-type", "text/html;charset=utf-8");
            response.getWriter().println("无效链接：" + originalFileUrl);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 输出html文件
     *
     * @param filePath
     * @param content
     * @return
     */
    private boolean contentToHtmlFile(String filePath, String content) {
        OutputStreamWriter os = null;
        BufferedWriter bw = null;
        try {

            File file = new File(filePath);
            os = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            bw = new BufferedWriter(os);
            bw.write(content);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

    /**
     * 线程工厂
     */
    static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);
        private String type;

        private NameTreadFactory(String type) {
            this.type = type;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, type + "-thread-" + mThreadNum.getAndIncrement());
        }
    }

}
