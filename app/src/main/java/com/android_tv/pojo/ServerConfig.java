package com.android_tv.pojo;

import java.util.HashMap;

/**
 * Purpose - ServerConfig is an server configuration file that helps to get all servers
 */
public class ServerConfig {
    private HashMap<String, String> serverConfigs = new HashMap<>();

    public ServerConfig() {
        serverConfigs.put("pmgb", "https://us.mobilepricecards.com/");
        serverConfigs.put("prateek", "http://mpcqa.mobilepricecards.com/");

        serverConfigs.put("7qg6", "https://orange.mobilepricecards.com/");
        serverConfigs.put("f8t0", "https://vzw.mobilepricecards.com/");
        serverConfigs.put("evau", "https://go.mobilepricecards.com/");
        serverConfigs.put("yi60", "https://victra.mobilepricecards.com/");
        serverConfigs.put("enlb", "https://nz.mobilepricecards.com/");
        serverConfigs.put("t89f", "https://digicel.mobilepricecards.com/");
        serverConfigs.put("q7cb", "https://ee.mobilepricecards.com/");
        serverConfigs.put("nkjt", "https://optus.mobilepricecards.com/");
        serverConfigs.put("a9xu", "https://optus.mobilepricecards.com/");
        serverConfigs.put("djr9", "http://mpcqa1.mobilepricecards.com/");
        serverConfigs.put("almq", "http://mpcqa2.mobilepricecards.com/");
        serverConfigs.put("z6ca", "http://mpcqa3.mobilepricecards.com/");
        serverConfigs.put("s7rr", "https://demo.mobilepricecards.com/");
        serverConfigs.put("sdpn", "http://od.mobilepricecards.com/");
        serverConfigs.put("7mpv", "http://od1.mobilepricecards.com/");
        serverConfigs.put("0t77", "http://od2.mobilepricecards.com/");
        serverConfigs.put("0t78", "http://od3.mobilepricecards.com/");


    }


    public HashMap<String, String> getServerConfig() {
        return serverConfigs;
    }


}
