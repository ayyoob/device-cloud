/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.iot.services;


import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.iot.web.register.IoTDeviceManagementService;


@Path("/DeviceManager")
public class DeviceManager {

	private static Log log = LogFactory.getLog(DeviceManager.class);

	@Path("/DeviceRegister")
	@PUT
	public void register(@QueryParam("deviceId") String deviceId,
	                      @QueryParam("type") String type,
	                     @QueryParam("name") String name,@QueryParam("owner") String owner,
	                     @Context HttpServletResponse response) throws DeviceManagementException
	                                                                                                 {

		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();
		boolean added=iotDeviceService.deviceEnroll(deviceId, type, name, owner);
		if (added) {

			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/RemoveDevice")
	@DELETE
	public void removeDevice(@QueryParam("deviceId") String deviceId,
	                         @QueryParam("deviceType") String type,
	                         @Context HttpServletResponse response) throws DeviceManagementException {
		
		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();
		boolean removed = iotDeviceService.removeDevice(deviceId, type);
		if (removed) {

			response.setStatus(200);
		} else {
			response.setStatus(409);

		}

	}

	@Path("/UpdateDevice")
	@POST
	public void updateDevice(@QueryParam("deviceId") String deviceId, @QueryParam("name") String name,
	                         @QueryParam("type") String type, @QueryParam("owner") String owner,
	                         @Context HttpServletResponse response) throws DeviceManagementException {

		
		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();
		boolean updated = iotDeviceService.updateDevice(deviceId, name, type);
		if (updated) {
			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/GetDevice")
	@GET
	@Consumes("application/json")
	public Device getDevice(@QueryParam("deviceId") String deviceId,
	                        @QueryParam("type") String type,
	                        @Context HttpServletResponse response) throws DeviceManagementException {

		

		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();
		Device device = iotDeviceService.getDevice(deviceId, type);
		if (device == null) {

			response.setStatus(409);
			return null;
		}

		response.setStatus(200);
		return device;

	}

	@Path("/GetDevicesForUser")
	@GET
	@Consumes("application/json")
	public List<Device> getAllDevice(@QueryParam("username") String username,
	                                 @Context HttpServletRequest request,
	                                 @Context HttpServletResponse response) throws DeviceManagementException {

		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();

		List<Device> devices = iotDeviceService.getAllDevice(username);
		if (devices == null) {

			response.setStatus(409);
			return null;
		}

		response.setStatus(200);
		return devices;

	}
	
	@Path("/GetDeviceTypes")
	@GET
	@Consumes("application/json")
	public List<DeviceType> getDeviceTypes(@QueryParam("username") String username,
	                                 @Context HttpServletRequest request,
	                                 @Context HttpServletResponse response) throws DeviceManagementDAOException {

		IoTDeviceManagementService iotDeviceService = new IoTDeviceManagementService();

		List<DeviceType> deviceTypes = iotDeviceService.getDeviceTypes();
		if (deviceTypes == null) {

			response.setStatus(409);
			return null;
		}

		response.setStatus(200);
		return deviceTypes;

	}

}
