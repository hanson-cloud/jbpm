package com.hanson.jbpm.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISequence extends Remote { 

    public String getId() throws RemoteException; 
    
    public void close() throws IOException;
    
}
