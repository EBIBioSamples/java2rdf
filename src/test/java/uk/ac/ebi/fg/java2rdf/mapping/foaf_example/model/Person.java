package uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model;

/**
 * Class from an example model
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Person
{
	private String email, name, surname;

	
	public Person ()
	{
		super ();
	}

	public Person ( String email, String name, String surname )
	{
		super ();
		this.email = email;
		this.name = name;
		this.surname = surname;
	}

	
	public String getEmail ()
	{
		return email;
	}

	public void setEmail ( String email )
	{
		this.email = email;
	}

	public String getName ()
	{
		return name;
	}

	public void setName ( String name )
	{
		this.name = name;
	}

	public String getSurname ()
	{
		return surname;
	}

	public void setSurname ( String surname )
	{
		this.surname = surname;
	}
	
}
