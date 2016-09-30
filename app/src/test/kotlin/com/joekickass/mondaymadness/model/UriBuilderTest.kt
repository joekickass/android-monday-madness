package com.joekickass.mondaymadness.model

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import uy.klutter.core.uri.UriBuilder
import uy.klutter.core.uri.buildUri
import java.net.URISyntaxException

class UriBuilderTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()!!

    val url = "https://open.spotify.com/track/4EZ3GItXQjKXWYslXV73MU"

    fun isValidUri(uri: UriBuilder): Boolean =
            uri.scheme == "https" && uri.host == "open.spotify.com"

    @Test
    fun randomSpotifyUrlIsValid() {
        val parsed = buildUri(url)
        Assert.assertTrue(isValidUri(parsed))
    }

    val msg = "Here’s a song for you… Gå aldrig ombord by Emil Jensen\nhttps://open.spotify.com/track/4EZ3GItXQjKXWYslXV73MU"

    @Test
    fun randomSpotifyShareMessageIsInvalid() {
        exception.expect(URISyntaxException::class.java)
        buildUri(msg)
    }
}