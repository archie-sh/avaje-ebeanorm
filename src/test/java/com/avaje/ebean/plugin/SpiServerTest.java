package com.avaje.ebean.plugin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.tests.model.basic.Customer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class SpiServerTest {

  @Test
  public void test() {

    EbeanServer defaultServer = Ebean.getDefaultServer();
    SpiServer pluginApi = defaultServer.getPluginApi();

    SpiBeanType<Customer> beanType = pluginApi.getBeanType(Customer.class);
    assertEquals("o_customer", beanType.getBaseTable());
    assertNotNull(pluginApi.getDatabasePlatform());
    assertNull(beanType.getFindController());
    assertNotNull(beanType.getPersistController());
    assertNull(beanType.getPersistListener());
    assertNull(beanType.getQueryAdapter());

    Customer customer = new Customer();
    customer.setId(42);

    assertEquals(42, beanType.getBeanId(customer));

    List<? extends SpiBeanType<?>> beanTypes = pluginApi.getBeanTypes("o_customer");
    assertEquals(1, beanTypes.size());
    assertSame(beanType, beanTypes.get(0));

    List<? extends SpiBeanType<?>> allTypes = pluginApi.getBeanTypes();
    assertTrue(!allTypes.isEmpty());
  }

}