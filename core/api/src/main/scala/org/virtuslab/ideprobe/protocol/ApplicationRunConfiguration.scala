package org.virtuslab.ideprobe.protocol

final case class ApplicationRunConfiguration(
    module: ModuleRef,
    mainClass: String,
    args: Seq[String] = Nil
)
