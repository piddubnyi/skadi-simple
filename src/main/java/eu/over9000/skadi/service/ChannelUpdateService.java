/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.service;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.ChannelDataRetriever;
import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.util.ExecutorUtil;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelUpdateService extends ScheduledService<ChannelMetadata> {

	private static final int UPDATE_INTERVAL = 60;
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelUpdateService.class);
	private final Channel toUpdate;

	public ChannelUpdateService(final Channel toUpdate) {
		setExecutor(ExecutorUtil.getExecutorService());
		this.toUpdate = toUpdate;
		setPeriod(Duration.seconds(UPDATE_INTERVAL));
		setRestartOnFailure(true);
		setOnSucceeded(event -> {

			final ChannelMetadata updated = (ChannelMetadata) event.getSource().getValue();
			if (updated != null) {

				synchronized (toUpdate) {
					toUpdate.updateFrom(updated);
				}
			}
		});
		setOnFailed(event -> LOGGER.error("scheduled channel updater failed for " + toUpdate.getName(), event.getSource().getException()));
	}

	@Override
	protected Task<ChannelMetadata> createTask() {
		return new Task<ChannelMetadata>() {

			@Override
			protected ChannelMetadata call() throws Exception {
				return ChannelDataRetriever.getChannelMetadata(toUpdate);
			}
		};
	}
}
