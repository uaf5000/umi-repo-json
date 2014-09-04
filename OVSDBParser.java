package fiujson;

import java.io.IOException;
import java.util.TreeMap;

/*****************************************************************************
 * 
 * @author Omair F.
 *
 * Parser accepts JSON String Object of form {"method":"value"}, removes tokens,
 * formats key:value pairs and loads array as such:  |key|value|key|value|
 * Then searches for necessary key: value pairs  ex. id:001 Bridge:92fcdfc4-767f-4e35-941d-1f0695b85453
 * Note: Each key's value = key index + 1.
 * Note: Values in JSON Array's(collections) ex. [value:value:value] are not parsed and cannot be searched
 *
 *****************************************************************************/

public class OVSDBParser {

	final private static String S_ID ="id";
	final private static String S_BRIDGE = "bridge";
	
	private static String unparsedString="";
	private static int size = 0;
	private static String id;
	private static String bridgeUUID;
	private static String keyValueArray[];
	public static String serverResponse="";
	final static String ECHO_REPLY = "{\"id\":\"echo\",\"error\":null,\"result\":[]}";
	
	public OVSDBParser()
	{
		
	}
	
	static void parseJSONResponse()throws IOException
 	{   
 		int lCurly = 0;
 		int rCurly = 0;
 		
 	   while(OVSDBClient.connectionOpen)
 	   {
 		
 		  int streamChars = OVSDBClient.reader.read();  
 		  //Close connect if End of Stream character (any char < 0) is reached
 		  if (streamChars < 0){
	    		OVSDBClient.connectionOpen = false ;	
	    		System.out.println("\n\n***Open V-Switch has closed Connection***");    		
	    		OVSDBClient.disconnectServer();
	    	} 
 	
 		  serverResponse += (char)streamChars;
 		   
 		  //check for {} end of object
 		  if (streamChars == 123)	  
 			  lCurly++;
 		  else if (streamChars == 125)
 			  rCurly++;
 		  
 		  //TODO: Exception handling
 		  if (lCurly == rCurly)
 		  {		  		  
 			 //Dump Echo Request/Reply
 			 if(serverResponse.equalsIgnoreCase(ECHO_REPLY))
 				 serverResponse ="";
 			 else
 			  System.out.println("From Parser - RESPONSE: "+serverResponse);
 			  unparsedString = serverResponse.toLowerCase().replaceAll("[{}\"]","");
 			  keyValueArray = new String [getSize()];
 			  loadArray(keyValueArray);
 			  serverResponse ="";
 			  break;
 		  }	 
 	   }
 	}
	

	
	//loads array with key:value pairs
	static void loadArray(String arr[])
	{
		int iterator = 0;
		int index = 0;
		String key ="";
		
		while(iterator < unparsedString.length())
		{
			if((int)unparsedString.charAt(iterator) != 58 && (int)unparsedString.charAt(iterator) !=44)
			{
				key += unparsedString.charAt(iterator);
			}
			else if ((int)unparsedString.charAt(iterator) == 58 || (int)unparsedString.charAt(iterator) == 44)
			{
				arr[index] = key;
				key ="";
				index++;
			}
			iterator++;
		}
		
		//Tests Array values
		System.out.println("***Key:Value Array has been loaded***");
		
			for(int count =0; count < arr.length-1;count++ )
			{
				//System.out.println(keyValueArray[count]);
			}
		
	}
	public String getID()
	{	
		for (int count = 0; count < keyValueArray.length-1; count++)
		{
			if (keyValueArray[count].equalsIgnoreCase(S_ID))
			{
				id = keyValueArray[count+1];
				break;
			}
		}
		return id;
	}
	public String getBridgeUUID()
	{
		for (int count = 0; count < keyValueArray.length-1; count++)
		{
			if (keyValueArray[count].equalsIgnoreCase(S_BRIDGE))
			{
				bridgeUUID = keyValueArray[count+1];
				break;
			}
		}
		return bridgeUUID;
	}
	
	//unecessary
	public String getUnparsed()
	{
		return unparsedString;
	}
	
	public static int getSize()
	{
		int i = 0;
		int count = 0;
		while (i < unparsedString.length())
		{
			if ((int)unparsedString.charAt(i) == 58)
			{
				//System.out.print(unparsedString.charAt(i));
				count++;	
			}
			i++;	
		}
		
		count +=count *2;
		
		return count;
	}
	

}
