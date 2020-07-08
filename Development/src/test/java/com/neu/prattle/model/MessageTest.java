package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageTest {
  private IMessage nullmessage;
  private IMessage message;

  @Before
  public void setUp() throws Exception {
    nullmessage = new Message();
    message = Message.messageBuilder()
            .setFrom("bob")
            .setTo("alice")
            .setMessageContent("Hello world!")
            .build();
  }

  @Test
  public void testToStringNull() {
    assertEquals("From: null To: null Content: null", nullmessage.toString());
  }

  @Test
  public void testToString() {
    assertEquals("From: bob To: alice Content: Hello world!", message.toString());
  }

  @Test
  public void getFrom() {
    assertEquals("bob", message.getFrom());
  }

  @Test
  public void setFrom() {
    message.setFrom("charlie");
    assertEquals("charlie", message.getFrom());
  }

  @Test
  public void getTo() {
    assertEquals("alice", message.getTo());
  }

  @Test
  public void setTo() {
    message.setTo("charlie");
    assertEquals("charlie", message.getTo());
  }

  @Test
  public void getContent() {
    assertEquals("Hello world!", message.getContent());
  }

  @Test
  public void setContent() {
    message.setContent("Hello prattle!");
    assertEquals("Hello prattle!", message.getContent());
  }

  @Test
  public void messageBuilder() {
    Message.MessageBuilder builder = Message.messageBuilder();
    assertNotNull(builder);
  }

  @Test
  public void testMessageBuildSetReceived() {
    Message.MessageBuilder builder = Message.messageBuilder();
    Message msg = builder.setReceived(true).build();
    assertEquals(true, msg.isReceived());
  }

  @Test
  public void testMessageBuildSetReceivedFalse() {
    Message.MessageBuilder builder = Message.messageBuilder();
    Message msg = builder.setReceived(false).build();
    assertEquals(false, msg.isReceived());
  }
}