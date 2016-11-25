package fund.cyber.chainparser.core.dao;

import fund.cyber.chainparser.model.File;
import org.springframework.stereotype.Repository;

@Repository
public class FileDao extends BaseEntityDao<File, Long> {

    public FileDao() {
        super(File.class);
    }

    public File getByHeight(final int height) {
        return getWithMaxValue("height", (builder, root) -> builder.lessThan(root.get("height"), height));
    }

    public File get(final long id) {
        return getEntity(id);
    }
}