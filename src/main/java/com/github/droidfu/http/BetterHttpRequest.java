/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.droidfu.http;

import java.net.ConnectException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.http.client.methods.HttpUriRequest;

public interface BetterHttpRequest {

    public HttpUriRequest unwrap();

    public String getRequestUrl();

    public BetterHttpRequest expecting(Integer... statusCodes);

    public BetterHttpRequest signed(OAuthConsumer oauthConsumer) throws OAuthException;

    public BetterHttpRequest retry(int retries);

    public BetterHttpResponse send() throws ConnectException;
}
