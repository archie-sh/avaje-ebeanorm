package com.avaje.ebean.event.readaudit;

/**
 */
public class ReadBeanEvent extends BaseReadEvent {

  protected Object id;

  public ReadBeanEvent(String beanType, String queryKey, String bindLog, Object id) {
    super(beanType, queryKey, bindLog);
    this.id = id;
  }

  public String toString() {
    return "beanType:" + beanType + " queryKey:" + queryKey  + " bind:" + bindLog + " objectId:" + id;
  }

  public Object getId() {
    return id;
  }

  public void setId(Object id) {
    this.id = id;
  }
}
