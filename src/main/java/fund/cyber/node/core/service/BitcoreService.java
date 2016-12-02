package fund.cyber.node.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fund.cyber.node.model.bitcore.Block;
import fund.cyber.node.model.bitcore.Header;
import fund.cyber.node.model.bitcore.Tx;
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

    public Header getHeader(final long height) throws IOException {
        final URL jsonUrl = new URL(bitcoreUrl + "block-index/" + height);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonUrl, Header.class);
    }

    public Tx getTx(final String txid) throws IOException {
        final URL jsonUrl = new URL(bitcoreUrl + "tx/" + txid);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonUrl, Tx.class);
    }

}
