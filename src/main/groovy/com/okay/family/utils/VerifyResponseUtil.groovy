package com.okay.family.utils

import com.alibaba.fastjson.JSONObject
import com.okay.family.common.basedata.OkayConstant
import com.okay.family.common.bean.testcase.CaseVerifyBean
import com.okay.family.fun.frame.SourceCode
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.PatternSyntaxException

class VerifyResponseUtil extends SourceCode {

    private static final Logger logger = LoggerFactory.getLogger(VerifyResponseUtil.class)

/**
 * 校验响应结果
 * @param response
 * @param beans
 * @return
 */
    static boolean verify(JSONObject response, List<CaseVerifyBean> beans) {
        String content = response.getIntValue(RESPONSE_CODE) == TEST_ERROR_CODE ? response.getString(RESPOINSE_CONTENT) : response.toString()
        if (StringUtils.isEmpty(content)) {
            beans.each {it.setResult("响应错误无法验证false")}
            return false
        }
        beans.each {
            def key = it.getText()
            def desc = StringUtils.isBlank(it.getDes()) ? OkayConstant.DEFAULT_VERIFY_DEC : it.getDes()
            if (key.contains("code")) {
                String str = "\"" + key + "\":" + it.getValue()
                it.setResult(desc + OkayConstant.RUN_RESULT_TEXT + content.contains(str))
            } else if (key.contains("regex")) {
                try {
                    it.setResult(desc + OkayConstant.RUN_RESULT_TEXT + (content ==~ it.getValue()))
                } catch (PatternSyntaxException e) {
                    it.setResult(desc + OkayConstant.RUN_RESULT_TEXT + OkayConstant.PATTEN_ERROR_TEXT + false)
                }
            } else {
                it.setResult(desc + OkayConstant.RUN_RESULT_TEXT + false)
            }
        }
        beans.every {
            it.getResult().endsWith("true")
        }
    }

/**
 * 获取code通用方法
 * @param response
 * @return
 */
    static int getCode(JSONObject response) {
        def str = response.toString()
        def matcher = str =~ /code":\d+/
        matcher.find() ? changeStringToInt(matcher[0] - "code\":") : TEST_ERROR_CODE
    }

}
