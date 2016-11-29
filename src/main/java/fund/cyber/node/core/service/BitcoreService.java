package fund.cyber.node.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fund.cyber.node.model.bitcore.Block;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class BitcoreService {

    @Value("${bitcore.insight.url}")
    private String bitcoreUrl;

    public Block getBlock(final String hash) throws IOException {
        final URL jsonUrl = new URL(bitcoreUrl + "block/" + hash);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonUrl, Block.class);
    }
}
