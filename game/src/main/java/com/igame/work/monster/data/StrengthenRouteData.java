package com.igame.work.monster.data;


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class StrengthenRouteData
{
	@XmlElement(name="item")
	private List<StrengthenRouteTemplate> its;
	
	private Map<Integer,StrengthenRouteTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(StrengthenRouteTemplate it: its)
		{
			maps.put(it.getStrengthen_route(), it);
			
		}
	}
	
	public StrengthenRouteTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public StrengthenRouteTemplate getTemplateByValue(int value)
	{	
		for(StrengthenRouteTemplate srt : its){
			if(srt.getValue() == value){
				return srt;
			}
		}
		return null;
	}


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 
