Courier is responsible for Data Consistency.

Courier是一个分布式消息投递的使者，确保消息的最终一致性

- 使用Spring 自动配置机制和环绕通知对方法上的注解进行解析和环绕通知处理，
- 引入mybatis插入对应的接口数据，通过数据执行成功/失败更新对应的sql
- 在异常情况下会触发fallback机制和告警

本地消息表方案：在本地事务中将要执行的异步操作记录在消息表中，如果执行失败，可以通过定时任务来补偿

- with spring auto configurations and around aspect to proceed methods
- import mybatis to insert a pre sql and post update sql with success or fail operation
- with fallback and fallback error（will trigger alarm ）support
