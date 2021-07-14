import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment


object TestKafka1 {
  def main(args: Array[String]) {

    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = StreamTableEnvironment.create(env)

    tEnv.executeSql(
      """
        |CREATE TABLE log (
        |  `message` Array<string>,
        |  `rowtime` TIMESTAMP(3) METADATA FROM 'timestamp',
        |  WATERMARK FOR `rowtime` AS `rowtime` - INTERVAL '1' SECOND
        |) WITH (
        |  'connector' = 'kafka',
        |  'topic' = 'test',
        |  'csv.array-element-delimiter' = '|',
        |  'properties.bootstrap.servers' = 'localhost:9092',
        |  'format' = 'csv'
        |)
        |""".stripMargin
    )

    val Test = tEnv.sqlQuery(
      """
        |select rowtime, message[1] as tmp, message[10] as actionId, cast(message[11] as bigint) as gold,
        |proctime() as proctime
        |from log
        |""".stripMargin)
    tEnv.createTemporaryView("test", Test)

    val res = tEnv.sqlQuery(
      """
        |SELECT HOP_START(rowtime, INTERVAL '1' SECOND, INTERVAL '10' SECOND) as startTime, actionId, count(*),
        | sum(gold)
        |FROM test
        |GROUP BY HOP(rowtime, INTERVAL '1' SECOND, INTERVAL '10' SECOND), actionId
        |""".stripMargin)

    res.execute().print()

    //    val result = tEnv.sqlQuery(
    //      """
    //        |SELECT TUMBLE_START(rowtime, INTERVAL '10' SECOND) as startTime, message[10], count(*),
    //        | sum(cast(message[11] as INT)), "test2"
    //        |FROM log
    //        |GROUP BY TUMBLE(rowtime, INTERVAL '10' SECOND), message[10]
    //        |""".stripMargin)
    //    val it = result.execute().print()
    tEnv.execute("test")
  }
}