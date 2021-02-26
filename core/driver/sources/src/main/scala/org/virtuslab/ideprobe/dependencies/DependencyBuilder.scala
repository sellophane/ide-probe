package org.virtuslab.ideprobe.dependencies

import java.nio.file.Path
import org.virtuslab.ideprobe.{Config, Id}

abstract class DependencyBuilder(val id: Id) {
  def build(config: Config, resources: ResourceProvider): Path
}
