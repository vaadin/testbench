/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
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
package org.rapidpm.dependencies.core.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rapidpm.dependencies.core.logger.factory.LoggerFactory;

/**
 * <p>Abstract LoggerFactorySupport class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class LoggerFactorySupport implements LoggerFactory {

    final ConcurrentMap<String, LoggingService> mapLoggers = new ConcurrentHashMap<>(100);

    final ConstructorFunction<String, LoggingService> loggerConstructor = this::createLogger;

    /** {@inheritDoc} */
    @Override
    public final LoggingService getLogger(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(mapLoggers, name, loggerConstructor);
    }

    /**
     * <p>createLogger.</p>
     *
     * @param name a {@link String} object.
     * @return a {@link org.rapidpm.dependencies.core.logger.LoggingService} object.
     */
    protected abstract LoggingService createLogger(String name);


}
