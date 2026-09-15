package dev.rocky.data.tiktok

import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionEvent
import dev.rocky.core.tiktok.TikTokConnectionPhase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DesktopTikTokChatClientTest {
    @Test
    fun connectsByUsernameAndForwardsLiveEvents() {
        val transport = FakeTikTokTransport()
        var requestedUsername = ""
        val client = DesktopTikTokChatClient(TikTokLiveTransportFactory { username, onEvent ->
            requestedUsername = username
            transport.onEvent = onEvent
            transport
        })
        val events = mutableListOf<TikTokConnectionEvent>()

        client.connect(TikTokConfiguration(" @rocky_live "), events::add)
        transport.emit(TikTokTransportEvent.Connected(TikTokTransportRoom(
            "room-1", "Minha live", "rocky_live", "Rocky", 42,
        )))
        transport.emit(TikTokTransportEvent.CommentReceived(TikTokTransportComment(
            "message-1", "viewer-1", "Ana", "Olá do TikTok", "room-1", "2026-09-14T12:00:00Z",
        )))
        transport.emit(TikTokTransportEvent.AudienceUpdated(51))

        assertEquals("rocky_live", requestedUsername)
        assertTrue(transport.connected)
        assertEquals(TikTokConnectionPhase.Connecting,
            assertIs<TikTokConnectionEvent.PhaseChanged>(events.first()).phase)
        val connected = events.filterIsInstance<TikTokConnectionEvent.Connected>().single()
        assertEquals("Rocky", connected.account.displayName)
        assertEquals("room-1", connected.room.id)
        val message = events.filterIsInstance<TikTokConnectionEvent.MessageReceived>().single().message
        assertEquals(StreamPlatform.TikTok, message.platform)
        assertEquals("Olá do TikTok", message.text)
        assertEquals(51, events.filterIsInstance<TikTokConnectionEvent.AudienceUpdated>().last().viewerCount)
    }

    @Test
    fun ignoresDuplicatesAndEventsAfterDisconnect() {
        val transport = FakeTikTokTransport()
        val client = DesktopTikTokChatClient(TikTokLiveTransportFactory { _, onEvent ->
            transport.onEvent = onEvent
            transport
        })
        val events = mutableListOf<TikTokConnectionEvent>()
        val comment = TikTokTransportEvent.CommentReceived(TikTokTransportComment(
            "message-1", "viewer-1", "Ana", "Olá", "room-1", null,
        ))

        client.connect(TikTokConfiguration("rocky_live"), events::add)
        transport.emit(comment)
        transport.emit(comment)
        client.disconnect()
        transport.emit(TikTokTransportEvent.AudienceUpdated(99))

        assertEquals(1, events.filterIsInstance<TikTokConnectionEvent.MessageReceived>().size)
        assertFalse(transport.connected)
        assertEquals(TikTokConnectionPhase.Disconnected,
            events.filterIsInstance<TikTokConnectionEvent.PhaseChanged>().last().phase)
        assertTrue(events.none { it is TikTokConnectionEvent.AudienceUpdated && it.viewerCount == 99 })
        client.close()
    }

    @Test
    fun reconnectsWithFreshTransportAndPreservesDeduplication() = fixture().use { f ->
        f.connect()
        val first = f.transports.first()
        val lateCallback = first.onEvent
        first.emit(connected())
        first.emit(comment())
        waitFor { f.messages().size == 1 }
        first.emit(TikTokTransportEvent.Disconnected("network"))
        first.emit(TikTokTransportEvent.Failed("duplicate error"))
        waitFor { f.transports.size == 2 && f.transports.last().connected }
        assertFalse(first.connected)
        f.transports.last().emit(connected())
        f.transports.last().emit(comment())
        f.transports.last().emit(comment("new-message"))
        lateCallback(comment("stale-message"))
        waitFor { f.messages().size == 2 }
        assertEquals(listOf("message", "new-message"), f.messages().map { it.message.id })
        assertEquals(1, f.events.filterIsInstance<TikTokConnectionEvent.PhaseChanged>().count { it.phase == TikTokConnectionPhase.Reconnecting })
    }

    @Test
    fun stopsAfterBoundedRetriesAndIgnoresLateEvents() = fixture(delays = listOf(10, 20)).use { f ->
        f.connect()
        repeat(3) { index ->
            waitFor { f.transports.size == index + 1 && f.transports[index].connected }
            f.transports[index].emit(TikTokTransportEvent.Failed("temporary"))
        }
        waitFor { f.phase() == TikTokConnectionPhase.Failed }
        f.transports.last().emit(comment())
        assertTrue(f.messages().isEmpty())
        assertEquals(3, f.transports.size)
        assertFalse(f.transports.last().connected)
    }

    }
}

private class FakeTikTokTransport : TikTokLiveTransport {
    @Volatile var connected = false
    @Volatile var onEvent: (TikTokTransportEvent) -> Unit = {}
    @Volatile var connectCalls = 0

    override fun connect() { connectCalls++; connected = true }
    override fun disconnect() { connected = false }
    fun emit(event: TikTokTransportEvent) = onEvent(event)
}

private class Fixture(delays: List<Long>, timeout: Long) : AutoCloseable {
    val transports = CopyOnWriteArrayList<FakeTikTokTransport>()
    val events = CopyOnWriteArrayList<TikTokConnectionEvent>()
    val client = DesktopTikTokChatClient(TikTokLiveTransportFactory { _, callback ->
        FakeTikTokTransport().also { it.onEvent = callback; transports.add(it) }
    }, delays, timeout)
    fun connect() {
        client.connect(TikTokConfiguration("rocky_live"), events::add)
        waitFor { transports.firstOrNull()?.connected == true }
    }
    fun phase() = events.filterIsInstance<TikTokConnectionEvent.PhaseChanged>().lastOrNull()?.phase
    fun messages() = events.filterIsInstance<TikTokConnectionEvent.MessageReceived>()
    override fun close() = client.close()
}

private fun connected() = TikTokTransportEvent.Connected(TikTokTransportRoom("room", "Live", "rocky_live", "Rocky", 42))
private fun comment(id: String = "message") = TikTokTransportEvent.CommentReceived(
    TikTokTransportComment(id, "viewer", "Ana", "Olá", "room", null))
private fun waitFor(condition: () -> Boolean) {
    val deadline = System.nanoTime() + 5_000_000_000L
    while (!condition()) {
        check(System.nanoTime() < deadline) { "Timed out waiting for TikTok client" }
        Thread.sleep(5)
    }
}
