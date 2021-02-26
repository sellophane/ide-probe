package org.virtuslab.ideprobe.log

import scala.collection.mutable

object MessageLog {
  private var messagesBuffer = mutable.ListBuffer[Message]()

  def all: Seq[Message] = messagesBuffer.toSeq

  def add(error: Message): Unit = synchronized {
    messagesBuffer += error
  }
}
