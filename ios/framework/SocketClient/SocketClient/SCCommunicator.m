//
//  communicator.m
//  socket-client
//
//  Created by toma on 1/26/17.
//  Copyright © 2017 toma. All rights reserved.
//

#import "SCCommunicator.h"
#import "SCConstants.h"
#import "Emitter.h"

CFReadStreamRef readStream;
CFWriteStreamRef writeStream;

NSInputStream *inputStream;
NSOutputStream *outputStream;

int const kSCBufferLength = 1024 * 4;

@implementation SCCommunicator

- (id)initWhitHost:(NSString*) host andPort:(int) port{
    if (self == [super init]) {
        _host = host;
        _port = port;
    }
    
    return self;
}

- (void)open {
    NSURL *url = [NSURL URLWithString:_host];
    
    NSLog(@"Setting up connection to %@ : %i", [url absoluteString], _port);
    
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef)[url host], _port, &readStream, &writeStream);
    
    if(!CFWriteStreamOpen(writeStream)) {
        [self emit:ERROR, @"Error, writeStream not open"];
    }
    
    inputStream = (__bridge NSInputStream *)readStream;
    outputStream = (__bridge NSOutputStream *)writeStream;
 
    [inputStream setDelegate:self];
    [outputStream setDelegate:self];
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^ {
        [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        [inputStream open];
        [[NSRunLoop currentRunLoop] run];
    });
    
    dispatch_async(queue, ^ {
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        [outputStream open];
        [[NSRunLoop currentRunLoop] run];
    });
}

- (void)close {
    [inputStream close];
    [outputStream close];
    
    [inputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    [inputStream setDelegate:nil];
    [outputStream setDelegate:nil];
    
    inputStream = nil;
    outputStream = nil;
    
    _stop = YES;
}

- (void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)event {
    NSLog(@"Stream triggered.");
    
    switch(event) {
        case NSStreamEventHasSpaceAvailable: {
            if(stream == outputStream) {
                [self emit:CONNECT];
                NSLog(@"outputStream is ready.");
            }
            break;
        }
        case NSStreamEventHasBytesAvailable: {
            if(stream == inputStream) {
                NSLog(@"inputStream is ready.");
                uint8_t buffer[kSCBufferLength];
                long len = [inputStream read:buffer maxLength:kSCBufferLength];;
                NSMutableString *message = [[NSMutableString alloc] init];
                while(len > 0 && !_stop) {
                    NSString *line = [[NSString alloc] initWithBytes: buffer length:len encoding:NSASCIIStringEncoding];
                    if(![line hasSuffix:@"\r\n"]) {
                         [message appendString:line];
                    } else {
                        [message appendString:line];
                        [self emit:DATA, message];
                    }
                   
                    len = [inputStream read:buffer maxLength:kSCBufferLength];
                   
                }
            }
            break;
        }
        default: {
            NSLog(@"Stream is sending an Event: %lu", event);
            break;
        }
    }
}

- (void)write:(NSString *)message {
    uint8_t *buf = (uint8_t *)[message UTF8String];
    if (!_stop) {
        [outputStream write:buf maxLength:strlen((char *)buf)];
    }
    
}

@end
