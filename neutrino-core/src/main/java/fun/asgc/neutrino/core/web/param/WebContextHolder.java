/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.web.param;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Init;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.bean.BeanWrapper;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.util.*;
import fun.asgc.neutrino.core.web.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/26
 */
@NonIntercept
@Component
public class WebContextHolder {
	private static ApplicationConfig.Http http;
	private static volatile String httpContextPath;
	private static volatile byte[] faviconBytes;
	private static volatile Long maxContentLength;
	private static volatile Integer port;
	private static List<BeanWrapper> controllerBeanWrapperList;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private SimpleBeanFactory applicationBeanFactory;

	@Init
	public void init() {
		WebContextHolder.http = applicationConfig.getHttp();
		initHttpContextPath();
		initFavicon();
		initMaxContentLength();
		initControllerBeanWrapperList();
		port = http.getPort();
	}

	public static String getHttpContextPath() {
		return httpContextPath;
	}

	public static byte[] getFaviconBytes() {
		return faviconBytes;
	}

	public static Long getMaxContentLength() {
		return maxContentLength;
	}

	public static Integer getPort() {
		return port;
	}

	private static void initHttpContextPath() {
		httpContextPath = http.getContextPath();
		if (StringUtil.isEmpty(httpContextPath)) {
			httpContextPath = "/";
		}
		if (!httpContextPath.startsWith("/")) {
			httpContextPath = "/" + httpContextPath;
		}
		if (httpContextPath.endsWith("/")) {
			httpContextPath = httpContextPath.substring(0, httpContextPath.length() - 1);
		}
	}

	private static void initFavicon() {
		if (null == http.getStaticResource() || CollectionUtil.isEmpty(http.getStaticResource().getLocations())) {
			return;
		}
		for (String location : http.getStaticResource().getLocations()) {
			faviconBytes = FileUtil.readBytes(location.concat("favicon.ico"));
			if (null != faviconBytes) {
				break;
			}
		}
	}

	private static void initMaxContentLength() {
		maxContentLength = http.getMaxContentLength();
		if (StringUtil.notEmpty(http.getMaxContentLengthDesc()) && null == http.getMaxContentLength()) {
			maxContentLength = NumberUtil.descriptionToSize(http.getMaxContentLengthDesc(), 64 * 1024);
		}
	}

	private void initControllerBeanWrapperList() {
		List<BeanWrapper> beanWrapperList = applicationBeanFactory.beanWrapperList();
		if (CollectionUtil.isEmpty(beanWrapperList)) {
			return;
		}
		controllerBeanWrapperList = beanWrapperList.stream()
			.filter(beanWrapper -> beanWrapper.getType().isAnnotationPresent(RestController.class)).collect(Collectors.toList());
	}

	public static List<BeanWrapper> getControllerBeanWrapperList() {
		return controllerBeanWrapperList;
	}
}
