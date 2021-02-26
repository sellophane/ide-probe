package org.virtuslab.ideprobe

import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginDescriptor
import org.virtuslab.ideprobe.ProbeHandlers.ProbeHandler
import org.virtuslab.ideprobe.jsonrpc.JsonRpc
import org.virtuslab.ideprobe.jsonrpc.PayloadJsonFormat.SerializedJson
import org.virtuslab.ideprobe.protocol.Endpoints

import scala.util.Try

trait ProbeHandlerContributor {
  def registerHandlers(handler: ProbeHandler): ProbeHandler
}

object ProbeHandlers {
  val EP_NAME = ExtensionPointName.create[ProbeHandlerContributor]("org.virtuslab.ideprobe.probeHandlerContributor")

  private var handler: JsonRpc.Handler = collectHandlers()

  EP_NAME.addExtensionPointListener(
    new ExtensionPointListener[ProbeHandlerContributor] {
      override def extensionAdded(extension: ProbeHandlerContributor, pluginDescriptor: PluginDescriptor): Unit =
        handler = collectHandlers()

      override def extensionRemoved(extension: ProbeHandlerContributor, pluginDescriptor: PluginDescriptor): Unit =
        handler = collectHandlers()
    },
    null
  )

  def get(): JsonRpc.Handler = handler

  private def collectHandlers(): ProbeHandler = {
    EP_NAME
      .getExtensions()
      .foldLeft(new ProbeHandler())((handler, contributor) => contributor.registerHandlers(handler))
  }

  final class ProbeHandler(dispatch: Map[String, SerializedJson => SerializedJson] = Map.empty)
      extends JsonRpc.Handler() {

    def on[A, B](method: JsonRpc.Method[A, B])(f: A => B): ProbeHandler = {
      val action: A => B =
        if (method == Endpoints.Shutdown) f
        else input => CurrentRequest.process(f(input))

      new ProbeHandler(dispatch + (method.name -> method.apply(action)))
    }

    override def apply(method: String, json: SerializedJson): Try[SerializedJson] = {
      Try {
        dispatch.get(method) match {
          case Some(handler) => handler(json)
          case None          => error(s"No handler for $method. Available handlers: ${dispatch.keys.toList.sorted}")
        }
      }
    }
  }
}
