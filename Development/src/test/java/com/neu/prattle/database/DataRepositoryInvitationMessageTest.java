package com.neu.prattle.database;

import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataRepositoryInvitationMessageTest {
  private IDataRepository dataRepository = DataRepository.getDataRepository();
  private String nullString;
  private InviteMessage nullInvite;

  @Before
  public void setup() {
    nullString = null;
    nullInvite = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindInivationByReceiverNull() {
    dataRepository.findInvitationsByReceiver(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindInivationBySendNull() {
    dataRepository.findInvitationsBySender(nullString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindUnacknowledgedInivationBySendNull() {
    dataRepository.findUnacknowledgedInvitationsByReceiver(nullString);
  }

  @Test
  public void testFindInvitation() {
    InviteMessage message1 = InviteMessage.messageBuilder()
            .setFrom("inviter")
            .setTo("invitee1")
            .build();
    message1.setGroupName("invite_group");

    InviteMessage message2 = InviteMessage.messageBuilder()
            .setFrom("inviter")
            .setTo("invitee2")
            .build();
    message2.setGroupName("invite_group");
    message2.setReceived(true);

    dataRepository.createMessage(message1);
    dataRepository.createMessage(message2);

    List<IMessage> res = dataRepository.findInvitationsByReceiver("invitee1");
    assertEquals("invitee1", res.get(0).getTo());

    InviteMessage message3 = (InviteMessage)message2.copy();
    message3.setId(message2.getId());
    message3.setAccepted(true);
    message3.setAcknowledged(true);

    dataRepository.updateInvitationMessageStatus(message3);


    res = dataRepository.findInvitationsByReceiver("invitee2");
    assertEquals("invitee2", res.get(0).getTo());

    res = dataRepository.findInvitationsBySender("inviter");
    assertEquals(2, res.size());

    res = dataRepository.findUnacknowledgedInvitationsByReceiver("invitee1");
    assertEquals(1,res.size());

    res = dataRepository.findUnacknowledgedInvitationsByReceiver("invitee2");
    assertEquals(0,res.size());

  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateNullMessage(){
    dataRepository.updateInvitationMessageStatus(nullInvite);
  }

  @Test(expected = IllegalStateException.class)
  public void testUpdateNonexistingMessage(){
    InviteMessage msg = InviteMessage.messageBuilder().build();
    msg.setId(Integer.MIN_VALUE);
    dataRepository.updateInvitationMessageStatus(msg);
  }
}
