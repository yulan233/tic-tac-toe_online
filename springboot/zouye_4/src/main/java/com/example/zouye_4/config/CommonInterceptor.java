package com.example.zouye_4.config;

import com.example.zouye_4.util.TokenManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CommonInterceptor implements HandlerInterceptor {

    @Autowired
    TokenManage tokenManage;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("token");
        int id= Integer.parseInt(request.getHeader("userid"));
        if(tokenManage.verifyToken(id,token)){
            return true;
        }
        handleFalseResponse(response);
        return false;
    }


    private void handleFalseResponse(HttpServletResponse response)
            throws Exception {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"code\":\"tokenError\",\"msg\":\"token失效或错误\"}");
        response.getWriter().flush();
    }

}
