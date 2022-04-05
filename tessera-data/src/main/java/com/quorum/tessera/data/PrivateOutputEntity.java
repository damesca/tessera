package com.quorum.tessera.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.Objects;

@Entity
@Table(name = "PRIVATE_OUTPUT_ENTITY")

public class PrivateOutputEntity implements Serializable {
    
    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "OUTPUT", nullable = false)
    private String output;

    @Column(name = "TIMESTAMP", nullable = false)
    private long timestamp;

    public PrivateOutputEntity(
        final String id,
        final String output) {
            this.id = id;
            this.output = output;
    }

    public PrivateOutputEntity() {}

    @PrePersist
    public void onPersist() {
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(final String output) {
        this.output = output;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int hashCode() {
        // TODO: fix
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        // TODO: fix
        return true;
    }


}
