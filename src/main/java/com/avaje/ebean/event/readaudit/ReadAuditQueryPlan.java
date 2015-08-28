package com.avaje.ebean.event.readaudit;

/**
 * Created by rob on 28/08/15.
 */
public class ReadAuditQueryPlan {

  String beanType;

  String queryKey;

  String sql;

  public ReadAuditQueryPlan(String beanType, String queryKey, String sql) {
    this.beanType = beanType;
    this.queryKey = queryKey;
    this.sql = sql;
  }

  /**
   * Construct for JSON tools.
   */
  public ReadAuditQueryPlan() {
  }

  public String toString() {
    return "beanType:" + beanType + " queryKey:" + queryKey + " sql:" + sql;
  }

  public String getBeanType() {
    return beanType;
  }

  public void setBeanType(String beanType) {
    this.beanType = beanType;
  }

  public String getQueryKey() {
    return queryKey;
  }

  public void setQueryKey(String queryKey) {
    this.queryKey = queryKey;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
