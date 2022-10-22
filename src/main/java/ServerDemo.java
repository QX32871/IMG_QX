

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerDemo {
    static int count = 0;
    static List<String> cache = new ArrayList<>();  //由于只需要用到一个集合，所以定义为全局变量，防止在循环中重复创建集合

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2221);
        while (true) {
            Socket socket = server.accept();
            System.out.println("Connected Waiting For Data......");
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    InputStream inputStream = socket.getInputStream();
                                    byte[] b = new byte[1024];
                                    int len;
                                    StringBuffer sb = new StringBuffer();
                                    //一次交互完成后，while循环过来，在此阻塞，即监听
                                    while ((len = inputStream.read(b)) != -1) {
                                        sb.append(new String(b, 0, len));
                                        //单次交互结束标识，跳出监听
                                        if (new String(b, 0, len).indexOf("####") >= 0) {
                                            break;
                                        }
//                                        else if (new String(b, 0, len).indexOf("!!!!") >= 0) {
//                                            //这个是作为解决粘包的方法，如果包尾是"!!!!"则表示一张图片的传输完成
//                                            break;//最后break出来跳出监听
//                                        }
                                    }
                                    String content = sb.toString();
//                                  System.out.println("接收到客户端消息" + content.substring(0, content.length() - 4));
                                    String InputData = content.substring(0, content.length() - 4);//去掉结束符的字符串
                                    System.out.println("接收到客户端消息" + InputData);
                                    //接下来开始拼接包,传下来的字符串都已经截去了终止符，所以无需再处理
                                    //首先要判断是不是尾包，约定好尾包有"----"，是文件尾的就不跳出处理并保存，不是文件尾的就只保存
                                    if (isTheLastData(InputData)) {
                                        //是尾包，截去文件尾标识符
                                        String lastPkgData = InputData.substring(0, InputData.length() - 4);
//                                        System.out.println(LastVerData);
                                        //存入集合并拼接数据，再调用方法转码，保存图片，最后清空集合，归零下标索引
                                        System.out.println("尾包的下标为:" + count + "数据为:" + lastPkgData);
                                        cache.add(count, lastPkgData);
                                        //拼接字符串
                                        String fullData = String.join("", cache);
                                        //转码,保存数据
                                        turnAndSaveFile(fullData);
                                        //释放资源
                                        count = 0;//归零索引
                                        cache.clear();//清空集合
                                    } else {
                                        //不是尾包，存入集合后继续接收数据
                                        System.out.println("将要存入集合下标为" + count + "的数据为" + InputData);
                                        cache.add(count, InputData);
                                        count++;
                                    }

                                    //往客户端发送数据
//                                    socket.getOutputStream().write(("The Data is:").getBytes(StandardCharsets.UTF_8));
//                                    socket.getOutputStream().flush();

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).start();
        }
    }

    /**
     * 判断是不是尾包
     *
     * @param inputData
     * @return
     */
    private static boolean isTheLastData(String inputData) {
        if (inputData.charAt(inputData.length() - 1) == '-') {
            //是文件尾
            return true;
        } else {
            //还不是文件尾
            return false;
        }
    }

    /**
     * 转码方法 字节转数字
     *
     * @param ch 要转的字节数组
     * @return 转好的数据
     */
    private static int charToInt(byte ch) {
        int val = 0;
        if (ch >= 0x30 && ch <= 0x39) {
            val = ch - 0x30;
        } else if (ch >= 0x41 && ch <= 0x46) {
            val = ch - 0x41 + 10;
        }
        return val;
    }

    /**
     * 将转好的字符串转换成一个图片文件
     *
     * @param src    输入的字符串
     * @param output 输出的字符串
     */
    public static void saveToImgFile(String src, String output) {
        if (src == null || src.length() == 0) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(output));
            byte[] bytes = src.getBytes();
            for (int i = 0; i < bytes.length; i += 2) {
                fileOutputStream.write(charToInt(bytes[i]) * 16 + charToInt(bytes[i + 1]));
            }
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 整合了转换方法直接把图片存在硬盘
     *
     * @param getDataInput 已经处理好的字符串
     */
    public static void turnAndSaveFile(String getDataInput) {
        int count = 1;
        //转换并保存
        File file = new File("E://Code//img//" + count + ".jpeg");
        if (!file.exists()) {
            saveToImgFile(getDataInput.toUpperCase(), "E://Code//img//" + count + ".jpeg");
            count++;
            System.out.println("已经创建了" + count + "个文件");
        }
    }
}
