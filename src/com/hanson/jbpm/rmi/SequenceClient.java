package com.hanson.jbpm.rmi;

import java.io.IOException;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class SequenceClient
{ 
    public static String getId() throws IOException, NotBoundException 
    { 
        String url = "rmi://" + ClusterServer.HOST + ":" + ClusterServer.PORT + "/sequence";
        ISequence sequence =(ISequence) Naming.lookup(url);        
        String seq = sequence.getId();
        return seq;
    } 
    
    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException  
	{
    	//ClusterServer.start();
    	long start = System.currentTimeMillis();
    	for (int i=0; i<10000; i++)
    		SequenceClient.getId();    
    	System.out.print( System.currentTimeMillis() - start + ", " + SequenceClient.getId());
	}
}
