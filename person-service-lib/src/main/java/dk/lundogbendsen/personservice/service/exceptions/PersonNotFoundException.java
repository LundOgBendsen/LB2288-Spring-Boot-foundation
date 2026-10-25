package dk.lundogbendsen.personservice.service.exceptions;

public class PersonNotFoundException extends RuntimeException
{
	public PersonNotFoundException()
	{
		super();
	}

	public PersonNotFoundException(String message)
	{
		super(message);
	}

	public PersonNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PersonNotFoundException(Throwable cause)
	{
		super(cause);
	}

	protected PersonNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
