package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServicePersistentImpl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ChatEndpointGovTest {
  private static ChatEndpoint ce1;
  private static ChatEndpoint ce2;
  private static ChatEndpoint ce3;

  private static Session session1;
  private static Session session2;
  private static Session session3;

  private static UserService userService;
  private static User govUser;
  private static User alonso;
  private static User kimi;

  private static RemoteEndpoint.Basic remote1;
  private static RemoteEndpoint.Basic remote2;
  private static RemoteEndpoint.Basic remote3;
  private static String password;

  @BeforeClass
  public static void setupAll() throws Exception {
    userService = UserServicePersistentImpl.getInstance();
    if (userService.findUserByName("gov").isPresent())
      govUser = userService.findUserByName("gov").get();
    password = "password";
    alonso = new User("alonso", password);
    kimi = new User("kimi", password);
    userService.addUser(alonso);
    userService.addUser(kimi);

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

    ce1.onOpen(session1, govUser.getName(), govUser.getPassword());
    ce2.onOpen(session2, alonso.getName(), password);
    ce3.onOpen(session3, kimi.getName(), password);
  }

  @Test
  public void testGovLs() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov ls").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }
  @Test
  public void testGovNormalUser(){
    try {
      Message message = Message.messageBuilder()
              .setFrom(kimi.getName())
              .setMessageContent("\\gov ls").build();
      ce2.onMessage(session2, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovHistoryUserMissing() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov history").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovWatchUserMissing() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov watch").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovHistory() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov history alonso").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovWatch() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov watch kimi").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovUnwatchUserMissing() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov unwatch").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGovUnwatch() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("gov")
              .setMessageContent("\\gov unwatch kimi").build();
      ce1.onMessage(session1, message);
    } catch (Exception e) {
      fail();
    }
  }
}
