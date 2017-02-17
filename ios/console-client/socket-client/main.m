//
//  main.m
//  socket-client
//
//  Created by toma on 1/26/17.
//  Copyright © 2017 toma. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "communicator.h"
#import "Emitter.h"

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        
        Communicator *c = [[Communicator alloc] init];
        c->host = @"http://192.168.1.202";
        c->port = 8080;

        [c setup];
        [c open];
        [c writeOut:@"{\"handshake-key\":\"66ervca04kxz4mgja5d4jme59a8\"}"];
        [c writeOut:@"test222"];
        //[c close];
                
        [c on:@"key" listener:^(BOOL param1, NSString *param) {
            NSLog(param);
        }];
        
        while (1);
    }
    return 0;
}
