import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author Dominik Ernsberger
 *
 * 05.04.2016
 */
public class Master extends Thread{

	private final List<Philosopher> listPhilosopher;
	private List<Integer> eatCount;
	private int totalEaten = 0;
	private volatile boolean continueTesting = true;
	
	private final static int MAX_EAT_MORE = 10;
	
	/**
	 * Initialize a single Master
	 * @param listPhilosopher list of all philosophers
	 */
	public Master(List<Philosopher> listPhilosopher){
		this.listPhilosopher = listPhilosopher;
		eatCount = new ArrayList<Integer>(listPhilosopher.size());
		for(int index = 0; index < listPhilosopher.size(); index++)
			eatCount.add(0);
	}
	
	/**
	 * Main function of the Master which counts and control the eating behavior
	 * of the philosopher
	 */
	@Override
	public void run(){
		while(continueTesting){
			
			//Get the number of meals of each philosopher as well as check and ban philosopher
			//which are eaten to much
			int minEaten=0;
			int count;
			
			for(int index = 0; index < listPhilosopher.size(); index++){
				count = listPhilosopher.get(index).getTotalEatenRounds();
				eatCount.set(index, count);
				
				if((count < minEaten) || (index == 0))
					minEaten = count;
			}
			
			for(int index = 0; index < listPhilosopher.size(); index++){
				if((eatCount.get(index)-minEaten) >= MAX_EAT_MORE)
					listPhilosopher.get(index).setBanned(true);
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the testing value for the loops to false.
	 * @param continueTesting false to stop
	 */
	public void stop(boolean continueTesting){
		this.continueTesting = continueTesting;
	}
	
	
	/**
	 * Print the result of the eaten plates.
	 */
	public void printResult(){
		System.out.println("---------FINISH----------");
		for(int index = 0; index < eatCount.size(); index++){
			System.out.println("Philosopher "+ index + " ate: " + eatCount.get(index) + " plates.");
			totalEaten += eatCount.get(index);
		}
		System.out.println("Total: " + totalEaten);
	}
}
