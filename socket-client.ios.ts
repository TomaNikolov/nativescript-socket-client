/// <reference path="./ios-client-socket.d.ts" />
export class SocketClient {
    private socket: any;
    constructor(private address: string, private port: number) {
        this.socket = SCCommunicator.alloc().initWhitHostAndPort(address, port);
    }

    public connect() {
        this.socket.open();
    }
    public disconnect() {
        this.socket.close();
    }

    public write(message: string) {
        this.socket.write(message);
    }

    public on(event: string, callback) {
        this.socket.on(event, (args) => {
                let payload = Array.prototype.slice.call(args);
                callback(args);
        });
    }
}