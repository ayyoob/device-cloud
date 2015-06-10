/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.device.mgt.iot.arduino.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.spi.DeviceMgtService;
import org.wso2.carbon.device.mgt.iot.common.DeviceTypeService;
import org.wso2.carbon.device.mgt.iot.arduino.impl.ArduinoManager;

///**
// * @scr.component name="org.wso2.carbon.device.mgt.iot.arduino.internal.ArduinoManagementServiceComponent"
// * immediate="true"
// * @scr.reference name="org.wso2.carbon.ndatasource"
// * interface="org.wso2.carbon.ndatasource.core.DataSourceService"
// * cardinality="1..1"
// * policy="dynamic"
// * bind="setDataSourceService"
// * unbind="unsetDataSourceService"
// */



//* @scr.reference name="org.wso2.carbon.ndatasource"
//		* interface="org.wso2.carbon.ndatasource.core.DataSourceService"
//		* cardinality="1..1"
//		* policy="dynamic"
//		* bind="setDataSourceService"
//		* unbind="unsetDataSourceService"

/**
 * @scr.component name="org.wso2.carbon.device.mgt.iot.arduino.internal.ArduinoManagementServiceComponent"
 * immediate="true"
 * @scr.reference name="wso2.carbon.device.mgt.iot.common.DeviceTypeService"
 * interface="org.wso2.carbon.device.mgt.iot.common.DeviceTypeService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setDeviceTypeService"
 * unbind="unsetDeviceTypeService"
 */
public class ArduinoManagementServiceComponent {
	

    private ServiceRegistration arduinoServiceRegRef;



    private static final Log log = LogFactory.getLog(ArduinoManagementServiceComponent.class);
    protected void activate(ComponentContext ctx) {
    	if (log.isDebugEnabled()) {
            log.debug("Activating Arduino Device Management Service Component");
        }
        try {
            BundleContext bundleContext = ctx.getBundleContext();


            arduinoServiceRegRef =
                    bundleContext.registerService(DeviceMgtService.class.getName(), new
					ArduinoManager(),
												  null);



            if (log.isDebugEnabled()) {
                log.debug("Arduino Device Management Service Component has been successfully activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while activating Arduino Device Management Service Component", e);
        }
    }

    protected void deactivate(ComponentContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("De-activating Arduino Device Management Service Component");
        }
        try {
            if (arduinoServiceRegRef != null) {
                arduinoServiceRegRef.unregister();
            }

            if (log.isDebugEnabled()) {
                log.debug(
                        "Arduino Device Management Service Component has been successfully de-activated");
            }
        } catch (Throwable e) {
            log.error("Error occurred while de-activating Arduino Device Management bundle", e);
        }
    }

    protected void setDeviceTypeService(DeviceTypeService deviceTypeService) {
		/* This is to avoid this component getting initialized before the
		common registered */
        if (log.isDebugEnabled()) {
            log.debug("Data source service set to mobile service component");
        }
    }

    protected void unsetDeviceTypeService(DeviceTypeService deviceTypeService)  {
        //do nothing
    }


    
}
