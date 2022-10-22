import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1919); //注册端口
            Socket socket = serverSocket.accept();  //调用accept方法等待接收客户端的信息，建立socket通信管道
            InputStream is = socket.getInputStream();//从socket通信管道中得到一个字节输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(is));//把字节输入流包装成缓冲字符输入流进行信息的接收
            //设定字符集为UTF-8
            //按行读取消息
            String msg;
            while ((msg = br.readLine()) != null) {
                System.out.println(socket.getRemoteSocketAddress());
                System.out.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
