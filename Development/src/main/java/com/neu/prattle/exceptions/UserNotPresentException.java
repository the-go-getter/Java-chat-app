package com.neu.prattle.exceptions;

/**
 * The type User not present exception.
 */
public class UserNotPresentException extends RuntimeException{
  /**
   * Instantiates a new User not present exception.
   *
   * @param message the message
   */
  public UserNotPresentException(String message)  {
    super(message);
  }
}
