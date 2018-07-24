package edu.uci.iotproject.tplinkplug;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        TplinkPlugWanClient client = new TplinkPlugWanClient();
        int c = 0;
        while (c < 15) {
            if (c % 2 == 0) {
                client.powerOn();
            }
            else {
                client.powerOff();
            }
            Thread.sleep(5_000);
            c++;
        }
    }

}


// To login, POST following JSON to https://wap.tplinkcloud.com
// The UUID is generated by the client - possibly used for tracking future logins from the same device?
// {
//     "method": "login",
//     "params": {
//     "appType": "Kasa_Android",
//     "cloudUserName": "iotuser22@gmail.com",
//     "cloudPassword": "Hqeas2tplink",
//     "terminalUUID": "7e8691de-cf4b-4727-ab31-863b4d4919b4"
//     }
// }
// Login output
// {"error_code":0,"result":{"accountId":"1619813","regTime":"2017-08-06 06:28:38","email":"iotuser22@gmail.com","token":"a749210e-A9F3yu9IMYGWAepK0KCVNp0"}}

// To get list of devices, POST following JSON to https://wap.tplinkcloud.com?token=TOKEN_FROM_LOGIN_RESPONSE_HERE
// {"method":"getDeviceList"}
// getDeviceList output (note that the appServerUrl points to the URL to send device control actions (on/off) to (in this case https://use1-wap.tplinkcloud.com)
// {"error_code":0,"result":{"deviceList":[{"fwVer":"1.4.3 Build 170504 Rel.144921","deviceName":"Smart Wi-Fi LED Bulb with Color Changing","status":0,"alias":"My_TPLink_LightBulb","deviceType":"IOT.SMARTBULB","appServerUrl":"https://use1-wap.tplinkcloud.com","deviceModel":"LB130(US)","deviceMac":"50C7BF59D584","role":0,"isSameRegion":true,"hwId":"111E35908497A05512E259BB76801E10","fwId":"00000000000000000000000000000000","oemId":"05BF7B3BE1675C5A6867B7A7E4C9F6F7","deviceId":"8012CE834562C3304F4FD28FBFBA86E4185B6843","deviceHwVer":"1.0"},{"fwVer":"1.2.5 Build 171206 Rel.085954","deviceName":"Wi-Fi Smart Plug With Energy Monitoring","status":1,"alias":"My Smart Plug","deviceType":"IOT.SMARTPLUGSWITCH","appServerUrl":"https://use1-wap.tplinkcloud.com","deviceModel":"HS110(US)","deviceMac":"50C7BF331F09","role":0,"isSameRegion":true,"hwId":"60FF6B258734EA6880E186F8C96DDC61","fwId":"00000000000000000000000000000000","oemId":"FFF22CFF774A0B89F7624BFC6F50D5DE","deviceId":"800617CC047187F5251E5B88567ACC6D1819FDCF","deviceHwVer":"1.0"}]}}




//    async set_relay_state(state){
//        return await super.tplink_request( {"system":{"set_relay_state":{"state": state }}} )
//    }

// deviceId 800617CC047187F5251E5B88567ACC6D1819FDCF or alias "My Smart Plug" ?
//    {
//        "method":"passthrough",
//        "params": {
//            "deviceId": "My Smart Plug",
//            "requestData": {"system":{"set_relay_state":{"state": 0 }}} // 0 for off, 1 for on
//        }
//    }

// curl --request POST "https://use1-wap.tplinkcloud.com/?token=a749210e-A9F3yu9IMYGWAepK0KCVNp0 HTTP/1.1" --data '{"method":"passthrough", "params": {"deviceId": "800617CC047187F5251E5B88567ACC6D1819FDCF", "requestData": "{\"system\":{\"set_relay_state\":{\"state\":1}}}" }}' --header "Content-Type: application/json"