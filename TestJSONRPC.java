package fiujson;

import java.io.IOException;

import org.json.simple.parser.ParseException;




public class TestJSONRPC {

	private static String ip ="172.16.1.1";
	private static String key ="001";
	private static String remote ="172.16.1.2";
	public static void main(String[] args) throws IOException, ParseException
	{
		boolean tunnelComplete = false;
		tunnelComplete = OVSDBClient.tunnel(ip,key,remote);
		
		if (tunnelComplete)
		{
			System.out.print("Tunnel Complete");
		}
	}

}
