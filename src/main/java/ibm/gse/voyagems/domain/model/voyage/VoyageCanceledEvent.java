package ibm.gse.voyagems.domain.model.voyage;

import ibm.gse.voyagems.domain.model.EventBase;

public class VoyageCanceledEvent extends EventBase {


	VoyageCanceledPayload payload;

    public VoyageCanceledEvent(long timestampMillis, String version, VoyageCanceledPayload payload) {
    	this.timestampMillis = timestampMillis;
    	this.version = version;
    	this.type = EventBase.TYPE_VOYAGE_CANCELED;
    	this.payload = payload;
    }

    public VoyageCanceledEvent() {
    }

	public VoyageCanceledPayload getPayload() {
		return payload;
	}

    

}
