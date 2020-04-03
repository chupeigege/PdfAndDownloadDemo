package vip.aquan.pdfdemo.util.pdf;

import java.io.File;

/**
 * html转换pdf
 *
 * @author wcp
 * @date 2019/12/31
 */
public class HtmlToPdf {
    //wkhtmltopdf在系统中的路径
    private static final String toPdfToolWindows = "e:/wkhtmltopdf/bin/wkhtmltopdf.exe";
//    private static final String toPdfToolWindows = "F:/wkhtmltopdf/bin/wkhtmltopdf.exe";
    private static final String toPdfToolLinux = "wkhtmltopdf";

    /**
     * html转pdf
     * @param srcPath html路径，可以是硬盘上的路径，也可以是网络路径
     * @param destPath pdf保存路径
     * @return 转换成功返回true
     */
    public static boolean convert(String srcPath, String destPath){
        File file = new File(destPath);
        File parent = file.getParentFile();
        //如果pdf保存路径不存在，则创建路径
        if(!parent.exists()){
            parent.mkdirs();
        }
        StringBuilder cmd = new StringBuilder();
        String osName = System.getProperty("os.name");
        // Windows
        if (osName.startsWith("Windows")) {
            cmd.append(toPdfToolWindows);
        }
        // Linux
        else {
            cmd.append(toPdfToolLinux);
//            return String.format("/opt/wkhtmltopdf/bin/wkhtmltopdf %s %s", htmlFilePath, pdfFilePath);
        }

        cmd.append(" ");
//        cmd.append("  --header-line");//页眉下面的线
//        cmd.append("  --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
//        cmd.append("  --margin-top 30mm ");//设置页面上边距 (default 10mm)
//        cmd.append(" --header-spacing 10 ");//    (设置页眉和内容的距离,默认0)
//        cmd.append(" --debug-javascript ");//显示javascript调试输出的信息
//        cmd.append(" --javascript-delay 3000 ");//延迟一定的毫秒等待javascript 执行完成(默认值是200)
//        cmd.append(" --window-status completed ");//
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);

        boolean result = true;
        try{
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            new HtmlToPdfInterceptor(proc.getErrorStream()).start();
            new HtmlToPdfInterceptor(proc.getInputStream()).start();
            proc.waitFor();
        }catch(Exception e){
            result = false;
            e.printStackTrace();
        }

        return result;
    }
    public static void main(String[] args) {
//        HtmlToPdf.convert("https://fanyi.baidu.com/translate", "e:/wkhtmltopdf1.pdf");
        HtmlToPdf.convert("https://fanyi.baidu.com/translate", "e:/wkhtmltopdf2.pdf");
//        HtmlToPdf.convert("https://yey.efala.com/kindergarten/a/login;JSESSIONID=7d6d072bc98241b9b81a030277e71070", "e:/wkhtmltopdf1.pdf");
    }
}
