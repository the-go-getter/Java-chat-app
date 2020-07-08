package com.neu.prattle.websocket;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.Message;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import javax.websocket.EndpointConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageEncoderTest {
  private MessageEncoder messageEncoder;
  private String msgString;
  private Message message;
  private GroupMessage groupMessage;
  private String groupMsgString;
  private InviteMessage inviteMessage;
  private String inviteMsgString;

  @Before
  public void setUp() throws Exception {
    Date d = new Date();
    messageEncoder = new MessageEncoder();
    msgString = "{\"@type\":\"msg\",\"id\":0,\"from\":\"alice\",\"to\":\"bob\",\"content\":\"hello\",\"timestamp\":" + d.getTime() + ",\"received\":false}";
    message = new Message();
    message.setContent("hello");
    message.setFrom("alice");
    message.setTo("bob");
    message.setTimestamp(d);

    groupMessage = GroupMessage.groupMessageBuilder().setGroupName("NEU")
            .setFrom("alice")
            .setTo("bob")
            .setMessageContent("hi bob").build();

    groupMessage.setTimestamp(d);
    groupMsgString = "{\"@type\":\"groupMsg\",\"id\":0,\"from\":\"alice\",\"to\":\"bob\",\"content\":\"hi bob\",\"timestamp\":" + d.getTime() + ",\"groupName\":\"NEU\",\"received\":false}";

    inviteMessage = InviteMessage.messageBuilder().setGroupName("g").setFrom("a").setTo("b").setMessageContent("invite message").build();
    inviteMessage.setTimestamp(d);
    inviteMsgString = "{\"@type\":\"inviteMsg\",\"id\":0,\"from\":\"a\",\"to\":\"b\",\"content\":\"invite message\",\"timestamp\":" + d.getTime() + ",\"groupName\":\"g\",\"accepted\":false,\"acknowledged\":false,\"received\":false}";

  }

  @Test
  public void testEncoder() {
    try {
      assertEquals(msgString, messageEncoder.encode(message));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testEncodeGroupMsg() {
    try {
      assertEquals(groupMsgString, messageEncoder.encode(groupMessage));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testEncodeInviteMsg() {
    try {
      assertEquals(inviteMsgString, messageEncoder.encode(inviteMessage));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testEncoderDestroy() {

    try {
      messageEncoder.destroy();
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testEncoderInit() {
    try {
      EndpointConfig ce = mock(EndpointConfig.class);
      messageEncoder.init(ce);
    } catch (Exception e) {
      fail();
    }
  }

}