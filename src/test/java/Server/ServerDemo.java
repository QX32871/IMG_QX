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

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(8081)) {
            System.out.println("wait for data.........");
            while (true) {
                theFullData(acceptData(ss), ss);


//                turnAndSaveFile(getDataInput);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String theFullData(String acceptData, ServerSocket ss) {
        List<String> cache = new ArrayList<>();
        int count = 1;
        while (isTheLast(acceptData)) {
            //这里的都不是文件尾，没有减号，直接存入集合里面
            cache.add(count, acceptData);
            count++;
            acceptData(ss);
        }
        //出了循环就是文件尾,删去结尾的-号之后存入集合
        //然后拼接字符串，返回
        cache.add(count, acceptData.substring(0, acceptData.length() - 1));
        //集合里面的元素拼接并转换成一个字符串，返回
        String[] fullData = cache.toArray(new String[cache.size()]);
        String returnData = fullData.toString();
        return returnData;
    }

    private static boolean isTheLast(String getDataInput) {
        if (getDataInput.charAt(getDataInput.length() - 1) == '-') {
            //还不是文件尾
            return true;
        } else {
            //是文件尾
            return false;
        }
    }

    private static String acceptData(ServerSocket ss) {
        String getDataInput;
        try {
            Socket socket = ss.accept();
            socket.setKeepAlive(true);  //连接有效性检测
            InputStream is = socket.getInputStream();
            byte[] byd = new byte[16384];
            int len = is.read(byd);
            getDataInput = new String(byd, 0, len);
            return getDataInput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void turnAndSaveFile(String getDataInput) {
        //转换并保存
        File file = new File("E://Code//img//" + num + ".jpeg");
        if (!file.exists()) {
            saveToImgFile(getDataInput.toUpperCase(), "E://Code//img//" + num + ".jpeg");
            num++;
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
