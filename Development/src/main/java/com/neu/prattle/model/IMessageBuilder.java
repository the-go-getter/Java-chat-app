package com.neu.prattle.model;

/**
 * The interface Message builder.
 *
 * @param <T> the type parameter
 */
public interface IMessageBuilder<T extends IMessage> {
  /**
   * Sets from.
   *
   * @param from the from
   * @return the from
   */
  IMessageBuilder<T> setFrom(String from);

  /**
   * Sets to.
   *
   * @param to the to
   * @return the to
   */
  IMessageBuilder<T> setTo(String to);

  /**
   * Sets message content.
   *
   * @param content the content
   * @return the message content
   */
  IMessageBuilder<T> setMessageContent(String content);


  /**
   * Sets received.
   *
   * @param isReceived the is received
   * @return the received
   */
  IMessageBuilder<T> setReceived(boolean isReceived);

  /**
   * Build t.
   *
   * @return the t
   */
  T build();
}
