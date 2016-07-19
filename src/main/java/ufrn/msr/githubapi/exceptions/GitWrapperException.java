/**
 * 
 */
package ufrn.msr.githubapi.exceptions;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitWrapperException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GitWrapperException(String message){
		super(message);
	}
	
	public GitWrapperException(Exception e){
		super(e);
	}

}
