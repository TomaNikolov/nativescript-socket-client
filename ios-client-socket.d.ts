declare class SCCommunicator {
    initWhitHostAndPort(address: string, port: number): SCCommunicator;
    static alloc(): SCCommunicator
    open(): void;
    close(): void;
    write(message: string): void;
}