package fund.cyber.chainparser.core.service;

import fund.cyber.chainparser.core.dao.FileDao;
import fund.cyber.chainparser.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;


@Component
@Transactional(propagation = REQUIRED)
public class FileService extends CommonService {

    @Autowired
    private FileDao fileDao;

    public File getByHeight(final int height) {
        return fileDao.getByHeight(height);
    }

    public void save(final File file) {
        final File old = fileDao.get(file.getId());
        if (old == null) {
            fileDao.persist(file);
        } else {
            old.setHeight(file.getHeight());
        }
    }
}
