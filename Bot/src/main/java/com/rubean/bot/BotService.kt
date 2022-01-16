package com.rubean.bot

import android.app.Service
import android.content.Intent
import android.os.*

const val BOT_SEND_WORD_ACTION_CODE = 1
const val BOT_GAME_OVER = 2
const val BOT_WORD_BUNDLE_KEY = "BOT_WORD_BUNDLE_KEY"
const val BOT_PLAY_KEY = "BOT_PLAY_KEY"

class BotService : Service() {

    private lateinit var messenger: Messenger

    internal class UserResponseHandler : Handler(Looper.getMainLooper()) {

        private var dummyWordGameMovePicker = DummyWordGameMovePicker()

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BOT_SEND_WORD_ACTION_CODE -> makeAMove(msg)
                BOT_GAME_OVER -> restartGame()
            }
        }

        private fun makeAMove(msg: Message) {
            val humanPlayerInput = msg.peekData().getString(BOT_WORD_BUNDLE_KEY)
            val client = msg.replyTo

            humanPlayerInput?.let { word ->
                client?.let {
                    val botMove = dummyWordGameMovePicker.decideNextMove(word)

                    val msgToModerator = Message.obtain(null, BOT_SEND_WORD_ACTION_CODE, 0, 0)
                    val data = Bundle()
                    data.putString(BOT_PLAY_KEY, botMove)
                    msgToModerator.data = data
                    it.send(msgToModerator)
                }
            }
        }

        private fun restartGame() {
            dummyWordGameMovePicker = DummyWordGameMovePicker()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        messenger = Messenger(UserResponseHandler())
        return messenger.binder
    }
}