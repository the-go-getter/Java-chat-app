package com.neu.prattle.service;

import com.neu.prattle.database.DataRepository;
import com.neu.prattle.database.IDataRepository;
import com.neu.prattle.exceptions.UserNotPresentException;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.User;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageServiceInvitationTest {
  private MessageService messageService = MessageServicePersistentImpl.getInstance();

  private static String nullString;
  private static InviteMessage nullInviteMessage;
  private static User messi;
  private static User ronaldo;
  private static IDataRepository repo = DataRepository.getDataRepository();

  @BeforeClass
  public static void setup() {
    nullString = null;
    nullInviteMessage = null;
    messi = new User("Messi", "messi");
    ronaldo = new User("Ronaldo", "ronaldo");
    repo.createUser(messi);
    repo.createUser(ronaldo);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullReceiver() {
    messageService.findInvitationsByReceiver(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSender() {
    messageService.findInvitationsBySender(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAckNullReceiver() {
    messageService.findUnacknowledgedInvitationsByReceiver(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateMessageNull() {
    messageService.updateInvitationMessageStatus(nullInviteMessage);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddMessageNull() {
    messageService.addInvitationMessage(nullInviteMessage);
  }

  @Test(expected = UserNotPresentException.class)
  public void testInvalidSender() {
    InviteMessage msg = InviteMessage.messageBuilder()
            .setFrom("non").setTo("Messi").build();
    messageService.addInvitationMessage(msg);
  }

  @Test(expected = UserNotPresentException.class)
  public void testInvalidReceiver() {
    InviteMessage msg = InviteMessage.messageBuilder()
            .setFrom("Ronaldo").setTo("non").build();
    messageService.addInvitationMessage(msg);
  }

  @Test
  public void testAddInvitation() {
    InviteMessage msg = InviteMessage.messageBuilder()
            .setFrom(ronaldo.getName())
            .setTo(messi.getName())
            .setMessageContent("World cup")
            .build();
    messageService.addInvitationMessage(msg);
    List<InviteMessage> msgs = messageService.findInvitationsByReceiver(messi.getName());
    assertEquals("World cup", msgs.get(0).getContent());
    msgs = messageService.findInvitationsBySender(ronaldo.getName());
    assertEquals("World cup", msgs.get(0).getContent());

    msgs = messageService.findUnacknowledgedInvitationsByReceiver(messi.getName());
    assertEquals("World cup", msgs.get(0).getContent());

    msg.setAcknowledged(true);
    messageService.updateInvitationMessageStatus(msg);
    msgs = messageService.findUnacknowledgedInvitationsByReceiver(messi.getName());
    assertEquals(0, msgs.size());

  }

}
