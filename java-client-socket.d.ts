declare module com {
    export module tcpsocket {
        export module client {
            export class SocketClient {
                constructor(address: string, port: number);
                connect(): void;
                disconnect(): void;
                write(message: string): void;
            }
            export module Emitter {
                export class Listener {
                    constructor(call: { call: (args: any) => void })
                }
            }
        }
    }
}
