package com.quorum.tessera.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

//@NamedQueries({
//    @NamedQuery(
//       name = "ExtendedPrivacyEntity.FindByKey",
//        query = "SELECT ep FROM ExtendedPrivacyEntity ep WHERE ep.key = :key"
//    )
//})
@Entity
@Table(
    name = "EXTENDED_PRIVACY_ENTITY"
)
public class ExtendedPrivacyEntity implements Serializable {
    
    @Id
    @Column(name = "ID", nullable = false)
    private String key;

    @Column(name = "DATA", nullable = false)
    private byte[] data;

    public ExtendedPrivacyEntity(final String key, final byte[] data) {
        this.key = key;
        this.data = data;
    }

    public ExtendedPrivacyEntity() {}

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        //return (obj instanceof ExtendedPrivacyEntity) && Arrays.equals(key, ((ExtendedPrivacyEntity) obj).key);
        return true;
    }

    @Override
    public int hashCode() {
        //return Arrays.hashCode(key);
        return 0;
    }

}
