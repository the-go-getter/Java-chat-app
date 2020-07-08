package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrivilegeTest {
  private Privilege privilege;
  private String nullString;

  @Before
  public void setup() {
    privilege = new Privilege("test");
    nullString = null;
  }


  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNull() {
    Privilege p = new Privilege(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetUsernameNull() {
    privilege.setUsername(nullString);
  }

  @Test
  public void testGetname() {
    assertEquals("test", privilege.getUsername());
  }

  @Test
  public void testSetname() {
    privilege.setUsername("set");
    assertEquals("set", privilege.getUsername());
  }
}