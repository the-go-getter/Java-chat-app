package com.neu.prattle.database;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DataRepositoryChatGroupTest {
  private static DataRepository repo;
  private static ChatGroup nullChatGroup;
  private static ChatGroup chatGroup;
  private User alice;
  private User bob;
  private User charlie;
  private static List<GroupUser> nullList;
  private static GroupUser nullGroupUser;


  @BeforeClass
  public static void setupBeforeClass() {
    repo = DataRepository.getDataRepository();
    nullChatGroup = null;
    nullList = null;
    nullGroupUser = null;
  }

  @Before
  public void setUp() {
    chatGroup = new ChatGroup("testGroup");
    alice = new User("alice1", "alice");
    bob = new User("bob1", "bob");
    charlie = new User("charlie1", "charlie");
  }

  @AfterClass
  public static void tearDown() {
    repo.deleteGroupById(chatGroup.getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNullGroup() {
    repo.createGroup(nullChatGroup);
  }

  @Test
  public void testCreateNormalGroup() {
    repo.createGroup(chatGroup);
    assertEquals("testGroup", repo.findGroupById(chatGroup.getId()).getGroupName());

    assertNotNull(repo.findGroupByName("testGroup"));

    try{
      repo.createGroup(chatGroup);
      fail();
    }catch (GroupAlreadyPresentException e){
      assertTrue(true);
    }
  }

  @Test
  public void testCreateGroup() {

    ChatGroup ab = new ChatGroup("ab3");
    ChatGroup bc = new ChatGroup("bc");
    ChatGroup ac = new ChatGroup("ac");


    GroupUser groupUserAliceAB = new GroupUser(alice, ab);
    GroupUser groupUserBobAB = new GroupUser(bob, ab);

    /*
    Here we can not use two group user objects. One in alice.getGroups()
    and the other in ab.getGroupUsers()
    This way will create duplicate primary key error.
    */

    ab.addUser(charlie);
    ab.addUser(bob);
    ab.addModerator(alice);

    assertEquals(0, alice.getGroups().size());
    assertEquals(3,ab.getGroupUsers().size());


    repo.createGroup(ab);

    alice = repo.findUserByUsername("alice1");
    assertEquals(1,alice.getGroups().size());


    ChatGroup cg = repo.findGroupById(ab.getId());
    List<GroupUser> gus = cg.getGroupUsers();
    assertEquals("alice1",gus.get(0).getUser().getName());
    assertEquals("bob1",gus.get(1).getUser().getName());
    assertEquals("charlie1",gus.get(2).getUser().getName());
  }

  @Test
  public void testUserSetGroups() {
    assertEquals(0, alice.getGroups().size());
    List<GroupUser> gu = new ArrayList<>();
    gu.add(new GroupUser(alice, chatGroup));
    alice.setGroups(gu);
    assertEquals(1, alice.getGroups().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUserSetGroupsNull() {
    alice.setGroups(nullList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateGroupNull() {
    repo.updateGroup(nullChatGroup);
  }

  @Test
  public void testUpdateGroup() {
    ChatGroup cg = new ChatGroup("toBeUpdated");
    repo.createGroup(cg);
    long id = cg.getId();
    cg.setGroupName("Updated");
    repo.updateGroup(cg);
    ChatGroup cgNew = repo.findGroupById(id);
    assertEquals("Updated", cgNew.getGroupName());

    assertNotNull(repo.findGroupByName("Updated"));
    assertNull(repo.findGroupByName("toBeUpdated"));
  }

  @Test
  public void testUpdateGroupWithModerator() {
    ChatGroup cg = new ChatGroup("toBeUpdated2");
    repo.createGroup(cg);
    long id = cg.getId();
    ChatGroup cgNew = repo.findGroupById(id);
    assertEquals(0, cgNew.getModerators().size());




    cg.setGroupName("Updated2");
    cg.addModerator(bob);
    cg.addModerator(alice);
    repo.updateGroup(cg);
    cgNew = repo.findGroupById(id);
    assertEquals(2, cgNew.getModerators().size());
    assertEquals("Updated2", cgNew.getGroupName());
    assertEquals("alice1", cgNew.getModerators().iterator().next().getUser().getName());


    cg.removeModerator(alice.getName());
    repo.updateGroup(cg);

    cgNew = repo.findGroupById(id);
//    assertEquals(1, cgNew.getModerators().size());
    assertEquals("alice1", cgNew.getModerators().iterator().next().getUser().getName());
  }

  @Test
  public void testRemoveLargeId() {
    try {
      repo.deleteGroupById(Integer.MAX_VALUE);
    } catch (Exception e) {
      fail();
    }
    assertTrue(true);
  }

  @Test
  public void testRemoveGroup() {
    ChatGroup cg = new ChatGroup("toBeRemoved");
    repo.createGroup(cg);
    long id = cg.getId();
    repo.deleteGroupById(id);
    ChatGroup cgNew = repo.findGroupById(id);
    assertNull(cgNew);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGroupUserNull() {
    repo.createGroupUser(nullGroupUser);
  }

  @Test
  public void testCreateGroupUser() {
    User ed = new User("ed", "ed");
    GroupUser gu = new GroupUser(ed, chatGroup);
    repo.createGroupUser(gu);

    repo.updateGroupUser(gu);

    assertEquals(0, ed.getGroups().size());
    User edNew = repo.findUserByUsername("ed");
    assertEquals(1, edNew.getGroups().size());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testDeleteNullGroupUser(){
    repo.deleteGroupUser(nullGroupUser);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateNullGroupUser(){
    repo.updateGroupUser(nullGroupUser);
  }

  @Test(expected = IllegalStateException.class)
  public void testNonexistGroup(){
    ChatGroup bc = new ChatGroup("bc");
    repo.updateGroup(bc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullGroupName(){
    String nullString = null;
    repo.findGroupByName(nullString);
  }
}
