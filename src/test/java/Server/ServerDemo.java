package Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerDemo {
    static int num = 1;
    static List<String> cache = new ArrayList<>();  //由于只需要用到一个集合，所以定义为全局变量，防止在循环中重复创建集合

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(1919)) {
            System.out.println("wait for data.........");
            Socket socket = ss.accept();
            while (true) {
//                turnAndSaveFile(theFullData(acceptData(ss), ss));
                String acceptData = acceptData(ss, socket);
                System.out.println("第一次连接传入的字符串:" + acceptData);
                String theFullData = theFullData(acceptData, ss, socket);
                System.out.println("拼接完毕的字符串:" + theFullData);
                turnAndSaveFile(theFullData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String theFullData(String acceptData, ServerSocket ss, Socket socket) {
        int count = 0;
        while (isTheLast(acceptData)) {
            //这里的都不是文件尾，没有减号，直接存入集合里面
            System.out.println("第" + count + "次进入循环的字符串:" + acceptData);
            cache.add(count, acceptData);
            count++;
            String nextData = acceptData(ss, socket);
            System.out.println("下一条数据:" + nextData);
            acceptData = nextData;
        }

        //出了循环就是文件尾,删去结尾的-号之后存入集合
        //然后拼接字符串，返回
        cache.add(count, acceptData.substring(0, acceptData.length() - 1));
        //集合里面的元素拼接并转换成一个字符串，返回
        String[] fullData = cache.toArray(new String[cache.size()]);
        String returnData = fullData.toString();
        cache.clear();  //每处理完一张图片就清理集合中的内容
        return returnData;
    }

    private static boolean isTheLast(String getDataInput) {
        if (getDataInput.charAt(getDataInput.length() - 1) == '-') {
            //是文件尾
            return false;
        } else {
            //还不是文件尾
            return true;
        }
    }

    private static String acceptData(ServerSocket ss, Socket socket) {
        String getDataInput;
        String cacheData;
        int count = 1;
        try (InputStream is = socket.getInputStream()) {
            byte[] bytes = new byte[1024];
            int len = is.read(bytes); //出错！将第一次传入的数据处理完之后并不会再从客户端接受新数据，故从第n+1次（接收第一次数据需要n次）请求输入流的时候无法得到数据
            getDataInput = new String(bytes, 0, len);
            cacheData = getDataInput;
//            return cacheData.equals(getDataInput) ? getNextData(ss, socket) : getDataInput;

            return getDataInput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static String getNextData(ServerSocket ss, Socket socket) throws Exception {
//        ss.accept();
//        InputStream is = socket.getInputStream();
//        byte[] bytes = new byte[1024];
//        int len = is.read(bytes);
//        String getNextData = new String(bytes, 0, len);
//        ss.close();
//        return getNextData;
//    }

    private static void turnAndSaveFile(String getDataInput) {
        //转换并保存
        File file = new File("E://Code//img//" + num + ".jpeg");
        if (!file.exists()) {
            saveToImgFile(getDataInput.toUpperCase(), "E://Code//img//" + num + ".jpeg");
            num++;
            System.out.println("已经创建了" + num + "个文件");
        }
    }


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

    private static int charToInt(byte ch) {
        int val = 0;
        if (ch >= 0x30 && ch <= 0x39) {
            val = ch - 0x30;
        } else if (ch >= 0x41 && ch <= 0x46) {
            val = ch - 0x41 + 10;
        }
        return val;
    }
}
