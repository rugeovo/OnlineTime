package ruge.onlinetime.database

import taboolib.common.platform.function.info
import taboolib.expansion.persistentContainer
import java.util.concurrent.ConcurrentHashMap

class OnlineTimeBase(){

    data class OnlineTimeData(
        var uuid : String,
        var time : String,
        var second : Int
    )

    /**
     * 数据缓存
     */
    val onlineTimeCache: ConcurrentHashMap<String,OnlineTimeData> = ConcurrentHashMap()

    val tableName = "onlineTime"

    private val container by lazy {
        persistentContainer { new<OnlineTimeData>(tableName) }
    }

    private val containerx = container[tableName]


    /**
     * 传入所有在线玩家的 uuid 列表
     * 获取当前在线玩家的所有数据
     */
    fun getAllDataByUidAndTime(uuids: MutableList<String>, time: String): List<OnlineTimeData> {

        // 1. 从数据库查询符合条件的数据
        val existingData = containerx.get<OnlineTimeData> {
            "uuid" inside uuids.toTypedArray()
            and {
                "time" eq time
            }
        }

        // 2. 提取已存在的 UUID（避免重复处理）
        val existingUuids = existingData.map { it.uuid }.toSet()

        // 3. 补全缺失的 UUID 数据
        val missingData = uuids
            .filterNot { it in existingUuids } // 过滤出未查询到的 UUID
            .map { uuid ->
                OnlineTimeData(
                    uuid = uuid,
                    time = time,
                    second = 0 // 默认补全为 0
                )
            }

        // 4. 将 uuid 没有的数据进行初始化
        addOnlineTimeDatas(missingData)

        // 5. 合并结果并返回
        return (existingData + missingData).also { datas ->
            onlineTimeCache.clear()
            datas.forEach { data ->
                onlineTimeCache[data.uuid] = data
            }
        }
    }

    /**
     * 更新数据
     */
    fun updateOnlineTimeDatas(data: List<OnlineTimeData>) {
        val tablex = containerx.table
        val dataSource = containerx.dataSource
        data.forEach { data ->
            tablex.update(dataSource) {
                where("uuid" eq data.uuid and("time" eq data.time))
                set("second", data.second)
            }
        }
    }

    /**
     * 添加初始数据到数据库中
     */
    fun addOnlineTimeDatas(datas: List<OnlineTimeData>) {
        containerx.insert(
            datas
        )
    }


}
