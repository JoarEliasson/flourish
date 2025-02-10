package com.flourish.server.model;

import com.flourish.shared.Message;

/**
 * Interface makes sure all ResponseHandlers have at
 * least these methods
 */
public interface IResponseHandler {

    Message getResponse(Message request);
}

