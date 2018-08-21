package com.igame.work.activity;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class ActivityConfigTemplate {
	

	@XmlAttribute(name = "activity_sign")
	private int activity_sign;
		
	@XmlAttribute(name = "activity_type")
	private String activity_type;
	
	@XmlAttribute(name = "order")
	private String order;

	@XmlAttribute(name = "activity_show")
	private String activity_show;

	@XmlAttribute(name = "activity_name")
	private  String activity_name;
	
	@XmlAttribute(name = "activity_text")
	private  String activity_text;
	
	@XmlAttribute(name = "gift_bag")
	private  String gift_bag;

	@XmlAttribute(name = "start_time")
	private  String start_time;

	@XmlAttribute(name = "time_limit")
	private  String time_limit;

	@XmlAttribute(name = "get_limit")
	private  String get_limit;

	@XmlAttribute(name = "get_value")
	private  String get_value;

	@XmlAttribute(name = "activity_drop")
	private  String activity_drop;

	public int getActivity_sign() {
		return activity_sign;
	}

	public void setActivity_sign(int activity_sign) {
		this.activity_sign = activity_sign;
	}

	public String getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(String activity_type) {
		this.activity_type = activity_type;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getActivity_show() {
		return activity_show;
	}

	public void setActivity_show(String activity_show) {
		this.activity_show = activity_show;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public String getActivity_text() {
		return activity_text;
	}

	public void setActivity_text(String activity_text) {
		this.activity_text = activity_text;
	}

	public String getGift_bag() {
		return gift_bag;
	}

	public void setGift_bag(String gift_bag) {
		this.gift_bag = gift_bag;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getTime_limit() {
		return time_limit;
	}

	public void setTime_limit(String time_limit) {
		this.time_limit = time_limit;
	}

	public String getGet_limit() {
		return get_limit;
	}

	public void setGet_limit(String get_limit) {
		this.get_limit = get_limit;
	}

	public String getGet_value() {
		return get_value;
	}

	public void setGet_value(String get_value) {
		this.get_value = get_value;
	}

	public String getActivity_drop() {
		return activity_drop;
	}

	public void setActivity_drop(String activity_drop) {
		this.activity_drop = activity_drop;
	}
}