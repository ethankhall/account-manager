package io.ehdev.account.web.configuration.internal

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.jooq.ExecuteContext
import org.jooq.Query
import org.jooq.impl.DefaultExecuteListener
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class JooqMetricsCollector(metrics: MeterRegistry, slowQueryTimeout: Long) : DefaultExecuteListener() {

    private val localTimer: ThreadLocal<LocalTimer>

    init {
        val responses = metrics.timer("${this.javaClass.simpleName}.queries")
        localTimer = ThreadLocal.withInitial {
            LocalTimer(responses, slowQueryTimeout)
        }
    }

    override fun start(ctx: ExecuteContext) {
        localTimer.get().start()
    }

    override fun executeEnd(ctx: ExecuteContext) {
        localTimer.get().end { ctx.query() }
    }

    class LocalTimer(private val timer: Timer, private val slowQueryTimeout: Long) {
        private val log = LoggerFactory.getLogger(LocalTimer::class.java)!!


        private var instance: Timer.Sample? = null

        fun start() {
            instance = Timer.start()
        }

        fun end(unit: () -> Query?) {
            val query = unit.invoke() ?: return
            val time = instance?.stop(timer) ?: return
            val ms = TimeUnit.NANOSECONDS.toMillis(time)
            if (query.sql != null && ms >= slowQueryTimeout) {
                log.info("Long running ({} ms) sql query `{}`", ms, query.sql)
            }
        }
    }
}
