/**
 * Copyright 2023 Robert Scholte
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
package com.sourcegrounds.lesslogging.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import liquibase.Scope;
import liquibase.ui.LoggerUIService;

@Component
public class LiquibasePreInitializer implements BeanPostProcessor
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LiquibasePreInitializer.class );
    
    @Override
    public Object postProcessBeforeInitialization( final Object bean, String beanName )
        throws BeansException
    {
        if ( "liquibase".equals( beanName ) )
        {
            try
            {
                Scope.enter( Map.of( Scope.Attr.ui.name(), new LoggerUIService() ) );
            }
            catch ( Exception e )
            {
                LOGGER.debug( "Failed to change UIService for Liquibase", e );
            }
        }
        return bean;
    }
}
