package com.neu.prattle.database;

import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.Message;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataRepositoryMessageTest {
  private DataRepository repo;
  private String nullUsername;
  private IMessage nullMessage;

  @Before
  public void setUp(){
    repo = DataRepository.getDataRepository();
    IMessage message1 = Message.messageBuilder().setFrom("charlie").setTo("alice").setMessageContent("hello~").build();
    IMessage message2 = Message.messageBuilder().setFrom("charlie").setTo("bob").setMessageContent("hello!").build();
    repo.createMessage(message1);
    repo.createMessage(message2);

    nullUsername = null;
    nullMessage = null;
  }

  @Test
  public void testCreateMessage(){
    IMessage message = Message.messageBuilder().setFrom("bob").setTo("alice").setMessageContent("hello").build();
    repo.createMessage(message);
    assertNotNull(repo.findMessageById(message.getId()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMessageNull(){
    repo.createMessage(nullMessage);
  }

  @Test
  public void testFindMessageBySender(){
    List<IMessage> results = repo.findMessageBySenderUsername("charlie");
    assertEquals( "From: charlie To: alice Content: hello~",results.get(0).toString());
    assertEquals( "From: charlie To: bob Content: hello!",results.get(1).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindMessageBySenderNull(){
    repo.findMessageBySenderUsername(nullUsername);
  }

  @Test
  public void testFindMessageByReceiver(){
    List<IMessage> results = repo.findMessageByReceiverUsername("bob");
    assertEquals("From: charlie To: bob Content: hello!",results.get(0).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindMessageByReceiverNull(){
    repo.findMessageByReceiverUsername(nullUsername);
  }

  @Test
  public void testMessageBySenderAndReceiver(){
    List<IMessage> results = repo.findMessageBySenderAndReceiver("charlie","bob");
    assertEquals( "From: charlie To: bob Content: hello!",results.get(0).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMessageBySenderNullAndReceiver(){
    repo.findMessageBySenderAndReceiver(nullUsername,"bob");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMessageBySenderAndReceiverNull(){
    repo.findMessageBySenderAndReceiver("charlie",nullUsername);
  }

  @Test
  public void testMessageBySenderAndReceiverEmpty(){
    List<IMessage> results = repo.findMessageBySenderAndReceiver("alice","bob");
    assertEquals( 0,results.size());
  }

  @Test
  public void testUpdateMessageReceivedFlag() {
    List<IMessage> results = repo.findMessageBySenderAndReceiver("charlie","bob");
    IMessage message = results.get(0);
    assertEquals(false, message.isReceived());
    message.setReceived(true);
    repo.updateMessageReceivedFlag(message);

    results = repo.findMessageBySenderAndReceiver("charlie","bob");
    assertEquals(true, results.get(0).isReceived());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateMessageReceivedFlagIllegalArgs() {
    repo.updateMessageReceivedFlag(nullMessage);
  }
}
