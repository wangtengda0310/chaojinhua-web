package com.igame.work.checkpoint.worldEvent;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldEventData
{
	@XmlElement(name="item")
	private List<WorldEventTemplate> its;
	
	private Map<String,WorldEventTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(WorldEventTemplate it: its)
		{
			maps.put(it.getEventType()+"_"+it.getDifficulty(), it);
			
		}
	}
	
	public WorldEventTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<WorldEventTemplate> getAll(){
		return its;
	}

	public int size()
	{
		return maps.size();
	}
}

 
