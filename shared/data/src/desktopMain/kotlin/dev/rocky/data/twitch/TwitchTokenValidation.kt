package dev.rocky.data.twitch

internal fun validateTwitchTokens(
    tokens: TwitchTokens,
    validate: (String) -> Unit,
    refresh: (String) -> TwitchTokens,
): TwitchTokens {
    try {
        validate(tokens.accessToken)
        return tokens
    } catch (error: TwitchApiException) {
        if (error.statusCode != 401) throw error
    }
    return refresh(tokens.refreshToken).also { validate(it.accessToken) }
}
