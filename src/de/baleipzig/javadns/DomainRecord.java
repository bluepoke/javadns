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

import java.util.Enumeration;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

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
	
	public static HashMap<String, Attribute> addRecord(String desiredHostName, HashMap<String, Attribute> desiredAttributes) {
		HashMap<String, Attribute> attributes = createCompleteAttributes();
		
		for (String key : desiredAttributes.keySet()) {
			attributes.put(key, desiredAttributes.get(key));
		}
		
		return records.put(desiredHostName, attributes);
	}
	
	public static HashMap<String, HashMap<String, Attribute>> getRecords() {
		return records;
	}

	// André fill the tree with nodes ///////////////////////
	public static DefaultMutableTreeNode fillTreeNode(DefaultMutableTreeNode node, String hostName, String record, String response) {
		 
			int i = 0; // counter entry level for the node child
			int j = 0; // counter HostNames 
			int k = 0; // counter for the second level
			// is necessary to add a node on the right node
			DefaultTreeModel treeModel = new DefaultTreeModel(node);
			DefaultMutableTreeNode hostNameNode = null;
			DefaultMutableTreeNode recordNode = null;
			DefaultMutableTreeNode responseLeaf = null;		
			
			// control if the hostName starts with www.
			// if true = replace it (because www.google.de and google.de give back the same result)
			if (hostName.startsWith("www"))
			{
				//System.out.println("We must do something.");
				hostName = hostName.replaceFirst("www.", "");
			}
			
			// check how many child elemnts in the tree
			// if this value !0 then, check if the host name value is the Tree
			if (node.getChildCount() != 0) {
					
					Enumeration e = node.children();
					
					while (e.hasMoreElements()) {
						String nextElement = e.nextElement().toString();
						// control if the value is not in the tree
						if (nextElement.equals(hostName))
						{
							j++;							
							// look if the hostNameLeaf have a child
							TreeNode hostNameTreeNode = node.getChildAt(i);
							Enumeration e2 = hostNameTreeNode.children();
							
							while (e2.hasMoreElements()) {								
								String nextNodeElement = e2.nextElement().toString();
								// look, if the record is in the node
								if (nextNodeElement.equals(record))
								{
									k++;
									break;
								}
							}
							
							// if no matching was found
							// add nextNodeElement, if they is not in the list
							if (k == 0)
							{
								recordNode = new DefaultMutableTreeNode(record);
								// control if the response string is no empty
								if (!response.isEmpty())
								{
									// split to make it better to read
									String[] splitS = response.split(", ");
									for (int l = 0; l < splitS.length; l++)
									{
										responseLeaf = new DefaultMutableTreeNode(splitS[l]);
										recordNode.add(responseLeaf);
									}
								}
								else
								{
									// wirte in the leaf:
									responseLeaf = new DefaultMutableTreeNode("no result");
									recordNode.add(responseLeaf);
								}
								// insert the Node on the right place (name of the new leaf, node of the leaf, add it at last in the leaf)
								treeModel.insertNodeInto(recordNode, (MutableTreeNode) node.getChildAt(i), node.getChildAt(i).getChildCount());
							}
							break;
						}						
						i++;
					}
					
					// if i == 0 then no matching was found
					// add hostName, if they is not in the list
					if (j == 0) {
						node.add(createNewNode(hostName, record, response , hostNameNode, recordNode, responseLeaf));
						return node;						
					}			
			}
			else
			{
				node.add(createNewNode(hostName, record, response , hostNameNode, recordNode, responseLeaf));
				return node;	
			}
			
			return node;	
					
	}
	
	private static DefaultMutableTreeNode createNewNode(String hostName, String record, String response,
			DefaultMutableTreeNode hostNameNode, DefaultMutableTreeNode recordNode, DefaultMutableTreeNode responseLeaf) {		
	  // add hostName, if the Tree is empty
		hostNameNode = new DefaultMutableTreeNode(hostName);
		
		recordNode = new DefaultMutableTreeNode(record);
		// add hostNameLeaf the recordLeaf
		hostNameNode.add(recordNode);
		
		// control if the response string is no empty
		if (!response.isEmpty())
		{
			String[] splitS = response.split(", ");
			for (int l = 0; l < splitS.length; l++)
			{
				responseLeaf = new DefaultMutableTreeNode(splitS[l]);
				// add recordNode the response
				recordNode.add(responseLeaf);
			}
		}
		else
		{
			responseLeaf = new DefaultMutableTreeNode("no result");
			// add recordNode the response
			recordNode.add(responseLeaf);
		}
		
		return hostNameNode;
	}
	
	// delete all nodes if the user do a Server refresh
	public static DefaultMutableTreeNode deleteTreeNode (DefaultMutableTreeNode node) {
		
		node.removeAllChildren();
		
		return node;
		
	}
}
