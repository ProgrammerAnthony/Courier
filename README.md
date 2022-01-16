Courier is responsible for Data Consistency.

Courier是一个分布式消息投递的使者，使用Spring aop对方法上的注解进行解析和环绕通知处理，
在异常情况下会触发fallback机制和告警，确保消息的最终一致性

with spring auto configurations to proceed methods