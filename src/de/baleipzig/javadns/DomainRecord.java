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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DomainRecord {
    
	private static final String[] recordTypes = new String[] { 
		"A", "AAAA", "LOC", "MX", "NS", "RP", "TXT", "AFSDB",
		"APL", "CERT", "CNAME", "DHCID", "DLV", "DNAME", "DNSKEY",
		"DS", "HIP", "IPSECKEY", "KEY", "KX", "NAPTR", "NSEC", "NSEC3",
		"NSEC3PARAM", "PTR", "RRSIG", "SIG", "SOA", "SPF", "SRV",
		"SSHFP", "TA", "TKEY", "TSIG" };
	
    private static HashMap<String, HashMap<String, Attribute>> records 
    	= new HashMap<String, HashMap<String, Attribute>>();
    
    public static DomainRecordMessage lookup(String hostName, String record) {
    	DomainRecordMessage dnsMessage = new DomainRecordMessage();
    	dnsMessage.setHostName(hostName);
    	dnsMessage.setRecordType(record);
    	
    	Vector<String> result = new Vector<String>();
    	try {
    		HashMap<String, Attribute> recordsEntry;
    		// check whether the hostName is known
    		if ((recordsEntry = records.get(hostName)) != null) {
    			Attribute recordEntryAttribute;
    			// check whether the specific attribute for this hostName is known
    			if ((recordEntryAttribute = recordsEntry.get(record)) != null) {
    				// fill the result with the appropriate String representations
    				NamingEnumeration<?> recordEntryAttributeEnumeration = recordEntryAttribute.getAll();
    				while (recordEntryAttributeEnumeration.hasMoreElements()) {
    					result.add((String) recordEntryAttributeEnumeration.next());
    				}
    				dnsMessage.setDnsResult(result);
    				dnsMessage.setLocalHostname(true);
    				dnsMessage.setLocalAttribute(true);
    				return dnsMessage;
    			}
				// the attribute could not be found for a known hostname
    			else {
    				Attribute lookupResult = remote_lookup(hostName, record);
    				recordsEntry.put(lookupResult.getID(), lookupResult);
        			// prepare the results
        			NamingEnumeration<?> resultEnumeration = lookupResult.getAll();
        			while (resultEnumeration.hasMoreElements()) {
        				result.add((String) resultEnumeration.next());
        			}
    				dnsMessage.setDnsResult(result);
    				dnsMessage.setLocalHostname(true);
    				dnsMessage.setLocalAttribute(false);
    				return dnsMessage;    			
    			}
    		}
    		// if either hostName or the specific attribute was not found, do a remote lookup for that
    		else {
    			Attribute lookupResult = remote_lookup(hostName, record);

    			recordsEntry = new HashMap<String, Attribute>();
    			recordsEntry.put(lookupResult.getID(), lookupResult);
    			records.put(hostName, recordsEntry);

    			// prepare the results
    			NamingEnumeration<?> resultEnumeration = lookupResult.getAll();
    			while (resultEnumeration.hasMoreElements()) {
    				result.add((String) resultEnumeration.next());
    			}
				dnsMessage.setDnsResult(result);
				dnsMessage.setLocalHostname(false);
				dnsMessage.setLocalAttribute(false);
				return dnsMessage;    		
    		}
    	} catch (NamingException e) {
			dnsMessage.setUnknownDNS(true);
    	} catch (NullPointerException e) {
    		dnsMessage.setUnknownAttribute(true);
    	}
		dnsMessage.setDnsResult(result);
		return dnsMessage;
    }

    private static Attribute remote_lookup(String hostName, String record) throws NamingException {
    	// create a JNDI environment and context
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);

		// grab attributes for a single record
		Attributes attributes = ictx.getAttributes(hostName, new String[] { record });
		// return what you found
		return attributes.get(record);
    }

	public static boolean reset() {
		records.clear();
		return true;
	}
	
	private static HashMap<String, Attribute> createCompleteAttributes() {
		HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
		
		for (String recordType : recordTypes) {
			attributes.put(recordType, new BasicAttribute(recordType));
		}
		
		return attributes;
	}
	
	public HashMap<String, Attribute> addRecord(String desiredHostName, HashMap<String, Attribute> desiredAttributes) {
		HashMap<String, Attribute> attributes = createCompleteAttributes();
		
		for (String key : desiredAttributes.keySet()) {
			attributes.put(key, desiredAttributes.get(key));
		}
		
		return records.put(desiredHostName, attributes);
	}
}
