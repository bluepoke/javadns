/* **************************************************************************
 *                                                                          *
 *  Copyright (C)  2011  Nils Foken, André Kießlich,                        *
 *                       Peter Kossek, Hans Laser                           *
 *                                                                          *
 *  Nils Foken       <nils.foken@it2009.ba-leipzig.de>                      *
 *  André Kießlich   <andre.kiesslich@it2009.ba-leipzig.de>                 *
 *  Peter Kossek     <peter.kossek@it2009.ba-leipzig.de>                    *
 *  Hans Laser       <hans.laser@it2009.ba-leipzig.de>                      *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  This file is part of 'javadns'.                                         *
 *                                                                          *
 *  This project is free software: you can redistribute it and/or modify    *
 *  it under the terms of the GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or       *
 *  any later version.                                                      *
 *                                                                          *
 *  This project is distributed in the hope that it will be useful,         *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *  GNU General Public License for more details.                            *
 *                                                                          *
 *  You should have received a copy of the GNU General Public License       *
 *  along with this project. If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                          *
 ****************************************************************************/

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
