/*

   Derby - Class org.apache.derby.impl.sql.compile.RevokeRoleNode

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

	  http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.dearbaby.impl.sql.compile;

import java.util.Iterator;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.shared.common.sanity.SanityManager; 

/**
 * This class represents a REVOKE role statement.
 */
class RevokeRoleNode extends DDLStatementNode
{
    private List<String> roles;
    private List<String> grantees;

	/**
     * Construct a RevokeRoleNode.
	 *
	 * @param roles list of strings containing role name to be revoked
	 * @param grantees list of strings containing grantee names
     * @param cm context manager
	 */
    RevokeRoleNode( List<String> roles,
                    List<String> grantees,
                    ContextManager cm) throws StandardException
	{
        super(cm);
        this.roles = roles;
        this.grantees = grantees;
	}

 
	/**
	 * Convert this object to a String.  See comments in QueryTreeNode.java
	 * for how this should be done for tree printing.
	 *
	 * @return	This object as a String
	 */
    @Override
	public String toString()
	{
		if (SanityManager.DEBUG) {
            StringBuilder sb1 = new StringBuilder();

            for( Iterator<?> it = roles.iterator(); it.hasNext();) {
				if( sb1.length() > 0) {
					sb1.append( ", ");
				}
				sb1.append( it.next().toString());
			}

            StringBuilder sb2 = new StringBuilder();

            for( Iterator<?> it = grantees.iterator(); it.hasNext();) {
				if( sb2.length() > 0) {
					sb2.append( ", ");
				}
				sb2.append( it.next().toString());
			}

            return (super.toString() +
					sb1.toString() +
					" FROM: " +
					sb2.toString() +
					"\n");
		} else {
			return "";
		}
	} // end of toString


    String statementToString()
	{
		return "REVOKE role";
	}
}
