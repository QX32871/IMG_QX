package Server;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        String input = in.next();
        Socket socket = new Socket("127.0.0.1", 8081);
        OutputStream os = socket.getOutputStream();
        os.write(input.getBytes());

//        //接收服务器返回的信息
//        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        System.out.println(br.readLine());

        socket.close();
    }
}
