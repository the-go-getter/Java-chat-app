package com.neu.prattle.websocket;

import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GroupService;
import com.neu.prattle.service.GroupServicePersistentImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServicePersistentImpl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ChatEndpointGroupTest {

  private static GroupService groupService = GroupServicePersistentImpl.getInstance();

  private static ChatEndpoint ce1;
  private static ChatEndpoint ce2;
  private static ChatEndpoint ce3;

  private static Session session1;
  private static Session session2;
  private static Session session3;

  private static String alice = "aliceGroupMock";
  private static String bob = "bobGroupMock";
  private static String charlie = "charlieGroupMock";
  private static String groupName = "g1";
  private static String password = "password";
  private static String createGroup = "\\creategroup";
  private static String addUserToGroup = "\\addusertogroup";
  private static String removeUserFromGroup = "\\removeuserfromgroup";
  private static String addModToGroup = "\\addmodtogroup";
  private static String leaveGroup = "\\leavegroup";

  private static String deleteGroup = "\\deletegroup";
  private static String groupMsgSeparator = "/:";
  private static UserService userService;

  private static RemoteEndpoint.Basic remote1;
  private static RemoteEndpoint.Basic remote2;
  private static RemoteEndpoint.Basic remote3;

  @BeforeClass
  public static void setUpAll() throws Exception {
    userService = UserServicePersistentImpl.getInstance();
    User user1 = new User(alice, password);
    User user2 = new User(bob, password);
    User user3 = new User(charlie, password);
    userService.addUser(user1);
    userService.addUser(user2);
    userService.addUser(user3);

    ce1 = new ChatEndpoint();
    ce2 = new ChatEndpoint();
    ce3 = new ChatEndpoint();

    remote1 = mock(RemoteEndpoint.Basic.class);
    remote2 = mock(RemoteEndpoint.Basic.class);
    remote3 = mock(RemoteEndpoint.Basic.class);


    session1 = mock(Session.class);
    session2 = mock(Session.class);
    session3 = mock(Session.class);

    Mockito.when(session1.getId()).thenReturn("x-sess-id-1");
    Mockito.when(session2.getId()).thenReturn("x-sess-id-2");
    Mockito.when(session3.getId()).thenReturn("x-sess-id-3");

    Mockito.when(session1.getBasicRemote()).thenReturn(remote1);
    Mockito.when(session2.getBasicRemote()).thenReturn(remote2);
    Mockito.when(session3.getBasicRemote()).thenReturn(remote3);

    ce1.onOpen(session1, alice, password);
    ce2.onOpen(session2, bob, password);
    ce3.onOpen(session3, charlie, password);

    //alice only create group once.
    aliceCreateGroup();
  }

  @Before
  public void setUp() throws Exception {

  }


  @Test
  public void addMemberRemoveMemberGroup() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, bob)).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", removeUserFromGroup, groupName, bob)).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void nonModAddMember() {
    try {
      Message addBob = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, bob)).build();
      ce1.onMessage(session1, addBob);
      Message bobAddMember = Message.messageBuilder()
              .setFrom(bob)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, charlie)).build();
      ce2.onMessage(session2, bobAddMember);
      Message removeBob = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", removeUserFromGroup, groupName, bob)).build();
      ce1.onMessage(session1, removeBob);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void nonModRemoveMember() {
    try {
      Message addBob = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, bob)).build();
      ce1.onMessage(session1, addBob);
      Message bobAddMember = Message.messageBuilder()
              .setFrom(bob)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", removeUserFromGroup, groupName, alice)).build();
      ce2.onMessage(session2, bobAddMember);
      Message removeBob = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", removeUserFromGroup, groupName, bob)).build();
      ce1.onMessage(session1, removeBob);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void addNotExistMemberTest() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, "Notexist")).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void removeNotExistMemberTest() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", removeUserFromGroup, groupName, "Notexist")).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSendGroupMessage() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s/:group message", groupName)).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  // May need to change.
  @Test
  public void testSendUnreadGroupMsg() {
    try {
      ce2.onClose(session2);
      Message add = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, bob)).build();
      ce1.onMessage(session1, add);
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s/:group message", groupName)).build();
      ce1.onMessage(session1, message);
      ce2.onOpen(session2, bob, password);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testLeaveGroup() {
    Message add = Message.messageBuilder()
            .setFrom(alice)
            .setTo("testee")
            .setMessageContent(String.format("%s %s %s", addUserToGroup, groupName, bob)).build();
    ce1.onMessage(session1, add);

    try {
      Message message = Message.messageBuilder()
              .setFrom(bob)
              .setMessageContent(String.format("%s %s", leaveGroup, groupName)).build();
      ce2.onMessage(session2, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testDeleteGroup() {
    try {
      ChatGroup group = groupService.findGroupByName(groupName);
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s", deleteGroup, group.getId())).build();
      ce1.onMessage(session1, message);
      aliceCreateGroup();
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testDeleteGroupNotInputId() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s", deleteGroup, "invalid")).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testDeleteGroupNotMod() {
    try {
      ChatGroup group = groupService.findGroupByName(groupName);
      Message message = Message.messageBuilder()
              .setFrom(bob)
              .setTo("testee")
              .setMessageContent(String.format("%s %s", deleteGroup, group.getId())).build();
      ce2.onMessage(session2, message);
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testGroupMessage() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s%shi group", groupName, groupMsgSeparator)).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testAddMod() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addModToGroup, groupName, bob)).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testNonModTryAddMod() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addModToGroup, groupName, charlie)).build();
      ce2.onMessage(session2, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testAddModNotExistUser() {
    try {
      Message message = Message.messageBuilder()
              .setFrom(alice)
              .setTo("testee")
              .setMessageContent(String.format("%s %s %s", addModToGroup, groupName, "Invalid")).build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }


  private static void aliceCreateGroup() {
    Message createMsg = Message.messageBuilder()
            .setFrom(alice)
            .setTo("testee")
            .setMessageContent(String.format("%s %s", createGroup, groupName)).build();
    ce1.onMessage(session1, createMsg);
  }

}
