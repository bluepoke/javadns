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

import java.util.Iterator;
import java.util.List;

public class DNSServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("\nDNS for mowyourlawn.com returns:");
        printList(DomainRecord.lookup("mowyourlawn.com", DomainRecord.RECORD_A));
        printList(DomainRecord.lookup("mowyourlawn.com", DomainRecord.RECORD_MX));
        printList(DomainRecord.lookup("mowyourlawn.com", DomainRecord.RECORD_NS));
        printList(DomainRecord.lookup("mowyourlawn.com", DomainRecord.RECORD_SOA));
        
		System.out.println("\nDNS for google.de returns:");
        printList(DomainRecord.lookup("google.de", "TXT"));
        printList(DomainRecord.lookup("google.de", DomainRecord.RECORD_MX));
        printList(DomainRecord.lookup("google.de", DomainRecord.RECORD_NS));
        printList(DomainRecord.lookup("google.de", DomainRecord.RECORD_SOA));

	}

    static void printList(List<String> l) {
        Iterator<String> iter = l.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
        System.out.println();
    }
	
}
