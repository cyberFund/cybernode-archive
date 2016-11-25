package fund.cyber.node.core.chain;


import fund.cyber.node.core.service.BlockChainService;
import fund.cyber.node.core.service.BlockService;
import fund.cyber.node.core.service.FileService;
import fund.cyber.node.model.File;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.toIntExact;
import static org.bitcoinj.core.Utils.readUint32;
import static org.bitcoinj.core.Utils.uint32ToByteArrayBE;

@Service
public class FileParser {

    @Value("${bitcoin.data.dir}")
    private String folder;

    private static final Logger log = LoggerFactory.getLogger(FileParser.class);

    @Autowired
    private NetworkParameters params;

    @Autowired
    private BlockChainService blockChainService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private FileService fileService;

    @Autowired
    private BlockStore blockStore;


    private final Boolean monitor = false;
    private Boolean reading = false;

    @Autowired
    private TaskExecutor taskExecutor;

    public void startReadBlocksFromDisk() {
        if (!reading) {
            taskExecutor.execute (() -> {
                synchronized (monitor) {
                    reading = true;
                    try {
                        readBlocksFromDisk();
                    } catch (final IOException ioe) {
                        log.error("File parsing error", ioe);
                    } catch (final BlockStoreException e) {
                        log.error("Block StoreFile reading error", e);
                    } catch (final Throwable t) {
                        log.error("Block StoreFile reading error", t);
                    } finally {
                        reading = false;
                    }
                }
            });
        }
    }

    public void stopReadBlocksFromDisk() {
        reading = false;
    }

    private void readBlocksFromDisk() throws IOException, BlockStoreException {
        int current;
        try {
            current = blockService.getEmptyBlockMinHeight();
        } catch (final NullPointerException npe) {
            reading = false;
            return;
        }

        final File fileObj = fileService.getByHeight(current);
        long fileNumber = fileObj != null ? fileObj.getId() : 0;

        final Map<Integer, Block> blocks = new HashMap<>();

        boolean fileOpened = false;
        boolean eof = false;
        InputStream input = null;
        int max = 0;
        while (!eof && reading) {
            if (!fileOpened) {
                max = 0;
                final String file = String.format("blk%05d.dat", fileNumber);
                log.info(file);
                try {
                    input = new FileInputStream(folder + file);
                } catch (final FileNotFoundException e) {
                    log.error(file, e);
                    eof = true;
                    continue;
                }
            }
            Block block = readBlock(input);
            if (block != null) {
                fileOpened = true;

                    final int height = blockStore.get(block.getHash()).getHeight();
                    max = Math.max(max, height);
                    log.info("read block:" + Integer.toString(height));
                    if (current == height) {

                        blockChainService.update(block);
                        log.info("write block:" + current);
                        current++;
                        while (blocks.size() > 0) {
                            if (!blocks.containsKey(current)) {
                                break;
                            }
                            block = blocks.get(current);
                            blocks.remove(current);
                            blockChainService.update(block);
                            log.info("write block:" + current);
                            current++;
                        }
                    } else if (current < height){
                        blocks.put(height, block);
                    }
            } else if (fileOpened) {
                input.close();
                fileNumber++;
                fileOpened = false;
                fileService.save(new File(fileNumber, max));
            } else {
                eof = true;
            }
        }
        reading = false;
    }

    private long detectFileNumberForPut() {
        final Integer height = blockService.getEmptyBlockMinHeight();
        if (height == null) {
            return -1;
        }
        final File fileObj = fileService.getByHeight(height);
        return fileObj != null ? fileObj.getId() : 0;

    }

    private int detectFilledHeight(final List<Integer> heights) {
        if (heights == null || heights.size() == 0) {
            return 0;
        }
        Collections.sort(heights);
        Integer result = heights.get(0);
        for (int i = 1; i < heights.size(); i++) {
            result++;
            if (!result.equals(heights.get(i))) {
                return result;
            }
        }
        return result;
    }

    private Block readBlock(final InputStream input) throws IOException {

        final byte[] bytes = new byte[4];
        if (input.read(bytes, 0, 4) < 4) {
            return null;
        }
        if (!checkHeader(params, bytes)) {
            log.error("Blockchain format error");
            return null;
        }

        input.read(bytes, 0, 4);
        final int size = toIntExact(readUint32(bytes, 0));

        final byte[] blockBytes = new byte[size];
        input.read(blockBytes, 0, size);

        try {
            return params.getDefaultSerializer().makeBlock(blockBytes);
        } catch (final ProtocolException e) {
            log.error("Blockchain format error", e);
            return null;
        }
    }

    private boolean checkHeader(final NetworkParameters params, final byte[] bytes) {
        final byte[] magicBytes = new byte[4];
        uint32ToByteArrayBE(params.getPacketMagic(), magicBytes, 0);
        return Arrays.equals(magicBytes, bytes);

    }

}
