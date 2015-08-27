package com.avaje.ebean.event.readaudit;

import java.util.List;

/**
 * Log that the query was executed
 */
public interface ReadAuditQuery {

  void addQueryPlan(String queryKey, String sql);

  /**
   * Audit a find bean query.
   */
  void auditBean(String queryKey, String bindLog, String baseTable, Object id);

  /**
   * Audit a find many query.
   */
  void auditMany(String queryKey, String bindLog, String baseTable, List<Object> ids);
}
