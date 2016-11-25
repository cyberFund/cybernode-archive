package fund.cyber.node.core.data;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RethinkDBConnectionFactory {

    @Value(value = "${rethinkdb.host}")
    private final String host;

    public RethinkDBConnectionFactory(final String host) {
        this.host = host;
    }

    public Connection createConnection() {
        return RethinkDB.r.connection().hostname(host).connect();
    }
}