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
package com.longmai.dbsafe.engine.outage;


import com.longmai.dbsafe.engine.dbsafe.option.DbSafeOptionsRepository;
import com.longmai.dbsafe.engine.dbsafe.DbSafeModuleManager;

import javax.management.StandardMBean;
import java.util.HashMap;
import java.util.Map;

public class DbSafeOutageOptions extends StandardMBean implements DbSafeOutageLoadableOptions {

  public static final String OUTAGEDETECTIONINTERVAL = "outagedetectioninterval";
  public static final String OUTAGEDETECTION = "outagedetection";

  protected static final Map<String, String> defaults;

  static {
    defaults = new HashMap<String, String>();
    defaults.put(OUTAGEDETECTION, Boolean.toString(false));
    defaults.put(OUTAGEDETECTIONINTERVAL, Long.toString(30));
  }

  private final DbSafeOptionsRepository optionsRepository;

  public DbSafeOutageOptions(final DbSafeOptionsRepository optionsRepository) {
    super(DbSafeOutageOptionsMBean.class, false);
    this.optionsRepository = optionsRepository;
  }

  @Override
  public Map<String, String> getDefaults() {
    return defaults;
  }

  @Override
  public void load(Map<String, String> options) {
    setOutageDetection(options.get(OUTAGEDETECTION));
    setOutageDetectionInterval(options.get(OUTAGEDETECTIONINTERVAL));
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link DbSafeOutageLoadableOptions}
   */
  public static DbSafeOutageLoadableOptions getActiveInstance() {
    return DbSafeModuleManager.getInstance().getOptions(DbSafeOutageOptions.class);
  }

  // JMX exposed API

  @Override
  public boolean getOutageDetection() {
    return optionsRepository.get(Boolean.class, OUTAGEDETECTION);
  }

  @Override
  public void setOutageDetection(String outagedetection) {
    optionsRepository.set(Boolean.class, OUTAGEDETECTION, outagedetection);
  }

  @Override
  public void setOutageDetection(boolean outagedetection) {
    optionsRepository.set(Boolean.class, OUTAGEDETECTION, outagedetection);
  }

  @Override
  public long getOutageDetectionInterval() {
    return optionsRepository.get(Long.class, OUTAGEDETECTIONINTERVAL);
  }

  @Override
  public long getOutageDetectionIntervalMS() {
    // TODO should we move it to setter?
    // but we would then need to intorduce safety check there
    // not sure about that
    return getOutageDetectionInterval() * 1000L;
  }

  @Override
  public void setOutageDetectionInterval(String outagedetectioninterval) {
    optionsRepository.set(Long.class, OUTAGEDETECTIONINTERVAL, outagedetectioninterval);
  }

  @Override
  public void setOutageDetectionInterval(long outagedetectioninterval) {
    optionsRepository.set(Long.class, OUTAGEDETECTIONINTERVAL, outagedetectioninterval);
  }

}
