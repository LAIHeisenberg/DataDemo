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

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;

public class DbSafeMBeansRegistry {

  private final Collection<ObjectName> mBeans = new ArrayList<ObjectName>();

  public static final String PACKAGE_NAME = "com.dbsafe";
  
  public void registerMBeans(Collection<DbSafeLoadableOptions> allOptions) throws MBeanRegistrationException, InstanceNotFoundException, MalformedObjectNameException, NotCompliantMBeanException {
    boolean jmx = true; 
    String jmxPrefix = "";
    
    for (DbSafeLoadableOptions options : allOptions) {
      if (options instanceof DbSafeImplOptions) {
        jmx = ((DbSafeImplOptions) options).getJmx();
        jmxPrefix = ((DbSafeImplOptions) options).getJmxPrefix();
        break;
      }
    }
       
    if (!jmx) {
      return;
    }
    
    // unreg possible conflicting ones first
    unregisterAllMBeans(jmxPrefix);
    
    // reg all
    for (DbSafeLoadableOptions options : allOptions) {
      try {
        registerMBean(options, jmxPrefix);
      } catch (InstanceAlreadyExistsException e) {
        // sounds like someone registered beans already (before we had a chance to do so)
        // so let's just make things consistent and re-register again
        registerMBeans(allOptions);
      }
    }
  }

  protected void registerMBean(DbSafeLoadableOptions mBean, String jmxPrefix) throws InstanceAlreadyExistsException,
      MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {

    checkMBean(mBean);

    final ObjectName mBeanObjectName = getObjectName(mBean, jmxPrefix);
    ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, mBeanObjectName);
    mBeans.add(mBeanObjectName);
  }

  public void unregisterAllMBeans(String jmxPrefix) throws MBeanRegistrationException, MalformedObjectNameException {

    // those we have reference to 
    final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    for (ObjectName mBeanObjectName : mBeans) {
      try {
        mbs.unregisterMBean(mBeanObjectName);
      } catch (InstanceNotFoundException e) {
        // this just means someone unregistered our beans already
        // but we're OK with that and it can't cause failure
      }
    }
    mBeans.clear();

    // to prevent naming conflicts: let's unreg also possible leftovers (with the same prefix)
    for (ObjectName objectName : mbs.queryNames(new ObjectName(getPackageName(jmxPrefix) + ":name=com.p6spy.*"), null)) {
      try {
        mbs.unregisterMBean(objectName);
      } catch (InstanceNotFoundException e) {
        // this just means someone unregistered the bean earlier than us
        // (quite unprobable, but parallel unreg could happen)
        // but we're OK with that and it can't cause failure
      }
      
    }
  }

  private void checkMBean(DbSafeLoadableOptions mBean) {
    if (null == mBean) {
      throw new IllegalArgumentException("mBean is null!");
    }

    if (!(mBean instanceof StandardMBean)) {
      throw new IllegalArgumentException(
          "mBean has to be instance of the StandardMBean class! But is not: " + mBean);
    }
  }

  protected ObjectName getObjectName(DbSafeLoadableOptions mBean, String jmxPrefix) throws MalformedObjectNameException {
    return new ObjectName(getPackageName(jmxPrefix) + ":name=" + mBean.getClass().getName());
  }
  
  protected static String getPackageName(String jmxPrefix) {
    return PACKAGE_NAME +  (null == jmxPrefix || jmxPrefix.isEmpty() ? "" : "." + jmxPrefix);
  }
  
}
