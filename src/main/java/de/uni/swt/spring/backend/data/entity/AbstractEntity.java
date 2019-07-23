package de.uni.swt.spring.backend.data.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    @Version
    private int version;

    public int getVersion() {
        return version;
    }
}
