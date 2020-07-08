package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChatGroupTest {
  private ChatGroup chatGroup;
  private String nullString;
  private List<GroupUser> nullGroupUser;
  private List<GroupUser> groupUsers;
  private User nullUser;
  private User alice;
  private User bob;

  @Before
  public void setUp() throws Exception {
    chatGroup = new ChatGroup("testChat");
    nullString = null;
    nullGroupUser = null;
    nullUser = null;
    alice = new User("alice", "alice");
    bob = new User("bob", "bob");
    groupUsers = new ArrayList<>();
    groupUsers.add(new GroupUser(alice, chatGroup));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNull() {
    new ChatGroup(nullString);
  }

  @Test
  public void testConstructor() {
    ChatGroup cg = new ChatGroup("chat");
    assertEquals("chat", cg.getGroupName());
  }

  @Test
  public void testSetId() {
    chatGroup.setId(1000);
    assertEquals(1000, chatGroup.getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetGroupNameNull() {
    chatGroup.setGroupName(nullString);
  }

  @Test
  public void testSetGroupName() {
    assertEquals("testChat", chatGroup.getGroupName());
    chatGroup.setGroupName("test");
    assertEquals("test", chatGroup.getGroupName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetGroupNull() {
    chatGroup.setGroupUsers(nullGroupUser);
  }

  @Test
  public void testSetGroup() {
    assertEquals(0, chatGroup.getGroupUsers().size());
    chatGroup.setGroupUsers(groupUsers);
    assertEquals(1, chatGroup.getGroupUsers().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddUserNull() {
    chatGroup.addUser(nullUser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddModeratorNull() {
    chatGroup.addModerator(nullUser);
  }

  @Test
  public void testAddUser() {
    chatGroup.addUser(alice);
    assertEquals("alice", chatGroup.getGroupUsers().get(0).getUser().getName());
    assertFalse(chatGroup.getGroupUsers().get(0).isModerator());
  }

  @Test
  public void testAddModerator() {
    chatGroup.addUser(alice);
    chatGroup.addModerator(bob);
    assertEquals("bob", chatGroup.getGroupUsers().get(1).getUser().getName());
    assertTrue(chatGroup.getGroupUsers().get(1).isModerator());
  }
}