import java.net.*;
import java.io.*;

public class GroupChatClient implements Runnable {
    private BufferedReader fromUser;
    private PrintWriter toSocket;
    public static String name;

    public GroupChatClient(BufferedReader reader, PrintWriter writer) {
        fromUser =  reader;
        toSocket = writer;
    }

    public void run() {
        try {
            while (true) {
                String line = fromUser.readLine();
                if (line == null) {
                    System.out.println("***Client Closing Connection***");
					break;
                }
                toSocket.println(name + ": " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        System.exit(0);
    }
    public static void main(String args[]) {
        if (args.length != 3) {
            System.out.println("usage: java GroupChatClient <host name> <port number> <client name>");
            System.exit(1);
        }
        name = args[2];
        Socket socket = null;
        try {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            System.out.println("Connected to server at " + args[0] + ":" + args[1]);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                Thread child = new Thread(new GroupChatClient(fromUser, toSocket));
                child.run();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    
    }    
}

