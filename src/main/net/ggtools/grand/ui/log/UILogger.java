// $Id$
/*
 * ====================================================================
 * Copyright (c) 2002-2004, Christophe Labouisse All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ggtools.grand.ui.log;

import org.apache.commons.logging.Log;

/**
 * @author Christophe Labouisse
 */
final class UILogger implements Log {

    private final Log underlying;

    private final String name;

    private LogEventBufferImpl logBuffer;

    /**
     * 
     */
    UILogger(final String name, final Log logger) {
        this.name = name;
        underlying = logger;
        logBuffer = LogEventBufferImpl.getInstance();
    }

    /**
     * @param message
     */
    public void debug(final Object message) {
        underlying.debug(message);
        logBuffer.addLogEvent(LogEvent.DEBUG, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void debug(final Object message, final Throwable t) {
        underlying.debug(message, t);
        logBuffer.addLogEvent(LogEvent.DEBUG, name, message, t);
    }

    /**
     * @param message
     */
    public void error(final Object message) {
        underlying.error(message);
        logBuffer.addLogEvent(LogEvent.ERROR, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void error(final Object message, final Throwable t) {
        underlying.error(message, t);
        logBuffer.addLogEvent(LogEvent.ERROR, name, message, t);
    }

    /**
     * @param message
     */
    public void fatal(final Object message) {
        underlying.fatal(message);
        logBuffer.addLogEvent(LogEvent.FATAL, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void fatal(final Object message, final Throwable t) {
        underlying.fatal(message, t);
        logBuffer.addLogEvent(LogEvent.FATAL, name, message, t);
    }

    /**
     * @param message
     */
    public void info(final Object message) {
        underlying.info(message);
        logBuffer.addLogEvent(LogEvent.INFO, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void info(final Object message, final Throwable t) {
        underlying.info(message, t);
        logBuffer.addLogEvent(LogEvent.INFO, name, message, t);
    }

    /**
     * @return
     */
    public boolean isDebugEnabled() {
        return underlying.isDebugEnabled();
    }

    /**
     * @return
     */
    public boolean isErrorEnabled() {
        return underlying.isErrorEnabled();
    }

    /**
     * @return
     */
    public boolean isFatalEnabled() {
        return underlying.isFatalEnabled();
    }

    /**
     * @return
     */
    public boolean isInfoEnabled() {
        return underlying.isInfoEnabled();
    }

    /**
     * @return
     */
    public boolean isTraceEnabled() {
        return underlying.isTraceEnabled();
    }

    /**
     * @return
     */
    public boolean isWarnEnabled() {
        return underlying.isWarnEnabled();
    }

    /**
     * @param message
     */
    public void trace(final Object message) {
        underlying.trace(message);
        logBuffer.addLogEvent(LogEvent.TRACE, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void trace(final Object message, final Throwable t) {
        underlying.trace(message, t);
        logBuffer.addLogEvent(LogEvent.TRACE, name, message, t);
    }

    /**
     * @param message
     */
    public void warn(final Object message) {
        underlying.warn(message);
        logBuffer.addLogEvent(LogEvent.WARNING, name, message);
    }

    /**
     * @param message
     * @param t
     */
    public void warn(final Object message, final Throwable t) {
        underlying.warn(message, t);
        logBuffer.addLogEvent(LogEvent.WARNING, name, message, t);
    }
}
