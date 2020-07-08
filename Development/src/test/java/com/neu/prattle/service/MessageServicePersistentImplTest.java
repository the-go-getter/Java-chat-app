package com.neu.prattle.service;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.Message;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MessageServicePersistentImplTest {
  private static MessageService messageService;
  private static String nullString;
  private static IMessage nullMessage;
  private static String emptyString = "";

  @BeforeClass
  public static void setUp() throws Exception {
    messageService = MessageServicePersistentImpl.getInstance();
    nullString = null;
    nullMessage = null;
  }

  @Test
  public void testGetInstance(){
    MessageService ms = MessageServicePersistentImpl.getInstance();
    assertNotNull(ms);
    assertEquals(ms,messageService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddMessageNull(){
    messageService.addMessage(nullMessage);
  }

  @Test
  public void testAddMessage(){
    IMessage message = Message.messageBuilder()
            .setFrom("tester")
            .setTo("testee")
            .setMessageContent("Perform test").build();

    messageService.addMessage(message);

    assertEquals("Perform test",messageService.
            findMessageBySender("tester").get(0).getContent());
    assertEquals("Perform test",messageService.
            findMessageByReceiver("testee").get(0).getContent());

    assertEquals("Perform test",messageService.
            findMessageBySenderAndReceiver("tester","testee").get(0).getContent());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMessageNoReceiver() {
    Message message = Message.messageBuilder()
            .setFrom("tester").build();
    messageService.addMessage(message);
  }

  @Test
  public void testUpdateMessageFlag() {
    IMessage message = Message.messageBuilder()
            .setReceived(false)
            .setFrom("aliceUpdateFlag")
            .setTo("bobUpdateFlag")
            .setMessageContent("message").build();

    messageService.addMessage(message);
    message  = messageService.findMessageBySenderAndReceiver("aliceUpdateFlag", "bobUpdateFlag").get(0);
    assertEquals(false, message.isReceived());
    message.setReceived(true);
    messageService.updateMessageReceivedFlag(message);
    message  = messageService.findMessageBySenderAndReceiver("aliceUpdateFlag", "bobUpdateFlag").get(0);
    assertEquals(true, message.isReceived());
  }

  //TODO: Bug, either creategroupMsg or findmsg doesn't work.
  @Test
  public void testFindGroupMsgByReceiver() {
    IMessage groupMessage = GroupMessage.groupMessageBuilder()
            .setReceived(false)
            .setFrom("aliceGroupMsgFind")
            .setTo("bobGroupMsgFind")
            .setMessageContent("groupMsg").build();

    messageService.addMessage(groupMessage);
    List<IMessage> msg = messageService.findMessageBySender("aliceGroupMsgFind");
    groupMessage  = messageService.findMessageByReceiver( "bobGroupMsgFind").get(0);
    assertEquals(false, groupMessage.isReceived());
    groupMessage.setReceived(true);
    messageService.updateMessageReceivedFlag(groupMessage);
    groupMessage  = messageService.findMessageByReceiver("bobGroupMsgFind").get(0);
    assertEquals(true, groupMessage.isReceived());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindBySenderEmptyString(){
    messageService.findMessageBySender(emptyString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByEmptyReceiver(){
    messageService.findMessageByReceiver(emptyString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindBySenderNull(){
    messageService.findMessageBySender(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByReceiverNull(){
    messageService.findMessageByReceiver(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindBySenderNullAndReceiver(){
    messageService.findMessageBySenderAndReceiver(nullString,"testee");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindBySenderAndReceiverNull(){
    messageService.findMessageBySenderAndReceiver("tester",nullString);
  }
}