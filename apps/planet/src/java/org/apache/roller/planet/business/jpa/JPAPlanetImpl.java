/*
* Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/

package org.apache.roller.planet.business.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.planet.PlanetException;
import org.apache.roller.planet.business.URLStrategy;
import org.apache.roller.planet.business.Planet;
import org.apache.roller.planet.business.PlanetManager;
import org.apache.roller.planet.business.FeedFetcher;
import org.apache.roller.planet.business.PropertiesManager;
import org.apache.roller.planet.config.PlanetConfig;


/**
 * Implements Planet, the entry point interface for the Roller-Planet business 
 * tier APIs using the Java Persistence API (JPA).
 */
@com.google.inject.Singleton
public class JPAPlanetImpl implements Planet {   
    
    private static Log log = LogFactory.getLog(JPAPlanetImpl.class);
    
    // a persistence utility class
    protected JPAPersistenceStrategy strategy = null;
    
    // our singleton instance
    protected static JPAPlanetImpl me = null;
        
    // references to the managers we maintain
    private PlanetManager planetManager = null;
    private PropertiesManager propertiesManager = null;
    
    // url strategy
    protected URLStrategy urlStrategy = null;
    
    // feed fetcher
    protected FeedFetcher feedFetcher = null;
    
        
    @com.google.inject.Inject  
    protected JPAPlanetImpl(
            JPAPersistenceStrategy strategy, 
            PlanetManager     planetManager, 
            PropertiesManager propertiesManager) throws PlanetException {
        
        this.strategy = strategy;
        this.propertiesManager = propertiesManager;
        this.planetManager = planetManager;
        
        try {
            String feedFetchClass = PlanetConfig.getProperty("feedfetcher.classname");
            if(feedFetchClass == null || feedFetchClass.trim().length() < 1) {
                throw new PlanetException("No FeedFetcher configured!!!");
            }
            
            Class fetchClass = Class.forName(feedFetchClass);
            FeedFetcher feedFetcher = (FeedFetcher) fetchClass.newInstance();
            
            // plug it in
            setFeedFetcher(feedFetcher); 
            
        } catch (Exception e) {
            throw new PlanetException("Error initializing feed fetcher", e);
        }
        
    }
    

    public URLStrategy getURLStrategy() {
        return this.urlStrategy;
    }
    
    public void setURLStrategy(URLStrategy urlStrategy) {
        this.urlStrategy = urlStrategy;
        log.info("Using URLStrategy: " + urlStrategy.getClass().getName());
    }
    
        public void flush() throws PlanetException {
        this.strategy.flush();
    }

    
    public void release() {
        this.strategy.release();
    }

    
    public void shutdown() {
        this.release();
    }
    
    /**
     * @see org.apache.roller.business.Roller#getBookmarkManager()
     */
    public PlanetManager getPlanetManager() {
        return planetManager;
    }

    protected PlanetManager createPlanetManager(
            JPAPersistenceStrategy strategy) {
        return new JPAPlanetManagerImpl(strategy);
    }    
    
    /**
     * @see org.apache.roller.business.Roller#getBookmarkManager()
     */
    public PropertiesManager getPropertiesManager() {
        return propertiesManager;
    }
    
    
    public FeedFetcher getFeedFetcher() {
        return this.feedFetcher;
    }
    
    public void setFeedFetcher(FeedFetcher feedFetcher) {
        this.feedFetcher = feedFetcher;
        log.info("Using FeedFetcher: " + feedFetcher.getClass().getName());
    }
    
}
