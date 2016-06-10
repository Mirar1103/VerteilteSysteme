package shared;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Maximilian on 10.06.2016.
 */
public class MasterMain {
    public static void main(String... args) throws RemoteException {
        LocateRegistry.createRegistry(Integer.parseInt(args[0]));
        MasterImpl master = new MasterImpl();
        String objName = "master";
        Master masterStub = (Master) master;
        Registry reg = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
        reg.rebind(objName, masterStub);
        System.out.println(objName + " bound to registry");


        System.out.println("Master is running!!");

    }
}
