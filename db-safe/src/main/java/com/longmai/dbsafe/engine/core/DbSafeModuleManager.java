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
package com.longmai.dbsafe.engine.core;

import com.longmai.dbsafe.engine.common.DbSafeUtil;
import com.longmai.dbsafe.engine.core.option.*;

import javax.management.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DbSafeModuleManager {

  // recreated on each reload
  private final DbSafeOptionsSource[] optionsSources = new DbSafeOptionsSource[] {
      new DbSafeDotProperties(), new EnvironmentVariables(), new SystemProperties() };
  private final Map<Class<? extends DbSafeLoadableOptions>, DbSafeLoadableOptions> allOptions = new HashMap<Class<? extends DbSafeLoadableOptions>, DbSafeLoadableOptions>();
  private final List<DbSafeFactory> factories = new CopyOnWriteArrayList<DbSafeFactory>();
  private final DbSafeMBeansRegistry mBeansRegistry = new DbSafeMBeansRegistry();

  private final DbSafeOptionsRepository optionsRepository = new DbSafeOptionsRepository();

  // current instance (not a singleton, hence no final modifier)
  private static DbSafeModuleManager instance;

  static {
    initMe();
  }

  private static synchronized void initMe() {
    try {
      cleanUp();
      
      instance = new DbSafeModuleManager();
//      DbSafeLogQuery.initialize();

    } catch (IOException e) {
      handleInitEx(e);
    } catch (MBeanRegistrationException e) {
      handleInitEx(e);
    } catch (InstanceNotFoundException e) {
      handleInitEx(e);
    } catch (MalformedObjectNameException e) {
      handleInitEx(e);
    } catch (NotCompliantMBeanException e) {
      handleInitEx(e);
    }
  }

  private static void cleanUp() throws MBeanRegistrationException, InstanceNotFoundException,
      MalformedObjectNameException {
    if (instance == null) {
      return;
    }

    for (DbSafeOptionsSource optionsSource : instance.optionsSources) {
      optionsSource.preDestroy(instance);
    }

    if (DbSafeSpyOptions.getActiveInstance().getJmx() //
      // unregister mbeans (to prevent naming conflicts)
      && instance.mBeansRegistry != null) {
        instance.mBeansRegistry.unregisterAllMBeans(DbSafeSpyOptions.getActiveInstance().getJmxPrefix());
    }
    
    // clean table plz (we need to make sure that all the configured factories will be re-loaded)
    new DefaultJdbcEventListenerFactory().clearCache();
  }

  /**
   * Used on the class load only (only once!)
   * 
   * @throws NotCompliantMBeanException
   * @throws MBeanRegistrationException
   * @throws InstanceAlreadyExistsException
   * @throws MalformedObjectNameException
   * @throws InstanceNotFoundException 
   */
  private DbSafeModuleManager() throws IOException, MBeanRegistrationException, NotCompliantMBeanException,
                           MalformedObjectNameException, InstanceNotFoundException {
    debug(this.getClass().getName() + " re/initiating modules started");

    // make sure the proper listener registration happens

    // hard coded - core module init - as it holds initial config
    final DbSafeSpyLoadableOptions spyOptions = (DbSafeSpyLoadableOptions) registerModule(new DbSafeImlFactory());
    loadDriversExplicitly(spyOptions);

    // configured modules init
    final Set<DbSafeFactory> moduleFactories = spyOptions.getModuleFactories();
    if (null != moduleFactories) {
	    for (DbSafeFactory factory : spyOptions.getModuleFactories()) {
	    	registerModule(factory);
	    }
  	}
  
    optionsRepository.initCompleted();
    
    mBeansRegistry.registerMBeans(allOptions.values());
    
    for (DbSafeOptionsSource optionsSource : optionsSources) {
      optionsSource.postInit(this);
    }

    debug(this.getClass().getName() + " re/initiating modules done");
  }
  
  

  protected synchronized DbSafeLoadableOptions registerModule(DbSafeFactory factory) /*throws InstanceAlreadyExistsException,
  	MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException*/ {
    
	  // re-register is not supported - skip silently
	  for (DbSafeFactory registeredFactory : factories) {
		  if (registeredFactory.getClass().equals(factory.getClass())) {
			  return null;
		  }
	  }
		  
	  final DbSafeLoadableOptions options = factory.getOptions(optionsRepository);
      loadOptions(options);

      factories.add(factory);
      
      debug("Registered factory: " + factory.getClass().getName() + " with options: " + options.getClass().getName());
      
      return options;
  }
  
  /**
   * Returns loaded options. These are loaded in the right order:
   * <ul>
   * <li>default values</li>
   * <li>based on the order defined in the {@link #optionsSources}</li>
   * </ul>
   * 
   * @param options
   * @return
   */
	private void loadOptions(final DbSafeLoadableOptions options) {
		// make sure to load defaults first
		options.load(options.getDefaults());

		// load the rest in the right order then
		for (DbSafeOptionsSource optionsSource : optionsSources) {
			Map<String, String> toLoad = optionsSource.getOptions();
			if (null != toLoad) {
				options.load(toLoad);
			}
		}

		// register to all the props then
		allOptions.put(options.getClass(), options);
	}

  public static DbSafeModuleManager getInstance() {
    return instance;
  }

  private static void handleInitEx(Exception e) {
    e.printStackTrace(System.err);
  }

	private void loadDriversExplicitly(DbSafeSpyLoadableOptions spyOptions) {
		final Collection<String> driverNames = spyOptions.getDriverNames();
		if (null != driverNames) {
			for (String driverName : driverNames) {
				try {
					// you really only need to load the driver if it is not a
					// type 4 driver!
					DbSafeUtil.forName(driverName).newInstance();
				} catch (Exception e) {
					String err = "Error registering driver names: "
							+ driverNames + " \nCaused By: " + e.toString();
//					DbSafeLogQuery.error(err);
					throw new DbSafeDriverNotFoundError(err);
				}
			}
		}
	}

  private void debug(String msg) {
    // not initialized yet => nowhere to log yet
    if (instance == null || factories.isEmpty()) {
      return;
    }

  }
  
  //
  // API methods
  //

  /**
   * @param optionsClass
   *          the class to get the options for.
   * @return the options instance depending on it's class.
   */
  @SuppressWarnings("unchecked")
  public <T extends DbSafeLoadableOptions> T getOptions(Class<T> optionsClass) {
    return (T) allOptions.get(optionsClass);
  }

  /**
   * Reloads the {@link DbSafeModuleManager}. <br>
   * <br>
   * The idea is that whoever initiates this one causes it to start with the clean table. No
   * previously set values are kept (even those set manually - via jmx will be forgotten).
   */
  public void reload() {
    initMe();
  }

  public List<DbSafeFactory> getFactories() {
    return factories;
  }

  public void registerOptionChangedListener(DbSafeOptionChangedListener listener) {
    optionsRepository.registerOptionChangedListener(listener);
  }

  public void unregisterOptionChangedListener(DbSafeOptionChangedListener listener) {
    optionsRepository.unregisterOptionChangedListener(listener);
  }
	
}
