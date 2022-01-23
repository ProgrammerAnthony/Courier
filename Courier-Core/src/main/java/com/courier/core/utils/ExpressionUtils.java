package com.courier.core.utils;

import com.courier.core.service.CourierTaskInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Anthony
 * @create 2022/1/23
 * @desc
 */
@Slf4j
public class ExpressionUtils {

    private static final String START_MARK = "\\$\\{";

    public static final String RESULT_FLAG = "true";

    /**
     * rewrite expression
     *
     * @param alertExpression
     * @return
     */
    public static String rewriteExpr(String alertExpression) {
        String exprExpr = StringUtils.replace(alertExpression, "executeTimes", "#taskInstance.executeTimes");
        StringJoiner exprJoiner = new StringJoiner("", "${", "}");
        exprJoiner.add(exprExpr);
        return exprJoiner.toString();
    }

    public static Map<String, Object> buildDataMap(Object object) {
        Map<String, Object> dataMap = new HashMap<>(1);
        dataMap.put("taskInstance", object);
        return dataMap;
    }



    public static String readExpr(String expr, Map<String, Object> dataMap) {
        try {
            expr = formatExpr(expr);
            // expression context
            EvaluationContext context = new StandardEvaluationContext();
            // put object into context
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                // key -> ref   value -> iterator.next().getValue()
                context.setVariable(entry.getKey(), entry.getValue());
            }
            // comparison with executeTimes
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(expr, new TemplateParserContext());
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            log.error("parse expression error {}", expr, e);
            return "";
        }
    }


    /**
     * public static Map<String, Object> buildDataMap(Object object) {
     * Map<String, Object> dataMap = new HashMap<>(1);
     * dataMap.put("taskInstance", object);
     * return dataMap;
     * }
     * <p>
     * /**
     * expression converter ${xxx.name} -> #{xxx.name}
     *
     * @param expr expression
     */
    private static String formatExpr(String expr) {
        return expr.replaceAll(START_MARK, "#{");
    }

    public static void main(String[] args) {
        CourierTaskInstance instance = CourierTaskInstance.builder()
                .executeTimes(4)
                .build();
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("taskInstance", instance);

        String expr = "executeTimes > 1 && executeTimes < 5";
        String executeTimesExpr = StringUtils.replace(expr, "executeTimes", "#taskInstance.executeTimes");
        System.out.println(executeTimesExpr);
        System.out.println(readExpr("${" + executeTimesExpr + "}", dataMap));

        String expr2 = "executeTimes % 2 == 0";
        String executeTimesExpr2 = StringUtils.replace(expr2, "executeTimes", "#taskInstance.executeTimes");
        System.out.println(executeTimesExpr2);
        System.out.println(readExpr("${" + executeTimesExpr2 + "}", dataMap));

        System.out.println(readExpr(rewriteExpr(expr), dataMap));
    }

}
