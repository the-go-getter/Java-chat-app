package com.neu.prattle.model;

import java.util.Date;

/**
 * The type Abstract message builder.
 *
 * @param <T> the type parameter
 */
public abstract class AbstractMessageBuilder<T extends IMessage> implements IMessageBuilder<T> {
  protected T message;

  @Override
  public IMessageBuilder<T> setFrom(String from) {
    message.setFrom(from);
    return this;
  }

  @Override
  public IMessageBuilder<T> setTo(String to) {
    message.setTo(to);
    return this;
  }

  @Override
  public IMessageBuilder<T> setMessageContent(String content) {
    message.setContent(content);
    return this;
  }

  @Override
  public IMessageBuilder<T> setReceived(boolean received) {
    message.setReceived(received);
    return this;
  }

  @Override
  public T build() {
    message.setTimestamp(new Date());
    return message;
  }
}
