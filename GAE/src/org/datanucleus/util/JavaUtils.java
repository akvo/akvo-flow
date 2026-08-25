/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 


Contributors:
    ...
**********************************************************************/
package org.datanucleus.util;

import java.util.StringTokenizer;

/**
 * Utilities relating to the version of Java in use at runtime.
 */
/*
 * AKVO PATCH of datanucleus-core 3.1.3.
 *
 * This file deliberately shadows the copy inside datanucleus-core-3.1.3.jar: a servlet
 * container loads WEB-INF/classes ahead of WEB-INF/lib, so this version wins. DataNucleus
 * 3.1.3 is dead upstream and cannot be rebuilt, and without this fix the ORM does not
 * function at all on any JVM newer than 8. Delete this file if DataNucleus is ever replaced.
 */
public class JavaUtils
{
    private static boolean versionInitialised=false;
    private static int majorVersion=1;
    private static int minorVersion=0;
    private static int isJRE15=-1;
    private static int isJRE16=-1;

    /**
     * Accessor for whether the JRE is 1.5 (or above).
     * Checks for the presence of a known 1.5 class.
     * @return Whether the JRE is 1.5 or above
     */
    public static boolean isJRE1_5OrAbove()
    {
        if (isJRE15 == -1)
        {
            try
            {
                Class.forName("java.util.Queue");
                isJRE15 = 1;
            }
            catch (Exception e)
            {
                isJRE15 = 0;
            }
        }
        return isJRE15 == 1;
    }

    /**
     * Accessor for whether the JRE is 1.6 (or above).
     * Checks for the presence of a known 1.6 class.
     * @return Whether the JRE is 1.6 or above
     */
    public static boolean isJRE1_6OrAbove()
    {
        if (isJRE16 == -1)
        {
            try
            {
                Class.forName("java.util.Deque");
                isJRE16 = 1;
            }
            catch (Exception e)
            {
                isJRE16 = 0;
            }
        }
        return isJRE16 == 1;
    }

    /**
     * Accessor for the major version number of the JRE.
     * @return The major version number of the JRE
     */
    public static int getJREMajorVersion()
    {
        if (!versionInitialised)
        {
            initialiseJREVersion();
        }
        return majorVersion;
    }

    /**
     * Accessor for the minor version number of the JRE.
     * @return The minor version number of the JRE
     */
    public static int getJREMinorVersion()
    {
        if (!versionInitialised)
        {
            initialiseJREVersion();
        }
        return minorVersion;
    }

    /**
     * Utility to initialise the values of the JRE major/minor version.
     * Assumes the "java.version" string is in the form "XX.YY.ZZ".
     * Works for SUN JRE's.
     */
    private static void initialiseJREVersion()
    {
        String version = System.getProperty("java.version");

        // AKVO PATCH -- see the class comment above.
        //
        // Up to Java 8 the version string was "1.8.0_502", so major/minor parsed as 1/8 and
        // every store_mapping entry (all of which declare java-version "1.3") passed the
        // isGreaterEqualsThan check. Java 9 changed the scheme to "21.0.12" (JEP 223), which
        // parses as major/minor 21/0 -- and 0 >= 3 is false, so NOT ONE mapped type gets
        // registered. Every persistent field then falls through to InterfaceMapping and any
        // class with a List field fails to map.
        //
        // The declared versions in the plugin descriptors are all of the "1.x" form, so the
        // fix is to keep reporting modern Java N in those terms: major 1, minor N. That makes
        // isGreaterEqualsThan("1.3") true on Java 9+ and leaves isEqualsThan (used by the
        // java-version-restricted entries) correctly false.
        StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try
        {
            Integer ver = Integer.valueOf(token);
            int first = ver.intValue();
            if (first == 1)
            {
                // Legacy "1.N.x" scheme.
                majorVersion = first;
                token = tokeniser.nextToken();
                ver = Integer.valueOf(token);
                minorVersion = ver.intValue();
            }
            else
            {
                // Java 9+ "N.x.y" scheme, expressed as 1.N for comparison purposes.
                majorVersion = 1;
                minorVersion = first;
            }
        }
        catch (Exception e)
        {
            // Do nothing
        }
        versionInitialised = true;
    }
    
    /**
     * Check if the current version is greater or equals than the argument version.
     * @param version the version
     * @return true if the runtime version is greater equals than the argument
     */
    public static boolean isGreaterEqualsThan(String version)
    {
        boolean greaterEquals = false;
        StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try
        {
            Integer ver = Integer.valueOf(token);
            int majorVersion = ver.intValue();

            token = tokeniser.nextToken();
            ver = Integer.valueOf(token);
            int minorVersion = ver.intValue();
            if (getJREMajorVersion() >= majorVersion && getJREMinorVersion() >= minorVersion)
            {
                greaterEquals = true;
            }
        }
        catch (Exception e)
        {
            // Do nothing
        }

        return greaterEquals;
    }

    /**
     * Check if the current version is equals than the argument version.
     * @param version the version
     * @return true if the runtime version is equals than the argument
     */
    public static boolean isEqualsThan(String version)
    {
        boolean equals = false;
        StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try
        {
            Integer ver = Integer.valueOf(token);
            int majorVersion = ver.intValue();

            token = tokeniser.nextToken();
            ver = Integer.valueOf(token);
            int minorVersion = ver.intValue();
            if (getJREMajorVersion() == majorVersion && getJREMinorVersion() == minorVersion)
            {
                equals = true;
            }
        }
        catch (Exception e)
        {
            // Do nothing
        }

        return equals;
    }    
}