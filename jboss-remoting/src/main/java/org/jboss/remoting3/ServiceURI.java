/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.remoting3;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A parser for JBoss Remoting URI types.
 *
 * @apiviz.exclude
 */
public final class ServiceURI {
    public static final String SCHEME = "jrs";

    private ServiceURI() {
    }

    /**
     * Determine if this URI is a valid Remoting service URI.
     *
     * @param uri the URI
     * @return {@code true} if the given URI is a valid Remoting service URI
     */
    public static boolean isRemotingServiceUri(final URI uri) {
        return SCHEME.equals(uri.getScheme()) && uri.isOpaque();
    }

    /**
     * Get the service type from a Remoting service URI.
     *
     * @param uri the URI
     * @return the service type
     * @throws IllegalArgumentException if the given URI is not for a remoting service
     */
    public static String getServiceType(final URI uri) throws IllegalArgumentException {
        if (! isRemotingServiceUri(uri)) {
            throw new IllegalArgumentException("Not a valid remoting service URI");
        }
        final String ssp = uri.getSchemeSpecificPart();
        final int firstColon = ssp.indexOf(':');
        final String serviceType;
        if (firstColon == -1) {
            serviceType = ssp;
        } else {
            serviceType = ssp.substring(0, firstColon);
        }
        return serviceType;
    }

    /**
     * Get the group name from a Remoting service URI.
     *
     * @param uri the URI
     * @return the group name
     * @throws IllegalArgumentException if the given URI is not for a remoting service
     */
    public static String getGroupName(final URI uri) throws IllegalArgumentException {
        if (! isRemotingServiceUri(uri)) {
            throw new IllegalArgumentException("Not a valid remoting service URI");
        }
        final String ssp = uri.getSchemeSpecificPart();
        final int firstColon = ssp.indexOf(':');
        final String groupName;
        if (firstColon == -1) {
            return "";
        }
        final int secondColon = ssp.indexOf(':', firstColon + 1);
        if (secondColon == -1) {
            groupName = ssp.substring(firstColon + 1);
        } else {
            groupName = ssp.substring(firstColon + 1, secondColon);
        }
        return groupName;
    }

    /**
     * Get the endpoint name from a Remoting service URI.
     *
     * @param uri the URI
     * @return the endpoint name
     * @throws IllegalArgumentException if the given URI is not for a remoting service
     */
    public static String getEndpointName(final URI uri) throws IllegalArgumentException {
        if (! isRemotingServiceUri(uri)) {
            throw new IllegalArgumentException("Not a valid remoting service URI");
        }
        final String ssp = uri.getSchemeSpecificPart();
        final int firstColon = ssp.indexOf(':');
        final String endpointName;
        if (firstColon == -1) {
            return "";
        }
        final int secondColon = ssp.indexOf(':', firstColon + 1);
        if (secondColon == -1) {
            return "";
        }
        // ::: is not officially supported, but this leaves room for extensions
        final int thirdColon = ssp.indexOf(':', secondColon + 1);
        if (thirdColon == -1) {
            endpointName = ssp.substring(secondColon + 1);
        } else {
            endpointName = ssp.substring(secondColon + 1, thirdColon);
        }
        return endpointName;
    }

    /**
     * Create a Remoting service URI.
     *
     * @param serviceType the service type, if any
     * @param groupName the group name, if any
     * @param endpointName the endpoint name, if any
     * @return the URI
     */
    public static URI create(String serviceType, String groupName, String endpointName) {
        try {
            StringBuilder builder = new StringBuilder(serviceType.length() + groupName.length() + endpointName.length() + 2);
            if (serviceType != null && serviceType.length() > 0) {
                builder.append(serviceType);
            }
            builder.append(':');
            if (groupName != null && groupName.length() > 0) {
                builder.append(groupName);
            }
            builder.append(':');
            if (endpointName != null && endpointName.length() > 0) {
                builder.append(endpointName);
            }
            return new URI(SCHEME, builder.toString(), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("URI syntax exception should not be possible here", e);
        }
    }
}
