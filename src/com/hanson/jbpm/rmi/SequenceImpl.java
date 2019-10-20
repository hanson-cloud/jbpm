package com.hanson.jbpm.rmi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;

public class SequenceImpl extends UnicastRemoteObject implements ISequence 
{
	private static final long serialVersionUID = 1L;	
	
	private static RandomAccessFile seqFile;
	
    public SequenceImpl() throws IOException 
    { 
    	seqFile = new RandomAccessFile("c:/id.txt", "rw");
    } 

    public String getId() throws RemoteException 
    { 
         String idx = IdentityCreator.getTaskId(5);
         try {
        	 if (seqFile.getChannel().isOpen()) {
        		 seqFile.seek(0);
        	 } else {
        		 seqFile = new RandomAccessFile("c:/id.txt", "rw");
        	 }
        	 seqFile.writeBytes(idx + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
         return idx;
    }

	public void close() throws IOException
	{
		seqFile.close();
	} 
}
