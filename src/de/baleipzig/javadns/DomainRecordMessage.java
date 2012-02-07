package de.baleipzig.javadns;

import java.util.Vector;

public class DomainRecordMessage {
	private Vector<String> dnsResult;
	private boolean localHostname;
	private boolean localAttribute;
	private boolean unknownAttribute;
	private boolean unknownDNS;
	private String hostName;
	private String recordType;
	
	public DomainRecordMessage() {
		dnsResult = new Vector<String>();
		localHostname = false;
		localAttribute = false;
		unknownAttribute = false;
		unknownDNS = false;
		hostName = "";
		recordType = "";
	}
	
	public boolean isUnknownAttribute() {
		return unknownAttribute;
	}

	public void setUnknownAttribute(boolean unknownAttribute) {
		this.unknownAttribute = unknownAttribute;
	}

	public boolean isUnknownDNS() {
		return unknownDNS;
	}

	public void setUnknownDNS(boolean unknownDNS) {
		this.unknownDNS = unknownDNS;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	
	public boolean isLocalHostname() {
		return localHostname;
	}

	public void setLocalHostname(boolean localHostname) {
		this.localHostname = localHostname;
	}

	public boolean isLocalAttribute() {
		return localAttribute;
	}

	public void setLocalAttribute(boolean localAttribute) {
		this.localAttribute = localAttribute;
	}

	public Vector<String> getDnsResult() {
		return dnsResult;
	}

	public void setDnsResult(Vector<String> dnsResult) {
		this.dnsResult = dnsResult;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		if(isUnknownDNS()) {
			ret.append("No DNS name found for '" + getHostName() + "' and attribute '" 
					+ getRecordType() + "'. " + dnsResult.toString());
			return ret.toString();
		}
		else if (isUnknownAttribute()) {
			ret.append("The attribute '" + getRecordType() + "' is unknown to the server '" 
					+ getHostName() + "'. " + dnsResult.toString());
			return ret.toString();
		}
		else if(isLocalHostname() && isLocalAttribute()) {
			ret.append("Local lookup for the '" + getRecordType() + "' for '" 
					+ getHostName() + "' was successful. " + dnsResult.toString());
			return ret.toString();
		}
		else if (isLocalHostname() && !isLocalAttribute()) {
			ret.append("Local lookup for '" + getHostName() + "' was successful, but the attribute '" 
					+ getRecordType() + "' required remote lookup. " + dnsResult.toString());
			return ret.toString();
		}
		else {
			ret.append("Required remote lookup for '" + getRecordType() + "' at '" 
					+ getHostName() + "'. " + dnsResult.toString());
			return ret.toString();
		}
	}
}
