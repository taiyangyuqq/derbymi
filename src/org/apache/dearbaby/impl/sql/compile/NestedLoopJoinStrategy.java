/*

   Derby - Class org.apache.derby.impl.sql.compile.NestedLoopJoinStrategy

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

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.shared.common.sanity.SanityManager; 
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary; 

class NestedLoopJoinStrategy extends BaseJoinStrategy {
    NestedLoopJoinStrategy() {
        int i = 3;
	}


	/**
	 * @see JoinStrategy#feasible
	 *
	 * @exception StandardException		Thrown on error
	 */
	public boolean feasible(Optimizable innerTable,
							OptimizablePredicateList predList,
							Optimizer optimizer
							)
					throws StandardException 
	{
		/* Nested loop is feasible, except in the corner case
		 * where innerTable is a VTI that cannot be materialized
		 * (because it has a join column as a parameter) and
		 * it cannot be instantiated multiple times.
		 * RESOLVE - Actually, the above would work if all of 
		 * the tables outer to innerTable were 1 row tables, but
		 * we don't have that info yet, and it should probably
		 * be hidden in inner table somewhere.
		 * NOTE: A derived table that is correlated with an outer
		 * query block is not materializable, but it can be
		 * "instantiated" multiple times because that only has
		 * meaning for VTIs.
		 */
		if (innerTable.isMaterializable())
		{
			return true;
		}
		if (innerTable.supportsMultipleInstantiations())
		{
			return true;
		}
		return false;
	}

	/** @see JoinStrategy#multiplyBaseCostByOuterRows */
	public boolean multiplyBaseCostByOuterRows() {
		return true;
	}

	/**
	 * @see JoinStrategy#getBasePredicates
	 *
	 * @exception StandardException		Thrown on error
	 */
	public OptimizablePredicateList getBasePredicates(
									OptimizablePredicateList predList,
									OptimizablePredicateList basePredicates,
									Optimizable innerTable)
							throws StandardException {
		if (SanityManager.DEBUG) {
			SanityManager.ASSERT(basePredicates == null ||
								 basePredicates.size() == 0,
				"The base predicate list should be empty.");
		}

		if (predList != null) {
			predList.transferAllPredicates(basePredicates);
			basePredicates.classify(innerTable,
				innerTable.getCurrentAccessPath().getConglomerateDescriptor());
		}

		return basePredicates;
	}

	/** @see JoinStrategy#nonBasePredicateSelectivity */
	public double nonBasePredicateSelectivity(
										Optimizable innerTable,
										OptimizablePredicateList predList) {
		/*
		** For nested loop, all predicates are base predicates, so there
		** is no extra selectivity.
		*/
		return 1.0;
	}
	
	/**
	 * @see JoinStrategy#putBasePredicates
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void putBasePredicates(OptimizablePredicateList predList,
									OptimizablePredicateList basePredicates)
					throws StandardException {
		for (int i = basePredicates.size() - 1; i >= 0; i--) {
			OptimizablePredicate pred = basePredicates.getOptPredicate(i);

			predList.addOptPredicate(pred);
			basePredicates.removeOptPredicate(i);
		}
	}

	 

	/** @see JoinStrategy#maxCapacity */
	public int maxCapacity( int userSpecifiedCapacity,
                            int maxMemoryPerTable,
                            double perRowUsage) {
		return Integer.MAX_VALUE;
	}

	/** @see JoinStrategy#getName */
	public String getName() {
		return "NESTEDLOOP";
	}

	/** @see JoinStrategy#scanCostType */
	public int scanCostType() {
		return 0;
	}

	/** @see JoinStrategy#getOperatorSymbol */
    public  String  getOperatorSymbol() { return "*"; }

	/** @see JoinStrategy#resultSetMethodName */
    public String resultSetMethodName(
            boolean bulkFetch,
            boolean multiprobe,
            boolean validatingCheckConstraint) {

        if (validatingCheckConstraint) {
            return "getValidateCheckConstraintResultSet";
        } else if (bulkFetch) {
			return "getBulkTableScanResultSet";
        } else if (multiprobe) {
			return "getMultiProbeTableScanResultSet";
        } else {
			return "getTableScanResultSet";
        }
	}

	/** @see JoinStrategy#joinResultSetMethodName */
	public String joinResultSetMethodName() {
		return "getNestedLoopJoinResultSet";
	}

	/** @see JoinStrategy#halfOuterJoinResultSetMethodName */
	public String halfOuterJoinResultSetMethodName() {
		return "getNestedLoopLeftOuterJoinResultSet";
	}

	 

	/**
	 * @see JoinStrategy#divideUpPredicateLists
	 *
	 * @exception StandardException		Thrown on error
	 */
	public void divideUpPredicateLists(
					Optimizable				 innerTable,
					OptimizablePredicateList originalRestrictionList,
					OptimizablePredicateList storeRestrictionList,
					OptimizablePredicateList nonStoreRestrictionList,
					OptimizablePredicateList requalificationRestrictionList,
					DataDictionary			 dd
					) throws StandardException
	{
		/*
		** All predicates are store predicates.  No requalification is
		** necessary for non-covering index scans.
		*/
		originalRestrictionList.setPredicatesAndProperties(storeRestrictionList);
	}

	/**
	 * @see JoinStrategy#doesMaterialization
	 */
	public boolean doesMaterialization()
	{
		return false;
	}

    @Override
	public String toString() {
		return getName();
	}

	/**
	 * Can this join strategy be used on the
	 * outermost table of a join.
	 *
	 * @return Whether or not this join strategy
     * can be used on the outermost table of a join.
	 */
    @Override
	protected boolean validForOutermostTable()
	{
		return true;
	}
}
