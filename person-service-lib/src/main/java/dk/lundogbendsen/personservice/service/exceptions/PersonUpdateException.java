package dk.lundogbendsen.personservice.service.exceptions;

public class PersonUpdateException extends RuntimeException
{
	public PersonUpdateException()
	{
		super();
	}

	public PersonUpdateException(String message)
	{
		super(message);
	}

	public PersonUpdateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PersonUpdateException(Throwable cause)
	{
		super(cause);
	}

	protected PersonUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
