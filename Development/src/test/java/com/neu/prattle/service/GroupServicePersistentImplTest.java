package com.neu.prattle.service;

import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GroupServicePersistentImplTest {

  private static ChatGroup nullChatGroup;
  private static User nullUser;
  private static String nullUsername;
  private ChatGroup group;
  private User alice;
  private User bob;
  private User charlie;

  private static UserService userService;
  private static GroupService groupService;

  @BeforeClass
  public static void setupAll() {
    nullChatGroup = null;
    nullUser = null;
    nullUsername = null;
    groupService = GroupServicePersistentImpl.getInstance();
    userService = UserServicePersistentImpl.getInstance();
  }

  @Before
  public void setUp() throws Exception {
    group = new ChatGroup("test_groups");
    alice = new User("alice_gs", "alice");
    bob = new User("Bob_gs", "bob");
    charlie = new User("charlie_gs", "charlie");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullGroup() {
    groupService.createGroup(nullChatGroup);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddUserToNullGroup() {
    groupService.addGroupUser(nullChatGroup, alice);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullUserToGroup() {
    groupService.addGroupUser(nullChatGroup, alice);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteNullGroup() {
    groupService.deleteGroupUser(nullChatGroup, "alice_g");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteNullUserFromGroup() {
    groupService.deleteGroupUser(group, nullUsername);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetModeratorToGroupNull() {
    groupService.addGroupModerator(nullChatGroup, bob);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetModeratorAsUserIsNull() {
    groupService.addGroupModerator(group, nullUser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveModeratorNullGroup() {
    groupService.removeGroupModerator(nullChatGroup, bob.getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveModeratorNullUsername() {
    groupService.removeGroupModerator(group, nullUsername);
  }

  @Test
  public void testCreateAndDeleteGroup() {
    groupService.createGroup(group);

    long id = group.getId();
    ChatGroup readGroup = groupService.findGroup(id);

    assertEquals("test_groups", readGroup.getGroupName());

    groupService.deleteGroup(id);

    assertNull(groupService.findGroup(id));
  }

  @Test
  public void testDeleteNonexistGroup() {
    try {
      groupService.deleteGroup(Integer.MIN_VALUE);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetGroupModerator() {

    groupService.createGroup(group);

    groupService.addGroupModerator(group, charlie);

    ChatGroup cg = groupService.findGroup(group.getId());
    Collection<GroupUser> moderators = cg.getModerators();
    boolean found = false;
    for (GroupUser mod : moderators) {
      if (mod.getUser().getName().equals(charlie.getName())) {
        found = true;
        break;
      }
    }
    assertTrue(found);

    groupService.deleteGroup(group.getId());
  }

  @Test
  public void testSetAndDeleteGroupModerator() {
    groupService.createGroup(group);

    groupService.addGroupModerator(group, bob);
    groupService.addGroupModerator(group, alice);
    ChatGroup cg = groupService.findGroup(group.getId());
    Collection<GroupUser> moderators = cg.getModerators();
    boolean found = false;
    for (GroupUser mod : moderators) {
      if (mod.getUser().getName().equals(bob.getName())) {
        found = true;
        break;
      }
    }
    assertTrue(found);

    groupService.removeGroupModerator(group, bob.getName());

    ChatGroup cg2 = groupService.findGroup(group.getId());
    moderators = cg2.getModerators();

    found = false;
    for (GroupUser mod : moderators) {
      if (mod.getUser().getName().equals(bob.getName())) {
        found = true;
        System.out.print("found ");
        System.out.print(mod.getUser().getName());
        break;
      }
    }
    assertFalse(found);

    assertEquals(1, cg.getModerators().size());
    assertEquals("alice_gs", cg.getGroupUsers().get(0).getUser().getName());

    groupService.deleteGroup(group.getId());
    userService.deleteUser(alice.getName());
  }

  @Test
  public void testAddAndDeleteUser() {
    groupService.createGroup(group);

    userService.addUser(alice);

    assertEquals(0, alice.getGroups().size());

    groupService.addGroupUser(group, alice);

    assertEquals(1, group.getGroupUsers().size());
    // Although alice is a managed object, it is not updated yet.
    assertEquals(0, alice.getGroups().size());

    groupService.addGroupModerator(group, bob);

    ChatGroup cg = groupService.findGroup(group.getId());

    assertEquals("alice_gs", cg.getGroupUsers().get(1).getUser().getName());

    if (userService.findUserByName(alice.getName()).isPresent()) {
      alice = userService.findUserByName(alice.getName()).get();
      assertEquals(1, alice.getGroups().size());
    }

    groupService.deleteGroupUser(group, "alice_gs");

    cg = groupService.findGroup(group.getId());

    assertEquals(1, cg.getGroupUsers().size());

    /*
     * A tricky part is the previous alice is outdated.
     */
    if (userService.findUserByName(alice.getName()).isPresent()) {
      alice = userService.findUserByName(alice.getName()).get();
      assertEquals(0, alice.getGroups().size());
    }

    userService.deleteUser(alice.getName());

    groupService.deleteGroup(group.getId());

  }

  @Test
  public void testAddAndDeleteUserOverride() {
    groupService.createGroup(group);
    userService.addUser(alice);
    groupService.addGroupUser(group, alice);

    groupService.addGroupModerator(group, charlie);

    ChatGroup cg = groupService.findGroup(group.getId());

    assertEquals("alice_gs", cg.getGroupUsers().get(1).getUser().getName());

    // We can delete managed object user alice.
    groupService.deleteGroupUser(group, alice);

    cg = groupService.findGroup(group.getId());

    assertEquals(1, cg.getGroupUsers().size());

    /*
     * A tricky part is the previous alice is outdated.
     */
    if (userService.findUserByName(alice.getName()).isPresent()) {
      alice = userService.findUserByName(alice.getName()).get();
      assertEquals(0, alice.getGroups().size());
    }

    userService.deleteUser(alice.getName());
    groupService.deleteGroup(group.getId());
  }


  @After
  public void tearDown() throws Exception {
  }
}