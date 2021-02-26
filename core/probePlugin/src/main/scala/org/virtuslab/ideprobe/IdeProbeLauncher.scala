package org.virtuslab.ideprobe

import com.intellij.ide.ApplicationInitializedListener
import org.virtuslab.ideprobe.log.IdeaLogInterceptor
import org.virtuslab.ideprobe.log.NotificationsInterceptor

/**
 * Starts the IdeProbe on startup
 */
final class IdeProbeLauncher extends ApplicationInitializedListener {
  override def componentsInitialized(): Unit = {
    IdeaLogInterceptor.inject()
    NotificationsInterceptor.inject()
    WindowMonitor.inject()
    IdeProbeService().start()
  }
}
