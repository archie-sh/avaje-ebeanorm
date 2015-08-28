package com.avaje.ebean.event.readaudit;

/**
 * Log that the query was executed
 */
public interface ReadAuditLogger {

  void logQueryPlan(ReadAuditQueryPlan queryPlan);

  /**
   * Audit a find bean query that returned a bean.
   * <p>
   * Finds that did not return a bean are excluded.
   * </p>
   */
  void auditBean(ReadBeanEvent readBean);

  /**
   * Audit a find many query that returned some beans.
   * <p>
   * Finds that did not return any beans are excluded.
   * </p>
   * <p>
   * For large queries executed via findEach() etc the ids are collected in batches
   * and logged. Hence the ids list has a maximum size of the batch size.
   * </p>
   */
  void auditMany(ReadManyEvent readMany);
}
