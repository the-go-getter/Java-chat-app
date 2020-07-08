package com.neu.prattle.websocket;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.Message;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import javax.websocket.EndpointConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MessageDecoderTest {
  private MessageDecoder messageDecoder;
  private String nullString;
  private String msgString;
  private Message message;
  private GroupMessage groupMessage;
  private InviteMessage inviteMessage;
  private String groupMsgString;
  private String inviteMsgString;

  @Before
  public void setUp() throws Exception {
    messageDecoder = new MessageDecoder();
    nullString = null;
    msgString = "{\"@type\":\"msg\",\"id\":0,\"from\":\"alice\",\"to\":\"bob\",\"content\":\"hello\",\"received\":false}";
    message = new Message();
    message.setContent("hello");
    message.setFrom("alice");
    message.setTo("bob");

    groupMsgString = "{\"@type\":\"groupMsg\",\"id\":0,\"from\":\"alice\",\"to\":\"bob\",\"content\":\"hi bob\",\"groupName\":\"NEU\",\"received\":false}";
    groupMessage = GroupMessage.groupMessageBuilder().setGroupName("g1")
            .setFrom("alice")
            .setTo("bob")
            .setMessageContent("hi bob").build();

    inviteMessage = InviteMessage.messageBuilder().setGroupName("g").setFrom("a").setTo("b").setMessageContent("invite message").build();
    inviteMsgString = "{\"@type\":\"inviteMsg\",\"id\":0,\"from\":\"a\",\"to\":\"b\",\"content\":\"invite message\",\"groupName\":\"g\",\"accepted\":false,\"acknowledged\":false,\"received\":false}";

  }

  @Test
  public void testWillDecodeNull() {
    assertFalse(messageDecoder.willDecode(nullString));
  }

  @Test
  public void testWillDecode() {
    assertTrue(messageDecoder.willDecode(msgString));
  }

  @Test
  public void testWillDecodeGroupMsg() {
    assertTrue(messageDecoder.willDecode(groupMsgString));
  }

  @Test
  public void testDecode() {
    IMessage msg = messageDecoder.decode(msgString);
    assertEquals(message.toString(), msg.toString());
  }

  @Test
  public void testDecodeGroupMessage() {
    IMessage msg = messageDecoder.decode(groupMsgString);
    assertEquals(groupMessage.toString(), msg.toString());
  }

  @Test
  public void testDecodeInviteMessage() {
    IMessage msg = messageDecoder.decode(inviteMsgString);
    Assert.assertEquals(inviteMessage.toString(), msg.toString());
  }


  @Test
  public void testEncoderDestroy() {
    try {
      messageDecoder.destroy();
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testEncoderInit() {
    try {
      EndpointConfig ce = mock(EndpointConfig.class);
      messageDecoder.init(ce);
    } catch (Exception e) {
      fail();
    }
  }
}