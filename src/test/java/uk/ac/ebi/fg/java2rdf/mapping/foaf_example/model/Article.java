package uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class from an example model
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Article
{
	private int id;
	
	private String title, abstractText;
	
	private Person editor;
	private Set<Person> authors = new HashSet<> ();

	
	
	public Article ()
	{
		super ();
	}

	public Article ( int id, String title, String abstractText )
	{
		super ();
		this.id = id;
		this.title = title;
		this.abstractText = abstractText;
	}
	
	

	public int getId ()
	{
		return id;
	}

	public void setId ( int id )
	{
		this.id = id;
	}

	public String getTitle ()
	{
		return title;
	}

	public void setTitle ( String title )
	{
		this.title = title;
	}

	public String getAbstractText ()
	{
		return abstractText;
	}

	public void setAbstractText ( String abstractText )
	{
		this.abstractText = abstractText;
	}

	public Person getEditor ()
	{
		return editor;
	}

	public void setEditor ( Person editor )
	{
		this.editor = editor;
	}

	public Set<Person> getAuthors ()
	{
		return authors;
	}

	public void setAuthors ( Set<Person> authors )
	{
		this.authors = authors;
	}

}
