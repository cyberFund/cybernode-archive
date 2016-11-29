package fund.cyber.node.core.data;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RethinkDBConnectionFactory {

    @Value("${rethinkdb.host}")
    private String host;

    public RethinkDBConnectionFactory() {

    }

    public Connection createConnection() {
        return RethinkDB.r.connection().hostname(host).connect();
    }
}