package com.quorum.tessera.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.Objects;

/** The JPA entity that contains the interactive privacy endpoint info */
//@NamedQueries({
//    @NamedQuery(
//        name = "InteractivePrivacyEndpoint.DeleteByHash",
//        query = "delete from InteractivePrivacyEndpoint where id = :hash")
//})
/*
@NamedQueries({
    @NamedQuery(
        name = "InteractivePrivacyEndpoint.FindById",
        query = "SELECT pe FROM InteractivePrivacyEndpoint pe WHERE pe.id = :id"),
    @NamedQuery(name = "InteractivePrivacyEndpoint.FindAll", query = "select pe from InteractivePrivacyEndpoint pe")
  })
*/
@Entity
@Table(name = "INTERACTIVE_PRIVACY_ENDPOINT")
//@Table(
//    name = "INTERACTIVE_PRIVACY_ENDPOINT",
//    indexes = {@Index(name = "INTERACTIVE_PRIVACY_ENDPOINT_LOOKUPID", columnList = "LOOKUP_ID")})
public class InteractivePrivacyEndpoint implements Serializable {
    /*
    @EmbeddedId
    @AttributeOverride(
        name = "hashBytes",
        column = @Column(name = "HASH", nullable = false, unique = true, updatable = false))
    private MessageHash hash;
    */
    @Id
    @Column(name = "ID", nullable = false)
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private byte[] id;

    @Column(name = "PORT", nullable = false)
    private int port;

    @Column(name = "TIMESTAMP", nullable = false)
    private long timestamp;

    /*
    public InteractivePrivacyEndpoint(
        final MessageHash hash,
        final int port) {
        this.hash = hash;
        this.port = port;
    }
    */
    
    public InteractivePrivacyEndpoint(
        final byte[] id,
        final int port) {
        this.id = id;
        this.port = port;
    }
    
    public InteractivePrivacyEndpoint() {}

    @PrePersist
    public void onPersist() {
        this.timestamp = System.currentTimeMillis();
    }
    /*
    public MessageHash getHash() {
        return this.hash;
    }

    public void setHash(final MessageHash hash) {
        this.hash = hash;
    }
    */
    
    public byte[] getId() {
        return this.id;
    }
    
    public void setId(final byte[] id) {
        this.id = id;
    }
    
    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int hashCode() {
        //return 47 * 3 + Objects.hashCode(this.id);
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        //return (obj instanceof InteractivePrivacyEndpoint)
        //    && Objects.equals(this.id, ((InteractivePrivacyEndpoint) obj).id);
        return true;
    }

}
