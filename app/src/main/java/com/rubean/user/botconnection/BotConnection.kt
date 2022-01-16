package com.rubean.user.botconnection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import com.rubean.user.dummywordgame.BotMoveListener

const val BOT_SEND_WORD_ACTION_CODE = 1
const val BOT_GAME_OVER = 2
const val BOT_WORD_BUNDLE_KEY = "BOT_WORD_BUNDLE_KEY"
const val BOT_PLAY_KEY = "BOT_PLAY_KEY"

const val BOT_PACKAGE = "com.rubean.bot"
const val BOT_CLASS_NAME = "com.rubean.bot.BotService"


class BotConnection {

    private var botListener: BotMoveListener? = null
    private val messengerFromBot: Messenger = Messenger(BotResponseHandler())
    private var messengerToBot: Messenger? = null
    private var isBound: Boolean = false

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            messengerToBot = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            messengerToBot = null
            isBound = false
        }
    }

    fun start(context: Context, botListener: BotMoveListener) {
        this.botListener = botListener
        val intent = Intent()
        intent.component = ComponentName(BOT_PACKAGE, BOT_CLASS_NAME)

        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun stop(context: Context) {
        if (isBound) {
            context.unbindService(mConnection)
            isBound = false
        }
    }

    fun sendWord(word: String) {
        if (!isBound) {
            return
        }

        val msg = Message.obtain(null, BOT_SEND_WORD_ACTION_CODE, 0, 0)
        val data = Bundle()
        data.putString(BOT_WORD_BUNDLE_KEY, word)
        msg.data = data
        msg.replyTo = messengerFromBot

        try {
            messengerToBot?.send(msg)
        } catch (e: RemoteException) {
            Log.e("BotConnection", e.message ?: "")
        }
    }

    fun tellBotGameIsOver() {
        if (!isBound) {
            return
        }

        val msg = Message.obtain(null, BOT_GAME_OVER, 0, 0)

        try {
            messengerToBot?.send(msg)
        } catch (e: RemoteException) {
            Log.e("BotConnection", e.message ?: "")
        }
    }

    inner class BotResponseHandler : Handler(Looper.getMainLooper())  {

        override fun handleMessage(msg: Message) {
            val botInput = msg.peekData()?.getString(BOT_PLAY_KEY) ?: ""
            botListener?.handleBotPlayerInput(botInput)
        }
    }
}