/*

   Derby - Class org.apache.derby.impl.sql.compile.DropAliasNode

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

import org.apache.derby.catalog.AliasInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager; 
import org.apache.derby.shared.common.sanity.SanityManager;

/**
 * A DropAliasNode  represents a DROP ALIAS statement.
 *
 */

class DropAliasNode extends DDLStatementNode
{
	private char aliasType;
	private char nameSpace;

	/**
     * Constructor for a DropAliasNode
	 *
	 * @param dropAliasName	The name of the method alias being dropped
	 * @param aliasType				Alias type
     * @param cm  Context manager
	 *
	 * @exception StandardException
	 */
    DropAliasNode(TableName dropAliasName, char aliasType, ContextManager cm)
				throws StandardException
	{
        super(dropAliasName, cm);
        this.aliasType = aliasType;
	
		switch (this.aliasType)
		{
			case AliasInfo.ALIAS_TYPE_AGGREGATE_AS_CHAR:
				nameSpace = AliasInfo.ALIAS_NAME_SPACE_AGGREGATE_AS_CHAR;
				break;

            case AliasInfo.ALIAS_TYPE_PROCEDURE_AS_CHAR:
				nameSpace = AliasInfo.ALIAS_NAME_SPACE_PROCEDURE_AS_CHAR;
				break;

			case AliasInfo.ALIAS_TYPE_FUNCTION_AS_CHAR:
				nameSpace = AliasInfo.ALIAS_NAME_SPACE_FUNCTION_AS_CHAR;
				break;

			case AliasInfo.ALIAS_TYPE_SYNONYM_AS_CHAR:
				nameSpace = AliasInfo.ALIAS_NAME_SPACE_SYNONYM_AS_CHAR;
				break;

			case AliasInfo.ALIAS_TYPE_UDT_AS_CHAR:
				nameSpace = AliasInfo.ALIAS_NAME_SPACE_UDT_AS_CHAR;
				break;

			default:
				if (SanityManager.DEBUG)
				{
					SanityManager.THROWASSERT("bad type to DropAliasNode: "+this.aliasType);
				}
		}
	}

	public	char	getAliasType() { return aliasType; }

    String statementToString()
	{
		return "DROP ".concat(aliasTypeName(aliasType));
	}

	 

	// inherit generate() method from DDLStatementNode


	 

	/* returns the alias type name given the alias char type */
	private static String aliasTypeName( char actualType)
	{
		String	typeName = null;

		switch ( actualType )
		{
			case AliasInfo.ALIAS_TYPE_AGGREGATE_AS_CHAR:
				typeName = "DERBY AGGREGATE";
				break;
			case AliasInfo.ALIAS_TYPE_PROCEDURE_AS_CHAR:
				typeName = "PROCEDURE";
				break;
			case AliasInfo.ALIAS_TYPE_FUNCTION_AS_CHAR:
				typeName = "FUNCTION";
				break;
			case AliasInfo.ALIAS_TYPE_SYNONYM_AS_CHAR:
				typeName = "SYNONYM";
				break;
			case AliasInfo.ALIAS_TYPE_UDT_AS_CHAR:
				typeName = "TYPE";
				break;
		}
		return typeName;
	}
}
