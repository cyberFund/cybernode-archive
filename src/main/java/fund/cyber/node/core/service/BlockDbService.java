package fund.cyber.node.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlNonExistenceError;
import com.rethinkdb.net.Connection;
import fund.cyber.node.core.data.RethinkDBConnectionFactory;
import fund.cyber.node.model.bitcore.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static fund.cyber.node.core.data.DBConstants.BLOCK_TABLE;
import static fund.cyber.node.core.data.DBConstants.DB;
import static fund.cyber.node.core.data.DBConstants.HEIGHT_FIELD;

@Service
public class BlockDbService {

    @Autowired
    private RethinkDBConnectionFactory connectionFactory;

    private static final RethinkDB r = RethinkDB.r;

    private static final Logger log = LoggerFactory.getLogger(BlockDbService.class);

    public Block getMaxHeightBlock() {
        try {
            final Connection connection = connectionFactory.createConnection();
            final Map<String, Object> blockJson = r.db(DB).table(BLOCK_TABLE).max(HEIGHT_FIELD).run(connection);
            connection.close();
            return convert(blockJson);
        } catch (final ReqlNonExistenceError e) {
            return null;
        } catch (final IOException e) {
            log.error("Convert error");
            throw new RuntimeException(e);
        }
    }

    private Block convert(final Map<String, Object> blockJson) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String blockString = mapper.writeValueAsString(blockJson);
        return mapper.readValue(blockString, Block.class);
    }

    public void putBlock(final Block block) {
        final Connection connection = connectionFactory.createConnection();
        r.db(DB).table(BLOCK_TABLE).insert(block).run(connection);
        connection.close();
    }
}
