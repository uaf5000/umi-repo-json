package fiujson;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/*****************************************************************************
 * 
 * @author Omair F.
 *
 * Contains JSON String commands for modifying the OVSDB Server
 * Current Strings:
 * 
 * Monitor Bridge - Requests Bridge info
 * Transact - Requests GRE interface creation
 * 
 *
 *****************************************************************************/


public class OVSDBCommands {
	

	public static String monitorOut;
	public static String bridgeUUIDOut;
	public static String requestOut; 
	public static String echoRequestOut;
	public static String transactOut;
	
	
	public static String monitor(int OVS_ID) throws IOException//, JSONRPC2ParseException
	{
		   
		   int id = OVS_ID;
		
		   Map<String,Boolean> selectVals=new LinkedHashMap<String,Boolean>();
		   selectVals.put("initial",true);
		   //selectVals.put("insert",true);
		   //selectVals.put("delete",true);
		   //selectVals.put("modify",true);
		  
		   JSONObject select = new JSONObject();
		   select.put("select",selectVals);
		
		   
		   JSONObject monitorVals = new JSONObject();
		   //monitorVals.put("Open_vSwitch", select);
		   monitorVals.put("Bridge", select);
		   //monitorVals.put("Port",select);
		   //monitorVals.put("Interface", select);
		
		   JSONArray monitorArray = new JSONArray();
		   monitorArray.add("Open_vSwitch");
		   monitorArray.add(new Integer(0));
		   monitorArray.add(monitorVals);
		   

		   Map monitor=new LinkedHashMap();
		   monitor.put("method","monitor");
		   monitor.put("id",id);
		   monitor.put("params",monitorArray);
		   
		   StringWriter out = new StringWriter();
		   JSONValue.writeJSONString(monitor, out);
		   String monitorOut = out.toString();
		   
		  return monitorOut;
	}
	public static String transact(int OVS_ID, String bridgeUUID, String key, String hostIP) throws IOException
	{
		  
		   int id = OVS_ID;
		   String uuid = bridgeUUID;
		   String greKey = key;
		   String ip = hostIP;

		   
		
		   JSONArray setArrayParams2 = new JSONArray();
		   setArrayParams2.add("named-uuid");
		   setArrayParams2.add("new_port");
		   
		   JSONArray setArrayParams = new JSONArray();
		   setArrayParams.add(setArrayParams2);
		   
		   JSONArray insertArrayParams = new JSONArray();
		   insertArrayParams.add(setArrayParams2);
		
		   JSONArray insertArray = new JSONArray();
		   insertArray.add("set");
		   insertArray.add(insertArrayParams);
		  
		   JSONArray mutationsArrayParams = new JSONArray();
		   mutationsArrayParams.add("ports");
		   mutationsArrayParams.add("insert");
		   mutationsArrayParams.add(insertArray);
		    
		   JSONArray mutationsArray = new JSONArray();
		   mutationsArray.add(mutationsArrayParams);
		
		
		   JSONArray uuidArray = new JSONArray();
		   uuidArray.add("uuid");
		   uuidArray.add(uuid); //Bridge UUID
		
		   JSONArray where2 = new JSONArray();
		   where2.add("_uuid");
		   where2.add("==");
		   where2.add(uuidArray);
		   
		   JSONArray whereArray = new JSONArray();
		   whereArray.add(where2);
		   
		   JSONArray where = new JSONArray();
		   where.add(whereArray);
		   
		   Map transactOp3=new LinkedHashMap();
		   transactOp3.put("op","mutate");
		   transactOp3.put("table","Bridge");
		   transactOp3.put("where",whereArray);
		   transactOp3.put("mutations", mutationsArray);
		
		  
		
		   JSONArray setParams = new JSONArray();
		   setParams.add("named-uuid");
		   setParams.add("gre"+greKey); //GRE Port
		    
		   JSONArray setArray = new JSONArray();
		   setArray.add(setParams);
		   
		   JSONArray externalParamsArray = new JSONArray();
		   externalParamsArray.add("even more stuff");
		
		   JSONArray externalParams = new JSONArray();
		   externalParams.add("map");
		   externalParams.add("more");
		   
		   JSONArray optionsParams2 = new JSONArray();
		   optionsParams2.add("set");
		   optionsParams2.add(setArray);
		   
		   Map transactRow2=new LinkedHashMap();
		   transactRow2.put("name","gre"+greKey); // Changes the name of GRE tunnel //parameterized
		   transactRow2.put("interfaces",optionsParams2);
		
		   Map transactOp2=new LinkedHashMap();
		   transactOp2.put("op","insert");
		   transactOp2.put("table","Port");
		   transactOp2.put("row",transactRow2);
		   transactOp2.put("uuid-name","new_port"); //Will need to be parameterized
		
		   JSONArray mapIP = new JSONArray();
		   mapIP.add("remote_ip");
		   mapIP.add(ip); //Sets remote IP Address  //Will need to be parameterized
		   
		   JSONArray mapCsum = new JSONArray();
		   mapCsum.add("csum");
		   mapCsum.add("true");
		   
		   JSONArray mapKey = new JSONArray();
		   mapKey.add("key");
		   mapKey.add(greKey);//needs to be paramterized
		
		   JSONArray mapParams = new JSONArray();
		   mapParams.add(mapIP);
		   mapParams.add(mapCsum);
		   mapParams.add(mapKey);
		 
		   
		   JSONArray optionsParams = new JSONArray();
		   optionsParams.add("map");
		   optionsParams.add(mapParams);
		
		   Map transactRow=new LinkedHashMap();
		   transactRow.put("name","gre"+greKey); //Will need to be parameterized
		   transactRow.put("type","gre");
		   transactRow.put("options",optionsParams);
		   
		   Map transactOp1=new LinkedHashMap();
		   transactOp1.put("op","insert");
		   transactOp1.put("table","Interface");
		   transactOp1.put("row",transactRow);
		   transactOp1.put("uuid-name","gre"+greKey);  //Will need to be parameterized
		   
		
		
		   JSONArray transactParams = new JSONArray();
		   transactParams.add("Open_vSwitch");
		   transactParams.add(transactOp1); //done
		   transactParams.add(transactOp2); //done
		   transactParams.add(transactOp3);

		
		   
		   Map transact=new LinkedHashMap();
		   transact.put("method","transact");
		   transact.put("id",id); //id Field
		   transact.put("params",transactParams);
		
		   
		   
		   StringWriter out = new StringWriter();
		   JSONValue.writeJSONString(transact, out);
		   String transactOut = out.toString();
		   		
		
		return transactOut;
	}
	
}
