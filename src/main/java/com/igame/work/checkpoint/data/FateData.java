package com.igame.work.checkpoint.data;



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
@XmlRootElement(name = "destinydata")
@XmlAccessorType(XmlAccessType.NONE)
public class FateData
{
	@XmlElement(name="low")
	private List<FatedataTemplate> its;
	
	private Map<Integer,FatedataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(FatedataTemplate it: its)
		{
			maps.put(it.getFloorNum(), it);
			
		}
	}
	
	public FatedataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<FatedataTemplate> getAll(){
		
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

 