//
//  communicator.h
//  socket-client
//
//  Created by toma on 1/26/17.
//  Copyright © 2017 toma. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Communicator : NSObject <NSStreamDelegate> {
@public
    
    NSString *host;
    int port;
}

- (void)setup;
- (void)open;
- (void)close;
- (void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)event;
- (void)readIn:(NSString *)s;
- (void)writeOut:(NSString *)s;

@end
