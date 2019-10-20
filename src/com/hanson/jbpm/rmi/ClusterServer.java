package com.hanson.jbpm.rmi;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.hanson.jbpm.mgmt.ProcessClient;

public class ClusterServer
{
	public static String HOST = "localhost";	
	public final static int PORT = 10090;
	
	//public static String[] hosts = AppHandle.getHandle("jbpm").getProperty("CLUSTER", "172.16.56.101").split(",");
		
	public static void start() throws IOException, AlreadyBoundException
	{		
		if (isMainServer()) {
			LocateRegistry.createRegistry(PORT);			
			ISequence sequence = new SequenceImpl();			
			Naming.bind("rmi://" + HOST + ":" + PORT + "/sequence", sequence);
		} 
	} 
	
	private static boolean isMainServer() throws UnknownHostException
	{
		HOST = ProcessClient.getIp();
		//if (hosts.length == 1) 
		//	return true;
		//if (HOST.equals(hosts[0]))
			return true;
		//HOST = hosts[0];
		//return false;
	}
	
	public static void main(String[] args) throws IOException, AlreadyBoundException 
	{
		System.setProperty("EAP_HOME", "f:/tomcat");
		
		ClusterServer.start();
	}
}
