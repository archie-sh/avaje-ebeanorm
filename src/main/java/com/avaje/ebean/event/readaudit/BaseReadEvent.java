package com.avaje.ebean.event.readaudit;

/**
 * Created by rob on 28/08/15.
 */
public abstract class BaseReadEvent {

  protected String beanType;

  protected String queryKey;

  protected String bindLog;

  public BaseReadEvent(String beanType, String queryKey, String bindLog) {
    this.beanType = beanType;
    this.queryKey = queryKey;
    this.bindLog = bindLog;
  }

  /**
   * Constructor for JSON tools.
   */
  public BaseReadEvent() {

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

  public String getBindLog() {
    return bindLog;
  }

  public void setBindLog(String bindLog) {
    this.bindLog = bindLog;
  }
}
