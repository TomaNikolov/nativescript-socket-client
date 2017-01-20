declare module SocketClent {
    export class SocketClent {
        socket: any;
        constructor(address: string, port: number);
        on(event: string, callback: Function): void;
        connect(): void;
        write(message: string): void;
        disconnect(): void;
    }
}
