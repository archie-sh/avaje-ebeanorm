package com.avaje.ebean.event.readaudit;

import java.util.List;

/**
 * Log that the query was executed
 */
public interface ReadAuditQuery {

  /**
   * Audit a find bean query.
   */
  void auditBean(String auditQueryKey, String bindLog, String baseTable, Object id);

  /**
   * Audit a find many query.
   */
  void auditMany(String auditQueryKey, String bindLog, String baseTable, List<Object> ids);
}
