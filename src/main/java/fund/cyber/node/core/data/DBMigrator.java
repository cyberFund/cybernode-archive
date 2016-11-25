package fund.cyber.node.core.data;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import fund.cyber.node.model.MigrationLog;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

import static fund.cyber.node.core.data.DBConstants.DB;
import static fund.cyber.node.core.data.DBConstants.MIGRATION_TABLE;


@Component
public class DBMigrator implements InitializingBean {

    @Autowired
    private List<Migration> migrations;

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    private static final RethinkDB r = RethinkDB.r;

    @Override
    public void afterPropertiesSet() throws Exception {
        migrations.sort(null);
        for (Migration migration: migrations) {
            Method runMethod = Migration.class.getMethod("run");
            int hash = runMethod.hashCode();
            MigrationLog log = getMigrationLog(migration);
            if (log != null && log.getHash() != hash) {
                throw new RuntimeException("Migration fail");
            } else if (log == null) {
                migration.run();
                final Connection connection = connectionFactory.createConnection();
                r.db(DB).table(MIGRATION_TABLE).insert(new MigrationLog(migration.getId(), hash)).run(connection);
            }
        }
    }

    private MigrationLog getMigrationLog(final Migration migration) {
            final Connection connection = connectionFactory.createConnection();
            final List<String> dbList = r.dbList().run(connection);
            if (!dbList.contains(DB)) {
                return null;
            }
            final List<String> tables = r.db(DB).tableList().run(connection);
            if (!tables.contains(MIGRATION_TABLE)) {
                return null;
            }
            return r.db(DB).table(MIGRATION_TABLE).get(migration.getId()).run(connection, MigrationLog.class);
        }

}
