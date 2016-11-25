package fund.cyber.chainparser.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file")
public class File extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "height")
    private int height;

    public File() {
    }

    public File(Long id, int height) {
        this.id = id;
        this.height = height;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
