package com.avaje.ebean.event.readaudit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class DefaultQueryAudit implements ReadAuditLogger {

  private static final Logger queryLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAuditQuery");

  private static final Logger planLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAuditPlan");

  @Override
  public void logQueryPlan(ReadAuditQueryPlan queryPlan) {
    planLogger.info(queryPlan.toString());
  }

  @Override
  public void auditBean(ReadBeanEvent beanEvent) {
    queryLogger.info(beanEvent.toString());
  }

  @Override
  public void auditMany(ReadManyEvent readMany) {
    queryLogger.info(readMany.toString());
  }
}
