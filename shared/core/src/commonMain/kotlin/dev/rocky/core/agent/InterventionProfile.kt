package dev.rocky.core.agent

enum class InterventionProfile(val intervalMillis: Long) {
    OnDemand(0),
    Discreet(300_000),
    Proactive(120_000),
}
