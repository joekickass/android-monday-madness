package com.joekickass.mondaymadness

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.*
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.joekickass.mondaymadness.realm.SpotifyShareHandlingService
import uy.klutter.core.uri.UriBuilder
import uy.klutter.core.uri.buildUri
import java.net.URISyntaxException

/**
 * Listens, validates and delegates Spotify share SMS
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG, "SMS Received!")

        val txt = getTextFromSms(intent?.extras)
        Log.d(TAG, "message=" + txt)

        val url = validateSpotifyUrl(txt)
        Log.d(TAG, "url=" + url)

        url?.let { handleUrl(context, it) }
    }

    private fun handleUrl(context: Context?, url: String) {
        val intent = Intent(context, SpotifyShareHandlingService::class.java)
        intent.data = Uri.parse(url)
        context?.startService(intent)
    }

    private fun getTextFromSms(extras: Bundle?): String {
        val pdus = extras?.get("pdus") as Array<*>
        val format = extras?.getString("format")
        var txt = ""
        for (pdu in pdus) {
            val smsmsg = getSmsMsg(pdu as ByteArray?, format)
            val submsg = smsmsg?.displayMessageBody
            submsg?.let { txt = "$txt$it" }
        }
        return txt
    }

    private fun getSmsMsg(pdu: ByteArray?, format: String?): SmsMessage? {
        when {
            VERSION.SDK_INT >= VERSION_CODES.M -> return SmsMessage.createFromPdu(pdu, format)
            else -> return SmsMessage.createFromPdu(pdu)
        }
    }

    private fun validateSpotifyUrl(msg: String) : String? {
        fun isValidUri(uri: UriBuilder): Boolean =
                uri.scheme == "https" && uri.host == "open.spotify.com"
        val url = msg.replaceBefore("https", "")
        val parsed = try { buildUri(url) } catch (e: URISyntaxException) { return null }
        return if ( isValidUri(parsed) ) parsed.asString() else null
    }

    companion object {
        private val TAG = SmsReceiver::class.java.simpleName
    }
}