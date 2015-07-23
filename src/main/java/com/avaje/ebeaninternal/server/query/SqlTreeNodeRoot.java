package com.avaje.ebeaninternal.server.query;

import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssocMany;
import com.avaje.ebeaninternal.server.deploy.DbSqlContext;
import com.avaje.ebeaninternal.server.deploy.TableJoin;

import java.util.List;

/**
 * Represents the root node of the Sql Tree.
 */
public final class SqlTreeNodeRoot extends SqlTreeNodeBean {

  private final TableJoin includeJoin;

  /**
   * Specify for SqlSelect to include an Id property or not.
   */
  public SqlTreeNodeRoot(BeanDescriptor<?> desc, SqlTreeProperties props, List<SqlTreeNode> myList, boolean withId,
                         TableJoin includeJoin, BeanPropertyAssocMany<?> many, SpiQuery.TemporalMode temporalMode) {
    super(null, null, desc, props, myList, withId, many, temporalMode);
    this.includeJoin = includeJoin;
  }

  public SqlTreeNodeRoot(BeanDescriptor<?> desc, SqlTreeProperties props, List<SqlTreeNode> myList, boolean withId) {
    super(null, null, desc, props, myList, withId, null, null);
    this.includeJoin = null;
  }

  /**
   * For the root node there is no join type or on clause etc.
   */
  @Override
  public SqlJoinType appendFromBaseTable(DbSqlContext ctx, SqlJoinType joinType) {

    ctx.append(desc.getBaseTable(temporalMode));
    ctx.append(" ").append(baseTableAlias);

    if (includeJoin != null) {
      String a1 = baseTableAlias;
      String a2 = "int_"; // unique alias for intersection join
      includeJoin.addJoin(joinType, a1, a2, ctx);
    }

    return joinType;
  }

}
