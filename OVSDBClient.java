package fiujson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;

/*****************************************************************************
 * 
 * @author Omair F.
 * 
 * This class is the main client that connects to the OVSDB server and performs
 * "Monitor", "Transact", and "Delete" requests to create and delete tunnels.
 * This client DOES NOT perform any other OVS requests "Get_Schema" etc.
 *
 *****************************************************************************/


public class OVSDBClient {

	
	final static int OVS_REMOTE_PORT = 6632;
	final static Timer rolex = new Timer();	
	final static MyTask echo = new MyTask();
	final static String ECHO_REPLY = "{\"id\":\"echo\",\"error\":null,\"result\":[]}";	
    private static String OVS_HOST;
	private static String serverRequest ="";
	public static PrintWriter writer;
	public static BufferedReader reader;
	public static OVSDBParser parser;
	public static boolean connectionOpen = true;
	public static Socket socket;
	public static int OVS_ID = 000;

	
	public static void connectToServer(String hostIP)
	{
		OVS_HOST = hostIP;
		try{
			System.out.println("***Openining Connection to: " +OVS_HOST+" on Port: "+OVS_REMOTE_PORT+"***\n");
			socket = new Socket(OVS_HOST, OVS_REMOTE_PORT);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			rolex.schedule(echo, 4000,4000); //keep alive request
		} catch (UnknownHostException e){
			System.out.print("Host Unknown Try Again");
		} catch (IOException e){
			System.out.println("IOException Found");
		} 
		
	}
	
	public static boolean tunnel(String hostIP, String tunnelKey, String remoteIP) throws IOException
	{
		//String remoteIP;
		String host = hostIP;
		String key = tunnelKey;
		String bridgeUUID;
		String remote = remoteIP;
		boolean incomplete = false;
	    OVSDBParser parser = new OVSDBParser();
		OVS_ID++;
		
		connectToServer(host);
		//Get Bridge UUID
		serverRequest=OVSDBCommands.monitor(OVS_ID);
		System.out.println("To Server - REQUEST: " + serverRequest);     
	        writer.println(serverRequest);
	 	    writer.flush();
	 	    OVSDBParser.parseJSONResponse();
	 	   bridgeUUID = parser.getBridgeUUID();
	 	
	 	//Create Tunnel
	 	OVS_ID++;
	 	serverRequest=OVSDBCommands.transact(OVS_ID, bridgeUUID, key, remote);
	 	System.out.println("To Server - REQUEST: " + serverRequest);     
	        writer.println(serverRequest);
	 	    writer.flush();
	 	    OVSDBParser.parseJSONResponse();
	 	    disconnectServer();
	 	    return !incomplete;
		
	}
	
	
	public static void disconnectServer() throws IOException
	{
	 		System.out.println("***Closing Connection to: " +OVS_HOST+" on port: "+OVS_REMOTE_PORT+"\n");
	    	socket.close();
	 		writer.close();
	 		reader.close();
	}
	
	//Keep alive request required by JSON RPC - runs on separate thread
    public static void jsonKeepAlive(){
	    String echoRequest = 
	   	"{\"method\":\"echo\",\"id\":\"echo\",\"params\":[]}";
	    System.out.println("***Connection Still Open: Sending Echo Request***");
		writer.println(echoRequest);
	 	writer.flush();
	 	    
	 	if (!connectionOpen){
	 	    rolex.cancel();
	 	    try {
				 disconnectServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 	    }
	}
	
}
