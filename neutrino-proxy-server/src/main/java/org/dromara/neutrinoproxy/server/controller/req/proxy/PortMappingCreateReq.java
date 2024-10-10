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
package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

import java.util.List;

/**
 * 端口映射创建请求
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Data
public class PortMappingCreateReq {
	/**
	 * licenseId
	 */
	private Integer licenseId;
	/**
	 * 协议
	 */
	private String protocal;
	/**
	 * 服务端端口
	 */
	private Integer serverPort;
	/**
	 * 客户端ip
	 */
	private String clientIp;
	/**
	 * 客户端端口
	 */
	private Integer clientPort;
    /**
     * 上传限速
     */
    private String upLimitRate;
    /**
     * 下载限速
     */
    private String downLimitRate;
	/**
	 * 代理响应数量（响应数据包数量，如果没有拆包则等于数据条数）
	 */
	private Integer proxyResponses;
	/**
	 * 代理超时时间
	 */
	private Long proxyTimeoutMs;

    /**
     * 安全组Id
     */
    private Integer securityGroupId;

	/**
	 * 描述
	 */
	private String description;

    /**
     * 域名映射列表
     */
    private List<DomainMapping> domainMappings;

    @Data
    public class DomainMapping {
        private Integer domainId;
        private String subdomain;
    }
}
