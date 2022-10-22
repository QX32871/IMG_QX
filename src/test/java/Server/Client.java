package Server;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 1919);
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        OutputStream os = socket.getOutputStream();
        PrintStream ps = new PrintStream(os);

        while (true) {
//            os.write(input.getBytes());
            ps.println(input);
            ps.flush();
        }

//        //接收服务器返回的信息
//        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        System.out.println(br.readLine());
    }
}
