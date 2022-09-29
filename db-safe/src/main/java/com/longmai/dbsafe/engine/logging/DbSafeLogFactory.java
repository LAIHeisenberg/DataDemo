/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.longmai.dbsafe.engine.logging;


import com.longmai.dbsafe.engine.event.JdbcEventListener;
import com.longmai.dbsafe.engine.dbsafe.option.DbSafeOptionsRepository;
import com.longmai.dbsafe.engine.dbsafe.DbSafeFactory;
import com.longmai.dbsafe.engine.dbsafe.DbSafeLoadableOptions;

import java.util.Iterator;
import java.util.ServiceLoader;

public class DbSafeLogFactory implements DbSafeFactory {

  private static ServiceLoader<LoggingEventListener> customLoggingEventListener = ServiceLoader
    .load(LoggingEventListener.class, DbSafeLogFactory.class.getClassLoader());

  @Override
  public JdbcEventListener getJdbcEventListener() {
    // return first custom implementation
    Iterator<LoggingEventListener> iterator = customLoggingEventListener.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    // if none found, return default impl
    return LoggingEventListener.INSTANCE;
  }

  @Override
  public DbSafeLoadableOptions getOptions(DbSafeOptionsRepository optionsRepository) {
    return new DbSafeLogOptions(optionsRepository);
  }

}
