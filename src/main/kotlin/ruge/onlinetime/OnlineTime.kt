package ruge.onlinetime

import ruge.onlinetime.profile.Files.onlineTimeFun
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.module.chat.colored

object OnlineTime : Plugin() {


    override fun onActive() {
        onlineTimeFun = OnlineTimeFun()
        onlineTimeFun.startTimer()
    }

}