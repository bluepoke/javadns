package de.baleipzig.javadns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.directory.Attribute;

public class Request implements Serializable{

	/**
	 * generated serialVersionUID 
	 */
	private static final long serialVersionUID = -1298209815758981257L;
	/**
	 * Constant to determine a LOOKUP-Request
	 */
	public static final int LOOKUP = 0;
	/**
	 * Constant to determint an IDENTIFY-Request
	 */
	public static final int IDENTIFY = 1;
	/**
	 * Constant to determine a RESET-Request
	 */
	public static final int RESET = 2;
	
	private String hostName;
	private String record;
	private int type;
	private HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
	
	/**
	 * Creates a RESET-Request
	 */
	public Request() {
		super();
		this.type = RESET;
	}
	
	/**
	 * Creates a LOOKUP-Request for one attribute
	 * @param hostName The hostname to lookup
	 * @param attribute The record to fetch
	 */
	public Request (String hostName, String attribute) {
		super();
		this.hostName = hostName;
		this.record = attribute;
		this.type = LOOKUP;
	}
	
	/**
	 * Creates an IDENTIFY-Request to register at the DNS server
	 * @param hostName The hostname to be known under
	 * @param attributes 
	 */
	public Request(String hostName, HashMap<String, Attribute> attributes) {
		super();
		this.hostName = hostName;
		this.attributes = attributes;
		this.type = IDENTIFY;
	}
	
	/**
	 * Type of this request
	 * @return Type of this request. Can be <code>LOOKUP, IDENTIFY</code> or <code>RESET</code>
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Name of host to lookup or to identify as.
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * Record to look up with this request.
	 * @return Name of the record (A, AAAA, RP, ...)
	 */
	public String getRecord() {
		return record;
	}
	
	/**
	 * Attributes for identification
	 * @return
	 */
	public HashMap<String, Attribute> getAttributes() {
		return attributes;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (type == IDENTIFY) {
			s.append("IDENTIFY ");
			s.append(hostName);
			s.append(" with records ");
			Iterator<String> iterator = attributes.keySet().iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				s.append(attributes.get(key));
				if (iterator.hasNext()) {
					s.append(", ");
				}
			}
		} else if (type == LOOKUP) {
			s.append("LOOKUP ");
			s.append("record ");
			s.append(record);
			s.append(" for host ");
			s.append(hostName);
		} else {
			//RESET
			s.append("RESET");
		}
		return s.toString();
	}
}
