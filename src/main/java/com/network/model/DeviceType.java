package com.network.model;

public enum DeviceType {
    Gateway(0),
    Switch(1),
    Access(2),
    ;

    DeviceType(int order) {
        this.order = order;
    }

    private int order;

    public int getOrder() {
        return order;
    }
}
