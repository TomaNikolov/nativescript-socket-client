//
//  communicator.h
//  socket-client
//
//  Created by toma on 1/26/17.
//  Copyright © 2017 toma. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCCommunicator : NSObject <NSStreamDelegate> {
@private
    
    NSString *_host;
    int _port;
    BOOL _stop;
}

- (id)initWhitHost:(NSString*) host andPort:(int) port;
- (void)open;
- (void)close;
- (void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)event;
- (void)write:(NSString *)message;

@end
