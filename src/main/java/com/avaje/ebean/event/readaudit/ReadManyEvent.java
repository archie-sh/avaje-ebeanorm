package com.avaje.ebean.event.readaudit;

import java.util.List;

/**
 */
public class ReadManyEvent extends BaseReadEvent {

  protected List<Object> ids;

  public ReadManyEvent(String beanType, String queryKey, String bindLog, List<Object> ids) {
    super(beanType, queryKey, bindLog);
    this.ids = ids;
  }

  /**
   * Construct for JSON tools.
   */
  public ReadManyEvent() {
  }

  public String toString() {
    return "beanType:" + beanType + " queryKey:" + queryKey  + " bind:" + bindLog + " objectIds:" + ids;
  }

  public List<Object> getIds() {
    return ids;
  }

  public void setIds(List<Object> ids) {
    this.ids = ids;
  }
}
