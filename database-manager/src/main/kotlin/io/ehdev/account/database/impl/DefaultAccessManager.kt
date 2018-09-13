package io.ehdev.account.database.impl

import io.ehdev.account.database.api.AccessManager
import io.ehdev.account.db.tables.RuleGrantTable
import io.ehdev.account.model.resource.AccessRuleModel
import io.ehdev.account.model.resource.RoleId
import io.ehdev.account.model.resource.UserId
import io.ehdev.account.model.user.AccountManagerUser
import org.jooq.DSLContext
import org.jooq.impl.DSL
import javax.inject.Inject

class DefaultAccessManager @Inject constructor(
        private val dslContext: DSLContext) : AccessManager {

    private val grantTable = RuleGrantTable.RULE_GRANT

    override fun grantPermission(user: AccountManagerUser, role: AccessRuleModel) {
        dslContext
                .insertInto(grantTable)
                .columns(grantTable.RULE_ID, grantTable.USER_ID)
                .values(role.id, user.userId)
                .onDuplicateKeyIgnore()
                .execute()
    }

    override fun revokePermission(user: AccountManagerUser, role: AccessRuleModel) {
        dslContext
                .deleteFrom(grantTable)
                .where(grantTable.RULE_ID.eq(role.id), grantTable.USER_ID.eq(user.userId))
                .execute()
    }

    override fun hasPermission(user: AccountManagerUser, role: AccessRuleModel): Boolean {
        val count = dslContext
                .select(DSL.count())
                .from(grantTable)
                .where(grantTable.RULE_ID.eq(role.id), grantTable.USER_ID.eq(user.userId))
                .fetchOne(0, Int::class.java)

        return count == 1 || user.userType == AccountManagerUser.UserType.SuperAdmin
    }

    override fun getUsersForRole(role: AccessRuleModel): List<UserId> {
        return dslContext
                .select(grantTable.USER_ID)
                .from(grantTable)
                .where(grantTable.RULE_ID.eq(role.id))
                .map { it -> createUserId(it.get(grantTable.USER_ID)) }
    }

    override fun getRolesForUser(user: AccountManagerUser): List<RoleId> {
        return dslContext
                .select(grantTable.RULE_ID)
                .from(grantTable)
                .where(grantTable.USER_ID.eq(user.userId))
                .map { it -> createRoleId(it.get(grantTable.RULE_ID)) }
    }

    companion object {
        fun createUserId(id: Long): UserId {
            return object : UserId {
                override fun getUserId(): Long = id
            }
        }

        fun createRoleId(id: Long): RoleId {
            return object : RoleId {
                override fun getRoleId(): Long = id
            }
        }
    }

}