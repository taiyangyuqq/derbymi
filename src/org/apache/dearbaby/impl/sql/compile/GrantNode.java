/*

   Derby - Class org.apache.derby.impl.sql.compile.GrantNode

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

package	org.apache.dearbaby.impl.sql.compile;


import java.util.List;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager; 
import org.apache.derby.shared.common.sanity.SanityManager;

/**
 * This class represents a GRANT statement.
 */
class GrantNode extends DDLStatementNode
{
	private PrivilegeNode privileges;
    private List<String> grantees;

    /**
     * Constructor for a GrantNode.
     *
     * @param privileges PrivilegesNode
     * @param grantees List
     * @param cm Context manager
     */
    GrantNode(PrivilegeNode privileges,
              List<String> grantees,
              ContextManager cm)
    {
        super(cm);
        this.privileges = privileges;
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
		if (SanityManager.DEBUG)
		{
            StringBuilder sb = new StringBuilder();

            for (String grantee : grantees)
			{
				if( sb.length() > 0)
					sb.append( ",");
                sb.append(grantee);
			}
			return super.toString() +
				   privileges.toString() +
				   "TO: \n" + sb.toString() + "\n";
		}
		else
		{
			return "";
		}
	} // end of toString

    String statementToString()
	{
		return "GRANT";
	}

	 

 
}
