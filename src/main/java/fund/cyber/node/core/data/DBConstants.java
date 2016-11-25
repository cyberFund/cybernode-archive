package fund.cyber.node.core.data;

/**
 * Created by andrey on 25.11.16.
 */
public class DBConstants {
    public static final String DB = "chain";
    public static final String MIGRATION_TABLE = "migration";
    public static final String BLOCK_TABLE = "block";
    public static final String TX_TABLE = "transaction";
    public static final String INPUT_TABLE = "input";
    public static final String OUTPUT_TABLE = "output";
    public static final String ADDRESS_TABLE = "address";
    public static final String FILE_TABLE = "file";
    public static final String HASH_FIELD = "hash";
    public static final String PARENT_BLOCK_ID_FIELD = "prevBlockId";
    public static final String HEIGHT_FIELD = "height";
    public static final String POSITION_FIELD = "position";
    public static final String BLOCK_ID_FIELD = "blockId";
    public static final String TX_ID_FIELD = "txId";
    public static final String OUTPUT_ID_FIELD = "outputId";
    public static final String TX_AND_POSITION_INDEX = "txAndPosition";
    public static final String BLOCK_AND_POSITION_INDEX = "blockAndPosition";

}
