package dev.rocky.data.kick

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionPhase
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyPairGenerator
import java.util.Base64
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesktopKickChatClientTest {}
