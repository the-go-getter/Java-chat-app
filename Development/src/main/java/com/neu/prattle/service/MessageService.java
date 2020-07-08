package com.neu.prattle.service;

import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;

import java.util.List;

/**
 * The interface Message service.
 */
public interface MessageService {
  /**
   * Add message.
   *
   * @param message the message
   */
  void addMessage(IMessage message);

  /**
   * Find message by sender list.
   *
   * @param sender the sender
   * @return the list
   */
  List<IMessage> findMessageBySender(String sender);

  /**
   * Find message by receiver list.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findMessageByReceiver(String receiver);

  /**
   * Find message by sender and receiver list.
   *
   * @param sender   the sender
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findMessageBySenderAndReceiver(String sender, String receiver);

  /**
   * Update message received flag.
   *
   * @param message the message
   */
  void updateMessageReceivedFlag(IMessage message);

  /**
   * Find invitations by receiver list.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<InviteMessage> findInvitationsByReceiver(String receiver);

  /**
   * Find invitations by sender list.
   *
   * @param sender the sender
   * @return the list
   */
  List<InviteMessage> findInvitationsBySender(String sender);

  /**
   * Find unacknowledged invitations by receiver list.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<InviteMessage> findUnacknowledgedInvitationsByReceiver(String receiver);

  /**
   * Update invitation message. Note, this messege must be an managed object. This means it must be
   * obtained by find* methods.
   *
   * @param message the message
   */
  void updateInvitationMessageStatus(InviteMessage message);

  void addInvitationMessage(InviteMessage message);
}
