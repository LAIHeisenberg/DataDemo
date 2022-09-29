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
package com.longmai.dbsafe.engine.core.option;


import com.longmai.dbsafe.engine.core.DbSafeModuleManager;
import com.longmai.dbsafe.engine.core.DbSafeImplLoadableOptions;
import com.longmai.dbsafe.engine.core.DbSafeImplOptions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DbSafeDotPropertiesReloader implements DbSafeOptionChangedListener {

  private ScheduledExecutorService reloader;
  private final DbSafeDotProperties dbSafeDotProperties;
  private volatile boolean killed = false;

  public DbSafeDotPropertiesReloader(DbSafeDotProperties dbSafeDotProperties,
                                     DbSafeModuleManager dbSafeModuleManager) {
    this.dbSafeDotProperties = dbSafeDotProperties;
    
    final DbSafeImplLoadableOptions spyOptions = dbSafeModuleManager.getOptions(DbSafeImplOptions.class);
    reschedule(spyOptions.getReloadProperties(), spyOptions.getReloadPropertiesInterval());
    
    dbSafeModuleManager.registerOptionChangedListener(this);
  }

  public synchronized void reschedule(final boolean enabled, final long reloadInterval) {
    shutdownNow();
    
    if (!enabled || killed) {
      return;
    } 

    reloader = Executors.newSingleThreadScheduledExecutor();
    final Runnable reader = new Runnable() {
      @Override
      public void run() {
        if (dbSafeDotProperties.isModified()) {
          // correctly stop the old reloader first
          shutdownNow();
  
          DbSafeModuleManager.getInstance().reload();
        }
      }
    };

    reloader.scheduleAtFixedRate(reader, reloadInterval, reloadInterval, TimeUnit.SECONDS);
    
    // seems someone killed in the meantime
    if (killed) {
      shutdownNow();
    }
  }
  
  public void kill(DbSafeModuleManager dbSafeModuleManager) {
    dbSafeModuleManager.unregisterOptionChangedListener(this);
    killed = true;
    shutdownNow();
  }
  
  private void shutdownNow() {
    if (wasEnabled()) {
      reloader.shutdownNow();
      reloader = null;
    }  
  }
  
  private boolean wasEnabled() {
    return reloader != null;
  }

  @Override
  public void optionChanged(String key, Object oldValue, Object newValue) {
    if (key.equals(DbSafeImplOptions.RELOADPROPERTIES)) {
      reschedule(Boolean.valueOf(newValue.toString()), DbSafeImplOptions.getActiveInstance().getReloadPropertiesInterval());
    } else if (key.equals(DbSafeImplOptions.RELOADPROPERTIESINTERVAL)) {
      reschedule(DbSafeImplOptions.getActiveInstance().getReloadProperties(), (Long) newValue);
    }
  }

}
