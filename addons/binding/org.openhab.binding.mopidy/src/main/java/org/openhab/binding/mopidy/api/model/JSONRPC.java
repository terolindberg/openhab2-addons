package org.openhab.binding.mopidy.api.model;

import java.util.HashMap;

public class JSONRPC {
    /*
     * curl -X POST -H Content-Type:application/json -d '{
     * "method": "core.get_version",
     * "jsonrpc": "2.0",
     * "params": {},
     * "id": 1
     * }' http://localhost:6680/mopidy/rpc
     */
    String method;
    HashMap<String, Object> params;
    String jsonrpc = "2.0";
    String id = "1";

    public JSONRPC(String cmd, HashMap<String, Object> params) {
        this.method = cmd;
        this.id = MopidyRPC.getID();
        if (params != null) {
            this.params = params;
        }
    }
}

class MopidyRPC {
    public static int apiCallID = 0;

    public static String getID() {
        MopidyRPC.apiCallID++;
        return ((Number) MopidyRPC.apiCallID).toString();
    }
}