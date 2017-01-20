/// <reference path="./java-client-socket.d.ts" />

export class SocketClient {
    private socket: any;
    constructor(private address: string, private port: number) {
        this.socket = new com.tcpsocket.client.SocketClient(address, port);
    }

    public connect() {
        this.socket.connect();
    }
    public disconnect() {
        this.socket.disconect();
    }

    public write(message: string) {
        this.socket.write(message);
    }

    public on(event: string, callback) {
        this.socket.on(event, new com.tcpsocket.client.Emitter.Listener({
            call: function (args) {
                let payload = Array.prototype.slice.call(args);
                callback(args);
            }
        }));
    }
}
