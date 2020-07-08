package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Date;

/**
 * The interface Message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Message.class, name = "msg"),
        @JsonSubTypes.Type(value = GroupMessage.class, name = "groupMsg"),
        @JsonSubTypes.Type(value = InviteMessage.class, name = "inviteMsg")
}
)
public interface IMessage {

  /**
   * Gets id.
   *
   * @return the id
   */
  long getId();

  /**
   * Sets id.
   *
   * @param id the id
   */
  void setId(long id);

  /**
   * Gets from.
   *
   * @return the from
   */
  String getFrom();

  /**
   * Sets from.
   *
   * @param from the from
   */
  void setFrom(String from);

  /**
   * Gets to.
   *
   * @return the to
   */
  String getTo();

  /**
   * Sets to.
   *
   * @param to the to
   */
  void setTo(String to);

  /**
   * Gets content.
   *
   * @return the content
   */
  String getContent();

  /**
   * Sets content.
   *
   * @param content the content
   */
  void setContent(String content);

  /**
   * if the message has been received.
   *
   * @return the boolean
   */
  boolean isReceived();

  /**
   * Sets received.
   *
   * @param received the received
   */
  void setReceived(boolean received);

  /**
   * Make a copy of itself
   * @return copy of current instance
   */
  IMessage copy();

  /**
   * Gets timestamp.
   *
   * @return the timestamp
   */
  Date getTimestamp();

  /**
   * Sets timestamp.
   *
   * @param timestamp the timestamp
   */
  void setTimestamp(Date timestamp);
}
