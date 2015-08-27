package com.avaje.ebean.event.readaudit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 */
public class DefaultQueryAudit implements ReadAuditQuery {

  private static final Logger queryLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAuditQuery");

  private static final Logger planLogger = LoggerFactory.getLogger("org.avaje.ebean.ReadAuditPlan");

  @Override
  public void addQueryPlan(String queryKey, String sql) {
    planLogger.info("queryKey:" + queryKey+" sql:"+sql);
  }

  @Override
  public void auditBean(String queryKey, String bindLog, String baseTable, Object id) {

    queryLogger.info("object:" + baseTable + " queryKey:" + queryKey + " bind:" + bindLog + " objectId:" + id);
  }

  @Override
  public void auditMany(String queryKey, String bindLog, String baseTable, List<Object> ids) {

    queryLogger.info("object:" + baseTable + " queryKey:" + queryKey + " bind:" + bindLog + " objectIds:" + ids);
  }
}
