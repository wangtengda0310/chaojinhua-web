package com.igame.work.monster.data;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "strengthenreward")
@XmlAccessorType(XmlAccessType.NONE)
public class StrengthenrewardData
{
	@XmlElement(name="low")
	private List<StrengthenrewardTemplate> its;
	
	private Map<Integer,StrengthenrewardTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(StrengthenrewardTemplate it: its)
		{
			maps.put(it.getStrengthen_type(), it);
			
		}
	}
	
	public StrengthenrewardTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<StrengthenrewardTemplate> getAll(){
		return its;
	}
	
	
	


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 