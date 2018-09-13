package io.ehdev.account.web.configuration.internal

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import org.jooq.ExecuteContext
import org.jooq.Query
import org.jooq.impl.DefaultExecuteListener
import org.slf4j.LoggerFactory

class JooqMetricsCollector(metrics: MetricRegistry, slowQueryTimeout: Long) : DefaultExecuteListener() {

    private val localTimer: ThreadLocal<LocalTimer>

    init {
        val responses = metrics.timer(MetricRegistry.name(JooqMetricsCollector::class.java, "queries"))
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

        var instance: Timer.Context? = null
        fun start() {
            instance = timer.time()
        }

        fun end(unit: () -> Query?) {
            val time = instance?.stop() ?: return
            val query = unit.invoke() ?: return
            val ms = time / 1000000
            if (query.sql != null && ms >= slowQueryTimeout) {
                log.info("Long running ({} ms) sql query `{}`", ms, query.sql)
            }
        }
    }
}
