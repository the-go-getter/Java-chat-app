package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GroupUserTest {
  private GroupUser groupUser;
  private ChatGroup nullGroup;
  private User nullUser;
  private User alice;
  private ChatGroup chatGroup;

  @Before
  public void setUp() throws Exception {
    nullUser = null;
    nullGroup = null;
    alice = new User("alice", "alice");
    chatGroup = new ChatGroup("testGroup");
    groupUser = new GroupUser(alice, chatGroup);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullUser(){
    new GroupUser(nullUser,chatGroup);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullGroup(){
    new GroupUser(alice,nullGroup);
  }

  @Test
  public void testConstrcutor(){
    GroupUser g = new GroupUser(alice,chatGroup);
    assertEquals("alice",g.getUser().getName());
    assertEquals("testGroup",g.getChatGroup().getGroupName());
  }

  @Test
  public void testSetModerator(){
    assertEquals(false,groupUser.isModerator());
    groupUser.setModerator(true);
    assertEquals(true,groupUser.isModerator());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetUserNull(){
    groupUser.setUser(nullUser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetGroupNull(){
    groupUser.setChatGroup(nullGroup);
  }

  @Test
  public void testSetUser(){
    assertEquals("alice",groupUser.getUser().getName());
    User bob = new User("bob","bob");
    groupUser.setUser(bob);
    assertEquals("bob",groupUser.getUser().getName());
  }

  @Test
  public void testSetGroup(){
    assertEquals("testGroup",groupUser.getChatGroup().getGroupName());
    ChatGroup cg = new ChatGroup("newChat");
    groupUser.setChatGroup(cg);
    assertEquals("newChat",groupUser.getChatGroup().getGroupName());
  }







}