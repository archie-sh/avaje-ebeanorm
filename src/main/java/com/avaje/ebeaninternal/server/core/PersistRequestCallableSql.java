package com.avaje.ebeaninternal.server.core;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

import com.avaje.ebean.CallableSql;
import com.avaje.ebeaninternal.api.BindParams;
import com.avaje.ebeaninternal.api.BindParams.Param;
import com.avaje.ebeaninternal.api.SpiCallableSql;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiTransaction;
import com.avaje.ebeaninternal.api.TransactionEventTable;
import com.avaje.ebeaninternal.server.persist.PersistExecute;

/**
 * Persist request specifically for CallableSql.
 */
public final class PersistRequestCallableSql extends PersistRequest {

	private final SpiCallableSql callableSql;

	private int rowCount;

	private String bindLog;

	private CallableStatement cstmt;

	private BindParams bindParam;

	/**
	 * Create.
	 */
	public PersistRequestCallableSql(SpiEbeanServer server,
			CallableSql cs, SpiTransaction t, PersistExecute persistExecute) {
		
		super(server, t, persistExecute);
		this.type = PersistRequest.Type.CALLABLESQL;
		this.callableSql = (SpiCallableSql)cs;
	}
	
	@Override
	public int executeOrQueue() {
		return executeStatement();
	}

	@Override
	public int executeNow() {
		return persistExecute.executeSqlCallable(this);
	}

	/**
	 * Return the CallableSql.
	 */
	public SpiCallableSql getCallableSql() {
		return callableSql;
	}

	/**
	 * The the log of bind values.
	 */
	public void setBindLog(String bindLog) {
		this.bindLog = bindLog;
	}

	/**
	 * Note the rowCount of the execution.
	 */
	public void checkRowCount(int count) {
		this.rowCount = count;
	}

	/**
	 * Only called for insert with generated keys.
	 */
	public void setGeneratedKey(Object idValue) {
	}

	/**
	 * Perform post execute processing for the CallableSql.
	 */
	public void postExecute() {

		if (transaction.isLogSummary()) {
			String m = "CallableSql label[" + callableSql.getLabel() + "]" + " rows[" + rowCount+ "]" + " bind[" + bindLog + "]";
			transaction.logSummary(m);
		}

		// register table modifications with the transaction event
		TransactionEventTable tableEvents = callableSql.getTransactionEventTable();
		
		if (tableEvents != null && !tableEvents.isEmpty()) {
			transaction.getEvent().add(tableEvents);
		} else {
			transaction.markNotQueryOnly();
		}

	}

	/**
	 * These need to be set for use with Non-batch execution. Specifically to
	 * read registered out parameters and potentially handle the
	 * executeOverride() method.
	 */
	public void setBound(BindParams bindParam, CallableStatement cstmt) {
		this.bindParam = bindParam;
		this.cstmt = cstmt;
	}

	/**
	 * Execute the statement in normal non batch mode.
	 */
	public int executeUpdate() throws SQLException {

		// check to see if the execution has been overridden
		// only works in non-batch mode
		if (callableSql.executeOverride(cstmt)) {
			return -1;
			// // been overridden so just return the rowCount
			// rowCount = callableSql.getRowCount();
			// return rowCount;
		}

		rowCount = cstmt.executeUpdate();

		// only read in non-batch mode
		readOutParams();

		return rowCount;
	}

	private void readOutParams() throws SQLException {

		List<Param> list = bindParam.positionedParameters();
		int pos = 0;

		for (int i = 0; i < list.size(); i++) {
			pos++;
			BindParams.Param param = list.get(i);
			if (param.isOutParam()) {
				Object outValue = cstmt.getObject(pos);
				param.setOutValue(outValue);
			}
		}
	}

}
