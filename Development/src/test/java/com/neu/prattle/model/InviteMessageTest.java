package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InviteMessageTest {
  private InviteMessage messageDefault;
  private InviteMessage message;

  @Before
  public void setUp() throws Exception {
    messageDefault = InviteMessage.messageBuilder().build();
    message = InviteMessage.messageBuilder()
            .setFrom("mod")
            .setTo("user")
            .setMessageContent("Join us!")
            .setReceived(false)
            .build();
  }


  @Test
  public void testDefaultAccept() {
    assertFalse(messageDefault.isAccepted());
  }

  @Test
  public void testDefaultAcknewledge() {
    assertFalse(messageDefault.isAcknowledged());
  }

  @Test
  public void testDefaultFrom() {
    assertEquals("Not set", messageDefault.getFrom());
  }

  @Test
  public void testDefaultReceived() {
    assertFalse(messageDefault.isReceived());
  }

  @Test
  public void testDefaultGroupName() {
    assertEquals("", messageDefault.getGroupName());
  }

  @Test
  public void testSetFrom() {
    assertEquals("mod", message.getFrom());
  }

  @Test
  public void testSetTo() {
    assertEquals("user", message.getTo());
  }

  @Test
  public void testSetAccept() {
    message.setAccepted(true);
    assertTrue(message.isAccepted());
  }

  @Test
  public void testSetAcknowledge() {
    message.setAcknowledged(true);
    assertTrue(message.isAcknowledged());
  }

  @Test
  public void testSetGroupName() {
    message.setGroupName("test_group");
    assertEquals("test_group", message.getGroupName());
  }

  @Test
  public void testBuilderSetAccept() {
    InviteMessage msg = InviteMessage.messageBuilder().setAccepted(true).build();
    assertTrue(msg.isAccepted());
  }

  @Test
  public void testBuilderSetAcknowledge() {
    InviteMessage msg = InviteMessage.messageBuilder().setAcknowledged(true).build();
    assertTrue(msg.isAcknowledged());
  }

  @Test
  public void testBuilderSetGroupName() {
    InviteMessage msg = InviteMessage.messageBuilder().setGroupName("test_group").build();
    assertEquals("test_group", msg.getGroupName());
  }

  @Test
  public void testMessageCopy(){
    IMessage copy = message.copy();
    InviteMessage recover = (InviteMessage)copy;
    assertEquals("mod",recover.getFrom());
    assertEquals("user",recover.getTo());
    assertEquals("Join us!",recover.getContent());
    assertEquals(false,recover.isAccepted());
    assertEquals(false,recover.isAcknowledged());
  }

}