package com.neu.prattle.service;

import com.neu.prattle.database.DataRepository;
import com.neu.prattle.exceptions.UserNotPresentException;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class MessageServicePersistentImpl implements MessageService {
  private DataRepository repository;

  /**
   * The logger.
   */
  private static final Logger logger = Logger.getLogger(MessageServicePersistentImpl.class);

  private MessageServicePersistentImpl() {
    this.repository = DataRepository.getDataRepository();
  }

  private static MessageService messageService;

  static {
    messageService = new MessageServicePersistentImpl();
  }

  public static MessageService getInstance() {
    return messageService;
  }

  /**
   * Find message by sender.
   *
   * @param sender the sender
   * @return the list
   */
  @Override
  public List<IMessage> findMessageBySender(String sender) {
    validateSender(sender);
    return repository.findMessageBySenderUsername(sender);
  }

  /**
   * Find message by receiver.
   *
   * @param receiver the receiver
   * @return the list
   */
  @Override
  public List<IMessage> findMessageByReceiver(String receiver) {
    validateReceiver(receiver);
    return repository.findMessageByReceiverUsername(receiver);
  }

  /**
   * Find message by sender and receiver.
   *
   * @param sender   the sender
   * @param receiver the receiver
   * @return the list
   */
  @Override
  public List<IMessage> findMessageBySenderAndReceiver(String sender, String receiver) {
    validateSender(sender);
    validateReceiver(receiver);
    return repository.findMessageBySenderAndReceiver(sender, receiver);
  }

  private void validateSender(String sender) {
    if (sender == null || sender.isEmpty())
      throw new IllegalArgumentException("Reference sender is null or empty string");
  }

  private void validateReceiver(String receiver) {
    if (receiver == null || receiver.isEmpty())
      throw new IllegalArgumentException("Reference receiver is null or empty string");
  }

  private void validateMessage(IMessage message) {
    logger.info("Validating message");
    if (message == null) {
      logger.warn("Message is null");
      throw new IllegalArgumentException("message can't be null");
    }
    logger.info("Message validated");
  }

  /**
   * Added message must have sender and receiver.
   */
  @Override
  public void addMessage(IMessage message) {
    validateMessage(message);
    validateReceiver(message.getTo());
    validateSender(message.getFrom());
    repository.createMessage(message);
  }


  @Override
  public void updateMessageReceivedFlag(IMessage message) {
    logger.info("Entering updateMessageReceivedFlag method for - ");
    logger.info(String.format("From : %s , To : %s , Content : %s", message.getFrom(),
            message.getTo(), message.getContent()));
    validateMessage(message);
    repository.updateMessageReceivedFlag(message);
  }


  @Override
  public List<InviteMessage> findInvitationsByReceiver(String receiver) {
    validateReceiver(receiver);
    List<InviteMessage> res = new LinkedList<>();
    for (IMessage msg : repository.findInvitationsByReceiver(receiver)) {
      res.add((InviteMessage) msg);
    }
    return res;
  }

  @Override
  public List<InviteMessage> findInvitationsBySender(String sender) {
    validateSender(sender);
    List<InviteMessage> res = new LinkedList<>();
    for (IMessage msg : repository.findInvitationsBySender(sender)) {
      res.add((InviteMessage) msg);
    }
    return res;
  }

  @Override
  public List<InviteMessage> findUnacknowledgedInvitationsByReceiver(String receiver) {
    validateReceiver(receiver);
    List<InviteMessage> res = new LinkedList<>();
    for (IMessage msg : repository.findUnacknowledgedInvitationsByReceiver(receiver)) {
      res.add((InviteMessage) msg);
    }
    return res;
  }

  @Override
  public void updateInvitationMessageStatus(InviteMessage message) {
    validateMessage(message);
    repository.updateInvitationMessageStatus(message);
  }

  @Override
  public void addInvitationMessage(InviteMessage message) {
    if (message==null){
      throw new IllegalArgumentException("Reference message is null.");
    }
    if (repository.findUserByUsername(message.getFrom()) == null) {
      throw new UserNotPresentException("Sender doesn't exits.");
    }
    if (repository.findUserByUsername(message.getTo()) == null) {
      throw new UserNotPresentException("Receiver doesn't exits.");
    }
    repository.createMessage(message);
  }
}
