package com.quorum.tessera.p2p.extendedPrivacy;

import io.swagger.v3.oas.annotations.media.Schema;

public class ExtendedPrivacyRequest {
    
	@Schema(
		description = "the id of the protocol to be performed", type="string")
	private String protocolId;

	@Schema(description = "the port to connect", type="int")
	private Integer port;

	@Schema(description = "the pmt of the associated transaction", type="string")
	private String pmt;

	public String getProtocolId() {
		return this.protocolId;
	}

	public void setProtocolId(final String protocolId) {
		this.protocolId = protocolId;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(final Integer port) {
		this.port = port;
	}

	public String getPmt() {
		return this.pmt;
	}

	public void setPmt(final String pmt) {
		this.pmt = pmt;
	}

}
