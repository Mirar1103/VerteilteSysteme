package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 10.06.2016.
 */
public interface Master extends Remote{

    public void registerTable(Table table) throws RemoteException;


}