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
package com.sourcegrounds.lesslogging;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Application.class );
    
    public static void main( String[] args )
    {
        if( LOGGER.isInfoEnabled() )
        {
            LOGGER.info( getMessage( args ) );
        }
    }
    
    static String getMessage( String... args )
    {
        return Stream.of( args ).collect( Collectors.joining( ", ", "Received the following arguments: ", "" ) );
    }
}