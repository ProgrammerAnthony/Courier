package com.courier.core.mapper;

import com.courier.core.service.CourierTaskInstance;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Mapper
@Repository
public interface TaskStoreMapper {

    /**
     * save courier task for data consistency
     * @param courierTaskInstance
     * @return
     */
    @Insert("INSERT INTO courier_consistency_task("
                + "task_id,"
                + "task_status,"
                + "execute_times,"
                + "execute_time,"
                + "parameter_types,"
                + "method_name,"
                + "method_sign_name,"
                + "execute_interval_sec,"
                + "delay_time,"
                + "task_parameter,"
                + "performance_way,"
                + "thread_way,"
                + "error_msg,"
                + "alert_expression,"
                + "alert_action_bean_name,"
                + "fallback_class_name,"
                + "fallback_error_msg,"
                + "shard_key,"
                + "gmt_create,"
                + "gmt_modified"
            + ") VALUES("
                + "#{taskId},"
                + "#{taskStatus},"
                + "#{executeTimes},"
                + "#{executeTime},"
                + "#{parameterTypes},"
                + "#{methodName},"
                + "#{methodSignName},"
                + "#{executeIntervalSec},"
                + "#{delayTime},"
                + "#{taskParameter},"
                + "#{performanceWay},"
                + "#{threadWay},"
                + "#{errorMsg},"
                + "#{alertExpression},"
                + "#{alertActionBeanName},"
                + "#{fallbackClassName},"
                + "#{fallbackErrorMsg},"
                + "#{shardKey},"
                + "#{gmtCreate},"
                + "#{gmtModified}"
            + ")")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    Long initTask(CourierTaskInstance courierTaskInstance);


    /**
     * get task by id
     * @param id
     * @param shardKey
     * @return
     */
    @Select("SELECT " +
            "id,task_id,task_status,execute_times,execute_time,parameter_types,method_name,method_sign_name, " +
            "execute_interval_sec,delay_time,task_parameter,performance_way," +
            "thread_way, error_msg, alert_expression, " +
            "alert_action_bean_name, fallback_class_name, fallback_error_msg,shard_key," +
            "gmt_create, gmt_modified " +
            "FROM courier_consistency_task " +
            "where " +
            "id = #{id} AND shard_key = #{shardKey}")
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "task_status", property = "taskStatus"),
            @Result(column = "execute_times", property = "executeTimes"),
            @Result(column = "execute_time", property = "executeTime"),
            @Result(column = "parameter_types", property = "parameterTypes"),
            @Result(column = "method_name", property = "methodName"),
            @Result(column = "method_sign_name", property = "methodSignName"),
            @Result(column = "execute_interval_sec", property = "executeIntervalSec"),
            @Result(column = "delay_time", property = "delayTime"),
            @Result(column = "bean_class_name", property = "beanClassName"),
            @Result(column = "task_parameter", property = "taskParameter"),
            @Result(column = "performance_way", property = "performanceWay"),
            @Result(column = "thread_way", property = "threadWay"),
            @Result(column = "error_msg", property = "errorMsg"),
            @Result(column = "alert_expression", property = "alertExpression"),
            @Result(column = "alert_action_bean_name", property = "alertActionBeanName"),
            @Result(column = "fallback_class_name", property = "fallbackClassName"),
            @Result(column = "fallback_error_msg", property = "fallbackErrorMsg"),
            @Result(column = "shard_key", property = "shardKey"),
            @Result(column = "gmt_create", property = "gmtCreate"),
            @Result(column = "gmt_modified", property = "gmtModified")
    })
    CourierTaskInstance getTaskByIdAndShardKey(@Param("id") Long id, @Param("shardKey") Long shardKey);

    /**
     * get uncompleted task
     * @param startTime
     * @param endTime
     * @param limitTaskCount
     * @return
     */
    @Select("SELECT " +
            "id,task_id,task_status,execute_times,execute_time,parameter_types,method_name,method_sign_name, " +
            "execute_interval_sec,delay_time,task_parameter,performance_way," +
            "thread_way, error_msg, alert_expression, " +
            "alert_action_bean_name, fallback_class_name, fallback_error_msg,shard_key," +
            "gmt_create, gmt_modified " +
            "FROM courier_consistency_task " +
            "WHERE " +
            "task_status <= 2 " +
            "AND execute_time>=#{startTime} AND execute_time<=#{endTime} " +
            "order by execute_time desc " +
            "LIMIT #{limitTaskCount}")
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "task_status", property = "taskStatus"),
            @Result(column = "execute_times", property = "executeTimes"),
            @Result(column = "execute_time", property = "executeTime"),
            @Result(column = "parameter_types", property = "parameterTypes"),
            @Result(column = "method_name", property = "methodName"),
            @Result(column = "method_sign_name", property = "methodSignName"),
            @Result(column = "execute_interval_sec", property = "executeIntervalSec"),
            @Result(column = "delay_time", property = "delayTime"),
            @Result(column = "task_parameter", property = "taskParameter"),
            @Result(column = "performance_way", property = "performanceWay"),
            @Result(column = "thread_way", property = "threadWay"),
            @Result(column = "error_msg", property = "errorMsg"),
            @Result(column = "alert_expression", property = "alertExpression"),
            @Result(column = "alert_action_bean_name", property = "alertActionBeanName"),
            @Result(column = "fallback_class_name", property = "fallbackClassName"),
            @Result(column = "fallback_error_msg", property = "fallbackErrorMsg"),
            @Result(column = "shard_key", property = "shardKey"),
            @Result(column = "gmt_create", property = "gmtCreate"),
            @Result(column = "gmt_modified", property = "gmtModified")
    })
    List<CourierTaskInstance> listByUnFinishTask(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("limitTaskCount") Long limitTaskCount);


    /**
     * start task
     * @param courierTaskInstance
     * @return
     */
    @Update("UPDATE "
            + "courier_consistency_task "
            + "SET "
            + "task_status=#{taskStatus},"
            + "execute_times=execute_times+1,"
            + "execute_time=#{executeTime} "
            + "WHERE id=#{id} and task_status!=1 and shard_key=#{shardKey}"
    )
    int turnOnTask(CourierTaskInstance courierTaskInstance);


    /**
     * mark task success means delete
     * @param courierTaskInstance
     * @return
     */
    @Delete("DELETE FROM courier_consistency_task WHERE id=#{id} and shard_key=#{shardKey}")
    int markSuccess(CourierTaskInstance courierTaskInstance);


    /**
     * mark task fail means update
     * @param courierTaskInstance
     * @return
     */
    @Update("UPDATE courier_consistency_task SET task_status=2, error_msg=#{errorMsg}, execute_time=#{executeTime} WHERE id=#{id} and shard_key=#{shardKey}")
    int markFail(CourierTaskInstance courierTaskInstance);


    /**
     * mark task fallback fail
     * @param courierTaskInstance
     * @return
     */
    @Update("UPDATE courier_consistency_task SET fallback_error_msg=#{fallbackErrorMsg} WHERE id=#{id} and shard_key=#{shardKey}")
    int markFallbackFail(CourierTaskInstance courierTaskInstance);

}
