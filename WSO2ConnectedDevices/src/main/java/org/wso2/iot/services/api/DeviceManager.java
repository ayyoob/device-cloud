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

package org.wso2.iot.services.api;

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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.device.Device;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.enroll.UserManagement;
import org.wso2.iot.user.User;
import org.wso2.iot.utils.IoTConfiguration;

@Path("/DeviceManager")
public class DeviceManager {

	private static Log log = LogFactory.getLog(UserManager.class);

	@Path("/DeviceAnonymousRegister")
	@PUT
	public void Register(@QueryParam("deviceId") String deviceId,
	                     @Context HttpServletResponse response) throws InstantiationException,
	                                                           IllegalAccessException,
	                                                           ConfigurationException {

		UserManagement userManagement = IoTConfiguration.getInstance().getUserManagementImpl();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();

		if (deviceManagement.isExist(deviceId)) {
			response.setStatus(409);
			return;

		}
		Device device = new Device();
		device.setDeviceId(deviceId);

		String token = deviceManagement.generateNewToken();
		device.setToken(token);
		boolean added = false;

		device.setOwner(userManagement.getAnonymousUserName());
		log.info("devic add");;
		added = deviceManagement.addNewDevice(device);

		if (added) {

			response.setStatus(200);
		} else {
			response.setStatus(409);
		}
		log.info("devic add2");;

	}

	@Path("/DeviceRegister")
	@PUT
	public void Register(@QueryParam("deviceId") String deviceId,
	                     @QueryParam("model") String model, @QueryParam("type") String type,
	                     @QueryParam("name") String name,
	                     @QueryParam("description") String description,
	                     @Context HttpServletRequest request, @Context HttpServletResponse response)
	                                                                                                throws InstantiationException,
	                                                                                                IllegalAccessException,
	                                                                                                ConfigurationException {

		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();

		if (deviceManagement.isExist(deviceId)) {
			response.setStatus(409);
			return;

		}
		Device device = new Device();
		device.setDeviceId(deviceId);
		device.setDesciption(description);
		device.setModel(model);
		device.setName(name);
		device.setType(type);

		String token = deviceManagement.generateNewToken();
		device.setToken(token);
		boolean added = false;

		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {

			response.setStatus(403);
			return;
		} else {

			device.setOwner(user.getUsername());
			added = deviceManagement.addNewDevice(device);

		}
		if (added) {

			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/RemoveUser")
	@DELETE
	public void removeDevice(@QueryParam("deviceId") String deviceId,
	                         @Context HttpServletRequest request,
	                         @Context HttpServletResponse response) throws InstantiationException,
	                                                               IllegalAccessException,
	                                                               ConfigurationException {

		UserManager userManager = new UserManager();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = deviceManagement.getDevice(deviceId);
		if (device == null) {

			response.setStatus(409);
			return;
		}
		boolean status = userManager.authorizedCheck(device.getOwner(), request, response);
		if (status) {

			boolean removed = deviceManagement.removeDevice(deviceId);
			if (removed) {

				response.setStatus(200);
			} else {
				response.setStatus(409);

			}
		}

	}

	@Path("/UpdateDevice")
	@POST
	public void updateUser(@QueryParam("deviceId") String deviceId,
	                       @QueryParam("description") String description,
	                       @QueryParam("model") String model, @QueryParam("name") String name,
	                       @QueryParam("type") String type, @QueryParam("owner") String owner,
	                       @Context HttpServletRequest request,
	                       @Context HttpServletResponse response) throws InstantiationException,
	                                                             IllegalAccessException,
	                                                             ConfigurationException {
		UserManager userManager = new UserManager();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = deviceManagement.getDevice(deviceId);
		if (device == null) {

			response.setStatus(409);
			return;
		}
		boolean status = userManager.authorizedCheck(device.getOwner(), request, response);
		if (status) {

			device.setDeviceId(deviceId);
			device.setDesciption(description);
			device.setModel(model);
			device.setName(name);
			device.setType(type);
			device.setOwner(owner);

			boolean updated = deviceManagement.update(device);
			if (updated) {
				response.setStatus(200);
			} else {
				response.setStatus(409);
			}

		}

	}

	@Path("/GetDevice")
	@GET
	@Consumes("application/json")
	public Device getDevice(@QueryParam("deviceId") String deviceId,
	                        @Context HttpServletRequest request,
	                        @Context HttpServletResponse response) throws InstantiationException,
	                                                              IllegalAccessException,
	                                                              ConfigurationException {

		UserManager userManager = new UserManager();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = deviceManagement.getDevice(deviceId);
		if (device == null) {

			response.setStatus(409);
			return null;
		}

		boolean status = userManager.authorizedCheck(device.getOwner(), request, response);
		if (status) {

			response.setStatus(200);
			return device;

		}
		return null;

	}

	@Path("/GetDeviceToken")
	@GET
	@Consumes("application/json")
	public String getDeviceToken(@QueryParam("deviceId") String deviceId,
	                             @Context HttpServletRequest request,
	                             @Context HttpServletResponse response)
	                                                                   throws InstantiationException,
	                                                                   IllegalAccessException,
	                                                                   ConfigurationException {

		UserManager userManager = new UserManager();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = deviceManagement.getDevice(deviceId);
		if (device == null) {

			response.setStatus(409);
			return null;
		}

		boolean status = userManager.authorizedCheck(device.getOwner(), request, response);
		if (status) {

			boolean updated = deviceManagement.update(device);
			if (updated) {
				response.setStatus(200);
			} else {
				response.setStatus(409);
			}

		}
		return null;

	}

	@Path("/UpdateDeviceToken")
	@POST
	public void updateDeviceToken(@QueryParam("deviceId") String deviceId,
	                              @Context HttpServletRequest request,
	                              @Context HttpServletResponse response)
	                                                                    throws InstantiationException,
	                                                                    IllegalAccessException,
	                                                                    ConfigurationException {

		UserManager userManager = new UserManager();
		DeviceManagement deviceManagement =
		                                    IoTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = deviceManagement.getDevice(deviceId);
		if (device == null) {

			response.setStatus(409);
			return;
		}

		boolean status = userManager.authorizedCheck(device.getOwner(), request, response);
		if (status) {
			device.setToken(deviceManagement.generateNewToken());
			deviceManagement.update(device);
			response.setStatus(200);

		}
		return;

	}

}