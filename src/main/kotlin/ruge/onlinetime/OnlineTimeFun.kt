package ruge.onlinetime

import org.bukkit.Bukkit
import ruge.onlinetime.profile.Files.onlineTimeBase
import taboolib.common.platform.function.submit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OnlineTimeFun {

    /**
     * 今天的时间,用来做刷新判断
     */
    var today : String = getTime()

    fun startTimer(){

        submit(async = true,period = 20){
            /**
             * 判断当前时间，是否是今天时间
             * 如果是，则更新玩家数据
             * 否，则进入新的一天，再更新数据
             */
            val newTime = getTime()
            if (today != newTime){
                today = newTime
            }

            /**
             * 获取当前所有在线玩家的uuid
             */
            val uuids = Bukkit.getOnlinePlayers().map { it.uniqueId.toString() }.toMutableList()

            if (uuids.isNotEmpty()){
                /**
                 * 给所有在线玩家的在线时间加 1
                 */
                val datas = onlineTimeBase.getAllDataByUidAndTime(uuids,today).map { data ->
                    data.copy(second = data.second + 1)
                }

                /**
                 * 更新玩家在数据库中的数据
                 */
                onlineTimeBase.updateOnlineTimeDatas(datas)
            }
        }
    }

    fun getTime(): String {
        // 获取当前时间
        val currentDateTime = LocalDateTime.now()

        // 定义格式化器
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 格式化时间
        return currentDateTime.format(formatter)

    }

}