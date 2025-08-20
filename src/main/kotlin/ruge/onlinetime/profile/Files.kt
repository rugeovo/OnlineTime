package ruge.onlinetime.profile

import ruge.onlinetime.OnlineTimeFun
import ruge.onlinetime.database.OnlineTimeBase
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object Files {

    @Config("config.yml", autoReload = true)
    lateinit var config: ConfigFile

    val onlineTimeBase : OnlineTimeBase by lazy { OnlineTimeBase() }

    lateinit var onlineTimeFun : OnlineTimeFun

}