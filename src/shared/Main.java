/**
 * 
 */
package shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public class Main {
	
	private static String ip="";
	/**
	 * this method starts the text based input for the user to add objekts.
	 * @param args
	 * @throws IOException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws IOException, NotBoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		boolean debugging=false;
		Table table = null;
		Master master = null;
		PhilosopherHelper phil = null;
		TableMain tableMain = new TableMain();
		SeatHelper seat = null;
		System.out.println("Welcome to the shared dining philosophers! Possible commands are:");
		System.out.println("debuggin on?(J/N)");
		if(br.readLine().equalsIgnoreCase("J")) {
			debugging=true;
		}
		else{
			System.out.println("Welcome in the Time-Mode. Runtime is 60sec after starting the Philosopher");
		}

		System.out.println("create new Master? (J/N)");
		if(br.readLine().equalsIgnoreCase("J")){
			getIP();
			System.out.println("Which IP do you want to use?");
			ip = br.readLine();
			System.out.println("Which Port?");
			String port;
			port = br.readLine();
			MasterMain.main(port, ip);
			master = (Master) LocateRegistry.getRegistry(Integer.parseInt(port)).lookup("master");
		}else{
			master = selectMaster(br);
		}
		System.out.println("create Table, create/remove Philosopher, create/remove Seat, set Debugging, quit");
		
		while(!(input = br.readLine()).equals("quit")){
			//create Table
			if(input.equalsIgnoreCase("create Table")){
				String port;
				if(ip.equals("")){
					getIP();
					System.out.println("Which IP do you want to use?");
					ip = br.readLine();
				}
				System.out.println("Which Port?");
				port = br.readLine();
				tableMain.registerTableToMaster(master, Integer.parseInt(port), ip, debugging);
				table = (Table) LocateRegistry.getRegistry(Integer.parseInt(port)).lookup("table");
				
				table.setMaster(master);
				if(phil == null){
					phil = new PhilosopherHelperImpl(table, master);
				}
				
			}
			//create Philosopher
			else if(input.equalsIgnoreCase("create Philosopher")){
				if(table == null){
					table = searchTable(br);
					phil = table.getPhilHelp();
				}
				int numberOfPhil;
				System.out.println("Number of Philosophers to add: ");
				numberOfPhil = Integer.parseInt(br.readLine());
				System.out.println("Hunger? (J/N)");
				if(br.readLine().equalsIgnoreCase("J")){
					phil.addPhilosopher(numberOfPhil, false, debugging, ip);
				}else{
					phil.addPhilosopher(numberOfPhil, true, debugging, ip);
				}
				if(!debugging){
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					master.printResult();
				}
			}
			//remove Philosopher
			else if(input.equalsIgnoreCase("remove Philosopher")){
				if(table == null){
					table = searchTable(br);
					phil = table.getPhilHelp();
				}
				int numberOfPhil;
				System.out.println("Number of Philosopher to remove: ");
				numberOfPhil = Integer.parseInt(br.readLine());
				phil.removePhilosopher(numberOfPhil, master);
			}
			//create seats
			else if(input.equalsIgnoreCase("create Seat")){
				if(table == null){
					table = searchTable(br);
					phil = table.getPhilHelp();
				}
				if(seat==null)
					seat = new SeatHelper(table);
				int numberOfseats;
				System.out.println("Number of Seats to add: ");
				numberOfseats = Integer.parseInt(br.readLine());
				seat.addSeat(numberOfseats);
			}
			//remove Seats
			else if(input.equalsIgnoreCase("remove Seat")){
				if(table == null){
					table = searchTable(br);
					phil = table.getPhilHelp();
				}
				if(seat==null)
					seat = new SeatHelper(table);
				int numberOfSeat;
				System.out.println("Number of Seats to remove: ");
				numberOfSeat = Integer.parseInt(br.readLine());
				seat.removeSeat(numberOfSeat);
			}
			//set Debugging
			else if(input.equalsIgnoreCase("set Debugging")){
					int newDebug;
					System.out.println("1==true; 0==false");
					newDebug = Integer.parseInt(br.readLine());
					if(newDebug==1){
						debugging=true;
					} else if(newDebug==0){
						debugging = false;
					}else
					{System.out.print("debuging state not chnaged");}
			}
			//stop Debugging
			else if(input.equalsIgnoreCase("sD")){
					debugging = false;
					phil.setDebugging(debugging);
			}
			//continue Debugging
			else if(input.equalsIgnoreCase("cD")){
				debugging = true;
				phil.setDebugging(debugging);
			}
			System.out.println("create Table, create/remove Philosopher, create/remove Seat, set Debugging, quit");
		}
		br.close();

	}

	/**
	 * searches for a table at a Host and port written into the given buffered reader.
	 * @param br
	 * @return
	 * @throws IOException 
	 * @throws NotBoundException 
	 */
	private static Table searchTable(BufferedReader br) throws IOException, NotBoundException {
		System.out.println("No Table found on this Server, please enter an adress for the Table.");
		System.out.println("Host Address: ");
		String host = br.readLine();
		ip = host;
		System.out.println("Port: ");
		String port = br.readLine();
		return (Table) Naming.lookup("//"+host+":"+port+"/table");
	}

	/**
	 * searches for a master at the host and port written into the buffered reader.
	 *
	 * @param br
	 * @return
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private static Master selectMaster(BufferedReader br) throws IOException, NotBoundException {
		System.out.println("Please Specify Master");
		System.out.println("Host Address: ");
		String host = br.readLine();
		System.out.println("Port: ");
		String port = br.readLine();
		return (Master) Naming.lookup("//"+host+":"+port+"/master");
	}
	
	private static void getIP(){
		String ip = "";
	    try {
	        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface iface = interfaces.nextElement();
	            // filters out 127.0.0.1 and inactive interfaces
	            if (iface.isLoopback() || !iface.isUp())
	                continue;

	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                ip = addr.getHostAddress();
	                if(!ip.contains(":"))
	                	System.out.println(iface.getDisplayName() + " " + ip);
	            }
	        }
	    } catch (SocketException e) {
	        throw new RuntimeException(e);
	    }
	}

}
