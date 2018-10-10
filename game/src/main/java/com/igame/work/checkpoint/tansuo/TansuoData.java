package com.igame.work.checkpoint.tansuo;


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
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class TansuoData
{
	@XmlElement(name="item")
	private List<TansuoTemplate> its;
	
	private Map<Integer, TansuoTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TansuoTemplate it: its)
		{
			maps.put(it.getNum(), it);
			
		}
	}
	
	public TansuoTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<TansuoTemplate> getAll(){
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

 
