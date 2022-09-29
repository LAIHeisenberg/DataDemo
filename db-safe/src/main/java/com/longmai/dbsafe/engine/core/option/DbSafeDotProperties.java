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


import com.longmai.dbsafe.engine.common.DbSafeUtil;
import com.longmai.dbsafe.engine.core.DbSafeModuleManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

public class DbSafeDotProperties implements DbSafeOptionsSource {

  public static final String OPTIONS_FILE_PROPERTY = "DbSafe.properties";
  public static final String OPTIONS_FILE_CHARSET_PROPERTY = OPTIONS_FILE_PROPERTY.concat(".charset");
  public static final String DEFAULT_OPTIONS_FILE = OPTIONS_FILE_PROPERTY;

  private final long lastModified;

  private DbSafeDotPropertiesReloader reloader;

  private final Map<String, String> options;

  /**
   * Creates a new instance and loads the properties file if found.
   *
   * @throws IOException
   */
  public DbSafeDotProperties() throws IOException {
    URL url = locate();

    if (null == url) {
      // no config file preset => skip props loading
      lastModified = -1;
      options = null;
      return;
    }

    lastModified = lastModified();

    InputStream in = null;
    try {
      in = url.openStream();
      final Properties properties = new Properties();

      String charsetName = System.getProperty(OPTIONS_FILE_CHARSET_PROPERTY, Charset.defaultCharset().name());
      properties.load(new InputStreamReader(in, charsetName));
      options = DbSafeUtil.getPropertiesMap(properties);
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch( Exception e ) {
        }
      }
    }
  }


  /**
   * Determines if the file has been modified since it was loaded
   *
   * @return true if modified, false otherwise
   */
  public boolean isModified() {
    return lastModified() != lastModified;
  }

  private long lastModified() {
    long lastMod = -1;
    URLConnection con = null;
    URL url = locate();
    if( url != null ) {
      try {
        con = url.openConnection();
        lastMod = con.getLastModified();
      } catch (IOException e) {
        // ignore
      } finally {
        if( con != null ) {
          // getLastModified opens an input stream if it is a file
          // the inputStream must be closed manually!
          InputStream in = null;
          try {
             in = con.getInputStream();
          } catch (IOException e) {
          }
          if( in != null ) {
            try {
              in.close();
            } catch (IOException e) {}
          }

        }
      }
    }
    return lastMod;
  }

  private URL locate() {
    String propsFileName = System.getProperty(OPTIONS_FILE_PROPERTY, DEFAULT_OPTIONS_FILE);
    if (null == propsFileName || propsFileName.isEmpty()) {
      propsFileName = DEFAULT_OPTIONS_FILE;
    }

    return DbSafeUtil.locateFile(propsFileName);
  }

  @Override
  public Map<String, String> getOptions() {
    return options;
  }


  @Override
  public void preDestroy(DbSafeModuleManager dbSafeModuleManager) {
    if (reloader != null) {
      reloader.kill(dbSafeModuleManager);
    }
  }

  @Override
  public void postInit(DbSafeModuleManager dbSafeModuleManager) {
    reloader = new DbSafeDotPropertiesReloader(this, dbSafeModuleManager);
  }
}
