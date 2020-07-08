package com.neu.prattle.exceptions;

import com.neu.prattle.model.ChatGroup;

/**
 * The type Group already present exception.
 *
 * Refer{@link com.neu.prattle.database.DataRepository#createGroup(ChatGroup)}
 * Refer{@link com.neu.prattle.service.GroupService#createGroup(ChatGroup)}
 */
public class GroupAlreadyPresentException extends RuntimeException {

  /**
   * Instantiates a new Group already present exception.
   *
   * @param message the message
   */
  public GroupAlreadyPresentException(String message)  {
    super(message);
  }
}
