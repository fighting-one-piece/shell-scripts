package com.netease.gather.common.util.exception;

public class ApplicationException extends Exception
{

public ApplicationException()
  {
  }

  public ApplicationException(String msg)
  {
    super(msg);
  }

  public ApplicationException(String msg, String solution) {
    super(msg + solution);
  }

  public ApplicationException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }
}