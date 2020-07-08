package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GroupUserIdTest {
  private GroupUserId groupUserId;
  private String nullString;

  @Before
  public void setUp() throws Exception {
    nullString = null;
    groupUserId = new GroupUserId("test", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullArg() {
    new GroupUserId(nullString, 1);
  }

  @Test
  public void testConstructor() {
    GroupUserId guid = new GroupUserId("testuser", 2);
    assertEquals("testuser", guid.getUsername());
    assertEquals(2, guid.getGroupid());
  }

  @Test
  public void testSetGroupId() {
    assertEquals(1, groupUserId.getGroupid());
    groupUserId.setGroupid(100000);
    assertEquals(100000, groupUserId.getGroupid());
  }

  @Test
  public void testSetUsername() {
    assertEquals("test", groupUserId.getUsername());
    groupUserId.setUsername("tester");
    assertEquals("tester", groupUserId.getUsername());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetUsernameNull() {
    groupUserId.setUsername(nullString);
  }

  @Test
  public void testEquals() {
    GroupUserId groupUserId1 = new GroupUserId("test", 1);
    assertEquals(groupUserId, groupUserId1);
    assertEquals(groupUserId1.hashCode(), groupUserId.hashCode());
  }

  @Test
  public void testNotEqualsId() {
    GroupUserId groupUserId1 = new GroupUserId("test", 2);
    assertNotEquals(groupUserId, groupUserId1);
    assertNotEquals(groupUserId1.hashCode(), groupUserId.hashCode());
  }

  @Test
  public void testNotEqualsName() {
    GroupUserId groupUserId1 = new GroupUserId("tester", 1);
    assertNotEquals(groupUserId, groupUserId1);
    assertNotEquals(groupUserId1.hashCode(), groupUserId.hashCode());
  }

  @Test
  public void testIdentical() {
    assertEquals(groupUserId, groupUserId);
  }

  @Test
  public void testNotEqualsWrongObject() {
    GroupUser gu = new GroupUser();
    assertNotEquals(gu, groupUserId);
  }

  @Test
  public void testNotEqualsNullObject() {
    assertNotEquals(nullString, groupUserId);
  }
}