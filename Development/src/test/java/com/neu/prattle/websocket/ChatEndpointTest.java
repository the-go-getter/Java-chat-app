package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServicePersistentImpl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ChatEndpointTest {
  private ChatEndpoint ce;
  private ChatEndpoint sender;
  private ChatEndpoint receiver;
  private ChatEndpoint ce_follow;

  private Session sess;
  private Session follow_sess;
  private static User testUser;
  private static User testRecv;

  private static UserService userService;

  private RemoteEndpoint.Basic basic;
  private RemoteEndpoint.Basic senderRemote;
  private RemoteEndpoint.Basic receiverRemote;
  private RemoteEndpoint.Basic followRemote;
  private static String password;

  @BeforeClass
  public static void setUpAll() {
    userService = UserServicePersistentImpl.getInstance();
    password = "password";
    testUser = new User("ce_tester", password);
    User testSender = new User("sender", password);
    testRecv = new User("recv", password);
    userService.addUser(testUser);
    userService.addUser(testRecv);
    userService.addUser(testSender);
    User follower = new User("follower", password);
    User followed = new User("followed", password, "email");
    userService.addUser(follower);
    userService.addUser(followed);
  }

  private Session senderSession;
  private Session receiverSession;

  @Before
  public void setUp() throws Exception {
    ce = new ChatEndpoint();
    sess = mock(Session.class);
    basic = mock(RemoteEndpoint.Basic.class);

    senderRemote = mock(RemoteEndpoint.Basic.class);
    receiverRemote = mock(RemoteEndpoint.Basic.class);
    followRemote = mock(RemoteEndpoint.Basic.class);

    sender = new ChatEndpoint();
    receiver = new ChatEndpoint();
    senderSession = mock(Session.class);
    receiverSession = mock(Session.class);
    follow_sess = mock(Session.class);

    Mockito.when(this.sess.getId()).thenReturn("x-sess-id-x");
    Mockito.when(this.senderSession.getId()).thenReturn("x-sess-id-send");
    Mockito.when(this.receiverSession.getId()).thenReturn("x-sess-id-recv");
    Mockito.when(this.follow_sess.getId()).thenReturn("x-sess-id-follower");

    Mockito.when(this.sess.getBasicRemote()).thenReturn(this.basic);
    Mockito.when(this.senderSession.getBasicRemote()).thenReturn(this.senderRemote);
    Mockito.when(this.receiverSession.getBasicRemote()).thenReturn(this.receiverRemote);
    Mockito.when(this.follow_sess.getBasicRemote()).thenReturn(this.followRemote);

    ce_follow = new ChatEndpoint();

    ce_follow.onOpen(follow_sess, "follower",password);

  }

  @Test
  public void testOnOpenSender() {
    try {
      ce.onOpen(senderSession, "sender", "password");
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnOpenReceiver() {
    try {
      ce.onOpen(senderSession, "recv", "password");
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnOpen() {
    try {
      ce.onOpen(sess, "ce_tester", "password");
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOpenInvalidUser() {
    try {
      ce.onOpen(sess, "invalid", "password");
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testOnClose() {
    try {
      ce.onClose(sess);
    } catch (Exception e) {
      fail();
    }
  }

//  @Test
//  public void testOnCloseNullSession(){
//    try{
//      ce.onClose(null);
//    }catch (Exception e){
//      fail();
//    }
//  }

  @Test
  public void testOnError() {
    try {
      Throwable throwable = mock(Throwable.class);
      ce.onError(sess, throwable);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessage() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("Perform test").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testEndToEndMessage() throws IOException, EncodeException {
    sender.onOpen(senderSession, "sender", "password");
    receiver.onOpen(receiverSession, "recv", "password");

    try {
      Message message = Message.messageBuilder()
              .setFrom("sender")
              .setTo("recv")
              .setMessageContent("recv::message").build();
      ce.onMessage(senderSession, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSingleUserMessageReceiverInvalid() throws IOException, EncodeException, NoSuchFieldException {
    sender.onOpen(senderSession, "sender", "password");

    try {
      Message message = Message.messageBuilder()
              .setFrom("sender")
              .setTo("invalidReceiver")
              .setMessageContent("invalidReceiver::message").build();
      sender.onMessage(senderSession, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testBroadcastMessage() throws IOException, EncodeException {
    sender.onOpen(senderSession, "sender", "password");

    try {
      Message message = Message.messageBuilder()
              .setFrom("sender")
              .setMessageContent("broadcast message").build();
      ce.onMessage(senderSession, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageCreateUser() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("\\createuser name passwd").build();
      ce.onMessage(sess, message);

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageCreateUserwithEmail() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("testUser")
              .setTo("testRecv")
              .setMessageContent("\\createuser name3 passwd email").build();
      ce.onMessage(sess, message);

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageCreateUserWithoutPassword() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("\\createuser name2").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }

  }

  @Test
  public void testOnMessageCreateUserEmpty() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("\\createuser").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }

  }


  @Test
  public void testOnMessageUpdateUser() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("\\updateuser ce_tester passwd").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageNotFollowingUserInfo(){
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\user followed").build();
      ce_follow.onMessage(follow_sess,message);
    } catch (Exception e){
      fail();
    }
  }

  @Test
  public void testOnMessageFollowUser() {
    try {
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\followuser followed").build();
      ce_follow.onMessage(follow_sess, message);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      fail();
    }
  }

  @Test
  public void testOnMessageFollowingUserInfo(){
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\followuser followed").build();
      ce_follow.onMessage(follow_sess, message);
      Message message2 = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\user followed").build();
      ce_follow.onMessage(follow_sess,message2);
    } catch (Exception e){
      fail();
    }
  }

  @Test
  public void testOnMessageUserFollowers(){
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\following").build();
      ce_follow.onMessage(follow_sess,message);
    } catch (Exception e){
      fail();
    }
  }

  @Test
  public void testOnMessageUnfollowUser(){
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\unfollowuser followed").build();
      ce_follow.onMessage(follow_sess,message);
    } catch (Exception e){
      fail();
    }
  }

  @Test
  public void testOnMessageFollowUserInvalid1() {
    try {
      Message message = Message.messageBuilder().setFrom("testUser")
              .setTo("testRecv")
              .setMessageContent("\\followuser testRecv").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageHelp() {
    try {
      Message message = Message.messageBuilder().setFrom("testUser")
              .setTo("testRecv")
              .setMessageContent("\\help").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageUnfollowUserInvalid1() {
    try {
      Message message = Message.messageBuilder().setFrom("testUser")
              .setTo("testRecv")
              .setMessageContent("\\unfollowuser testRecv").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageFollowUserInvalid2() {
    try {
      Message message = Message.messageBuilder().setFrom("invalid_sender")
              .setTo("invalid_receiver")
              .setMessageContent("\\followuser").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageUnfollowUserInvalid2() {
    try {
      Message message = Message.messageBuilder().setFrom("invalid_sender")
              .setTo("invalid_receiver")
              .setMessageContent("\\unfollowuser").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageUpdateUserEmpty() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("tester")
              .setTo("testee")
              .setMessageContent("\\updateuser").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }

  }

  @Test
  public void testOnMessageDeleteUser() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("ce_tester")
              .setTo("testee")
              .setMessageContent("\\deleteuser ce_tester").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }

  }

  @Test
  public void testOnMessageDeleteUserEmpty() {
    try {
      Message message = Message.messageBuilder()
              .setFrom("ce_tester")
              .setTo("testee")
              .setMessageContent("\\deleteuser").build();
      ce.onMessage(sess, message);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testOnMessageSearchUser() {
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\search foll").build();
      ce_follow.onMessage(follow_sess,message);
    } catch (Exception e){
      fail();
    }
  }

  @Test
  public void testOnMessageSearchUserNotPresent() {
    try{
      Message message = Message.messageBuilder().setFrom("follower")
              .setTo("followed")
              .setMessageContent("\\search b").build();
      ce_follow.onMessage(follow_sess,message);
    } catch (Exception e){
      fail();
    }
  }


}