/*
 * This file is generated by jOOQ.
 */
package io.ehdev.account.db;


import io.ehdev.account.db.tables.FlywaySchemaHistoryTable;
import io.ehdev.account.db.tables.RuleGrantTable;
import io.ehdev.account.db.tables.SsUserconnectionTable;
import io.ehdev.account.db.tables.TargetAccessRuleTable;
import io.ehdev.account.db.tables.TargetTable;
import io.ehdev.account.db.tables.UserDetailsTable;
import io.ehdev.account.db.tables.UserTokensTable;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in account_manager
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>account_manager.flyway_schema_history</code>.
     */
    public static final FlywaySchemaHistoryTable FLYWAY_SCHEMA_HISTORY = io.ehdev.account.db.tables.FlywaySchemaHistoryTable.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>account_manager.rule_grant</code>.
     */
    public static final RuleGrantTable RULE_GRANT = io.ehdev.account.db.tables.RuleGrantTable.RULE_GRANT;

    /**
     * The table <code>account_manager.ss_UserConnection</code>.
     */
    public static final SsUserconnectionTable SS_USERCONNECTION = io.ehdev.account.db.tables.SsUserconnectionTable.SS_USERCONNECTION;

    /**
     * The table <code>account_manager.target</code>.
     */
    public static final TargetTable TARGET = io.ehdev.account.db.tables.TargetTable.TARGET;

    /**
     * The table <code>account_manager.target_access_rule</code>.
     */
    public static final TargetAccessRuleTable TARGET_ACCESS_RULE = io.ehdev.account.db.tables.TargetAccessRuleTable.TARGET_ACCESS_RULE;

    /**
     * The table <code>account_manager.user_details</code>.
     */
    public static final UserDetailsTable USER_DETAILS = io.ehdev.account.db.tables.UserDetailsTable.USER_DETAILS;

    /**
     * The table <code>account_manager.user_tokens</code>.
     */
    public static final UserTokensTable USER_TOKENS = io.ehdev.account.db.tables.UserTokensTable.USER_TOKENS;
}
