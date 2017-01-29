//
//  communicator.m
//  socket-client
//
//  Created by toma on 1/26/17.
//  Copyright © 2017 toma. All rights reserved.
//

#import "Communicator.h"

CFReadStreamRef readStream;
CFWriteStreamRef writeStream;

NSInputStream *inputStream;
NSOutputStream *outputStream;

@implementation Communicator

- (void)setup {
    NSURL *url = [NSURL URLWithString:host];
    
    NSLog(@"Setting up connection to %@ : %i", [url absoluteString], port);
    
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef)[url host], port, &readStream, &writeStream);
    
    if(!CFWriteStreamOpen(writeStream)) {
        NSLog(@"Error, writeStream not open");
        
        return;
    }
    [self open];
    
    NSLog(@"Status of outputStream: %i", [outputStream streamStatus]);
    
    return;
}

- (void)open {
    NSLog(@"Opening streams.");
    
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
    NSLog(@"Closing streams.");
    
    [inputStream close];
    [outputStream close];
    
    [inputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    [inputStream setDelegate:nil];
    [outputStream setDelegate:nil];
    
    inputStream = nil;
    outputStream = nil;
}

- (void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)event {
    NSLog(@"Stream triggered.");
    
    switch(event) {
        case NSStreamEventHasSpaceAvailable: {
            if(stream == outputStream) {
                NSLog(@"outputStream is ready.");
            }
            break;
        }
        case NSStreamEventHasBytesAvailable: {
            if(stream == inputStream) {
                NSLog(@"inputStream is ready.");
                
                uint8_t buffer[1024];
                long len = [inputStream read:buffer maxLength:1024];;
                NSMutableString *message = [[NSMutableString alloc] init];
                while(len > 0) {
                    NSString *line = [[NSString alloc] initWithBytes: buffer length:len encoding:NSASCIIStringEncoding];
                    if(![line hasSuffix:@"\r\n"]) {
                         [message appendString:line];
                    } else {
                        [message appendString:line];
                        [self readIn:message];
                    }
                   
                    len = [inputStream read:buffer maxLength:1024];
                   
                }
            }
            break;
        }
        default: {
            NSLog(@"Stream is sending an Event: %i", event);
            
            break;
        }
    }
}

- (void)readIn:(NSString *)s {
    NSLog(@"Reading in the following:");
    NSLog(@"%@", s);
}

- (void)writeOut:(NSString *)s {
    uint8_t *buf = (uint8_t *)[s UTF8String];
    
    [outputStream write:buf maxLength:strlen((char *)buf)];
    
    NSLog(@"Writing out the following:");
    NSLog(@"%@", s);
}

@end
