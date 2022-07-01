package com.nju.HttpServer.Controller.Router;

import com.nju.HttpServer.Common.RequestMethod;
import com.nju.HttpServer.Common.Template;
import com.nju.HttpServer.Services.StaticResourceService;
import com.nju.HttpServer.Http.HttpRequest;
import com.nju.HttpServer.Http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责注册要匹配的方法，调用匹配的方法
 **/
public class Router {

    /**
     * 内部类Action, 保存类及其包含的方法，用于反射执行
     */
    private class Action {
        private Object object;
        private Method method;

        public Action(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        /**
         * 调用注册的方法
         *
         * @param request:传给方法的参数
         */
        public HttpResponse call(HttpRequest request) {
            HttpResponse response = null;
            try {
                //调用方法，传参
                response = (HttpResponse) method.invoke(object, request);
            } catch (IllegalAccessException e) {
                logger.error(e);
            } catch (InvocationTargetException e) {
                logger.error(e);
            }
            return response;
        }
    }

    private static Logger logger = LogManager.getLogger(Router.class);
    private Map<String, Object> controllerBeans = new HashMap<String, Object>();
    private Map<String, HashMap<String, Action>> uri2Action = new HashMap<>(); //uri -> (requestMethod -> Action)

    /**
     * 注册方法
     *
     * @param controllerClass:RequestMapper的引用，以com.开头
     **/
    public void addRouter(String controllerClass) {
        try {
            // 加载class
            Class<?> aClass = this.getClass().getClassLoader().loadClass(controllerClass);

            // 反射class中所有方法
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                // 逐一对方法获取对应注解
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    // 如果注解类型是RouteMapping, 解析其URI
                    if (annotation.annotationType() == RequestMapping.class) {
                        RequestMapping anno = (RequestMapping) annotation;
                        // 提取注解中的路由uri,请求方法
                        String uri = anno.uri();
                        String requestMethod = anno.method();
                        // 保存Bean单例
                        if (!controllerBeans.containsKey(aClass.getName())) {
                            controllerBeans.put(aClass.getName(), aClass.newInstance());
                        }
                        // 保存uri -> (requestMethod -> (obj,method))
                        HashMap<String, Action> method2Action = new HashMap<>();
                        method2Action.put(requestMethod, new Action(controllerBeans.get(aClass.getName()), method));
                        uri2Action.put(uri, method2Action);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 匹配请求uri和请求方法
     **/
    public HttpResponse MapRoute(HttpRequest request) throws Exception {
        HttpResponse response = null;
        String uri = request.getStartLine().getTarget();
        String method = request.getStartLine().getMethod();
//      最先把静态资源请求处理掉！
        if (StaticResourceService.isStaticTarget(uri)) {
            //如果请求的uri对了，但方法错了，响应405
            response = method.equals(RequestMethod.GET) ? new StaticResourceService().handle(request) : Template.generateStatusCode_405();
        } else {
            //非静态资源，使用注解匹配反射，参见RequestMapper
            HashMap<String, Action> method2Action = uri2Action.get(uri);
            if (method2Action != null) {
                Action action = method2Action.get(method);
                //如果请求的uri对了，但方法错了，响应405
                response = action != null ? action.call(request) : Template.generateStatusCode_405();
            } else {
                //没有这样的uri，响应404
                response = Template.generateStatusCode_404();
                logger.error(uri + " is not found, 404");
            }
        }
        return response;
    }
}

