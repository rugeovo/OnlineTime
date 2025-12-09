package ruge.onlinetime

import org.bukkit.Bukkit
import ruge.onlinetime.profile.Files.onlineTimeBase
import taboolib.common.platform.function.submit
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class OnlineTimeFun {

    /**
     * 今天的时间,用来做刷新判断
     */
    var today : String = getTime()

    fun startTimer(){

        submit(async = true,period = 20){
            /**
             * 更新今日日期
             */
            today = getTime()

            /**
             * 获取当前所有在线玩家的uuid
             */
            val uuids = Bukkit.getOnlinePlayers().map { it.uniqueId.toString() }.toMutableList()

            if (uuids.isNotEmpty()){
                /**
                 * 获取在线玩家当日数据，并增加 1 秒
                 */
                val datas = onlineTimeBase.getAllDataByUidAndTime(uuids, today).map { data ->
                    data.copy(second = data.second + 1)
                }

                /**
                 * 批量更新到数据库
                 */
                onlineTimeBase.updateOnlineTimeDatas(datas)
            }
        }
    }

    /**
     * 获取当前日期（使用 Asia/Shanghai 时区）
     *
     * 为什么不用 LocalDateTime.now()？
     * - 它依赖 JVM 系统时区，跨服务器部署会不一致
     * - 使用显式时区确保所有服务器的"今天"是同一天
     */
    fun getTime(): String {
        val currentDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDateTime.format(formatter)
    }

}