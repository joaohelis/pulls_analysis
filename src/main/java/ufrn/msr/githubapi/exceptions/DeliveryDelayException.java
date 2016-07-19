/**
 * 
 */
package ufrn.msr.githubapi.exceptions;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class DeliveryDelayException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeliveryDelayException(String message){
		super(message);
	}
	
	public DeliveryDelayException(Exception e){
		super(e);
	}

}
