# Persistence

## Transactions

All database operations MUST be performed in a Transaction.
The reason we have not been bothered by this so far, is because all Spring sets up an implicit Transaction around all
the test cases.

To test how transactions works, we must disable automatic transactions and take control our selves. We can do this by
adding the @Transactional annotation on our test case. And then we can tweak that annotation to allow testing it.

### Exercise 1: Repository methods are automatically transactional (see SimpleJpaRepository.class)

In this exercise we are going to realize that repository methods originating from the CrudRepository are marked with
transactional.

- Make a test case for deleting a student.
- Create a new student and use studentRepository to save it.
- Immediately after - use studentRepository to delete it again.
- Run the test

Of course this works. What is happening is that Spring is implicitly setting up a transaction around the test case.
Let's make that explicit:

- Annotate the test case with @Transactional.
- Run the test again.

It still works. Just this time you are explicitly controlling the transaction.

### Exercise 2: Disable propagation of the explicit transaction

In this exercise we continue working on the test from before. This time we'll disable transaction propagation meaning
that the repository will not be part of the overall transaction.

- Change the Transactional annotation to `@Transactional(propagation = Propagation.NEVER)`
- Run the test again.

This STILL works. That is because the delete () operation comes from CrudRepository, and therefore is transactional.

```java
    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void deleteStudentsByName() {
        final Student christian = Student.builder().name("Christian").build();
        studentRepository.save(christian);
        studentRepository.delete(christian);
    }
```

### Exercise 3: Create a deleteByName method on the StudentRepository

In this exercise we will create a new way of deleting a Student by name, and see how that works with transactions.

- Make a method `void deleteByName(String name);` on the StudentRepository.
- Update the testcase from before to delete the student using the new method.
- Run the test case. Will it work?

NO! It fails because there is no transaction associated with the call to the repository, and we did not mark it
ourselves ("No EntityManager with actual transaction available for current thread").
There MUST be a transaction.

The methods YOU provide on a Repository does not automatically have a transaction associated.

### Exercise 4: Include deleteByName in a Transaction

In this exercise we are going to execute the deleteByName in a transaction. There are two approaces to this.

Either we are marking the repository method explicitly with the @Transactional annotation, OR we are propagating the
transaction from the testmethod.

- Update the repository method by annotating it with `@Transactional`
- Run the test. Works it?

YES! Because it runs in an explicit transaction of its own.

Solutions for these exercises are in [SOLUTIONS-06-transaction-auditing.md](SOLUTIONS-06-transaction-auditing.md).
