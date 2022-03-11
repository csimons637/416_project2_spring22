/**
 * @author Charles Simons - csimons637@gmail.com
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class GroupChatServer implements Runnable
{
	private Socket clientSock;
	private static List<PrintWriter> clientList;

	/**
     * Constructs the chat server and sets the socket
     * @param sock - The socket with which to communicate
     */
    public GroupChatServer(Socket sock) {
		clientSock = sock;
        clientList = new ArrayList<PrintWriter>();
	}

	/**
     * Adds the input client from list of clients
     * @param toClient - The client to add to the list
     * @return - The list, now with "toClient"
     */
    public static synchronized boolean addClient(PrintWriter toClient)
	{
		return(clientList.add(toClient));
	}

	/**
     * Removes the input client from list of clients
     * @param toClient - The client to remove from the list
     * @return - The list, now without "toClient"
     */
    public static synchronized boolean removeClient(PrintWriter toClient)
	{
		return(clientList.remove(toClient));
	}

	/**
     * Relays the input message to all clients, except the input client
     * @param fromClient - The sender/client that doesn't get the message
     * @param msg - The sender's message
     */
    public static synchronized void relayMessage(PrintWriter fromClient, String msg)
	{
        System.out.println(msg);
        for (PrintWriter c : clientList) {
            c.println(msg);
        }
	}

    /**
     * Adds and removes clients from clients list
     * Reads client input and relays to other listed clients
     */
	public void run()
	{
		try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            PrintWriter toClient = new PrintWriter(clientSock.getOutputStream(), true);

			while (true) {
                addClient(toClient);

                String msg = null;
                msg = fromClient.readLine();
                if (msg == null) { // If null, client quit or EOF
                    System.out.println("***Client Quit | Closing Connection***");
                    break;
                }
                relayMessage(toClient, msg);
			}

            removeClient(toClient);
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	public static void main(String args[])
	{
		if (args.length != 1) {
			System.out.println("usage: java GroupChatServer <server port>");
			System.exit(1);
		}

		Socket clientSock = null;
		try {
            ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Awaiting Connections...");
            while (true) {
                clientSock = server.accept();
                System.out.println("Connected to a client at ('" +
                    ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getAddress().getHostAddress()
                    + "', '" +
                    ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getPort()
                    + "')");
                Thread child = new Thread(new GroupChatServer(clientSock));
                child.start();
			}
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}
}
