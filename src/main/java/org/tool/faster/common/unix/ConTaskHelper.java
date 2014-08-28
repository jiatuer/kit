package org.tool.faster.common.unix;

import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConTaskHelper {


	private static Map<Long, OutputStream> batchStreamMap = new ConcurrentHashMap<Long, OutputStream>();
	private static Map<Long, Object> uc4ClientMap = new ConcurrentHashMap<Long, Object>();

	private ConTaskHelper() {

	}

//
//	public static void add1Stream(Object s,OutputStream stream){
//		if(s!=null){
//			batchStreamMap.put(s, stream);
//		}	
//	}
//	
//	public static void remove1Stream(Object 1){
//		batchStreamMap.remove(1);
//	}
//	
//	
//	public static OutputStream getStreamBy1(Object 1){
//		return batchStreamMap.get(1);
//	}
//
//	
//	public static void addUc4Con(long taskId,UC4Client client){
//		if(client!=null){
//			uc4ClientMap.put(taskId, client);
//		}	
//	}
//	
//	public static void cleanup(long taskId){
//		  UC4Client client = uc4ClientMap.get(taskId);
//		  if(client==null) return;
//		  client.destroy();
//		  uc4ClientMap.remove(taskId);
//	}
//	
//	
//	public static UC4Client getClientById(long taskId){
//		return uc4ClientMap.get(taskId);
//	}
		
	
	
}
