package fund.cyber.node.core.data.migrations;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import fund.cyber.node.core.data.Migration;
import fund.cyber.node.core.data.RethinkDBConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fund.cyber.node.core.data.DBConstants.ADDRESS_TABLE;
import static fund.cyber.node.core.data.DBConstants.BLOCK_AND_POSITION_INDEX;
import static fund.cyber.node.core.data.DBConstants.BLOCK_ID_FIELD;
import static fund.cyber.node.core.data.DBConstants.BLOCK_TABLE;
import static fund.cyber.node.core.data.DBConstants.DB;
import static fund.cyber.node.core.data.DBConstants.FILE_TABLE;
import static fund.cyber.node.core.data.DBConstants.HASH_FIELD;
import static fund.cyber.node.core.data.DBConstants.HEIGHT_FIELD;
import static fund.cyber.node.core.data.DBConstants.INPUT_TABLE;
import static fund.cyber.node.core.data.DBConstants.MIGRATION_TABLE;
import static fund.cyber.node.core.data.DBConstants.OUTPUT_ID_FIELD;
import static fund.cyber.node.core.data.DBConstants.OUTPUT_TABLE;
import static fund.cyber.node.core.data.DBConstants.PARENT_BLOCK_ID_FIELD;
import static fund.cyber.node.core.data.DBConstants.POSITION_FIELD;
import static fund.cyber.node.core.data.DBConstants.TX_AND_POSITION_INDEX;
import static fund.cyber.node.core.data.DBConstants.TX_ID_FIELD;
import static fund.cyber.node.core.data.DBConstants.TX_TABLE;

@Component
public class InitDbMigration extends Migration {

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    private static final RethinkDB r = RethinkDB.r;

    protected InitDbMigration() {
        super(0);
    }

    @Override
    public void run() {
        final Connection connection = connectionFactory.createConnection();

        r.dbCreate(DB).run(connection);

        r.db(DB).tableCreate(MIGRATION_TABLE).run(connection);
        r.db(DB).tableCreate(BLOCK_TABLE).run(connection);
        r.db(DB).tableCreate(TX_TABLE).run(connection);
        r.db(DB).tableCreate(INPUT_TABLE).run(connection);
        r.db(DB).tableCreate(OUTPUT_TABLE).run(connection);
        r.db(DB).tableCreate(ADDRESS_TABLE).run(connection);
        r.db(DB).tableCreate(FILE_TABLE).run(connection);

        r.db(DB).table(ADDRESS_TABLE).indexCreate(HASH_FIELD).run(connection);
        r.db(DB).table(BLOCK_TABLE).indexCreate(PARENT_BLOCK_ID_FIELD).run(connection);
        r.db(DB).table(BLOCK_TABLE).indexCreate(HASH_FIELD).run(connection);
        r.db(DB).table(BLOCK_TABLE).indexCreate(HEIGHT_FIELD).run(connection);
        r.db(DB).table(TX_TABLE).indexCreate(HASH_FIELD).run(connection);
        r.db(DB).table(TX_TABLE).indexCreate(BLOCK_AND_POSITION_INDEX, row -> r.array(row.g(BLOCK_ID_FIELD), row.g(POSITION_FIELD))).run(connection);
        r.db(DB).table(OUTPUT_TABLE).indexCreate(TX_AND_POSITION_INDEX, row -> r.array(row.g(TX_ID_FIELD), row.g(POSITION_FIELD))).run(connection);
        r.db(DB).table(INPUT_TABLE).indexCreate(TX_AND_POSITION_INDEX, row -> r.array(row.g(TX_ID_FIELD), row.g(POSITION_FIELD))).run(connection);
        r.db(DB).table(INPUT_TABLE).indexCreate(OUTPUT_ID_FIELD).run(connection);

        connection.close();
    }
}
