package ruge.onlinetime.papi

import org.bukkit.entity.Player
import ruge.onlinetime.profile.Files.onlineTimeBase
import taboolib.platform.compat.PlaceholderExpansion

object OnlineTimePapi : PlaceholderExpansion{

    override val identifier: String = "onlineTime"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player == null) return "null"
        val uuid = player.uniqueId.toString()

        val data = onlineTimeBase.onlineTimeCache[uuid] ?: return "null"


        return formatFlexibleTime(data.second,args)
    }

    /**
     * 将秒数转换为自定义格式的时间字符串
     * @param seconds 总秒数（应始终为正数）
     * @param format 格式字符串（如 "HH时mm分SS秒"）
     * @return 格式化后的时间字符串
     */
    fun formatFlexibleTime(seconds: Int, format: String): String {
        // 计算各时间单位
        val hours = seconds / 3600
        val minutes = seconds / 60

        // 替换格式中的占位符
        return format
            .replace("HH", hours.toString())
            .replace("mm", minutes.toString())
            .replace("SS", seconds.toString())
    }

}