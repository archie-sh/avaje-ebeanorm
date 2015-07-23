package com.avaje.ebeaninternal.api;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.OrderBy;
import com.avaje.ebean.PersistenceContextScope;
import com.avaje.ebean.Query;
import com.avaje.ebean.bean.BeanCollectionTouched;
import com.avaje.ebean.bean.CallStack;
import com.avaje.ebean.bean.ObjectGraphNode;
import com.avaje.ebean.bean.PersistenceContext;
import com.avaje.ebean.event.BeanQueryRequest;
import com.avaje.ebeaninternal.server.autofetch.AutoFetchManager;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssocMany;
import com.avaje.ebeaninternal.server.deploy.TableJoin;
import com.avaje.ebeaninternal.server.query.CancelableQuery;
import com.avaje.ebeaninternal.server.querydefn.NaturalKeyBindParam;
import com.avaje.ebeaninternal.server.querydefn.OrmQueryDetail;
import com.avaje.ebeaninternal.server.querydefn.OrmQueryProperties;

import java.sql.Timestamp;
import java.util.List;

/**
 * Object Relational query - Internal extension to Query object.
 */
public interface SpiQuery<T> extends Query<T> {

  enum Mode {
    NORMAL(false), LAZYLOAD_MANY(false), LAZYLOAD_BEAN(true), REFRESH_BEAN(true);

    Mode(boolean loadContextBean) {
      this.loadContextBean = loadContextBean;
    }

    private final boolean loadContextBean;

    public boolean isLoadContextBean() {
      return loadContextBean;
    }
  }

  /**
   * The type of query result.
   */
  enum Type {

    /**
     * Find by Id or unique returning a single bean.
     */
    BEAN,

    /**
     * Find iterate type query - findEach(), findIterate() etc.
     */
    ITERATE,

    /**
     * Find returning a List.
     */
    LIST,

    /**
     * Find returning a Set.
     */
    SET,

    /**
     * Find returning a Map.
     */
    MAP,

    /**
     * Find the Id's.
     */
    ID_LIST,

    /**
     * Find rowCount.
     */
    ROWCOUNT,

    /**
     * A subquery used as part of a where clause.
     */
    SUBQUERY
  }

  enum TemporalMode {
    /**
     * Query runs against current data (normal).
     */
    CURRENT,

    /**
     * Query runs potentially returning many versions of the same bean.
     */
    VERSIONS,

    /**
     * Query runs 'As Of' a given date time.
     */
    AS_OF;

    /**
     * Return the mode of the query of if null return CURRENT mode.
     */
    public static TemporalMode of(SpiQuery<?> query) {
      return (query != null) ? query.getTemporalMode() : TemporalMode.CURRENT;
    }
  }

  /**
   * Return the PersistenceContextScope that this query should use.
   * <p>
   * This can be null and in that case use the default scope.
   * </p>
   */
  PersistenceContextScope getPersistenceContextScope();

  /**
   * Return the default lazy load batch size.
   */
  int getLazyLoadBatchSize();

  /**
   * Return true if select all properties was used to ensure the property
   * invoking a lazy load was included in the query.
   */
  boolean selectAllForLazyLoadProperty();

  /**
   * Set the query mode.
   */
  void setMode(Mode m);

  /**
   * Return the query mode.
   */
  Mode getMode();

  /**
   * Return the Temporal mode for the query.
   */
  TemporalMode getTemporalMode();

  /**
   * Return true if this is a 'As Of' query.
   */
  boolean isAsOfQuery();

  /**
   * Return the asOf Timestamp which the query should run as.
   */
  Timestamp getAsOf();

  /**
   * Add a table alias for a @History entity involved in a 'As Of' query.
   */
  void addAsOfTableAlias(String tableAlias);

  /**
   * Return the list of table alias involved in a 'As Of' query that have @History support.
   */
  List<String> getAsOfTableAlias();

  /**
   * Return a listener that wants to be notified when the bean collection is
   * first used.
   */
  BeanCollectionTouched getBeanCollectionTouched();

  /**
   * Set a listener to be notified when the bean collection has been touched
   * (when the list/set/map is first used).
   */
  void setBeanCollectionTouched(BeanCollectionTouched notify);

  /**
   * Set the list of Id's that is being populated.
   * <p>
   * This is a mutating list of id's and we are setting this so that other
   * threads have access to the id's before the id query has finished.
   * </p>
   */
  void setIdList(List<Object> ids);

  /**
   * Return the list of Id's that is currently being fetched by a background
   * thread.
   */
  List<Object> getIdList();

  /**
   * Return a copy of the query.
   */
  SpiQuery<T> copy();

  /**
   * Return a copy of the query attaching to a different EbeanServer.
   */
  SpiQuery<T> copy(EbeanServer server);

  /**
   * Return the type of query (List, Set, Map, Bean, rowCount etc).
   */
  Type getType();

  /**
   * Set the query type (List, Set etc).
   */
  void setType(Type type);

  /**
   * Return a more detailed description of the lazy or query load.
   */
  String getLoadDescription();

  /**
   * Return the load mode (+lazy or +query).
   */
  String getLoadMode();

  /**
   * This becomes a lazy loading query for a many relationship.
   */
  void setLazyLoadForParents(BeanPropertyAssocMany<?> many);

  /**
   * Return the lazy loading 'many' property.
   */
  BeanPropertyAssocMany<?> getLazyLoadForParentsProperty();

  /**
   * Set the load mode (+lazy or +query) and the load description.
   */
  void setLoadDescription(String loadMode, String loadDescription);

  /**
   * Set the BeanDescriptor for the root type of this query.
   */
  void setBeanDescriptor(BeanDescriptor<?> desc);

  /**
   * Initialise/determine the joins required to support 'many' where clause predicates.
   */
  boolean initManyWhereJoins();

  /**
   * Return the joins required to support predicates on the many properties.
   */
  ManyWhereJoins getManyWhereJoins();

  /**
   * Return a Natural Key bind parameter if supported by this query.
   */
  NaturalKeyBindParam getNaturalKeyBindParam();

  /**
   * Set the query to select the id property only.
   */
  void setSelectId();

  /**
   * Set a filter to a join path.
   */
  void setFilterMany(String prop, ExpressionList<?> filterMany);

  /**
   * Remove the query joins from query detail.
   * <p>
   * These are registered with the Load Context.
   * </p>
   */
  List<OrmQueryProperties> removeQueryJoins();

  /**
   * Remove the lazy joins from query detail.
   * <p>
   * These are registered with the Load Context.
   * </p>
   */
  List<OrmQueryProperties> removeLazyJoins();

  /**
   * Set the path of the many when +query/+lazy loading query is executed.
   */
  void setLazyLoadManyPath(String lazyLoadManyPath);

  /**
   * Convert any many joins fetch joins to query joins.
   */
  void convertManyFetchJoinsToQueryJoins(boolean allowOne, int queryBatch);

  /**
   * Return the TransactionContext.
   * <p>
   * If no TransactionContext is present on the query then the
   * TransactionContext from the Transaction is used (transaction scoped
   * persistence context).
   * </p>
   */
  PersistenceContext getPersistenceContext();

  /**
   * Set an explicit TransactionContext (typically for a refresh query).
   * <p>
   * If no TransactionContext is present on the query then the
   * TransactionContext from the Transaction is used (transaction scoped
   * persistence context).
   * </p>
   */
  void setPersistenceContext(PersistenceContext transactionContext);

  /**
   * Return true if the query detail has neither select or joins specified.
   */
  boolean isDetailEmpty();

  /**
   * Return explicit autoFetch setting or null. If null then not explicitly
   * set so we use the default behaviour.
   */
  Boolean isAutofetch();

  /**
   * Set to true if you want to capture executed secondary queries.
   */
  void setLogSecondaryQuery(boolean logSecondaryQuery);

  /**
   * Return true if executed secondary queries should be captured.
   */
  boolean isLogSecondaryQuery();

  /**
   * Return the list of secondary queries that were executed.
   */
  List<SpiQuery<?>> getLoggedSecondaryQueries();

  /**
   * Log an executed secondary query.
   */
  void logSecondaryQuery(SpiQuery<?> query);

  /**
   * If return null then no autoFetch profiling for this query. If a
   * AutoFetchManager is returned this implies that profiling is turned on for
   * this query (and all the objects this query creates).
   */
  AutoFetchManager getAutoFetchManager();

  /**
   * This has the effect of turning on autoFetch profiling for this query.
   */
  void setAutoFetchManager(AutoFetchManager manager);

  /**
   * Return the origin point for the query.
   * <p>
   * This MUST be call prior to a query being changed via tuning. This is
   * because the queryPlanHash is used to identify the query point.
   * </p>
   */
  ObjectGraphNode setOrigin(CallStack callStack);

  /**
   * Set the profile point of the bean or collection that is lazy loading.
   * <p>
   * This enables use to hook this back to the original 'root' query by the
   * queryPlanHash and stackPoint.
   * </p>
   */
  void setParentNode(ObjectGraphNode node);

  /**
   * Set the property that invoked the lazy load and MUST be included in the
   * lazy loading query.
   */
  void setLazyLoadProperty(String lazyLoadProperty);

  /**
   * Return the property that invoked lazy load.
   */
  String getLazyLoadProperty();

  /**
   * Used to hook back a lazy loading query to the original query (query
   * point).
   * <p>
   * This will return null or an "original" query.
   * </p>
   */
  ObjectGraphNode getParentNode();

  /**
   * Return false when this is a lazy load or refresh query for a bean.
   * <p>
   * We just take/copy the data from those beans and don't collect autoFetch
   * usage profiling on those lazy load or refresh beans.
   * </p>
   */
  boolean isUsageProfiling();

  /**
   * Set to false if this query should not be included in the autoFetch usage
   * profiling information.
   */
  void setUsageProfiling(boolean usageProfiling);

  /**
   * Return the query name.
   */
  String getName();

  /**
   * Calculate a hash used by AutoFetch to identify when a query has changed
   * (and hence potentially needs a new tuned query plan to be developed).
   * <p>
   * Excludes bind values and occurs prior to AutoFetch potentially
   * tuning/modifying the query.
   * </p>
   */
  HashQueryPlan queryAutofetchHash(HashQueryPlanBuilder builder);

  /**
   * Identifies queries that are the same bar the bind variables.
   * <p>
   * This is used AFTER AutoFetch has potentially tuned the query. This is
   * used to identify and reused query plans (the final SQL string and
   * associated SqlTree object).
   * </p>
   * <p>
   * Excludes the actual bind values (as they don't effect the query plan).
   * </p>
   */
  HashQueryPlan queryPlanHash(BeanQueryRequest<?> request);

  /**
   * Calculate a hash based on the bind values used in the query.
   * <p>
   * Combined with queryPlanHash() to return getQueryHash (a unique hash for a
   * query).
   * </p>
   */
  int queryBindHash();

  /**
   * Identifies queries that are exactly the same including bind variables.
   */
  HashQuery queryHash();

  /**
   * Return true if this is a query based on a SqlSelect rather than
   * generated.
   */
  boolean isSqlSelect();

  /**
   * Return true if this is a RawSql query.
   */
  boolean isRawSql();

  /**
   * Return the Order By clause or null if there is none defined.
   */
  OrderBy<T> getOrderBy();

  /**
   * Return additional where clause. This should be added to any where clause
   * that was part of the original query.
   */
  String getAdditionalWhere();

  /**
   * Can return null if no expressions where added to the where clause.
   */
  SpiExpressionList<T> getWhereExpressions();

  /**
   * Can return null if no expressions where added to the having clause.
   */
  SpiExpressionList<T> getHavingExpressions();

  /**
   * Return additional having clause. Where raw String expressions are added
   * to having clause rather than Expression objects.
   */
  String getAdditionalHaving();

  /**
   * Returns true if either firstRow or maxRows has been set.
   */
  boolean hasMaxRowsOrFirstRow();

  /**
   * Return true if this query should use/check the bean cache.
   */
  Boolean isUseBeanCache();

  /**
   * Return true if this query should use/check the query cache.
   */
  boolean isUseQueryCache();

  /**
   * Return true if the beans from this query should be loaded into the bean
   * cache.
   */
  boolean isLoadBeanCache();

  /**
   * Return true if the beans returned by this query should be read only.
   */
  Boolean isReadOnly();

  /**
   * Return the query timeout.
   */
  int getTimeout();

  /**
   * Return the bind parameters.
   */
  BindParams getBindParams();

  /**
   * Get the orm query as a String. Only available if the query was built from
   * a string.
   */
  String getQuery();

  /**
   * Replace the query detail. This is used by the autoFetch feature to as a
   * fast way to set the query properties and joins.
   * <p>
   * Note care must be taken to keep the where, orderBy, firstRows and maxRows
   * held in the detail attributes.
   * </p>
   */
  void setDetail(OrmQueryDetail detail);

  /**
   * Autofetch tune the detail specifying properties to select on already defined joins
   * and adding extra joins where they are missing.
   */
  boolean tuneFetchProperties(OrmQueryDetail detail);

  /**
   * Set to true if this query has been tuned by autoFetch.
   */
  void setAutoFetchTuned(boolean autoFetchTuned);

  /**
   * Return the query detail.
   */
  OrmQueryDetail getDetail();

  TableJoin getIncludeTableJoin();

  void setIncludeTableJoin(TableJoin includeTableJoin);

  /**
   * Return the property used to specify keys for a map.
   */
  String getMapKey();

  /**
   * Return the maximum number of rows to return in the query.
   */
  int getMaxRows();

  /**
   * Return the index of the first row to return in the query.
   */
  int getFirstRow();

  /**
   * Internally set by Ebean when this query must use the DISTINCT keyword.
   * <p/>
   * This does not exclude/remove the use of the id property.
   */
  Query<T> setSqlDistinct(boolean sqlDistinct);

  /**
   * Return true if this query has been specified by a user or internally by Ebean to use DISTINCT.
   */
  boolean isDistinctQuery();

  /**
   * Return true if this query has been specified by a user to use DISTINCT.
   */
  boolean isDistinct();

  /**
   * Set default select clauses where none have been explicitly defined.
   */
  void setDefaultSelectClause();

  /**
   * Return the where clause from a parsed string query.
   */
  String getRawWhereClause();

  /**
   * Set the generated sql for debug purposes.
   */
  void setGeneratedSql(String generatedSql);

  /**
   * Return the hint for Statement.setFetchSize().
   */
  int getBufferFetchSizeHint();

  /**
   * Return true if this is a query executing in the background.
   */
  boolean isFutureFetch();

  /**
   * Set to true to indicate the query is executing in a background thread
   * asynchronously.
   */
  void setFutureFetch(boolean futureFetch);

  /**
   * Set the underlying cancelable query (with the PreparedStatement).
   */
  void setCancelableQuery(CancelableQuery cancelableQuery);

  /**
   * Return true if this query has been cancelled.
   */
  boolean isCancelled();

  /**
   * Return root table alias set by {@link #alias(String)} command.
   */
  String getAlias();
}
