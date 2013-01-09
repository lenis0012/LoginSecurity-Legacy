package me.lenis0012.ls.Util;

public class ParseResult
{
	private String description;
	private String link;
	private String pubDate;

  	public ParseResult(String link, String pubDate, String description)
  	{
  		this.link = link;
    	setPubDate(pubDate);
    	setDescription(description);
  	}

  	public String getDescription()
  	{
  		return this.description;
  	}

  	public String getLink()
  	{
  		return this.link;
  	}

  	public String getPubDate()
  	{
  		return this.pubDate;
  	}

  	public void setDescription(String description)
  	{
	  	this.description = description;
  	}

  	public void setLink(String link)
  	{
	  	this.link = link;
  	}

  	public void setPubDate(String pubDate)
  	{
  		this.pubDate = pubDate;
  	}
}
