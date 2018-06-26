package controller;

public class CustomerAlreadyExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomerAlreadyExistException() {
		super();
	}

	public CustomerAlreadyExistException(String message) {
		super(message);
	}
}
