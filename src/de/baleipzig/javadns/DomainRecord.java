package de.baleipzig.javadns;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DomainRecord {

    public static final String RECORD_A = "A";
    public static final String RECORD_SOA = "SOA";
    public static final String RECORD_NS = "NS";
    public static final String RECORD_MX = "MX";
    public static final String RECORD_CNAME = "CNAME";
    
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
}
