package com.avaje.ebeaninternal.server.readaudit;

import com.avaje.ebean.event.readaudit.ReadAuditLogger;
import com.avaje.ebean.event.readaudit.ReadAuditQueryPlan;
import com.avaje.ebean.event.readaudit.ReadBeanEvent;
import com.avaje.ebean.event.readaudit.ReadManyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class DefaultQueryAudit implements ReadAuditLogger {

  private static final Logger queryLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAuditQuery");

  private static final Logger planLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAudit");

  @Override
  public void queryPlan(ReadAuditQueryPlan queryPlan) {
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
