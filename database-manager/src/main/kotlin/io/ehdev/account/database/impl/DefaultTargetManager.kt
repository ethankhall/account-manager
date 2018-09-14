package io.ehdev.account.database.impl

import io.ehdev.account.database.api.TargetManager
import io.ehdev.account.db.Tables
import io.ehdev.account.model.ADMIN_RULE
import io.ehdev.account.model.ErrorCode
import io.ehdev.account.model.PERMISSION_ADMIN
import io.ehdev.account.model.RESOURCE_MANAGEMENT_NAME
import io.ehdev.account.model.Result
import io.ehdev.account.model.resource.AccessRuleModel
import io.ehdev.account.model.resource.TargetModel
import org.jooq.DSLContext

class DefaultTargetManager(private val dslContext: DSLContext) : TargetManager {

    private val targetTable = Tables.TARGET
    private val ruleTable = Tables.TARGET_ACCESS_RULE

    override fun createTarget(targetName: String): Result<TargetModel> {
        dslContext.transaction { ctx ->
            val transactionContext = ctx.dsl()
            val id = transactionContext
                    .insertInto(targetTable, targetTable.TARGET_NAME)
                    .values(targetName)
                    .returning(targetTable.TARGET_ID)
                    .fetchOne()
                    .get(targetTable.TARGET_ID)

            createAccess(transactionContext, id, RESOURCE_MANAGEMENT_NAME, ADMIN_RULE)
            createAccess(transactionContext, id, RESOURCE_MANAGEMENT_NAME, PERMISSION_ADMIN)
        }

        return retrieveTargetModel(dslContext, targetName)
    }

    override fun deleteTarget(targetName: String): Result<Boolean> {
        return deleteTarget(dslContext, targetName)
    }

    private fun deleteTarget(context: DSLContext, targetName: String): Result<Boolean> {
        val rows = context
                .deleteFrom(targetTable)
                .where(targetTable.TARGET_NAME.eq(targetName))
                .execute()

        return if (rows >= 1) {
            Result.ok(true)
        } else {
            Result.err(ErrorCode.TARGET_NOT_EXIST)
        }
    }

    override fun getTarget(targetName: String): Result<TargetModel> {
        return retrieveTargetModel(dslContext, targetName)
    }

    override fun hasTarget(targetName: String): Boolean {
        return getTargetId(dslContext, targetName) != null
    }

    private fun retrieveTargetModel(context: DSLContext, targetName: String): Result<TargetModel> {
        return context.transactionResult { ctx ->
            val dsl = ctx.dsl()
            val targetId = getTargetId(dsl, targetName)
                    ?: return@transactionResult Result.err(ErrorCode.TARGET_NOT_EXIST)

            val rt = ruleTable.`as`("rt")
            val ruleList = dsl
                    .select(rt.RULE_ID, rt.RESOURCE_NAME, rt.ACTION_NAME)
                    .from(rt)
                    .where(rt.TARGET_ID.eq(targetId))
                    .fetch()
                    .map {
                        AccessRuleModel(targetId, it.get(rt.RULE_ID),
                                it.get(rt.RESOURCE_NAME), it.get(rt.ACTION_NAME))
                    }

            if (ruleList.isEmpty()) {
                return@transactionResult Result.err(ErrorCode.TARGET_NOT_EXIST)
            }

            return@transactionResult Result.ok(TargetModel(targetId, targetName, ruleList))
        }
    }

    private fun getTargetId(context: DSLContext, targetName: String): Long? {
        return context
                .select(targetTable.TARGET_ID)
                .from(targetTable)
                .where(targetTable.TARGET_NAME.eq(targetName))
                .fetchOne(targetTable.TARGET_ID)
    }

    override fun createAccess(targetName: String, resource: String, permission: String): Result<AccessRuleModel> {
        return dslContext.transactionResult { ctx ->
            val dsl = ctx.dsl()
            val targetId = getTargetId(dsl, targetName)
                    ?: return@transactionResult Result.err(ErrorCode.TARGET_NOT_EXIST)
            val ruleId = createAccess(dsl, targetId, resource, permission)
            return@transactionResult Result.ok(AccessRuleModel(targetId, ruleId, resource, permission))
        }
    }

    private fun createAccess(dslContext: DSLContext, targetId: Long, resource: String, permission: String): Long {
        return dslContext
                .insertInto(ruleTable)
                .columns(ruleTable.TARGET_ID, ruleTable.RESOURCE_NAME, ruleTable.ACTION_NAME)
                .values(targetId, resource, permission)
                .returning(ruleTable.RULE_ID)
                .fetchOne()
                .get(ruleTable.RULE_ID)
    }

    override fun deleteAccess(targetName: String, resource: String, permission: String): Result<Boolean> {
        return dslContext.transactionResult { ctx ->
            val dsl = ctx.dsl()
            val targetId = getTargetId(dsl, targetName)
                    ?: return@transactionResult Result.err(ErrorCode.TARGET_NOT_EXIST)

            val count = dsl
                    .deleteFrom(ruleTable)
                    .where(ruleTable.TARGET_ID.eq(targetId))
                    .and(ruleTable.RESOURCE_NAME.eq(resource))
                    .and(ruleTable.ACTION_NAME.eq(permission))
                    .execute()

            return@transactionResult if (count == 1) {
                Result.ok(true)
            } else {
                Result.err(ErrorCode.ROLE_NOT_EXIST)
            }
        }
    }
}