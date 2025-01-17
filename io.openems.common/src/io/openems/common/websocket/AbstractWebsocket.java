package io.openems.common.websocket;

import org.slf4j.Logger;

public abstract class AbstractWebsocket<T extends WsData> {

	private final String name;

	/**
	 * Creates an empty WsData object that is attached to the WebSocket as early as
	 * possible.
	 *
	 * @return the typed {@link WsData}
	 */
	protected abstract T createWsData();

	/**
	 * Callback for internal error.
	 *
	 * @return the {@link OnInternalError} callback
	 */
	protected abstract OnInternalError getOnInternalError();

	/**
	 * Callback for websocket OnOpen event.
	 *
	 * @return the {@link OnOpen} callback
	 */
	protected abstract OnOpen getOnOpen();

	/**
	 * Callback for JSON-RPC request.
	 *
	 * @return the {@link OnRequest} callback
	 */
	protected abstract OnRequest getOnRequest();

	/**
	 * Callback for JSON-RPC notification.
	 *
	 * @return the {@link OnNotification} callback
	 */
	protected abstract OnNotification getOnNotification();

	/**
	 * Callback for websocket error.
	 *
	 * @return the {@link OnError} callback
	 */
	protected abstract OnError getOnError();

	/**
	 * Callback for websocket OnClose event.
	 *
	 * @return the {@link OnClose} callback
	 */
	protected abstract OnClose getOnClose();

	/**
	 * Construct this {@link AbstractWebsocket}.
	 *
	 * @param name a name that is used to identify log messages
	 */
	public AbstractWebsocket(String name) {
		this.name = name;
	}

	/**
	 * Gets the internal name of this websocket client/server.
	 *
	 * @return the internal name
	 */
	public String getName() {
		return this.name;
	}

	protected void start() {

	}

	protected void stop() {
	}

	/**
	 * Execute a {@link Runnable}.
	 *
	 * @param command the {@link Runnable}
	 */
	protected abstract void execute(Runnable command);

	/**
	 * Handles an internal Error asynchronously.
	 * 
	 * @param e            the {@link Exception} to be handled
	 * @param wsDataString the toString() content of the WsData attachment of the
	 *                     WebSocket
	 */
	protected void handleInternalErrorAsync(Exception e, String wsDataString) {
		this.execute(new OnInternalErrorHandler(this.getOnInternalError(), e, wsDataString));
	}

	/**
	 * Handles an internal Error synchronously.
	 *
	 * @param e            the {@link Exception}to be handled
	 * @param wsDataString the toString() content of the WsData attachment of the
	 *                     WebSocket
	 */
	protected void handleInternalErrorSync(Exception e, String wsDataString) {
		this.getOnInternalError().run(e, wsDataString);
	}

	/**
	 * Log a info message.
	 *
	 * @param log     a Logger instance
	 * @param message the message
	 */
	protected abstract void logInfo(Logger log, String message);

	/**
	 * Log a warn message.
	 *
	 * @param log     a Logger instance
	 * @param message the message
	 */
	protected abstract void logWarn(Logger log, String message);

}
