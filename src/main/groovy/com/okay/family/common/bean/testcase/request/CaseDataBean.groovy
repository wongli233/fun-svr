package com.okay.family.common.bean.testcase.request

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.okay.family.common.bean.testcase.CaseVerifyBean
import com.okay.family.fun.base.bean.AbstractBean
import com.okay.family.fun.config.Constant

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class CaseDataBean extends AbstractBean {

    private static final long serialVersionUID = -629048686822729332L;

    int id

    @Min(value = 1L)
    int uid

    int envId

    @Min(value = 1L)
    int apiId

    @NotNull
    JSONArray header

    @NotNull
    @Pattern(regexp = "GET|POST-FORM|POST-JSON", message = "请求方式传参错误")
    String httpType


    @Min(value = 1L)
    int serviceId

    @NotNull
    List<CaseVerifyBean> testWish

    @NotNull
    JSONArray upData

    /**
     * 接口地址
     */
    @Pattern(regexp = "/.+", message = "接口名称应该以/开头!")
    String url

/**
 * 1:可用,2:不可用
 */
    int available

    JSONObject headers

    JSONObject params


    void init() {
        initHeader()
        initParams()
        available = testWish.size() > 0 ? 1 : 2
    }

    private def initHeader() {
        headers = new JSONObject()
        if (header.size() > 0) {
            header.each {x ->
                JSONObject json = JSON.parseObject(JSONObject.toJSONString(x))
                def key = json.getString("name")
                def value = json.getString("demo")
                headers.put(key, value)
            }
        }
    }

    private def initParams() {
        params = new JSONObject()
        if (upData.size() > 0) {
            upData.each {x ->
                JSONObject json = JSON.parseObject(JSON.toJSONString(x))
                def type = json.getString("dataType")
                if (type ==~ /number|string|integer/) {
                    def key = json.getString("name")
                    def value = json.getString("demo")
                    params.put(key, value)
                } else if (type == "object") {
                    def key = json.getString("name")
                    JSONArray array = json.getJSONArray("children")
                    JSONObject param = new JSONObject()
                    array.each {y ->
                        JSONObject json2 = JSON.parseObject(JSON.toString(y))
                        def key2 = json2.getString("name")
                        def value2 = json2.getString("demo")
                        param.put(key2, value2)
                    }
                    params.put(key, param)
                } else if (type == "array") {
                    /*此处不兼容array<object>*/
                    def key = json.getString("name")
                    def value = json.getString("demo")
                    def list = Arrays.asList(value.split(Constant.COMMA))
                    params.put(key, list)
                }
            }
        }
    }

}
