/**
 * 
 */
package ufrn.msr.githubapi.exceptions;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitHubWrapperException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GitHubWrapperException(String message){
		super(message);
	}
	
	public GitHubWrapperException(Exception e){
		super(e);
	}

}
