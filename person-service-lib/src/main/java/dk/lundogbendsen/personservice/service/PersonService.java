package dk.lundogbendsen.personservice.service;


import dk.lundogbendsen.personservice.model.Person;
import dk.lundogbendsen.personservice.service.exceptions.PersonCreateException;
import dk.lundogbendsen.personservice.service.exceptions.PersonNotFoundException;
import dk.lundogbendsen.personservice.service.exceptions.PersonUpdateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PersonService
{
	private final Map<Long, Person> persons = new HashMap<>();
	private final AtomicLong ids = new AtomicLong();

	public List<Person> list()
	{
		return new ArrayList<Person>(persons.values());
	}

	public Person create(Person person)
	{
		if(person.getId() != null)
		{
			throw new PersonCreateException("You must not assign the ID when creating a Person");
		}
		person.setId(ids.incrementAndGet());
		persons.put(person.getId(), person);
		return person;
	}

	public Person get(Long id)
	{
		final Person person = persons.get(id);
		if(person == null)
		{
			throw new PersonNotFoundException("No Person with ID " + id + " exists");
		}
		return person;
	}

	public Person update(Person person)
	{
		if(person.getId() == null)
		{
			throw new PersonUpdateException("You cannot update a Person without ID");
		}
		final Person personToUpdate = persons.get(person.getId());
		if(personToUpdate == null)
		{
			throw new PersonNotFoundException("No Person with ID " + person.getId() + " exists");
		}
		persons.put(person.getId(), person);
		return person;
	}

	public void delete(Long id)
	{
		persons.remove(id);
	}


}
