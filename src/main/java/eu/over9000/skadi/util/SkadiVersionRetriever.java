/*******************************************************************************
 * Copyright (c) 2014 Jan Strauß
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import eu.over9000.skadi.logging.SkadiLogging;

public class SkadiVersionRetriever {
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getAPIResponse(final String api_url) throws URISyntaxException, IOException {
		final URI URL = new URI(api_url);
		final HttpResponse response = SkadiVersionRetriever.httpClient.execute(new HttpGet(URL));
		
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
		
	}
	
	public static String getLatestVersion() {
		
		try {
			final String response = SkadiVersionRetriever
			        .getAPIResponse("https://api.github.com/repos/s1mpl3x/skadi/releases");
			
			final JsonArray tagsArray = SkadiVersionRetriever.parser.parse(response).getAsJsonArray();
			final String name = tagsArray.get(0).getAsJsonObject().get("tag_name").getAsString();
			
			return name;
		} catch (URISyntaxException | IOException e) {
			SkadiLogging.log(e);
		}
		return "";
	}
}
