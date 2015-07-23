package com.avaje.ebeaninternal.server.query;

import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.server.deploy.DbReadContext;
import com.avaje.ebeaninternal.server.deploy.DbSqlContext;

import java.sql.SQLException;
import java.util.List;

public interface SqlTreeNode {

  String COMMA = ", ";

  /**
   * Build the select chain for a RawSql query.
   */
  void buildRawSqlSelectChain(List<String> selectChain);

  /**
   * Append the required column information to the SELECT part of the sql
   * statement.
   */
  void appendSelect(DbSqlContext ctx, boolean subQuery);

  /**
   * Append to the FROM part of the sql.
   */
  void appendFrom(DbSqlContext ctx, SqlJoinType joinType);

  /**
   * Append any where predicates for inheritance.
   */
  void appendWhere(DbSqlContext ctx);

  /**
   * Recurse through the tree adding an table alias' for @History entity beans.
   */
  void addAsOfTableAlias(SpiQuery<?> query);

  /**
   * Load the appropriate information from the SqlSelectReader.
   * <p>
   * At a high level this actually controls the reading of the data from the
   * jdbc resultSet and putting it into the bean etc.
   * </p>
   */
  EntityBean load(DbReadContext ctx, EntityBean localBean, EntityBean contextBean) throws SQLException;

}
