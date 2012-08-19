package ru.kt15.finomen.neerc.core.net.p2p;

import ru.kt15.finomen.neerc.core.net.Endpoint;

class P2PEndpoint {
	private final String id;
	
	P2PEndpoint(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

}
