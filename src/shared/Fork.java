/**
 * 
 */
package shared;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public interface Fork {

	public boolean hasOwner();
	public boolean pickUpFork(Philosopher owner);
	public void drop();
}
