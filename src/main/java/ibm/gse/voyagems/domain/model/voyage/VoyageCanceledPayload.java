package ibm.gse.voyagems.domain.model.voyage;

public class VoyageCanceledPayload {

    private String orderID;
    private String reason;

    public VoyageCanceledPayload(String orderID, String reason) {
        this.orderID = orderID;
        this.reason = reason;
    }

    public VoyageCanceledPayload() {}

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
