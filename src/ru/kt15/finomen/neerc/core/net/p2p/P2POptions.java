package ru.kt15.finomen.neerc.core.net.p2p;

public class P2POptions {
	private String clientId;
	private boolean useMulticast;
	private String multicastGroupIP;
	private int multicastGroupPort;
	private boolean allowUDPRelay;
	private boolean allowTCPRelay;
	private int UDPPort;
	private int TCPPort;
	private boolean echoRequests;
	private String clientCert;
	private String rootSign;
	private String rootCert;
	private boolean useIpV6;
	
	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * @return the useMulticast
	 */
	public boolean isUseMulticast() {
		return useMulticast;
	}
	/**
	 * @param useMulticast the useMulticast to set
	 */
	public void setUseMulticast(boolean useMulticast) {
		this.useMulticast = useMulticast;
	}
	/**
	 * @return the multicastGroupIP
	 */
	public String getMulticastGroupIP() {
		return multicastGroupIP;
	}
	/**
	 * @param multicastGroupIP the multicastGroupIP to set
	 */
	public void setMulticastGroupIP(String multicastGroupIP) {
		this.multicastGroupIP = multicastGroupIP;
	}
	/**
	 * @return the multicastGroupPort
	 */
	public int getMulticastGroupPort() {
		return multicastGroupPort;
	}
	/**
	 * @param multicastGroupPort the multicastGroupPort to set
	 */
	public void setMulticastGroupPort(int multicastGroupPort) {
		this.multicastGroupPort = multicastGroupPort;
	}
	/**
	 * @return the allowUDPRelay
	 */
	public boolean isAllowUDPRelay() {
		return allowUDPRelay;
	}
	/**
	 * @param allowUDPRelay the allowUDPRelay to set
	 */
	public void setAllowUDPRelay(boolean allowUDPRelay) {
		this.allowUDPRelay = allowUDPRelay;
	}
	/**
	 * @return the allowTCPRelay
	 */
	public boolean isAllowTCPRelay() {
		return allowTCPRelay;
	}
	/**
	 * @param allowTCPRelay the allowTCPRelay to set
	 */
	public void setAllowTCPRelay(boolean allowTCPRelay) {
		this.allowTCPRelay = allowTCPRelay;
	}
	/**
	 * @return the uDPPort
	 */
	public int getUDPPort() {
		return UDPPort;
	}
	/**
	 * @param uDPPort the uDPPort to set
	 */
	public void setUDPPort(int uDPPort) {
		UDPPort = uDPPort;
	}
	/**
	 * @return the tCPPort
	 */
	public int getTCPPort() {
		return TCPPort;
	}
	/**
	 * @param tCPPort the tCPPort to set
	 */
	public void setTCPPort(int tCPPort) {
		TCPPort = tCPPort;
	}
	/**
	 * @return the echoRequests
	 */
	public boolean isEchoRequests() {
		return echoRequests;
	}
	/**
	 * @param echoRequests the echoRequests to set
	 */
	public void setEchoRequests(boolean echoRequests) {
		this.echoRequests = echoRequests;
	}
	/**
	 * @return the clientCert
	 */
	public String getClientCert() {
		return clientCert;
	}
	/**
	 * @param clientCert the clientCert to set
	 */
	public void setClientCert(String clientCert) {
		this.clientCert = clientCert;
	}
	/**
	 * @return the rootSign
	 */
	public String getRootSign() {
		return rootSign;
	}
	/**
	 * @param rootSign the rootSign to set
	 */
	public void setRootSign(String rootSign) {
		this.rootSign = rootSign;
	}
	/**
	 * @return the rootCert
	 */
	public String getRootCert() {
		return rootCert;
	}
	/**
	 * @param rootCert the rootCert to set
	 */
	public void setRootCert(String rootCert) {
		this.rootCert = rootCert;
	}
	/**
	 * @return the useIpV6
	 */
	public boolean isUseIpV6() {
		return useIpV6;
	}
	/**
	 * @param useIpV6 the useIpV6 to set
	 */
	public void setUseIpV6(boolean useIpV6) {
		this.useIpV6 = useIpV6;
	}
}
