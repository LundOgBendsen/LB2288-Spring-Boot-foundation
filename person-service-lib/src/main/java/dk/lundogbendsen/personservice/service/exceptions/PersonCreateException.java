package dk.lundogbendsen.personservice.service.exceptions;

public class PersonCreateException extends RuntimeException
{
	public PersonCreateException()
	{
		super();
	}

	public PersonCreateException(String message)
	{
		super(message);
	}

	public PersonCreateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PersonCreateException(Throwable cause)
	{
		super(cause);
	}

	protected PersonCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
